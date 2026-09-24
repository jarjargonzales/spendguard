import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Approval, ApprovalDecision } from '../models/approval.model';

@Injectable({ providedIn: 'root' })
export class ApprovalService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/approvals`;

  getPending(): Observable<Approval[]> {
    return this.http.get<Approval[]>(`${this.apiUrl}/pending`);
  }

  getHistory(): Observable<Approval[]> {
    return this.http.get<Approval[]>(`${this.apiUrl}/history`);
  }

  decide(requestId: number, decision: ApprovalDecision): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${requestId}`, decision);
  }
}