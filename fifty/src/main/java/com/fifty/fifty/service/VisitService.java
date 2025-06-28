package com.fifty.fifty.service;

import java.time.LocalDate;
import java.util.List;


import com.fifty.fifty.domain.Visit;
import com.fifty.fifty.domain.VisitSummary;


public interface VisitService {
    
    public void saveVisit(boolean isNewVisitor);
    
    public VisitSummary getTodayStats();
   public VisitSummary getTotalStats();
   public List<Visit> getStatsByDate(LocalDate startDate, LocalDate endDate);
}
