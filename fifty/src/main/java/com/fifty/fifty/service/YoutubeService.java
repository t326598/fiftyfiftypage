package com.fifty.fifty.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fifty.fifty.domain.YoutubeChartData;
import com.fifty.fifty.domain.YoutubeVideoList;
import com.fifty.fifty.mapper.YoutubeChartMapper;

@Service
public class YoutubeService {

    private static List<String> keywords = List.of("FIFTY FIFTY", "fiftyfifty", "피프티", "피프티피프티");
    private static List<String> singleFiftyKeywords = List.of("fifty");
    private static List<String> excludeKeywords = List.of("어블름", "ablume", "EBUBE", "Dhaliwal", "starwars", "느좋");
    private static final int SUBSCRIBER_THRESHOLD = 10_000;

    @Autowired
    private YoutubeChartMapper youtubeMapper;

    private RestTemplate restTemplate;

    public List<YoutubeChartData> getTodayChart() {
        return youtubeMapper.getTodayChart();
    }

    public void updateYoutubeChart() throws IOException, InterruptedException {
        List<YoutubeChartData> videos = fetchTop10YoutubeVideos();
        System.out.println(videos.size());
        for (int i = 0; i < videos.size(); i++) {
            YoutubeChartData video = videos.get(i);

            // 1) HTML 엔티티 디코딩
            String decodedTitle = StringEscapeUtils.unescapeHtml4(video.getTitle());
            video.setTitle(decodedTitle);

            video.setNo(i + 1);

            System.out.println("이거나옴?");

            youtubeMapper.updateChart(video);

        }
    }

    @Value("${youtube.api.key}")
    private String apiKey;

    public List<YoutubeChartData> fetchTop10YoutubeVideos() throws IOException, InterruptedException {
        String publishedAfter = "2024-09-24T00:00:00Z";
        int maxResultsPerKeyword = 20;
        int maxResultsToReturn = 10;

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        List<YoutubeChartData> result = new ArrayList<>();
        List<String> collectedIds = new ArrayList<>();

        for (String keyword : keywords) {
            String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="
                    + URLEncoder.encode(keyword, StandardCharsets.UTF_8)
                    + "&type=video&maxResults=" + maxResultsPerKeyword
                    + "&order=viewCount&publishedAfter=" + publishedAfter
                    + "&key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());

            for (JsonNode item : root.get("items")) {
                String videoId = item.get("id").get("videoId").asText();

                if (collectedIds.contains(videoId))
                    continue;

                String title = item.get("snippet").get("title").asText().toLowerCase();
                boolean excluded = excludeKeywords.stream()
                        .map(String::toLowerCase)
                        .anyMatch(kw -> title.toLowerCase().contains(kw));
                String publishedAtRaw = item.get("snippet").get("publishedAt").asText();
                String publishedAtFormatted = publishedAtRaw.replace("T", " ").replace("Z", "");

                if (excluded)
                    continue;

                YoutubeChartData video = new YoutubeChartData();
                video.setVideoId(videoId);
                video.setTitle(item.get("snippet").get("title").asText());
                          String decodedTitle = StringEscapeUtils.unescapeHtml4(video.getTitle());
            video.setTitle(decodedTitle);
                video.setThumbnailUrl(item.get("snippet").get("thumbnails").get("high").get("url").asText());
                video.setPublishedAt(publishedAtFormatted);
                video.setVideoUrl("https://www.youtube.com/watch?v=" + videoId);

                // 🔽 조회수 추가로 받아오기 (Videos API로)
                String statsUrl = "https://www.googleapis.com/youtube/v3/videos?part=statistics&id="
                        + videoId + "&key=" + apiKey;
                HttpRequest statsRequest = HttpRequest.newBuilder()
                        .uri(URI.create(statsUrl))
                        .build();
                HttpResponse<String> statsResponse = client.send(statsRequest, HttpResponse.BodyHandlers.ofString());
                JsonNode statsRoot = mapper.readTree(statsResponse.body());

                JsonNode stats = statsRoot.get("items").get(0).get("statistics");
                long viewCount = stats.has("viewCount") ? stats.get("viewCount").asLong() : 0;
                video.setViewCount(viewCount);

                result.add(video);
                collectedIds.add(videoId);

                if (result.size() >= maxResultsToReturn)
                    break;
            }
            if (result.size() >= maxResultsToReturn)
                break;
        }

        return result;
    }

