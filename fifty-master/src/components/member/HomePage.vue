<template>
  <div :class="['mainbody', { 'dark-mode': isDark }]">
    <!-- 다크모드 토글 버튼 -->
    <button @click="toggleTheme" class="theme-toggle">{{ isDark ? '☀️' : '🌙' }}</button>

    <!-- 로고 -->
    <div class="header">
      <transition :name="isDark ? 'slide-dark' : 'slide-light'" mode="out-in">
        <img :key="isDark ? 'dark-logo' : 'light-logo'"
          :src="isDark ? require('@/assets/image/test.png') : require('@/assets/image/3333.svg')" alt="fiftyfifty"
          class="fifty-img" />
      </transition>
    </div>

    <!-- 메인 이미지 -->
    <section class="hero-image" style="margin-bottom: 30px;">
      <transition :name="isDark ? 'slide-dark' : 'slide-light'" mode="out-in">
        <img :key="isDark ? 'dark-hero' : 'light-hero'"
          :src="isDark ? require('@/assets/image/darkmidnight.png') : require('../../assets/image/tofifty.png')"
          class="hero-image1" alt="FIFTY FIFTY Members" style="border-radius: 20px; width: 60%;" />
      </transition>
    </section>

    <section class="community">
      <a href="https://www.youtube.com/@WE_FIFTYFIFTY" target="_blank">
        <img src="@/assets/image/yb.png" alt="YouTube" class="social-icon" />
        <p>FIFTY FIFTY YOUTUBE</p>
      </a>
      <a href="https://www.instagram.com/we_fiftyfifty" target="_blank">
        <img src="@/assets/image/is.jpg" alt="Instagram" class="social-icon" />
        <p>FIFTY FIFTY INSTAGRAM</p>
      </a>
      <a href="https://www.tiktok.com/@we_fiftyfifty" target="_blank">
        <img src="@/assets/image/tt.png" alt="TikTok" class="social-icon" />
        <p>FIFTY FIFTY TIKTOK</p>
      </a>
      <a href="https://weverse.io/fiftyfifty" target="_blank">
        <img src="@/assets/image/wb.jpg" alt="Weverse" class="social-icon" />
        <p>FIFTY FIFTY WEVERSE</p>
      </a>
    </section>
    <h1>일정표</h1>
    <section class="calendar-wrapper">
      <div class="calendar-content">
        <!-- 배경 이미지 -->
        <div class="calendar-background"
          :style="{ backgroundImage: `url('http://localhost:8080/upload/${backgroundImage}')` }"></div>

        <!-- 캘린더 -->
        <CalendarLegend />
        <div :class="{ 'dark-calendar': isDark }">
          <FullCalendar :options="calendarOptions" />
        </div>
        <!-- 모달 -->
        <div v-if="isModalOpen" class="modal-overlay" @click.self="isModalOpen = false">
          <div class="modal-content1">
            <h1>{{ selectedEvent.title }}</h1>
            <h2>{{ formatDateDay(selectedEvent.start) }}</h2>
            <p> {{ formatDate(selectedEvent.start) }} ~ {{ formatDate(selectedEvent.end) }}</p>
            <p>{{ selectedEvent.description }}</p>
            <button @click="isModalOpen = false" class="close-btn">닫기</button>
          </div>
        </div>
      </div>
    </section>
    <section class="notice">
      <h3 class="notice-title">📢 공지사항</h3>
      <div class="notice-card">
        <ul class="notice-list">

          <li v-for="(item, index) in notices" :key="index" @click="openPopup(item)" class="notice-item">
            {{ item.title }}
          </li>
        </ul>
      </div>

      <!-- 팝업창 -->
      <div v-if="selectedNotice" class="modal-overlay" @click.self="closePopup">
        <div class="modal-content1">
          <h2>{{ selectedNotice.title }}</h2>
          <p class="content" style="font-size: 30px;">{{ selectedNotice.content }}</p>
          <div class="meta">
            <p>등록일자: {{ formatKoreanDateTime(selectedNotice.createdAt) }}</p>
            <p>수정일자: {{ formatKoreanDateTime(selectedNotice.updatedAt) }}</p>
          </div>
          <button @click="closePopup" class="close-btn">닫기</button>
        </div>
      </div>
    </section>
    <hr class="section-divider" />
    <section class="rank">
      <h1 style="margin-bottom: 10px;">조회수 TOP 10 영상</h1>
      <div class="slider-container">
        <div class="slider-wrapper" ref="sliderRef">
          <div class="slide-item" v-for="(video, index) in repeatedVideos" :key="index + '-' + video.videoId"
            @click="goToVideo(video.videoUrl)" style="position: relative;">
            <!-- 1~3등 랭크 표시 -->
            <div v-if="video.rank && video.rank <= 3" class="rank-badge top-highlight">TOP {{ video.rank }}</div>
            <div v-else-if="video.rank && video.rank <= 10" class="rank-badge top-regular">
              TOP {{ video.rank }}
            </div>

            <img :src="video.thumbnailUrl" :alt="video.title" class="thumbnail" />
            <div class="info">
              <p class="title" style=" font-size: 20px;">{{ video.title }}</p>
              <p class="views" style=" font-size: 15px;">{{ formatViews(video.viewCount) }} views</p>
            </div>
          </div>
        </div>
      </div>
    </section>
    <hr class="section-divider" />
    <h1 style="margin-bottom: 10px;">멤버 프로필</h1>
    <section class="profile" style="margin-top: 10px;">
      <div class="card-grid">
        <div v-for="member in profile" :key="member.no" class="member-card" @click="showProfileBox(member)">
          <img v-if="!isSmallScreen" :src="`http://localhost:8080/upload/${member.filePath}`" :alt="member.title"
            class="member-img" />
          <p v-else class="member-text">{{ member.title }}</p>
        </div>
      </div>
    </section>

    <!-- 프로필 박스 -->
    <transition name="slide-down" @after-leave="handleAfterLeave">
      <section v-if="showProfile" class="profile-filter-section" :key="selectedMemberNo?.memberNo">
        <div class="profile-box">
          <div class="profile-box-content" v-if="selectedMemberNo">
            <section style="width: 30%; overflow: hidden;">
              <img :src="`http://localhost:8080/upload/${selectedMemberNo.filePath}`" :alt="selectedMemberNo.title"
                class="member-profile" style="width: 259px; height: 323px; object-fit: cover;" />
            </section>
            <section style="width: 60%; text-align: center;">
              <h1 style="font-size: 40px;">{{ selectedMemberNo.title }}</h1>
              <h2 style="color: black; font-size: 25px;">"{{ selectedMemberNo.subContent }}"</h2>
              <p style="color: black; font-size: 25px;">{{ selectedMemberNo.content }}</p>
            </section>
          </div>
        </div>
        <button v-if="showProfile" class="close-btn" @click="closeProfileBox">닫기</button>
      </section>
    </transition>
    <section class="card-image-wrapper">
      <div class="card-image">
        <div v-for="member in displayedMembers" :key="member.no" class="member-card-image"
          @click="openModal(`http://localhost:8080/upload/${member.name}`)">
          <img :src="`http://localhost:8080/upload/${member.name}`" :alt="member.name" class="member-img" />
        </div>
      </div>
    </section>
    <div class="pagination" style="margin-top: 50px; color: black;">
      <button @click="changePage('prev')" :disabled="currentPage === 1">Previous</button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <button @click="changePage('next')" :disabled="currentPage === totalPages">Next</button>
    </div>
    <footer class="footer">
      <div class="footer-content">
        <p>&copy; 2025 Your Company. All rights reserved.</p>
        <ul class="footer-links">
          <li><a href="#">이용약관</a></li>
          <li><a href="#">개인정보처리방침</a></li>
          <li><a href="#">문의하기</a></li>
        </ul>
      </div>
    </footer>
  </div>

  <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      <img :src="selectedImage" alt="확대 이미지" class="modal-img" />
      <button class="modal-close" @click="closeModal">×</button>
    </div>
  </div>

  <div class="fixed-buttons">
    <button class="top-button" @click="scrollToTop">
      ⬆️ 최상단
    </button>
    <a href="https://open.kakao.com/" target="_blank" rel="noopener noreferrer" class="kakao-chat-button">
      💬 카카오톡 문의
    </a>

  </div>



