import { useMutation } from '@tanstack/react-query'
import { verifyPatient, type PatientAuthPayload } from '../api/authApi'
import type { AuthResponse } from '../types'

export function usePatientAuth() {
  return useMutation<AuthResponse, Error, PatientAuthPayload>({
    mutationFn: verifyPatient,
    onSuccess: (data) => {
      sessionStorage.setItem('token', data.accessToken)
    },
  })
}
