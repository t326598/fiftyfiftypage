package com.fifty.fifty.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Visit {
    
    private LocalDate visitDate;
    private int totalCount;
    private int uniqueCount;


}
