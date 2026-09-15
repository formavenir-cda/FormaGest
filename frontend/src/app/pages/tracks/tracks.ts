import {
  Component,
  computed,
  inject,
  OnInit,
  signal,
  TemplateRef,
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

import type { Sector } from '../../models/training/sector.model';
import type { Track } from '../../models/training/track.model';
import { SectorService } from '../../services/training/sector.service';
import { TrackService } from '../../services/training/track.service';

@Component({
  selector: 'app-tracks',
  imports: [
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTooltip,
  ],
  templateUrl: './tracks.html',
  styleUrl: './tracks.scss',
})
export class Tracks implements OnInit {
  private readonly trackService = inject(TrackService);
  private readonly sectorService = inject(SectorService);
  private readonly dialog = inject(MatDialog);
  private readonly route = inject(ActivatedRoute);

  private formDialogRef?: MatDialogRef<unknown>;
  private deleteDialogRef?: MatDialogRef<unknown>;

  readonly tracks = signal<Track[]>([]);
  readonly sectors = signal<Sector[]>([]);
  readonly selectedTrack = signal<Track | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly search = signal('');
  readonly selectedSectorFilter = signal<number | null>(null);

  readonly trackName = signal('');
  readonly trackSectorId = signal<number | null>(null);
  readonly formError = signal('');
  readonly saving = signal(false);
  readonly editingTrack = signal<Track | null>(null);

  readonly trackToDelete = signal<Track | null>(null);
  readonly deleting = signal(false);
  readonly deleteError = signal('');

  readonly filteredTracks = computed(() => {
    const search = this.normalizeSearch(this.search());
    const sectorId = this.selectedSectorFilter();

    return this.tracks().filter((track) => {
      const matchesSearch = search
        ? this.normalizeSearch(track.name).includes(search)
        : true;
      const matchesSector = sectorId
        ? track.sectorId === sectorId
        : true;

      return matchesSearch && matchesSector;
    });
  });

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const sectorId = Number(params.get('sectorId'));
      this.selectedSectorFilter.set(Number.isFinite(sectorId) && sectorId > 0
        ? sectorId
        : null);
    });

    this.loadData();
  }

  sectorName(sectorId: number): string {
    return this.sectors().find((sector) => sector.id === sectorId)?.name
      ?? 'Filière inconnue';
  }

  openCreateForm(template: TemplateRef<unknown>): void {
    this.editingTrack.set(null);
    this.trackName.set('');
    this.trackSectorId.set(this.sectors()[0]?.id ?? null);
    this.formError.set('');

    this.formDialogRef = this.dialog.open(template, {
      width: '520px',
      maxWidth: '95vw',
    });
  }

  openEditForm(template: TemplateRef<unknown>): void {
    const track = this.selectedTrack();

    if (!track) return;

    this.editingTrack.set(track);
    this.trackName.set(track.name);
    this.trackSectorId.set(track.sectorId);
    this.formError.set('');

    this.formDialogRef = this.dialog.open(template, {
      width: '520px',
      maxWidth: '95vw',
    });
  }

  editTrack(track: Track, template: TemplateRef<unknown>): void {
    this.selectedTrack.set(track);
    this.openEditForm(template);
  }

  cancelForm(): void {
    this.formDialogRef?.close();
  }

  submitForm(): void {
    if (this.saving()) return;

    const name = this.trackName().trim();
    const sectorId = this.trackSectorId();

    if (!name) {
      this.formError.set('Le nom est obligatoire.');
      return;
    }

    if (name.length > 255) {
      this.formError.set('Le nom ne doit pas dépasser 255 caractères.');
      return;
    }

    if (!sectorId) {
      this.formError.set('La filière est obligatoire.');
      return;
    }

    this.formError.set('');
    this.saving.set(true);

    if (this.formDialogRef) {
      this.formDialogRef.disableClose = true;
    }

    const trackToEdit = this.editingTrack();
    const request = trackToEdit
      ? this.trackService.update(trackToEdit.id, name, sectorId)
      : this.trackService.create(name, sectorId);

    request.subscribe({
      next: (track) => {
        this.tracks.update((list) =>
          trackToEdit
            ? list.map((item) => item.id === track.id ? track : item)
            : [...list, track]
        );

        this.selectedTrack.set(track);
        this.saving.set(false);
        this.formDialogRef?.close();
      },
      error: (err) => {
        this.saving.set(false);
        this.formError.set(
          err.status === 409
            ? 'Un cursus portant ce nom existe déjà.'
            : 'Impossible d’enregistrer le cursus. Veuillez réessayer.'
        );

        if (this.formDialogRef) {
          this.formDialogRef.disableClose = false;
        }
      },
    });
  }

  openDeleteForm(template: TemplateRef<unknown>): void {
    const track = this.selectedTrack();

    if (!track) return;

    this.trackToDelete.set(track);
    this.deleteError.set('');

    this.deleteDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
      autoFocus: '[data-cancel-delete-track]',
    });
  }

  deleteTrack(track: Track, template: TemplateRef<unknown>): void {
    this.selectedTrack.set(track);
    this.openDeleteForm(template);
  }

  cancelDelete(): void {
    this.deleteDialogRef?.close();
  }

  confirmDelete(): void {
    const track = this.trackToDelete();

    if (!track || this.deleting()) return;

    this.deleting.set(true);
    this.deleteError.set('');

    if (this.deleteDialogRef) {
      this.deleteDialogRef.disableClose = true;
    }

    this.trackService.delete(track.id).subscribe({
      next: () => {
        this.tracks.update((list) =>
          list.filter((item) => item.id !== track.id)
        );

        if (this.selectedTrack()?.id === track.id) {
          this.selectedTrack.set(null);
        }

        this.deleting.set(false);
        this.deleteDialogRef?.close();
        this.trackToDelete.set(null);
      },
      error: (err) => {
        this.deleting.set(false);
        this.deleteError.set(
          typeof err.error === 'string' && err.error.trim()
            ? err.error
            : 'Impossible de supprimer le cursus. Veuillez réessayer.'
        );

        if (this.deleteDialogRef) {
          this.deleteDialogRef.disableClose = false;
        }
      },
    });
  }

  private loadData(): void {
    this.loading.set(true);
    this.error.set('');

    this.sectorService.findAll().subscribe({
      next: (sectors) => {
        this.sectors.set(sectors);
        this.loadTracks();
      },
      error: () => {
        this.error.set('Impossible de charger les filières.');
        this.loading.set(false);
      },
    });
  }

  private loadTracks(): void {
    this.trackService.findAll().subscribe({
      next: (tracks) => {
        this.tracks.set(tracks);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les cursus.');
        this.loading.set(false);
      },
    });
  }

  private normalizeSearch(value: string): string {
    return value
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '');
  }
}
