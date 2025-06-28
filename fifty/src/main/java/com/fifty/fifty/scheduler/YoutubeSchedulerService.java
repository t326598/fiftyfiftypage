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


@Scheduled(cron = "0 00 9 * * *") // 매일 오전 9시
public void scheduledYoutubeUpdate() throws IOException, InterruptedException {
    chartService.updateYoutubeChart();
}

 private final AtomicBoolean isSchedulerRunning = new AtomicBoolean(false);

    @Async
    @Scheduled(cron = "0 27 23 * * *")
    public void updateVideosScheduler() {
      if (isSchedulerRunning.get()) {
            System.out.println("스케줄러: 'VideoUpdateScheduler' 이전 작업이 아직 실행 중입니다. 이번 실행은 건너뜁니다. (" + ZonedDateTime.now() + ")");
            return; 
        }

        try {
            isSchedulerRunning.set(true); // ⭐ 스케줄러 작업 시작을 표시합니다.
            System.out.println("스케줄러: 'VideoUpdateScheduler' 작업 시작: " + ZonedDateTime.now());

            // 실제 비디오 업데이트 로직을 수행하는 서비스 메서드를 호출합니다.
            chartService.fetchAndUpdateVideos();
            chartService.fetchFanMeetUpdateVideos();

            System.out.println("스케줄러: 'VideoUpdateScheduler' 작업 완료: " + ZonedDateTime.now());

        } catch (Exception e) {
            System.err.println("스케줄러: 'VideoUpdateScheduler' 작업 중 오류 발생: " + e.getMessage());
            e.printStackTrace(); // 오류 발생 시 스택 트레이스를 출력합니다.
        } finally {
            isSchedulerRunning.set(false); // ⭐ 스케줄러 작업이 성공하든 실패하든, 완료되었음을 표시합니다.
        }
    }


}