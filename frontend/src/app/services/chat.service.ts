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

    private defaultChannelId = 'general';
    private pollingUsersSubscription: any;
    private pollingMessagesSubscription: any;

    constructor(
        private http: HttpClient,
        private authService: AuthService
    ) {
        this.authService.isAuthenticated$.subscribe(isAuthenticated => {
            if (isAuthenticated) {
                this.startPolling();
            } else {
                this.stopPolling();
                this.onlineUsersSubject.next([]);
                this.messagesSubject.next([]);
            }
        });
    }

    private startPolling(): void {
        this.startPollingUsers();
        this.startPollingMessages();
    }

    private stopPolling(): void {
        if (this.pollingUsersSubscription) {
            this.pollingUsersSubscription.unsubscribe();
        }
        if (this.pollingMessagesSubscription) {
            this.pollingMessagesSubscription.unsubscribe();
        }
    }

    private startPollingUsers(): void {
        this.pollingUsersSubscription = interval(2000)
            .pipe(
                switchMap(() => {
                    const token = this.authService.getToken();
                    if (!token) return [];
                    return this.http.get<User[]>(`${this.apiUrl}/users`, {
                        headers: { 'X-Auth-Token': token }
                    });
                }),
                shareReplay(1)
            )
            .subscribe({
                next: (users) => {
                    if (Array.isArray(users)) {
                        this.onlineUsersSubject.next(users.filter(u => u.active));
                    }
                },
                error: (err) => console.error('Error fetching online users:', err)
            });
    }

    private startPollingMessages(): void {
        this.pollingMessagesSubscription = interval(1000)
            .pipe(
                switchMap(() => {
                    const token = this.authService.getToken();
                    if (!token) return [];
                    return this.http.get<ChatMessage[]>(`${this.apiUrl}/chat/channels/${this.defaultChannelId}/messages`, {
                        headers: { 'X-Auth-Token': token }
                    });
                }),
                shareReplay(1)
            )
            .subscribe({
                next: (messages) => {
                    if (Array.isArray(messages)) {
                        // Transformer les messages pour l'affichage si nécessaire
                        const formattedMessages = messages.map(m => ({
                            ...m,
                            senderUsername: m.senderUsername || (m as any).from // Compatibilité avec MessageDto.from du backend
                        }));
                        this.messagesSubject.next(formattedMessages);
                    }
                },
                error: (err) => console.error('Error fetching messages:', err)
            });
    }

    sendMessage(content: string): void {
        const token = this.authService.getToken();
        let currentUser: User | null = null;
        this.authService.currentUser$.subscribe(u => currentUser = u);

        if (!token || !currentUser) return;

        // On envoie le message au serveur (le polling s'occupera de l'afficher)
        this.http.post(`${this.apiUrl}/chat/channels/${this.defaultChannelId}/messages`, {
            from: (currentUser as User).username,
            content: content
        }, {
            headers: { 'X-Auth-Token': token }
        }).subscribe({
            next: () => {
                // Optionnel: on pourrait forcer un rafraîchissement immédiat ici
            },
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
