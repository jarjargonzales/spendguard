export type ApprovalAction = 'APPROVE' | 'REJECT' | 'ESCALATE';

export interface Approval {
  approvalId: number;
  requestId: number;
  approverId: number;
  approverName: string;
  decision: string;
  comments?: string;
  decidedAt?: string;
  sequenceOrder: number;
}

export interface ApprovalDecision {
  decision: ApprovalAction;
  comments?: string;
}