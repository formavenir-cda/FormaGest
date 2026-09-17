import { Component, computed, inject, OnInit, signal, TemplateRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltip } from '@angular/material/tooltip';
import { DatePipe } from '@angular/common';

import { UserService } from '../../../services/users/user.service';
import type { Student, UserPayload } from '../../../models/users/user.model';
import { ALL_USER_ROLES, USER_ROLE_LABELS } from '../../../models/users/user-role';
import type { UserRole } from '../../../models/users/user-role';
import { SectorService } from '../../../services/training/sector.service';
import type { Sector } from '../../../models/training/sector.model';

@Component({
  selector: 'app-students',
  imports: [
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTooltip,
    DatePipe
  ],
  styleUrl: './students.scss',
  templateUrl: './students.html',
})
export class Students implements OnInit {
  private readonly userService = inject(UserService);
  private readonly sectorService = inject(SectorService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  private createDialogRef?: MatDialogRef<unknown>;
  private deleteDialogRef?: MatDialogRef<unknown>;
  private changeRoleDialogRef?: MatDialogRef<unknown>;

  readonly roleLabels = USER_ROLE_LABELS;

  readonly students = signal<Student[]>([]);
  readonly sectors = signal<Sector[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly search = signal('');

  readonly newLastName = signal('');
  readonly newFirstName = signal('');
  readonly newEmail = signal('');
  readonly newPassword = signal('');
  readonly newBirthDate = signal('');
  readonly createError = signal('');
  readonly creating = signal(false);
  readonly editingStudent = signal<Student | null>(null);

  readonly studentToDelete = signal<Student | null>(null);
  readonly deleting = signal(false);
  readonly deleteError = signal('');

  readonly changingRoleStudent = signal<Student | null>(null);
  readonly newRole = signal<UserRole | null>(null);
  readonly newRoleSectorId = signal<number | null>(null);
  readonly newRoleBirthDate = signal('');
  readonly changeRoleError = signal('');
  readonly changingRole = signal(false);

  readonly availableRoles = computed(() =>
    ALL_USER_ROLES.filter((role) => role !== this.changingRoleStudent()?.role)
  );

  readonly filteredStudents = computed(() => {
    const search = this.normalizeSearch(this.search());

    return this.students().filter((student) =>
      search
        ? this.normalizeSearch(
            `${student.firstName} ${student.lastName} ${student.email}`
          ).includes(search)
        : true
    );
  });

  ngOnInit(): void {
    this.userService.findAll('STUDENT').subscribe({
      next: (students) => {
        this.students.set(students as Student[]);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les élèves.');
        this.loading.set(false);
      },
    });

    this.sectorService.findAll().subscribe({
      next: (sectors) => this.sectors.set(sectors),
    });
  }

  openCreateForm(template: TemplateRef<unknown>): void {
    this.editingStudent.set(null);
    this.newLastName.set('');
    this.newFirstName.set('');
    this.newEmail.set('');
    this.newPassword.set('');
    this.newBirthDate.set('');
    this.createError.set('');

    this.createDialogRef = this.dialog.open(template, {
      width: '480px',
      maxWidth: '95vw',
    });
  }

  cancelCreate(): void {
    this.createDialogRef?.close();
  }

  editStudent(student: Student, template: TemplateRef<unknown>): void {
    this.editingStudent.set(student);
    this.newLastName.set(student.lastName);
    this.newFirstName.set(student.firstName);
    this.newEmail.set(student.email);
    this.newPassword.set('');
    this.newBirthDate.set(student.birthDate);
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
    const birthDate = this.newBirthDate();
    const studentToEdit = this.editingStudent();

    if (!lastName || !firstName || !email) {
      this.createError.set('Le nom, le prénom et l’email sont obligatoires.');
      return;
    }

    if (!birthDate) {
      this.createError.set('La date de naissance est obligatoire.');
      return;
    }

    if (!studentToEdit && !password) {
      this.createError.set('Le mot de passe est obligatoire à la création.');
      return;
    }

    const payload: UserPayload = {
      email,
      lastName,
      firstName,
      active: studentToEdit?.active ?? true,
      role: 'STUDENT',
      birthDate,
      ...(password ? { password } : {}),
    };

    this.createError.set('');
    this.creating.set(true);

    if (this.createDialogRef) {
      this.createDialogRef.disableClose = true;
    }

    const request = studentToEdit
      ? this.userService.update(studentToEdit.id, payload)
      : this.userService.create(payload);

    request.subscribe({
      next: (student) => {
        this.students.update((list) =>
          studentToEdit
            ? list.map((item) => (item.id === student.id ? (student as Student) : item))
            : [...list, student as Student]
        );

        this.creating.set(false);
        this.createDialogRef?.close();
      },
      error: (err) => {
        this.creating.set(false);

        this.createError.set(
          err.status === 409
            ? 'Un élève utilisant cet email existe déjà.'
            : 'Impossible d’enregistrer l’élève. Veuillez réessayer.'
        );

        if (this.createDialogRef) {
          this.createDialogRef.disableClose = false;
        }
      },
    });
  }

  deleteStudent(student: Student, template: TemplateRef<unknown>): void {
    this.studentToDelete.set(student);
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
    const student = this.studentToDelete();

    if (!student || this.deleting()) return;

    this.deleting.set(true);
    this.deleteError.set('');

    if (this.deleteDialogRef) {
      this.deleteDialogRef.disableClose = true;
    }

    this.userService.delete(student.id).subscribe({
      next: () => {
        this.students.update((list) =>
          list.filter((item) => item.id !== student.id)
        );

        this.deleting.set(false);
        this.deleteDialogRef?.close();
        this.studentToDelete.set(null);
      },
      error: (err) => {
        this.deleting.set(false);

        this.deleteError.set(
          typeof err.error === 'string' && err.error.trim()
            ? err.error
            : 'Impossible de supprimer l’élève. Veuillez réessayer.'
        );

        if (this.deleteDialogRef) {
          this.deleteDialogRef.disableClose = false;
        }
      },
    });
  }

  toggleActive(student: Student): void {
    this.userService.toggleActive(student.id).subscribe({
      next: (updated) => {
        this.students.update((list) =>
          list.map((item) => (item.id === updated.id ? (updated as Student) : item))
        );
      },
      error: (err) => {
        this.snackBar.open(
          typeof err.error === 'string' && err.error.trim()
            ? err.error
            : 'Impossible de modifier le statut de cet élève.',
          'Fermer',
          { duration: 5000 }
        );
      },
    });
  }

  openChangeRoleForm(student: Student, template: TemplateRef<unknown>): void {
    this.changingRoleStudent.set(student);
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

    const student = this.changingRoleStudent();
    const role = this.newRole();

    if (!student || !role) {
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
      email: student.email,
      lastName: student.lastName,
      firstName: student.firstName,
      active: student.active,
      role,
      ...(role === 'TEACHER' ? { sectorId: this.newRoleSectorId()! } : {}),
      ...(role === 'STUDENT' ? { birthDate: this.newRoleBirthDate() } : {}),
    };

    this.changeRoleError.set('');
    this.changingRole.set(true);

    if (this.changeRoleDialogRef) {
      this.changeRoleDialogRef.disableClose = true;
    }

    this.userService.changeRole(student.id, payload).subscribe({
      next: () => {
        this.students.update((list) => list.filter((item) => item.id !== student.id));

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
