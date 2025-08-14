<template>
  <div class="godDoUser">
    <Header />
    <div class="container">
      <Sidebar />
      <div class="main">
        <div class="inner">
          <div class="title">
            <h2>공지사항 목록</h2>
          </div>
          <div style="display: flex; justify-content: end; width: 1300px;">
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
                  <th style="width: 5%">
                    번호
                  </th>
                  <th style="width: 15%">
                    제목
                  </th>
                  <th style="width: 55%">
                    내용
                  </th>
                  <th style="width: 15%">
                    등록일자
                  </th>
                  <th style="width: 5%" />
                  <th style="width: 5%" />
                </tr>
              </thead>
              <tbody>
                <tr v-if="userList.length === 0">
                  <td colspan="8">
                    조회된 데이터가 없습니다.
                  </td>
                </tr>
                <tr
                  v-for="user in userList"
                  :key="user.no"
                >
                  <td>{{ user.no }}</td>
                  <td>{{ user.title }}</td>
                  <td>{{ user.content }}</td>
                  <td>{{ formatDate(user.createdAt) }}</td>
                  <td v-if="user.userAuth !== 'ROLE_ADMIN'">
                    <button
                      class="updateBtn"
                      @click="openEditModal(user)"
                    >
                      수정
                    </button>
                  </td>
                  <td v-if="user.userAuth !== 'ROLE_ADMIN'">
                    <button
                      class="deleteBtn"
                      @click="deleteNotice(user.no)"
                    >
                      삭제
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div
    v-if="isModalOpen"
    class="modal-overlay"
  >
    <div class="modal">
      <h3>공지사항 수정</h3>
      <label>제목</label>
      <input
        v-model="editData.title"
        type="text"
      >
      <label>내용</label>
      <textarea v-model="editData.content" />
      <div class="modal-buttons">
        <button @click="updateNotice">
          저장
        </button>
        <button @click="closeModal">
          닫기
        </button>
      </div>
    </div>
  </div>

  <NoticeInsertModal
    v-if="isInsertModalOpen"
    @close="closeInsertModal"
    @noticeAdded="handleNoticeAdded"
  />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from '@/api/notice' // axios 인스턴스 경로 확인
import Header from '@/components/admin/adminHeader.vue'
import Sidebar from '@/components/admin/adminSidebar.vue'
import NoticeInsertModal from '@/components/admin/insertNotice.vue' // 새로 만든 모달 컴포넌트 임포트

const isModalOpen = ref(false) // 기존 수정 모달 가시성
const isInsertModalOpen = ref(false) // 새로운 등록 모달 가시성
const editData = ref({ no: 0, title: '', content: '' })
const userList = ref<any[]>([])

const openEditModal = (user: any) => {
  editData.value = { ...user }
  isModalOpen.value = true
}

// 수정 모달 닫기
const closeModal = () => {
  isModalOpen.value = false
}

// 공지 수정
const updateNotice = async () => {
  try {
    await axios.updateNotice({
      no: editData.value.no,
      title: editData.value.title,
      content: editData.value.content
    })
    alert('수정되었습니다.')
    closeModal()
    fetchNoticeList() // 다시 불러오기
  } catch (err) {
    console.error('공지 수정 실패', err)
    alert('수정 중 오류가 발생했습니다.')
  }
}

// 공지 삭제
const deleteNotice = async (no: number) => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    await axios.deleteNotice(no)
    alert('삭제되었습니다.')
    fetchNoticeList()
  } catch (err) {
    console.error('삭제 실패', err)
    alert('삭제 중 오류가 발생했습니다.')
  }
}

const fetchNoticeList = async () => {
  try {
    const res = await axios.ListNotice()
    console.log(res.data)
    userList.value = res.data
  } catch (err) {
    console.error(err)
  }
}

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleString()
}

onMounted(() => {
  fetchNoticeList()
})

// === 공지사항 등록 모달 관련 함수 ===
const openInsertModal = () => {
  isInsertModalOpen.value = true
}

const closeInsertModal = () => {
  isInsertModalOpen.value = false
}

const handleNoticeAdded = () => {
  fetchNoticeList() 
}
// ===================================
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
  /* z-index는 InsertModal.vue의 z-index보다 낮거나 같아야 함 */
  z-index: 1500; /* 이전에 2000을 줬다면 1500으로 */
}

.modal {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 500px;
  /* 이 모달의 z-index는 overlay보다 높아야 하지만,
     다른 모달과 겹치지 않도록 주의 */
  z-index: 1501;
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

/* 이하 기존 스타일 유지 */
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
  max-height: 550px;
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
  height: 800px;
  width: 1500px;
  background-color: #e7ecef;
  border-radius: 5%;
  box-sizing: border-box;
}

.godDoUser .inner .title {
  width: 100%;
  height: 150px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.godDoUser .mainTitle {
  width: 100%;
  height: 100px;
  text-align: center;
  line-height: 100px;
  font-weight: bold;
  font-size: 1.2em;
}

.godDoUser .attendance-table th:nth-child(3),
.godDoUser .attendance-table td:nth-child(3) {
  width: 80px;
}
</style>