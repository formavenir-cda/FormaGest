import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import type { Observable } from 'rxjs';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { UserService } from '../../services/users/user.service';
import { CohortService } from '../../services/training/cohort.service';
import { EnrollmentService } from '../../services/enrollment/enrollment.service';
import type { Student } from '../../models/users/user.model';
import type { Cohort } from '../../models/training/cohort.model';
import type { CohortStatus } from '../../models/training/cohort-status';
import type { ScheduledCourse } from '../../models/training/scheduled-course.model';
import type { CohortEnrollment, ScheduledCourseEnrollment } from '../../models/enrollment/enrollment.model';

type EnrollMode = 'cohort' | 'scheduledCourse';

interface ScheduledCourseOption extends ScheduledCourse {
  cohortName: string;
}

const STATUS_LABELS: Record<CohortStatus, string> = {
  UPCOMING: 'À venir',
  IN_PROGRESS: 'En cours',
  COMPLETED: 'Terminée',
};

interface EnrollResult {
  student: Student;
  success: boolean;
  message: string;
  canForce: boolean;
}

// Fragment présent uniquement dans le message RG09 (ordre pédagogique, EnrollmentController),
// pour le distinguer du refus RG08 (doublon, jamais forçable) qui renvoie aussi un 409.
const FORCE_HINT = 'cours précédents';

