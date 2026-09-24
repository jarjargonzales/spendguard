import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LayoutComponent } from './shared/components/layout/layout.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'expense-requests',
        loadComponent: () => import('./features/expense-requests/request-list/request-list.component').then(m => m.RequestListComponent)
      },
      {
        path: 'approvals',
        loadComponent: () => import('./features/approvals/pending-approvals/pending-approvals.component').then(m => m.PendingApprovalsComponent)
      },
      {
        path: 'reports',
        loadComponent: () => import('./features/reports/budget-report/budget-report.component').then(m => m.BudgetReportComponent)
      }
    ]
  },
  { path: '**', redirectTo: 'login' }
];