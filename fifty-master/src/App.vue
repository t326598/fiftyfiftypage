<template>
  <div
    id="app"
    :class="{ 'loading-active': isLoading }"
  >
    <!-- 앱이 초기 로딩 중일 때 표시할 오버레이 -->
    <div
      v-if="isLoading"
      class="loading-overlay"
    >
      <div class="loading-spinner" />
      <p class="loading-text">
        페이지 로딩 중...
      </p>
    </div>

    <!-- 로딩이 완료되면 라우터가 컴포넌트를 렌더링할 위치 -->
    <router-view v-else />
  </div>
</template>

<script>
// lang="ts"를 제거하여 순수 JavaScript로 변환했습니다.
import { defineComponent, ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuth } from '@/composables/useAuth'; // useAuth 컴포저블 임포트

export default defineComponent({
  name: 'App',
  setup() {
    // 로딩 상태를 관리하는 반응형 변수
    const isLoading = ref(true); // 초기에는 로딩 중으로 설정

    // Vue Router 인스턴스 가져오기
    const router = useRouter();

    // useAuth 컴포저블에서 필요한 상태와 함수 가져오기
    const { isLogin, fetchUserInfo } = useAuth();

    // 컴포넌트가 마운트된 후에 실행될 로직
    onMounted(async () => {
      const token = localStorage.getItem('jwt');

      // 1. 토큰은 있지만 아직 isLogin 상태가 false인 경우 (새로고침 등)
      if (!isLogin.value && token) {
        try {
          // 사용자 정보를 비동기적으로 가져오고 완료될 때까지 기다림
          await fetchUserInfo();
          console.log('[App.vue] User info fetched on mount.');
        } catch (e) {
          // 사용자 정보 로드 실패 시 (예: 토큰 만료/유효하지 않음)
          console.error('[App.vue] Failed to fetch user info on mount:', e);
          localStorage.removeItem('jwt'); // 유효하지 않은 토큰 제거
          // 로그인 페이지로 강제 리다이렉션. router.push는 onMounted 내에서 안전하게 사용됩니다.
          router.push('/login');
        }
      }
      // 초기 로딩 프로세스 완료. isLoading 상태를 false로 변경하여 router-view를 보여줌.
      isLoading.value = false;
      console.log('[App.vue] Initial loading complete.');
    });

    // setup 함수에서 반응형 데이터 반환
    return {
      isLoading,
    };
  },
});
</script>

<style>
/* 기본 CSS 스타일 */
body {
  margin: 0;
  font-family: 'Inter', sans-serif; /* 폰트 설정 (Tailwind CSS와 함께 사용 시) */
}

#app {
  text-align: center;
  min-height: 100vh; /* 최소 높이를 뷰포트 높이로 설정 */
  display: flex;
  flex-direction: column;
}

/* 로딩 오버레이 스타일 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.9); /* 반투명 흰색 배경 */
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 9999; /* 다른 콘텐츠 위에 표시 */
  color: #333;
}

.loading-spinner {
  border: 4px solid #f3f3f3; /* Light grey */
  border-top: 4px solid #3498db; /* Blue */
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite; /* 스피너 애니메이션 */
  margin-bottom: 10px;
}

.loading-text {
  font-size: 1.2em;
  font-weight: bold;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 로딩 중일 때 app 콘텐츠를 숨기거나 비활성화하는 스타일 (선택 사항) */
#app.loading-active > :not(.loading-overlay) {
  visibility: hidden; /* 로딩 중일 때 라우터 뷰 콘텐츠를 숨김 */
  opacity: 0;
  transition: visibility 0s 0.3s, opacity 0.3s linear;
}
</style>
