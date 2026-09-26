import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ExpenseRequestService } from '../../../core/services/expense-request.service'; 
import { ExpenseRequest } from '../../../core/models/expense-request.model';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { RequestFormComponent } from '../request-form/request-form.component'; 

@Component({
  selector: 'app-request-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    StatusBadgeComponent
  ],
  templateUrl: './request-list.component.html',
  styleUrl: './request-list.component.scss',
  template: `<h1>Solicitudes</h1><mat-card><mat-card-content>Próximamente...</mat-card-content></mat-card>`
})
export class RequestListComponent implements OnInit {
  private requestService = inject(ExpenseRequestService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  loading = signal(false);
  requests = signal<ExpenseRequest[]>([]);
  displayedColumns = ['title', 'amount', 'department', 'category', 'status', 'actions'];

  ngOnInit(): void {
    this.loadRequests();
  }

  loadRequests(): void {
    this.loading.set(true);
    this.requestService.getAll().subscribe({
      next: data => {
        this.requests.set(data);
        this.loading.set(false);
      },
      error: err => {
        console.error(err);
        this.loading.set(false);
        this.snackBar.open('Error al cargar solicitudes', 'Cerrar', { duration: 3000 });
      }
    });
  }

  openCreateDialog(): void {
    const dialogRef = this.dialog.open(RequestFormComponent, { width: '500px' });
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.loadRequests();
    });
  }

  submit(id: number): void {
    this.requestService.submit(id).subscribe({
      next: () => {
        this.snackBar.open('Solicitud enviada', 'Cerrar', { duration: 3000 });
        this.loadRequests();
      },
      error: err => {
        console.error(err);
        this.snackBar.open(err.error?.message || 'Error al enviar', 'Cerrar', { duration: 3000 });
      }
    });
  }

  cancel(id: number): void {
    if (!confirm('¿Cancelar esta solicitud?')) return;
    this.requestService.cancel(id).subscribe({
      next: () => {
        this.snackBar.open('Solicitud cancelada', 'Cerrar', { duration: 3000 });
        this.loadRequests();
      },
      error: err => {
        console.error(err);
        this.snackBar.open(err.error?.message || 'Error al cancelar', 'Cerrar', { duration: 3000 });
      }
    });
  }
}