import { useQuery } from '@tanstack/react-query'
import { getQueueByDepartment } from '../api/visitApi'

export function useWaiting(departmentId: number | null, myQueueNumber: number | null) {
  const { data: queue = [], ...rest } = useQuery({
    queryKey: ['queue', departmentId],
    queryFn: () => getQueueByDepartment(departmentId!, 'WAITING'),
    enabled: departmentId !== null,
    refetchInterval: 5000,  // 5초 폴링
  })

  const myEntry = queue.find((q) => q.queueNumber === myQueueNumber) ?? null
  const ahead = queue.filter((q) => myQueueNumber !== null && q.queueNumber < myQueueNumber).length

  return { queue, myEntry, ahead, ...rest }
}
