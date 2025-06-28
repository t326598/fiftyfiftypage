<script lang="ts" setup>
import { ref, onMounted,computed, watch } from 'vue';
import axios from '@/api/chart';
import type{YoutubeVideo} from '@/api/chart';


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

onMounted(() => {
  fetchVideos();
});

</script>

<template>

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

  <div>
     <h1 style="margin-bottom: 60px;">
      {{ selectedVideo === 'all' ? '피프티피프티 최신 영상' : '피프티피프티 팬싸 영상' }}
    </h1>
    <div v-if="loading">로딩 중...</div>
    <div v-else>
      <div  class="video-box" v-for="video in paginated" :key="video.videoId">
        <section class="img-box">
      <a :href="`https://www.youtube.com/watch?v=${video.videoId}`" target="_blank">
        <img class="thmbnail-img" :src="video.thumbnailUrl" alt="썸네일" />
      </a>
        </section>
        <section class="info-box">
          <a style="margin-top: 10px;" :href="`https://www.youtube.com/watch?v=${video.videoId}`" target="_blank">{{ video.title }}</a>
          <p>{{ video.channelTitle }} - 구독자 {{ formatNumber(video.subscriberCount) }}</p>

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

  <div class="pagination">
    <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 1">이전</button>
    <span>{{ currentPage }} / {{ totalPages }}</span>
    <button @click="goToPage(currentPage +1 )" :disabled="currentPage === totalPages">다음</button>
  </div>
</template>

<style scoped>
.video-box{
  display: flex;
  flex-direction: row;
  margin: auto;
  justify-content: center;
  width: 1000px;
  max-height: 180px;
  border: 1px solid #000;
  margin-bottom: 50px;
  border-radius: 20px;
  overflow: hidden;
}
.info-box{
  display: flex;
  flex-direction: column;
  width: 800px;
  
}

.thmbnail-img{
  width: 100%;
}

.img-box{
  width: 300px;
}

.video-box img,
.video-box a,
.video-box p {
  margin-bottom: -3px;
}

.button-group{

}
</style>
