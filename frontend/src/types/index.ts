export interface AuthResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
}

export interface PatientResponse {
  id: number
  ciValue: string
  name: string
  birthDate?: string
  gender?: string
  phone?: string
  firstVisitAt?: string
  lastVisitAt?: string
  createdAt?: string
}

export interface VisitRequest {
  kioskId?: number
  departmentId?: number
  symptomKeyword?: string
  hisPatientNo?: string
  hospitalCode?: string
}

export interface VisitResponse {
  visitId: number
  patientId: number
  visitType: 'FIRST' | 'RETURN'
  queueNumber: number
  departmentId: number
  visitedAt: string
}

export interface WaitingQueue {
  id: number
  patientId: number
  departmentId: number
  queueNumber: number
  status: 'WAITING' | 'CALLED' | 'DONE' | 'CANCELLED'
  queuedAt: string
  calledAt?: string
  completedAt?: string
}

export interface ApiError {
  status: number
  code: string
  message: string
}

export interface Symptom {
  keyword: string
  label: string
  icon: string
  departmentId: number
  departmentName: string
}
