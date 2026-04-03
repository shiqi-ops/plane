<template>
  <div class="layout">
    <header class="topnav">
      <div class="nav-brand" @click="router.push('/home')">
        <span class="brand-icon">⬡</span>
        <span class="brand-name">DRONE<em>EVAL</em></span>
      </div>
      <nav class="nav-links">
        <router-link to="/home" class="nav-link">首页</router-link>
        <router-link to="/algorithm" class="nav-link">算法与模型</router-link>
        <router-link to="/robustness" class="nav-link">鲁棒性测试</router-link>
        <router-link to="/assistant" class="nav-link">智能助手</router-link>
        <router-link to="/account" class="nav-link">账户信息</router-link>
      </nav>
      <div class="nav-right">
        <span class="user-tag">{{ username }}</span>
        <button class="logout-btn" @click="handleLogout">退出</button>
      </div>
    </header>
    
    <main><RouterView /></main>

    <div class="bot-container">
      <Transition name="fade-slide">
        <div v-if="showBot" class="chat-bubble-window">
          <div class="chat-header">
            <span>⬡ AI 助手</span>
            <button class="close-mini" @click="showBot = false">×</button>
          </div>
          
          <div class="chat-content" ref="chatBox">
            <div v-if="messages.length === 0" class="mini-empty">
              需要什么帮助吗？
            </div>
            <div v-for="msg in messages" :key="msg.id" class="mini-msg" :class="msg.role">
              <div class="mini-bubble">{{ msg.content }}</div>
            </div>
            <div v-if="chatLoading" class="mini-msg ai">
              <div class="mini-bubble typing"><span></span><span></span><span></span></div>
            </div>
          </div>

          <div class="chat-input-area">
            <input 
              v-model="chatInput" 
              placeholder="输入问题..." 
              @keyup.enter="sendMsg()"
            />
            <button :disabled="!chatInput.trim() || chatLoading" @click="sendMsg()">发送</button>
          </div>
        </div>
      </Transition>

      <div class="bot-trigger" @click="showBot = !showBot">
        <img src="/bot-trigger.jpg" alt="AI Assistant" />
        <div v-if="!showBot" class="bot-tooltip">有问题问我</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { RouterView, useRouter } from 'vue-router'
import api from '../api/index.js' // 确保路径正确

const router = useRouter()
const username = computed(() => localStorage.getItem('username') || 'USER')

// 聊天逻辑
const showBot = ref(false)
const messages = ref([])
const chatInput = ref('')
const chatLoading = ref(false)
const chatBox = ref(null)
let msgId = 0

async function sendMsg() {
  const content = chatInput.value.trim()
  if (!content) return
  
  chatInput.value = ''
  messages.value.push({ id: ++msgId, role: 'user', content })
  await nextTick(); scrollBottom()

  chatLoading.value = true
  try {
    const res = await api.post('/ai/chat', { 
      id: Date.now().toString(), 
      messages: content 
    })
    messages.value.push({ 
      id: ++msgId, 
      role: 'ai', 
      content: typeof res.data === 'string' ? res.data : res.data.reply 
    })
  } catch {
    messages.value.push({ id: ++msgId, role: 'ai', content: '请求失败，请检查网络。' })
  } finally {
    chatLoading.value = false
    await nextTick(); scrollBottom()
  }
}

function scrollBottom() {
  if (chatBox.value) chatBox.value.scrollTop = chatBox.value.scrollHeight
}

function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  router.push('/login')
}
</script>
<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=Noto+Sans+SC:wght@400;700&display=swap');

.layout {
  min-height: 100vh;
  background: #0a0c0f;
  color: #d4d8de;
  font-family: 'Noto Sans SC', sans-serif;
  display: flex;
  flex-direction: column;
}

