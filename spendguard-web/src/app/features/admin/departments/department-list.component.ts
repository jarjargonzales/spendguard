import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Department, DepartmentService } from '../../../core/services/department.service';

@Component({
  selector: 'app-department-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule
  ],
  template: `
    <div class="header">
      <h2>Departamentos</h2>
    </div>

    <mat-card class="form-card">
      <mat-card-content>
        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="form-row">
          <mat-form-field appearance="outline">
            <mat-label>Nombre</mat-label>
            <input matInput formControlName="name" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Descripción</mat-label>
            <input matInput formControlName="description" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Presupuesto mensual</mat-label>
            <input matInput type="number" formControlName="monthlyBudget" />
          </mat-form-field>

          <button mat-raised-button color="primary" type="submit" [disabled]="form.invalid">Crear</button>
        </form>
      </mat-card-content>
    </mat-card>

    <mat-card>
      <mat-card-content>
        <table mat-table [dataSource]="departments()" class="full-width">
          <ng-container matColumnDef="name">
            <th mat-header-cell *matHeaderCellDef>Nombre</th>
            <td mat-cell *matCellDef="let d">{{ d.name }}</td>
          </ng-container>

          <ng-container matColumnDef="description">
            <th mat-header-cell *matHeaderCellDef>Descripción</th>
            <td mat-cell *matCellDef="let d">{{ d.description }}</td>
          </ng-container>

          <ng-container matColumnDef="monthlyBudget">
            <th mat-header-cell *matHeaderCellDef>Presupuesto</th>
            <td mat-cell *matCellDef="let d">{{ d.monthlyBudget | currency }}</td>
          </ng-container>

          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef></th>
            <td mat-cell *matCellDef="let d">
              <button mat-icon-button color="warn" (click)="delete(d.departmentId)">
                <mat-icon>delete</mat-icon>
              </button>
            </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="columns"></tr>
          <tr mat-row *matRowDef="let row; columns: columns"></tr>
        </table>
      </mat-card-content>
    </mat-card>
  `,
  styles: [`
    .header { margin-bottom: 16px; }
    .form-card { margin-bottom: 16px; }
    .form-row {
      display: flex;
      gap: 16px;
      align-items: center;
      flex-wrap: wrap;
    }
    .full-width { width: 100%; }
  `]
})
export class DepartmentListComponent implements OnInit {
  private departmentService = inject(DepartmentService);
  private snackBar = inject(MatSnackBar);
  private fb = inject(FormBuilder);

  departments = signal<Department[]>([]);
  columns = ['name', 'description', 'monthlyBudget', 'actions'];

  form = this.fb.group({
    name: ['', Validators.required],
    description: [''],
    monthlyBudget: [0, [Validators.required, Validators.min(0)]]
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.departmentService.getAll().subscribe({
      next: data => this.departments.set(data),
      error: err => {
        console.error(err);
        this.snackBar.open('Error al cargar departamentos', 'Cerrar', { duration: 3000 });
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.departmentService.create(this.form.getRawValue() as any).subscribe({
      next: () => {
        this.snackBar.open('Departamento creado', 'Cerrar', { duration: 3000 });
        this.form.reset();
        this.load();
      },
      error: err => {
        console.error(err);
        this.snackBar.open(err.error?.message || 'Error al crear', 'Cerrar', { duration: 3000 });
      }
    });
  }

  delete(id: number): void {
    if (!confirm('¿Desactivar este departamento?')) return;
    this.departmentService.delete(id).subscribe({
      next: () => {
        this.snackBar.open('Departamento desactivado', 'Cerrar', { duration: 3000 });
        this.load();
      },
      error: err => {
        console.error(err);
        this.snackBar.open('Error al eliminar', 'Cerrar', { duration: 3000 });
      }
    });
  }
}