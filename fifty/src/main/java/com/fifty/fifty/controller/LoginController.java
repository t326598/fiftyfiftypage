package com.fifty.fifty.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fifty.fifty.config.security.constants.SecurityConstants;
import com.fifty.fifty.config.security.props.JwtProps;
import com.fifty.fifty.domain.AuthenticationRequest;
import com.fifty.fifty.domain.Users;
import com.fifty.fifty.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@Tag(name = "Login", description = "로그인 및 JWT 토큰 발급/해석 API")
public class LoginController {

    @Autowired
    private JwtProps jwtProps;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(
        summary = "로그인 요청",
        description = "아이디와 비밀번호를 인증하고 JWT 토큰을 발급합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "JWT 토큰 발급 성공",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> login(
        @Parameter(description = "로그인 요청 정보", required = true,
                   content = @Content(schema = @Schema(implementation = AuthenticationRequest.class)))
        @RequestBody AuthenticationRequest authReq
    ) throws Exception {
        String username = authReq.getId();
        String password = authReq.getPassword();
        log.info("username : " + username);
        log.info("password : " + password);

        Users user = userService.select(username);
        String jwt = createJwtToken(username, user.getId());

        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    private String createJwtToken(String username, String id) {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");

        String secretKey = jwtProps.getSecretKey();
        byte[] signingKey = secretKey.getBytes();

        int day5 = 1000 * 60 * 60 * 24 * 5;
        String jwt = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(signingKey), Jwts.SIG.HS512)
                .header()
                .add("typ", SecurityConstants.TOKEN_TYPE)
                .and()
                .claim("id", id)
                .claim("username", username)
                .claim("rol", roles)
                .expiration(new Date(System.currentTimeMillis() + day5))
                .compact();
        log.info("jwt : " + jwt);

        return jwt;
    }

    @GetMapping("/user")
    @Operation(
        summary = "JWT 토큰 해석",
        description = "Authorization 헤더의 JWT 토큰을 해석하여 사용자 정보를 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "JWT 토큰 해석 성공",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> user(
        @Parameter(description = "Authorization 헤더 (Bearer JWT)", example = "Bearer eyJhbGciOiJIUzUxMiJ9...")
        @RequestHeader(name = "Authorization") String authorization
    ) {
        log.info("Authrization : " + authorization);

        String jwt = authorization.substring(7);
        log.info("jwt : " + jwt);

        String secretKey = jwtProps.getSecretKey();
        byte[] signingKey = secretKey.getBytes();

        Jws<Claims> parsedToken = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(signingKey))
                .build()
                .parseSignedClaims(jwt);

        String username = parsedToken.getPayload().get("id").toString();
        log.info("username : " + username);

        Object roles = parsedToken.getPayload().get("rol");
        List<String> roleList = (List<String>) roles;
        log.info("roles : " + roles);
        log.info("roleList : " + roleList);

        return new ResponseEntity<>(parsedToken.toString(), HttpStatus.OK);
    }
}
