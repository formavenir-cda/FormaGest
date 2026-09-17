import {
  Component,
  computed,
  inject,
  OnInit,
  signal,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import {
  CdkDragDrop,
  DragDropModule,
  moveItemInArray,
} from '@angular/cdk/drag-drop';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
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
  private unassociatedCourseDialogRef?: MatDialogRef<unknown>;

  @ViewChild('unassociatedCourseDialog')
  private unassociatedCourseDialog?: TemplateRef<unknown>;

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
  readonly associationSectorId = signal<number | null>(null);
  readonly associationTrackId = signal<number | null>(null);
  readonly pendingAssociations = signal<CourseAssociation[]>([]);
  readonly formError = signal('');
  readonly saving = signal(false);
  readonly editingCourse = signal<Course | null>(null);

  readonly courseToDelete = signal<Course | null>(null);
  readonly deleting = signal(false);
  readonly deleteError = signal('');
  readonly unassociatedCourseMessage = signal('');
  readonly reordering = signal(false);
  readonly reorderError = signal('');

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

  readonly associationTrackOptions = computed(() => {
    const sectorId = this.associationSectorId();
    const selectedTrackIds = new Set(
      this.pendingAssociations().map((association) => association.trackId)
    );

    return this.tracks()
      .filter((track) => !sectorId || track.sectorId === sectorId)
      .filter((track) => !selectedTrackIds.has(track.id))
      .sort((a, b) => a.name.localeCompare(b.name));
  });

  readonly selectedSectorHasNoTracks = computed(() =>
    !!this.associationSectorId() && this.associationTrackOptions().length === 0
  );

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
    this.associationSectorId.set(null);
    this.associationTrackId.set(null);
    this.pendingAssociations.set([]);
    this.formError.set('');

    this.formDialogRef = this.dialog.open(template, {
      width: '520px',
      maxWidth: '95vw',
    });
  }

  openEditForm(template: TemplateRef<unknown>): void {
    const course = this.selectedCourse();

    if (!course) return;

    this.editingCourse.set(course);
    this.courseName.set(course.name);
    this.courseDurationInDays.set(course.durationInDays);
    this.associationSectorId.set(null);
    this.associationTrackId.set(null);
    this.pendingAssociations.set([...course.associations]);
    this.formError.set('');

    this.formDialogRef = this.dialog.open(template, {
      width: '520px',
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

    const courseToEdit = this.editingCourse();
    const associations = this.pendingAssociations();

    if (!courseToEdit && associations.length === 0) {
      this.unassociatedCourseMessage.set(
        this.associationSectorId()
          ? 'La filière seule ne peut pas être enregistrée sans cursus. Voulez-vous créer ce cours sans cursus associé ?'
          : 'Êtes-vous sûr de vouloir créer un cours sans filière ni cursus ?'
      );

      if (this.unassociatedCourseDialog) {
        this.unassociatedCourseDialogRef = this.dialog.open(
          this.unassociatedCourseDialog,
          {
            width: '480px',
            maxWidth: '95vw',
            autoFocus: '[data-cancel-unassociated-course]',
          }
        );

        this.unassociatedCourseDialogRef.afterClosed().subscribe((confirmed) => {
          if (confirmed) {
            this.saveCourse(name, durationInDays, courseToEdit, associations);
          }
        });
      }

      return;
    }

    this.saveCourse(name, durationInDays, courseToEdit, associations);
  }

  private saveCourse(
    name: string,
    durationInDays: number,
    courseToEdit: Course | null,
    associations: CourseAssociation[]
  ): void {
    this.formError.set('');
    this.saving.set(true);

    if (this.formDialogRef) {
      this.formDialogRef.disableClose = true;
    }

    const request = courseToEdit
      ? this.courseService.update(courseToEdit.id, name, durationInDays, associations)
      : this.courseService.create(name, durationInDays, associations);

    request.subscribe({
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
        if (err.status === 409) {
          this.formError.set(this.errorMessage(
            err,
            'Impossible d’enregistrer le cours. Veuillez réessayer.'
          ));

          if (this.formDialogRef) {
            this.formDialogRef.disableClose = false;
          }

          return;
        }
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

  selectAssociationSector(sectorId: number | null): void {
    this.associationSectorId.set(sectorId);
    this.associationTrackId.set(null);
  }

  selectAssociationTrack(trackId: number | null): void {
    this.associationTrackId.set(trackId);
  }

  addAssociation(): void {
    const trackId = this.associationTrackId();

    if (!trackId) {
      return;
    }

    const track = this.tracks().find((item) => item.id === trackId);
    const sector = track
      ? this.sectors().find((item) => item.id === track.sectorId)
      : null;

    if (!track || !sector) {
      return;
    }

    if (this.pendingAssociations().some((association) =>
      association.trackId === track.id
    )) {
      return;
    }

    this.pendingAssociations.update((associations) => [
      ...associations,
      {
        sectorId: sector.id,
        sectorName: sector.name,
        trackId: track.id,
        trackName: track.name,
        position: 0,
      },
    ]);
    this.associationTrackId.set(null);
  }

  removeAssociation(trackId: number): void {
    this.pendingAssociations.update((associations) =>
      associations.filter((association) => association.trackId !== trackId)
    );
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
