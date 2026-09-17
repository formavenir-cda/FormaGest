export interface BaseEnrollment {
  id: number;
  enrollmentDate: string;
  studentId: number;
}

export interface CohortEnrollment extends BaseEnrollment {
  cohortId: number;
}

export interface ScheduledCourseEnrollment extends BaseEnrollment {
  scheduledCourseId: number;
  forced: boolean;
  justificationForced?: string;
}

export type Enrollment =
  | CohortEnrollment
  | ScheduledCourseEnrollment;
