package com.fifty.fifty.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fifty.fifty.domain.Notice;
import com.fifty.fifty.service.NoticeServiceImpl;

import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/notice")
@Tag(name = "Notice", description = "공지사항 관리 API")
public class NoticeController {

    @Autowired
    private NoticeServiceImpl noticeServiceImpl;

    @GetMapping()
    @Operation(summary = "공지 전체 조회", description = "등록된 모든 공지사항을 반환합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Notice.class)))),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> getAll() {
        try {
            List<Notice> noticeList = noticeServiceImpl.list();
            return new ResponseEntity<>(noticeList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{no}")
    @Operation(summary = "공지 단건 조회", description = "식별자(no)에 해당하는 공지사항을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Notice.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> getOne(
        @Parameter(description = "공지사항 식별자", example = "1", required = true)
        @PathVariable Long no
    ) {
        try {
            Notice notice = noticeServiceImpl.selecetByNo(no);
            return new ResponseEntity<>(notice, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping()
    @Operation(summary = "공지 생성", description = "요청 본문의 공지 정보를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "생성 성공",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "요청 본문 오류"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> create(
        @Parameter(description = "생성할 공지 정보", required = true,
            content = @Content(schema = @Schema(implementation = Notice.class)))
        @RequestBody Notice notice
    ) {
        try {
            int result = noticeServiceImpl.insert(notice);
            if(result > 0){
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
            return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping()
    @Operation(summary = "공지 수정", description = "요청 본문의 공지 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "요청 본문 오류"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> update(
        @Parameter(description = "수정할 공지 정보", required = true,
            content = @Content(schema = @Schema(implementation = Notice.class)))
        @RequestBody Notice notice
    ) {
        try {
            int result = noticeServiceImpl.update(notice);
            if(result > 0){
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
            return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{no}")
    @Operation(summary = "공지 삭제", description = "식별자(no)에 해당하는 공지사항을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> destroy(
        @Parameter(description = "공지사항 식별자", example = "1", required = true)
        @PathVariable Long no
    ) {
        try {
            int result = noticeServiceImpl.delete(no);
            if(result > 0){
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
            return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
