import instance from './axios'
import type { AuthResponse } from '../types'

export interface PatientAuthPayload {
  name: string
  rrn: string
  address: string
  phone: string
}

export const verifyPatient = async (payload: PatientAuthPayload): Promise<AuthResponse> => {
  const { data } = await instance.post<AuthResponse>('/auth/verify', payload)
  return data
}
