package com.fifty.fifty.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fifty.fifty.domain.Visit;
import com.fifty.fifty.domain.VisitSummary;
import com.fifty.fifty.mapper.VisitMapper;

@Service
public class VisitServiceImpl implements VisitService {

    @Autowired
    private VisitMapper visitMapper;

        @Override
    public void saveVisit(boolean isNewVisitor) {
        VisitSummary today = visitMapper.getTodayStats();

        if(today == null){
            visitMapper.insert();
        } else {
            visitMapper.updateTotal();
            if(isNewVisitor){
                visitMapper.updateUnique();
            }
        }

    }

    @Override
    public VisitSummary getTodayStats() {
        return visitMapper.getTodayStats();
    }

    @Override
    public VisitSummary getTotalStats() {
        return visitMapper.getTotalStats();
    }

    @Override
    public List<Visit> getStatsByDate(LocalDate startDate, LocalDate endDate) {
        return visitMapper.getStatsByDate(startDate, endDate);
    }


    
}
