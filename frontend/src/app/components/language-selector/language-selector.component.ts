import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { I18nService } from '../../services/i18n.service';

@Component({
    selector: 'app-language-selector',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './language-selector.component.html',
    styleUrl: './language-selector.component.scss'
})
export class LanguageSelectorComponent {
    i18n = inject(I18nService);

    locales = [
        { id: 'en-US', flag: '🇺🇸', name: 'English (US)' },
        { id: 'en-GB', flag: '🇬🇧', name: 'English (UK)' },
        { id: 'en-CA', flag: '🇨🇦', name: 'Canadian (EN)' },
        { id: 'fr-CA', flag: '🇨🇦', name: 'Canadien (FR)' },
        { id: 'fr-FR', flag: '🇫🇷', name: 'Français' },
        { id: 'it-IT', flag: '🇮🇹', name: 'Italiano' },
        { id: 'de-DE', flag: '🇩🇪', name: 'Deutsch' },
        { id: 'es-ES', flag: '🇪🇸', name: 'Español' }
    ];

    select(locale: string) {
        this.i18n.setLocale(locale);
    }
}
