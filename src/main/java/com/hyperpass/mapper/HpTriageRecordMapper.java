package com.hyperpass.mapper;

import com.hyperpass.domain.HpTriageRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HpTriageRecordMapper {
    int insert(HpTriageRecord record);
}
