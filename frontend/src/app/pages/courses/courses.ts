import {
  Component,
  computed,
  inject,
  OnInit,
  signal,
  TemplateRef,
} from '@angular/core';
import {
  CdkDragDrop,
  DragDropModule,
  moveItemInArray,
} from '@angular/cdk/drag-drop';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, switchMap } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';

import type { Course, CourseAssociation } from '../../models/training/course.model';
import type { Sector } from '../../models/training/sector.model';
import type { Track } from '../../models/training/track.model';
import type { TrackCourse } from '../../models/training/track-course.model';
import { CourseService } from '../../services/training/course.service';
import { SectorService } from '../../services/training/sector.service';
import { TrackService } from '../../services/training/track.service';

@Component({
  selector: 'app-courses',
  imports: [
    DragDropModule,
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTooltip,
  ],
  templateUrl: './courses.html',
  styleUrl: './courses.scss',
})
export class Courses implements OnInit {
  private readonly courseService = inject(CourseService);
  private readonly sectorService = inject(SectorService);
  private readonly trackService = inject(TrackService);
  private readonly dialog = inject(MatDialog);
  private readonly route = inject(ActivatedRoute);

  private formDialogRef?: MatDialogRef<unknown>;
  private deleteDialogRef?: MatDialogRef<unknown>;

  readonly courses = signal<Course[]>([]);
  readonly sectors = signal<Sector[]>([]);
  readonly tracks = signal<Track[]>([]);
  readonly selectedCourse = signal<Course | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly search = signal('');
  readonly selectedSectorFilter = signal<number | null>(null);
  readonly selectedTrackFilter = signal<number | null>(null);

  readonly courseName = signal('');
  readonly courseDurationInDays = signal<number | null>(null);
  readonly formError = signal('');
  readonly saving = signal(false);
  readonly editingCourse = signal<Course | null>(null);

  readonly courseToDelete = signal<Course | null>(null);
  readonly deleting = signal(false);
  readonly deleteError = signal('');
  readonly reordering = signal(false);
  readonly reorderError = signal('');

  private trackDialogRef?: MatDialogRef<unknown>;

  readonly editTrackName = signal('');
  readonly editTrackSectorId = signal<number | null>(null);
  readonly trackFormError = signal('');
  readonly trackSaving = signal(false);

  readonly currentTrack = computed(() =>
    this.tracks().find((track) => track.id === this.selectedTrackFilter()) ?? null
  );

  readonly currentTrackSectorName = computed(() => {
    const track = this.currentTrack();

    return track
      ? this.sectors().find((sector) => sector.id === track.sectorId)?.name ?? 'Filière inconnue'
      : '';
  });

  private attachDialogRef?: MatDialogRef<unknown>;

  readonly courseToAttachId = signal<number | null>(null);
  readonly attaching = signal(false);
  readonly attachError = signal('');

  readonly attachableCourses = computed(() => {
    const track = this.currentTrack();

    if (!track) return [];

    return this.courses()
      .filter((course) =>
        !course.associations.some((association) => association.trackId === track.id)
      )
      .sort((a, b) => a.name.localeCompare(b.name));
  });

  readonly sectorOptions = computed(() => {
    return this.sectors()
      .map((sector) => ({
        id: sector.id,
        name: sector.name,
      }))
      .sort((a, b) => a.name.localeCompare(b.name));
  });

  readonly trackOptions = computed(() => {
    const sectorId = this.selectedSectorFilter();
    const options = new Map<number, string>();

    this.courses().forEach((course) => {
      course.associations.forEach((association) => {
        if (!sectorId || association.sectorId === sectorId) {
          options.set(association.trackId, association.trackName);
        }
      });
    });

    return [...options.entries()]
      .map(([id, name]) => ({ id, name }))
      .sort((a, b) => a.name.localeCompare(b.name));
  });

  readonly canDragReorder = computed(() =>
    !!this.selectedTrackFilter()
    && !this.normalizeSearch(this.search())
    && !this.reordering()
  );

