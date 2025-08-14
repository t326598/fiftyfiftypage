package com.fifty.fifty.domain;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "MusicChart", description = "음악 플랫폼별 차트 데이터 DTO")
public class MusicChart {

    @Schema(description = "고유 번호 (PK)", example = "1")
    private int no;

    @Schema(description = "플랫폼 이름", example = "Melon")
    private String platform;

    @Schema(description = "곡 제목", example = "Cupid")
    private String songTitle;

    @Schema(description = "아티스트 이름", example = "FIFTY FIFTY")
    private String artist;

    @Schema(description = "오늘의 순위", example = "5")
    private int todayRank;

    @Schema(description = "어제의 순위 (없을 경우 null)", example = "7")
    private Integer yesterdayRank;

    @Schema(description = "순위 변동 (어제 대비)", example = "-2")
    private Integer rankDiff;

    @Schema(description = "스트리밍 수", example = "1234567")
    private int streamCount;

    @Schema(description = "차트 기준 날짜", example = "2025-08-14T12:00:00")
    private LocalDateTime date;
}
