<template>
  <div class="page">
    <!-- 背景层 -->
    <div class="bg-layer">
      <div class="bg-grid"></div>
      <div class="bg-radar"></div>
      <div class="bg-glow"></div>
      <div class="scan-line"></div>
    </div>

    <div class="login-container">
      <!-- 左侧装饰性元素 -->
      <div class="side-deco left">
        <div class="deco-line"></div>
        <div class="deco-data">
          <span>SYSTEM_STATUS: OK</span>
          <span>ENCRYPTION: AES-256</span>
          <span>LATENCY: 12ms</span>
        </div>
      </div>

      <div class="login-wrap">
        <div class="brand">
          <div class="brand-logo">
            <span class="logo-hex">⬡</span>
            <div class="logo-rings">
              <span></span><span></span>
            </div>
          </div>
          <h1 class="brand-name">DRONE<em>EVAL</em></h1>
          <p class="brand-sub">无人机视觉模型鲁棒性评测平台</p>
        </div>

        <!-- 登录卡片 -->
        <div class="login-card">
          <!-- 装饰角 -->
          <div class="card-corner tl"></div>
          <div class="card-corner tr"></div>
          <div class="card-corner bl"></div>
          <div class="card-corner br"></div>

          <div class="card-header">
            <div class="tabs">
              <button
                class="tab"
                :class="{ active: mode === 'login' }"
                @click="switchMode('login')"
              >
                <span class="tab-text">访问控制</span>
                <span class="tab-sub">LOGIN</span>
              </button>
              <button
                class="tab"
                :class="{ active: mode === 'register' }"
                @click="switchMode('register')"
              >
                <span class="tab-text">建立档案</span>
                <span class="tab-sub">REGISTER</span>
              </button>
              <button
                v-if="mode === 'forgot'"
                class="tab active"
              >
                <span class="tab-text">找回密钥</span>
                <span class="tab-sub">RESET</span>
              </button>
            </div>
          </div>

          <div class="form-container">
            <div class="form-group">
              <!-- 找回密码模式：第一步输入邮箱获取验证码 -->
              <div class="field" v-if="mode === 'forgot'">
                <div class="field-header">
                  <span class="field-label">绑定邮箱</span>
                  <span class="field-id">#RECOVERY_EMAIL</span>
                </div>
                <div class="input-wrapper email-input-group">
                  <input
                    v-model="form.email"
                    class="field-input"
                    placeholder="请输入绑定的邮箱"
                  />
                  <button 
                    class="send-code-btn" 
                    :disabled="codeTimer > 0 || !form.email || loading"
                    @click="handleSendCode"
                  >
                    {{ codeTimer > 0 ? `${codeTimer}s` : '获取验证码' }}
                  </button>
                  <div class="input-focus-line"></div>
                </div>
              </div>

              <!-- 找回密码模式：第二步输入验证码 -->
              <div class="field" v-if="mode === 'forgot'">
                <div class="field-header">
                  <span class="field-label">验证码</span>
                  <span class="field-id">#VERIFY_CODE</span>
                </div>
                <div class="input-wrapper">
                  <input
                    v-model="form.code"
                    class="field-input"
                    placeholder="请输入邮箱验证码"
                  />
                  <div class="input-focus-line"></div>
                </div>
              </div>

              <div class="field" v-if="mode !== 'forgot'">
                <div class="field-header">
                  <span class="field-label">识别码</span>
                  <span class="field-id">#USER_ID</span>
                </div>
                <div class="input-wrapper">
                  <input
                    v-model="form.username"
                    class="field-input"
                    placeholder="请输入用户名"
                    @keyup.enter="handleSubmit"
                  />
                  <div class="input-focus-line"></div>
                </div>
              </div>

              <!-- 注册时显示邮箱（必填） -->
              <div class="field" v-if="mode === 'register'">
                <div class="field-header">
                  <span class="field-label">通信信道</span>
                  <span class="field-id">#EMAIL</span>
                </div>
                <div class="input-wrapper">
                  <input
                    v-model="form.email"
                    class="field-input"
                    placeholder="请输入邮箱（必填）"
                    @keyup.enter="handleSubmit"
                  />
                  <div class="input-focus-line"></div>
                </div>
              </div>

              <div class="field">
                <div class="field-header">
                  <span class="field-label">{{ mode === 'forgot' ? '新访问密钥' : '访问密钥' }}</span>
                  <span class="field-id">#ACCESS_KEY</span>
                </div>
                <div class="input-wrapper">
                  <input
                    v-model="form.password"
                    type="password"
                    class="field-input"
                    :placeholder="mode === 'forgot' ? '请输入新密码' : '请输入密码'"
                    @keyup.enter="handleSubmit"
                  />
                  <div class="input-focus-line"></div>
                </div>
              </div>

              <div class="field" v-if="mode !== 'login'">
                <div class="field-header">
                  <span class="field-label">密钥确认</span>
                  <span class="field-id">#VERIFY</span>
                </div>
                <div class="input-wrapper">
                  <input
                    v-model="form.confirmPassword"
                    type="password"
                    class="field-input"
                    placeholder="再次输入密码"
                    @keyup.enter="handleSubmit"
                  />
                  <div class="input-focus-line"></div>
                </div>
              </div>
            </div>

            <div class="form-footer">
              <div class="forgot-link-wrap" v-if="mode === 'login'">
                <button class="forgot-btn" @click="mode = 'forgot'">忘记密码？</button>
              </div>
              <div class="forgot-link-wrap" v-if="mode === 'forgot'">
                <button class="forgot-btn" @click="mode = 'login'">返回登录</button>
              </div>

              <button class="login-btn" @click="handleSubmit" :disabled="loading">
                <div class="btn-bg"></div>
                <span v-if="loading" class="loading-spinner"></span>
                <span class="btn-text">
                  {{ loading ? '同步中...' : mode === 'login' ? '执行登录指令' : mode === 'forgot' ? '确认修改密钥' : '建立访问权限' }}
                </span>
                <span class="btn-arrow">→</span>
              </button>

              <p class="error" v-if="error">
                <span class="error-icon">!</span> {{ error }}
              </p>
              <p class="success" v-if="successMsg">
                <span class="success-icon">✓</span> {{ successMsg }}
              </p>
            </div>
          </div>
        </div>

        <div class="footer-info">
          <div class="info-item">
            <span class="info-label">PROTOCOL</span>
            <span class="info-val">DRONE-V1.0</span>
          </div>
          <div class="info-divider"></div>
          <div class="info-item">
            <span class="info-label">SEC_LEVEL</span>
            <span class="info-val">HIGH</span>
          </div>
        </div>
      </div>

      <!-- 右侧装饰性元素 -->
      <div class="side-deco right">
        <div class="deco-hex-grid">
          <span v-for="i in 6" :key="i">⬡</span>
        </div>
        <div class="deco-line"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/index.js'