    public YoutubeService() {
        this.restTemplate = new RestTemplate();
    }


    
        public void fetchAndUpdateVideos() {
         List<YoutubeVideoList> allCandidateVideos = new ArrayList<>(); // 필터링 전 모든 후보 영상
         Set<String> seenVideoIds = new HashSet<>(); // 중복 방지를 위해 사용
 
         // 오늘 날짜의 자정 (한국 시간 -> UTC)을 기준으로 설정
         ZonedDateTime nowKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
         ZonedDateTime startOfTodayUTC = nowKST.toLocalDate().atStartOfDay(ZoneOffset.UTC);
         String publishedAfter = startOfTodayUTC.format(DateTimeFormatter.ISO_INSTANT);
 
         System.out.println("Searching videos published after (UTC): " + publishedAfter);
 
         // --- 1단계: publishedAfter를 사용하여 오늘 영상만 최우선으로 가져오기 ---
         for (String keyword : keywords) {
             String nextPageToken = null;
             // 각 키워드에서 오늘 영상 검색 (최대 검색량까지, 예를 들어 200개 정도 시도)
             // 여기서 너무 많은 페이지를 돌면 할당량 소모가 크니 적절히 조절
             int currentKeywordTodayVideoCount = 0;
             final int MAX_PAGES_FOR_TODAY = 2; // 각 키워드당 오늘 영상 검색 페이지 수 (예: 2페이지 = 100개)
 
             for (int page = 0; page < MAX_PAGES_FOR_TODAY; page++) {
                 String searchUrl = "https://www.googleapis.com/youtube/v3/search" +
                     "?part=snippet&maxResults=" + 50 + "&order=date&q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) +
                     "&type=video&key=" + apiKey +
                     "&publishedAfter=" + publishedAfter;
                    
                     
                 if (nextPageToken != null) {
                     searchUrl += "&pageToken=" + nextPageToken;
                 }
 
                 Map<String, Object> searchResponse = restTemplate.getForObject(searchUrl, Map.class);
                 List<Map<String, Object>> items = (List<Map<String, Object>>) searchResponse.get("items");
                 nextPageToken = (String) searchResponse.get("nextPageToken");
 
                 if (items == null || items.isEmpty()) break;
 
                 // 채널 정보 일괄 조회 (Channel ID가 중복될 수 있으므로 Set 사용 후 Map으로 변환)
                 Set<String> channelIds = items.stream()
                     .map(item -> ((Map<String, Object>) item.get("snippet")).get("channelId").toString())
                     .collect(Collectors.toSet());
 
                 Map<String, Integer> channelSubscriberMap = new HashMap<>();
                 Map<String, String> channelTitleMap = new HashMap<>();
                 if (!channelIds.isEmpty()) { // 채널 ID가 있을 경우에만 API 호출
                     String channelsUrl = "https://www.googleapis.com/youtube/v3/channels" +
                         "?part=statistics,snippet&id=" + String.join(",", channelIds) +
                         "&key=" + apiKey;
 
                     Map<String, Object> channelsResponse = restTemplate.getForObject(channelsUrl, Map.class);
                     List<Map<String, Object>> channelItems = (List<Map<String, Object>>) channelsResponse.get("items");
 
                     if (channelItems != null) {
                         for (Map<String, Object> channel : channelItems) {
                             String channelId = (String) channel.get("id");
                             Map<String, Object> statistics = (Map<String, Object>) channel.get("statistics");
                             Map<String, Object> snippet = (Map<String, Object>) channel.get("snippet");
 
                             int subscriberCount = 0;
                             if (statistics != null && statistics.containsKey("subscriberCount")) {
                                 subscriberCount = Integer.parseInt((String) statistics.get("subscriberCount"));
                             }
                             String channelTitle = (String) snippet.get("title");
 
                             channelSubscriberMap.put(channelId, subscriberCount);
                             channelTitleMap.put(channelId, channelTitle);
                         }
                     }
                 }
 
                 for (Map<String, Object> item : items) {
                     Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                     String title = (String) snippet.get("title");
                     String lowerTitle = title.toLowerCase();
 
                     boolean excluded = excludeKeywords.stream()
                         .map(String::toLowerCase)
                         .anyMatch(lowerTitle::contains);
                     if (excluded) continue;
 
                    boolean containsSingleFifty = singleFiftyKeywords.stream()
                                        .anyMatch(lowerTitle::contains);

                    boolean containsOtherKeyword = keywords.stream()
                                            .map(String::toLowerCase)
                                            .anyMatch(lowerTitle::contains);

                    if (containsSingleFifty && !containsOtherKeyword) continue;


                     String channelId = (String) snippet.get("channelId");
                     int subCount = channelSubscriberMap.getOrDefault(channelId, 0);
                     if (subCount < SUBSCRIBER_THRESHOLD) continue;
 
                     // type=video 필터를 사용했기 때문에 null 체크를 최소화했지만, 안전을 위해 .get("videoId")에 대한 null 체크도 고려 가능
                     Map<String, Object> idMap = (Map<String, Object>) item.get("id");
                     if (idMap == null || !idMap.containsKey("videoId")) continue;
                     String videoId = idMap.get("videoId").toString();
 
                     if (seenVideoIds.contains(videoId)) continue;
                     seenVideoIds.add(videoId);
 
                     String thumbnailUrl = ((Map<String, Object>) ((Map<String, Object>) snippet.get("thumbnails")).get("default")).get("url").toString();
                     String publishedAtStr = (String) snippet.get("publishedAt");
                     LocalDateTime publishedAt = LocalDateTime.parse(publishedAtStr.substring(0, 19));
 
                     YoutubeVideoList video = new YoutubeVideoList();
                     video.setVideoId(videoId);
                     video.setTitle(title);
                     video.setChannelId(channelId);
                     video.setChannelTitle(channelTitleMap.get(channelId));
                     video.setSubscriberCount(subCount);
                     video.setPublishDate(publishedAt);
                     video.setThumbnailUrl(thumbnailUrl);
 
                     allCandidateVideos.add(video);
                 }
 
                 if (nextPageToken == null) break;
             }
         }
 
         // --- 2단계: 1단계에서 50개가 안 채워졌으면, publishedAfter 없이 최신 영상으로 채우기 ---
         // (단, 이미 allCandidateVideos에 포함된 영상은 제외)
         if (allCandidateVideos.size() < 50) {
             System.out.println("Not enough videos from today. Fetching older videos to fill up to " + 50);
             for (String keyword : keywords) {
                 String nextPageToken = null;
                 while (allCandidateVideos.size() < 50) {
                     String searchUrl = "https://www.googleapis.com/youtube/v3/search" +
                         "?part=snippet&maxResults=" + 50 + "&order=date&q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) +
                         "&type=video&key=" + apiKey;
 
                     if (nextPageToken != null) {
                         searchUrl += "&pageToken=" + nextPageToken;
                     }
 
                     Map<String, Object> searchResponse = restTemplate.getForObject(searchUrl, Map.class);
                     List<Map<String, Object>> items = (List<Map<String, Object>>) searchResponse.get("items");
                     nextPageToken = (String) searchResponse.get("nextPageToken");
 
                     if (items == null || items.isEmpty()) break;
 
                     // 채널 정보 일괄 조회
                     Set<String> channelIds = items.stream()
                         .map(item -> ((Map<String, Object>) item.get("snippet")).get("channelId").toString())
                         .collect(Collectors.toSet());
 
                     Map<String, Integer> channelSubscriberMap = new HashMap<>();
                     Map<String, String> channelTitleMap = new HashMap<>();
                     if (!channelIds.isEmpty()) {
                         String channelsUrl = "https://www.googleapis.com/youtube/v3/channels" +
                             "?part=statistics,snippet&id=" + String.join(",", channelIds) +
                             "&key=" + apiKey;
 
                         Map<String, Object> channelsResponse = restTemplate.getForObject(channelsUrl, Map.class);
                         List<Map<String, Object>> channelItems = (List<Map<String, Object>>) channelsResponse.get("items");
 
                         if (channelItems != null) {
                             for (Map<String, Object> channel : channelItems) {
                                 String channelId = (String) channel.get("id");
                                 Map<String, Object> statistics = (Map<String, Object>) channel.get("statistics");
                                 Map<String, Object> snippet = (Map<String, Object>) channel.get("snippet");
 
                                 int subscriberCount = 0;
                                 if (statistics != null && statistics.containsKey("subscriberCount")) {
                                     subscriberCount = Integer.parseInt((String) statistics.get("subscriberCount"));
                                 }
                                 String channelTitle = (String) snippet.get("title");
 
                                 channelSubscriberMap.put(channelId, subscriberCount);
                                 channelTitleMap.put(channelId, channelTitle);
                             }
                         }
                     }
 
                     for (Map<String, Object> item : items) {
                         Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                         String title = (String) snippet.get("title");
                         String lowerTitle = title.toLowerCase();
 
                         boolean excluded = excludeKeywords.stream()
                             .map(String::toLowerCase)
                             .anyMatch(lowerTitle::contains);
                         if (excluded) continue;
 
                         String channelId = (String) snippet.get("channelId");
                         int subCount = channelSubscriberMap.getOrDefault(channelId, 0);
                         if (subCount < SUBSCRIBER_THRESHOLD) continue;
 
                         Map<String, Object> idMap = (Map<String, Object>) item.get("id");
                         if (idMap == null || !idMap.containsKey("videoId")) continue;
                         String videoId = idMap.get("videoId").toString();
 
                         if (seenVideoIds.contains(videoId)) continue; // 이미 추가된 영상은 건너뛰기
                         seenVideoIds.add(videoId);
 
                         String thumbnailUrl = ((Map<String, Object>) ((Map<String, Object>) snippet.get("thumbnails")).get("default")).get("url").toString();
                         String publishedAtStr = (String) snippet.get("publishedAt");
                         LocalDateTime publishedAt = LocalDateTime.parse(publishedAtStr.substring(0, 19));
 
                         YoutubeVideoList video = new YoutubeVideoList();
                         video.setVideoId(videoId);
                         video.setTitle(title);
                         video.setChannelId(channelId);
                         video.setChannelTitle(channelTitleMap.get(channelId));
                         video.setSubscriberCount(subCount);
                         video.setPublishDate(publishedAt);
                         video.setThumbnailUrl(thumbnailUrl);
 
                         allCandidateVideos.add(video);
                         if (allCandidateVideos.size() >= 50) break;
                     }
                     if (nextPageToken == null || allCandidateVideos.size() >= 50) break;
                 }
                 if (allCandidateVideos.size() >= 50) break;
             }
         }
 
         // --- 모든 후보 영상 중 조건에 맞는 최종 영상 선택 ---
         // 조회수 추가 및 최종 50개 선정 로직은 동일
         if (allCandidateVideos.isEmpty()) {
             System.out.println("조건에 맞는 영상이 없습니다.");
             return;
         }
 
         // --- 📊 조회수 추가 ---
         // 최대 50개의 videoId만 요청하도록 변경 (API 할당량 효율)
         List<String> videoIdsForStats = allCandidateVideos.stream()
             .map(YoutubeVideoList::getVideoId)
             .limit(50) // 최종적으로 필요한 개수만큼만 ID 추출
             .toList();
 
         // 비디오 ID가 없을 경우 조회수 요청 스킵
         if (videoIdsForStats.isEmpty()) {
             System.out.println("조회수를 가져올 영상이 없습니다.");
             return;
         }
 
         String videoIdsParam = String.join(",", videoIdsForStats);
         String videosUrl = "https://www.googleapis.com/youtube/v3/videos" +
             "?part=statistics&id=" + videoIdsParam + "&key=" + apiKey;
 
         Map<String, Object> videosResponse = restTemplate.getForObject(videosUrl, Map.class);
         List<Map<String, Object>> videosItems = (List<Map<String, Object>>) videosResponse.get("items");
 
         Map<String, Integer> videoViewCountMap = new HashMap<>();
         if (videosItems != null) {
             for (Map<String, Object> videoItem : videosItems) {
                 String vid = (String) videoItem.get("id");
                 Map<String, Object> statistics = (Map<String, Object>) videoItem.get("statistics");
                 int viewCount = 0;
                 if (statistics != null && statistics.containsKey("viewCount")) {
                     viewCount = Integer.parseInt((String) statistics.get("viewCount"));
                 }
                 videoViewCountMap.put(vid, viewCount);
             }
         }
 
         for (YoutubeVideoList video : allCandidateVideos) {
             video.setViewCount(videoViewCountMap.getOrDefault(video.getVideoId(), 0));
         }
 
         // --- 🔽 최종 정렬 후 상위 50개만 저장 ---
         // 오늘 영상 우선 -> 최신순으로 정렬
         // 주의: 여기서는 allCandidateVideos를 가지고 정렬/필터링해야 합니다.
         // publishedAfter로 가져온 영상들이 publishDate가 더 "최신"일 수 있으므로 그대로 사용
         List<YoutubeVideoList> finalFilteredVideos = allCandidateVideos.stream()
             .sorted(Comparator.comparing(YoutubeVideoList::getPublishDate).reversed()) // 최신순으로 정렬
             .limit(50) // 상위 50개만 유지
             .collect(Collectors.toList());
 
         System.out.println("최종 저장 영상 수: " + finalFilteredVideos.size());
 
         // --- 데이터베이스 업데이트 ---
         youtubeMapper.deleteAllVideos();
         youtubeMapper.insertVideos(finalFilteredVideos);
     }

