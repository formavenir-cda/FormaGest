import {inject, Service} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';

@Service()
export class EnrollmentService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/enrollments';

  enrollmentToCohort(cohortId: number, studentId: number): Observable<CohortEnrollment> {
    return this.http.post<CohortEnrollment>(this.apiUrl, { cohortId, studentId })
  }
}
