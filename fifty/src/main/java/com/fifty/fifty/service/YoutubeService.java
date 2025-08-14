package com.fifty.fifty.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fifty.fifty.domain.YoutubeChartData;
import com.fifty.fifty.domain.YoutubeVideoList;
import com.fifty.fifty.mapper.YoutubeChartMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class YoutubeService {

    private static List<String> keywords = List.of("FIFTY FIFTY", "fiftyfifty", "피프티", "피프티피프티")
    .stream()
    .filter(s -> s.matches("[a-zA-Z가-힣\\s]+"))
    .toList();

    private static List<String> excludeKeywords = List.of("어블름", "ablume", "EBUBE", "Dhaliwal", "starwars", "느좋", "어피트");
    private static final int SUBSCRIBER_THRESHOLD = 10_000;

private static final Pattern SINGLE_FIFTY_WORD_PATTERN =
        Pattern.compile("\\bfifty\\b", Pattern.CASE_INSENSITIVE);
 private static final int MAX_RESULTS_PER_PAGE = 50; 


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
        System.out.println("--- fetchAndUpdateVideos 시작 ---");
        Set<String> seenVideoIds = new HashSet<>(); // 전체 프로세스에서 중복 방지
        List<YoutubeVideoList> allCandidateVideos = new ArrayList<>(); // 최종 후보 영상 리스트

        // 1. 오늘 날짜의 자정 (한국 시간 -> UTC)을 기준으로 설정
        ZonedDateTime nowKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime startOfTodayUTC = nowKST.toLocalDate().atStartOfDay(ZoneOffset.UTC);
        String publishedAfter = startOfTodayUTC.format(DateTimeFormatter.ISO_INSTANT);
        System.out.println("기준 시간 (KST): " + nowKST);
        System.out.println("publishedAfter (UTC): " + publishedAfter);

        // 2. 1단계: publishedAfter를 사용하여 오늘 영상만 최우선으로 가져오기
        System.out.println("\n--- 1단계: 오늘 영상 검색 시작 (publishedAfter 기준) ---");
        // 각 키워드당 2페이지 (MAX_RESULTS_PER_PAGE * 2 = 100개 영상)
        searchAndAddVideos(allCandidateVideos, seenVideoIds, publishedAfter, 2, "1단계");
        System.out.println("1단계 완료. 현재 확보된 영상 수: " + allCandidateVideos.size());

        // 3. 2단계: 1단계에서 충분한 영상이 안 채워졌으면, publishedAfter 없이 최신 영상으로 채우기
        if (allCandidateVideos.size() < MAX_RESULTS_PER_PAGE) { // 목표 50개를 채우지 못했을 경우
            System.out.println("\n--- 2단계: 충분한 영상 확보를 위해 최신 영상 검색 시작 ---");
            // 각 키워드당 5페이지 (MAX_RESULTS_PER_PAGE * 5 = 250개 영상)
            searchAndAddVideos(allCandidateVideos, seenVideoIds, null, 5, "2단계");
            System.out.println("총 확보된 최종 후보 영상 수: " + allCandidateVideos.size());
        } else {
            System.out.println("\n--- 2단계 건너뜀: 1단계에서 충분한 영상(" + allCandidateVideos.size() + "개) 확보 완료 ---");
        }


        if (allCandidateVideos.isEmpty()) {
            System.out.println("최종적으로 조건에 맞는 영상이 없어 데이터베이스를 업데이트하지 않습니다.");
            return;
        }

        // 4. 조회수 추가 (최종 MAX_RESULTS_PER_PAGE 개 영상에 대해)
        addVideoViewCounts(allCandidateVideos);

        // 5. 최종 정렬 후 상위 MAX_RESULTS_PER_PAGE 개만 저장
        List<YoutubeVideoList> finalFilteredVideos = filterAndSortFinalVideos(allCandidateVideos);
        System.out.println("\n최종 저장될 영상 수: " + finalFilteredVideos.size());

        // 6. 데이터베이스 업데이트
        updateDatabase(finalFilteredVideos);

        System.out.println("--- fetchAndUpdateVideos 종료 ---");
    }

    /**
     * YouTube API를 호출하여 영상을 검색하고 allCandidateVideos 리스트에 추가합니다.
     * @param allCandidateVideos 최종 후보 영상을 담을 리스트
     * @param seenVideoIds 이미 추가된 영상 ID를 추적하는 Set
     * @param publishedAfter 특정 날짜 이후에 발행된 영상만 검색할 때 사용 (null이면 모든 날짜)
     * @param maxPagesPerKeyword 각 키워드당 검색할 최대 페이지 수
     * @param stageName 현재 검색 단계의 이름 (로깅용, 예: "1단계", "2단계")
     */
    private void searchAndAddVideos(List<YoutubeVideoList> allCandidateVideos, Set<String> seenVideoIds,
                                    String publishedAfter, int maxPagesPerKeyword, String stageName) {
        for (String keyword : keywords) {
            String nextPageToken = null;
            System.out.println("  [" + stageName + "] 키워드: '" + keyword + "' 검색 중...");
            for (int page = 0; page < maxPagesPerKeyword; page++) {
                // 목표 영상 수(50개)를 채웠고, publishedAfter가 null인 2단계인 경우 더 이상 검색하지 않음
                if (allCandidateVideos.size() >= MAX_RESULTS_PER_PAGE && publishedAfter == null) {
                    System.out.println("  [" + stageName + "] 목표 영상 수(" + MAX_RESULTS_PER_PAGE + "개) 달성. 추가 검색 중단.");
                    return; // 메서드 전체 종료
                }

                String searchUrl = null;
                try {
                    searchUrl = buildSearchUrl(keyword, publishedAfter, nextPageToken);
                } catch (UnsupportedEncodingException e) {
                    System.err.println("  [" + stageName + "] URL 인코딩 오류: " + e.getMessage());
                    e.printStackTrace();
                    continue; // 다음 키워드로
                }

                Map<String, Object> searchResponse;
                try {
                    searchResponse = restTemplate.getForObject(searchUrl, Map.class);
                } catch (Exception e) {
                    System.err.println("  [" + stageName + "] Youtube API 호출 오류: " + e.getMessage());
                    e.printStackTrace();
                    break; // 현재 키워드의 다음 페이지 검색 중단
                }

                List<Map<String, Object>> items = (List<Map<String, Object>>) searchResponse.get("items");
                nextPageToken = (String) searchResponse.get("nextPageToken");

                if (items == null || items.isEmpty()) {
                    System.out.println("  [" + stageName + "] 더 이상 검색 결과가 없거나 페이지 끝 (키워드: " + keyword + ", 페이지: " + (page + 1) + ")");
                    break; // 현재 키워드의 다음 페이지 검색 중단
                }

                // 채널 정보 일괄 조회
                Map<String, ChannelInfo> channelInfoMap = fetchChannelInfoBatch(items, stageName);

                for (Map<String, Object> item : items) {
                    addVideoIfValid(item, allCandidateVideos, seenVideoIds, channelInfoMap, stageName);
                    // 2단계에서만 50개 목표 달성 시 루프 중단
                    if (allCandidateVideos.size() >= MAX_RESULTS_PER_PAGE && publishedAfter == null) {
                        break; // 내부 루프 중단 (for item : items)
                    }
                }

                if (nextPageToken == null) {
                    System.out.println("  [" + stageName + "] 다음 페이지 토큰 없음.");
                    break; // 현재 키워드의 다음 페이지 검색 중단
                }
            }
        }
    }

    /**
     * Youtube API URL을 생성합니다.
     */
    private String buildSearchUrl(String keyword, String publishedAfter, String pageToken) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder("https://www.googleapis.com/youtube/v3/search")
                .append("?part=snippet&maxResults=").append(MAX_RESULTS_PER_PAGE)
                .append("&order=date&q=").append(URLEncoder.encode(keyword, StandardCharsets.UTF_8))
                .append("&type=video&key=").append(apiKey);

        if (publishedAfter != null && !publishedAfter.isEmpty()) {
            urlBuilder.append("&publishedAfter=").append(publishedAfter);
        }
        if (pageToken != null && !pageToken.isEmpty()) {
            urlBuilder.append("&pageToken=").append(pageToken);
        }
        return urlBuilder.toString();
    }

    /**
     * YouTube API 검색 결과에서 채널 ID를 추출하여 채널 정보를 일괄 조회합니다.
     * @param items YouTube API 검색 결과 아이템 리스트
     * @param stageName 현재 처리 단계 (로깅용)
     * @return 채널 ID를 키로, ChannelInfo 객체를 값으로 하는 맵
     */
    private Map<String, ChannelInfo> fetchChannelInfoBatch(List<Map<String, Object>> items, String stageName) {
        Set<String> channelIds = items.stream()
                .map(item -> ((Map<String, Object>) item.get("snippet")).get("channelId").toString())
                .collect(Collectors.toSet());

        Map<String, ChannelInfo> channelInfoMap = new HashMap<>();
        if (channelIds.isEmpty()) {
            return channelInfoMap;
        }

        try {
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
                        // subscriberCount는 String으로 반환될 수 있으므로 int로 파싱
                        subscriberCount = Integer.parseInt((String) statistics.get("subscriberCount"));
                    }
                    String channelTitle = (String) snippet.get("title");
                    channelInfoMap.put(channelId, new ChannelInfo(channelTitle, subscriberCount));
                }
            }
        } catch (Exception e) {
            System.err.println("  [" + stageName + "] 채널 정보 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return channelInfoMap;
    }

    /**
     * 단일 비디오 아이템을 검증하고 필터링 조건을 통과하면 리스트에 추가합니다.
     * @param item YouTube API에서 가져온 비디오 아이템 데이터
     * @param allCandidateVideos 최종 후보 영상 리스트
     * @param seenVideoIds 이미 추가된 영상 ID를 추적하는 Set
     * @param channelInfoMap 채널 정보 맵
     * @param stageName 현재 처리 단계 (로깅용)
     */
    private void addVideoIfValid(Map<String, Object> item, List<YoutubeVideoList> allCandidateVideos,
                                 Set<String> seenVideoIds, Map<String, ChannelInfo> channelInfoMap, String stageName) {
        Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
        String title = (String) snippet.get("title");

        String decodedTitle = StringEscapeUtils.unescapeHtml4(title); // HTML 엔티티 디코딩
        String lowerTitle = decodedTitle.toLowerCase(); // 비교용 소문자

        // 1. 제외 키워드 검사
        boolean excluded = excludeKeywords.stream()
                .map(String::toLowerCase)
                .anyMatch(lowerTitle::contains);
        if (excluded) {
            System.out.println("  [" + stageName + "] SKIP (제외 키워드): " + decodedTitle);
            return;
        }

        // 2. "fifty" 단일 키워드 정확히 검사 (정규표현식 사용)
        Matcher singleFiftyMatcher = SINGLE_FIFTY_WORD_PATTERN.matcher(lowerTitle);
        boolean containsOnlySingleFiftyWord = singleFiftyMatcher.find(); // " fifty "와 같이 단일 단어로 'fifty'가 있는지 확인

        // 3. 메인 키워드 (FIFTY FIFTY 관련) 검사
        boolean containsMainKeyword = keywords.stream()
                .anyMatch(mainKeyword -> lowerTitle.contains(mainKeyword.toLowerCase()));

        // 필터링 로직: 메인 키워드 포함 + "fifty" 단일 키워드 (단어 경계)가 아닌 경우만 통과
        // 즉, "FIFTY FIFTY"는 허용, "Fifty Shades"는 거부
        if (!containsMainKeyword && containsOnlySingleFiftyWord) {
            System.out.println("  [" + stageName + "] SKIP ('fifty' 단일 키워드): " + decodedTitle);
            return;
        }
        if (!containsMainKeyword) { // 메인 키워드가 아예 없으면 기본적으로 스킵
            System.out.println("  [" + stageName + "] SKIP (메인 키워드 없음): " + decodedTitle);
            return;
        }

        String channelId = (String) snippet.get("channelId");
        ChannelInfo channelInfo = channelInfoMap.get(channelId);
        if (channelInfo == null) {
            System.out.println("  [" + stageName + "] SKIP (채널 정보 없음 또는 조회 실패): " + decodedTitle);
            return;
        }

        if (channelInfo.subscriberCount() < SUBSCRIBER_THRESHOLD) {
            System.out.println("  [" + stageName + "] SKIP (구독자 수 부족 " + channelInfo.subscriberCount() + "): " + decodedTitle);
            return;
        }

        Map<String, Object> idMap = (Map<String, Object>) item.get("id");
        if (idMap == null || !idMap.containsKey("videoId")) {
            System.out.println("  [" + stageName + "] SKIP (비디오 ID 없음): " + decodedTitle);
            return;
        }
        String videoId = idMap.get("videoId").toString();

        if (seenVideoIds.contains(videoId)) {
            // System.out.println("  [" + stageName + "] SKIP (중복 비디오 ID): " + decodedTitle); // 중복이 많으면 로그 폭주 가능
            return;
        }
        seenVideoIds.add(videoId); // 중복 확인용 Set에 추가

        // 썸네일 URL 추출 (null 체크 강화)
        String thumbnailUrl = null;
        Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
        if (thumbnails != null) {
            Map<String, Object> defaultThumbnail = (Map<String, Object>) thumbnails.get("default");
            if (defaultThumbnail != null && defaultThumbnail.containsKey("url")) {
                thumbnailUrl = defaultThumbnail.get("url").toString();
            }
        }
        if (thumbnailUrl == null) {
             System.out.println("  [" + stageName + "] SKIP (썸네일 URL 없음): " + decodedTitle);
             return;
        }


        String publishedAtStr = (String) snippet.get("publishedAt");
        // YouTube API publishedAt 포맷은 'YYYY-MM-DDTHH:MM:SSZ' (UTC) 이므로 19번째 문자까지 잘라 파싱
        LocalDateTime publishedAt = LocalDateTime.parse(publishedAtStr.substring(0, 19));

        // YoutubeVideoList 객체 생성 및 값 설정
        YoutubeVideoList video = new YoutubeVideoList();
        video.setVideoId(videoId);
        video.setTitle(decodedTitle); // 디코딩된 제목 저장
        video.setChannelId(channelId);
        video.setChannelTitle(channelInfo.channelTitle());
        video.setSubscriberCount(channelInfo.subscriberCount());
        video.setPublishDate(publishedAt);
        video.setThumbnailUrl(thumbnailUrl);
        // 조회수는 나중에 일괄 추가되므로 여기서는 0 또는 기본값

        allCandidateVideos.add(video);
        System.out.println("  [" + stageName + "] ADD: " + decodedTitle);
    }

    /**
     * Youtube API를 호출하여 비디오들의 조회수를 일괄 조회하고 YoutubeVideoList 객체에 반영합니다.
     * @param videos 조회수를 업데이트할 YoutubeVideoList 객체 리스트
     */
    private void addVideoViewCounts(List<YoutubeVideoList> videos) {
        System.out.println("\n--- 조회수 정보 가져오는 중 ---");
        // 최종 50개 영상에 대해서만 조회수 요청 (API 할당량 효율)
        List<String> videoIdsForStats = videos.stream()
                .map(YoutubeVideoList::getVideoId)
                .limit(MAX_RESULTS_PER_PAGE)
                .collect(Collectors.toList());

        if (videoIdsForStats.isEmpty()) {
            System.out.println("  조회수를 가져올 영상 ID가 없어 요청을 건너뜝니다.");
            return;
        }

        try {
            // ID 목록을 콤마(,)로 구분하여 URL 파라미터로 전달
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

            // 조회수 맵을 사용하여 YoutubeVideoList 객체 업데이트
            for (YoutubeVideoList video : videos) {
                video.setViewCount(videoViewCountMap.getOrDefault(video.getVideoId(), 0));
            }
            System.out.println("  조회수 정보 업데이트 완료.");
        } catch (Exception e) {
            System.err.println("  조회수 정보 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 후보 영상 리스트를 최종 필터링 및 정렬하여 상위 50개를 반환합니다.
     * @param videos 정렬 및 필터링할 YoutubeVideoList 객체 리스트
     * @return 최종 선정된 YoutubeVideoList 객체 리스트 (최대 50개)
     */
    private List<YoutubeVideoList> filterAndSortFinalVideos(List<YoutubeVideoList> videos) {
        System.out.println("최종 영상 정렬 (최신순) 및 상위 " + MAX_RESULTS_PER_PAGE + "개 필터링 중...");
        return videos.stream()
                .sorted(Comparator.comparing(YoutubeVideoList::getPublishDate).reversed()) // 최신순으로 정렬
                .limit(MAX_RESULTS_PER_PAGE) // 상위 50개만 유지
                .collect(Collectors.toList());
    }

    /**
     * 최종 필터링된 영상을 데이터베이스에 업데이트합니다.
     * @param videosToSave 데이터베이스에 저장할 YoutubeVideoList 객체 리스트
     */
    private void updateDatabase(List<YoutubeVideoList> videosToSave) {
        System.out.println("\n--- 데이터베이스 업데이트 시작 ---");
        try {
            youtubeMapper.deleteAllVideos(); // 기존 차트 데이터 전체 삭제
            if (!videosToSave.isEmpty()) {
                youtubeMapper.insertVideos(videosToSave); // 새로 필터링된 영상 삽입
                System.out.println("데이터베이스 업데이트 완료. " + videosToSave.size() + "개 영상 삽입.");
            } else {
                System.out.println("데이터베이스에 저장할 영상이 없어 insert를 건너뜝니다.");
            }
        } catch (Exception e) {
            System.err.println("데이터베이스 업데이트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 채널 정보를 담기 위한 내부 도우미 레코드 (Java 16+에서 사용 가능)
     * record는 불변(immutable) 데이터를 표현할 때 유용하며, getter, equals, hashCode, toString이 자동으로 생성됩니다.
     * Java 16 미만 버전 사용 시에는 일반 private static class로 정의하고 생성자, getter 등을 수동으로 작성해야 합니다.
     */
    private record ChannelInfo(String channelTitle, int subscriberCount) {}
        private static final Pattern DURATION_PATTERN = Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?");
        private static List<String> targetChannelNames = List.of("UCtFOgQ30H7Y1C-vVbelYbFw","UCpVNA5ssOW02U7QiEL_ibhw","UCwY-LPK3X1AsEQxxCQrpMtg", "UCeDS74Wtx9VeBFNpYLaZiwA","UC-3PWIifHJEefhEBJNeCjuw" );
        private static final List<String> commonVideoTitleKeywords = List.of(
            "피프티피프티", "FIFTY", "fifty"
    );    

  private static final int MAX_PAGES_PER_CHANNEL_TODAY = 2; // 오늘 영상 검색 시
    private static final int MAX_PAGES_PER_CHANNEL_ADDITIONAL = 2; // 추가 영상 검색 시 (더 많은 페이지를 탐색할 수 있음)
    private static final int TARGET_VIDEO_COUNT = 50;


  public void fetchFanMeetUpdateVideos() {
        List<YoutubeVideoList> allCandidateVideos = new ArrayList<>();
        Set<String> seenVideoIds = new HashSet<>(); // 중복 영상 ID 저장을 위한 Set

        ZonedDateTime nowKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime startOfTodayUTC = nowKST.toLocalDate().atStartOfDay(ZoneOffset.UTC);
        String publishedAfterToday = startOfTodayUTC.format(DateTimeFormatter.ISO_INSTANT);

      

        performVideoSearch(allCandidateVideos, seenVideoIds, publishedAfterToday, MAX_PAGES_PER_CHANNEL_TODAY);

        System.out.println("스케줄러 로직: 초기 (오늘 영상) 검색 완료. 현재 수집된 영상 수: " + allCandidateVideos.size());

        if (allCandidateVideos.size() < TARGET_VIDEO_COUNT) {
            System.out.println("스케줄러 로직: 목표 영상 수(" + TARGET_VIDEO_COUNT + ") 미달. 추가 영상 검색 시작.");
            
            performVideoSearch(allCandidateVideos, seenVideoIds, null, MAX_PAGES_PER_CHANNEL_ADDITIONAL);
            System.out.println("스케줄러 로직: 추가 영상 검색 완료. 현재 수집된 영상 수: " + allCandidateVideos.size());
        }


        List<String> videoIdsForStats = allCandidateVideos.stream()
                .map(YoutubeVideoList::getVideoId)
                .distinct() // 중복 ID 제거
                .limit(TARGET_VIDEO_COUNT) // 최종 저장할 개수만큼만 조회수 요청 (여기서 limit을 적용하면 API 호출 횟수를 줄일 수 있습니다)
                .collect(Collectors.toList());

        if (videoIdsForStats.isEmpty()) {
            System.out.println("스케줄러 로직: 조회수를 가져올 영상이 없습니다. 데이터베이스를 비웁니다.");
            youtubeMapper.deleteAllFanVideos(); // 영상이 하나도 없으면 기존 데이터도 삭제
            return;
        }

        try {
            // 한 번의 API 호출로 여러 영상의 조회수를 가져옵니다.
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
                        // 조회수는 문자열로 오므로 Integer.parseInt로 변환
                        viewCount = Integer.parseInt((String) statistics.get("viewCount"));
                    }
                    videoViewCountMap.put(vid, viewCount);
                }
            }

            // 수집된 영상 목록에 조회수를 업데이트합니다.
            for (YoutubeVideoList video : allCandidateVideos) {
                video.setViewCount(videoViewCountMap.getOrDefault(video.getVideoId(), 0));
            }
        } catch (Exception e) {
            System.err.println("스케줄러 로직: 영상 조회수 가져오는 중 오류 발생: " + e.getMessage());
            // 오류가 발생해도 기존에 가져온 영상은 저장 시도
        }

        // --- 🔽 최종 정렬 후 상위 TARGET_VIDEO_COUNT개만 저장 ---
        // 수집된 모든 후보 영상들을 게시일 기준 최신순으로 정렬한 후, 목표 개수만큼만 선택합니다.
        List<YoutubeVideoList> finalFilteredVideos = allCandidateVideos.stream()
                .sorted(Comparator.comparing(YoutubeVideoList::getPublishDate).reversed()) // 최신순으로 정렬
                .limit(TARGET_VIDEO_COUNT) // 상위 TARGET_VIDEO_COUNT개 (50개)만 유지
                .collect(Collectors.toList());

        System.out.println("스케줄러 로직: 최종 저장 영상 수: " + finalFilteredVideos.size());

        // --- 데이터베이스 업데이트 ---
        // 기존 데이터를 모두 삭제하고 새로 수집된 데이터를 삽입합니다.
        youtubeMapper.deleteAllFanVideos();
        if (!finalFilteredVideos.isEmpty()) { // 삽입할 영상이 있을 때만 삽입 호출
            youtubeMapper.insertFanVideos(finalFilteredVideos);
        }
        System.out.println("스케줄러 로직: 데이터베이스 업데이트 완료.");
    }


    private void performVideoSearch(List<YoutubeVideoList> allCandidateVideos, Set<String> seenVideoIds,
                                    String publishedAfter, int maxPagesPerChannel) {

        for (String channelIdFromList : targetChannelNames) {
            String uploadsPlaylistId = null;
            String actualChannelTitle = null; 

            String channelDetailsUrl = "https://www.googleapis.com/youtube/v3/channels" +
                    "?part=contentDetails,snippet&id=" + channelIdFromList +
                    "&key=" + apiKey;
            try {
                Map<String, Object> channelDetailsResponse = restTemplate.getForObject(channelDetailsUrl, Map.class);
                List<Map<String, Object>> channelItems = (List<Map<String, Object>>) channelDetailsResponse.get("items");

                if (channelItems != null && !channelItems.isEmpty()) {
                    Map<String, Object> contentDetails = (Map<String, Object>) channelItems.get(0).get("contentDetails");
                    Map<String, Object> relatedPlaylists = (Map<String, Object>) contentDetails.get("relatedPlaylists");
                    uploadsPlaylistId = (String) relatedPlaylists.get("uploads");

                    Map<String, Object> snippet = (Map<String, Object>) channelItems.get(0).get("snippet");
                    actualChannelTitle = (String) snippet.get("title"); 

               
                } else {
                    
                    continue; // 다음 채널로 건너뜁니다.
                }
            } catch (Exception e) {
                System.err.println("스케줄러 로직: 채널 '" + channelIdFromList + "' 업로드 플레이리스트 ID 조회 중 오류 발생: " + e.getMessage());
                continue; // 오류 발생 시에도 다음 채널로 진행합니다.
            }

            if (uploadsPlaylistId == null) {
                System.out.println("스케줄러 로직: 채널 '" + channelIdFromList + "'의 업로드 플레이리스트 ID를 가져올 수 없습니다. 건너뜁니다.");
                continue; // 플레이리스트 ID가 없으면 다음 채널로 건너뜁니다.
            }

            String nextPageToken = null;
            for (int page = 0; page < maxPagesPerChannel; page++) {
                // 각 채널당 정해진 페이지 수(maxPagesPerChannel)만큼 무조건 조회합니다.

                // 2단계: playlistItems 엔드포인트를 사용하여 플레이리스트 항목 가져오기
                // `playlistItems`는 기본적으로 최신순으로 정렬됩니다.
                String playlistItemsUrl = "https://www.googleapis.com/youtube/v3/playlistItems" +
                        "?part=snippet&maxResults=" + 50 + // 페이지당 최대 50개 영상
                        "&playlistId=" + uploadsPlaylistId +
                        "&key=" + apiKey;

                if (nextPageToken != null) {
                    playlistItemsUrl += "&pageToken=" + nextPageToken;
                }

                Map<String, Object> playlistItemsResponse;
                try {
                    playlistItemsResponse = restTemplate.getForObject(playlistItemsUrl, Map.class);
                } catch (Exception e) {
                    System.err.println("스케줄러 로직: 채널 '" + channelIdFromList + "'의 플레이리스트 항목 가져오는 중 오류 발생: " + e.getMessage());
                    break; // 이 채널에 대한 처리를 중단하고 다음 채널로 이동합니다.
                }

                List<Map<String, Object>> items = (List<Map<String, Object>>) playlistItemsResponse.get("items");
                nextPageToken = (String) playlistItemsResponse.get("nextPageToken");

                if (items == null || items.isEmpty()) {
                  
                    break; // 현재 페이지에 영상이 없으면 이 채널 검색을 종료합니다.
                }

                // 현재 배치에 있는 모든 영상 ID들을 모아서 한 번에 contentDetails (duration) 조회
                List<String> currentBatchVideoIds = new ArrayList<>();
                for (Map<String, Object> item : items) {
                    Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                    Map<String, Object> resourceId = (Map<String, Object>) snippet.get("resourceId");
                    if (resourceId != null && resourceId.containsKey("videoId")) {
                        currentBatchVideoIds.add(resourceId.get("videoId").toString());
                    }
                }

                Map<String, String> videoDurationMap = new HashMap<>();
                if (!currentBatchVideoIds.isEmpty()) {
                    try {
                        String videosDetailsUrl = "https://www.googleapis.com/youtube/v3/videos" +
                                "?part=contentDetails&id=" + String.join(",", currentBatchVideoIds) +
                                "&key=" + apiKey;

                        Map<String, Object> videosDetailsResponse = restTemplate.getForObject(videosDetailsUrl, Map.class);
                        List<Map<String, Object>> videoDetailsItems = (List<Map<String, Object>>) videosDetailsResponse.get("items");

                        if (videoDetailsItems != null) {
                            for (Map<String, Object> detailItem : videoDetailsItems) {
                                String vidId = (String) detailItem.get("id");
                                Map<String, Object> contentDetails = (Map<String, Object>) detailItem.get("contentDetails");
                                if (contentDetails != null && contentDetails.containsKey("duration")) {
                                    videoDurationMap.put(vidId, (String) contentDetails.get("duration"));
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("스케줄러 로직: 영상 길이 가져오는 중 오류 발생 (배치): " + e.getMessage());
                        // 이 오류가 발생해도 개별 영상 처리 시도
                    }
                }

                // 이제 각 영상을 순회하며 필터링 및 추가
                for (Map<String, Object> item : items) {
                    Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                    String title = (String) snippet.get("title");
                    String lowerTitle = title.toLowerCase();
                    String publishedAtStr = (String) snippet.get("publishedAt");
                    LocalDateTime publishedAt = LocalDateTime.parse(publishedAtStr.substring(0, 19));

                    Map<String, Object> resourceIdMap = (Map<String, Object>) snippet.get("resourceId");
                    if (resourceIdMap == null || !resourceIdMap.containsKey("videoId")) continue;
                    String videoId = resourceIdMap.get("videoId").toString();

                    // 1. **중복 영상 필터링**: 이미 추가된 영상은 건너뜁니다.
                    if (seenVideoIds.contains(videoId)) {
                        continue;
                    }

                    // 2. **게시일 필터링 (수동)**: `publishedAfter` 파라미터가 있을 경우에만 적용
                    // `playlistItems` 엔드포인트는 `publishedAfter` 파라미터를 직접 지원하지 않으므로,
                    // 가져온 영상의 게시일을 확인하여 필터링합니다.
                    if (publishedAfter != null) {
                        ZonedDateTime videoPublishedUtc = publishedAt.atZone(ZoneOffset.UTC);
                        Instant filterStartInstant = Instant.parse(publishedAfter);
                        ZonedDateTime filterStartUtc = ZonedDateTime.ofInstant(filterStartInstant, ZoneOffset.UTC);
                        if (videoPublishedUtc.isBefore(filterStartUtc)) {
                            // 현재 영상이 필터링 시작일 이전이면, 이 채널의 남은 영상들도 대부분 그 이전일 가능성이 높으므로
                            // (playlistItems가 최신순 정렬이므로) 이 채널 검색을 중단합니다.
                            // 즉, today 검색일 때는 오래된 영상이 나오면 해당 채널은 더 이상 검색하지 않습니다.
                         
                            nextPageToken = null; // 다음 페이지 토큰을 null로 만들어 바깥 루프도 종료
                            break; // 현재 채널의 for 루프를 종료하고 다음 채널로 넘어갑니다.
                        }
                    }

                    // 3. **제외 키워드 필터링**: `excludeKeywords`에 있는 단어가 제목에 포함되면 제외합니다.
                    boolean excluded = excludeKeywords.stream()
                            .map(String::toLowerCase)
                            .anyMatch(lowerTitle::contains);
                    if (excluded) {
                      
                        continue;
                    }

                    // 4. **제목 키워드 필수 포함 필터링**: `commonVideoTitleKeywords` 중 하나라도 제목에 포함되어야 합니다.
                    boolean keywordIncluded = commonVideoTitleKeywords.stream()
                            .map(String::toLowerCase)
                            .anyMatch(lowerTitle::contains);
                    if (!keywordIncluded) {
                        continue; // 키워드가 없으면 건너뜁니다.
                    }

                    // 5. **영상 길이 필터링 (2분 이상)**:
                    String durationIso = videoDurationMap.get(videoId);
                    if (durationIso == null) {
                       
                        continue;
                    }
                    Duration videoDuration = parseIso8601Duration(durationIso);
                    if (videoDuration.toMinutes() < 2) {
            
                        continue;
                    }

                    // 모든 필터를 통과한 유효한 영상만 `seenVideoIds`에 추가합니다.
                    seenVideoIds.add(videoId);

                    // 썸네일 URL을 가져옵니다.
                    String thumbnailUrl = ((Map<String, Object>) ((Map<String, Object>) snippet.get("thumbnails")).get("default")).get("url").toString();

                    // YoutubeVideoList 객체를 생성하고 값들을 설정합니다.
                    YoutubeVideoList video = new YoutubeVideoList();
                    video.setVideoId(videoId);
                    video.setTitle(title);
                    video.setChannelId(channelIdFromList); // 실제 채널 ID 저장
                    video.setChannelTitle(actualChannelTitle); // 실제 채널 제목 저장
                    video.setSubscriberCount(0); // 구독자 수 필터링 제거 (더 이상 사용 안 함)
                    video.setPublishDate(publishedAt);
                    video.setThumbnailUrl(thumbnailUrl);
                    video.setViewCount(0); // 초기 조회수는 0으로 설정, 나중에 업데이트

                    // 최종 후보 목록에 추가합니다.
                    allCandidateVideos.add(video);
                }

                if (nextPageToken == null) {
                    break; // 다음 페이지가 없으면 이 채널 검색을 종료합니다.
                }
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
