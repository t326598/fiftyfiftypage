package com.fifty.fifty.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fifty.fifty.domain.YoutubeVideoList;
import com.fifty.fifty.mapper.YoutubeChartMapper;

@RequestMapping("/video")
@RestController
public class YoutubueVideoController {
    
    @Autowired
    private YoutubeChartMapper youtubeVideoMapper;



    @GetMapping()
    public List<YoutubeVideoList> getVideos() {
        return youtubeVideoMapper.selectAll();
    }
    @GetMapping("/fan")
    public List<YoutubeVideoList> getFanVideos() {
        return youtubeVideoMapper.selectAllFanVideo();
    }

}
