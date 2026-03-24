import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { I18nService } from '../services/i18n.service';
import { LanguageSelectorComponent } from './language-selector/language-selector.component';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, FormsModule, LanguageSelectorComponent],
    templateUrl: './login.component.html',
    styleUrl: './login.component.scss'
})
export class LoginComponent {
    username = signal('');
    password = signal('');
    loading = signal(false);
    error = signal('');

    authService = inject(AuthService);
    router = inject(Router);
    i18n = inject(I18nService);

    login(): void {
        if (!this.username() || !this.password()) {
            this.error.set(this.i18n.t('errorRequired'));
            return;
        }

        this.loading.set(true);
        this.error.set('');

        this.authService.login(this.username(), this.password()).subscribe({
            next: () => {
                this.router.navigate(['/chat']);
            },
            error: (err) => {
                this.error.set(this.i18n.t('errorInvalid'));
                this.loading.set(false);
            }
        });
    }
}
