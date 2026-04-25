export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface Department {
  id: number;
  code: string;
  name: string;
}

export interface PreCheckInRequest {
  mainSymptom?: string;
  symptomKeywords?: string[];
  painArea?: string;
  painLevel?: number;
  startedAtText?: string;
  freeText?: string;
}

export interface PreCheckInResponse {
  receptionId: number;
  visitType: "FIRST" | "RETURN";
  status: string;
  message: string;
}

export interface StaffLoginRequest {
  username: string;
  password: string;
}

export interface StaffLoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  name: string;
}

export interface PendingReceptionResponse {
  receptionId: number;
  patientId: number;
  patientName: string;
  visitType: "FIRST" | "RETURN";
  isIdVerified: boolean;
  departmentId?: number;
  departmentName?: string;
  mainSymptom?: string;
  symptomKeywords?: string;
  painArea?: string;
  painLevel?: number;
  freeText?: string;
  submittedAt: string;
}

export interface ApproveReceptionRequest {
  departmentId: number;
}

export interface ApproveReceptionResponse {
  receptionId: number;
  departmentId: number;
  status: string;
  approvedAt: string;
  message: string;
}

export interface ApiError {
  status: number;
  code: string;
  message: string;
}
