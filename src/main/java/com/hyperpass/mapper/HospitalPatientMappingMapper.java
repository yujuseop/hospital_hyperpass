package com.hyperpass.mapper;

import com.hyperpass.domain.HospitalPatientMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HospitalPatientMappingMapper {

    HospitalPatientMapping findByPatientIdAndHospitalCode(
            @Param("patientId") Long patientId,
            @Param("hospitalCode") String hospitalCode);

    int insert(HospitalPatientMapping mapping);
}