        private static final Pattern DURATION_PATTERN = Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?");
        private static List<String> targetChannelNames = List.of("UCtFOgQ30H7Y1C-vVbelYbFw","UCpVNA5ssOW02U7QiEL_ibhw","oldman sunny", "N The","FIFTY FIFTY Fantube" );

        private static final List<String> commonVideoTitleKeywords = List.of(
            "피프티피프티", "FIFTY", "fifty"
    );    

  private static final int MAX_PAGES_PER_CHANNEL_TODAY = 2; // 오늘 영상 검색 시
    private static final int MAX_PAGES_PER_CHANNEL_ADDITIONAL = 2; // 추가 영상 검색 시 (더 많은 페이지를 탐색할 수 있음)
    private static final int TARGET_VIDEO_COUNT = 50;


    public void fetchFanMeetUpdateVideos() {
        List<YoutubeVideoList> allCandidateVideos = new ArrayList<>();
        Set<String> seenVideoIds = new HashSet<>(); // 중복 방지를 위해 사용

        ZonedDateTime nowKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime startOfTodayUTC = nowKST.toLocalDate().atStartOfDay(ZoneOffset.UTC);
        String publishedAfter = startOfTodayUTC.format(DateTimeFormatter.ISO_INSTANT);

        System.out.println("스케줄러 로직: 오늘 날짜 이후 영상 검색 시작 (UTC): " + publishedAfter);

        // --- 1단계: 오늘 날짜 이후 영상 검색 ---
        performVideoSearch(allCandidateVideos, seenVideoIds, publishedAfter, MAX_PAGES_PER_CHANNEL_TODAY);

        System.out.println("스케줄러 로직: 오늘 날짜 영상 검색 완료. 현재 수집된 영상 수: " + allCandidateVideos.size());

        // --- 2단계: 50개 미달 시 추가로 최신 영상 검색 (publishedAfter 없이) ---
        if (allCandidateVideos.size() < TARGET_VIDEO_COUNT) {
            System.out.println("스케줄러 로직: 목표 영상 수(" + TARGET_VIDEO_COUNT + ") 미달. 추가 최신 영상 검색 시작.");
            // publishedAfter를 null로 전달하여 모든 최신 영상을 가져오도록 함
            performVideoSearch(allCandidateVideos, seenVideoIds, null, MAX_PAGES_PER_CHANNEL_ADDITIONAL);
            System.out.println("스케줄러 로직: 추가 최신 영상 검색 완료. 현재 수집된 영상 수: " + allCandidateVideos.size());
        }

        // --- 📊 조회수 추가 ---
        // 최종적으로 필요한 개수(TARGET_VIDEO_COUNT)만큼만 ID를 추출하여 조회수 요청 (API 할당량 효율)
        List<String> videoIdsForStats = allCandidateVideos.stream()
                .map(YoutubeVideoList::getVideoId)
                .limit(TARGET_VIDEO_COUNT)
                .toList();

        if (videoIdsForStats.isEmpty()) {
            System.out.println("스케줄러 로직: 조회수를 가져올 영상이 없습니다. 데이터베이스를 비웁니다.");
            youtubeMapper.deleteAllFanVideos(); // 영상이 하나도 없으면 기존 데이터도 삭제
            return;
        }

        try {
            String videoIdsParam = String.join(",", videoIdsForStats);
            String videosUrl = "https://www.googleapis.com/youtube/v3/videos" +
                    "?part=statistics&id=" + videoIdsParam + "&key=" + apiKey;

            Map<String, Object> videosResponse = restTemplate.getForObject(videosUrl, Map.class);
            List<Map<String, Object>> videosItems = (List<Map<String, Object>>) videosResponse.get("items");

            Map<String, Integer> videoViewCountMap = new HashMap<>();
            if (videosItems != null) {
                for (Map<String, Object> videoItem : videosItems) {
                    String vid = (String) videoItem.get("id");
                    Map<String, Object> statistics = (Map<String, Object>) videoItem.get("statistics");
                    int viewCount = 0;
                    if (statistics != null && statistics.containsKey("viewCount")) {
                        viewCount = Integer.parseInt((String) statistics.get("viewCount"));
                    }
                    videoViewCountMap.put(vid, viewCount);
                }
            }

            for (YoutubeVideoList video : allCandidateVideos) {
                video.setViewCount(videoViewCountMap.getOrDefault(video.getVideoId(), 0));
            }
        } catch (Exception e) {
            System.err.println("스케줄러 로직: 영상 조회수 가져오는 중 오류 발생: " + e.getMessage());
            // 이 오류가 발생해도 기존에 가져온 영상은 저장 시도
        }

        // --- 🔽 최종 정렬 후 상위 TARGET_VIDEO_COUNT개만 저장 ---
        List<YoutubeVideoList> finalFilteredVideos = allCandidateVideos.stream()
                .sorted(Comparator.comparing(YoutubeVideoList::getPublishDate).reversed()) // 최신순으로 정렬
                .limit(TARGET_VIDEO_COUNT) // 상위 TARGET_VIDEO_COUNT개만 유지
                .collect(Collectors.toList());

        System.out.println("스케줄러 로직: 최종 저장 영상 수: " + finalFilteredVideos.size());

        // --- 데이터베이스 업데이트 ---
        youtubeMapper.deleteAllFanVideos();
        youtubeMapper.insertFanVideos(finalFilteredVideos);
        System.out.println("스케줄러 로직: 데이터베이스 업데이트 완료.");
    }


