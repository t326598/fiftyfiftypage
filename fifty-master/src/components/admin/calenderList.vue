<template>
  <div class="adminTrainerList">
    <div class="container">
      <Header />

      <div class="main">
        <div class="inner d-flex">
          <Sidebar />

          <div class="contentArea">
            <div class="mainTitle d-flex items-center">
              <h1>달력 백그라운드 관리</h1>
            </div>

            <div class="profileRow d-flex justify-between">
              <div
                v-for="months in calendarList"
                :key="months.id"
                class="profileCard text-center"
              >
              <img :src="`http://localhost:8080/upload/${months.name}`" :alt="months.month" class="member-img" />
                <h5 class="mt-2">{{ months.month }} 월</h5>
                <button class="updateBtn mt-1" @click="editCalendar(months.id)">수정</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
    <EditMothsModal
  v-if="showModal"
  :months="selectedCalendar"
  @close="closeModal"
  @updated="fetchCalendarList"
/>


</template>


<script setup lang="ts">
import Sidebar from '../admin/adminSidebar.vue'
import Header from '../admin/adminHeader.vue'
import axios from "@/api/plan";
import { ref, onMounted} from 'vue'
import EditMothsModal from '@/components/admin/updateCalendarModal.vue'

const calendarList = ref([]);
const selectedCalendar = ref(null)
const showModal = ref(false)

const fetchCalendarList = async () => {
  try {
    const res = await axios.ListCalendar()
    calendarList.value = res.data
    console.log(calendarList.value)
  } catch (err) {
    console.error(err)
  }
}


const editCalendar = (calendarId: number) => {
  const calendar = calendarList.value.find(c => c.id === calendarId)
  if (calendar) {
    selectedCalendar.value = { ...calendar }
    showModal.value = true
  }
}

const closeModal = () => {
  showModal.value = false
  selectedCalendar.value = null
}

onMounted(() => {
   fetchCalendarList()
})

</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
}

.main {
  display: flex;
  flex: 1;
}

.inner {
  display: flex;
  width: 100%;
}

.contentArea {

  flex: 1;
  padding: 20px;
}

.mainTitle {
  height: 80px;
  padding-left: 20px;
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 20px;
  display: flex;
  align-items: center;

}
.profileRow {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  justify-content: flex-start; /* 또는 space-between */
  padding: 10px 0;
}

.profileCard {
  width: calc(20% - 30px); /* 4개씩 한 줄에 */
  padding: 8px;
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.08);
}

.member-img {
  width: 100%;
  height: 300px;
  object-fit: cover;
  border-radius: 4px;
}

.profileCard h5 {
  font-size: 14px;
  margin: 6px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.updateBtn {
  width: 100%;
  background-color: #7c92c0;
  color: #fff;
  border: none;
  padding: 6px 0;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: bold;
  margin-top: 4px;
}

.updateBtn:hover {
  filter: brightness(90%);
}


</style>