const router = useRouter()

const mode = ref('login') // 'login' | 'register' | 'forgot'
const loading = ref(false)
const error = ref('')
const successMsg = ref('')
const codeTimer = ref(0)
let timerId = null

const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  code: '', // 找回密码验证码
})

// 切换 tab 时清空状态
function switchMode(m) {
  mode.value = m
  error.value = ''
  successMsg.value = ''
  form.value = { username: '', email: '', password: '', confirmPassword: '', code: '' }
  if (timerId) {
    clearInterval(timerId)
    codeTimer.value = 0
  }
}

function handleSubmit() {
  if (mode.value === 'login') {
    handleLogin()
  } else if (mode.value === 'register') {
    handleRegister()
  } else {
    handleResetPassword()
  }
}

// ── 发送验证码 ────────────────────────────────
async function handleSendCode() {
  if (!form.value.email) {
    error.value = '请先输入邮箱'
    return
  }
  
  loading.value = true
  error.value = ''
  console.log(form.value.email);
  
  try {
    await api.post('/auth/send', { email: form.value.email })
    successMsg.value = '验证码已发送至邮箱'
    startTimer()
  } catch (e) {
    error.value = e.response?.data?.message ?? '发送失败，请检查邮箱是否正确'
  } finally {
    loading.value = false
  }
}

function startTimer() {
  codeTimer.value = 60
  timerId = setInterval(() => {
    codeTimer.value--
    if (codeTimer.value <= 0) {
      clearInterval(timerId)
    }
  }, 1000)
}

