package com.fifty.fifty.domain;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "YouTube 동영상 상세 정보 DTO")
public class YoutubeVideoList {

    @Schema(description = "유튜브 비디오 ID", example = "dQw4w9WgXcQ")
    private String videoId;

    @Schema(description = "비디오 제목", example = "Never Gonna Give You Up")
    private String title;

    @Schema(description = "채널 ID", example = "UC38IQsAvIsxxjztdMZQtwHA")
    private String channelId;

    @Schema(description = "채널 이름", example = "RickAstleyVEVO")
    private String channelTitle;

    @Schema(description = "업로드 일자", example = "2009-10-25T15:30:00")
    private LocalDateTime publishDate;

    @Schema(description = "채널 구독자 수", example = "3500000")
    private Integer subscriberCount;

    @Schema(description = "비디오 조회수", example = "1284930200")
    private Integer viewCount;

    @Schema(description = "썸네일 URL", example = "https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg")
    private String thumbnailUrl;
}
