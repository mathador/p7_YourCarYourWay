import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, interval } from 'rxjs';
import { switchMap, shareReplay } from 'rxjs/operators';
import { AuthService, User } from './auth.service';

export interface ChatMessage {
    id: string;
    senderId: string;
    senderUsername: string;
    content: string;
    timestamp: number;
}

@Injectable({
    providedIn: 'root'
})
export class ChatService {
    private apiUrl = 'http://localhost:8080';
    private messagesSubject = new BehaviorSubject<ChatMessage[]>([]);
    public messages$ = this.messagesSubject.asObservable();

    private onlineUsersSubject = new BehaviorSubject<User[]>([]);
    public onlineUsers$ = this.onlineUsersSubject.asObservable();

    constructor(
        private http: HttpClient,
        private authService: AuthService
    ) {
        this.startPollingUsers();
    }

    private startPollingUsers(): void {
        interval(2000)
            .pipe(
                switchMap(() => this.http.get<User[]>(`${this.apiUrl}/users`)),
                shareReplay(1)
            )
            .subscribe({
                next: (users) => this.onlineUsersSubject.next(users.filter(u => u.active)),
                error: (err) => console.error('Error fetching online users:', err)
            });
    }

    sendMessage(content: string): void {
        const token = this.authService.getToken();
        if (!token) return;

        const message: ChatMessage = {
            id: Math.random().toString(36).substr(2, 9),
            senderId: 'current-user',
            senderUsername: 'You',
            content: content,
            timestamp: Date.now()
        };

        const messages = this.messagesSubject.value;
        this.messagesSubject.next([...messages, message]);

        // Simuler l'envoi au serveur
        this.http.post(`${this.apiUrl}/chat/messages`, {
            content
        }, {
            headers: { 'X-Auth-Token': token }
        }).subscribe({
            error: (err) => console.error('Error sending message:', err)
        });
    }

    getOnlineUsers(): Observable<User[]> {
        return this.onlineUsers$;
    }

    getMessages(): Observable<ChatMessage[]> {
        return this.messages$;
    }
}
