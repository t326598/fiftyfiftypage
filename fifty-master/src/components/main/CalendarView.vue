<template>
    <h1>일정표</h1>
        <div class="calendar-wrapper">
      <div class="calendar-content">
        <!-- 배경 이미지 -->
        <div class="calendar-background"
          :style="{ backgroundImage: `url('http://localhost:8080/upload/${backgroundImage}')` }"></div>

        <!-- 캘린더 -->
        <div :class="{ 'dark-calendar': isDark }">
          <CalendarLegend />
          <FullCalendar :options="calendarOptions" />
        </div>
        <!-- 모달 -->
        <div v-if="isModalOpen" class="modal-overlay" @click.self="isModalOpen = false">
          <div class="modal-content1">
            <h1 style="color: black;">{{ selectedEvent.title }}</h1>
            <h2 style="color: black;">{{ formatDateDay(selectedEvent.start) }}</h2>
            <p style="color: black;"> {{ formatDate(selectedEvent.start) }} ~ {{ formatDate(selectedEvent.end) }}</p>
            <p style="color: black;">{{ selectedEvent.description }}</p>
            <button @click="isModalOpen = false" class="close-btn">닫기</button>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, defineProps } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import koLocale from '@fullcalendar/core/locales/ko';
import listPlugin from '@fullcalendar/list';
import axios from '@/api/plan'
import CalendarLegend from '@/components/sub/CalendarLegend.vue'

const props = defineProps<{ isDark: boolean }>()

const selectedEvent = ref({
  title: '',
  start: null,
  end: null,
  description: ''
});

const calendarEvents = ref<any[]>([])
const isModalOpen = ref(false);
const backgroundImage = ref('');
const colors = ref([]);



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


async function fetchBackground(month: number) {
  try {
    const response = await axios.backgroundList(month);
    backgroundImage.value = response.data.name;
  } catch (error) {
    console.error(error);
  }
}

function formatDateDay(date: Date | null) {
  if (!date) return '';
  const year = date.getFullYear();
  const month = date.getMonth() + 1; // 0-based
  const day = date.getDate();
  return `${year}년 ${month}월 ${day}일`;
}

// 일정
function formatDate(date: Date | null) {
  if (!date) return '';
  return new Intl.DateTimeFormat('ko-KR', {
    hour: '2-digit', minute: '2-digit'
  }).format(date);
}


async function fetchPlan() {
  try {
    const response = await axios.ListPlan()
    calendarEvents.value = response.data || []
    colors.value = response.data.map(event => event.backgroundColor);

  } catch (error) {
    console.error('일정 목록을 불러오는 데 실패했습니다:', error)
  }
}


onMounted(async () => {
  fetchPlan();
});


</script>

<style scoped src="@/assets/style/mainpage.css"></style>