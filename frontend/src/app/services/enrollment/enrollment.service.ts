import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { Observable } from 'rxjs';
import type { CohortEnrollment } from '../../models/enrollment/enrollment.model';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class EnrollmentService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/enrollments';

  enrollmentToCohort(cohortId: number, studentId: number): Observable<CohortEnrollment> {
    return this.http.post<CohortEnrollment>(this.apiUrl, { cohortId, studentId })
  }
}