</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, reactive, onBeforeUnmount } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import axios from '../../api/files'
import axiospr from '../../api/profile'
import axiosCt from '../../api/chart';
import { useRoute, useRouter } from 'vue-router'
import koLocale from '@fullcalendar/core/locales/ko';
import listPlugin from '@fullcalendar/list';
import CalendarLegend from '../sub/CalendarLegend.vue'


const backgroundImage = ref('');


// 공지사항 목록 (예시 데이터)
const notices = ref([]);

const isDark = ref(false);

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth',
  });
};

const isModalOpen = ref(false);
const selectedEvent = ref({
  title: '',
  start: null,
  end: null,
  description: ''
});

function toggleTheme() {
  isDark.value = !isDark.value;
}

// 일정
function formatDate(date: Date | null) {
  if (!date) return '';
  return new Intl.DateTimeFormat('ko-KR', {
    hour: '2-digit', minute: '2-digit'
  }).format(date);
}

function formatDateDay(date: Date | null) {
  if (!date) return '';
  const year = date.getFullYear();
  const month = date.getMonth() + 1; // 0-based
  const day = date.getDate();
  return `${year}년 ${month}월 ${day}일`;
}
//  공지사항
function formatKoreanDateTime(dateString: string | null): string {
  if (!dateString) return '';
  const date = new Date(dateString);

  const year = date.getFullYear();
  const month = date.getMonth() + 1; // 월은 0부터 시작
  const day = date.getDate();
  const hours = date.getHours();
  const minutes = date.getMinutes().toString().padStart(2, '0'); // 1자리면 앞에 0 추가

  return `${year}년 ${month}월 ${day}일 ${hours}시 ${minutes}분`;
}



