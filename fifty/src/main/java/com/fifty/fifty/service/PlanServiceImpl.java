package com.fifty.fifty.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fifty.fifty.domain.Plan;
import com.fifty.fifty.mapper.PlanMapper;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PlanServiceImpl implements PlanService {

    @Autowired
    private PlanMapper planMapper;

    @Override
    public List<Plan> list() throws Exception {
        List<Plan> planList = planMapper.list();
        return planList;
    }

    @Override
    public int insert(Plan plan) throws Exception {
       int result = planMapper.insert(plan);
       return result;
    }

    @Override
    public int update(Plan plan) throws Exception {
        int result = planMapper.update(plan);
       return result;
    }

    @Override
    public int delete(Long no) throws Exception {
           int result = planMapper.delete(no);
       return result;
    }
    
}
