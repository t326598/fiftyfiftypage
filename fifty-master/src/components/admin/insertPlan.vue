<template>
  <div>
    <Header />
    <div class="container">
      <Sidebar />
      <FullCalendar
        :options="calendarOptions"
        class="fullcalendars"
      />

      <!-- 모달 -->
      <div
        v-if="isModalOpen"
        class="modal-overlay"
        @click.self="closeModal"
      >
        <div class="modal-content">
          <h3>{{ selectedEventIndex !== null ? '일정 수정' : '일정 추가' }}</h3>

          <!-- 카테고리 탭 -->
          <div class="category-tabs">
            <button
              v-for="(name, num) in categories"
              :key="num"
              type="button"
              :class="{ active: form.crt === Number(num) }"
              @click="form.crt = Number(num)"
            >
              {{ name }}
            </button>
          </div>
      

          <form @submit.prevent="submitEvent">
            <div>
              <label>일정 제목:</label>
              <input
                v-model="form.title"
                required
              >
            </div>
            <div>
              <label>시작 시간:</label>
              <select
                v-model="form.startTime"
                required
              >
                <option
                  v-for="time in times"
                  :key="time"
                  :value="time"
                >
                  {{ time }}
                </option>
              </select>
            </div>
            <div>
              <label>종료 시간:</label>
              <select
                v-model="form.endTime"
                required
              >
                <option
                  v-for="time in times"
                  :key="time"
                  :value="time"
                >
                  {{ time }}
                </option>
              </select>
            </div>
            <div style="margin-top: 1em;">
              <button type="submit">
                {{ selectedEventIndex !== null ? '수정 완료' : '추가' }}
              </button>
              <button
                type="button"
                @click="closeModal"
              >
                취소
              </button>
              <button
                v-if="selectedEventIndex !== null"
                type="button"
                style="margin-left: auto; background: #f56c6c; color: white;"
                @click="deleteEvent"
              >
                삭제
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted,computed } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import listPlugin from '@fullcalendar/list'
import koLocale from '@fullcalendar/core/locales/ko'
import Header from '@/components/admin/adminHeader.vue'
import Sidebar from '@/components/admin/adminSidebar.vue'
import axios from '@/api/plan'
import Swal from 'sweetalert2'

interface CalendarEvent {
  no: number 
  title: string
  content: string
  start: string
  end?: string
  crt: number
}

// 카테고리 매핑
const categories: Record<number, string> = {
  1: '음방',
  2: '자컨',
  3: '방송',
  4: '무대',
  5: '팬미팅',
  6: '곡발매',
  7: '생일',
}

const calendarEvents = ref<CalendarEvent[]>([])
const colors = ref([]);
const isModalOpen = ref(false)
const selectedDate = ref('')
const selectedEventIndex = ref<number | null>(null)

const form = reactive({
  no: 1,
  title: '',
  startTime: '09:00',
  endTime: '10:00',
  crt: 1,
})

const times = []
for (let h = 0; h < 24; h++) {
  for (let m = 0; m < 60; m += 10) {
    const hh = h.toString().padStart(2, '0')
    const mm = m.toString().padStart(2, '0')
    times.push(`${hh}:${mm}`)
  }
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
  buttonText: {
    today: '오늘',
    month: '월간',
    week: '주간',
    day: '일간',
    list: '리스트',
  },
  events: computed(() => calendarEvents.value),
  eventDidMount(info: any) {
    info.el.style.position = 'relative'
    info.el.style.zIndex = '10'
    info.el.style.transition = 'transform 0.2s ease'
    info.el.addEventListener('mouseenter', () => {
      info.el.style.transform = 'scale(1.05)'
      info.el.style.boxShadow = '0 4px 10px rgba(0, 0, 0, 0.2)'
      info.el.style.zIndex = '10'
      info.el.style.cursor = 'pointer'
    })
    info.el.addEventListener('mouseleave', () => {
      info.el.style.transform = 'scale(1)'
      info.el.style.boxShadow = ''
      info.el.style.zIndex = ''
    })
  },
  dayCellDidMount(info: any) {
    const dateStr = info.date.toISOString().slice(0, 10)
    const eventsForThisDay = calendarEvents.value.filter(event =>
      event.start.startsWith(dateStr)
    )
    if (eventsForThisDay.length > 0) {
      const tooltipContent = eventsForThisDay
        .map(ev => `• ${ev.title} (${new Date(ev.start).getHours()}시)`)
        .join('\n')
      info.el.setAttribute('title', tooltipContent)
    }
  },
  eventClick(info: any) {
    // 클릭한 이벤트 인덱스 찾기
    const clickedEvent = info.event
    const idx = calendarEvents.value.findIndex(ev => 
      ev.title === clickedEvent.title
    )
    if (idx !== -1) {
      openModalForEdit(idx)
    } else {
      
      alert('이벤트를 찾을 수 없습니다.')
    }
  },
  dateClick(info: any) {
    selectedDate.value = info.dateStr
    selectedEventIndex.value = null
    openModal()
  },
})




