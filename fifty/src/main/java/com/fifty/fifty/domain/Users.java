package com.fifty.fifty.domain;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(name = "Users", description = "회원(사용자) 정보를 담는 엔티티")
public class Users {
    
    @Schema(description = "사용자 고유 번호 (PK)", example = "1")
    private Long no;

    @Schema(description = "랜덤 UUID (DB 저장용 식별자)", example = "550e8400-e29b-41d4-a716-446655440000")
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Schema(description = "사용자 로그인 아이디", example = "aloha123")
    private String username;

    @Schema(description = "비밀번호", example = "123456")
    private String password;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @Schema(description = "이메일 주소", example = "aloha@example.com")
    private String email;

    @Schema(description = "소셜 로그인 제공자 (local, naver, kakao, google 등)", example = "local")
    private String provider;

    @Schema(description = "계정 생성 일자", example = "2025-08-14T10:15:30")
    private Date createdAt;

    @Schema(description = "계정 수정 일자", example = "2025-08-14T10:15:30")
    private Date updatedAt;

    @Schema(description = "계정 활성화 여부", example = "true")
    private Boolean enabled;

    @Schema(description = "사용자 권한 목록")
    private List<UserAuth> authList;

    public Users() {
        this.id = UUID.randomUUID().toString();
        this.provider = "local";
    }
}