  readonly filteredCourses = computed(() => {
    const search = this.normalizeSearch(this.search());
    const sectorId = this.selectedSectorFilter();
    const trackId = this.selectedTrackFilter();

    const filtered = this.courses().filter((course) => {
      const visibleAssociations = this.visibleAssociations(course);
      const searchableValues = [
        course.name,
        ...visibleAssociations.map((association) => association.sectorName),
        ...visibleAssociations.map((association) => association.trackName),
      ];

      const matchesSearch = search
        ? searchableValues.some((value) =>
          this.normalizeSearch(value).includes(search)
        )
        : true;

      const matchesSector = sectorId
        ? course.associations.some((association) =>
          association.sectorId === sectorId
        )
        : true;

      const matchesTrack = trackId
        ? course.associations.some((association) =>
          association.trackId === trackId
        )
        : true;

      return matchesSearch && matchesSector && matchesTrack;
    });

    return trackId
      ? filtered.sort((a, b) =>
        this.positionForTrack(a, trackId) - this.positionForTrack(b, trackId)
      )
      : filtered.sort((a, b) => a.name.localeCompare(b.name));
  });

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const sectorId = Number(params.get('sectorId'));
      const trackId = Number(params.get('trackId'));

      this.selectedSectorFilter.set(Number.isFinite(sectorId) && sectorId > 0
        ? sectorId
        : null);
      this.selectedTrackFilter.set(Number.isFinite(trackId) && trackId > 0
        ? trackId
        : null);
    });

    this.courseService.findAll().subscribe({
      next: (courses) => {
        this.courses.set(courses);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les cours.');
        this.loading.set(false);
      },
    });

    this.sectorService.findAll().subscribe({
      next: (sectors) => this.sectors.set(sectors),
    });

    this.trackService.findAll().subscribe({
      next: (tracks) => this.tracks.set(tracks),
    });
  }

  openCreateForm(template: TemplateRef<unknown>): void {
    this.editingCourse.set(null);
    this.courseName.set('');
    this.courseDurationInDays.set(null);
    this.formError.set('');

    this.formDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  openEditForm(template: TemplateRef<unknown>): void {
    const course = this.selectedCourse();

    if (!course) return;

    this.editingCourse.set(course);
    this.courseName.set(course.name);
    this.courseDurationInDays.set(course.durationInDays);
    this.formError.set('');

    this.formDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  editCourse(course: Course, template: TemplateRef<unknown>): void {
    this.selectedCourse.set(course);
    this.openEditForm(template);
  }

  cancelForm(): void {
    this.formDialogRef?.close();
  }

  submitForm(): void {
    if (this.saving()) return;

    const name = this.courseName().trim();
    const durationInDays = this.courseDurationInDays();

    if (!name) {
      this.formError.set('Le nom est obligatoire.');
      return;
    }

    if (name.length > 255) {
      this.formError.set('Le nom ne doit pas dépasser 255 caractères.');
      return;
    }

    if (!durationInDays || durationInDays < 1) {
      this.formError.set('La durée doit être supérieure à 0.');
      return;
    }

    this.formError.set('');
    this.saving.set(true);

    if (this.formDialogRef) {
      this.formDialogRef.disableClose = true;
    }

    const courseToEdit = this.editingCourse();
    const track = this.currentTrack();

    const request$ = courseToEdit
      ? this.courseService.update(courseToEdit.id, name, durationInDays)
      : this.courseService.create(name, durationInDays).pipe(
        switchMap((course) =>
          track ? this.courseService.updateTracks(course.id, [track.id]) : of(course)
        )
      );

    request$.subscribe({
      next: (course) => {
        this.courses.update((list) =>
          courseToEdit
            ? list.map((item) => item.id === course.id ? course : item)
            : [...list, course]
        );

        this.selectedCourse.set(course);
        this.saving.set(false);
        this.formDialogRef?.close();
      },
      error: (err) => {
        this.saving.set(false);
        this.formError.set(
          err.status === 409
            ? 'Un cours portant ce nom existe déjà.'
            : 'Impossible d’enregistrer le cours. Veuillez réessayer.'
        );

        if (this.formDialogRef) {
          this.formDialogRef.disableClose = false;
        }
      },
    });
  }

  openAttachCourseForm(template: TemplateRef<unknown>): void {
    this.courseToAttachId.set(null);
    this.attachError.set('');

    this.attachDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  cancelAttachCourse(): void {
    this.attachDialogRef?.close();
  }

  confirmAttachCourse(): void {
    const track = this.currentTrack();
    const courseId = this.courseToAttachId();

    if (!track || !courseId || this.attaching()) return;

    const course = this.courses().find((item) => item.id === courseId);

    if (!course) return;

    this.attaching.set(true);
    this.attachError.set('');

    if (this.attachDialogRef) {
      this.attachDialogRef.disableClose = true;
    }

    const trackIds = [
      ...course.associations.map((association) => association.trackId),
      track.id,
    ];

    this.courseService.updateTracks(course.id, trackIds).subscribe({
      next: (updatedCourse) => {
        this.courses.update((list) =>
          list.map((item) => item.id === updatedCourse.id ? updatedCourse : item)
        );

        this.attaching.set(false);
        this.attachDialogRef?.close();
      },
      error: (err) => {
        this.attaching.set(false);
        this.attachError.set(this.errorMessage(
          err,
          'Impossible d’ajouter ce cours au cursus. Veuillez réessayer.'
        ));

        if (this.attachDialogRef) {
          this.attachDialogRef.disableClose = false;
        }
      },
    });
  }

  deleteCourse(course: Course, template: TemplateRef<unknown>): void {
    this.selectedCourse.set(course);
    this.openDeleteForm(template);
  }

  openDeleteForm(template: TemplateRef<unknown>): void {
    const course = this.selectedCourse();

    if (!course) return;

    this.courseToDelete.set(course);
    this.deleteError.set('');

    this.deleteDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
      autoFocus: '[data-cancel-delete-course]',
    });
  }

  cancelDelete(): void {
    this.deleteDialogRef?.close();
  }

  confirmDelete(): void {
    const course = this.courseToDelete();

    if (!course || this.deleting()) return;

    this.deleting.set(true);
    this.deleteError.set('');

    if (this.deleteDialogRef) {
      this.deleteDialogRef.disableClose = true;
    }

    this.courseService.delete(course.id).subscribe({
      next: () => {
        this.courses.update((list) =>
          list.filter((item) => item.id !== course.id)
        );

        if (this.selectedCourse()?.id === course.id) {
          this.selectedCourse.set(null);
        }

        this.deleting.set(false);
        this.deleteDialogRef?.close();
        this.courseToDelete.set(null);
      },
      error: (err) => {
        this.deleting.set(false);
        if (err.status === 409) {
          this.deleteError.set(this.errorMessage(
            err,
            'Impossible de supprimer le cours. Veuillez réessayer.'
          ));

          if (this.deleteDialogRef) {
            this.deleteDialogRef.disableClose = false;
          }

          return;
        }
        this.deleteError.set(
          typeof err.error === 'string' && err.error.trim()
            ? err.error
            : 'Impossible de supprimer le cours. Veuillez réessayer.'
        );

        if (this.deleteDialogRef) {
          this.deleteDialogRef.disableClose = false;
        }
      },
    });
  }

  openEditTrackForm(template: TemplateRef<unknown>): void {
    const track = this.currentTrack();

    if (!track) return;

    this.editTrackName.set(track.name);
    this.editTrackSectorId.set(track.sectorId);
    this.trackFormError.set('');

    this.trackDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  cancelTrackForm(): void {
    this.trackDialogRef?.close();
  }

  submitTrackForm(): void {
    const track = this.currentTrack();

    if (!track || this.trackSaving()) return;

    const name = this.editTrackName().trim();
    const sectorId = this.editTrackSectorId();

    if (!name) {
      this.trackFormError.set('Le nom est obligatoire.');
      return;
    }

    if (name.length > 255) {
      this.trackFormError.set('Le nom ne doit pas dépasser 255 caractères.');
      return;
    }

    if (!sectorId) {
      this.trackFormError.set('La filière est obligatoire.');
      return;
    }

    this.trackFormError.set('');
    this.trackSaving.set(true);

    if (this.trackDialogRef) {
      this.trackDialogRef.disableClose = true;
    }

    this.trackService.update(track.id, name, sectorId).subscribe({
      next: (updatedTrack) => {
        this.tracks.update((list) =>
          list.map((item) => item.id === updatedTrack.id ? updatedTrack : item)
        );

        this.trackSaving.set(false);
        this.trackDialogRef?.close();
      },
      error: (err) => {
        this.trackSaving.set(false);
        this.trackFormError.set(this.errorMessage(
          err,
          err.status === 409
            ? 'Un cursus portant ce nom existe déjà.'
            : 'Impossible d’enregistrer le cursus. Veuillez réessayer.'
        ));

        if (this.trackDialogRef) {
          this.trackDialogRef.disableClose = false;
        }
      },
    });
  }

  selectSectorFilter(sectorId: number | null): void {
    this.selectedSectorFilter.set(sectorId);
    this.selectedTrackFilter.set(null);
    this.reorderError.set('');
  }

  selectTrackFilter(trackId: number | null): void {
    this.selectedTrackFilter.set(trackId);
    this.reorderError.set('');
  }

  dropCourse(event: CdkDragDrop<Course[]>): void {
    const trackId = this.selectedTrackFilter();

    if (!trackId || !this.canDragReorder()) return;

    const orderedCourses = this.coursesForTrack(trackId);

    if (event.previousIndex === event.currentIndex) {
      return;
    }

    const reorderedCourses = [...orderedCourses];
    moveItemInArray(reorderedCourses, event.previousIndex, event.currentIndex);

    const order = reorderedCourses.map((item, index) => ({
      courseId: item.id,
      position: index + 1,
    }));

    this.reordering.set(true);
    this.reorderError.set('');

    this.trackService.reorderCourses(trackId, order).subscribe({
      next: (trackCourses) => {
        this.applyTrackCourseOrder(trackId, trackCourses);
        this.reordering.set(false);
      },
      error: (err) => {
        this.reordering.set(false);
        if (err.status === 409) {
          this.reorderError.set(this.errorMessage(
            err,
            'Impossible de modifier l’ordre des cours. Veuillez réessayer.'
          ));
          return;
        }
        this.reorderError.set(
          'Impossible de modifier l’ordre des cours. Veuillez réessayer.'
        );
      },
    });
  }

  private visibleAssociations(course: Course): CourseAssociation[] {
    const trackId = this.selectedTrackFilter();

    return trackId
      ? course.associations.filter((association) =>
        association.trackId === trackId
      )
      : course.associations;
  }

  private positionForTrack(course: Course, trackId: number): number {
    return course.associations.find((association) =>
      association.trackId === trackId
    )?.position ?? Number.MAX_SAFE_INTEGER;
  }

  private coursesForTrack(trackId: number): Course[] {
    return this.courses()
      .filter((course) =>
        course.associations.some((association) =>
          association.trackId === trackId
        )
      )
      .sort((a, b) =>
        this.positionForTrack(a, trackId) - this.positionForTrack(b, trackId)
      );
  }

  private applyTrackCourseOrder(
    trackId: number,
    trackCourses: TrackCourse[]
  ): void {
    const positionsByCourseId = new Map(
      trackCourses.map((trackCourse) => [
        trackCourse.courseId,
        trackCourse.position,
      ])
    );

    this.courses.update((courses) =>
      courses.map((course) => ({
        ...course,
        associations: course.associations.map((association) =>
          association.trackId === trackId && positionsByCourseId.has(course.id)
            ? {
              ...association,
              position: positionsByCourseId.get(course.id)!,
            }
            : association
        ),
      }))
    );
  }

  associatedPositions(course: Course): string {
    return this.distinctNames(
      this.visibleAssociations(course).map((association) =>
        association.position.toString()
      )
    );
  }

  associatedSectorNames(course: Course): string {
    return this.distinctNames(
      this.visibleAssociations(course).map((association) =>
        association.sectorName
      )
    );
  }

  associatedTrackNames(course: Course): string {
    return this.distinctNames(
      this.visibleAssociations(course).map((association) =>
        association.trackName
      )
    );
  }

  private distinctNames(names: string[]): string {
    const distinct = [...new Set(names)].filter(Boolean);

    return distinct.length ? distinct.join(', ') : 'Non associé';
  }

  private normalizeSearch(value: string): string {
    return value
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '');
  }

  private errorMessage(err: unknown, fallback: string): string {
    const httpError = err as {
      error?: string | { message?: string; detail?: string };
      status?: number;
    };
    const body = httpError.error;

    if (typeof body === 'string' && body.trim()) {
      return body;
    }

    if (body && typeof body === 'object') {
      if (body.message) {
        return body.message;
      }

      if (body.detail) {
        return body.detail;
      }
    }

    return httpError.status === 409
      ? 'Cette action est impossible car ce cours ou ce cursus est utilisé dans une promotion.'
      : fallback;
  }
}
