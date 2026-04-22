import instance from './axios'
import type { VisitRequest, VisitResponse, WaitingQueue } from '../types'

export const processVisit = async (payload: VisitRequest): Promise<VisitResponse> => {
  const { data } = await instance.post<VisitResponse>('/visits', payload)
  return data
}

export const getQueueByDepartment = async (
  departmentId: number,
  status = 'WAITING'
): Promise<WaitingQueue[]> => {
  const { data } = await instance.get<WaitingQueue[]>(
    `/queue/department/${departmentId}?status=${status}`
  )
  return data
}

export const updateQueueStatus = async (
  queueId: number,
  status: string
): Promise<WaitingQueue> => {
  const { data } = await instance.patch<WaitingQueue>(`/queue/${queueId}/status`, { status })
  return data
}
