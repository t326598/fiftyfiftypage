<template>
  <h1>일정표</h1>
  <div class="calendar-wrapper">
    <div class="calendar-content">
      <div class="calendar-background" :style="{ backgroundImage: `url('${backgroundImage}')` }"></div>
      <div :class="{ 'dark-calendar': isDark }" class="calendar-main-area">
        <CalendarLegend />
        <div v-if="!isMobile">
          <FullCalendar :options="fullCalendarOptions" ref="fullCalendarRef"></FullCalendar>
        </div>
        

        <div v-else>
          <VueCal class="vuecal-custom-theme" :events="vueCalEvents" locale="ko" active-view="month"
            :disable-views="['years', 'year', 'week', 'day']" hide-view-selector week-start="0"
            @cell-click="onCellClick" @view-change="handleVueCalViewChange">
            <template #cell="{ day, events = [], view, outside }">
              <div v-if="view === 'month'" class="mc-monthCell">
                <div style="font-size:10px; color:red;">
                  🔥 이벤트 개수: {{ events.length }}
                </div>

                <div class="mc-date-small" :class="{ 'mc-outside': outside }">
                  {{ day.getDate() }}
                </div>

                <div class="mc-events-list">
                  <div v-for="ev in events" :key="ev.id" class="mc-event-item" @click.stop="onCellClick(day)">
                    {{ ev.title }}
                  </div>
                </div>

                <div class="mc-indicator">
                  <span v-for="ev in events.slice(0, 3)" :key="ev.id" class="mc-dot"
                    :style="{ backgroundColor: ev.backgroundColor }" @click.stop="onCellClick(day)"></span>
                  <span v-if="events.length > 3" class="mc-more-count" @click.stop="onCellClick(day)">+{{ events.length
                    - 3 }}</span>
                </div>
              </div>
            </template>
          </VueCal>
        </div>
      </div>

      <div v-if="isModalOpen" class="modal-overlay" @click.self="isModalOpen = false">
        <div class="modal-content1">
          <h1>{{ selectedEvent.title }}</h1>
          <h2>{{ formatDateDay(selectedEvent.start) }}</h2>
          <p>{{ formatTime(selectedEvent.start) }} ~ {{ formatTime(selectedEvent.end) }}</p>
          <p>{{ selectedEvent.description }}</p>
          <button @click="isModalOpen = false" class="close-btn">닫기</button>
        </div>
      </div>

      <div v-if="isModalVueOpen" class="modal-overlay" @click.self="isModalVueOpen = false">
        <div class="modal-content1">
          <h2>{{ formatDateDay(selectedDate) }}</h2>
          <ul class="event-list">
            <li v-for="ev in dayEvents" :key="ev.id" class="event-item" @click="showDetail(ev)"
              style="list-style-type: none; color: black;">
              <span class="event-color-dot" :style="{ backgroundColor: ev.backgroundColor }" />
              <div class="event-info">
                <div class="event-title">{{ ev.title }}</div>
              </div>
            </li>
            <li v-if="!dayEvents.length" class="no-event">일정이 없습니다.</li>
          </ul>
          <button @click="isModalVueOpen = false" class="close-btn">닫기</button>
        </div>
      </div>

      <div v-if="isDetailOpen" class="modal-overlay" @click.self="isDetailOpen = false">
        <div class="modal-content1">
          <h3 style="color: black;">{{ selectedEvent.title }}</h3>
          <p>{{ formatDateDay(selectedEvent.start) }}</p>
          <p>{{ formatTime(selectedEvent.start) }} ~ {{ formatTime(selectedEvent.end) }}</p>
          <p>{{ selectedEvent.description }}</p>
          <button @click="isDetailOpen = false" class="close-btn">닫기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, defineProps, computed, onUnmounted } from 'vue';

// FullCalendar 관련 import
import FullCalendar from '@fullcalendar/vue3';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import koLocaleFullCalendar from '@fullcalendar/core/locales/ko';
import listPlugin from '@fullcalendar/list';
import VueCal from 'vue-cal';
import 'vue-cal/dist/vuecal.css';
import axios from '@/api/plan'; // axios import 경로 확인
import CalendarLegend from '@/components/sub/CalendarLegend.vue'; // CalendarLegend import 경로 확인


