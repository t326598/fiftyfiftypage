package com.fifty.fifty.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fifty.fifty.service.FilesServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/files")
@Slf4j
@Tag(name = "Files", description = "파일 경로/메타 관리 API")
public class FilesController {

    @Autowired
    private FilesServiceImpl filesService;

    @GetMapping
    @Operation(
        summary = "파일 목록 조회",
        description = "조건(crt, trueDay)과 페이징(page, rows)으로 파일 목록과 전체 개수를 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공", 
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> getFiles(
            @Parameter(description = "분류/카테고리 필터", example = "image")
            @RequestParam(name = "crt", required = false) String crt,
            @Parameter(description = "실제 촬영/생성 일자(yyyy-MM-dd) 필터", example = "2025-08-01")
            @RequestParam(name = "trueDay", required = false) String trueDay,
            @Parameter(description = "0부터 시작하는 페이지 번호", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지당 행 수", example = "10")
            @RequestParam(name = "rows", defaultValue = "10") int rows) throws Exception {

        long startTime = System.nanoTime(); // 처리 시작 시간

        int start = page * rows;

        Map<String, Object> params = new HashMap<>();
        params.put("crt", crt);
        params.put("trueDay", trueDay);
        params.put("start", start);
        params.put("rows", rows);

        List<FilePath> list = filesService.list(params);
        long listStart = System.nanoTime();

        long listTime = System.nanoTime() - listStart;
        System.out.println("list() 처리 시간: " + (listTime / 1_000_000.0) + "ms");

        long countStart = System.nanoTime();
        long countTime = System.nanoTime() - countStart;
        System.out.println("count() 처리 시간: " + (countTime / 1_000_000.0) + "ms");

        int total = filesService.mainCount(params);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);

        long endTime = System.nanoTime(); // 처리 종료 시간
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        System.out.println("[API] GET /files 처리 시간: " + elapsedMs + "ms");

        return ResponseEntity.ok(result);
    }

    @PostMapping("")
    @Operation(
        summary = "파일 메타 생성",
        description = "폼 데이터로 전달된 FilePath 정보를 생성합니다. (S3 경로나 로컬 경로 등)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "생성 성공", 
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "요청 파라미터 오류"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> create(
            @Parameter(description = "파일 메타 정보 (폼 데이터)", required = true)
            @ModelAttribute FilePath file) {
        try {
            int result = filesService.insert(file);

            if (result > 0) {
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update")
    @Operation(
        summary = "파일 메타 수정",
        description = "폼 데이터로 전달된 FilePath 정보를 수정합니다. 기존 경로가 S3 URL이면 S3 객체도 정리합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공", 
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "요청 파라미터 오류"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> update(
            @Parameter(description = "수정할 파일 메타 정보 (폼 데이터)", required = true)
            @ModelAttribute FilePath file) {
        try {

            FilePath existingFile = filesService.select(file.getNo()); // DB에서 기존 FilePath 레코드를 조회

            if (existingFile != null && existingFile.getPath() != null
                    && existingFile.getPath().startsWith("https://")) {

                filesService.deleteFileFromS3(existingFile.getPath());
            }

            int result = filesService.update(file);

            if (result > 0) {
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            System.err.println("파일 업데이트 중 서버 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{no}")
    @Operation(
        summary = "파일 메타 삭제",
        description = "식별자(no)에 해당하는 파일 메타 정보를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공", 
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> destroy(
            @Parameter(description = "파일 메타 식별자", example = "123", required = true)
            @PathVariable Long no) {
        try {
            int result = filesService.delete(no);
            if (result > 0) {
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}