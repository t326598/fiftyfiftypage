package com.fifty.fifty.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fifty.fifty.domain.Months;
import com.fifty.fifty.service.MonthServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/months")
@Slf4j
@Tag(name = "Months", description = "월별 배경/캘린더 관리 API")
public class MonthController {
    
    @Autowired
    private MonthServiceImpl monthServiceImpl;

    @GetMapping("/list")
    @Operation(
        summary = "월별 캘린더 목록 조회",
        description = "전체 월(Months) 목록을 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Months.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> getMethodName() {
        List<Months> monthList = monthServiceImpl.calendarList();
        return new ResponseEntity<>(monthList, HttpStatus.OK);
    }

    @GetMapping("")
    @Operation(
        summary = "월별 배경 조회",
        description = "month 파라미터로 해당 월의 배경 정보를 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Months.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> getMethodName(
        @Parameter(description = "조회할 월(1~12)", example = "8", required = true)
        @RequestParam("month") int month
    ) {
        try{
            Months url = monthServiceImpl.getBackgroundByMonth(month);
            return new ResponseEntity<>(url, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping()
    @Operation(
        summary = "월별 배경 수정",
        description = "요청 본문의 Months 정보를 기반으로 배경 이미지를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "요청 본문 오류"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> updateBackground(
        @Parameter(description = "수정할 월별 배경 정보", required = true,
            content = @Content(schema = @Schema(implementation = Months.class)))
        @RequestBody Months request
    ) {
        try {
            System.out.println("왜안나오나요??" + request);
            if (request.getOldFilePath() != null && !request.getOldFilePath().equals(request.getImageUrl())) {
                Path oldFile = Paths.get("/path/to/upload/dir", request.getOldFilePath());
                Files.deleteIfExists(oldFile);
            }
            int result = monthServiceImpl.monthsUpdate(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 수정 실패");
        }
    }
}
