<template>
  <div class="page">
    <div class="bg-grid"></div>

    <div class="login-wrap">
      <div class="brand">
        <span class="brand-icon">⬡</span>
        <span class="brand-name">DRONE<em>EVAL</em></span>
      </div>

      <div class="login-card">
        <div class="card-header">
          <!-- Tab 切换 -->
          <div class="tabs">
            <button
              class="tab"
              :class="{ active: mode === 'login' }"
              @click="switchMode('login')"
            >登录</button>
            <button
              class="tab"
              :class="{ active: mode === 'register' }"
              @click="switchMode('register')"
            >注册</button>
          </div>
          <p class="card-sub">无人机视觉模型鲁棒性评测平台</p>
        </div>

        <div class="form">
          <div class="field">
            <label class="field-label">用户名</label>
            <input
              v-model="form.username"
              class="field-input"
              placeholder="请输入用户名"
              @keyup.enter="handleSubmit"
            />
          </div>

          <!-- 注册时额外显示 -->
          <div class="field" v-if="mode === 'register'">
            <label class="field-label">邮箱（选填）</label>
            <input
              v-model="form.email"
              class="field-input"
              placeholder="请输入邮箱"
              @keyup.enter="handleSubmit"
            />
          </div>

          <div class="field">
            <label class="field-label">密码</label>
            <input
              v-model="form.password"
              type="password"
              class="field-input"
              placeholder="请输入密码"
              @keyup.enter="handleSubmit"
            />
          </div>

          <div class="field" v-if="mode === 'register'">
            <label class="field-label">确认密码</label>
            <input
              v-model="form.confirmPassword"
              type="password"
              class="field-input"
              placeholder="再次输入密码"
              @keyup.enter="handleSubmit"
            />
          </div>

          <p class="error" v-if="error">⚠ {{ error }}</p>
          <p class="success" v-if="successMsg">✓ {{ successMsg }}</p>

          <button class="login-btn" @click="handleSubmit" :disabled="loading">
            <span v-if="loading" class="loading-dot"></span>
            {{ loading ? '请稍候...' : mode === 'login' ? '登 录' : '注 册' }}
          </button>
        </div>
      </div>

      <p class="footer-text">DRONE ROBUSTNESS PLATFORM · v1.0</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/index.js'

const router = useRouter()

const mode = ref('login') // 'login' | 'register'
const loading = ref(false)
const error = ref('')
const successMsg = ref('')

const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
})

// 切换 tab 时清空状态
function switchMode(m) {
  mode.value = m
  error.value = ''
  successMsg.value = ''
  form.value = { username: '', email: '', password: '', confirmPassword: '' }
}

function handleSubmit() {
  if (mode.value === 'login') {
    handleLogin()
  } else {
    handleRegister()
  }
}

// ── 登录 ──────────────────────────────────────
async function handleLogin() {
  error.value = ''
  if (!form.value.username || !form.value.password) {
    error.value = '请填写用户名和密码'
    return
  }

  // localStorage.setItem('token', 'mock-token')
  // localStorage.setItem('username', form.value.username)
  // router.push('/home')
  // return

  loading.value = true
  try {
    const res = await api.post('/auth/login', {
      username: form.value.username,
      password: form.value.password,
    })
    // 后端返回 { token: '...', username: '...' }
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('username', res.data.username ?? form.value.username)
    router.push('/home')
  } catch (e) {
    error.value = e.response?.data?.message ?? '用户名或密码错误'
  } finally {
    loading.value = false
  }
}

