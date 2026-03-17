<template>
  <div class="layout">
    <header class="topnav">
      <div class="nav-brand" @click="router.push('/home')">
        <span class="brand-icon">⬡</span>
        <span class="brand-name">DRONE<em>EVAL</em></span>
      </div>
      <nav class="nav-links">
        <router-link to="/home"       class="nav-link">首页</router-link>
        <router-link to="/algorithm"  class="nav-link">算法与模型</router-link>
        <router-link to="/robustness" class="nav-link">鲁棒性测试</router-link>
        <router-link to="/assistant"  class="nav-link">智能助手</router-link>
        <router-link to="/account"    class="nav-link">账户信息</router-link>
      </nav>
      <div class="nav-right">
        <span class="user-tag">{{ username }}</span>
        <button class="logout-btn" @click="handleLogout">退出</button>
      </div>
    </header>
    <main><RouterView /></main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { RouterView, useRouter } from 'vue-router'

const router = useRouter()
const username = computed(() => localStorage.getItem('username') || 'USER')

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
</style>