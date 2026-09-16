import {
  Component,
  computed,
  inject,
  OnInit,
  signal,
  TemplateRef,
  ViewChild,
} from '@angular/core';
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
import { CourseService } from '../../services/training/course.service';
import { SectorService } from '../../services/training/sector.service';
import { TrackService } from '../../services/training/track.service';

@Component({
  selector: 'app-courses',
  imports: [
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
  readonly associationSectorId = signal<number | null>(null);
  readonly associationTrackId = signal<number | null>(null);
  readonly associationPosition = signal<number | null>(null);
  readonly formError = signal('');
  readonly saving = signal(false);
  readonly editingCourse = signal<Course | null>(null);

  readonly courseToDelete = signal<Course | null>(null);
  readonly deleting = signal(false);
  readonly deleteError = signal('');
  readonly unassociatedCourseMessage = signal('');

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

    return this.tracks()
      .filter((track) => !sectorId || track.sectorId === sectorId)
      .sort((a, b) => a.name.localeCompare(b.name));
  });

  readonly selectedSectorHasNoTracks = computed(() =>
    !!this.associationSectorId() && this.associationTrackOptions().length === 0
  );

  readonly filteredCourses = computed(() => {
    const search = this.normalizeSearch(this.search());
    const sectorId = this.selectedSectorFilter();
    const trackId = this.selectedTrackFilter();

    return this.courses().filter((course) => {
      const searchableValues = [
        course.name,
        ...course.associations.map((association) => association.sectorName),
        ...course.associations.map((association) => association.trackName),
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
    this.associationSectorId.set(null);
    this.associationTrackId.set(null);
    this.associationPosition.set(null);
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
    this.associationSectorId.set(course.associations[0]?.sectorId ?? null);
    this.associationTrackId.set(course.associations[0]?.trackId ?? null);
    this.associationPosition.set(course.associations[0]?.position ?? null);
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

    if (!name) {
      this.formError.set('Le nom est obligatoire.');
      return;
    }

    if (name.length > 255) {
      this.formError.set('Le nom ne doit pas dépasser 255 caractères.');
      return;
    }

    const courseToEdit = this.editingCourse();
    const associations = this.selectedFormAssociations();

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
            this.saveCourse(name, courseToEdit, associations);
          }
        });
      }

      return;
    }

    this.saveCourse(name, courseToEdit, associations);
  }

  private saveCourse(
    name: string,
    courseToEdit: Course | null,
    associations: CourseAssociation[]
  ): void {
    this.formError.set('');
    this.saving.set(true);

    if (this.formDialogRef) {
      this.formDialogRef.disableClose = true;
    }

    const request = courseToEdit
      ? this.courseService.update(courseToEdit.id, name, associations)
      : this.courseService.create(name, associations);

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
  }

  selectTrackFilter(trackId: number | null): void {
    this.selectedTrackFilter.set(trackId);
  }

  selectAssociationSector(sectorId: number | null): void {
    this.associationSectorId.set(sectorId);
    this.associationTrackId.set(null);
    this.associationPosition.set(null);
  }

  selectAssociationTrack(trackId: number | null): void {
    this.associationTrackId.set(trackId);

    const existingAssociation = this.editingCourse()?.associations.find(
      (association) => association.trackId === trackId
    );

    this.associationPosition.set(existingAssociation?.position ?? null);
  }

  setAssociationPosition(position: string | number | null): void {
    const value = Number(position);

    this.associationPosition.set(Number.isFinite(value) && value > 0
      ? value
      : null);
  }

  private selectedFormAssociations(): CourseAssociation[] {
    const trackId = this.associationTrackId();

    if (!trackId) {
      return [];
    }

    const track = this.tracks().find((item) => item.id === trackId);
    const sector = track
      ? this.sectors().find((item) => item.id === track.sectorId)
      : null;

    if (!track || !sector) {
      return [];
    }

    const existingAssociation = this.editingCourse()?.associations.find(
      (association) => association.trackId === track.id
    );

    return [
      {
        sectorId: sector.id,
        sectorName: sector.name,
        trackId: track.id,
        trackName: track.name,
        position: this.associationPosition()
          ?? existingAssociation?.position
          ?? 0,
      },
    ];
  }

  associatedPositions(course: Course): string {
    return this.distinctNames(
      course.associations.map((association) =>
        association.position.toString()
      )
    );
  }

  associatedSectorNames(course: Course): string {
    return this.distinctNames(
      course.associations.map((association) => association.sectorName)
    );
  }

  associatedTrackNames(course: Course): string {
    return this.distinctNames(
      course.associations.map((association) => association.trackName)
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
}
