import { useMutation } from '@tanstack/react-query'
import { loginStaff } from '../api/adminApi'
import type { StaffLoginRequest, StaffLoginResponse } from '../types'

// 원무과 로그인 요청을 처리하고 세션 토큰을 저장한다.
export function useAdminAuth() {
  return useMutation<StaffLoginResponse, Error, StaffLoginRequest>({
    mutationFn: loginStaff,
    onSuccess: (result) => {
      sessionStorage.setItem('token', result.accessToken)
      sessionStorage.setItem('staffName', result.name)
    },
  })
}
