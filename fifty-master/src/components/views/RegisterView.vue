<template>
  <div class="register-container">
    <img
      src="@/assets/image/3333.svg"
      alt="FiftyFifty 로고"
      class="logo"
    >

    <h2>회원가입</h2>

    <input
      v-model="username"
      placeholder="아이디"
      class="input"
    >
    <input
      v-model="password"
      type="password"
      placeholder="비밀번호"
      class="input"
    >
    <input
      v-model="email"
      type="email"
      placeholder="이메일"
      class="input"
    >

    <button
      class="register-btn"
      @click="handleRegister"
    >
      회원가입
    </button>
    <p class="login-link">
      이미 계정이 있으신가요?
      <router-link to="/login">
        로그인
      </router-link>
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import axios from '@/api/auth'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')
const email = ref('')

const handleRegister = async () => {
  try {
    const res = await axios.register({
      username:username.value,
      password:password.value,
      email:email.value
    })

    if (res.status === 200 || res.status === 201) {
      alert('회원가입이 완료되었습니다.')
      router.push('/login')
    } else {
      alert('회원가입 실패')
    }
  } catch (e) {
    console.error('회원가입 에러:', e)
    alert('회원가입 중 오류가 발생했습니다.')
  }
}
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 40px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 12px;
  box-shadow: 2px 2px 10px #eee;
  text-align: center;
  background-color: #fff;
}

.logo {
  width: 100px;
  margin-bottom: 20px;
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

.input {
  width: 100%;
  padding: 10px;
  margin-bottom: 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 14px;
}

.register-btn {
  width: 100%;
  padding: 12px;
  background-color: #3f51b5;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s;
}

.register-btn:hover {
  background-color: #303f9f;
}

.login-link {
  margin-top: 16px;
  font-size: 14px;
}
</style>
