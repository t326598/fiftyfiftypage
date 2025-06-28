<template>
  <div class="login-container">
    <img src="@/assets/image/3333.svg" alt="FiftyFifty 로고" class="logo" />

    <h2>로그인</h2>

    <input v-model="username" placeholder="아이디" class="input" />
    <input v-model="password" type="password" placeholder="비밀번호" class="input" />

    <button @click="handleLogin" class="login-btn">로그인</button>

    <p class="register-link">
      아직 회원이 아니신가요?
      <router-link to="/register">회원가입</router-link>
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuth } from '@/composables/useAuth'

const { login } = useAuth()
const username = ref('')
const password = ref('')

const handleLogin = () => login(username.value, password.value)
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 60px auto;
  padding: 30px;
  border: 1px solid #ccc;
  border-radius: 12px;
  box-shadow: 2px 2px 12px #eee;
  background-color: #fff;
  text-align: center;
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
  margin-bottom: 14px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 14px;
}

.login-btn {
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

.login-btn:hover {
  background-color: #303f9f;
}

.register-link {
  margin-top: 16px;
  font-size: 14px;
}
</style>
