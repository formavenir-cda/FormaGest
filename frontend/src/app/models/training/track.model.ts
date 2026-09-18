import type { TrackCourse } from './track-course.model';

export interface Track {
  id: number;
  name: string;
  sectorId: number;
  courses: TrackCourse[];
  hasInProgressCohort: boolean;
}