async function fetchBackground(month: number) {
  try {
    const response = await axiospr.backgroundList(month);
    backgroundImage.value = response.data.imageUrl;
  } catch (error) {
    console.error(error);
  }
}

async function fetchNotice() {
  try {
    const response = await axiospr.ListNotice()
    notices.value = response.data
    console.log(response.data)
    // 받아온 데이터 구조에 따라 처리

  } catch (error) {
    console.error('공지사항을 불러오는데 실패했습니다.:', error)
  }
}


const selectedNotice = ref(null)

function openPopup(item: any) {
  selectedNotice.value = item
}

function closePopup() {
  selectedNotice.value = null
}



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

  const itemWidth = sliderRef.value.scrollWidth / repeatedVideos.value.length;
  const totalWidth = itemWidth * videoitems.value.length;

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




const route = useRoute()
const router = useRouter()

const currentPage = ref(Number(route.query.page) || 1)

// 페이지 변경 시 쿼리 업데이트
watch(currentPage, (newPage) => {
  router.replace({ query: { ...route.query, page: newPage } })
})
const isSmallScreen = ref(window.innerWidth <= 480)

const showModal = ref(false)
const selectedImage = ref('')
const showProfile = ref(false) // 프로필 박스를 보여줄지 여부
const allImagesLoaded = ref(false)
const crt = ref('')

const selectedMemberNo = ref([]);
const calendarEvents = ref<any[]>([])


// 페이징
const totalItems = ref(0)
const itemsPerPage = 15 // 한 페이지에 15명씩

const members = ref([]);

const profile = ref([]);
const colors = ref([]);

