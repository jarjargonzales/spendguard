export interface BudgetStatus {
  departmentId: number;
  departmentName: string;
  monthYear: string;
  budgetAllocated: number;
  budgetConsumed: number;
  remainingBudget: number;
  consumptionPercentage: number;
}