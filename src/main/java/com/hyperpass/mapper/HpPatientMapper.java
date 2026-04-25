package com.hyperpass.mapper;

import com.hyperpass.domain.HpPatient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface HpPatientMapper {
    HpPatient findById(@Param("id") Long id);
    HpPatient findByNameAndPhone(@Param("name") String name, @Param("phone") String phone);
    int insert(HpPatient patient);
    int updateAuthProfile(HpPatient patient);
    int updateLastVisitDate(@Param("id") Long id, @Param("lastVisitDate") LocalDate lastVisitDate);
}
