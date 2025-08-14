package com.fifty.fifty.scheduler;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fifty.fifty.service.YoutubeService;


@Service
public class YoutubeSchedulerService {

    @Autowired
    private YoutubeService chartService;


  @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul9")
public void scheduledYoutubeUpdate() throws IOException, InterruptedException {
    chartService.updateYoutubeChart();
}

 private final AtomicBoolean isSchedulerRunning = new AtomicBoolean(false);

    @Scheduled(cron = "0 0 9,13,19,0 * * *", zone = "Asia/Seoul")
    public void updateVideosScheduler() {
      if (isSchedulerRunning.get()) {
            System.out.println("스케줄러: 'VideoUpdateScheduler' 이전 작업이 아직 실행 중입니다. 이번 실행은 건너뜁니다. (" + ZonedDateTime.now() + ")");
            return; 
        }

        try {
            isSchedulerRunning.set(true); 
            System.out.println("스케줄러: 'VideoUpdateScheduler' 작업 시작: " + ZonedDateTime.now());

            chartService.fetchFanMeetUpdateVideos();
            chartService.fetchAndUpdateVideos();

            System.out.println("스케줄러: 'VideoUpdateScheduler' 작업 완료: " + ZonedDateTime.now());

        } catch (Exception e) {
            System.err.println("스케줄러: 'VideoUpdateScheduler' 작업 중 오류 발생: " + e.getMessage());
            e.printStackTrace(); 
        } finally {
            isSchedulerRunning.set(false);
        }
    }


}