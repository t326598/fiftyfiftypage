import CalenderList from "@/components/admin/calenderList.vue";
import ImageList from "@/components/admin/imageList.vue";
import InsertFiles from "@/components/admin/insertFiles.vue";
import InsertNotice from "@/components/admin/insertNotice.vue";
import InsertPlan from "@/components/admin/insertPlan.vue";
import NoticeList from "@/components/admin/noticeList.vue";
import ProfileList from "@/components/admin/profileList.vue";
import LoginView from "@/components/views/LoginView.vue";
import RegisterView from "@/components/views/RegisterView.vue";

import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import { useAuth } from '@/composables/useAuth';
import MainPage from "@/MainPage.vue";
import TodayChart from "@/components/admin/TodayChart.vue";
import YoutubeVideo from "@/components/main/YoutubeVideo.vue";

interface Meta {
  requiresAuth?: boolean;
  roles?: string[];
}
const routes: RouteRecordRaw[] = [
  { path: '/', name: 'MainPage', component: MainPage },
  { path: '/login', component: LoginView },
  { path: '/register', component: RegisterView },

  {
    path: '/youtubeVideo', component: YoutubeVideo
  },
  // admin routes
  {
    path: '/admin',
    name: 'CalenderList',
    component: CalenderList,
    meta: {
      requiresAuth: true,
      roles: ['ROLE_ADMIN']
    }
  },
  {
    path: '/admin/insertFiles',
    name: 'insertFiles',
    component: InsertFiles,
    meta: {
      requiresAuth: true,
      roles: ['ROLE_ADMIN']
    }
  },
  {
    path: '/admin/notice',
    name: 'insertNotice',
    component: InsertNotice,
    meta: {
      requiresAuth: true,
      roles: ['ROLE_ADMIN']
    }
  },
  {
    path: '/admin/plan',
    name: 'insertPlan',
    component: InsertPlan,
    meta: {
      requiresAuth: true,
      roles: ['ROLE_ADMIN']
    }
  },
  {
    path: '/admin/noticeList',
    name: 'noticeList',
    component: NoticeList,
    meta: {
        roles: ['ROLE_ADMIN']
    }
  },
  {
    path: '/admin/profileList',
    name: 'profileList',
    component: ProfileList,
    meta: {
      requiresAuth: true,
      roles: ['ROLE_ADMIN']
    }
  },
  {
    path: '/admin/calendarList',
    name: 'calendarList',
    component: CalenderList,
    meta: {
      requiresAuth: true,
      roles: ['ROLE_ADMIN']
    }
  },
  {
    path: '/admin/imageList',
    name: 'imageList',
    component: ImageList,
    meta: {
      requiresAuth: true,
      roles: ['ROLE_ADMIN']
    }
  },
    {
      path: '/admin/todayChart',
      name: 'todayChart',
      component: TodayChart,
      meta:{
        requiresAuth: true,
        roles: ['ROLE_ADMIN']
      },
    }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const { isLogin, fetchUserInfo, hasRole, userInfo } = useAuth()
  const token = localStorage.getItem('jwt')

  // 로그인 상태가 아니지만 토큰이 있는 경우: 사용자 정보 불러오기
  if (!isLogin.value && token) {
    try {
      await fetchUserInfo()
    } catch (e) {
      console.error('사용자 정보 가져오기 실패', e)
      localStorage.removeItem('jwt')
      return next('/login')
    }
  }

  const meta = to.meta as Meta

  // 인증이 필요한 페이지인데 로그인 안 되어 있으면
  if (meta.requiresAuth && !isLogin.value) {
    return next('/login')
  }

  // 권한이 필요한 페이지인데 권한 없으면
  if (meta.roles) {
    // authList가 아직 없으면 막기 (에러 방지)
    if (!userInfo.value?.authList) {
      return next('/login') // 또는 로딩 대기 페이지로 보내도 됨
    }

    if (!meta.roles.some(role => hasRole(role))) {
      alert('접근 권한이 없습니다')
      return next('/')
    }
  }

  next()
})


export default router
