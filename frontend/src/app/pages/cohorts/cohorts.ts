import {
  Component,
  computed,
  inject,
  OnInit,
  signal,
  TemplateRef,
} from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';

import type { Cohort } from '../../models/training/cohort.model';
import type { Track } from '../../models/training/track.model';
import type { Student } from '../../models/users/user.model';
import type { CohortEnrollment } from '../../models/enrollment/enrollment.model';
import { CohortService } from '../../services/training/cohort.service';
import { TrackService } from '../../services/training/track.service';
import { UserService } from '../../services/users/user.service';
import { EnrollmentService } from '../../services/enrollment/enrollment.service';

@Component({
  selector: 'app-cohorts',
  imports: [
    DatePipe,
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTabsModule,
  ],
  templateUrl: './cohorts.html',
  styleUrl: './cohorts.scss',
})
export class Cohorts implements OnInit {
  private readonly cohortService = inject(CohortService);
  private readonly trackService = inject(TrackService);
  private readonly userService = inject(UserService);
  private readonly enrollmentService = inject(EnrollmentService);
  private readonly dialog = inject(MatDialog);
  private detailDialogRef?: MatDialogRef<unknown>;

  private formDialogRef?: MatDialogRef<unknown>;

  readonly cohorts = signal<Cohort[]>([]);
  readonly selectedCohort = signal<Cohort | null>(null);
  readonly tracks = signal<Track[]>([]);
  readonly students = signal<Student[]>([]);
  readonly cohortEnrollments = signal<CohortEnrollment[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly search = signal('');

  readonly cohortName = signal('');
  readonly trackId = signal<number | null>(null);
  readonly startDate = signal('');
  readonly formError = signal('');
  readonly saving = signal(false);


  readonly trackOptions = computed(() =>
    this.tracks().sort((a, b) => a.name.localeCompare(b.name))
  );

  readonly cohortStudents = computed(() => {
    const students = this.students();

    return this.cohortEnrollments()
      .map((enrollment) => students.find((student) => student.id === enrollment.studentId))
      .filter((student): student is Student => !!student);
  });

  readonly filteredCohorts = computed(() => {
    const search = this.normalizeSearch(this.search());

    return this.cohorts().filter((cohort) =>
      search ? this.normalizeSearch(cohort.name).includes(search) : true
    );
  });

  ngOnInit(): void {
    this.cohortService.findAll().subscribe({
      next: (cohorts) => {
        this.cohorts.set(cohorts);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les promotions.');
        this.loading.set(false);
      },
    });

    this.trackService.findAll().subscribe({
      next: (tracks) => this.tracks.set(tracks),
    });

    this.userService.findAll('STUDENT').subscribe({
      next: (students) => this.students.set(students as Student[]),
    });
  }

  openCreateForm(template: TemplateRef<unknown>): void {
    this.cohortName.set('');
    this.trackId.set(null);
    this.startDate.set('');
    this.formError.set('');

    this.formDialogRef = this.dialog.open(template, {
      width: '520px',
      maxWidth: '95vw',
    });
  }

  cancelForm(): void {
    this.formDialogRef?.close();
  }

  submitForm(): void {
    if (this.saving()) return;

    const name = this.cohortName().trim();
    const trackId = this.trackId();
    const startDate = this.startDate();

    if (!name || !trackId || !startDate) {
      this.formError.set('Tous les champs sont obligatoires.');
      return;
    }

    this.formError.set('');
    this.saving.set(true);

    if (this.formDialogRef) {
      this.formDialogRef.disableClose = true;
    }

    this.cohortService.create(name, trackId, startDate).subscribe({
      next: (cohort) => {
        this.cohorts.update((list) => [...list, cohort]);
        this.saving.set(false);
        this.formDialogRef?.close();
      },
      error: (err) => {
        this.saving.set(false);

        this.formError.set(
          err.status === 409
            ? 'Une promotion portant ce nom existe déjà.'
            : 'Impossible de créer la promotion. Veuillez réessayer.'
        );

        if (this.formDialogRef) {
          this.formDialogRef.disableClose = false;
        }
      },
    });
  }

  openDetail(cohort: Cohort, template: TemplateRef<unknown>): void {
    this.selectedCohort.set(cohort);
    this.cohortEnrollments.set([]);

    this.enrollmentService.findCohortEnrollmentsByCohort(cohort.id).subscribe({
      next: (enrollments) => this.cohortEnrollments.set(enrollments),
    });

    this.detailDialogRef = this.dialog.open(template, {
      width: '90vw',
      maxWidth: '1100px',
      height: '85vh',
    });
  }

  closeDetail(): void {
    this.detailDialogRef?.close();
  }

  trackName(trackId: number): string {
    return this.tracks().find((track) => track.id === trackId)?.name ?? 'Cursus inconnu';
  }

  statusLabel(status: Cohort['status']): string {
    switch (status) {
      case 'UPCOMING':
        return 'À venir';
      case 'IN_PROGRESS':
        return 'En cours';
      case 'COMPLETED':
        return 'Terminée';
    }
  }

  private normalizeSearch(value: string): string {
    return value
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[̀-ͯ]/g, '');
  }
}