async function fetchPlan() {
  try {
    const response = await axios.ListPlan()
    calendarEvents.value = response.data || []
    colors.value = response.data.map(event => event.backgroundColor);
    console.log(response.data)

  } catch (error) {
    console.error('일정 목록을 불러오는 데 실패했습니다:', error)
  }
}

function openModal() {
  form.no = 1
  form.title = ''
  form.startTime = '09:00'
  form.endTime = '10:00'
  form.crt = 1
  isModalOpen.value = true
}

function openModalForEdit(index: number) {
  const ev = calendarEvents.value[index]
  selectedEventIndex.value = index
  selectedDate.value = ev.start.slice(0, 10)
   form.no = ev.no 
  form.title = ev.title
  form.startTime = ev.start.slice(11, 16)
  form.endTime = ev.end ? ev.end.slice(11, 16) : '10:00'
  form.crt = ev.crt
  isModalOpen.value = true
  console.log('form.category:', form.crt, typeof form.crt)
}

function closeModal() {
  isModalOpen.value = false
  selectedEventIndex.value = null
}

async function submitEvent() {
  const newEvent = {
    no: form.no,
    title: form.title,
    startAt: `${selectedDate.value}T${form.startTime}`,
    endAt: `${selectedDate.value}T${form.endTime}`,
    crt: form.crt,
  }
    if (selectedEventIndex.value !== null) {
      await axios.getUpdatePlan(newEvent)
   
  } else {
   await axios.getInsertPlan(newEvent);
    
  }
  await fetchPlan()
  closeModal()
     await Swal.fire({
        icon: 'success',
        title: '완료',
        text: '일정이 성공적으로 적용되었습니다.',
        timer: 1500,
        showConfirmButton: false,
      })
}

async function deleteEvent() {
  if (selectedEventIndex.value !== null) {
    const eventToDelete = calendarEvents.value[selectedEventIndex.value]

    const confirmed = confirm(`일정 "${eventToDelete.title}"을(를) 삭제하시겠습니까?`)
    if (!confirmed) return

    try {
      await axios.getDeletePlan(form.no)
      await fetchPlan()
      closeModal()
        closeModal()
        await Swal.fire({
          icon: 'success',
          title: '삭제 완료',
          text: '일정이 성공적으로 삭제되었습니다.',
          timer: 1500,
          showConfirmButton: false,
        })
      } catch (error) {
        console.error('일정 삭제 실패:', error)
        await Swal.fire({
          icon: 'error',
          title: '삭제 실패',
          text: '일정 삭제에 실패했습니다.',
        })
      }
    } 
  }

onMounted(() => {
  fetchPlan();
});

</script>

<style scoped>
.swal2-container {
  position: fixed !important;
  z-index: 2147483647 !important; /* 최대값 */
}
.container {
  display: flex;
}


.fullcalendars{
  margin: 0 0 200px 0;
  margin-top: 50px;
  min-width: 900px;
  width: 60%;
  height: 60%;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}
.modal-content {
  background: white;
  padding: 1.5em;
  border-radius: 8px;
  width: 320px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.3);
}
.modal-content form div {
  margin-bottom: 1em;
}
.modal-content label {
  display: block;
  margin-bottom: 0.3em;
  font-weight: bold;
}
.modal-content input,
.modal-content select {
  width: 100%;
  padding: 0.4em;
  box-sizing: border-box;
}
.modal-content button {
  margin-right: 0.5em;
  padding: 0.5em 1em;
}

/* 카테고리 탭 스타일 */
.category-tabs {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1em;
}
.category-tabs button {
  flex: 1;
  margin-right: 4px;
  padding: 0.5em;
  background: #eee;
  border: 1px solid #ccc;
  cursor: pointer;
  font-weight: 600;
  border-radius: 4px;
  user-select: none;
  transition: background-color 0.2s ease;
}
.category-tabs button:last-child {
  margin-right: 0;
}
.category-tabs button.active {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
}
</style>
