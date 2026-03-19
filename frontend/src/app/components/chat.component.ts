import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, User } from '../services/auth.service';
import { ChatService, ChatMessage } from '../services/chat.service';

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

    constructor(
        private authService: AuthService,
        private chatService: ChatService,
        private router: Router
    ) { }

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

    formatTime(timestamp: number): string {
        const date = new Date(timestamp);
        return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
    }
}
