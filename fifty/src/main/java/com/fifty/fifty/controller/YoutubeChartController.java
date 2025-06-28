package com.fifty.fifty.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.fifty.fifty.domain.YoutubeChartData;
import com.fifty.fifty.scheduler.YoutubeSchedulerService;
import com.fifty.fifty.service.YoutubeService;

@RestController
@RequestMapping("/api/chart/youtube")
public class YoutubeChartController {

    @Autowired
    private YoutubeService chartService;
    @Autowired
    private YoutubeSchedulerService schedulerService;


    @GetMapping("/today")
    public ResponseEntity<?> getTodayChart() {
        try {
            List<YoutubeChartData> musicList = chartService.getTodayChart();
            return new ResponseEntity<>(musicList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetch")
    public String fetchYoutubeChartManually() {
        try {
            // 1. 유튜브 영상 리스트 받아오기
            List<YoutubeChartData> videos = chartService.fetchTop10YoutubeVideos();

            for (YoutubeChartData video : videos) {
                System.out.printf("제목: %s, 조회수: %d, URL: %s%n",
                    video.getTitle(), video.getViewCount(), video.getVideoUrl());
            }

            // 3. DB 업데이트 실행
            chartService.updateYoutubeChart();

            return "YouTube 차트 수동 업데이트 완료";
        } catch (Exception e) {
            e.printStackTrace();
            return "업데이트 중 오류 발생: " + e.getMessage();
        }
    }

}
