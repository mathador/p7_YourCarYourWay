import { Injectable, signal, inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { catchError, of, tap } from 'rxjs';

export interface LocaleInfo {
    locale: string;
    language: string;
    country: string;
    dateFormat: string;
    timeFormat: string;
    translations: { [key: string]: string };
}

@Injectable({
    providedIn: 'root'
})
export class I18nService {
    private http = inject(HttpClient);
    private platformId = inject(PLATFORM_ID);
    private apiUrl = 'http://localhost:8080/i18n';

    currentLocale = signal<string>(this.getInitialLocale());
    translations = signal<{ [key: string]: string }>({});
    dateFormat = signal<string>('MM/dd/yyyy');
    timeFormat = signal<string>('hh:mm a');
    loading = signal<boolean>(false);

    constructor() {
        this.loadTranslations(this.currentLocale());
    }

    private getInitialLocale(): string {
        if (isPlatformBrowser(this.platformId)) {
            return localStorage.getItem('selected_locale') || 'en-US';
        }
        return 'en-US';
    }

    setLocale(locale: string) {
        this.currentLocale.set(locale);
        if (isPlatformBrowser(this.platformId)) {
            localStorage.setItem('selected_locale', locale);
        }
        this.loadTranslations(locale);
    }

    private loadTranslations(locale: string) {
        if (isPlatformBrowser(this.platformId)) {
            const cached = localStorage.getItem(`i18n_cache_${locale}`);
            if (cached) {
                const data = JSON.parse(cached);
                this.translations.set(data.translations);
                this.dateFormat.set(data.dateFormat);
                this.timeFormat.set(data.timeFormat);
                return;
            }
        }

        this.loading.set(true);
        this.http.get<LocaleInfo>(`${this.apiUrl}/locale?locale=${locale}`).pipe(
            tap(info => {
                this.translations.set(info.translations);
                this.dateFormat.set(info.dateFormat);
                this.timeFormat.set(info.timeFormat);
                if (isPlatformBrowser(this.platformId)) {
                    localStorage.setItem(`i18n_cache_${locale}`, JSON.stringify({
                        translations: info.translations,
                        dateFormat: info.dateFormat,
                        timeFormat: info.timeFormat
                    }));
                }
                this.loading.set(false);
            }),
            catchError(err => {
                console.error(`Failed to load translations for ${locale}`, err);
                this.loading.set(false);
                return of(null);
            })
        ).subscribe();
    }

    t(key: string): string {
        return this.translations()[key] || key;
    }

    clearCache() {
        if (isPlatformBrowser(this.platformId)) {
            const keys = [];
            for (let i = 0; i < localStorage.length; i++) {
                const key = localStorage.key(i);
                if (key?.startsWith('i18n_cache_')) {
                    keys.push(key);
                }
            }
            keys.forEach(k => localStorage.removeItem(k));
        }
    }
}