@Component({
  selector: 'app-enrollments',
  imports: [
    DatePipe,
    FormsModule,
    MatButtonModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatInputModule,
    MatRadioModule,
  ],
  styleUrl: './enrollments.scss',
  templateUrl: './enrollments.html',
})
export class Enrollments implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly userService = inject(UserService);
  private readonly cohortService = inject(CohortService);
  private readonly enrollmentService = inject(EnrollmentService);

  readonly students = signal<Student[]>([]);
  readonly cohorts = signal<Cohort[]>([]);
  readonly activeCohortEnrollments = signal<CohortEnrollment[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly search = signal('');

  readonly selectedStudentIds = signal<Set<number>>(new Set());
  readonly mode = signal<EnrollMode>('cohort');
  readonly selectedCohortId = signal<number | null>(null);
  readonly selectedScheduledCourseId = signal<number | null>(null);
  readonly targetSearch = signal('');

  readonly submitting = signal(false);
  readonly results = signal<EnrollResult[]>([]);

  private readonly activeMode = signal<EnrollMode | null>(null);
  private readonly activeTargetId = signal<number | null>(null);
  readonly preselectedStudentId = signal<number | null>(null);

  // Élève -> son inscription à une promotion active, pour l'affichage et pour RG (un élève
  // déjà inscrit à une promotion active ne peut pas en rejoindre une autre).
  readonly activeCohortByStudentId = computed(
    () => new Map(this.activeCohortEnrollments().map((enrollment) => [enrollment.studentId, enrollment]))
  );

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

  // Élève arrivé présélectionné depuis la fiche élève (bouton Inscrire) : toujours
  // en tête de liste, pour rester visible sans avoir à chercher/faire défiler.
  // Puis élèves sans promotion d'abord, pour repérer en un coup d'œil qui reste à
  // inscrire ; ordre alphabétique en dernier critère au sein de chaque groupe.
  readonly sortedStudents = computed(() => {
    const activeByStudentId = this.activeCohortByStudentId();
    const preselectedId = this.preselectedStudentId();

    return [...this.filteredStudents()].sort((a, b) => {
      const aPreselected = a.id === preselectedId ? 0 : 1;
      const bPreselected = b.id === preselectedId ? 0 : 1;

      if (aPreselected !== bPreselected) {
        return aPreselected - bPreselected;
      }

      const aHasCohort = activeByStudentId.has(a.id) ? 1 : 0;
      const bHasCohort = activeByStudentId.has(b.id) ? 1 : 0;

      return aHasCohort - bHasCohort || a.lastName.localeCompare(b.lastName);
    });
  });

  // Une promotion terminée n'a plus vocation à recevoir de nouvelles inscriptions,
  // ni à l'unité (ses cours planifiés appartiennent au passé).
  readonly activeCohorts = computed(() =>
    this.cohorts().filter((cohort) => cohort.status !== 'COMPLETED')
  );

  readonly scheduledCourseOptions = computed<ScheduledCourseOption[]>(() =>
    this.activeCohorts().flatMap((cohort) =>
      cohort.scheduledCourses.map((scheduledCourse) => ({
        ...scheduledCourse,
        cohortName: cohort.name,
      }))
    )
  );

  readonly filteredCohorts = computed(() => {
    const search = this.normalizeSearch(this.targetSearch());

    return this.activeCohorts().filter((cohort) =>
      search ? this.normalizeSearch(cohort.name).includes(search) : true
    );
  });

  readonly filteredScheduledCourseOptions = computed(() => {
    const search = this.normalizeSearch(this.targetSearch());

    return this.scheduledCourseOptions().filter((option) =>
      search
        ? this.normalizeSearch(`${option.courseName} ${option.cohortName}`).includes(search)
        : true
    );
  });

  // Cette restriction ne s'applique pas à l'inscription à l'unité (RG08/RG09 suffisent).
  readonly disabledStudentIds = computed(() =>
    this.mode() === 'cohort' ? new Set(this.activeCohortByStudentId().keys()) : new Set<number>()
  );

  readonly canSubmit = computed(() => {
    const selectable = [...this.selectedStudentIds()].filter(
      (id) => !this.disabledStudentIds().has(id)
    );

    if (selectable.length === 0 || this.submitting()) {
      return false;
    }

    return this.mode() === 'cohort'
      ? this.selectedCohortId() !== null
      : this.selectedScheduledCourseId() !== null;
  });

  ngOnInit(): void {
    const studentId = Number(this.route.snapshot.queryParamMap.get('studentId'));

    if (studentId) {
      this.selectedStudentIds.set(new Set([studentId]));
      this.preselectedStudentId.set(studentId);
    }

    if (this.route.snapshot.queryParamMap.get('mode') === 'scheduledCourse') {
      this.mode.set('scheduledCourse');
    }

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

    this.enrollmentService.findActiveCohortEnrollments().subscribe({
      next: (enrollments) => this.activeCohortEnrollments.set(enrollments),
    });
  }

  cohortName(cohortId: number): string {
    return this.cohorts().find((cohort) => cohort.id === cohortId)?.name ?? '—';
  }

  statusLabel(status: CohortStatus): string {
    return STATUS_LABELS[status];
  }

  toggleStudent(studentId: number): void {
    if (this.disabledStudentIds().has(studentId)) {
      return;
    }

    this.selectedStudentIds.update((ids) => {
      const next = new Set(ids);

      if (next.has(studentId)) {
        next.delete(studentId);
      } else {
        next.add(studentId);
      }

      return next;
    });
  }

  setMode(mode: EnrollMode): void {
    this.mode.set(mode);
    this.selectedCohortId.set(null);
    this.selectedScheduledCourseId.set(null);
    this.targetSearch.set('');
  }

  submit(): void {
    if (!this.canSubmit()) {
      return;
    }

    const mode = this.mode();
    const targetId = mode === 'cohort' ? this.selectedCohortId() : this.selectedScheduledCourseId();

    this.activeMode.set(mode);
    this.activeTargetId.set(targetId);

    this.submitting.set(true);
    this.results.set([]);

    const students = this.students().filter(
      (student) => this.selectedStudentIds().has(student.id) && !this.disabledStudentIds().has(student.id)
    );
    const calls = students.map((student) => this.enrollOne(student, false));

    forkJoin(calls).subscribe((results) => {
      this.results.set(results);
      this.submitting.set(false);
    });
  }

  retryForced(studentId: number): void {
    const student = this.students().find((item) => item.id === studentId);

    if (!student) {
      return;
    }

    this.enrollOne(student, true).subscribe((result) => {
      this.results.update((list) =>
        list.map((item) => (item.student.id === studentId ? result : item))
      );
    });
  }

  private enrollOne(student: Student, forced: boolean): Observable<EnrollResult> {
    const mode = this.activeMode();
    const targetId = this.activeTargetId()!;

    const request$: Observable<CohortEnrollment | ScheduledCourseEnrollment> =
      mode === 'cohort'
        ? this.enrollmentService.enrollmentToCohort(targetId, student.id)
        : this.enrollmentService.enrollmentToScheduledCourse(student.id, targetId, forced);

    return request$.pipe(
      map(() => ({
        student,
        success: true,
        message: 'Inscription réussie.',
        canForce: false,
      })),
      catchError((err) => {
        const message =
          typeof err.error === 'string' && err.error.trim()
            ? err.error
            : 'Impossible d’inscrire cet élève.';

        return of({
          student,
          success: false,
          message,
          canForce: !forced && message.includes(FORCE_HINT),
        });
      })
    );
  }

  private normalizeSearch(value: string): string {
    return value
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[̀-ͯ]/g, '');
  }
}
