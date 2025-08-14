package com.fifty.fifty.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "YoutubeChartData", description = "YouTube 차트 영상 데이터 엔티티")
public class YoutubeChartData {

    @Schema(description = "고유 번호 (PK)", example = "1")
    private int no;

    @Schema(description = "유튜브 영상 ID", example = "dQw4w9WgXcQ")
    private String videoId;

    @Schema(description = "영상 제목", example = "FIFTY FIFTY - Cupid (Official MV)")
    private String title;

    @Schema(description = "썸네일 이미지 URL", example = "https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg")
    private String thumbnailUrl;

    @Schema(description = "유튜브 영상 URL", example = "https://www.youtube.com/watch?v=dQw4w9WgXcQ")
    private String videoUrl;

    @Schema(description = "현재 조회수", example = "123456789")
    private long viewCount;

    @Schema(description = "이전 조회수 (변화량 계산용)", example = "120000000")
    private long previousViewCount;

    @Schema(description = "영상 업로드 시간 (ISO 8601 형식)", example = "2025-08-10T15:30:00Z")
    private String publishedAt;
}
