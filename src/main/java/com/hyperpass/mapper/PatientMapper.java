package com.hyperpass.mapper;

import com.hyperpass.domain.Patient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper {

    Patient findById( Long id);

    int insert(Patient patient);
}