const props = defineProps<{ isDark: boolean }>();
const dayEvents = ref<any[]>([]);
const selectedDate = ref<Date | null>(null);
const selectedEvent = ref({
  title: '',
  start: null as Date | null,
  end: null as Date | null,
  description: ''
});



const isModalOpen = ref(false);
const isModalVueOpen = ref(false);
const isDetailOpen = ref(false);
const backgroundImage = ref('');

const isMobile = ref(window.innerWidth < 920); // PC와 모바일 전환 기준 픽셀 (조절 가능)

function updateIsMobile() {
  isMobile.value = window.innerWidth < 920;
}


// 캘린더 이벤트 데이터 (ref로 관리)
const fullCalendarEvents = ref<any[]>([]); // FullCalendar용 이벤트 데이터
const vueCalEvents = ref<any[]>([]);       // VueCal용 이벤트 데이터

// FullCalendar 컴포넌트 인스턴스 참조 (타입 명시하여 'never' 에러 방지)
const fullCalendarRef = ref<InstanceType<typeof FullCalendar> | null>(null);
// VueCal 컴포넌트 인스턴스 참조 (VueCal 타입이 필요하다면 정의하거나 any 사용)

// FullCalendar 옵션 설정 (reactive 객체로 선언)
const fullCalendarOptions = reactive({
  plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin, listPlugin], // 사용하는 모든 플러그인 명시
  locale: koLocaleFullCalendar, // 한국어 로케일
  dayMaxEventRows: true, // 하루에 표시할 이벤트 최대 줄 수
  eventDisplay: 'block', // 이벤트 표시 방식 (블록)
  initialView: 'dayGridMonth', // 초기 뷰 (onMounted에서 isMobile에 따라 변경 예정)
  headerToolbar: { // 캘린더 헤더 툴바 설정
    left: 'prev,next today',
    center: 'title',
    right: '', // onMounted에서 isMobile에 따라 변경 예정
  },
  datesSet: (info: any) => { // 캘린더 날짜 범위 변경 시 호출
    const currentStartDate = new Date(info.view.currentStart);
    const month = currentStartDate.getMonth() + 1;
    fetchBackground(month); // 배경 이미지 업데이트
  },
  buttonText: { // 버튼 텍스트 한글화
    today: '오늘',
  },
  events: (fetchInfo: any, successCallback: any, failureCallback: any) => {
    successCallback(fullCalendarEvents.value);
  },
  eventDidMount(info: any) { // 이벤트 요소가 DOM에 마운트된 후
    info.el.style.position = 'relative';
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
  contentHeight: 'auto',
  aspectRatio: 1.35,
  dayCellDidMount(info: any) { // 날짜 셀이 DOM에 마운트된 후
    const dateStr = info.date.toISOString().slice(0, 10);
    const eventsForThisDay = fullCalendarEvents.value.filter(event =>
      event.start.startsWith(dateStr)
    );
    if (eventsForThisDay.length > 0) {
      const tooltipContent = eventsForThisDay
        .map(ev => `• ${ev.title}`)
        .join('\n');
      info.el.setAttribute('title', tooltipContent);
    }
  },
  eventClick: handleFullCalendarEventClick, // 이벤트 클릭 핸들러
});



// 배경 이미지 불러오기
async function fetchBackground(month: number) {
  try {
    const response = await axios.backgroundList(month);
    backgroundImage.value = response.data.path;
  } catch (error) {
    console.error("배경 이미지를 불러오는 데 실패했습니다:", error);
    backgroundImage.value = '';
  }
}

function onCellClick(date: Date | null) {
  if (!date) return;
  const eventsOfDay = vueCalEvents.value.filter(e => new Date(e.start).toDateString() === date.toDateString());
  if (!eventsOfDay.length) return;
  selectedDate.value = date;
  dayEvents.value = eventsOfDay;
  isModalVueOpen.value = true;
}

function showDetail(ev: any) {
  selectedEvent.value = ev;
  isDetailOpen.value = true;
  isModalVueOpen.value = false;
}


