import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-pending-approvals',
  standalone: true,
  imports: [MatCardModule],
  template: `<h1>Aprobaciones</h1><mat-card><mat-card-content>Próximamente...</mat-card-content></mat-card>`
})
export class PendingApprovalsComponent {}
