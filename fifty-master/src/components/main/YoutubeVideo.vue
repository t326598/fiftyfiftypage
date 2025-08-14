<script lang="ts" setup>
import { ref, onMounted,computed, watch } from 'vue';
import axios from '@/api/chart';
import type{YoutubeVideo} from '@/api/chart';
import YoutubeRankingSlider from '@/components/main/YoutubeRankingSlider.vue'
import FixedButtons from '@/components/main/FixedButtons.vue'
import CheckToday from '@/components/main/CheckToday.vue'

const videos = ref<YoutubeVideo[]>([]);
const fanVideos = ref<YoutubeVideo[]>([]);
const loading = ref(false);

const selectedVideo =ref<'all'|'fansign'>('all');

// 페이징처리

const currentPage = ref(1);
const itemsPerPage = 10;

const totalPages = computed(() =>
  Math.ceil(displayedVideo.value.length / itemsPerPage)
)

const paginated = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return displayedVideo.value.slice(start,start + itemsPerPage);
})

function goToPage(page:number){
  if(page >= 1 && page <= totalPages.value){
    currentPage.value = page;
    scrollToTop();
  }
}

watch(selectedVideo,() => {
  currentPage.value = 1;
})

async function fetchVideos() {
  loading.value = true;
  try {
    const res = await axios.getVideo()
    const fanres = await axios.getFanVideo()
    videos.value = res.data;
    fanVideos.value = fanres.data;
    console.log(videos.value);
  } catch (error) {
    console.error('영상 불러오기 실패', error);
  } finally {
    loading.value = false;
  }
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  const koreaTime = new Date(date.getTime() + 9 * 60 * 60 * 1000);
  return koreaTime.toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    hour12: false
  });
}



function formatNumber(num?: number): string {
  if (num === undefined) return '정보 없음';
  if (num >= 10_000) return `${(num / 10_000).toFixed(1)}만`;
  if (num >= 1000) return `${(num / 1000).toFixed(1)}천`;
  return num.toLocaleString('ko-KR');
}

const displayedVideo = computed(() => {
  return selectedVideo.value === 'all' ? videos.value : fanVideos.value;
})

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  fetchVideos();
});

</script>

<template>
  <div class="a-box">
    <a href="/"> FIFTY페이지</a> 
  </div>
  <YoutubeRankingSlider />
  <div class="button-group">
    <button
      :class="{ active: selectedVideo === 'all' }"
      @click="selectedVideo = 'all'"
    >
      피프티피프티 최신 영상
    </button>
    <button
      :class="{ active: selectedVideo === 'fansign' }"
      @click="selectedVideo = 'fansign'"
    >
      피프티피프티 팬싸 영상
    </button>
  </div>
  <h4 style="color: red;">
    * 해당 페이지는 비상업적 팬 페이지이며 서버비, 저작권 등 사유로 사라질 수 있으니 놀라지 마세요...
  </h4>
  <h4 style="color: black;">
    * 영상은 9시, 13시, 19시, 00시 최신화 될 예정입니다.
  </h4>

  <div>
    <h1 style="margin-bottom: 60px;">
      {{ selectedVideo === 'all' ? '피프티피프티 최신 영상' : '피프티피프티 팬싸 영상' }}
    </h1>
    <div v-if="loading">
      로딩 중...
    </div>
    <div v-else>
      <div
        v-for="video in paginated"
        :key="video.videoId"
        class="video-box"
      >
        <section class="img-box">
          <a
            :href="`https://www.youtube.com/watch?v=${video.videoId}`"
            target="_blank"
          >
            <img
              class="thmbnail-img"
              :src="video.thumbnailUrl"
              alt="썸네일"
            >
          </a>
        </section>
        <section class="info-box">
          <a
            style="margin-top: 10px;"
            :href="`https://www.youtube.com/watch?v=${video.videoId}`"
            target="_blank"
          >{{ video.title }}</a>
          <p>{{ video.channelTitle }} </p>
          <p v-if="selectedVideo !== 'fansign'">
            구독자 {{ formatNumber(video.subscriberCount) }}
          </p>

          <p>조회수: {{ formatNumber(video.viewCount) }}</p>
          <p>
            업로드 날짜:
            {{
              formatDate(video.publishDate)
            }}
          </p>
        </section>
      </div>
    </div>
  </div>

  <div
    class="pagination"
    style="margin-bottom: 200px;"
  >
    <button
      :disabled="currentPage === 1"
      @click="goToPage(currentPage - 1)"
    >
      이전
    </button>
    <span>{{ currentPage }} / {{ totalPages }}</span>
    <button
      :disabled="currentPage === totalPages"
      @click="goToPage(currentPage +1 )"
    >
      다음
    </button>
  </div>
  <FixedButtons @top="scrollToTop" />
  <CheckToday />
