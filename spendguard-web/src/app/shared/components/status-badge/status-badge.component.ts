import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RequestStatus } from '../../../core/models/expense-request.model';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],
  template: `<span class="badge" [ngClass]="status.toLowerCase()">{{ label }}</span>`,
  styles: [`
    .badge {
      display: inline-block;
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 600;
      color: white;
    }
    .draft { background: #9e9e9e; }
    .submitted { background: #2196f3; }
    .under_review { background: #ff9800; }
    .approved { background: #4caf50; }
    .rejected { background: #f44336; }
    .paid { background: #3f51b5; }
    .archived { background: #607d8b; }
    .cancelled { background: #795548; }
  `]
})
export class StatusBadgeComponent {
  @Input({ required: true }) status!: RequestStatus;

  get label(): string {
    return this.status.replace('_', ' ');
  }
}