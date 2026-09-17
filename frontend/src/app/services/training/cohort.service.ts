import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { Observable } from 'rxjs';
import type { Cohort } from '../../models/training/cohort.model';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class CohortService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/cohorts';

  findAll(): Observable<Cohort[]> {
    return this.http.get<Cohort[]>(this.apiUrl);
  }

  create(
    name: string,
    trackId: number,
    startDate: string
  ): Observable<Cohort> {
    return this.http.post<Cohort>(this.apiUrl, {
      name,
      trackId,
      startDate,
    });
  }

}
