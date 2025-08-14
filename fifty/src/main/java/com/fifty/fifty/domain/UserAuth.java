package com.fifty.fifty.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "UserAuth", description = "사용자 권한 정보를 담는 DTO")
public class UserAuth {

    @Schema(description = "권한 고유 번호 (PK)", example = "1")
    private Long no;

    @Schema(description = "사용자 아이디", example = "aloha123")
    private String username;

    @Schema(description = "권한명 (ROLE_USER, ROLE_ADMIN 등)", example = "ROLE_USER")
    private String auth;
}
