<template>
  <div class="rank">
    <h1 style="margin-bottom: 10px;">
      조회수 TOP 10 영상
    </h1>
   
      
    <div class="slider-container">
      <div
        ref="sliderRef"
        class="slider-wrapper"
      >
        <div
          v-for="(video, index) in repeatedVideos"
          :key="index + '-' + video.videoId"
          class="slide-item"
          style="position: relative;"
          @click="goToVideo(video.videoUrl)"
        >
          <!-- 1~3등 랭크 표시 -->
          <div
            v-if="video.rank && video.rank <= 3"
            class="rank-badge top-highlight"
          >
            TOP {{ video.rank }}
          </div>
          <div
            v-else-if="video.rank && video.rank <= 10"
            class="rank-badge top-regular"
          >
            TOP {{ video.rank }}
          </div>

          <img
            :src="video.thumbnailUrl"
            :alt="video.title"
            class="thumbnail"
          >
          <div class="info">
            <p
              class="title"
              style=" font-size: 20px;"
            >
              {{ video.title }}
            </p>
            <p
              class="views"
              style=" font-size: 15px;"
            >
              {{ formatViews(video.viewCount) }} views
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted, computed, onBeforeUnmount } from 'vue'
import axiosCt from '../../api/chart'



const videoitems = ref<any[]>([]);
const currentOffset = ref(0);
const sliderRef = ref<HTMLElement | null>(null);
let timer: number | undefined;

const repeatedVideos = computed(() => {
  return [...videoitems.value, ...videoitems.value].map((video, i) => {
    const baseIndex = i % videoitems.value.length;
    return {
      ...video,
      rank: baseIndex < 11 ? baseIndex + 1 : null,
    };
  });
});


const fetchMusic = async () => {
  const response = await axiosCt.getYoutubeChart();
  videoitems.value = response.data;
};

const formatViews = (num: number) => {
  if (num >= 1_000_000) return (num / 1_000_000).toFixed(1) + 'M';
  if (num >= 1_000) return (num / 1_000).toFixed(1) + 'K';
  return num.toString();
};

const goToVideo = (url: string) => {
  window.open(url, '_blank');
};

const slideSpeed = 0.5; // px/frame

const slide = () => {
  if (!sliderRef.value) return;

  currentOffset.value += slideSpeed;

  const totalWidth = sliderRef.value.scrollWidth / 2; // 원본 한 세트 너비 (반복된 두 세트 중 한 세트 길이)

  if (currentOffset.value >= totalWidth) {
    currentOffset.value = 0;
  }

  sliderRef.value.style.transform = `translateX(-${currentOffset.value}px)`;
};


const startSliding = () => {
  timer = window.setInterval(slide, 16);
};

const stopSliding = () => {
  if (timer) clearInterval(timer);
};

onBeforeUnmount(() => {
  stopSliding();
});

onMounted(async () => {
  await fetchMusic();
  startSliding();
});


</script>


<style scoped src="@/assets/style/mainpage.css"></style>
