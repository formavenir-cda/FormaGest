import type { CohortStatus } from './cohort-status';
import type { ScheduledCourse } from './scheduled-course.model';

export interface Cohort {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  status: CohortStatus;
  trackId: number;
  scheduledCourses: ScheduledCourse[];
}
