package com.hyperpass.mapper;

import com.hyperpass.domain.HpIdentityLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HpIdentityLogMapper {
    int insert(HpIdentityLog log);
}
