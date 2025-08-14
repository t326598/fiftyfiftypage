package com.fifty.fifty.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fifty.fifty.domain.YoutubeChartData;
import com.fifty.fifty.service.YoutubeSchedulerService;
import com.fifty.fifty.service.YoutubeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/chart/youtube")
@Tag(name = "YouTube Chart", description = "유튜브 차트 조회/갱신 API")
public class YoutubeChartController {

    @Autowired
    private YoutubeService chartService;
    @Autowired
    private YoutubeSchedulerService schedulerService;

    @GetMapping("/today")
    @Operation(
        summary = "오늘의 유튜브 차트 조회",
        description = "수집된 데이터 기준으로 오늘의 유튜브 차트 목록을 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = YoutubeChartData.class)))),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> getTodayChart() {
        try {
            List<YoutubeChartData> musicList = chartService.getTodayChart();
            return new ResponseEntity<>(musicList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetch")
    @Operation(
        summary = "유튜브 차트 수동 수집/갱신",
        description = "유튜브 상위 영상을 조회하여 내부 차트 데이터를 수동으로 갱신합니다. (운영 환경에서는 권한/접근 제어 권장)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수동 업데이트 결과 메시지",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public String fetchYoutubeChartManually() {
        try {
            List<YoutubeChartData> videos = chartService.fetchTop10YoutubeVideos();

            for (YoutubeChartData video : videos) {
                System.out.printf("제목: %s, 조회수: %d, URL: %s%n",
                    video.getTitle(), video.getViewCount(), video.getVideoUrl());
            }

            chartService.updateYoutubeChart();

            return "YouTube 차트 수동 업데이트 완료";
        } catch (Exception e) {
            e.printStackTrace();
            return "업데이트 중 오류 발생: " + e.getMessage();
        }
    }
}
