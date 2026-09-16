import {
  Component,
  computed,
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
import { MatSelectModule } from '@angular/material/select';

import type { Cohort } from '../../models/training/cohort.model';
import type { Track } from '../../models/training/track.model';
import { CohortService } from '../../services/training/cohort.service';
import { TrackService } from '../../services/training/track.service';

@Component({
  selector: 'app-promotions',
  imports: [
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './promotions.html',
  styleUrl: './promotions.scss',
})
export class Promotions implements OnInit {
  private readonly cohortService = inject(CohortService);
  private readonly trackService = inject(TrackService);
  private readonly dialog = inject(MatDialog);

  private formDialogRef?: MatDialogRef<unknown>;

  readonly cohorts = signal<Cohort[]>([]);
  readonly tracks = signal<Track[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');

  readonly promotionName = signal('');
  readonly trackId = signal<number | null>(null);
  readonly startDate = signal('');
  readonly endDate = signal('');
  readonly formError = signal('');
  readonly saving = signal(false);

  readonly trackOptions = computed(() =>
    this.tracks().sort((a, b) => a.name.localeCompare(b.name))
  );

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
  }

  openCreateForm(template: TemplateRef<unknown>): void {
    this.promotionName.set('');
    this.trackId.set(null);
    this.startDate.set('');
    this.endDate.set('');
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

    const name = this.promotionName().trim();
    const trackId = this.trackId();
    const startDate = this.startDate();
    const endDate = this.endDate();

    if (!name || !trackId || !startDate || !endDate) {
      this.formError.set('Tous les champs sont obligatoires.');
      return;
    }

    if (endDate < startDate) {
      this.formError.set('La date de fin doit être postérieure ou égale à la date de début.');
      return;
    }

    this.formError.set('');
    this.saving.set(true);

    if (this.formDialogRef) {
      this.formDialogRef.disableClose = true;
    }

    this.cohortService.create(name, trackId, startDate, endDate).subscribe({
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
}
