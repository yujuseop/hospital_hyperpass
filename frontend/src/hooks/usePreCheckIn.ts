import { useMutation } from '@tanstack/react-query'
import { submitPreCheckIn } from '../api/receptionApi'
import type { PreCheckInRequest, PreCheckInResponse } from '../types'

export function usePreCheckIn() {
  return useMutation<PreCheckInResponse, Error, PreCheckInRequest>({
    mutationFn: submitPreCheckIn,
  })
}
