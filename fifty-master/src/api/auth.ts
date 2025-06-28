import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080',
})

// ✅ 모든 요청 전에 Authorization 헤더에 JWT 자동으로 넣기
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

const login = (username: string, password: string) =>
  api.post('/login', { username, password })

const register = (data:any) =>
  api.post('/users', data )

const getUserInfo = () => {
  const token = localStorage.getItem('token')  // 로그인 후 저장한 JWT
  return api.get('/users/info', {
    headers: {
      Authorization: `Bearer ${token}`
    }
  })
}


export default{
  login,
  register,
  getUserInfo
};