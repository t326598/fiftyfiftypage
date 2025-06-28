package com.fifty.fifty.mapper;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fifty.fifty.domain.Visit;
import com.fifty.fifty.domain.VisitSummary;

@Mapper
public interface VisitMapper {
    
   public int insert();
   public int updateTotal();
   public int updateUnique();
    

   public VisitSummary getTodayStats();
   public VisitSummary getTotalStats();
   public List<Visit> getStatsByDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


}
