import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, User } from '../services/auth.service';
import { ChatService, ChatMessage } from '../services/chat.service';
import { I18nService } from '../services/i18n.service';

@Component({
    selector: 'app-chat',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './chat.component.html',
    styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {
    currentUser = signal<User | null>(null);
    onlineUsers = signal<User[]>([]);
    messages = signal<ChatMessage[]>([]);
    newMessage = signal('');
    loadingLogout = signal(false);

    authService = inject(AuthService);
    chatService = inject(ChatService);
    router = inject(Router);
    i18n = inject(I18nService);

    ngOnInit(): void {
        this.authService.currentUser$.subscribe(user => {
            this.currentUser.set(user);
        });

        this.chatService.getOnlineUsers().subscribe(users => {
            this.onlineUsers.set(users);
        });

        this.chatService.getMessages().subscribe(msgs => {
            this.messages.set(msgs);
        });
    }

    sendMessage(): void {
        if (!this.newMessage().trim()) return;

        this.chatService.sendMessage(this.newMessage());
        this.newMessage.set('');
    }

    logout(): void {
        this.loadingLogout.set(true);
        this.authService.logout().subscribe({
            next: () => {
                this.router.navigate(['/login']);
            },
            error: () => {
                // Navigate anyway as AuthService already cleared local state
                this.router.navigate(['/login']);
            }
        });
    }

    getRoleClass(role: string): string {
        return role === 'AGENT' ? 'agent' : 'client';
    }

    getRoleLabel(role: string): string {
        return role === 'AGENT' ? this.i18n.t('roleAdmin') : this.i18n.t('roleUser');
    }

    formatTime(timestamp: number): string {
        const date = new Date(timestamp);
        return date.toLocaleTimeString(this.i18n.currentLocale(), { hour: '2-digit', minute: '2-digit' });
    }
}
