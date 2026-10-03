import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ExpenseRequestService } from '../../../core/services/expense-request.service';
import { Category, CategoryService } from '../../../core/services/category.service';
import { Department, DepartmentService } from '../../../core/services/department.service';
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
export class RequestFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private requestService = inject(ExpenseRequestService);
  private categoryService = inject(CategoryService);
  private departmentService = inject(DepartmentService);
  private authService = inject(AuthService);
  private dialogRef = inject(MatDialogRef<RequestFormComponent>);
  private snackBar = inject(MatSnackBar);

  loading = signal(false);
  categories = signal<Category[]>([]);
  departments = signal<Department[]>([]);
  currencies = ['USD', 'PEN', 'EUR'];

  form = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(3)]],
    description: [''],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    currency: ['USD', Validators.required],
    categoryId: [null as number | null, Validators.required],
    departmentId: [null as number | null, Validators.required]
  });

  ngOnInit(): void {
    this.categoryService.getAll().subscribe({
      next: data => this.categories.set(data),
      error: err => console.error(err)
    });

    this.departmentService.getAll().subscribe({
      next: data => {
        this.departments.set(data);
        const deptId = this.authService.getDepartmentId();
        if (deptId) {
          this.form.patchValue({ departmentId: deptId });
        }
      },
      error: err => console.error(err)
    });
  }

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