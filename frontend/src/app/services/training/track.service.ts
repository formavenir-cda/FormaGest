import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { Observable } from 'rxjs';
import type { Track } from '../../models/training/track.model';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class TrackService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/tracks';

  findAll(): Observable<Track[]> {
    return this.http.get<Track[]>(this.apiUrl);
  }

  findBySector(sectorId: number): Observable<Track[]> {
    return this.http.get<Track[]>(this.apiUrl, {
      params: { sectorId },
    });
  }

  create(name: string, sectorId: number): Observable<Track> {
    return this.http.post<Track>(this.apiUrl, { name, sectorId });
  }

  update(id: number, name: string, sectorId: number): Observable<Track> {
    return this.http.put<Track>(
      `${this.apiUrl}/${id}`,
      { name, sectorId }
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
