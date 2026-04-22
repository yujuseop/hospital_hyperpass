import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import SymptomCard from '../components/SymptomCard'
import { useVisit } from '../hooks/useVisit'
import type { Symptom } from '../types'

const SYMPTOMS: Symptom[] = [
  { keyword: '기침',    label: '기침',    icon: '🫁', departmentId: 1,  departmentName: '내과'     },
  { keyword: '발열',    label: '발열',    icon: '🌡️', departmentId: 1,  departmentName: '내과'     },
  { keyword: '복통',    label: '복통',    icon: '🤢', departmentId: 1,  departmentName: '내과'     },
  { keyword: '골절',    label: '골절',    icon: '🦴', departmentId: 3,  departmentName: '정형외과' },
  { keyword: '관절통',  label: '관절통',  icon: '🦵', departmentId: 3,  departmentName: '정형외과' },
  { keyword: '피부발진',label: '피부발진',icon: '🩹', departmentId: 6,  departmentName: '피부과'   },
  { keyword: '눈충혈',  label: '눈충혈',  icon: '👁️', departmentId: 7,  departmentName: '안과'     },
  { keyword: '이통',    label: '이통',    icon: '👂', departmentId: 8,  departmentName: '이비인후과'},
  { keyword: '두통',    label: '두통',    icon: '🧠', departmentId: 9,  departmentName: '신경과'   },
  { keyword: '흉통',    label: '흉통',    icon: '❤️', departmentId: 12, departmentName: '심장내과' },
]

export default function SymptomsPage() {
  const navigate = useNavigate()
  const [selected, setSelected] = useState<string[]>([])
  const visitMutation = useVisit()

  const toggle = (keyword: string) => {
    setSelected((prev) =>
      prev.includes(keyword) ? prev.filter((k) => k !== keyword) : [...prev, keyword]
    )
  }

  const handleSubmit = async () => {
    // 첫 번째 선택 증상의 진료과로 접수 (복수 선택 시 첫 번째 우선)
    const primary = SYMPTOMS.find((s) => s.keyword === selected[0])
    const kioskId = sessionStorage.getItem('kioskId')

    const result = await visitMutation.mutateAsync({
      kioskId: kioskId ? Number(kioskId) : undefined,
      departmentId: primary?.departmentId,
      symptomKeyword: selected[0],
    })

    sessionStorage.setItem('queueNumber', String(result.queueNumber))
    sessionStorage.setItem('departmentId', String(result.departmentId))
    navigate('/waiting')
  }

  return (
    <div className="min-h-screen flex flex-col p-6 gap-6">
      <div className="text-center space-y-1 pt-4">
        <h1 className="text-2xl font-extrabold text-gray-900">증상 선택</h1>
        <p className="text-gray-500">해당하는 증상을 선택해 주세요 (복수 선택 가능)</p>
      </div>

      <div className="grid grid-cols-2 gap-3 relative">
        {SYMPTOMS.map((symptom) => (
          <SymptomCard
            key={symptom.keyword}
            symptom={symptom}
            selected={selected.includes(symptom.keyword)}
            onToggle={toggle}
          />
        ))}
      </div>

      {visitMutation.isError && (
        <div className="bg-red-50 border border-red-200 rounded-xl px-4 py-3">
          <p className="text-red-600 text-base">접수 중 오류가 발생했습니다. 다시 시도해 주세요.</p>
        </div>
      )}

      <div className="sticky bottom-6">
        <button
          className="btn-primary disabled:opacity-40"
          disabled={selected.length === 0 || visitMutation.isPending}
          onClick={handleSubmit}
        >
          {visitMutation.isPending ? '접수 중…' : `접수하기 ${selected.length > 0 ? `(${selected.length}개 선택)` : ''}`}
        </button>
      </div>
    </div>
  )
}