</template>
<style scoped>
/* 기존 스타일은 그대로 유지됩니다. */

/* 전체 컨테이너의 최대 너비 조정 */
.video-list-container { /* 이 div는 <template>의 최상위 div를 감싸는 새로운 div라고 가정합니다. */
    max-width: 1200px; /* 데스크톱에서 너무 넓지 않게 최대 너비 설정 */
    margin: 0 auto; /* 중앙 정렬 */
    padding: 20px; /* 좌우 여백 추가 */
}


.a-box {
    display: flex;
    justify-content: start;
    margin-bottom: 20px; /* 하단 여백 추가 */
}

a {
    text-decoration: none;
    color: black;
    border: 1px solid black;
    border-radius: 5px; /* 버튼처럼 보이도록 테두리 둥글게 */
    transition: background-color 0.3s, color 0.3s;
}

a:hover {
    background-color: #f0f0f0;
}

.button-group {
    display: flex;
    justify-content: center;
    gap: 15px; /* 버튼 간 간격 */
    margin-bottom: 40px; /* 하단 여백 */
    flex-wrap: wrap; /* 모바일에서 버튼이 많으면 줄바꿈 */
}

.button-group button {
    padding: 12px 25px; /* 패딩 증가 */
    font-size: 1.1em; /* 폰트 크기 증가 */
    border: 1px solid #ccc;
    border-radius: 8px; /* 버튼 둥글게 */
    background-color: #f9f9f9;
    cursor: pointer;
    transition: background-color 0.3s, color 0.3s, border-color 0.3s;
}

.button-group button.active {
    background-color: #007bff; /* 활성 버튼 색상 */
    color: white;
    border-color: #007bff;
}

.button-group button:hover:not(.active) {
    background-color: #e2e6ea;
}

h1 {
    text-align: center;
    margin-bottom: 60px;
    font-size: 2.2em; /* 제목 크기 조정 */
    color: #333;
}

.video-box {
    display: flex;
    flex-direction: row; /* 기본은 가로 정렬 */
    margin: 0 auto 50px auto; /* 중앙 정렬 및 하단 여백 */
    width: 100%; /* 부모 컨테이너에 맞게 100% 사용 */
    max-width: 1000px; /* 데스크톱에서 최대 너비 제한 */
    border: 1px solid #e0e0e0; /* 테두리 색상 연하게 */
    border-radius: 12px; /* 더 둥글게 */
    overflow: hidden;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); /* 그림자 추가 */
    background-color: white;
}

.img-box {
    width: 350px; /* 썸네일 박스 너비 조정 */
    flex-shrink: 0; /* 내용이 많아도 줄어들지 않게 */
    position: relative; /* 이미지 비율 유지를 위해 */
    overflow: hidden; /* 둥근 모서리 적용을 위해 */
}

.img-box a {
    display: block;
    height: 100%; /* 링크가 전체 영역을 차지하도록 */
}

.thmbnail-img {
    width: 100%;
    height: 100%; /* 부모 높이에 맞춤 */
    object-fit: cover; /* 이미지가 잘리지 않고 채워지도록 */
    display: block; /* 이미지 하단 공백 제거 */
}

