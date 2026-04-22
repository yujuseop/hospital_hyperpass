import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useKakaoAuth } from '../hooks/useKakaoAuth'

export default function AuthPage() {
  const navigate = useNavigate()
  const { authenticate, loading, error, isMock } = useKakaoAuth()

  const [name, setName] = useState('')
  const [phone, setPhone] = useState('')

  const handleMockSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const ok = await authenticate({ name, phone })
    if (ok) navigate('/symptoms')
  }

  const handleKakaoClick = async () => {
    const ok = await authenticate()
    if (ok) navigate('/symptoms')
  }

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-6 gap-8">
      <div className="text-center space-y-2">
        <p className="text-5xl">🔐</p>
        <h1 className="text-2xl font-extrabold text-gray-900">본인 확인</h1>
        <p className="text-gray-500 text-base">
          {isMock ? '개발용 Mock 인증 모드' : '카카오 인증서로 안전하게 확인합니다'}
        </p>
      </div>

      <div className="w-full max-w-sm space-y-4">
        {isMock ? (
          /* ── Mock 모드 입력 폼 ── */
          <form onSubmit={handleMockSubmit} className="card space-y-4">
            <p className="text-sm font-semibold text-yellow-700 bg-yellow-50 border border-yellow-200 rounded-xl px-3 py-2">
              🧪 Mock 모드 — 실제 카카오 인증 없이 테스트합니다
            </p>

            <div className="space-y-2">
              <label className="text-base font-semibold text-gray-700">이름</label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="홍길동"
                required
                className="w-full border border-gray-300 rounded-xl px-4 py-3 text-base
                           focus:outline-none focus:border-primary"
              />
            </div>

            <div className="space-y-2">
              <label className="text-base font-semibold text-gray-700">전화번호</label>
              <input
                type="tel"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="010-1234-5678"
                required
                className="w-full border border-gray-300 rounded-xl px-4 py-3 text-base
                           focus:outline-none focus:border-primary"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="btn-primary disabled:opacity-50"
            >
              {loading ? '인증 중…' : '확인'}
            </button>
          </form>
        ) : (
          /* ── 실제 카카오 인증서 버튼 ── */
          <button
            className="btn-kakao"
            onClick={handleKakaoClick}
            disabled={loading}
          >
            <span className="text-2xl">💛</span>
            {loading ? '인증 중…' : '카카오 인증서로 본인확인'}
          </button>
        )}

        {error && (
          <div className="bg-red-50 border border-red-200 rounded-xl px-4 py-3">
            <p className="text-red-600 text-base">{error}</p>
          </div>
        )}
      </div>
    </div>
  )
}
