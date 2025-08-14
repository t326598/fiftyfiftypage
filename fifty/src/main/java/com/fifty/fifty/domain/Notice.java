package com.fifty.fifty.domain;

import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Notice", description = "공지사항 DTO")
public class Notice {

    @Schema(description = "공지사항 고유 번호 (PK)", example = "1")
    private Long no;

    @Schema(description = "첨부 파일 번호 (FK)", example = "101")
    private Long fileNo;

    @Schema(description = "공지사항 제목", example = "서버 점검 안내")
    private String title;

    @Schema(description = "공지사항 내용", example = "2025년 8월 15일 오전 2시부터 4시까지 서버 점검이 진행됩니다.")
    private String content;

    @Schema(description = "공지 등록일", example = "2025-08-14T10:30:00")
    private Date createdAt;

    @Schema(description = "공지 수정일", example = "2025-08-14T15:00:00")
    private Date updatedAt;
}
