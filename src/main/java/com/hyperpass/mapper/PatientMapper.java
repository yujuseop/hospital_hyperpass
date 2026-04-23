package com.hyperpass.mapper;

import com.hyperpass.domain.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PatientMapper {

    Patient findById(@Param("id") Long id);

    Patient findByCiValue(@Param("ciValue") String ciValue);

    int countByCiValue(@Param("ciValue") String ciValue);

    int insert(Patient patient);

    int update(Patient patient);

    int updateSsn(@Param("id") Long id, @Param("ssn") String ssn);
}
