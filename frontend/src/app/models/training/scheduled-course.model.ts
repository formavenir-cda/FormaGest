export interface ScheduledCourse {
  id: number;
  cohortId: number;
  courseId: number;
  courseName: string;
  teacherId: number | null;
  startDate: string | null;
  endDate: string | null;
}
