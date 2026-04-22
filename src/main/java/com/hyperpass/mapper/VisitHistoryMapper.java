package com.hyperpass.mapper;

import com.hyperpass.domain.VisitHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VisitHistoryMapper {

    int insert(VisitHistory visitHistory);

    List<VisitHistory> findByPatientId(@Param("patientId") Long patientId);

    int countByPatientId(@Param("patientId") Long patientId);
}
