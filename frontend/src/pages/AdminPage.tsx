import { useMemo, useState } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { verifyId, approveReception } from '../api/adminApi'
import { useAdminAuth } from '../hooks/useAdminAuth'
import { usePendingReceptions } from '../hooks/usePendingReceptions'
import { useDepartments } from '../hooks/useDepartments'
import type { PendingReceptionResponse } from '../types'

function formatDateTime(value: string): string {
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return value
  return d.toLocaleString('ko-KR', { hour12: false })
}

function ReceptionCard({
  row,
  departments,
  onVerifyId,
  onApprove,
  isPending,
}: {
  row: PendingReceptionResponse
  departments: { id: number; name: string }[]
  onVerifyId: (id: number) => void
  onApprove: (id: number, departmentId: number) => void
  isPending: boolean
}) {
  const [selectedDeptId, setSelectedDeptId] = useState<number | ''>('')
  const needsIdVerify = row.visitType === 'FIRST' && !row.isIdVerified
  const canApprove = !needsIdVerify && selectedDeptId !== ''

  return (
    <div className="card space-y-3">
      {/* 헤더 */}
      <div className="flex items-start justify-between gap-3">
        <div>
          <div className="flex items-center gap-2">
            <p className="text-sm text-gray-500">접수번호 #{row.receptionId}</p>
            <span
              className={`text-xs font-semibold px-2 py-0.5 rounded-full ${
                row.visitType === 'FIRST'
                  ? 'bg-orange-100 text-orange-700'
                  : 'bg-blue-100 text-blue-700'
              }`}
            >
              {row.visitType === 'FIRST' ? '초진' : '재진'}
            </span>
          </div>
          <p className="text-lg font-semibold text-gray-900">{row.patientName}</p>
          <p className="text-sm text-gray-500">{formatDateTime(row.submittedAt)}</p>
        </div>
      </div>

      {/* 신분증 확인 경고 (초진 & 미확인) */}
      {needsIdVerify && (
        <div className="flex items-center justify-between bg-orange-50 border border-orange-200 rounded-xl px-3 py-2">
          <p className="text-sm font-semibold text-orange-700">⚠️ 신분증 확인 필요</p>
          <button
            className="text-sm font-semibold text-white bg-orange-500 hover:bg-orange-600 rounded-lg px-3 py-1.5 disabled:opacity-50"
            disabled={isPending}
            onClick={() => onVerifyId(row.receptionId)}
          >
            신분증 확인 완료
          </button>
        </div>
      )}

      {/* 신분증 확인 완료 표시 */}
      {row.visitType === 'FIRST' && row.isIdVerified && (
        <div className="bg-green-50 border border-green-200 rounded-xl px-3 py-2">
          <p className="text-sm font-semibold text-green-700">✅ 신분증 확인 완료</p>
        </div>
      )}

      {/* 문진 내용 */}
      <div className="text-sm text-gray-600 space-y-1">
        <p><span className="font-semibold text-gray-700">대표 증상:</span> {row.mainSymptom ?? '-'}</p>
        <p><span className="font-semibold text-gray-700">복수 증상:</span> {row.symptomKeywords ?? '-'}</p>
        <p><span className="font-semibold text-gray-700">통증 부위:</span> {row.painArea ?? '-'}</p>
        <p><span className="font-semibold text-gray-700">통증 강도:</span> {row.painLevel ?? 0}</p>
        <p><span className="font-semibold text-gray-700">추가 전달:</span> {row.freeText ?? '-'}</p>
      </div>

      {/* 진료과 선택 + 승인 */}
      <div className="flex gap-2 items-center">
        <select
          value={selectedDeptId}
          onChange={(e) => setSelectedDeptId(e.target.value === '' ? '' : Number(e.target.value))}
          className="flex-1 border border-gray-300 rounded-xl px-3 py-2 text-sm outline-none focus:border-primary"
        >
          <option value="">진료과 선택</option>
          {departments.map((d) => (
            <option key={d.id} value={d.id}>{d.name}</option>
          ))}
        </select>
        <button
          className="btn-primary !w-auto px-4 text-sm min-h-0 h-10 disabled:opacity-40"
          disabled={!canApprove || isPending}
          onClick={() => onApprove(row.receptionId, selectedDeptId as number)}
        >
          접수 승인
        </button>
      </div>
    </div>
  )
}

