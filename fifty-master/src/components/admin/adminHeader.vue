<template>
  <div class="admin-layout">
    <!-- Header -->
    <header class="header">
      <div class="header-link">
        <a href="/">홈페이지</a>
        <button
          type="button"
          class="btnst"
          @click="handleLogout"
        >
          로그아웃
        </button>
      </div>
    </header>
  </div>
</template>

<script setup lang="ts">import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import Swal from 'sweetalert2'

const router = useRouter()
const { isLogin, userInfo } = useAuth()

const handleLogout = async () => {
  const result = await Swal.fire({
    title: '로그아웃 하시겠습니까?',
    text: '확인 시 로그인 페이지로 이동합니다.',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '로그아웃',
    cancelButtonText: '취소',
    reverseButtons: true
  })

  if (result.isConfirmed) {
    // 1. JWT 삭제
    localStorage.removeItem('jwt')

    // 2. 상태 초기화
    isLogin.value = false
    userInfo.value = null

    // 3. 페이지 이동
    await Swal.fire('로그아웃 완료', '', 'success')
    router.push('/login')
  }
}

</script>

<style scoped>
/* Header */
.btnst {
  text-decoration: none;
  color: inherit;
  border: none;
  background: none;
  font-size: 18px;
  text-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
  font-weight: bold;
  cursor: pointer;
}

.header {
  padding: 0 30px;
  width: 2500px;
  height: 75px;
  background-color: #E7ECEF;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  width: 250px;
  height: 75px;
}

.logo img {
  width: 200px;
  height: 75px;
  object-fit: contain;
}

.header-link {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 250px;
  height: 50px;
}

.header-link > a {
  color: #000;
  text-decoration: none;
  width: 125px;
  font-size: 18px;
  font-weight: bold;
  text-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
}

.header-link > a:hover {
  color: #ddd;
}

/* Sidebar */
.sidebar * {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  list-style: none;
  font: inherit;
}

</style>
