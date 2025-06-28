<template>
  <div class="godDoUser">
    <Header />
    <div class="container">
      <Sidebar />
      <div class="main">
        <div class="inner">
          <div class="title">
            <h1>이미지 목록</h1>
          </div>
          <div style="display: flex; justify-content: end; width: 1300px; gap: 50px; margin-bottom: 20px;">
            <select v-model="selectedCrt">
              <option value="">전체 카테고리</option>
              <option value="1">키나</option>
              <option value="2">문샤넬</option>
              <option value="3">예원</option>
              <option value="4">하나</option>
              <option value="5">아테나</option>
            </select>
            <!-- 연도 선택 -->
            <select v-model="selectedYear">
                   <option value="">전체 연도</option>
              <option value="2024">2024년</option>
              <option value="2025">2025년</option>
            </select>

          <button @click="openInsertPopup" class="button">등록</button>
        </div>

          <!-- 이미지 테이블 -->
          <div class="list">
            <table>
              <thead>
                <tr>
                  <th>이미지</th>
                  <th>멤버 이름</th>
                  <th>사진 연도</th>
                  <th>등록일자</th>
                  <th>삭제</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="member in displayedMembers" :key="member.no">
                  <td>
                    <img :src="`http://localhost:8080/upload/${member.name}`" :alt="member.name" class="member-img" style="height: 60px;" />
                  </td>
                  <td>{{ getCrtName(member.crt) }}</td>
                  <td>{{ member.trueDay || '없음' }}</td>
                  <td>{{ formatDate(member.createdAt) }}</td>
                  <td>
                    <button @click="deleteImage(member.no)">삭제</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <!-- 페이지네이션 -->
          <div class="pagination" style="margin-top: 50px; color: black;">
            <button @click="changePage('prev')" :disabled="currentPage === 1">Previous</button>
            <span>{{ currentPage }} / {{ totalPages }}</span>
            <button @click="changePage('next')" :disabled="currentPage === totalPages">Next</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup lang="ts">
import { ref, onMounted,computed,watch } from 'vue'
import axios from '@/api/files'
import Header from '@/components/admin/adminHeader.vue'
import Sidebar from '@/components/admin/adminSidebar.vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const isModalOpen = ref(false)
const editData = ref({ no: 0, title: '', content: '' })
const userList = ref<any[]>([])
const allImagesLoaded = ref(false)
const currentPage = ref(Number(route.query.page) || 1)
const crt = ref('')
const totalItems = ref(0)
const itemsPerPage = 10 // 한 페이지에 15명씩
const members = ref([]);
const selectedCrt = ref('')
const selectedYear = ref('')




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

watch([selectedCrt, selectedYear], () => {
  fetchMembers()
  currentPage.value = 1
})

// 공지 삭제
const deleteImage = async (no: number) => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    await axios.deleteImage(no)
    alert('삭제되었습니다.')
      fetchMembers()
  } catch (err) {
    console.error('삭제 실패', err)
    alert('삭제 중 오류가 발생했습니다.')
  }
}

async function fetchMembers() {
  try {
    const response = await axios.fetchFiles({
      crt: selectedCrt.value,
      trueDay: selectedYear.value,
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
const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleString()
}


onMounted(() => {
  fetchMembers()
})

const openInsertPopup = () => {
  const width = 600
  const height = 400
  const left = (window.screen.width + width) - 50
  const top = (window.screen.height - height) / 2

   const newWindow = window.open(
    '/admin', // 라우터 경로
    '공지사항등록',
    `width=${width},height=${height},left=${left},top=${top},resizable=no`
  )

  
  const timer = setInterval(() => {
    if (newWindow?.closed) {
      clearInterval(timer)
      fetchMembers() // 등록창 닫히면 리스트 갱신
    }
  }, 500)
  
}


const getCrtName = (crtValue: string | number): string => {
  const map: Record<string, string> = {
    '1': '키나',
    '2': '문샤넬',
    '3': '예원',
    '4': '하나',
    '5': '아테나',
  }

  if (typeof crtValue === 'number') {
    return map[crtValue.toString()] || '알 수 없음'
  }

  if (typeof crtValue === 'string') {
    return crtValue
      .split(',')
      .map(c => map[c.trim()] || '알 수 없음')
      .join(', ')
  }

  return '알 수 없음'
}

</script>

<style scoped>

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.6);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 500px;
}

.modal input,
.modal textarea {
  width: 100%;
  margin-bottom: 10px;
}

.modal-buttons {
  display: flex;
  justify-content: space-between;
}

.godDoUser .container {
  display: flex;
}

.godDoUser .ticketList .mainTitle {
  height: 100px;
  text-align: center;
  line-height: 100px;
  font-weight: bold;
  font-size: 1.2em;
}

.godDoUser .main {
  display: flex;
  width: 100%;
}

.godDoUser .button,
.godDoUser .search button,
.godDoUser .search-form button {
  padding: 10px 20px;
  background-color: #008cba;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 5px;
  font-size: 14px;
  transition: background-color 0.3s ease, color 0.3s ease;
  box-shadow: 3px 3px 5px rgba(0, 0, 0, 0.2);
}

.godDoUser .button:hover,
.godDoUser .search button:hover,
.godDoUser .search-form button:hover {
  background-color: #005f73;
}

.godDoUser .search {
  width: 100%;
  display: flex;
  justify-content: end;
  margin-left: -30px;
}

.godDoUser .search input[type='text'],
.godDoUser .search-form input[type='text'] {
  padding: 8px;
  width: 200px;
  margin-right: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #fff;
}

.godDoUser .list {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  width: 1500px;
}

.godDoUser table {
  width: 80%;
  border-collapse: collapse;
  background-color: white;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.godDoUser table th,
.godDoUser table td {
  border: 1px solid #ddd;
  padding: 4px;
  text-align: center;
  font-size: 14px;
}

.godDoUser table th {
  background-color: #f4f4f4;
}

.godDoUser .pagination {
  display: flex;
  justify-content: center;
  margin-top: 100px;
}

.godDoUser .pagination a {
  margin: 0 5px;
  padding: 8px 16px;
  text-decoration: none;
  color: #008cba;
  border: 1px solid #ddd;
  border-radius: 5px;
  transition: background-color 0.3s ease, color 0.3s ease;
  box-shadow: 3px 3px 5px rgba(0, 0, 0, 0.1);
}

.godDoUser .pagination a:hover {
  background-color: #005f73;
  color: white;
}

.godDoUser .pagination a.active {
  background-color: #008cba;
  color: white;
  border: 1px solid #008cba;
  pointer-events: none;
}

.godDoUser .inner {
  margin-top: 50px;
  height: 1050px;
  width: 1500px;
  background-color: #e7ecef;
  border-radius: 5%;
  box-sizing: border-box;
}



.godDoUser .attendance-table th:nth-child(3),
.godDoUser .attendance-table td:nth-child(3) {
  width: 80px;
}
</style>
