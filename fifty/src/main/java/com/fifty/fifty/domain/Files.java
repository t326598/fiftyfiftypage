package com.fifty.fifty.domain;

import java.util.Date;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "파일 메타 정보")
public class Files {
    
    @Schema(description = "고유 식별자(PK)", example = "1")
    private Long no;

    @Schema(description = "랜덤 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "카테고리/분류 코드", example = "image")
    private String crt;

    @Schema(description = "저장 경로(S3/로컬)", 
            example = "https://bucket.s3.amazonaws.com/uploads/file.jpg")
    private String path;

    @Schema(description = "파일 이름", example = "profile.png")
    private String name;

    @Schema(description = "파일 크기(byte)", example = "204800")
    private Long size;

    @Schema(description = "실제 업로드/촬영일 (yyyy-MM-dd)", example = "2025-08-01")
    private String trueDay;

    @Schema(description = "레코드 생성 일시", example = "2025-08-14T10:15:30")
    private Date createdAt; 

    @Schema(description = "업로드할 파일 데이터 (폼 전송 시 사용)", type = "string", format = "binary")
    private MultipartFile data;

    public Files(){
        this.id = UUID.randomUUID().toString();
    }
}
