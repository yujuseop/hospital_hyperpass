import instance from './axios'
import type { PreCheckInRequest, PreCheckInResponse } from '../types'

export const submitPreCheckIn = async (payload: PreCheckInRequest): Promise<PreCheckInResponse> => {
  const { data } = await instance.post<PreCheckInResponse>('/receptions/precheckin', payload)
  return data
}
