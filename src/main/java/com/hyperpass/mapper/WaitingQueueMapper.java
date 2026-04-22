package com.hyperpass.mapper;

import com.hyperpass.domain.WaitingQueue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface WaitingQueueMapper {

    int insert(WaitingQueue queue);

    WaitingQueue findById(@Param("id") Long id);

    List<WaitingQueue> findByDepartmentIdAndStatus(
            @Param("departmentId") Long departmentId,
            @Param("status") String status);

    int maxQueueNumberByDepartmentId(@Param("departmentId") Long departmentId);

    int updateStatus(
            @Param("id") Long id,
            @Param("status") String status,
            @Param("calledAt") LocalDateTime calledAt,
            @Param("completedAt") LocalDateTime completedAt);
}
