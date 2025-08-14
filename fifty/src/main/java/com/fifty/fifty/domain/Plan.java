package com.fifty.fifty.domain;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Plan {

    @Schema(description = "고유 번호", example = "1")
    private Long no;

    @Schema(description = "카테고리 코드", example = "1001")
    private Long crt;

    @Schema(description = "제목", example = "회의 일정")
    private String title;

    @Schema(description = "내용", example = "주간 회의 진행")
    private String content;

    @Schema(description = "첨부 파일 번호", example = "10")
    private Long fileNo;

    @Schema(description = "시작 시간", example = "2025-08-12T10:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startAt;

    @Schema(description = "종료 시간", example = "2025-08-12T11:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endAt;

    @Schema(description = "수정 일자", example = "2025-08-12")
    private Date updatedAt;

    @Schema(description = "등록 일자", example = "2025-08-12")
    private Date createdAt;
}