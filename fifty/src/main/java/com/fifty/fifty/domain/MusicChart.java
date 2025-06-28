package com.fifty.fifty.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MusicChart {
    private int no;
    private String platform;
    private String songTitle;
    private String artist;
    private int todayRank;
    private Integer yesterdayRank;
    private Integer rankDiff;
    private int streamCount;
    private LocalDateTime date;
}
