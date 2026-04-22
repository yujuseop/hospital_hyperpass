import type { WaitingQueue } from '../types'

interface Props {
  myQueue: WaitingQueue | null
  ahead: number
  departmentName: string
}

const STATUS_LABEL: Record<string, { text: string; color: string }> = {
  WAITING:   { text: '대기 중',  color: 'bg-yellow-100 text-yellow-700' },
  CALLED:    { text: '호출됨!',  color: 'bg-green-100 text-green-700'  },
  DONE:      { text: '완료',     color: 'bg-gray-100  text-gray-500'   },
  CANCELLED: { text: '취소됨',   color: 'bg-red-100   text-red-500'    },
}

export default function QueueStatus({ myQueue, ahead, departmentName }: Props) {
  if (!myQueue) {
    return (
      <div className="card text-center py-10">
        <p className="text-gray-400 text-lg">대기 정보를 불러오는 중입니다…</p>
      </div>
    )
  }

  const { text, color } = STATUS_LABEL[myQueue.status] ?? STATUS_LABEL.WAITING

  return (
    <div className="card text-center space-y-4">
      <p className="text-gray-500 text-lg">{departmentName}</p>

      <div className="py-4">
        <p className="text-gray-400 text-base mb-1">내 대기 번호</p>
        <p className="text-7xl font-extrabold text-primary">{myQueue.queueNumber}</p>
        <p className="text-gray-400 text-base mt-1">번</p>
      </div>

      <span className={`inline-block px-4 py-2 rounded-full text-base font-semibold ${color}`}>
        {text}
      </span>

      {myQueue.status === 'WAITING' && (
        <p className="text-gray-500 text-base">
          앞에 <span className="font-bold text-gray-800">{ahead}명</span> 대기 중
        </p>
      )}

      {myQueue.status === 'CALLED' && (
        <div className="bg-green-50 border border-green-200 rounded-xl p-4">
          <p className="text-green-700 font-bold text-lg">진료실로 이동해 주세요!</p>
        </div>
      )}
    </div>
  )
}
