import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateExpenseRequest, ExpenseRequest } from '../models/expense-request.model';

@Injectable({ providedIn: 'root' })
export class ExpenseRequestService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/expense-requests`;

  getAll(): Observable<ExpenseRequest[]> {
    return this.http.get<ExpenseRequest[]>(this.apiUrl);
  }

  getById(id: number): Observable<ExpenseRequest> {
    return this.http.get<ExpenseRequest>(`${this.apiUrl}/${id}`);
  }

  create(dto: CreateExpenseRequest): Observable<ExpenseRequest> {
    return this.http.post<ExpenseRequest>(this.apiUrl, dto);
  }

  update(id: number, dto: CreateExpenseRequest): Observable<ExpenseRequest> {
    return this.http.put<ExpenseRequest>(`${this.apiUrl}/${id}`, dto);
  }

  submit(id: number): Observable<ExpenseRequest> {
    return this.http.post<ExpenseRequest>(`${this.apiUrl}/${id}/submit`, {});
  }

  cancel(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}