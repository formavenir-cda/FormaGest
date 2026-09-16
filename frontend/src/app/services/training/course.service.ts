import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { Observable } from 'rxjs';
import { of, switchMap } from 'rxjs';
import type { Course, CourseAssociation } from '../../models/training/course.model';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class CourseService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/courses';

  findAll(): Observable<Course[]> {
    return this.http.get<Course[]>(this.apiUrl);
  }

  create(name: string, associations: CourseAssociation[] = []): Observable<Course> {
    return this.http.post<Course>(this.apiUrl, { name }).pipe(
      switchMap((course) =>
        associations.length
          ? this.updateTracks(course.id, this.trackIds(associations))
          : of(course)
      )
    );
  }

  update(
    id: number,
    name: string,
    associations: CourseAssociation[] = []
  ): Observable<Course> {
    return this.http.put<Course>(
      `${this.apiUrl}/${id}`,
      { name }
    ).pipe(
      switchMap(() =>
        this.updateTracks(id, this.trackIds(associations))
      )
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  private updateTracks(id: number, trackIds: number[]): Observable<Course> {
    return this.http.put<Course>(`${this.apiUrl}/${id}/tracks`, trackIds);
  }

  private trackIds(associations: CourseAssociation[]): number[] {
    return associations.map((association) => association.trackId);
  }
}
