package com.fifty.fifty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fifty.fifty.domain.CustomUser;
import com.fifty.fifty.domain.Users;
import com.fifty.fifty.service.UserService;

import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "사용자 관리 API")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    @Operation(
        summary = "인증 사용자 정보 조회",
        description = "인증 컨텍스트의 사용자 정보를 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Users.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> userInfo(
        @Parameter(description = "인증된 사용자(principal)")
        @AuthenticationPrincipal CustomUser customUser
    ) {
        log.info("::::: 사용자 정보 조회 :::::");
        log.info("customUser : " + customUser);

        if (customUser == null) {
            log.error("AuthenticationPrincipal에서 CustomUser를 가져올 수 없습니다.");
            return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        }

        Users user = customUser.getUser();
        log.info("user : " + user);

        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("")
    @Operation(
        summary = "회원 가입",
        description = "요청 본문의 사용자 정보를 등록합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "등록 성공",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "요청 본문 오류"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> join(
        @Parameter(description = "등록할 사용자 정보", required = true,
            content = @Content(schema = @Schema(implementation = Users.class)))
        @RequestBody Users user
    ) throws Exception {
        log.info("회원 가입 요청");
        System.out.println(user);
        log.info("회원 가입 요청");
        boolean result = userService.insert(user);

        if (result) {
            log.info("회원가입 성공!");
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } else {
            log.info("회원가입 실패!");
            return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize(" hasRole('ROLE_ADMIN') or #p0.username == authentication.name ")
    @PutMapping("")
    @Operation(
        summary = "회원 정보 수정",
        description = "요청 본문의 사용자 정보를 수정합니다. (권한: 관리자 또는 본인)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "요청 본문 오류"),
        @ApiResponse(responseCode = "401", description = "인증/권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> update(
        @Parameter(description = "수정할 사용자 정보", required = true,
            content = @Content(schema = @Schema(implementation = Users.class)))
        @RequestBody Users user
    ) throws Exception {

        boolean result = userService.update(user);

        if (result) {
            log.info("회원 수정 성공!");
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } else {
            log.info("회원 수정 실패!");
            return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize(" hasRole('ROLE_ADMIN') or #p0 == authentication.name ")
    @DeleteMapping("/{username}")
    @Operation(
        summary = "회원 삭제(탈퇴)",
        description = "식별자(username)에 해당하는 사용자를 삭제합니다. (권한: 관리자 또는 본인)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증/권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> delete(
        @Parameter(description = "사용자 아이디(Username)", example = "aloha", required = true)
        @PathVariable("username") String username
    ) throws Exception {
        try {
            boolean result = userService.delete(username);
            if (result)
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            else
                return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
