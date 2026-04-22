import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import QueueStatus from '../components/QueueStatus'
import { useWaiting } from '../hooks/useWaiting'

const DEPT_NAMES: Record<number, string> = {
  1:  '내과',     2: '외과',    3: '정형외과',
  4:  '소아과',   5: '산부인과', 6: '피부과',
  7:  '안과',     8: '이비인후과', 9: '신경과',
  10: '정신건강의학과', 11: '비뇨의학과', 12: '심장내과', 13: '응급의학과',
}

export default function WaitingPage() {
  const navigate = useNavigate()

  const queueNumber = Number(sessionStorage.getItem('queueNumber')) || null
  const departmentId = Number(sessionStorage.getItem('departmentId')) || null
  const departmentName = departmentId ? (DEPT_NAMES[departmentId] ?? '진료과') : '진료과'

  const { myEntry, ahead, isError } = useWaiting(departmentId, queueNumber)

  // 호출 시 진동 알림
  useEffect(() => {
    if (myEntry?.status === 'CALLED' && navigator.vibrate) {
      navigator.vibrate([300, 100, 300])
    }
  }, [myEntry?.status])

  if (!queueNumber || !departmentId) {
    return (
      <div className="min-h-screen flex items-center justify-center p-6">
        <div className="card text-center space-y-3 max-w-sm w-full">
          <p className="text-4xl">⚠️</p>
          <p className="text-lg font-semibold">접수 정보가 없습니다</p>
          <button className="btn-primary" onClick={() => navigate('/')}>처음으로</button>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-6 gap-6">
      <div className="text-center space-y-1">
        <h1 className="text-2xl font-extrabold text-gray-900">대기 현황</h1>
        <p className="text-gray-400 text-sm">5초마다 자동으로 갱신됩니다</p>
      </div>

      <div className="w-full max-w-sm space-y-4">
        {isError ? (
          <div className="card text-center">
            <p className="text-red-500">대기 정보를 불러오지 못했습니다.</p>
          </div>
        ) : (
          <QueueStatus
            myQueue={myEntry ?? null}
            ahead={ahead}
            departmentName={departmentName}
          />
        )}

        {myEntry?.status === 'DONE' && (
          <button className="btn-primary" onClick={() => navigate('/')}>
            처음으로 돌아가기
          </button>
        )}
      </div>
    </div>
  )
}
