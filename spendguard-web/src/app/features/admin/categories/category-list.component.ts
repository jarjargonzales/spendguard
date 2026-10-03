import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Category, CategoryService } from '../../../core/services/category.service';

@Component({
  selector: 'app-category-list',
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
    MatSlideToggleModule,
    MatSnackBarModule
  ],
  template: `
    <div class="header">
      <h2>Categorías de gasto</h2>
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

          <mat-slide-toggle formControlName="requiresApproval">Requiere aprobación</mat-slide-toggle>

          <button mat-raised-button color="primary" type="submit" [disabled]="form.invalid">Crear</button>
        </form>
      </mat-card-content>
    </mat-card>

    <mat-card>
      <mat-card-content>
        <table mat-table [dataSource]="categories()" class="full-width">
          <ng-container matColumnDef="name">
            <th mat-header-cell *matHeaderCellDef>Nombre</th>
            <td mat-cell *matCellDef="let c">{{ c.name }}</td>
          </ng-container>

          <ng-container matColumnDef="description">
            <th mat-header-cell *matHeaderCellDef>Descripción</th>
            <td mat-cell *matCellDef="let c">{{ c.description }}</td>
          </ng-container>

          <ng-container matColumnDef="requiresApproval">
            <th mat-header-cell *matHeaderCellDef>Requiere aprobación</th>
            <td mat-cell *matCellDef="let c">{{ c.requiresApproval ? 'Sí' : 'No' }}</td>
          </ng-container>

          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef></th>
            <td mat-cell *matCellDef="let c">
              <button mat-icon-button color="warn" (click)="delete(c.categoryId)">
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
export class CategoryListComponent implements OnInit {
  private categoryService = inject(CategoryService);
  private snackBar = inject(MatSnackBar);
  private fb = inject(FormBuilder);

  categories = signal<Category[]>([]);
  columns = ['name', 'description', 'requiresApproval', 'actions'];

  form = this.fb.group({
    name: ['', Validators.required],
    description: [''],
    requiresApproval: [true]
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.categoryService.getAll().subscribe({
      next: data => this.categories.set(data),
      error: err => {
        console.error(err);
        this.snackBar.open('Error al cargar categorías', 'Cerrar', { duration: 3000 });
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.categoryService.create(this.form.getRawValue() as any).subscribe({
      next: () => {
        this.snackBar.open('Categoría creada', 'Cerrar', { duration: 3000 });
        this.form.reset({ requiresApproval: true });
        this.load();
      },
      error: err => {
        console.error(err);
        this.snackBar.open(err.error?.message || 'Error al crear', 'Cerrar', { duration: 3000 });
      }
    });
  }

  delete(id: number): void {
    if (!confirm('¿Desactivar esta categoría?')) return;
    this.categoryService.delete(id).subscribe({
      next: () => {
        this.snackBar.open('Categoría desactivada', 'Cerrar', { duration: 3000 });
        this.load();
      },
      error: err => {
        console.error(err);
        this.snackBar.open('Error al eliminar', 'Cerrar', { duration: 3000 });
      }
    });
  }
}