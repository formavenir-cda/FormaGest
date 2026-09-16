export type UserRole =
  | 'STUDENT'
  | 'TEACHER'
  | 'ADMINISTRATIVE_MANAGER'
  | 'ADMINISTRATOR';

export const ALL_USER_ROLES: UserRole[] = [
  'STUDENT',
  'TEACHER',
  'ADMINISTRATIVE_MANAGER',
  'ADMINISTRATOR',
];

export const USER_ROLE_LABELS: Record<UserRole, string> = {
  STUDENT: 'Élève',
  TEACHER: 'Formateur',
  ADMINISTRATIVE_MANAGER: 'Référente administrative',
  ADMINISTRATOR: 'Administrateur',
};