const totalPages = computed(() => {
  return members.value.length === 0
    ? 1
    : Math.ceil(totalItems.value / itemsPerPage);
});

// 현재 페이지의 멤버들만 추출
const displayedMembers = computed(() => {
  const start = (currentPage.value - currentPage.value) * itemsPerPage
  const end = start + itemsPerPage
  return members.value.slice(start, end)
})

async function fetchMembers() {
  try {
    const response = await axios.fetchFiles({
      crt: crt.value,
      page: currentPage.value - 1,
      rows: itemsPerPage,  // ← 'rows'로 전달
    })
    console.log(response.data)
    // 받아온 데이터 구조에 따라 처리
    members.value = response.data.list || []       // 'list' 또는 'members' 키를 서버 응답에 맞게
    totalItems.value = response.data.total || 0    // 'total' 키를 서버 응답에 맞게
    allImagesLoaded.value = true
  } catch (error) {
    console.error('멤버 목록을 불러오는 데 실패했습니다:', error)
  }
}

async function fetchProfile() {
  try {
    const response = await axiospr.ListProfile()
    profile.value = response.data.list || []
  }
  catch (error) {
    console.error('멤버 목록을 불러오는 데 실패했습니다:', error)
  }
}

// 페이지 변경
function changePage(direction: 'next' | 'prev') {
  if (direction === 'next' && currentPage.value < totalPages.value) {
    currentPage.value++
  } else if (direction === 'prev' && currentPage.value > 1) {
    currentPage.value--
  }
}

watch(currentPage, () => {
  fetchMembers()
})

watch(crt, () => {
  currentPage.value = 1;
  fetchMembers();
});


// 프로필 박스 열기 함수 수정
function showProfileBox(member: any) {
  selectedMemberNo.value = member
  crt.value = member.no
  fetchMembers()
  console.log(crt.value)
  showProfile.value = true
}

// 프로필 박스 닫기
function closeProfileBox() {
  showProfile.value = false
}

function handleAfterLeave() {
  selectedMemberNo.value = []
  crt.value = ""
  fetchMembers()
}

async function fetchPlan() {
  try {
    const response = await axiospr.ListPlan()
    calendarEvents.value = response.data || []
    colors.value = response.data.map(event => event.backgroundColor);

  } catch (error) {
    console.error('일정 목록을 불러오는 데 실패했습니다:', error)
  }
}

function openModal(imageUrl: string) {
  selectedImage.value = imageUrl
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}


const calendarOptions = reactive({
  plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin, listPlugin],
  locale: koLocale,
  dayMaxEventRows: true,
  eventDisplay: 'block',
  initialView: window.innerWidth < 920 ? 'listWeek' : 'dayGridMonth',
  headerToolbar: {
    left: 'prev,next today',
    center: 'title',
    right: window.innerWidth < 920 ? '' : 'dayGridMonth,timeGridWeek,timeGridDay',
  },
  datesSet: (info: any) => {
    const currentStartDate = new Date(info.view.currentStart);
    const month = currentStartDate.getMonth() + 1;
    fetchBackground(month);
  },

  buttonText: {
    today: '오늘',
    month: '월간',
    week: '주간',
    day: '일간',
    list: '리스트',
  },

  events: calendarEvents,

  // ✅ 일정(이벤트)에 호버 시 커스터마이징
  eventDidMount(info: any) {
    info.el.style.position = 'relative'; // 또는 'absolute'
    info.el.style.zIndex = '10';
    info.el.style.transition = 'transform 0.2s ease';
    info.el.addEventListener('mouseenter', () => {
      info.el.style.transform = 'scale(1.05)';
      info.el.style.boxShadow = '0 4px 10px rgba(0, 0, 0, 0.2)';
      info.el.style.zIndex = '10';
      info.el.style.cursor = 'pointer';
    });
    info.el.addEventListener('mouseleave', () => {
      info.el.style.transform = 'scale(1)';
      info.el.style.boxShadow = '';
      info.el.style.zIndex = '';
    });
  },
  // ✅ 날짜 셀에 마우스 올렸을 때 일정 툴팁 표시
  dayCellDidMount(info: any) {
    const dateStr = info.date.toISOString().slice(0, 10); // yyyy-mm-dd
    const eventsForThisDay = calendarEvents.value.filter(event =>
      event.start.startsWith(dateStr)
    );

    if (eventsForThisDay.length > 0) {
      const tooltipContent = eventsForThisDay
        .map(ev => `• ${ev.title} (${new Date(ev.start).getHours()}시)`)
        .join('\n');

      // HTML title 속성에 툴팁 텍스트 삽입
      info.el.setAttribute('title', tooltipContent);
    }
  },
  eventClick(info: any) {
    const event = info.event;
    selectedEvent.value = {
      title: event.title,
      start: event.start,
      end: event.end,
      description: event.extendedProps.description || ''
    };
    isModalOpen.value = true;
  }

});



