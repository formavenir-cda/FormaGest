import {inject, Injectable} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { Observable } from 'rxjs';
import type { Sector } from '../../models/training/sector.model';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class SectorService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/sectors';

  findAll(): Observable<Sector[]> {
    return this.http.get<Sector[]>(this.apiUrl);
  }

  create(name: string): Observable<Sector> {
    return this.http.post<Sector>(this.apiUrl, { name });
  }

  update(id: number, name: string): Observable<Sector> {
    return this.http.put<Sector>(
      `${this.apiUrl}/${id}`,
      { name }
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