.topnav {
  display: flex;
  align-items: center;
  padding: 0 40px;
  height: 64px;
  border-bottom: 1px solid #1e2530;
  background: rgba(10,12,15,0.96);
  backdrop-filter: blur(10px);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 1rem;
  letter-spacing: 0.15em;
  color: #e8eaed;
  cursor: pointer;
  flex-shrink: 0;
  margin-right: 24px;
}
.brand-icon { color: #f59e0b; font-size: 1.2rem; }
.brand-name em { font-style: normal; color: #f59e0b; }

/* 导航链接平分剩余空间 */
.nav-links {
  display: flex;
  flex: 1;
  justify-content: space-around; /* 平分 */
  align-items: stretch;
  height: 100%;
}

.nav-link {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.88rem;          /* 字大一点 */
  letter-spacing: 0.08em;
  color: #6b7280;
  text-decoration: none;
  display: flex;
  align-items: center;
  padding: 0 12px;
  position: relative;
  transition: color 0.2s;
  white-space: nowrap;
}
.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0; left: 0; right: 0;
  height: 2px;
  background: #f59e0b;
  transform: scaleX(0);
  transition: transform 0.2s;
}
.nav-link:hover { color: #d4d8de; }
.router-link-active {
  color: #f59e0b !important;
}
.router-link-active::after { transform: scaleX(1); }

.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  margin-left: 24px;
}

.user-tag {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.75rem;
  color: #6b7280;
  background: #13171f;
  border: 1px solid #1e2530;
  padding: 4px 12px;
  border-radius: 2px;
  letter-spacing: 0.08em;
}

.logout-btn {
  background: none;
  border: 1px solid #374151;
  color: #9ca3af;
  padding: 6px 16px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.75rem;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.2s;
  letter-spacing: 0.08em;
}
.logout-btn:hover { border-color: #f59e0b; color: #f59e0b; }

main { flex: 1; display: flex; flex-direction: column; }

/* 机器人入口样式 */
/* 机器人容器定位 */
.bot-container {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.bot-trigger {
  width: 80px; /* 缩小一点更协调 */
  height: 80px;
  cursor: pointer;
  transition: transform 0.2s;
}
.bot-trigger:hover { transform: scale(1.1); }
.bot-trigger img {
  width: 100%; height: 100%;
  object-fit: cover;
  border-radius: 50%;
  border: 2px solid #1e2530;
  box-shadow: 0 8px 24px rgba(0,0,0,0.4);
}

/* 聊天气泡窗 */
.chat-bubble-window {
  width: 320px;
  height: 420px;
  background: #0d1017;
  border: 1px solid #1e2530;
  border-radius: 12px;
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 12px 32px rgba(0,0,0,0.5);
}

.chat-header {
  padding: 12px 16px;
  background: #13171f;
  border-bottom: 1px solid #1e2530;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-family: 'Share Tech Mono', monospace;
  color: #f59e0b;
  font-size: 0.85rem;
}

.close-mini {
  background: none; border: none; color: #6b7280;
  font-size: 1.2rem; cursor: pointer;
}

.chat-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background-image: radial-gradient(circle at center, rgba(245,158,11,0.02) 0%, transparent 100%);
}

/* 气泡样式微调 */
.mini-msg { display: flex; width: 100%; }
.mini-msg.user { justify-content: flex-end; }
.mini-bubble {
  max-width: 85%;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 0.85rem;
  line-height: 1.5;
}
.user .mini-bubble { background: #f59e0b; color: #0a0c0f; border-bottom-right-radius: 2px; }
.ai .mini-bubble { background: #1a1f26; color: #d4d8de; border: 1px solid #2d3543; border-bottom-left-radius: 2px; }

.chat-input-area {
  padding: 12px;
  display: flex;
  gap: 8px;
  background: #13171f;
  border-top: 1px solid #1e2530;
}
.chat-input-area input {
  flex: 1;
  background: #0a0c0f;
  border: 1px solid #374151;
  color: #f0f2f5;
  padding: 6px 12px;
  border-radius: 4px;
  outline: none;
  font-size: 0.8rem;
}
.chat-input-area button {
  background: #f59e0b;
  border: none;
  color: #0a0c0f;
  padding: 0 12px;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  font-size: 0.75rem;
}

/* 动画 */
.fade-slide-enter-active, .fade-slide-leave-active {
  transition: all 0.3s ease;
}
.fade-slide-enter-from, .fade-slide-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

/* 打字动画复用 */
.typing { display: flex; align-items: center; gap: 4px; padding: 4px 8px; }
.typing span {
  width: 4px; height: 4px;
  background: #f59e0b; border-radius: 50%;
  animation: typing 1.2s infinite;
}
@keyframes typing { 0%,60%,100%{transform:translateY(0)} 30%{transform:translateY(-4px)} }
</style>