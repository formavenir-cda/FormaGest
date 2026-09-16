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
import type { Administrator, UserPayload } from '../../../models/users/user.model';
import { ALL_USER_ROLES, USER_ROLE_LABELS } from '../../../models/users/user-role';
import type { UserRole } from '../../../models/users/user-role';
import { SectorService } from '../../../services/training/sector.service';
import type { Sector } from '../../../models/training/sector.model';

@Component({
  selector: 'app-administrators',
  imports: [
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTooltip,
  ],
  styleUrl: './administrators.scss',
  templateUrl: './administrators.html',
})
export class Administrators implements OnInit {
  private readonly userService = inject(UserService);
  private readonly sectorService = inject(SectorService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  private createDialogRef?: MatDialogRef<unknown>;
  private deleteDialogRef?: MatDialogRef<unknown>;
  private changeRoleDialogRef?: MatDialogRef<unknown>;

  readonly roleLabels = USER_ROLE_LABELS;

  readonly administrators = signal<Administrator[]>([]);
  readonly sectors = signal<Sector[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly search = signal('');

  readonly newLastName = signal('');
  readonly newFirstName = signal('');
  readonly newEmail = signal('');
  readonly newPassword = signal('');
  readonly createError = signal('');
  readonly creating = signal(false);
  readonly editingAdministrator = signal<Administrator | null>(null);

  readonly administratorToDelete = signal<Administrator | null>(null);
  readonly deleting = signal(false);
  readonly deleteError = signal('');

  readonly changingRoleAdministrator = signal<Administrator | null>(null);
  readonly newRole = signal<UserRole | null>(null);
  readonly newRoleSectorId = signal<number | null>(null);
  readonly newRoleBirthDate = signal('');
  readonly changeRoleError = signal('');
  readonly changingRole = signal(false);

  readonly availableRoles = computed(() =>
    ALL_USER_ROLES.filter((role) => role !== this.changingRoleAdministrator()?.role)
  );

  readonly filteredAdministrators = computed(() => {
    const search = this.normalizeSearch(this.search());

    return this.administrators().filter((administrator) =>
      search
        ? this.normalizeSearch(
            `${administrator.firstName} ${administrator.lastName} ${administrator.email}`
          ).includes(search)
        : true
    );
  });

  ngOnInit(): void {
    this.userService.findAll('ADMINISTRATOR').subscribe({
      next: (administrators) => {
        this.administrators.set(administrators as Administrator[]);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les administrateurs.');
        this.loading.set(false);
      },
    });

    this.sectorService.findAll().subscribe({
      next: (sectors) => this.sectors.set(sectors),
    });
  }

  openCreateForm(template: TemplateRef<unknown>): void {
    this.editingAdministrator.set(null);
    this.newLastName.set('');
    this.newFirstName.set('');
    this.newEmail.set('');
    this.newPassword.set('');
    this.createError.set('');

    this.createDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  cancelCreate(): void {
    this.createDialogRef?.close();
  }

  editAdministrator(administrator: Administrator, template: TemplateRef<unknown>): void {
    this.editingAdministrator.set(administrator);
    this.newLastName.set(administrator.lastName);
    this.newFirstName.set(administrator.firstName);
    this.newEmail.set(administrator.email);
    this.newPassword.set('');
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
    const administratorToEdit = this.editingAdministrator();

    if (!lastName || !firstName || !email) {
      this.createError.set('Le nom, le prénom et l’email sont obligatoires.');
      return;
    }

    if (!administratorToEdit && !password) {
      this.createError.set('Le mot de passe est obligatoire à la création.');
      return;
    }

    const payload: UserPayload = {
      email,
      lastName,
      firstName,
      active: administratorToEdit?.active ?? true,
      role: 'ADMINISTRATOR',
      ...(password ? { password } : {}),
    };

    this.createError.set('');
    this.creating.set(true);

    if (this.createDialogRef) {
      this.createDialogRef.disableClose = true;
    }

    const request = administratorToEdit
      ? this.userService.update(administratorToEdit.id, payload)
      : this.userService.create(payload);

    request.subscribe({
      next: (administrator) => {
        this.administrators.update((list) =>
          administratorToEdit
            ? list.map((item) =>
                item.id === administrator.id ? (administrator as Administrator) : item
              )
            : [...list, administrator as Administrator]
        );

        this.creating.set(false);
        this.createDialogRef?.close();
      },
      error: (err) => {
        this.creating.set(false);

        this.createError.set(
          err.status === 409
            ? 'Un administrateur utilisant cet email existe déjà.'
            : 'Impossible d’enregistrer l’administrateur. Veuillez réessayer.'
        );

        if (this.createDialogRef) {
          this.createDialogRef.disableClose = false;
        }
      },
    });
  }

  deleteAdministrator(administrator: Administrator, template: TemplateRef<unknown>): void {
    this.administratorToDelete.set(administrator);
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
    const administrator = this.administratorToDelete();

    if (!administrator || this.deleting()) return;

    this.deleting.set(true);
    this.deleteError.set('');

    if (this.deleteDialogRef) {
      this.deleteDialogRef.disableClose = true;
    }

    this.userService.delete(administrator.id).subscribe({
      next: () => {
        this.administrators.update((list) =>
          list.filter((item) => item.id !== administrator.id)
        );

        this.deleting.set(false);
        this.deleteDialogRef?.close();
        this.administratorToDelete.set(null);
      },
      error: (err) => {
        this.deleting.set(false);

        this.deleteError.set(
          typeof err.error === 'string' && err.error.trim()
            ? err.error
            : 'Impossible de supprimer l’administrateur. Veuillez réessayer.'
        );

        if (this.deleteDialogRef) {
          this.deleteDialogRef.disableClose = false;
        }
      },
    });
  }

  toggleActive(administrator: Administrator): void {
    this.userService.toggleActive(administrator.id).subscribe({
      next: (updated) => {
        this.administrators.update((list) =>
          list.map((item) =>
            item.id === updated.id ? (updated as Administrator) : item
          )
        );
      },
      error: (err) => {
        this.snackBar.open(
          typeof err.error === 'string' && err.error.trim()
            ? err.error
            : 'Impossible de modifier le statut de cet administrateur.',
          'Fermer',
          { duration: 5000 }
        );
      },
    });
  }

  openChangeRoleForm(administrator: Administrator, template: TemplateRef<unknown>): void {
    this.changingRoleAdministrator.set(administrator);
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

    const administrator = this.changingRoleAdministrator();
    const role = this.newRole();

    if (!administrator || !role) {
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
      email: administrator.email,
      lastName: administrator.lastName,
      firstName: administrator.firstName,
      active: administrator.active,
      role,
      ...(role === 'TEACHER' ? { sectorId: this.newRoleSectorId()! } : {}),
      ...(role === 'STUDENT' ? { birthDate: this.newRoleBirthDate() } : {}),
    };

    this.changeRoleError.set('');
    this.changingRole.set(true);

    if (this.changeRoleDialogRef) {
      this.changeRoleDialogRef.disableClose = true;
    }

    this.userService.changeRole(administrator.id, payload).subscribe({
      next: () => {
        this.administrators.update((list) =>
          list.filter((item) => item.id !== administrator.id)
        );

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
