package com.fifty.fifty.domain;

import java.util.Date;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "파일 업로드 및 경로 관리 DTO")
public class FilePath {

    @Schema(description = "고유 번호", example = "1")
    private Long no;

    @Schema(description = "UUID 기반 파일 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "구분자 (카테고리/분류 등)", example = "notice")
    private String crt;

    @Schema(description = "저장된 파일 경로", example = "/uploads/2025/08/14/sample.png")
    private String path;

    @Schema(description = "원본 파일명", example = "sample.png")
    private String name;

    @Schema(description = "파일 크기 (bytes)", example = "204800")
    private Long size;

    @Schema(description = "파일 업로드 날짜(yyyy-MM-dd)", example = "2025-08-14")
    private String trueDay;

    @Schema(description = "생성일", example = "2025-08-14T10:15:30")
    private Date createdAt;

    @Schema(description = "수정 시 이전 파일 경로", example = "old_sample.png")
    private String oldFilePath;

    @Schema(description = "업로드할 파일 (multipart/form-data 로 전송)", 
            type = "string", format = "binary")
    private MultipartFile data;

    public FilePath() {
        this.id = UUID.randomUUID().toString();
    }
}
