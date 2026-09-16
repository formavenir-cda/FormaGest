import { Component, computed, inject, OnInit, signal, TemplateRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltip } from '@angular/material/tooltip';

import { UserService } from '../../../services/users/user.service';
import type { Teacher, UserPayload } from '../../../models/users/user.model';
import { ALL_USER_ROLES, USER_ROLE_LABELS } from '../../../models/users/user-role';
import type { UserRole } from '../../../models/users/user-role';
import { SectorService } from '../../../services/training/sector.service';
import type { Sector } from '../../../models/training/sector.model';

@Component({
  selector: 'app-teachers',
  imports: [
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTooltip,
  ],
  styleUrl: './teachers.scss',
  templateUrl: './teachers.html',
})
export class Teachers implements OnInit {
  private readonly userService = inject(UserService);
  private readonly sectorService = inject(SectorService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  private createDialogRef?: MatDialogRef<unknown>;
  private deleteDialogRef?: MatDialogRef<unknown>;
  private changeRoleDialogRef?: MatDialogRef<unknown>;

  readonly roleLabels = USER_ROLE_LABELS;

  readonly teachers = signal<Teacher[]>([]);
  readonly sectors = signal<Sector[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly search = signal('');

  readonly newLastName = signal('');
  readonly newFirstName = signal('');
  readonly newEmail = signal('');
  readonly newPassword = signal('');
  readonly newSectorId = signal<number | null>(null);
  readonly createError = signal('');
  readonly creating = signal(false);
  readonly editingTeacher = signal<Teacher | null>(null);

  readonly teacherToDelete = signal<Teacher | null>(null);
  readonly deleting = signal(false);
  readonly deleteError = signal('');

  readonly changingRoleTeacher = signal<Teacher | null>(null);
  readonly newRole = signal<UserRole | null>(null);
  readonly newRoleSectorId = signal<number | null>(null);
  readonly newRoleBirthDate = signal('');
  readonly changeRoleError = signal('');
  readonly changingRole = signal(false);

  readonly availableRoles = computed(() =>
    ALL_USER_ROLES.filter((role) => role !== this.changingRoleTeacher()?.role)
  );

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

  openCreateForm(template: TemplateRef<unknown>): void {
    this.editingTeacher.set(null);
    this.newLastName.set('');
    this.newFirstName.set('');
    this.newEmail.set('');
    this.newPassword.set('');
    this.newSectorId.set(null);
    this.createError.set('');

    this.createDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  cancelCreate(): void {
    this.createDialogRef?.close();
  }

  editTeacher(teacher: Teacher, template: TemplateRef<unknown>): void {
    this.editingTeacher.set(teacher);
    this.newLastName.set(teacher.lastName);
    this.newFirstName.set(teacher.firstName);
    this.newEmail.set(teacher.email);
    this.newPassword.set('');
    this.newSectorId.set(teacher.sectorId);
    this.createError.set('');

    this.createDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  submitForm(): void {
    if (this.creating()) {
      return;
    }

    const lastName = this.newLastName().trim();
    const firstName = this.newFirstName().trim();
    const email = this.newEmail().trim();
    const password = this.newPassword().trim();
    const sectorId = this.newSectorId();
    const teacherToEdit = this.editingTeacher();

    if (!lastName || !firstName || !email) {
      this.createError.set('Le nom, le prénom et l’email sont obligatoires.');
      return;
    }

    if (!sectorId) {
      this.createError.set('La filière est obligatoire.');
      return;
    }

    if (!teacherToEdit && !password) {
      this.createError.set('Le mot de passe est obligatoire à la création.');
      return;
    }

    const payload: UserPayload = {
      email,
      lastName,
      firstName,
      active: teacherToEdit?.active ?? true,
      role: 'TEACHER',
      sectorId,
      ...(password ? { password } : {}),
    };

    this.createError.set('');
    this.creating.set(true);

    if (this.createDialogRef) {
      this.createDialogRef.disableClose = true;
    }

    const request = teacherToEdit
      ? this.userService.update(teacherToEdit.id, payload)
      : this.userService.create(payload);

    request.subscribe({
      next: (teacher) => {
        this.teachers.update((list) =>
          teacherToEdit
            ? list.map((item) => (item.id === teacher.id ? (teacher as Teacher) : item))
            : [...list, teacher as Teacher]
        );

        this.creating.set(false);
        this.createDialogRef?.close();
      },
      error: (err) => {
        this.creating.set(false);

        this.createError.set(
          err.status === 409
            ? 'Un formateur utilisant cet email existe déjà.'
            : 'Impossible d’enregistrer le formateur. Veuillez réessayer.'
        );

        if (this.createDialogRef) {
          this.createDialogRef.disableClose = false;
        }
      },
    });
  }

  deleteTeacher(teacher: Teacher, template: TemplateRef<unknown>): void {
    this.teacherToDelete.set(teacher);
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
    const teacher = this.teacherToDelete();

    if (!teacher || this.deleting()) return;

    this.deleting.set(true);
    this.deleteError.set('');

    if (this.deleteDialogRef) {
      this.deleteDialogRef.disableClose = true;
    }

    this.userService.delete(teacher.id).subscribe({
      next: () => {
        this.teachers.update((list) =>
          list.filter((item) => item.id !== teacher.id)
        );

        this.deleting.set(false);
        this.deleteDialogRef?.close();
        this.teacherToDelete.set(null);
      },
      error: (err) => {
        this.deleting.set(false);

        this.deleteError.set(
          typeof err.error === 'string' && err.error.trim()
            ? err.error
            : 'Impossible de supprimer le formateur. Veuillez réessayer.'
        );

        if (this.deleteDialogRef) {
          this.deleteDialogRef.disableClose = false;
        }
      },
    });
  }

  toggleActive(teacher: Teacher): void {
    this.userService.toggleActive(teacher.id).subscribe({
      next: (updated) => {
        this.teachers.update((list) =>
          list.map((item) => (item.id === updated.id ? (updated as Teacher) : item))
        );
      },
      error: (err) => {
        this.snackBar.open(
          typeof err.error === 'string' && err.error.trim()
            ? err.error
            : 'Impossible de modifier le statut de ce formateur.',
          'Fermer',
          { duration: 5000 }
        );
      },
    });
  }

  openChangeRoleForm(teacher: Teacher, template: TemplateRef<unknown>): void {
    this.changingRoleTeacher.set(teacher);
    this.newRole.set(null);
    this.newRoleSectorId.set(null);
    this.newRoleBirthDate.set('');
    this.changeRoleError.set('');

    this.changeRoleDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  cancelChangeRole(): void {
    this.changeRoleDialogRef?.close();
  }

  submitChangeRole(): void {
    if (this.changingRole()) {
      return;
    }

    const teacher = this.changingRoleTeacher();
    const role = this.newRole();

    if (!teacher || !role) {
      this.changeRoleError.set('Choisissez un rôle.');
      return;
    }

    if (role === 'TEACHER' && !this.newRoleSectorId()) {
      this.changeRoleError.set('La filière est obligatoire pour un formateur.');
      return;
    }

    if (role === 'STUDENT' && !this.newRoleBirthDate()) {
      this.changeRoleError.set('La date de naissance est obligatoire pour un élève.');
      return;
    }

    const payload: UserPayload = {
      email: teacher.email,
      lastName: teacher.lastName,
      firstName: teacher.firstName,
      active: teacher.active,
      role,
      ...(role === 'TEACHER' ? { sectorId: this.newRoleSectorId()! } : {}),
      ...(role === 'STUDENT' ? { birthDate: this.newRoleBirthDate() } : {}),
    };

    this.changeRoleError.set('');
    this.changingRole.set(true);

    if (this.changeRoleDialogRef) {
      this.changeRoleDialogRef.disableClose = true;
    }

    this.userService.changeRole(teacher.id, payload).subscribe({
      next: () => {
        this.teachers.update((list) => list.filter((item) => item.id !== teacher.id));

        this.changingRole.set(false);
        this.changeRoleDialogRef?.close();
      },
      error: (err) => {
        this.changingRole.set(false);

        this.changeRoleError.set(
          typeof err.error === 'string' && err.error.trim()
            ? err.error
            : 'Impossible de changer le rôle. Veuillez réessayer.'
        );

        if (this.changeRoleDialogRef) {
          this.changeRoleDialogRef.disableClose = false;
        }
      },
    });
  }

  private normalizeSearch(value: string): string {
    return value
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[̀-ͯ]/g, '');
  }
}
