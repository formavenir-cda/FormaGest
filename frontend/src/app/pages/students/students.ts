import { Component, computed, inject, OnInit, signal, TemplateRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';

import { UserService } from '../../services/users/user.service';
import { CohortService } from '../../services/training/cohort.service';
import { CourseService } from '../../services/training/course.service';
import { EnrollmentService } from '../../services/enrollment/enrollment.service';
import type { Student } from '../../models/users/user.model';
import type { Cohort } from '../../models/training/cohort.model';
import type { Course } from '../../models/training/course.model';
import type { CohortEnrollment, ScheduledCourseEnrollment } from '../../models/enrollment/enrollment.model';

@Component({
  selector: 'app-students',
  imports: [
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    DatePipe
  ],
  styleUrl: './students.scss',
  templateUrl: './students.html',
})
export class Students implements OnInit {
  private readonly userService = inject(UserService);
  private readonly cohortService = inject(CohortService);
  private readonly courseService = inject(CourseService);
  private readonly enrollmentService = inject(EnrollmentService);
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);

  private detailsDialogRef?: MatDialogRef<unknown>;

  readonly students = signal<Student[]>([]);
  readonly cohorts = signal<Cohort[]>([]);
  readonly courses = signal<Course[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly search = signal('');

  readonly viewingStudent = signal<Student | null>(null);
  readonly viewingCohortEnrollment = signal<CohortEnrollment | null>(null);
  readonly viewingScheduledCourseEnrollments = signal<ScheduledCourseEnrollment[]>([]);
  readonly activeCohortEnrollments = signal<CohortEnrollment[]>([]);

  readonly filteredStudents = computed(() => {
    const search = this.normalizeSearch(this.search());

    return this.students().filter((student) =>
      search
        ? this.normalizeSearch(
          `${student.firstName} ${student.lastName} ${student.email}`
        ).includes(search)
        : true
    );
  });

  readonly scheduledCourseLabels = computed(() => {
    const courses = this.courses();
    const labels = new Map<number, string>();

    for (const cohort of this.cohorts()) {
      for (const scheduledCourse of cohort.scheduledCourses) {
        const courseName = courses.find((course) => course.id === scheduledCourse.courseId)?.name ?? '—';
        labels.set(scheduledCourse.id, `${courseName} (${cohort.name})`);
      }
    }

    return labels;
  });

  // Élève -> son inscription à une promotion active (à venir ou en cours).
  readonly activeCohortByStudentId = computed(
    () => new Map(this.activeCohortEnrollments().map((enrollment) => [enrollment.studentId, enrollment]))
  );

  // Élève -> nom de sa promotion, uniquement quand elle est en cours (pas à venir),
  // pour la colonne dédiée du tableau.
  readonly inProgressCohortNameByStudentId = computed(() => {
    const cohorts = this.cohorts();
    const names = new Map<number, string>();

    for (const enrollment of this.activeCohortEnrollments()) {
      const cohort = cohorts.find((item) => item.id === enrollment.cohortId);

      if (cohort?.status === 'IN_PROGRESS') {
        names.set(enrollment.studentId, cohort.name);
      }
    }

    return names;
  });

  ngOnInit(): void {
    this.userService.findAll('STUDENT').subscribe({
      next: (students) => {
        this.students.set(students as Student[]);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les élèves.');
        this.loading.set(false);
      },
    });

    this.cohortService.findAll().subscribe({
      next: (cohorts) => this.cohorts.set(cohorts),
    });

    this.courseService.findAll().subscribe({
      next: (courses) => this.courses.set(courses),
    });

    this.enrollmentService.findActiveCohortEnrollments().subscribe({
      next: (enrollments) => this.activeCohortEnrollments.set(enrollments),
    });
  }

  openDetails(student: Student, template: TemplateRef<unknown>): void {
    this.viewingStudent.set(student);
    this.viewingCohortEnrollment.set(null);
    this.viewingScheduledCourseEnrollments.set([]);

    this.enrollmentService.findCohortEnrollmentByStudent(student.id).subscribe({
      next: (enrollment) => this.viewingCohortEnrollment.set(enrollment),
    });

    this.enrollmentService.findScheduledCourseEnrollmentsByStudent(student.id).subscribe({
      next: (enrollments) => this.viewingScheduledCourseEnrollments.set(enrollments),
    });

    this.detailsDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  cohortName(cohortId: number): string {
    return this.cohorts().find((cohort) => cohort.id === cohortId)?.name ?? '—';
  }

  closeDetails(): void {
    this.detailsDialogRef?.close();
  }

  openEnroll(student: Student): void {
    // Un élève déjà inscrit à une promotion (à venir ou en cours) ne peut pas en
    // rejoindre une autre : on lui présente directement l'inscription à l'unité.
    const mode = this.activeCohortByStudentId().has(student.id) ? 'scheduledCourse' : 'cohort';

    this.router.navigate(['/enrollments'], { queryParams: { studentId: student.id, mode } });
  }

  private normalizeSearch(value: string): string {
    return value
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[̀-ͯ]/g, '');
  }
}
