import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './login.component.html',
    styleUrl: './login.component.scss'
})
export class LoginComponent {
    username = signal('');
    password = signal('');
    loading = signal(false);
    error = signal('');

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    login(): void {
        if (!this.username() || !this.password()) {
            this.error.set('Username and password are required');
            return;
        }

        this.loading.set(true);
        this.error.set('');

        this.authService.login(this.username(), this.password()).subscribe({
            next: () => {
                this.router.navigate(['/chat']);
            },
            error: (err) => {
                this.error.set('Invalid credentials');
                this.loading.set(false);
            }
        });
    }

    demoLogin(username: string): void {
        this.username.set(username);
        this.password.set(username);
        this.login();
    }
}
