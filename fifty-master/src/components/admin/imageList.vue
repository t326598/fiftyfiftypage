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
              <option value="">
                전체 카테고리
              </option>
              <option value="1">
                키나
              </option>
              <option value="2">
                문샤넬
              </option>
              <option value="3">
                예원
              </option>
              <option value="4">
                하나
              </option>
              <option value="5">
                아테나
              </option>
            </select>
            <select v-model="selectedYear">
              <option value="">
                전체 연도
              </option>
              <option value="2024">
                2024년
              </option>
              <option value="2025">
                2025년
              </option>
            </select>

            <button
              class="button"
              @click="openInsertModal"
            >
              등록
            </button>
          </div>

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
                <tr
                  v-for="member in displayedMembers"
                  :key="member.no"
                >
                  <td>
                    <img
                      :src="member.path"
                      :alt="member.path"
                      class="member-img"
                      style="height: 60px;"
                    >
                  </td>
                  <td>{{ getCrtName(member.crt) }}</td>
                  <td>{{ member.trueDay || '없음' }}</td>
                  <td>{{ formatDate(member.createdAt) }}</td>
                  <td>
                    <button @click="deleteImage(member.no)">
                      삭제
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div
            class="pagination"
            style="margin-top: 50px; color: black;"
          >
            <button
              :disabled="currentPage === 1"
              @click="changePage('prev')"
            >
              Previous
            </button>
            <span>{{ currentPage }} / {{ totalPages }}</span>
            <button
              :disabled="currentPage === totalPages"
              @click="changePage('next')"
            >
              Next
            </button>
          </div>
        </div>
      </div>
    </div>

    <InsertFilesModal
      v-if="isModalOpen"
      @close="closeInsertModal"
      @imageAdded="handleImageAdded"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import axios from '@/api/files' // axios 인스턴스 경로 확인
import Header from '@/components/admin/adminHeader.vue'
import Sidebar from '@/components/admin/adminSidebar.vue'
import InsertFilesModal from '@/components/admin/insertFiles.vue' // 새로 만든 모달 컴포넌트 임포트
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const isModalOpen = ref(false) // 모달 가시성 상태
const userList = ref<any[]>([]) // 사용되지 않는 변수일 경우 제거 가능
const allImagesLoaded = ref(false)
const currentPage = ref(Number(route.query.page) || 1)
const crt = ref('') // 사용되지 않는 변수일 경우 제거 가능
const totalItems = ref(0)
const itemsPerPage = 10 // 한 페이지에 15명씩
const members = ref<any[]>([]); // 타입 명확화
const selectedCrt = ref('')
const selectedYear = ref('')


const totalPages = computed(() => {
  return members.value.length === 0
    ? 1
    : Math.ceil(totalItems.value / itemsPerPage);
});

// 현재 페이지의 멤버들만 추출
const displayedMembers = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage; 
  const end = start + itemsPerPage;
  return members.value.slice(start, end);
})


// 페이지 변경
function changePage(direction: 'next' | 'prev') {
  if (direction === 'next' && currentPage.value < totalPages.value) {
    currentPage.value++
  } else if (direction === 'prev' && currentPage.value > 1) {
    currentPage.value--
  }
  router.push({ query: { ...route.query, page: currentPage.value } });
}

watch(currentPage, () => {
  fetchMembers()
})

watch([selectedCrt, selectedYear], () => {
  fetchMembers()
  currentPage.value = 1 // 필터 변경 시 첫 페이지로 이동
})

// 이미지 삭제
const deleteImage = async (no: number) => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    await axios.deleteImage(no)
    alert('삭제되었습니다.')
    fetchMembers() // 삭제 후 리스트 갱신
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
      page: currentPage.value - 1, // Spring Data JPA Pageable은 0부터 시작하므로 -1
      rows: itemsPerPage,
    })
    console.log("Fetched Data:", response.data)
    members.value = response.data.list || []
    totalItems.value = response.data.total || 0
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

// 모달 열기 함수
const openInsertModal = () => {
  isModalOpen.value = true
}

// 모달 닫기 함수
const closeInsertModal = () => {
  isModalOpen.value = false
}

const handleImageAdded = () => {
  fetchMembers() // 이미지 목록 새로고침
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
    // 쉼표로 구분된 문자열 처리 (예: "1, 3")
    return crtValue
      .split(',')
      .map(c => map[c.trim()] || '알 수 없음')
      .join(', ')
  }

  return '알 수 없음'
}
</script>

<style scoped>

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
  width: 1500px; /* 이 너비는 실제 레이아웃에 맞게 조정 필요 */
}

.godDoUser table {
  width: 80%; /* 이 너비는 실제 레이아웃에 맞게 조정 필요 */
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

.godDoUser .pagination button { /* 버튼에 대한 스타일 추가 */
  margin: 0 5px;
  padding: 8px 16px;
  text-decoration: none;
  color: #008cba;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 5px;
  transition: background-color 0.3s ease, color 0.3s ease;
  box-shadow: 3px 3px 5px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.godDoUser .pagination button:hover:not(:disabled) {
  background-color: #005f73;
  color: white;
}

.godDoUser .pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}


.godDoUser .inner {
  margin-top: 50px;
  height: 1050px; /* 고정 높이 대신 min-height 사용을 고려하세요 */
  width: 1500px; /* 고정 너비 대신 max-width 및 반응형 디자인 고려 */
  background-color: #e7ecef;
  border-radius: 5%;
  box-sizing: border-box;
  padding: 20px; /* 내부 여백 추가 */
}

.godDoUser .title {
  text-align: center;
  margin-bottom: 30px;
}

.godDoUser .title h1 {
  font-size: 2em;
  color: #333;
}

/* 추가된 선택 박스 스타일 */
select {
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 5px;
    background-color: #fff;
    font-size: 14px;
    cursor: pointer;
}

select:focus {
    outline: none;
    border-color: #008cba;
    box-shadow: 0 0 0 2px rgba(0, 140, 186, 0.2);
}

.godDoUser .attendance-table th:nth-child(3),
.godDoUser .attendance-table td:nth-child(3) {
  width: 80px;
}
</style>