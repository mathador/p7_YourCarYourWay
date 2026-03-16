import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, interval, of } from 'rxjs';
import { switchMap, shareReplay, catchError } from 'rxjs/operators';
import { AuthService, User } from './auth.service';

export interface ChatMessage {
    id: number;
    sessionId: string;
    senderId: number;
    senderUsername: string;
    content: string;
    timestamp: any;
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

    private defaultChannelId = '00000000-0000-0000-0000-000000000000';
    private pollingUsersSubscription: any;
    private pollingMessagesSubscription: any;

    constructor(
        private http: HttpClient,
        private authService: AuthService
    ) {
        this.authService.isAuthenticated$.subscribe(isAuthenticated => {
            if (isAuthenticated) {
                this.ensureDefaultSession();
                this.startPolling();
            } else {
                this.stopPolling();
                this.onlineUsersSubject.next([]);
                this.messagesSubject.next([]);
            }
        });
    }

    private ensureDefaultSession(): void {
        const token = this.authService.getToken();
        if (!token) return;
        
        this.http.post(`${this.apiUrl}/chat/sessions`, {
            userId: 1,
            guestName: 'System',
            countryCode: 'FR'
        }, {
            headers: { 'X-Auth-Token': token }
        }).subscribe({
            next: () => this.loadSessions(token),
            error: () => this.loadSessions(token)
        });
    }

    private loadSessions(token: string): void {
        this.http.get<any[]>(`${this.apiUrl}/chat/sessions`, {
            headers: { 'X-Auth-Token': token }
        }).subscribe(sessions => {
            if (sessions && sessions.length > 0) {
                this.defaultChannelId = sessions[0].sessionId;
            }
        });
    }

    private startPolling(): void {
        this.startPollingUsers();
        this.startPollingMessages();
    }

    private stopPolling(): void {
        if (this.pollingUsersSubscription) this.pollingUsersSubscription.unsubscribe();
        if (this.pollingMessagesSubscription) this.pollingMessagesSubscription.unsubscribe();
    }

    private startPollingUsers(): void {
        this.pollingUsersSubscription = interval(2000)
            .pipe(
                switchMap(() => {
                    const token = this.authService.getToken();
                    if (!token) return of([]);
                    return this.http.get<User[]>(`${this.apiUrl}/users/online`, {
                        headers: { 'X-Auth-Token': token }
                    }).pipe(catchError(() => of([])));
                }),
                shareReplay(1)
            )
            .subscribe({
                next: (users) => {
                    if (Array.isArray(users)) {
                        this.onlineUsersSubject.next(users);
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
                    if (!token || !this.defaultChannelId || this.defaultChannelId === '00000000-0000-0000-0000-000000000000') return of([]);
                    return this.http.get<any[]>(`${this.apiUrl}/chat/sessions/${this.defaultChannelId}/messages`, {
                        headers: { 'X-Auth-Token': token }
                    }).pipe(catchError(() => of([])));
                }),
                shareReplay(1)
            )
            .subscribe({
                next: (messages) => {
                    if (Array.isArray(messages)) {
                        const formattedMessages = messages.map(m => ({
                            id: m.id,
                            sessionId: m.sessionId,
                            senderId: m.senderId,
                            senderUsername: m.senderUsername || (m.senderId ? m.senderId.toString() : 'Guest'),
                            content: m.content,
                            timestamp: m.timestampUtc
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

        if (!token || !currentUser || !this.defaultChannelId) return;

        this.http.post(`${this.apiUrl}/chat/sessions/${this.defaultChannelId}/messages`, {
            senderId: (currentUser as User).id,
            senderUsername: (currentUser as User).username,
            content: content
        }, {
            headers: { 'X-Auth-Token': token }
        }).subscribe({
            next: () => {},
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
