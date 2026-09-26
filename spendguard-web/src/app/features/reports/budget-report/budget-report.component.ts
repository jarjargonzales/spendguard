import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-budget-report',
  standalone: true,
  imports: [MatCardModule],
  template: `<h1>Reportes</h1><mat-card><mat-card-content>Próximamente...</mat-card-content></mat-card>`
})
export class BudgetReportComponent {}
