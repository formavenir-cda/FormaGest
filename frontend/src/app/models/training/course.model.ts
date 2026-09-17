export interface CourseAssociation {
  sectorId: number;
  sectorName: string;
  trackId: number;
  trackName: string;
  position: number;
}

export interface Course {
  id: number;
  name: string;
  durationInDays: number;
  associations: CourseAssociation[];
}
