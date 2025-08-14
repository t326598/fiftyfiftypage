package com.fifty.fifty.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.fifty.fifty.domain.Plan;
import com.fifty.fifty.service.PlanServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/plan")
@Slf4j
@Tag(name = "Plan", description = "일정(Plan) 관리 API")
public class PlanController {

    @Autowired
    private PlanServiceImpl planServiceImpl;

    @Operation(summary = "모든 계획 조회", description = "저장된 모든 Plan을 조회해 이벤트 목록 형태로 반환합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공적으로 이벤트 리스트 반환",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Map.class)))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/plans")
    public ResponseEntity<?> getAll() {
        try {
            List<Plan> planList = planServiceImpl.list();

            List<Map<String, Object>> eventList = planList.stream().map(plan -> {
                Map<String, Object> map = new HashMap<>();
                map.put("no", plan.getNo());
                map.put("crt", plan.getCrt());
                map.put("title", plan.getTitle());
                map.put("start", plan.getStartAt().toString());
                map.put("end", plan.getEndAt() != null ? plan.getEndAt().toString() : null);
                map.put("backgroundColor", getCategoryColor(plan.getCrt()));
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(eventList);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String getCategoryColor(Long crt) {
        if (crt == null) return "#adb5bd";
        switch (crt.intValue()) {
            case 1: return "#ff6b81";
            case 2: return "#4dabf7";
            case 3: return "#51cf66";
            case 4: return "#f4a261";
            case 5: return "#ff7f50";
            case 6: return "#8e44ad";
            default: return "#adb5bd";
        }
    }
    
    @Operation(summary = "계획 생성", description = "요청 본문의 Plan 정보를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "생성 성공",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "요청 본문 오류"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping()
    public ResponseEntity<?> create(
        @Parameter(description = "생성할 계획 정보", required = true,
            content = @Content(schema = @Schema(implementation = Plan.class)))
        @RequestBody Plan plan
    ) {
        try {
            int result = planServiceImpl.insert(plan);
            if(result > 0){
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
            return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "계획 수정", description = "요청 본문의 Plan 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "요청 본문 오류"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping()
    public ResponseEntity<?> update(
        @Parameter(description = "수정할 계획 정보", required = true,
            content = @Content(schema = @Schema(implementation = Plan.class)))
        @RequestBody Plan plan
    ) {
        try {
            int result = planServiceImpl.update(plan);
            if(result > 0){
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
            return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "계획 삭제", description = "식별자(no)에 해당하는 계획을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{no}")
    public ResponseEntity<?> destroy(
        @Parameter(description = "계획 식별자", example = "1", required = true)
        @PathVariable Long no
    ) {
        try {
            int result = planServiceImpl.delete(no);
            if(result > 0){
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
            return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
