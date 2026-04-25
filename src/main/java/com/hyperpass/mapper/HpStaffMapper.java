package com.hyperpass.mapper;

import com.hyperpass.domain.HpStaff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HpStaffMapper {
    HpStaff findByUsername(@Param("username") String username);
    int insert(HpStaff staff);
}
