package com.fifty.fifty.domain;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Plan {
    private Long no;
    private Long crt;
    private String title;
    private String content;
    private Long fileNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startAt;      // 시작 시간 (FullCalendar 대응)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endAt;        // 종료 시간 (FullCalendar 대응)
    private Date updatedAt;
    private Date createdAt;
}