// ── 重置密码 ──────────────────────────────────
async function handleResetPassword() {
  error.value = ''
  if (!form.value.email || !form.value.code || !form.value.password) {
    error.value = '请填写完整信息'
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    error.value = '两次密码不一致'
    return
  }

  loading.value = true
  try {
    await api.post('/auth/update', {
      email: form.value.email,
      verificationCode: form.value.code,
      password: form.value.password
    })
    successMsg.value = '密码重置成功'
    setTimeout(() => switchMode('login'), 1500)
  } catch (e) {
    error.value = e.response?.data?.message ?? '重置失败，验证码可能已过期'
  } finally {
    loading.value = false
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
    console.log('res对象:', res)
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
  if (!form.value.username || !form.value.password || !form.value.email) {
    error.value = '用户名、密码和邮箱不能为空'
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
  background: #05070a;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Noto Sans SC', sans-serif;
  position: relative;
  overflow: hidden;
  color: #d4d8de;
}

/* ── 背景层 ── */
.bg-layer { position: absolute; inset: 0; pointer-events: none; }

.bg-grid {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(rgba(245, 158, 11, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(245, 158, 11, 0.05) 1px, transparent 1px);
  background-size: 60px 60px;
}

.bg-radar {
  position: absolute;
  top: 50%; left: 50%;
  width: 800px; height: 800px;
  transform: translate(-50%, -50%);
  background: radial-gradient(circle, rgba(245, 158, 11, 0.03) 0%, transparent 70%);
  border: 1px solid rgba(245, 158, 11, 0.05);
  border-radius: 50%;
}

.bg-glow {
  position: absolute;
  top: -10%; right: -10%;
  width: 600px; height: 600px;
  background: radial-gradient(circle, rgba(6, 182, 212, 0.05) 0%, transparent 70%);
  filter: blur(80px);
}

.scan-line {
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(245, 158, 11, 0.2), transparent);
  animation: scan 8s linear infinite;
  z-index: 2;
}
@keyframes scan { 0% { top: -10%; } 100% { top: 110%; } }

/* ── 容器布局 ── */
.login-container {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 60px;
  width: 100%;
  max-width: 1100px;
  padding: 40px;
}

.login-wrap {
  flex: 1;
  max-width: 440px;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* ── 品牌 ── */
.brand { text-align: center; }
.brand-logo {
  position: relative;
  width: 64px; height: 64px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.logo-hex {
  font-size: 2.4rem;
  color: #f59e0b;
  z-index: 2;
}
.logo-rings span {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  border: 1px solid rgba(245, 158, 11, 0.3);
  border-radius: 50%;
  animation: ring-pulse 4s infinite;
}
.logo-rings span:last-child { animation-delay: 2s; }
@keyframes ring-pulse { 0% { transform: scale(0.8); opacity: 0; } 50% { opacity: 1; } 100% { transform: scale(1.5); opacity: 0; } }

.brand-name {
  font-family: 'Share Tech Mono', monospace;
  font-size: 2rem;
  letter-spacing: 0.25em;
  margin: 0 0 8px;
  color: #f0f2f5;
}
.brand-name em { font-style: normal; color: #f59e0b; text-shadow: 0 0 15px rgba(245,158,11,0.3); }
.brand-sub { font-size: 0.85rem; color: #6b7280; letter-spacing: 0.1em; margin: 0; }

/* ── 侧边装饰 ── */
.side-deco {
  display: flex;
  flex-direction: column;
  gap: 20px;
  opacity: 0.4;
}
.deco-line { width: 1px; height: 100px; background: linear-gradient(to bottom, transparent, #f59e0b, transparent); }
.deco-data {
  display: flex;
  flex-direction: column;
  gap: 12px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.6rem;
  color: #f59e0b;
  letter-spacing: 0.1em;
}
.deco-hex-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  font-size: 1.2rem;
  color: #f59e0b;
}

/* ── 登录卡片 ── */
.login-card {
  position: relative;
  background: rgba(13, 16, 23, 0.8);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(156, 163, 175, 0.2);
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
}

.card-corner {
  position: absolute;
  width: 12px; height: 12px;
  border-color: #f59e0b;
  border-style: solid;
  z-index: 5;
}
.tl { top: 0; left: 0; border-width: 2px 0 0 2px; }
.tr { top: 0; right: 0; border-width: 2px 2px 0 0; }
.bl { bottom: 0; left: 0; border-width: 0 0 2px 2px; }
.br { bottom: 0; right: 0; border-width: 0 2px 2px 0; }

.card-header { border-bottom: 1px solid rgba(156, 163, 175, 0.1); }
.tabs { display: flex; }
.tab {
  flex: 1;
  background: none;
  border: none;
  padding: 20px 0;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  transition: all 0.3s;
  position: relative;
}
.tab-text { font-size: 0.95rem; color: #9ca3af; transition: color 0.3s; }
.tab-sub { font-family: 'Share Tech Mono', monospace; font-size: 0.6rem; color: #4b5563; letter-spacing: 0.1em; }

.tab.active .tab-text { color: #f59e0b; font-weight: 700; }
.tab.active::after {
  content: '';
  position: absolute;
  bottom: 0; left: 0; right: 0;
  height: 2px;
  background: #f59e0b;
  box-shadow: 0 0 10px rgba(245,158,11,0.5);
}
.tab:not(.active):hover .tab-text { color: #d4d8de; }

/* ── 表单 ── */
.form-container { padding: 32px; }
.form-group { display: flex; flex-direction: column; gap: 24px; }

.field-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}
.field-label { font-size: 0.75rem; color: #f59e0b; font-weight: 700; letter-spacing: 0.05em; }
.field-id { font-family: 'Share Tech Mono', monospace; font-size: 0.6rem; color: #4b5563; }

.input-wrapper { position: relative; }
.field-input {
  width: 100%;
  background: rgba(8, 10, 13, 0.6);
  border: 1px solid rgba(156, 163, 175, 0.3);
  padding: 12px 16px;
  color: #e8eaed;
  font-size: 0.9rem;
  border-radius: 2px;
  outline: none;
  transition: all 0.3s;
}
.field-input::placeholder { color: #374151; }
.field-input:focus { border-color: rgba(245, 158, 11, 0.5); background: rgba(8, 10, 13, 0.8); }

.input-focus-line {
  position: absolute;
  bottom: 0; left: 50%;
  width: 0; height: 1px;
  background: #f59e0b;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}
.field-input:focus + .input-focus-line { width: 100%; left: 0; }

/* ── 找回密码 ── */
.forgot-link-wrap {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}
.forgot-btn {
  background: none;
  border: none;
  color: #4b5563;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.75rem;
  cursor: pointer;
  transition: color 0.2s;
}
.forgot-btn:hover { color: #f59e0b; }

.email-input-group {
  display: flex;
  gap: 12px;
}
.send-code-btn {
  flex-shrink: 0;
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.3);
  color: #f59e0b;
  padding: 0 16px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.75rem;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.2s;
  white-space: nowrap;
}
.send-code-btn:hover:not(:disabled) {
  background: #f59e0b;
  color: #0a0c0f;
}
.send-code-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  border-color: #374151;
  color: #374151;
}

/* ── 按钮 ── */
.form-footer { margin-top: 32px; display: flex; flex-direction: column; gap: 20px; }

.login-btn {
  position: relative;
  width: 100%;
  padding: 14px;
  background: none;
  border: 1px solid #f59e0b;
  color: #f59e0b;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.9rem;
  font-weight: 700;
  letter-spacing: 0.2em;
  cursor: pointer;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  transition: all 0.3s;
}
.btn-bg {
  position: absolute;
  top: 0; left: -100%;
  width: 100%; height: 100%;
  background: #f59e0b;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: -1;
}
.login-btn:hover:not(:disabled) { color: #0a0c0f; }
.login-btn:hover:not(:disabled) .btn-bg { left: 0; }
.login-btn:disabled { opacity: 0.4; cursor: not-allowed; border-color: #374151; color: #374151; }

.btn-arrow { transition: transform 0.3s; }
.login-btn:hover .btn-arrow { transform: translateX(5px); }

.loading-spinner {
  width: 16px; height: 16px;
  border: 2px solid rgba(245, 158, 11, 0.3);
  border-top-color: currentColor;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ── 状态提示 ── */
.error, .success {
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  padding: 10px 14px;
  border-radius: 2px;
  animation: slideIn 0.3s ease-out;
}
.error { color: #f43f5e; background: rgba(244, 63, 94, 0.1); border: 1px solid rgba(244, 63, 94, 0.2); }
.success { color: #10b981; background: rgba(16, 185, 129, 0.1); border: 1px solid rgba(16, 185, 129, 0.2); }

@keyframes slideIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }

/* ── 底部信息 ── */
.footer-info {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
}
.info-item { display: flex; flex-direction: column; align-items: center; gap: 4px; }
.info-label { font-family: 'Share Tech Mono', monospace; font-size: 0.55rem; color: #4b5563; }
.info-val { font-family: 'Share Tech Mono', monospace; font-size: 0.7rem; color: #9ca3af; }
.info-divider { width: 1px; height: 20px; background: rgba(156, 163, 175, 0.1); }

/* ── 动画 ── */
.fade-enter-active, .fade-leave-active { transition: all 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(-10px); }

/* ── 响应式 ── */
@media (max-width: 900px) {
  .side-deco { display: none; }
  .login-container { justify-content: center; padding: 20px; }
}
</style>