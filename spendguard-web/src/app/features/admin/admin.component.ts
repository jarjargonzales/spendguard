import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { CategoryListComponent } from './categories/category-list.component';
import { DepartmentListComponent } from './departments/department-list.component';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [MatTabsModule, CategoryListComponent, DepartmentListComponent],
  template: `
    <h1>Administración</h1>
    <mat-tab-group>
      <mat-tab label="Categorías">
        <app-category-list></app-category-list>
      </mat-tab>
      <mat-tab label="Departamentos">
        <app-department-list></app-department-list>
      </mat-tab>
    </mat-tab-group>
  `
})
export class AdminComponent {}