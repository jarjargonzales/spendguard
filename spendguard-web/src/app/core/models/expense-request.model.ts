export type RequestStatus =
  | 'DRAFT'
  | 'SUBMITTED'
  | 'UNDER_REVIEW'
  | 'APPROVED'
  | 'REJECTED'
  | 'PAID'
  | 'ARCHIVED'
  | 'CANCELLED';

export interface ExpenseRequest {
  requestId: number;
  title: string;
  description?: string;
  amount: number;
  currency: string;
  status: RequestStatus;
  submissionDate?: string;
  resolutionDate?: string;
  requesterId: number;
  requesterName: string;
  departmentId: number;
  departmentName: string;
  categoryId: number;
  categoryName: string;
}

export interface CreateExpenseRequest {
  title: string;
  description?: string;
  amount: number;
  currency: string;
  categoryId: number;
  departmentId: number;
}