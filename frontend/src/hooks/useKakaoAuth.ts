import { useState } from 'react'
import { verifyKakao } from '../api/authApi'

interface MockAuthInput {
  name: string
  phone: string
}

const isMock = import.meta.env.VITE_KAKAO_MOCK === 'true'

export function useKakaoAuth() {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  /**
   * Mock 모드: 이름·전화번호 입력 → CI는 UUID 자동 생성
   * 실제 모드: 카카오 SDK window.Kakao.Cert.request() 호출
   */
  const authenticate = async (mockInput?: MockAuthInput): Promise<boolean> => {
    setLoading(true)
    setError(null)

    try {
      let ciValue: string
      let name: string
      let phone: string

      if (isMock) {
        if (!mockInput) {
          setError('Mock 모드에서는 이름과 전화번호가 필요합니다.')
          return false
        }
        // Mock CI: 이름 + 전화번호 기반 고정 식별값 (실제 환경에서는 카카오 CI로 대체)
        ciValue = `mock-ci-${mockInput.name}-${mockInput.phone}`
        name = mockInput.name
        phone = mockInput.phone
      } else {
        // TODO: 카카오 앱 키 발급 후 SDK 연동
        // const result = await window.Kakao.Cert.request({ ... })
        // ciValue = result.ci
        // name = result.name
        // phone = result.phone_number
        throw new Error('카카오 앱 키가 설정되지 않았습니다.')
      }

      const authResponse = await verifyKakao({ ciValue, name, phone })
      sessionStorage.setItem('token', authResponse.accessToken)

      return true
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : '인증에 실패했습니다.'
      setError(msg)
      return false
    } finally {
      setLoading(false)
    }
  }

  return { authenticate, loading, error, isMock }
}
