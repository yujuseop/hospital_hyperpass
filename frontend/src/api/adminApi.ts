import instance from './axios'
import type {
  ApproveReceptionRequest,
  ApproveReceptionResponse,
  PendingReceptionResponse,
  StaffLoginRequest,
  StaffLoginResponse,
} from '../types'

export const loginStaff = async (payload: StaffLoginRequest): Promise<StaffLoginResponse> => {
  const { data } = await instance.post<StaffLoginResponse>('/admin/login', payload)
  return data
}

export const getPendingReceptions = async (): Promise<PendingReceptionResponse[]> => {
  const { data } = await instance.get<PendingReceptionResponse[]>('/admin/receptions/pending')
  return data
}

export const verifyId = async (receptionId: number): Promise<void> => {
  await instance.patch(`/admin/receptions/${receptionId}/verify-id`)
}

export const approveReception = async (
  receptionId: number,
  payload: ApproveReceptionRequest
): Promise<ApproveReceptionResponse> => {
  const { data } = await instance.patch<ApproveReceptionResponse>(
    `/admin/receptions/${receptionId}/approve`,
    payload
  )
  return data
}
