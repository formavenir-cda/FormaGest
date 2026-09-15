import {
  Component,
  computed,
  inject,
  OnInit,
  signal,
  TemplateRef,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltip } from '@angular/material/tooltip';

import { SectorService } from '../../services/training/sector.service';
import type { Sector } from '../../models/training/sector.model';

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
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);

  private createDialogRef?: MatDialogRef<unknown>;
  private deleteDialogRef?: MatDialogRef<unknown>;

  readonly sectors = signal<Sector[]>([]);
  readonly selectedSector = signal<Sector | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly search = signal('');

  readonly newSectorName = signal('');
  readonly createError = signal('');
  readonly creating = signal(false);
  readonly editingSector = signal<Sector | null>(null);
  readonly sectorToDelete = signal<Sector | null>(null);
  readonly deleting = signal(false);
  readonly deleteError = signal('');

  readonly filteredSectors = computed(() => {
    const search = this.normalizeSearch(this.search());

    return this.sectors().filter((sector) =>
      search ? this.normalizeSearch(sector.name).includes(search) : true
    );
  });

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

  openSectorTracks(sector: Sector): void {
    this.router.navigate(['/formation/cursus'], {
      queryParams: { sectorId: sector.id },
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
      this.createError.set('Le nom ne doit pas dépasser 255 caractères.');
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

  editSector(sector: Sector, template: TemplateRef<unknown>): void {
    this.selectedSector.set(sector);
    this.openEditForm(template);
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

  deleteSector(sector: Sector, template: TemplateRef<unknown>): void {
    this.selectedSector.set(sector);
    this.openDeleteForm(template);
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

  private normalizeSearch(value: string): string {
    return value
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '');
  }
}