.info-box {
    display: flex;
    flex-direction: column;
    flex-grow: 1; /* 남은 공간을 채우도록 */
    padding: 15px 20px; /* 정보 박스 내부 여백 */
    justify-content: center; /* 세로 중앙 정렬 */
}

.info-box a {
    margin-top: 0; /* 기본 마진 제거 */
    font-size: 1.2em; /* 제목 폰트 크기 */
    font-weight: bold;
    line-height: 1.4;
    max-height: 3.2em; /* 2줄 정도만 보이도록 (line-height * 2) */
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2; /* 2줄까지만 표시하고 넘치면 ... */
    -webkit-box-orient: vertical;
    margin-bottom: 10px;
    border: none; /* 링크에 불필요한 테두리 제거 */
    padding: 0; /* 링크에 불필요한 패딩 제거 */
}

.info-box p {
    margin: 5px 0; /* 문단 간 여백 조정 */
    color: #666;
    font-size: 0.95em;
    line-height: 1.5;
}

.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 50px;
    margin-bottom: 50px; /* 하단 여백 추가 */
    gap: 15px; /* 버튼과 텍스트 간 간격 */
}

.pagination button {
    padding: 10px 20px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 1em;
    transition: background-color 0.3s;
}

.pagination button:hover:not(:disabled) {
    background-color: #0056b3;
}

.pagination button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
}

.pagination span {
    font-size: 1.1em;
    font-weight: bold;
    color: #333;
}

/* ==================================================================== */
/* 모바일 화면 최적화 (Media Queries) */
/* ==================================================================== */

@media (max-width: 768px) { /* 태블릿 및 모바일 */
    h1 {
        font-size: 1.8em; /* 모바일에서 제목 크기 줄임 */
        margin-bottom: 40px;
    }

    .button-group {
        flex-direction: column; /* 버튼을 세로로 쌓음 */
        gap: 10px;
        margin-bottom: 30px;
    }

    .button-group button {
        width: 80%; /* 버튼 너비를 넓게 */
        max-width: 300px; /* 최대 너비 제한 */
        margin: 0 auto; /* 중앙 정렬 */
    }

    .video-box {
        flex-direction: column; /* 모바일에서는 세로로 쌓음 */
        max-width: 95%; /* 모바일에서 화면 너비에 거의 꽉 차게 */
        margin-left: auto;
        margin-right: auto;
        margin-bottom: 30px; /* 하단 여백 줄임 */
    }

    .img-box {
        width: 100%; /* 썸네일이 위쪽을 꽉 채우도록 */
        height: auto; /* 높이 자동 조절 */
        /* 모바일에서 16:9 비율 유지 (예: 너비의 56.25%) */
        padding-bottom: 56.25%; /* 16:9 비율 (9 / 16 = 0.5625) */
        position: relative;
    }

    .thmbnail-img {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    .info-box {
        padding: 15px; /* 모바일에서 패딩 줄임 */
    }

    .info-box a {
        font-size: 1.1em; /* 모바일에서 제목 폰트 크기 줄임 */
        margin-bottom: 8px;
    }

    .info-box p {
        font-size: 0.9em; /* 모바일에서 정보 폰트 크기 줄임 */
        margin: 3px 0;
    }

    .pagination {
        margin-top: 30px;
        margin-bottom: 30px;
        gap: 10px;
    }

    .pagination button {
        padding: 8px 15px;
        font-size: 0.9em;
    }

    .pagination span {
        font-size: 1em;
    }
}

@media (max-width: 480px) { /* 더 작은 모바일 화면 */
    h1 {
        font-size: 1.5em;
        margin-bottom: 30px;
    }
    .button-group button {
        font-size: 1em;
        padding: 10px 20px;
    }
    .info-box a {
        font-size: 1em;
    }
    .info-box p {
        font-size: 0.85em;
    }
}
</style>