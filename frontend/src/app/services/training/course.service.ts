import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { Observable } from 'rxjs';
import type { Course } from '../../models/training/course.model';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class CourseService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/courses';

  findAll(): Observable<Course[]> {
    return this.http.get<Course[]>(this.apiUrl);
  }

  create(name: string, durationInDays: number): Observable<Course> {
    return this.http.post<Course>(this.apiUrl, { name, durationInDays });
  }

  update(id: number, name: string, durationInDays: number): Observable<Course> {
    return this.http.put<Course>(`${this.apiUrl}/${id}`, { name, durationInDays });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Remplace l’ensemble des cursus associés à un cours.
   * N’est jamais appelé depuis la popup de modification d’un cours :
   * l’association d’un cours à un cursus se gère depuis la page du cursus.
   */
  updateTracks(id: number, trackIds: number[]): Observable<Course> {
    return this.http.put<Course>(`${this.apiUrl}/${id}/tracks`, trackIds);
  }
}