// ── 注册 ──────────────────────────────────────
async function handleRegister() {
  error.value = ''
  successMsg.value = ''

  // 前端校验
  if (!form.value.username || !form.value.password) {
    error.value = '用户名和密码不能为空'
    return
  }
  if (form.value.password.length < 6) {
    error.value = '密码至少 6 位'
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    error.value = '两次密码不一致'
    return
  }

  loading.value = true
  try {
    await api.post('/auth/register', {
      username: form.value.username,
      email: form.value.email, 
      password: form.value.password,
    })
    // 注册成功：提示后跳到登录 tab
    successMsg.value = '注册成功，请登录'
    setTimeout(() => switchMode('login'), 1200)
  } catch (e) {
    error.value = e.response?.data?.message ?? '注册失败，用户名可能已存在'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=Noto+Sans+SC:wght@400;700&display=swap');

.page {
  min-height: 100vh;
  background: #0a0c0f;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Noto Sans SC', sans-serif;
  position: relative;
  overflow: hidden;
}

.bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(#1e2530 1px, transparent 1px),
    linear-gradient(90deg, #1e2530 1px, transparent 1px);
  background-size: 48px 48px;
  opacity: 0.3;
  pointer-events: none;
}

.page::after {
  content: '';
  position: absolute;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(245, 158, 11, 0.06) 0%, transparent 70%);
  pointer-events: none;
}

.login-wrap {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32px;
  width: 100%;
  max-width: 420px;
  padding: 24px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 1.2rem;
  letter-spacing: 0.2em;
  color: #e8eaed;
}
.brand-icon { color: #f59e0b; font-size: 1.6rem; }
.brand-name em { font-style: normal; color: #f59e0b; }

.login-card {
  width: 100%;
  background: #0d1017;
  border: 1px solid #1e2530;
  border-top: 2px solid #f59e0b;
  border-radius: 4px;
  overflow: hidden;
}

/* ── Tab ── */
.card-header {
  padding: 24px 32px 20px;
  border-bottom: 1px solid #1e2530;
}

.tabs {
  display: flex;
  gap: 0;
  margin-bottom: 14px;
  border: 1px solid #1e2530;
  border-radius: 2px;
  overflow: hidden;
  width: fit-content;
}

.tab {
  background: none;
  border: none;
  padding: 7px 24px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.78rem;
  letter-spacing: 0.12em;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s;
}
.tab:first-child { border-right: 1px solid #1e2530; }
.tab.active {
  background: #f59e0b;
  color: #0a0c0f;
  font-weight: 700;
}
.tab:not(.active):hover { color: #9ca3af; }

.card-sub {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.7rem;
  letter-spacing: 0.1em;
  color: #374151;
  margin: 0;
}

/* ── 表单 ── */
.form {
  padding: 28px 32px 32px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.field { display: flex; flex-direction: column; gap: 8px; }

.field-label {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.7rem;
  letter-spacing: 0.15em;
  color: #f59e0b;
  text-transform: uppercase;
}

.field-input {
  background: #080a0d;
  border: 1px solid #1e2530;
  color: #e8eaed;
  padding: 11px 16px;
  font-family: 'Noto Sans SC', sans-serif;
  font-size: 0.9rem;
  border-radius: 2px;
  outline: none;
  transition: border 0.2s;
  width: 100%;
  box-sizing: border-box;
}
.field-input::placeholder { color: #374151; }
.field-input:focus { border-color: #f59e0b; }

.error {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.75rem;
  color: #f43f5e;
  letter-spacing: 0.05em;
  margin: 0;
}

.success {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.75rem;
  color: #22c55e;
  letter-spacing: 0.05em;
  margin: 0;
}

.login-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  background: #f59e0b;
  color: #0a0c0f;
  border: none;
  padding: 13px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.9rem;
  letter-spacing: 0.15em;
  font-weight: 700;
  cursor: pointer;
  border-radius: 2px;
  margin-top: 4px;
  transition: all 0.2s;
}
.login-btn:hover:not(:disabled) { background: #fbbf24; }
.login-btn:disabled { opacity: 0.4; cursor: not-allowed; }

.loading-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: #0a0c0f;
  animation: pulse 1s infinite;
}
@keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.3; } }

.footer-text {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.65rem;
  letter-spacing: 0.15em;
  color: #1e2530;
}
</style>