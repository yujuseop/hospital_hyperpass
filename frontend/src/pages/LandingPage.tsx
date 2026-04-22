import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'

export default function LandingPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const [kioskId, setKioskId] = useState<string | null>(null)

  useEffect(() => {
    const id = searchParams.get('kioskId')
    if (id) {
      sessionStorage.setItem('kioskId', id)
      setKioskId(id)
    }
  }, [searchParams])

  const handleStart = () => navigate('/auth')

  if (!kioskId) {
    return (
      <div className="min-h-screen flex items-center justify-center p-6">
        <div className="card text-center space-y-3 max-w-sm w-full">
          <p className="text-4xl">⚠️</p>
          <p className="text-lg font-semibold text-gray-800">잘못된 접근입니다</p>
          <p className="text-gray-500">키오스크 QR 코드를 다시 스캔해 주세요.</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-6 gap-8">
      <div className="text-center space-y-3">
        <p className="text-6xl">🏥</p>
        <h1 className="text-2xl font-extrabold text-gray-900">하이패스 접수</h1>
        <p className="text-gray-500">본인 확인 후 빠르게 접수할 수 있습니다</p>
      </div>

      <div className="w-full max-w-sm space-y-4">
        <div className="card text-center">
          <p className="text-gray-400 text-sm mb-1">접수 단말기</p>
          <p className="text-primary font-bold text-lg">{kioskId}</p>
        </div>

        <button className="btn-primary" onClick={handleStart}>
          본인확인 시작하기
        </button>
      </div>
    </div>
  )
}
