import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import type { Observable } from 'rxjs';
import type { CohortEnrollment, ScheduledCourseEnrollment } from '../../models/enrollment/enrollment.model';
import { environment } from '../../../environments/environment.development';

@Service()
export class EnrollmentService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/enrollments';

  enrollmentToCohort(cohortId: number, studentId: number): Observable<CohortEnrollment> {
    return this.http.post<CohortEnrollment>(`${this.apiUrl}/cohorts`, { cohortId, studentId })
  }

  enrollmentToScheduledCourse(
    studentId: number,
    scheduledCourseId: number,
    forced = false,
    justificationForced?: string
  ): Observable<ScheduledCourseEnrollment> {
    return this.http.post<ScheduledCourseEnrollment>(`${this.apiUrl}/scheduled-courses`, {
      studentId,
      scheduledCourseId,
      forced,
      justificationForced,
    })
  }

  findCohortEnrollmentByStudent(studentId: number): Observable<CohortEnrollment | null> {
    return this.http.get<CohortEnrollment | null>(`${this.apiUrl}/students/${studentId}/cohort`);
  }

  findScheduledCourseEnrollmentsByStudent(studentId: number): Observable<ScheduledCourseEnrollment[]> {
    return this.http.get<ScheduledCourseEnrollment[]>(`${this.apiUrl}/students/${studentId}/scheduled-courses`);
  }

  findActiveCohortEnrollments(): Observable<CohortEnrollment[]> {
    return this.http.get<CohortEnrollment[]>(`${this.apiUrl}/students/active-cohort`);
  }

  findCohortEnrollmentsByCohort(cohortId: number): Observable<CohortEnrollment[]> {
    return this.http.get<CohortEnrollment[]>(`${this.apiUrl}/cohorts/${cohortId}/students`);
  }
}
