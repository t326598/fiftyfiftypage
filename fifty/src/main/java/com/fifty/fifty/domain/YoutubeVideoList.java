package com.fifty.fifty.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class YoutubeVideoList {
        private String videoId;
    private String title;
    private String channelId;
    private String channelTitle;
    private LocalDateTime publishDate;
    private Integer subscriberCount;
    private Integer viewCount;
    private String thumbnailUrl;
}