onMounted(async () => {
  await fetchMusic();
  startSliding();
  fetchMembers();
  fetchProfile();
  fetchPlan();
  fetchNotice();
});


</script>

<style scoped>
.dark-calendar .fc {
  background-color: #121212;
  color: #ffffff;
}

.dark-calendar .fc-event {
  background-color: #333333;
  border-color: #555555;
  color: #ffffff;
}

.footer {
  background-color: #f4f4f4;
  padding: 20px 40px;
  text-align: center;
  border-top: 1px solid #ccc;
}

.footer-content {
  max-width: 960px;
  margin: 0 auto;
}

.footer p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin-top: 10px;
  display: flex;
  justify-content: center;
  gap: 15px;
}

.footer-links li a {
  text-decoration: none;
  color: #007acc;
  font-size: 14px;
}

.footer-links li a:hover {
  text-decoration: underline;
}

.fixed-buttons {
  position: fixed;
  bottom: 20px;
  right: 20px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
  z-index: 1000;
}

.kakao-chat-button,
.top-button {
  padding: 12px 16px;
  border-radius: 50px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  font-weight: bold;
  font-size: 14px;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

/* 카카오톡 버튼 스타일 */
.kakao-chat-button {
  background-color: #fae100;
  color: #000;
}

.kakao-chat-button:hover {
  background-color: #ffe000;
  transform: translateY(-2px);
}

/* 최상단 버튼 스타일 */
.top-button {
  background-color: #333;
  color: white;
  border: none;
}

.top-button:hover {
  background-color: #555;
  transform: translateY(-2px);
}


.section-divider {
  border: none;
  height: 2px;
  background: linear-gradient(to right, #ccc, #eee, #ccc);
  margin: 3vh auto;
  width: 80%;
}

.gallery-section {
  background-color: #f9f9f9;
}


.rank-badge {
  position: absolute;
  top: 2px;
  left: 9px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: bold;
}

.top-highlight {
  background-color: #ffcc00;
  color: #000;
}

.top-regular {
  background-color: #e0e0e0;
  color: #333;
}

.calendar-legend {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  justify-content: flex-end;
  width: 100%;
}

.calendar-wrapper {
  display: flex;
  justify-content: center;
  height: 100%;
  margin-top: 10px;
}



.calendar-content {
  padding: 50px;
  color: #000000;
  font-weight: bold;
  position: relative;
  z-index: 1;
  width: 50%;
  overflow: hidden;
  /* 이 부분이 중요! 내부 내용만 보여줍니다 */
  border-radius: 5%;
}

.calendar-background {
  position: absolute;
  filter: blur(2px);
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  opacity: 0.3;
  z-index: 0;
}


.notice-title {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 16px;
  color: #ff4d6d;
}

.notice {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  /* 수평 정렬 */
  padding: 5px;
  text-align: center;
  /* 텍스트 중앙 정렬 */
}

.notice-list {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  /* 수평 정렬 */
  padding: 5px;
  text-align: center;
  /* 텍스트 중앙 정렬 */
  color: black;
  font-size: 20px;
  list-style: none;
}

.notice-item {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  margin-bottom: 5px;
  cursor: pointer;
  transition: background 0.2s;
  border-radius: 8px;
}

.notice-item:hover {
  background-color: #f5f5f5;
  transition: transform 0.3s ease;
}

.notice-card {
  background: rgba(255, 255, 255, 0.25);
  border: 1px solid #f8bbd0;
  padding-bottom: 10px;
  border-radius: 1rem;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  width: 60%;
  margin-top: 20px;
}


/* 팝업 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal-content1 {
  background-color: white;
  color: black;
  padding: 40px;
  border-radius: 16px;
  width: 50%;
  max-width: 800px;
  max-height: 80%;
  overflow-y: auto;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  animation: slideBounceIn 0.4s ease;
}

.modal-content h2 {
  margin-top: 0;
  font-size: 28px;
}

.modal-content .content {
  margin: 24px 0;
  font-size: 18px;
  white-space: pre-wrap;
}

.modal-content .meta {
  font-size: 14px;
  color: #666;
  margin-bottom: 24px;
}

/* 애니메이션 */
@keyframes slideBounceIn {
  0% {
    transform: translateX(-100%) scale(0.95);
    opacity: 0;
  }

  60% {
    transform: translateX(10%) scale(1.02);
    opacity: 1;
  }

  80% {
    transform: translateX(-5%) scale(0.98);
  }

  100% {
    transform: translateX(0) scale(1);
  }
}

.fifty-img {
  margin: 150px 0 0px 0px;
  position: relative;
  width: 50%;
  min-width: 600px;
  height: auto;
  z-index: 2;

}

.title,
.views {
  color: black
}

.hero-image1 {
  min-width: 600px;
  max-width: 1200px;
  max-height: 469.7px;
  margin: -30px 0 0 0;
  z-index: 1;
  position: relative;
}

.mainbody {
  position: relative;
  z-index: 10;
  transition: background 1s ease-in-out;
  background: linear-gradient(to bottom, #ffffff 0%, #fdd7ef 8%, #f7e0ee 85%, #ffffff 100%);
  background-size: 100% auto;
  height: 100%;
  overflow-x: auto;

  &.dark-mode {
    background-image: none;
    background-color: #333;
    z-index: 11;
    height: 100%;
    color: white;
  }
}

.mainbody::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('@/assets/image/darkback.png');
  transform: translateX(-100%);
  transition: transform 1s ease-in-out;
  z-index: -1;
}

.mainbody.dark-mode::before {
  transform: translateX(0);
}

/* 🌞 → 🌙 : 왼쪽에서 들어오기 */
.slide-dark-enter-from {
  opacity: 0;
  transform: translateX(-50px);
}

.slide-dark-enter-active {
  transition: all 0.6s ease;
}

.slide-dark-leave-to {
  opacity: 0;
  transform: translateX(50px);
}

.slide-dark-leave-active {
  transition: all 0.6s ease;
}

/* 🌙 → 🌞 : 오른쪽에서 들어오기 */
.slide-light-enter-from {
  opacity: 0;
  transform: translateX(50px);
}

.slide-light-enter-active {
  transition: all 0.6s ease;
}

.slide-light-leave-to {
  opacity: 0;
  transform: translateX(-50px);
}

.slide-light-leave-active {
  transition: all 0.6s ease;
}


.theme-toggle {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1000;
  background: transparent;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

.profile-box {
  background-color: white;
  border-radius: 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  gap: 40px;
  margin: 30px auto;
  width: 1000px;
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.7s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  transform: translateY(-20px);
  opacity: 0;
}

.slide-down-leave-from {
  transform: translateY(0);
  opacity: 1;
}


.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}


.profile-box-content {
  display: flex;
  justify-content: space-around;
  text-align: left;
  padding: 20px;
  width: 100%;
}

.close-btn {
  background-color: #f5b8cc;
  color: rgb(0, 0, 0);
  border: none;
  padding: 10px 20px;
  border-radius: 12px;
  margin: 20px auto;
  display: block;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.close-btn:hover {
  background-color: #ec407a;
  cursor: pointer;
}

.member-text {
  color: #333;
  font-size: 18px;
  font-weight: bold;
}

h1 {
  color: #ff4d6d;
  font-size: 30px;
  letter-spacing: 1px;

}

.mainbody h1 {
  margin: 0;
}

.rank {
  height: 400px;
}

/* 한줄로 나열 */
.community {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 20px;
  max-width: 1500px;
  margin: 0 auto;
  margin-bottom: 40px;
  padding: 30px 0;
  box-sizing: border-box;
}


.community a {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  text-decoration: none;
  color: rgb(252, 252, 252);
  width: 100%;
  max-width: 300px;
  padding: 10px;
  background-image: linear-gradient(to bottom, #7D64BD, #B166B6 25%, #CF8AB4 80%);
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.calander {
  margin: 30px auto;
  width: 80%;
  max-width: 1200px;
  overflow-x: auto;
  font-weight: bold;
  color: black;
}

.profile {
  height: 400px;
}

.card-image-wrapper {
  margin-top: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.card-image {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 60px;
  padding: 20px;
}

.card-grid {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 20px;
}

.profile-filter-section {
  background: linear-gradient(to bottom, #ffdeeb, #faf5f6);
  padding: 1px;
  width: 1300px;
  margin: auto;
  border-radius: 50px;
}

.member-card {
  border-radius: 20px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  padding: 10px;
  background: #fff0f6;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  width: 200px;
  height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.member-card-image {
  border-radius: 20px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  width: 250px;
  height: 350px;
}

@media (max-width: 1380px) {
  .header1 {

    font-size: 120px;
  }

  .profile-box {
    margin-top: 20px;
    overflow: hidden;
    width: 800px;
  }

  .profile-filter-section {
    background: linear-gradient(to bottom, #fda4c8, #fce4ec);
    padding: 10px;
    width: 1000px;
    margin: auto;
    border-radius: 50px;
  }

  .community {
    width: 100%;
    grid-template-columns: 1fr;
  }

  .card-image {
    grid-template-columns: repeat(4, 1fr);
  }

  .pagination button {
    padding: 12px 25px;
    font-size: 16px;
  }
}

@media (max-width: 1080px) {
  .card-image {
    grid-template-columns: repeat(3, 1fr);
  }

  .pagination button {
    padding: 12px 25px;
    font-size: 16px;
  }

  .profile-box {
    margin-top: 20px;
    overflow: hidden;
    width: 600px;
  }

  .profile-filter-section {
    background: linear-gradient(to bottom, #fda4c8, #fce4ec);
    padding: 10px;
    width: 800px;
    margin: auto;
    border-radius: 50px;
  }
}

@media (max-width: 920px) {
  .community {
    width: 100%;
    grid-template-columns: 1fr;
  }

  .card-image {
    grid-template-columns: repeat(2, 1fr);
  }

  .pagination button {
    padding: 12px 25px;
    font-size: 16px;
  }

  .profile-box {
    margin-top: 20px;
    overflow: hidden;
    width: 300px;
  }

  .profile-filter-section {
    background: linear-gradient(to bottom, #fda4c8, #fce4ec);
    padding: 10px;
    width: 500px;
    margin: auto;
    border-radius: 50px;
  }

  @media (max-width: 520px) {
    .community {
      grid-template-columns: 1fr;
    }

    .card-image {
      grid-template-columns: 1fr;
    }

    .pagination button {
      padding: 10px 20px;
      font-size: 14px;
    }

    .profile-box {
      font-size: 10px;
      margin-top: 20px;
      overflow: hidden;
      width: 300px;
    }

    .profile-filter-section {
      background: linear-gradient(to bottom, #fda4c8, #fce4ec);
      padding: 10px;
      width: 400px;
      margin: auto;
      border-radius: 50px;
    }

    .profile-box-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      gap: 15px;
      padding: 20px;
    }
  }

  @media (max-width: 440px) {
    .calendar-wrapper {
      width: 450px;
      margin-left: -12%;
    }

    .fc {
      font-size: 11px;
    }

    .fc-header-toolbar {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4px;
    }

    .fc .fc-button {
      font-size: 10px !important;
      padding: 2px 6px !important;
    }

    .fc-list-day {
      font-size: 12px;
      padding: 4px 6px;
    }

    .fc-list-event-title,
    .fc-list-event-time {
      font-size: 11px;
    }

    .fc-header-toolbar {
      flex-wrap: wrap;
      justify-content: center;
    }

    #calendar {
      padding: 5px;
      max-width: 100%;
    }
  }

}

.pagination button {
  background-color: white;
  color: black;
  font-size: 18px;
  padding: 10px 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  margin-bottom: 100px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.pagination button:hover {
  background-color: #f0f0f0;
}

.pagination button:disabled {
  background-color: #e0e0e0;
  cursor: not-allowed;
}

.pagination span {
  font-size: 18px;
  margin: 0 10px;
}

.member-card-image:hover {
  transform: scale(1.05);
}

.member-card:hover,
.member-card-image:hover {
  transform: scale(1.05);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
}

.member-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 15px;
}

.social-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.modal-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
}

.modal-img {
  height: 500px;
  object-fit: contain;
  border-radius: 20px;
}

.modal-close {
  position: absolute;
  top: 10px;
  right: 20px;
  font-size: 28px;
  background: none;
  color: white;
  border: none;
  cursor: pointer;
}

.slider-container {
  overflow-x: auto;
  width: 100%;
  padding-bottom: 20px;
  -ms-overflow-style: none;
  /* IE/Edge */
  scrollbar-width: none;
  /* Firefox */
}

.slider-container::-webkit-scrollbar {
  display: none;
}

.slider-wrapper {
  display: flex;
  justify-content: flex-start;
  /* 왼쪽부터 붙이기 */
  padding: 0 20px;
  gap: 20px;
  /* slide 사이 간격도 가능 */
  width: max-content;
  /* 내용 너비만큼만 */
}


.slide-item {
  flex: 0 0 auto;
  width: 320px;
  padding: 10px;
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: transform 0.3s ease;

}

.slide-item:hover {
  transform: scale(1.2) !important;
  cursor: pointer;

}



.thumbnail {
  width: 280px;
  height: 158px;
  object-fit: cover;
  border-radius: 6px;
  margin-bottom: 6px;
}

.info {
  width: 280px;
}

.title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.views {
  font-size: 14px;
  color: #666;
}


.dark-mode h1,
.dark-mode h2,
.dark-mode h3,
.dark-mode p,
.dark-mode span,
.dark-mode li,
.dark-mode a {
  color: white;
}

.dark-mode .footer {
  background-color: #333;
  color: white;
}

.dark-mode .footer {
  color: #ccc;
}

.dark-mode .modal-content1,
.dark-mode .notice-card,
.dark-mode .calendar-wrapper,
.dark-mode .profile-box-content,
.dark-mode .card-grid {
  color: white;
}

.dark-mode .member-profile,
.dark-mode .member-img {
  border: 1px solid #333;
}

.dark-mode .rank-badge {
  color: #fff;
  /* 밝은 글자색 */
}

.dark-mode .rank-badge.top-highlight {
  background-color: #bb86fc;
  /* 강조 색상 (보라색 계열 예시) */
}

.dark-mode .rank-badge.top-regular {
  color: #333;
  /* 일반 색상 */
}

.dark-mode .rank-badge .info p {
  color: #ffffff;
  /* 일반 색상 */
}
</style>