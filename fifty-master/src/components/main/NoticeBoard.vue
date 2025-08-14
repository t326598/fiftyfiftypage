<template>
  <div class="notice">
    <h3 class="notice-title">
      📢 공지사항
    </h3>
    <div class="notice-card">
      <ul class="notice-list">
        <li
          v-for="(item, index) in notices"
          :key="index"
          class="notice-item"
          @click="openPopup(item)"
        >
          {{ item.title }}
        </li>
      </ul>
    </div>

    <!-- 팝업창 -->
    <div
      v-if="selectedNotice"
      class="modal-overlay"
      @click.self="closePopup"
    >
      <div class="modal-content1">
        <h2 style="color: black;">
          {{ selectedNotice.title }}
        </h2>
        <p
          class="content"
          style="font-size: 30px; color: black;"
        >
          {{ selectedNotice.content }}
        </p>
           
       
        <component
          :is="selectedNotice.contentComponent"
          v-if="selectedNotice.contentComponent"
        />
        <div class="meta">
          <p style="color: black;">
            등록일자: {{ formatKoreanDateTime(selectedNotice.createdAt) }}
          </p>
          <p style="color: black;">
            수정일자: {{ formatKoreanDateTime(selectedNotice.updatedAt) }}
          </p>
        </div>
        <button
          class="close-btn"
          @click="closePopup"
        >
          닫기
        </button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from '../../api/notice'
import PinnedNotice from '@/components/main/PinnedNotice.vue'


const notices = ref([]);

async function fetchNotice() {
  try {
    const response = await axios.ListNotice()
    notices.value = response.data
    console.log(response.data)
    notices.value = [pinnedNotice, ...response.data]
  } catch (error) {
    console.error('공지사항을 불러오는데 실패했습니다.:', error)
  }
}


const pinnedNotice = {
  id: 'pinned',
  title: '[고정] 사이트 이용방법',
  contentComponent: PinnedNotice,
  createdAt: new Date().toISOString(),
  updatedAt: new Date().toISOString(),
  isPinned: true
}

const selectedNotice = ref(null)

function formatKoreanDateTime(dateString: string | null): string {
  if (!dateString) return '';
  const date = new Date(dateString);

  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hours = date.getHours();
  const minutes = date.getMinutes().toString().padStart(2, '0'); 
  return `${year}년 ${month}월 ${day}일 ${hours}시 ${minutes}분`;
}

function openPopup(item: any) {
  selectedNotice.value = item
}

function closePopup() {
  selectedNotice.value = null
}

onMounted(async () => {
  fetchNotice();
});
</script>

<style scoped src="@/assets/style/mainpage.css"></style>