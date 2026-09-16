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
import type { AdministrativeManager, UserPayload } from '../../../models/users/user.model';
import { ALL_USER_ROLES, USER_ROLE_LABELS } from '../../../models/users/user-role';
import type { UserRole } from '../../../models/users/user-role';
import { SectorService } from '../../../services/training/sector.service';
import type { Sector } from '../../../models/training/sector.model';

@Component({
  selector: 'app-administrative-managers',
  imports: [
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTooltip,
  ],
  styleUrl: './administrative-managers.scss',
  templateUrl: './administrative-managers.html',
})
export class AdministrativeManagers implements OnInit {
  private readonly userService = inject(UserService);
  private readonly sectorService = inject(SectorService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  private createDialogRef?: MatDialogRef<unknown>;
  private deleteDialogRef?: MatDialogRef<unknown>;
  private changeRoleDialogRef?: MatDialogRef<unknown>;

  readonly roleLabels = USER_ROLE_LABELS;

  readonly managers = signal<AdministrativeManager[]>([]);
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
  readonly editingManager = signal<AdministrativeManager | null>(null);

  readonly managerToDelete = signal<AdministrativeManager | null>(null);
  readonly deleting = signal(false);
  readonly deleteError = signal('');

  readonly changingRoleManager = signal<AdministrativeManager | null>(null);
  readonly newRole = signal<UserRole | null>(null);
  readonly newRoleSectorId = signal<number | null>(null);
  readonly newRoleBirthDate = signal('');
  readonly changeRoleError = signal('');
  readonly changingRole = signal(false);

  readonly availableRoles = computed(() =>
    ALL_USER_ROLES.filter((role) => role !== this.changingRoleManager()?.role)
  );

  readonly filteredManagers = computed(() => {
    const search = this.normalizeSearch(this.search());

    return this.managers().filter((manager) =>
      search
        ? this.normalizeSearch(
            `${manager.firstName} ${manager.lastName} ${manager.email}`
          ).includes(search)
        : true
    );
  });

  ngOnInit(): void {
    this.userService.findAll('ADMINISTRATIVE_MANAGER').subscribe({
      next: (managers) => {
        this.managers.set(managers as AdministrativeManager[]);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les référentes administratives.');
        this.loading.set(false);
      },
    });

    this.sectorService.findAll().subscribe({
      next: (sectors) => this.sectors.set(sectors),
    });
  }

  openCreateForm(template: TemplateRef<unknown>): void {
    this.editingManager.set(null);
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

  editManager(manager: AdministrativeManager, template: TemplateRef<unknown>): void {
    this.editingManager.set(manager);
    this.newLastName.set(manager.lastName);
    this.newFirstName.set(manager.firstName);
    this.newEmail.set(manager.email);
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
    const managerToEdit = this.editingManager();

    if (!lastName || !firstName || !email) {
      this.createError.set('Le nom, le prénom et l’email sont obligatoires.');
      return;
    }

    if (!managerToEdit && !password) {
      this.createError.set('Le mot de passe est obligatoire à la création.');
      return;
    }

    const payload: UserPayload = {
      email,
      lastName,
      firstName,
      active: managerToEdit?.active ?? true,
      role: 'ADMINISTRATIVE_MANAGER',
      ...(password ? { password } : {}),
    };

    this.createError.set('');
    this.creating.set(true);

    if (this.createDialogRef) {
      this.createDialogRef.disableClose = true;
    }

    const request = managerToEdit
      ? this.userService.update(managerToEdit.id, payload)
      : this.userService.create(payload);

    request.subscribe({
      next: (manager) => {
        this.managers.update((list) =>
          managerToEdit
            ? list.map((item) =>
                item.id === manager.id ? (manager as AdministrativeManager) : item
              )
            : [...list, manager as AdministrativeManager]
        );

        this.creating.set(false);
        this.createDialogRef?.close();
      },
      error: (err) => {
        this.creating.set(false);

        this.createError.set(
          err.status === 409
            ? 'Une référente utilisant cet email existe déjà.'
            : 'Impossible d’enregistrer la référente. Veuillez réessayer.'
        );

        if (this.createDialogRef) {
          this.createDialogRef.disableClose = false;
        }
      },
    });
  }

  deleteManager(manager: AdministrativeManager, template: TemplateRef<unknown>): void {
    this.managerToDelete.set(manager);
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
    const manager = this.managerToDelete();

    if (!manager || this.deleting()) return;

    this.deleting.set(true);
    this.deleteError.set('');

    if (this.deleteDialogRef) {
      this.deleteDialogRef.disableClose = true;
    }

    this.userService.delete(manager.id).subscribe({
      next: () => {
        this.managers.update((list) =>
          list.filter((item) => item.id !== manager.id)
        );

        this.deleting.set(false);
        this.deleteDialogRef?.close();
        this.managerToDelete.set(null);
      },
      error: (err) => {
        this.deleting.set(false);

        this.deleteError.set(
          typeof err.error === 'string' && err.error.trim()
            ? err.error
            : 'Impossible de supprimer la référente. Veuillez réessayer.'
        );

        if (this.deleteDialogRef) {
          this.deleteDialogRef.disableClose = false;
        }
      },
    });
  }

  toggleActive(manager: AdministrativeManager): void {
    this.userService.toggleActive(manager.id).subscribe({
      next: (updated) => {
        this.managers.update((list) =>
          list.map((item) =>
            item.id === updated.id ? (updated as AdministrativeManager) : item
          )
        );
      },
      error: (err) => {
        this.snackBar.open(
          typeof err.error === 'string' && err.error.trim()
            ? err.error
            : 'Impossible de modifier le statut de cette référente.',
          'Fermer',
          { duration: 5000 }
        );
      },
    });
  }

  openChangeRoleForm(manager: AdministrativeManager, template: TemplateRef<unknown>): void {
    this.changingRoleManager.set(manager);
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

    const manager = this.changingRoleManager();
    const role = this.newRole();

    if (!manager || !role) {
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
      email: manager.email,
      lastName: manager.lastName,
      firstName: manager.firstName,
      active: manager.active,
      role,
      ...(role === 'TEACHER' ? { sectorId: this.newRoleSectorId()! } : {}),
      ...(role === 'STUDENT' ? { birthDate: this.newRoleBirthDate() } : {}),
    };

    this.changeRoleError.set('');
    this.changingRole.set(true);

    if (this.changeRoleDialogRef) {
      this.changeRoleDialogRef.disableClose = true;
    }

    this.userService.changeRole(manager.id, payload).subscribe({
      next: () => {
        this.managers.update((list) => list.filter((item) => item.id !== manager.id));

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
