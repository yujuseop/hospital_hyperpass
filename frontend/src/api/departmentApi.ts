import instance from './axios'
import type { Department } from '../types'

export const getDepartments = async (): Promise<Department[]> => {
  const { data } = await instance.get<Department[]>('/departments')
  return data
}