    private void performVideoSearch(List<YoutubeVideoList> allCandidateVideos, Set<String> seenVideoIds,
                                    String publishedAfter, int maxPagesPerChannel) {

        String commonKeywordQuery = "";
        if (!commonVideoTitleKeywords.isEmpty()) {
            // 키워드를 OR 조건으로 묶어 URL 인코딩
            commonKeywordQuery = "&q=" + URLEncoder.encode(String.join("|", commonVideoTitleKeywords), StandardCharsets.UTF_8);
        }

        for (String channelName : targetChannelNames) {
            // 채널 ID 검색 (항상 필요)
            String channelSearchUrl = "https://www.googleapis.com/youtube/v3/search" +
                    "?part=id,snippet&maxResults=" + 1 +
                    "&channelId=" + channelName   +
                    "&type=channel&key=" + apiKey;

            try {
                Map<String, Object> channelSearchResponse = restTemplate.getForObject(channelSearchUrl, Map.class);
                List<Map<String, Object>> channelItems = (List<Map<String, Object>>) channelSearchResponse.get("items");

                if (channelItems == null || channelItems.isEmpty()) {
                    System.out.println("스케줄러 로직: 채널명 '" + channelName + "'에 해당하는 채널을 찾을 수 없습니다.");
                    continue;
                }

                String targetChannelId = (String) ((Map<String, Object>) channelItems.get(0).get("id")).get("channelId");
                System.out.println("스케줄러 로직: 채널명 '" + channelName + "'에 대한 채널 ID '" + targetChannelId + "' 검색 시작.");

                String nextPageToken = null;

                for (int page = 0; page < maxPagesPerChannel; page++) {
                    // 목표 영상 개수를 채웠고, 오늘 영상만 찾는 경우가 아니라면 더 이상 검색하지 않음
                    // 오늘 영상을 찾는 중에는 목표 개수를 채웠더라도 해당 페이지까지는 탐색을 진행해야 할 수 있습니다.
                    if (allCandidateVideos.size() >= TARGET_VIDEO_COUNT && publishedAfter == null) {
                        System.out.println("스케줄러 로직: 목표 영상 수(" + TARGET_VIDEO_COUNT + ") 달성. 추가 검색 중단.");
                        break; // 이 채널에 대한 추가 페이지 검색 중단
                    }

                    String videoSearchUrl = "https://www.googleapis.com/youtube/v3/search" +
                            "?part=snippet&maxResults=" + 50 + // 페이지당 최대 50개 영상
                            "&order=date" + // 최신순 정렬
                            "&channelId=" + targetChannelId +
                            "&type=video&key=" + apiKey +
                            commonKeywordQuery; // 공통 제목 키워드 추가

                    if (publishedAfter != null) {
                        videoSearchUrl += "&publishedAfter=" + publishedAfter; // 오늘 날짜 이후 필터
                    }

                    if (nextPageToken != null) {
                        videoSearchUrl += "&pageToken=" + nextPageToken;
                    }

                    Map<String, Object> videoSearchResponse = restTemplate.getForObject(videoSearchUrl, Map.class);
                    List<Map<String, Object>> items = (List<Map<String, Object>>) videoSearchResponse.get("items");
                    nextPageToken = (String) videoSearchResponse.get("nextPageToken");

                    if (items == null || items.isEmpty()) break; // 현재 페이지에 영상이 없으면 이 채널 검색 종료

                    String actualChannelTitle = (String) ((Map<String, Object>) items.get(0).get("snippet")).get("channelTitle");

                    // 현재 배치에 있는 모든 영상 ID들을 모아서 한 번에 contentDetails (duration) 조회
                    List<String> currentBatchVideoIds = items.stream()
                            .map(item -> (Map<String, Object>) item.get("id"))
                            .filter(idMap -> idMap != null && idMap.containsKey("videoId"))
                            .map(idMap -> idMap.get("videoId").toString())
                            .collect(Collectors.toList());

                    if (!currentBatchVideoIds.isEmpty()) {
                        String videosDetailsUrl = "https://www.googleapis.com/youtube/v3/videos" +
                                "?part=contentDetails&id=" + String.join(",", currentBatchVideoIds) +
                                "&key=" + apiKey;

                        Map<String, Object> videosDetailsResponse = restTemplate.getForObject(videosDetailsUrl, Map.class);
                        List<Map<String, Object>> videoDetailsItems = (List<Map<String, Object>>) videosDetailsResponse.get("items");

                        Map<String, String> videoDurationMap = new HashMap<>();
                        if (videoDetailsItems != null) {
                            for (Map<String, Object> detailItem : videoDetailsItems) {
                                String vidId = (String) detailItem.get("id");
                                Map<String, Object> contentDetails = (Map<String, Object>) detailItem.get("contentDetails");
                                if (contentDetails != null && contentDetails.containsKey("duration")) {
                                    videoDurationMap.put(vidId, (String) contentDetails.get("duration"));
                                }
                            }
                        }

                        // 이제 각 영상을 순회하며 필터링 및 추가
                        for (Map<String, Object> item : items) {
                            Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                            String title = (String) snippet.get("title");
                            String lowerTitle = title.toLowerCase();

                            // 제외 키워드 필터링
                            boolean excluded = excludeKeywords.stream()
                                    .map(String::toLowerCase)
                                    .anyMatch(lowerTitle::contains);
                            if (excluded) {
                                System.out.println("스케줄러 로직: 제외 키워드 포함 영상 건너뜀 - " + title);
                                continue;
                            }

                            Map<String, Object> idMap = (Map<String, Object>) item.get("id");
                            if (idMap == null || !idMap.containsKey("videoId")) continue;
                            String videoId = idMap.get("videoId").toString();

                            if (seenVideoIds.contains(videoId)) continue; // 이미 추가된 영상은 중복 방지

                            // ⭐ 2분 이상 영상 필터링 로직 ⭐
                            String durationIso = videoDurationMap.get(videoId);
                            if (durationIso == null) {
                                System.out.println("스케줄러 로직: 영상 '" + videoId + "'의 길이를 가져올 수 없습니다. 건너뜀.");
                                continue;
                            }
                            Duration videoDuration = parseIso8601Duration(durationIso);
                            if (videoDuration.toMinutes() < 2) {
                                System.out.println("스케줄러 로직: 2분 미만 영상 건너뜀 - " + title + " (" + videoDuration.toMinutes() + "분 " + (videoDuration.getSeconds() % 60) + "초)");
                                continue;
                            }
                            // ⭐ 필터링 로직 끝 ⭐

                            seenVideoIds.add(videoId); // 모든 필터를 통과한 유효한 영상만 seenVideoIds에 추가

                            String thumbnailUrl = ((Map<String, Object>) ((Map<String, Object>) snippet.get("thumbnails")).get("default")).get("url").toString();
                            String publishedAtStr = (String) snippet.get("publishedAt");
                            LocalDateTime publishedAt = LocalDateTime.parse(publishedAtStr.substring(0, 19));

                            YoutubeVideoList video = new YoutubeVideoList();
                            video.setVideoId(videoId);
                            video.setTitle(title);
                            video.setChannelId(targetChannelId);
                            video.setChannelTitle(actualChannelTitle);
                            video.setSubscriberCount(0); // 구독자 수 필터링 제거 (더 이상 사용 안 함)
                            video.setPublishDate(publishedAt);
                            video.setThumbnailUrl(thumbnailUrl);
                            video.setViewCount(0); // 초기 조회수는 0으로 설정, 나중에 업데이트

                            allCandidateVideos.add(video);
                               // --- 데이터베이스 업데이트 ---

                        }
                    }
     
                    if (nextPageToken == null) break; // 다음 페이지가 없으면 이 채널 검색 종료
                }
  
            } catch (Exception e) {
                System.err.println("스케줄러 로직: 채널 '" + channelName + "' 영상 검색 중 오류 발생: " + e.getMessage());
                // 오류 발생 시에도 다음 채널로 진행
            }
        }
    }

     
    /**
     * ISO 8601 Duration 문자열 (예: PT1H30M5S)을 Duration 객체로 파싱합니다.
     */
    private Duration parseIso8601Duration(String isoDuration) {
        Matcher matcher = DURATION_PATTERN.matcher(isoDuration);
        if (matcher.matches()) {
            int hours = Optional.ofNullable(matcher.group(1)).map(Integer::parseInt).orElse(0);
            int minutes = Optional.ofNullable(matcher.group(2)).map(Integer::parseInt).orElse(0);
            int seconds = Optional.ofNullable(matcher.group(3)).map(Integer::parseInt).orElse(0);
            return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        }
        return Duration.ZERO;
    }

}
