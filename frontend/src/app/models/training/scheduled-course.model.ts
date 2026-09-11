export interface ScheduledCourse {
  id: number;
  cohortId: number;
  courseId: number;
  teacherId: number | null;
  startDate: string;
  endDate: string;
}
