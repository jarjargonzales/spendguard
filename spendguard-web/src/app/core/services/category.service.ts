import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Category {
  categoryId: number;
  name: string;
  description?: string;
  requiresApproval: boolean;
  active: boolean;
}

export interface CategoryDTO {
  name: string;
  description?: string;
  requiresApproval: boolean;
}

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/categories`;

  getAll(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiUrl);
  }

  create(dto: CategoryDTO): Observable<Category> {
    return this.http.post<Category>(this.apiUrl, dto);
  }

  update(id: number, dto: CategoryDTO): Observable<Category> {
    return this.http.put<Category>(`${this.apiUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}