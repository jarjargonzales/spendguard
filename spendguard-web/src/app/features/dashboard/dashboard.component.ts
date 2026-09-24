import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [MatCardModule],
  template: `
    <h1>Dashboard</h1>
    <mat-card>
      <mat-card-content>
        <p>Bienvenido a SpendGuard</p>
      </mat-card-content>
    </mat-card>
  `
})
export class DashboardComponent {}