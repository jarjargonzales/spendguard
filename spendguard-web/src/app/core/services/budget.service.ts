import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { BudgetStatus } from '../models/budget.model';

@Injectable({ providedIn: 'root' })
export class BudgetService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/budgets`;

  getStatus(departmentId: number): Observable<BudgetStatus> {
    return this.http.get<BudgetStatus>(`${this.apiUrl}/${departmentId}/status`);
  }
}