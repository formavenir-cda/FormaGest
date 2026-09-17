import { Component, computed, inject, OnInit, signal, TemplateRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { UserService } from '../../services/users/user.service';
import type { Teacher } from '../../models/users/user.model';
import { SectorService } from '../../services/training/sector.service';
import type { Sector } from '../../models/training/sector.model';

@Component({
  selector: 'app-teachers',
  imports: [
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  styleUrl: './teachers.scss',
  templateUrl: './teachers.html',
})
export class Teachers implements OnInit {
  private readonly userService = inject(UserService);
  private readonly sectorService = inject(SectorService);
  private readonly dialog = inject(MatDialog);

  private detailsDialogRef?: MatDialogRef<unknown>;

  readonly teachers = signal<Teacher[]>([]);
  readonly sectors = signal<Sector[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly search = signal('');

  readonly viewingTeacher = signal<Teacher | null>(null);

  readonly filteredTeachers = computed(() => {
    const search = this.normalizeSearch(this.search());

    return this.teachers().filter((teacher) =>
      search
        ? this.normalizeSearch(
          `${teacher.firstName} ${teacher.lastName} ${teacher.email}`
        ).includes(search)
        : true
    );
  });

  ngOnInit(): void {
    this.userService.findAll('TEACHER').subscribe({
      next: (teachers) => {
        this.teachers.set(teachers as Teacher[]);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les formateurs.');
        this.loading.set(false);
      },
    });

    this.sectorService.findAll().subscribe({
      next: (sectors) => this.sectors.set(sectors),
    });
  }

  sectorName(sectorId: number): string {
    return this.sectors().find((sector) => sector.id === sectorId)?.name ?? '—';
  }

  openDetails(teacher: Teacher, template: TemplateRef<unknown>): void {
    this.viewingTeacher.set(teacher);

    this.detailsDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  closeDetails(): void {
    this.detailsDialogRef?.close();
  }

  private normalizeSearch(value: string): string {
    return value
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[̀-ͯ]/g, '');
  }
}
