package com.fifty.fifty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.fifty.fifty.domain.YoutubeChartData;
import com.fifty.fifty.domain.YoutubeVideoList;

@Mapper
public interface YoutubeChartMapper {
     YoutubeChartData findByVideoId(String videoId);
    void insertChart(YoutubeChartData dto);
    void updateChart(YoutubeChartData dto);
    List<YoutubeChartData> getTodayChart();

    void deleteAllVideos();
    void insertVideos(List<YoutubeVideoList> videos);
    List<YoutubeVideoList> selectAll();

    void deleteAllFanVideos();
    void insertFanVideos(List<YoutubeVideoList> videos);
    List<YoutubeVideoList> selectAllFanVideo();


}
