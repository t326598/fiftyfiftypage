<template>
           <h1 style="margin-bottom: 10px;">멤버 프로필</h1>
    <div class="profile" style="margin-top: 10px;">
      <div class="card-grid">
        <div v-for="member in profile" :key="member.no" class="member-card" @click="showProfileBox(member)">
          <img v-if="!isSmallScreen" :src="`http://localhost:8080/upload/${member.name}`" :alt="member.title"
            class="member-img" />
          <p v-else class="member-text">{{ member.title }}</p>
        </div>
      </div>
    </div>

    <!-- 프로필 박스 -->
    <transition name="slide-down" @after-leave="handleAfterLeave">
      <div v-if="showProfile" class="profile-filter-section" :key="selectedMemberNo?.memberNo">
        <div class="profile-box">
          <div class="profile-box-content" v-if="selectedMemberNo">
            <section style="width: 30%; overflow: hidden;">
              <img :src="`http://localhost:8080/upload/${selectedMemberNo.name}`" :alt="selectedMemberNo.title"
                class="member-profile" style="width: 259px; height: 323px; object-fit: cover;" />
            </section>
            <section style="width: 60%; text-align: center;">
              <h1 style="color: black; font-size: 40px;">{{ selectedMemberNo.title }}</h1>
              <h2 style="color: black; font-size: 25px;">"{{ selectedMemberNo.subContent }}"</h2>
              <p style="color: black; font-size: 25px;">{{ selectedMemberNo.content }}</p>
            </section>
          </div>
        </div>
        <button v-if="showProfile" class="close-btn" @click="closeProfileBox">닫기</button>
      </div>
    </transition>

   <div class="card-image-wrapper">
      <div class="card-image">
        <div v-for="member in displayedMembers" :key="member.no" class="member-card-image"
          @click="openModal(`http://localhost:8080/upload/${member.name}`)">
          <img :src="`http://localhost:8080/upload/${member.name}`" :alt="member.name" class="member-img" />
        </div>
      </div>
    </div>
    <div class="pagination" style="margin-top: 50px; color: black;">
      <button @click="changePage('prev')" :disabled="currentPage === 1">Previous</button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <button @click="changePage('next')" :disabled="currentPage === totalPages">Next</button>
    </div>

    
  <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      <img :src="selectedImage" alt="확대 이미지" class="modal-img" />
      <button class="modal-close" @click="closeModal">×</button>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import axios from '../../api/files'
import { useRoute, useRouter } from 'vue-router'
import axiospr from '../../api/profile'

const route = useRoute()
const router = useRouter()
const crt = ref('')
const currentPage = ref(Number(route.query.page) || 1)

// 페이지 변경 시 쿼리 업데이트
watch(currentPage, (newPage) => {
  router.replace({ query: { ...route.query, page: newPage } })
})

watch(currentPage, () => {
  fetchMembers()
})

watch(crt, () => {
  currentPage.value = 1;
  fetchMembers();
});

async function fetchProfile() {
  try {
    const response = await axiospr.ListProfile()
    profile.value = response.data.list || []
  }
  catch (error) {
    console.error('멤버 목록을 불러오는 데 실패했습니다:', error)
  }
}

const showModal = ref(false)
const selectedImage = ref('')
const showProfile = ref(false) // 프로필 박스를 보여줄지 여부
const allImagesLoaded = ref(false)

// 페이징
const totalItems = ref(0)
const itemsPerPage = 15 // 한 페이지에 15명씩

const members = ref([]);

const profile = ref([]);
const selectedMemberNo = ref([]);
const isSmallScreen = ref(window.innerWidth <= 480)

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
      rows: itemsPerPage, 
    })
    console.log(response.data)
    members.value = response.data.list || []      
    totalItems.value = response.data.total || 0   
    allImagesLoaded.value = true
  } catch (error) {
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

const totalPages = computed(() => {
  return members.value.length === 0
    ? 1
    : Math.ceil(totalItems.value / itemsPerPage);
});


function openModal(imageUrl: string) {
  selectedImage.value = imageUrl
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

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



onMounted(async () => {
  fetchMembers();
  fetchProfile();
});



</script>

<style scoped src="@/assets/style/mainpage.css"></style>