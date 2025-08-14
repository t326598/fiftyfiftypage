package com.fifty.fifty.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fifty.fifty.domain.YoutubeChartData;
import com.fifty.fifty.domain.YoutubeVideo;
import com.fifty.fifty.mapper.YoutubeChartMapper;

@Service
public class YoutubeSchedulerService {

    @Autowired
    private YoutubeService chartService;
    @Autowired
    private YoutubeChartMapper chartMapper;

@Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시
public void scheduledYoutubeUpdate() throws IOException, InterruptedException {
    chartService.updateYoutubeChart();
}
}