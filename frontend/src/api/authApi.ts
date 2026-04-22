import instance from './axios'
import type { AuthResponse } from '../types'

export interface VerifyKakaoPayload {
  ciValue: string
  name: string
  phone: string
}

export const verifyKakao = async (payload: VerifyKakaoPayload): Promise<AuthResponse> => {
  const { data } = await instance.post<AuthResponse>('/auth/verify-kakao', payload)
  return data
}
