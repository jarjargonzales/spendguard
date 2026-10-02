import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Department {
  departmentId: number;
  name: string;
  description?: string;
  monthlyBudget: number;
  active: boolean;
}

export interface DepartmentDTO {
  name: string;
  description?: string;
  monthlyBudget: number;
}

@Injectable({ providedIn: 'root' })
export class DepartmentService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/departments`;

  getAll(): Observable<Department[]> {
    return this.http.get<Department[]>(this.apiUrl);
  }

  create(dto: DepartmentDTO): Observable<Department> {
    return this.http.post<Department>(this.apiUrl, dto);
  }

  update(id: number, dto: DepartmentDTO): Observable<Department> {
    return this.http.put<Department>(`${this.apiUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}