import { Component, computed, inject, OnInit, signal, TemplateRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { DatePipe } from '@angular/common';

import { UserService } from '../../services/users/user.service';
import type { Student } from '../../models/users/user.model';

@Component({
  selector: 'app-students',
  imports: [
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    DatePipe
  ],
  styleUrl: './students.scss',
  templateUrl: './students.html',
})
export class Students implements OnInit {
  private readonly userService = inject(UserService);
  private readonly dialog = inject(MatDialog);

  private detailsDialogRef?: MatDialogRef<unknown>;

  readonly students = signal<Student[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly search = signal('');

  readonly viewingStudent = signal<Student | null>(null);

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
  }

  openDetails(student: Student, template: TemplateRef<unknown>): void {
    this.viewingStudent.set(student);

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
