import {inject, Service} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import {environment} from '../../../environments/environment.development';
import type {User} from '../../models/users/user.model';


interface LoginResponse {
  token: string;
  user: User;
}

const STORAGE_KEY = 'currentUser';
const TOKEN_KEY = 'authToken';

@Service()
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/auth';

  private readonly userSubject = new BehaviorSubject<User | null>(this.readStoredUser());
  readonly user$ = this.userSubject.asObservable();

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { email, password }).pipe(
      tap(({ token, user }) => {
        sessionStorage.setItem(TOKEN_KEY, token);
        sessionStorage.setItem(STORAGE_KEY, JSON.stringify(user));
        this.userSubject.next(user);
      }),
    );
  }

  logout(): void {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(STORAGE_KEY);
    this.userSubject.next(null);
  }

  getToken(): string | null {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  private readStoredUser(): User | null {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  }
}
