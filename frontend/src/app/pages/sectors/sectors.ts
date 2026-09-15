import {
  Component,
  inject,
  OnInit,
  signal,
  TemplateRef,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { SectorService } from '../../services/training/sector.service';
import { TrackService } from '../../services/training/track.service';
import type { Sector } from '../../models/training/sector.model';
import type { Track } from '../../models/training/track.model';
import {MatTooltip, MatTooltipModule} from '@angular/material/tooltip';

@Component({
  selector: 'app-sectors',
  imports: [
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatTooltip,
  ],
  templateUrl: './sectors.html',
  styleUrl: './sectors.scss',
})
export class Sectors implements OnInit {
  private readonly sectorService = inject(SectorService);
  private readonly trackService = inject(TrackService);
  private readonly dialog = inject(MatDialog);

  private createDialogRef?: MatDialogRef<unknown>;
  private deleteDialogRef?: MatDialogRef<unknown>;
  private createTrackDialogRef?: MatDialogRef<unknown>;
  private deleteTrackDialogRef?: MatDialogRef<unknown>;

  readonly sectors = signal<Sector[]>([]);
  readonly tracks = signal<Track[]>([]);
  readonly selectedSector = signal<Sector | null>(null);
  readonly selectedTrack = signal<Track | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly tracksLoading = signal(false);
  readonly tracksError = signal('');

  readonly newSectorName = signal('');
  readonly createError = signal('');
  readonly creating = signal(false);
  readonly editingSector = signal<Sector | null>(null);
  readonly sectorToDelete = signal<Sector | null>(null);
  readonly deleting = signal(false);
  readonly deleteError = signal('');
  readonly newTrackName = signal('');
  readonly createTrackError = signal('');
  readonly creatingTrack = signal(false);
  readonly editingTrack = signal<Track | null>(null);
  readonly trackToDelete = signal<Track | null>(null);
  readonly deletingTrack = signal(false);
  readonly deleteTrackError = signal('');

  ngOnInit(): void {
    this.sectorService.findAll().subscribe({
      next: (sectors) => {
        this.sectors.set(sectors);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les filières.');
        this.loading.set(false);
      },
    });
  }

  selectSector(sector: Sector): void {
    this.selectedSector.set(sector);
    this.selectedTrack.set(null);
    this.loadTracks(sector.id);
  }

  private loadTracks(sectorId: number): void {
    this.tracks.set([]);
    this.tracksError.set('');
    this.tracksLoading.set(true);

    this.trackService.findBySector(sectorId).subscribe({
      next: (tracks) => {
        this.tracks.set(tracks);
        this.tracksLoading.set(false);
      },
      error: () => {
        this.tracksError.set('Impossible de charger les cursus.');
        this.tracksLoading.set(false);
      },
    });
  }

  openCreateForm(template: TemplateRef<unknown>): void {
    this.editingSector.set(null);
    this.newSectorName.set('');
    this.createError.set('');

    this.createDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  cancelCreate(): void {
    this.createDialogRef?.close();
  }

  submitForm(): void {
    if (this.creating()) {
      return;
    }

    const name = this.newSectorName().trim();

    if (!name) {
      this.createError.set('Le nom est obligatoire.');
      return;
    }

    if (name.length > 255) {
      this.createError.set(
        'Le nom ne doit pas dépasser 255 caractères.',
      );
      return;
    }

    this.createError.set('');
    this.creating.set(true);

    if (this.createDialogRef) {
      this.createDialogRef.disableClose = true;
    }

    const sectorToEdit = this.editingSector();

    const request = sectorToEdit
      ? this.sectorService.update(sectorToEdit.id, name)
      : this.sectorService.create(name);

    request.subscribe({
      next: (sector) => {
        this.sectors.update((list) =>
          sectorToEdit
            ? list.map((item) => item.id === sector.id ? sector : item)
            : [...list, sector]
        );

        this.selectedSector.set(sector);
        this.creating.set(false);
        this.createDialogRef?.close();

        if (!sectorToEdit) {
          this.selectSector(sector);
        }
      },
      error: (err) => {
        this.creating.set(false);

        this.createError.set(
          err.status === 409
            ? 'Une filière portant ce nom existe déjà.'
            : 'Impossible de créer la filière. Veuillez réessayer.'
        );

        if (this.createDialogRef) {
          this.createDialogRef.disableClose = false;
        }
      },
    });
  }

  openEditForm(template: TemplateRef<unknown>): void {
    const sector = this.selectedSector();

    if (!sector) return;

    this.editingSector.set(sector);
    this.newSectorName.set(sector.name);
    this.createError.set('');

    this.createDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  openDeleteForm(template: TemplateRef<unknown>): void {
    const sector = this.selectedSector();

    if (!sector) return;

    this.sectorToDelete.set(sector);
    this.deleteError.set('');

    this.deleteDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
      autoFocus: '[data-cancel-delete]',
    });
  }

  cancelDelete(): void {
    this.deleteDialogRef?.close();
  }

  confirmDelete(): void {
    const sector = this.sectorToDelete();

    if (!sector || this.deleting()) return;

    this.deleting.set(true);
    this.deleteError.set('');

    if (this.deleteDialogRef) {
      this.deleteDialogRef.disableClose = true;
    }

    this.sectorService.delete(sector.id).subscribe({
      next: () => {
        this.sectors.update((list) =>
          list.filter((item) => item.id !== sector.id)
        );

        if (this.selectedSector()?.id === sector.id) {
          this.selectedSector.set(null);
          this.selectedTrack.set(null);
          this.tracks.set([]);
        }

        this.deleting.set(false);
        this.deleteDialogRef?.close();
        this.sectorToDelete.set(null);
      },
      error: (err) => {
        this.deleting.set(false);

        this.deleteError.set(
          typeof err.error === 'string' && err.error.trim()
            ? err.error
            : 'Impossible de supprimer la filière. Veuillez réessayer.'
        );

        if (this.deleteDialogRef) {
          this.deleteDialogRef.disableClose = false;
        }
      },
    });
  }

  openCreateTrackForm(template: TemplateRef<unknown>): void {
    if (!this.selectedSector()) return;

    this.editingTrack.set(null);
    this.newTrackName.set('');
    this.createTrackError.set('');

    this.createTrackDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  cancelTrackForm(): void {
    this.createTrackDialogRef?.close();
  }

  submitTrackForm(): void {
    const sector = this.selectedSector();

    if (!sector || this.creatingTrack()) {
      return;
    }

    const name = this.newTrackName().trim();

    if (!name) {
      this.createTrackError.set('Le nom est obligatoire.');
      return;
    }

    if (name.length > 255) {
      this.createTrackError.set(
        'Le nom ne doit pas dépasser 255 caractères.',
      );
      return;
    }

    this.createTrackError.set('');
    this.creatingTrack.set(true);

    if (this.createTrackDialogRef) {
      this.createTrackDialogRef.disableClose = true;
    }

    const trackToEdit = this.editingTrack();

    const request = trackToEdit
      ? this.trackService.update(trackToEdit.id, name, sector.id)
      : this.trackService.create(name, sector.id);

    request.subscribe({
      next: (track) => {
        this.tracks.update((list) =>
          trackToEdit
            ? list.map((item) => item.id === track.id ? track : item)
            : [...list, track]
        );

        this.selectedTrack.set(track);
        this.creatingTrack.set(false);
        this.createTrackDialogRef?.close();
      },
      error: (err) => {
        this.creatingTrack.set(false);

        this.createTrackError.set(
          err.status === 409
            ? 'Un cursus portant ce nom existe déjà.'
            : 'Impossible d’enregistrer le cursus. Veuillez réessayer.'
        );

        if (this.createTrackDialogRef) {
          this.createTrackDialogRef.disableClose = false;
        }
      },
    });
  }

  openEditTrackForm(template: TemplateRef<unknown>): void {
    const track = this.selectedTrack();

    if (!track) return;

    this.editingTrack.set(track);
    this.newTrackName.set(track.name);
    this.createTrackError.set('');

    this.createTrackDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  openDeleteTrackForm(template: TemplateRef<unknown>): void {
    const track = this.selectedTrack();

    if (!track) return;

    this.trackToDelete.set(track);
    this.deleteTrackError.set('');

    this.deleteTrackDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
      autoFocus: '[data-cancel-delete-track]',
    });
  }

  cancelDeleteTrack(): void {
    this.deleteTrackDialogRef?.close();
  }

  confirmDeleteTrack(): void {
    const track = this.trackToDelete();

    if (!track || this.deletingTrack()) return;

    this.deletingTrack.set(true);
    this.deleteTrackError.set('');

    if (this.deleteTrackDialogRef) {
      this.deleteTrackDialogRef.disableClose = true;
    }

    this.trackService.delete(track.id).subscribe({
      next: () => {
        this.tracks.update((list) =>
          list.filter((item) => item.id !== track.id)
        );

        if (this.selectedTrack()?.id === track.id) {
          this.selectedTrack.set(null);
        }

        this.deletingTrack.set(false);
        this.deleteTrackDialogRef?.close();
        this.trackToDelete.set(null);
      },
      error: (err) => {
        this.deletingTrack.set(false);

        this.deleteTrackError.set(
          typeof err.error === 'string' && err.error.trim()
            ? err.error
            : 'Impossible de supprimer le cursus. Veuillez réessayer.'
        );

        if (this.deleteTrackDialogRef) {
          this.deleteTrackDialogRef.disableClose = false;
        }
      },
    });
  }
}
