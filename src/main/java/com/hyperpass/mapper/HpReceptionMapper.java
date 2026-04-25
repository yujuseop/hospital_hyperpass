package com.hyperpass.mapper;

import com.hyperpass.domain.HpReception;
import com.hyperpass.dto.PendingReceptionResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface HpReceptionMapper {
    HpReception findById(@Param("id") Long id);
    HpReception findActiveByPatientId(@Param("patientId") Long patientId);
    List<PendingReceptionResponse> findPending();
    int insert(HpReception reception);
    int updateIdVerified(@Param("id") Long id, @Param("idVerifiedAt") LocalDateTime idVerifiedAt);
    int updateApproval(@Param("id") Long id,
                       @Param("departmentId") Long departmentId,
                       @Param("approvedBy") Long approvedBy,
                       @Param("approvedAt") LocalDateTime approvedAt,
                       @Param("updatedAt") LocalDateTime updatedAt);
}
