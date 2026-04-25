import { useQuery } from '@tanstack/react-query'
import { getPendingReceptions } from '../api/adminApi'
import type { PendingReceptionResponse } from '../types'

// 원무과 대시보드의 승인 대기 목록을 주기적으로 새로고침한다.
export function usePendingReceptions(enabled: boolean) {
  return useQuery<PendingReceptionResponse[], Error>({
    queryKey: ['admin', 'pending-receptions'],
    queryFn: getPendingReceptions,
    enabled,
    refetchInterval: 5000,
  })
}
