import type { UserRole } from './user-role';

export interface BaseUser {
  id: number;
  email: string;
  lastName: string;
  firstName: string;
  active: boolean;
  role: UserRole;
}

export interface Student extends BaseUser {
  role: 'STUDENT';
  birthDate: string;
}

export interface Teacher extends BaseUser {
  role: 'TEACHER';
}

export interface AdministrativeManager extends BaseUser {
  role: 'ADMINISTRATIVE_MANAGER';
}

export interface Administrator extends BaseUser {
  role: 'ADMINISTRATOR';
}

export type User =
  | Student
  | Teacher
  | AdministrativeManager
  | Administrator;
