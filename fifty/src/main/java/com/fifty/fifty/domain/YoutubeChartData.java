package com.fifty.fifty.domain;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class YoutubeChartData {
    private int no;
    private String videoId;
    private String title;
    private String thumbnailUrl;
    private String videoUrl;
    private long viewCount;
    private long previousViewCount;
    private String publishedAt;

}
