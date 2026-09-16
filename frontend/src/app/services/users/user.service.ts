import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { Observable } from 'rxjs';
import type { User, UserPayload } from '../../models/users/user.model';
import type { UserRole } from '../../models/users/user-role';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/users';

  findAll(role?: UserRole): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl, {
      params: role ? { role } : {},
    });
  }

  create(payload: UserPayload): Observable<User> {
    return this.http.post<User>(this.apiUrl, payload);
  }

  update(id: number, payload: UserPayload): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, payload);
  }

  changeRole(id: number, payload: UserPayload): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}/role`, payload);
  }

  toggleActive(id: number): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}/active`, null);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
