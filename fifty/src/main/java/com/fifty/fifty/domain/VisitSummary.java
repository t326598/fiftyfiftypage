package com.fifty.fifty.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class VisitSummary {
    
    private LocalDate visitDate;
    private int totalCount;
    private int uniqueCount;

}
