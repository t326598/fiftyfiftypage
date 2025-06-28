package com.fifty.fifty.service;

import java.util.List;


import com.fifty.fifty.domain.Plan;

public interface PlanService {
    
     public List<Plan> list() throws Exception;
    
    public int insert(Plan plan) throws Exception;
    
    public int update(Plan plan) throws Exception;

    public int delete(Long no) throws Exception;

}
