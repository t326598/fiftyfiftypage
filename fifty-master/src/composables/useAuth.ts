import { ref } from 'vue'
import { useRouter } from 'vue-router'
import authApi from '@/api/auth'

const isLogin = ref(false)
const userInfo = ref<any>(null)
const roles = ref<string[]>([])

export function useAuth() {
  const router = useRouter()

const login = async (username: string, password: string) => {
  try {
    const res = await authApi.login(username, password)
    console.log('서버 응답:', res.data)
    const token = res.data.token
    if (!token) {
      console.error('서버에서 토큰을 받지 못했습니다.')
      return
    }

    console.log('받은 토큰:', token) // ✅ 확인용
    localStorage.setItem('jwt', token)

    // ✅ 토큰 저장 후 userInfo 요청
    await fetchUserInfo()

    isLogin.value = true
    router.push('/admin/noticeList')
  } catch (e) {
    alert('로그인 실패')
    console.error(e)
  }
}
  const register = async (data:any) => {
    try {
      await authApi.register(data)
      alert('회원가입 성공')
      router.push('/login')
    } catch (e) {
      alert('회원가입 실패')
    }
  }

  const logout = () => {
    localStorage.removeItem('jwt')
    userInfo.value = null
    isLogin.value = false
    roles.value = []
    router.push('/login')
  }

  const fetchUserInfo = async () => {
    try {
      const res = await authApi.getUserInfo()
      userInfo.value = res.data
      roles.value = res.data.roles
      isLogin.value = true
    } catch {
      logout()
    }
  }

const hasRole = (role: string) => {
  return userInfo.value?.authList?.some((auth: { auth: string }) => auth?.auth?.includes(role)) ?? false;
}

  return {
    isLogin,
    userInfo,
    roles,
    login,
    logout,
    register,
    fetchUserInfo,
    hasRole
  }
}
