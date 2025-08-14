package com.fifty.fifty.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fifty.fifty.service.FilesServiceImpl;

@Component
public class FileCleanScheduler {
    
    @Autowired
    private FilesServiceImpl filesServiceImpl;



    @Scheduled(cron = "00 00 00 * * ?")
    public void scheduledCleanup() throws Exception {
        // filesServiceImpl.cleanupFiles();
    }
}

