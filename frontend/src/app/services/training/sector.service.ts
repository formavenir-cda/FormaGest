import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { Observable } from 'rxjs';
import type { Sector } from '../../models/training/sector.model';
import { environment } from '../../../environments/environment.development';

@Service()
export class SectorService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/sectors';

  findAll(): Observable<Sector[]> {
    return this.http.get<Sector[]>(this.apiUrl, {
      withCredentials: true,
    });
  }
}
