package com.fifty.fifty.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "YoutubeVideo", description = "YouTube 영상 간단 정보 DTO")
public class YoutubeVideo {

    @Schema(description = "유튜브 영상 ID", example = "dQw4w9WgXcQ")
    private String videoId;

    @Schema(description = "영상 제목", example = "FIFTY FIFTY - Cupid (Official MV)")
    private String title;

    @Schema(description = "썸네일 이미지 URL", example = "https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg")
    private String thumbnailUrl;
}
