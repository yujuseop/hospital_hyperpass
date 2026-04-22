import { useMutation } from '@tanstack/react-query'
import { processVisit } from '../api/visitApi'
import type { VisitRequest, VisitResponse } from '../types'

export function useVisit() {
  return useMutation<VisitResponse, Error, VisitRequest>({
    mutationFn: processVisit,
  })
}