// ⭐ 일정 데이터 불러오기 (fetchPlan 함수)
async function fetchPlan() {
  try {
    const response = await axios.ListPlan(); // API 호출
    const rawEvents = response.data || []; // 데이터 안전하게 가져오기

    // FullCalendar용 데이터 형식 변환 및 할당
    fullCalendarEvents.value = rawEvents.map((event: any) => ({
      id: event.id,
      title: event.title,
      start: event.start, // 서버에서 받은 ISO 8601 문자열 또는 Date 객체 그대로 사용
      end: event.end,
      description: event.description || '',
      backgroundColor: event.backgroundColor || '',
      borderColor: event.backgroundColor || '',
      extendedProps: {
        description: event.description || ''
      },
      classNames: event.backgroundColor ? [`event-color-${event.backgroundColor.replace('#', '')}`] : []
    }));

    // VueCal용 데이터 형식 변환 및 할당 (Date 객체로 변환)
    vueCalEvents.value = rawEvents.map(e => ({
      id: e.id,
      start: new Date(e.start),
      end: new Date(e.end),
      title: e.title,
      backgroundColor: e.backgroundColor,
      allDay: true
    }));

    console.log('fetchPlan: 데이터 로드 완료. FullCalendar 이벤트 수:', fullCalendarEvents.value.length);

  } catch (error) {
    console.error('fetchPlan: 일정 목록을 불러오는 데 실패했습니다:', error);
  }
}

function handleVueCalViewChange(payload: { startDate: Date, endDate: Date, view: string }) {
  if (payload.view === 'month') {
    const month = payload.startDate.getMonth() + 1;
    fetchBackground(month);
  }
}

function handleFullCalendarEventClick(info: any) {
  const event = info.event;
  selectedEvent.value = {
    title: event.title,
    start: event.start,
    end: event.end,
    description: event.extendedProps.description || ''
  };
  isModalOpen.value = true;
}

function formatDateDay(date: Date | null) {
  if (!date) return '';
  const d = new Date(date);
  const year = d.getFullYear();
  const month = d.getMonth() + 1; // 0-based
  const day = d.getDate();
  return `${year}년 ${month}월 ${day}일`;
}

function formatTime(date: Date | string | null) {
  if (!date) return '';
  const d = new Date(date);
  return new Intl.DateTimeFormat('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  }).format(d);
}


// --- 컴포넌트 마운트 시 실행되는 로직 (핵심) ---
onMounted(async () => {
  // 1. **가장 먼저 데이터 로드:** `fetchPlan`이 완료될 때까지 기다립니다. (⭐ await 추가)
  await fetchPlan();


  const initialMonth = new Date().getMonth() + 1;
  fetchBackground(initialMonth);

  if (!isMobile.value && fullCalendarRef.value) {
    const api = fullCalendarRef.value.getApi();
    api.refetchEvents();
  }

});

onMounted(() => {
  window.addEventListener('resize', updateIsMobile);
});
onUnmounted(() => {
  window.removeEventListener('resize', updateIsMobile);
});
</script>

<style scoped>
/* 필요한 스타일시트 import (경로 확인) */
@import url('@/assets/style/mainpage.css');
@import url('@/assets/style/calendar.css');




/* 날짜 숫자 */
:deep(.mc-date-small) {
  position: absolute;
  top: 4px;
  right: 4px;
  font-size: 12px;
}

/* 이벤트 제목 리스트 */
:deep(.mc-events-list) {
  position: absolute;
  top: 20px;
  left: 4px;
  right: 4px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  max-height: 40px;
  overflow-y: auto;
}

:deep(.mc-event-item) {
  font-size: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
}

/* 컬러 점 표시 */
:deep(.mc-indicator) {
  position: absolute;
  bottom: 4px;
  left: 4px;
  display: flex;
  align-items: center;
}

:deep(.mc-dot) {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 2px;
}

:deep(.mc-more-count) {
  font-size: 10px;
  color: #999;
  margin-left: 2px;
}

.modal-content1 .event-list {
  list-style: none;
  /* 기본 불릿 제거 */
  padding: 0;
  /* 기본 들여쓰기 제거 */
  margin: 0 auto;
  /* 좌우 마진 자동 */
  text-align: center;
  /* 자식 요소 inline‐block 들을 가운데로 */
}

/* LI 를 inline‐block 으로 변경해서 UL 의 text-align: center 에 반응하게 함 */
.modal-content1 .event-list .event-item {
  display: inline-block;
  text-align: left;
  /* 내부 텍스트는 왼쪽 정렬, 필요시 center 로도 변경 가능 */
  margin: 0.5rem 0;
  /* 항목 간 간격 */
  width: auto;
  /* 혹은 고정 폭 주고 싶으면 px/percent 로 지정 */
}
</style>