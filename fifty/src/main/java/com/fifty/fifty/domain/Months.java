package com.fifty.fifty.domain;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "월별 배경 이미지 및 정보 DTO")
public class Months {

    @Schema(description = "고유 ID", example = "1")
    private int id;

    @Schema(description = "월 (1~12)", example = "5")
    private int month;

    @Schema(description = "배경 이미지 URL", example = "https://example.com/images/may-bg.jpg")
    private String imageUrl;

    @Schema(description = "이전 파일 경로 (업데이트 시 사용)", example = "old_bg_may.jpg")
    private String oldFilePath;

    @Schema(description = "업로드할 이미지 파일 (multipart/form-data 로 전송)", type = "string", format = "binary")
    private MultipartFile data;
}
