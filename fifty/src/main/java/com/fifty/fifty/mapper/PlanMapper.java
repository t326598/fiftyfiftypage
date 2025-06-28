package com.fifty.fifty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.fifty.fifty.domain.Plan;

@Mapper
public interface PlanMapper {
    
    public List<Plan> list() throws Exception;
    
    public int insert(Plan plan) throws Exception;
    
    public int update(Plan plan) throws Exception;

    public int delete(Long no) throws Exception;

}
