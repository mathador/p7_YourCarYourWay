import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface LoginRequest {
    username: string;
    password: string;
}

export interface LoginResponse {
    token: string;
}

export interface User {
    id: string;
    username: string;
    role: string;
    active: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = 'http://localhost:8080/users';
    private platformId = inject(PLATFORM_ID);

    private currentUserSubject = new BehaviorSubject<User | null>(null);
    public currentUser$ = this.currentUserSubject.asObservable();

    private tokenSubject = new BehaviorSubject<string | null>(
        isPlatformBrowser(inject(PLATFORM_ID)) ? (typeof localStorage !== 'undefined' ? localStorage.getItem('auth_token') : null) : null
    );
    public token$ = this.tokenSubject.asObservable();

    private isAuthenticatedSubject = new BehaviorSubject<boolean>(
        isPlatformBrowser(inject(PLATFORM_ID)) ? (typeof localStorage !== 'undefined' ? !!localStorage.getItem('auth_token') : false) : false
    );
    public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

    constructor(private http: HttpClient) {
        if (isPlatformBrowser(this.platformId) && this.getStoredToken()) {
            this.loadCurrentUser();
        }
    }

    login(username: string, password: string): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(`${this.apiUrl}/login`, {
            username,
            password
        }).pipe(
            tap(response => {
                if (isPlatformBrowser(this.platformId)) {
                    localStorage.setItem('auth_token', response.token);
                }
                this.tokenSubject.next(response.token);
                this.isAuthenticatedSubject.next(true);
                this.loadCurrentUser();
            })
        );
    }

    logout(): Observable<any> {
        const token = this.getStoredToken();
        return this.http.post(`${this.apiUrl}/logout`, {}, {
            headers: { 'X-Auth-Token': token || '' }
        }).pipe(
            tap(() => {
                if (isPlatformBrowser(this.platformId)) {
                    localStorage.removeItem('auth_token');
                }
                this.tokenSubject.next(null);
                this.isAuthenticatedSubject.next(false);
                this.currentUserSubject.next(null);
            })
        );
    }

    loadCurrentUser(): void {
        const token = this.getStoredToken();
        if (!token) {
            return;
        }
        this.http.get<User>(`${this.apiUrl}/me`, {
            headers: { 'X-Auth-Token': token }
        }).subscribe({
            next: (user) => this.currentUserSubject.next(user),
            error: () => {
                if (isPlatformBrowser(this.platformId)) {
                    localStorage.removeItem('auth_token');
                }
                this.tokenSubject.next(null);
                this.isAuthenticatedSubject.next(false);
            }
        });
    }

    getStoredToken(): string | null {
        if (!isPlatformBrowser(this.platformId)) {
            return null;
        }
        return localStorage.getItem('auth_token');
    }

    getToken(): string | null {
        return this.tokenSubject.value;
    }
}
