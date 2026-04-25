package com.hyperpass.mapper;

import com.hyperpass.domain.HpDepartment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HpDepartmentMapper {
    List<HpDepartment> findAll();
    HpDepartment findById(Long id);
}
