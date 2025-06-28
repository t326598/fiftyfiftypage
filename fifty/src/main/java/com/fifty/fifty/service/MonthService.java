package com.fifty.fifty.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.fifty.fifty.domain.Months;

public interface MonthService {
    List<Months> calendarList();

    Months getBackgroundByMonth(@Param("month") int month);

    int monthsUpdate(Months months);
}