export default function AdminPage() {
  const queryClient = useQueryClient()
  const staffName = sessionStorage.getItem('staffName')
  const [username, setUsername] = useState('admin')
  const [password, setPassword] = useState('admin1234')
  const [errorMessage, setErrorMessage] = useState<string | null>(null)

  const isLoggedIn = useMemo(() => Boolean(staffName), [staffName])
  const loginMutation = useAdminAuth()
  const pendingQuery = usePendingReceptions(isLoggedIn)
  const departmentsQuery = useDepartments()

  const verifyIdMutation = useMutation({
    mutationFn: (receptionId: number) => verifyId(receptionId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'pending-receptions'] })
    },
    onError: () => setErrorMessage('신분증 확인 처리 중 오류가 발생했습니다.'),
  })

  const approveMutation = useMutation({
    mutationFn: ({ receptionId, departmentId }: { receptionId: number; departmentId: number }) =>
      approveReception(receptionId, { departmentId }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'pending-receptions'] })
    },
    onError: () => setErrorMessage('승인 처리 중 오류가 발생했습니다. 다시 시도해 주세요.'),
  })

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setErrorMessage(null)
    try {
      await loginMutation.mutateAsync({ username, password })
    } catch {
      setErrorMessage('로그인에 실패했습니다. 아이디/비밀번호를 확인해 주세요.')
    }
  }

  const handleLogout = () => {
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('staffName')
    window.location.reload()
  }

  const isMutating = verifyIdMutation.isPending || approveMutation.isPending

  if (!isLoggedIn) {
    return (
      <div className="min-h-screen flex items-center justify-center p-6">
        <form onSubmit={handleLogin} className="card w-full max-w-sm space-y-4">
          <div className="text-center space-y-1">
            <p className="text-4xl">🧾</p>
            <h1 className="text-2xl font-extrabold text-gray-900">원무과 로그인</h1>
            <p className="text-gray-500 text-sm">승인 대기 문진과 접수 현황을 관리합니다.</p>
          </div>

          <label className="block space-y-1">
            <span className="text-sm text-gray-600">아이디</span>
            <input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full border border-gray-300 rounded-xl px-4 py-3 text-base focus:outline-none focus:border-primary"
              required
            />
          </label>

          <label className="block space-y-1">
            <span className="text-sm text-gray-600">비밀번호</span>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full border border-gray-300 rounded-xl px-4 py-3 text-base focus:outline-none focus:border-primary"
              required
            />
          </label>

          <button className="btn-primary" type="submit" disabled={loginMutation.isPending}>
            {loginMutation.isPending ? '로그인 중…' : '로그인'}
          </button>

          {errorMessage && (
            <p className="text-red-600 text-sm bg-red-50 border border-red-200 rounded-xl px-3 py-2">
              {errorMessage}
            </p>
          )}
        </form>
      </div>
    )
  }

  return (
    <div className="min-h-screen p-4 md:p-6 space-y-4">
      <div className="card flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
        <div>
          <p className="text-sm text-gray-500">원무과 대시보드</p>
          <h1 className="text-xl font-extrabold text-gray-900">사전 문진 승인 대기</h1>
          <p className="text-sm text-gray-500">{staffName} 님</p>
        </div>
        <button
          className="rounded-xl px-4 py-2 border border-gray-300 text-sm text-gray-700 hover:bg-gray-50"
          onClick={handleLogout}
        >
          로그아웃
        </button>
      </div>

      {pendingQuery.isLoading ? (
        <div className="card text-gray-500">목록을 불러오는 중입니다…</div>
      ) : pendingQuery.isError ? (
        <div className="card text-red-600">승인 대기 목록 조회 중 오류가 발생했습니다.</div>
      ) : pendingQuery.data && pendingQuery.data.length > 0 ? (
        <div className="space-y-3">
          {pendingQuery.data.map((row) => (
            <ReceptionCard
              key={row.receptionId}
              row={row}
              departments={departmentsQuery.data ?? []}
              isPending={isMutating}
              onVerifyId={(id) => verifyIdMutation.mutate(id)}
              onApprove={(id, deptId) => approveMutation.mutate({ receptionId: id, departmentId: deptId })}
            />
          ))}
        </div>
      ) : (
        <div className="card text-gray-500">현재 승인 대기 중인 문진이 없습니다.</div>
      )}

      {errorMessage && (
        <p className="text-red-600 text-sm bg-red-50 border border-red-200 rounded-xl px-3 py-2">
          {errorMessage}
        </p>
      )}
    </div>
  )
}
