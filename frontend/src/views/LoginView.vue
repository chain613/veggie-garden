<template>
  <div class="login-container">
    <div class="login-card">
      <h1>🌱 菜园小院</h1>
      <p class="subtitle">沉浸式种菜体验</p>
      <input
        v-model="phone"
        type="tel"
        maxlength="11"
        placeholder="请输入手机号"
        class="phone-input"
      />
      <button @click="login" :disabled="loading" class="login-btn">
        {{ loading ? '进入中...' : '进入菜园' }}
      </button>
      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { loginApi } from '../api/user'

const router = useRouter()
const userStore = useUserStore()
const phone = ref('')
const loading = ref(false)
const error = ref('')

async function login() {
  if (!/^1\d{10}$/.test(phone.value)) {
    error.value = '请输入正确的手机号'
    return
  }
  loading.value = true
  error.value = ''
  try {
    const res = await loginApi(phone.value)
    if (res.success) {
      userStore.setUser(res.user, res.token)
      router.push('/garden')
    } else {
      error.value = res.message
    }
  } catch (e) {
    error.value = '网络错误，请重试'
  }
  loading.value = false
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(180deg, #87CEEB 0%, #c9e8c2 60%, #5c4033 100%);
}
.login-card {
  background: rgba(255,255,255,0.9);
  padding: 40px 30px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
}
.phone-input {
  display: block;
  width: 100%;
  padding: 12px;
  font-size: 18px;
  border: 2px solid #7cbd6e;
  border-radius: 8px;
  margin: 20px 0;
  text-align: center;
}
.login-btn {
  width: 100%;
  padding: 12px;
  font-size: 18px;
  background: #5c4033;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
.login-btn:disabled { background: #aaa; }
.error { color: #d44; margin-top: 10px; }
</style>
