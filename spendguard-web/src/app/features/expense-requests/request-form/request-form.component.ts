import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ExpenseRequestService } from '../../../core/services/expense-request.service'; 
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-request-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatSnackBarModule
  ],
  templateUrl: './request-form.component.html',
  styleUrl: './request-form.component.scss'
})
export class RequestFormComponent {
  private fb = inject(FormBuilder);
  private requestService = inject(ExpenseRequestService);
  private authService = inject(AuthService);
  private dialogRef = inject(MatDialogRef<RequestFormComponent>);
  private snackBar = inject(MatSnackBar);

  loading = signal(false);

  // En una versión completa, estos vendrían de endpoints /categories y /departments.
  // Por ahora hardcodeamos un par de ejemplo:
  categories = [
    { id: 1, name: 'Capacitación' },
    { id: 2, name: 'Viajes' },
    { id: 3, name: 'Software' }
  ];
  departments = [
    { id: 1, name: 'Administración' }
  ];

  form = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(3)]],
    description: [''],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    currency: ['USD', Validators.required],
    categoryId: [null as number | null, Validators.required],
    departmentId: [1, Validators.required]
  });

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);

    this.requestService.create(this.form.getRawValue() as any).subscribe({
      next: () => {
        this.loading.set(false);
        this.snackBar.open('Solicitud creada', 'Cerrar', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: err => {
        this.loading.set(false);
        console.error(err);
        this.snackBar.open(err.error?.message || 'Error al crear', 'Cerrar', { duration: 3000 });
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}