export type UserRole = 'EMPLOYEE' | 'MANAGER' | 'DIRECTOR' | 'CFO' | 'ADMIN';

export interface User {
  userId: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  role: UserRole;
  departmentId?: number;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}