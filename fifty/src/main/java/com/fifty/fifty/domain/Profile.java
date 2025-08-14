package com.fifty.fifty.domain;

import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Profile", description = "사용자 프로필 DTO")
public class Profile {

    @Schema(description = "프로필 고유 번호 (PK)", example = "1")
    private Long no;

    @Schema(description = "프로필 이미지 파일 경로", example = "profile/2025/08/14/abcd1234_img.png")
    private String filePath;

    @Schema(description = "프로필 내용", example = "안녕하세요, 저는 음악을 좋아하는 개발자입니다.")
    private String content;

    @Schema(description = "프로필 제목", example = "백엔드 개발자")
    private String title;

    @Schema(description = "보조 설명", example = "Spring Boot와 React를 활용한 서비스 개발")
    private String subContent;

    @Schema(description = "이전 프로필 이미지 경로 (수정 시 기존 파일 삭제용)", 
            example = "profile/2025/07/old_img.png")
    private String oldFilePath;

    @Schema(description = "업로드할 파일 (Multipart 형식)", type = "string", format = "binary")
    private MultipartFile data;
}
