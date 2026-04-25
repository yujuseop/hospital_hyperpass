import { useQuery } from '@tanstack/react-query'
import { getDepartments } from '../api/departmentApi'
import type { Department } from '../types'

export function useDepartments() {
  return useQuery<Department[], Error>({
    queryKey: ['departments'],
    queryFn: getDepartments,
    staleTime: Infinity,
  })
}
