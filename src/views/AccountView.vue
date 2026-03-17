<template>
  <div class="account-page">
    <div class="page-header">
      <div class="ph-bg"></div>
      <div class="ph-content">
        <span class="ph-eyebrow">ACCOUNT</span>
        <h1 class="ph-title">账户信息</h1>
      </div>
    </div>

    <div class="account-body">
      <div class="tabs">
        <button class="tab" :class="{ active: tab === 'profile' }" @click="tab = 'profile'">
          个人信息
        </button>
        <button class="tab" :class="{ active: tab === 'history' }" @click="tab = 'history'">
          历史记录
          <span v-if="history.length" class="tab-badge">{{ history.length }}</span>
        </button>
      </div>

      <!-- 个人信息 -->
      <div v-if="tab === 'profile'" class="tab-panel">
        <div class="profile-card">
          <div class="avatar">{{ username[0]?.toUpperCase() }}</div>
          <div class="profile-rows">
            <div class="prow">
              <span class="prow-label">用户名</span>
              <span class="prow-value">{{ username }}</span>
            </div>
            <div class="prow">
              <span class="prow-label">身份</span>
              <span class="prow-value">评测用户</span>
            </div>
            <div class="prow">
              <span class="prow-label">累计评测次数</span>
              <span class="prow-value highlight">{{ history.length }} 次</span>
            </div>
            <div class="prow">
              <span class="prow-label">平台版本</span>
              <span class="prow-value">v1.0</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 历史记录 -->
      <div v-if="tab === 'history'" class="tab-panel">
        <div v-if="history.length === 0" class="empty">
          <span class="empty-icon">◎</span>
          <p>暂无历史记录</p>
          <span>完成一次评测后，记录将自动保存于此</span>
        </div>

        <div v-else>
          <div class="history-list">
            <div v-for="r in history" :key="r.id" class="hi-item">
              <div class="hi-left">
                <span class="hi-type" :class="'type-' + r.type">
                  {{ { one: '单攻击', more: '组合', own: '自定义' }[r.type] }}
                </span>
                <div>
                  <p class="hi-name">{{ r.model }} × {{ r.attack ?? r.attack_group }}</p>
                  <p class="hi-date">{{ r.date }}</p>
                </div>
              </div>
              <div class="hi-right">
                <div class="hi-stat">
                  <span class="hi-stat-label">清洁</span>
                  <span class="hi-stat-val">{{ (r.result.clean_accuracy * 100).toFixed(1) }}%</span>
                </div>
                <div class="hi-stat">
                  <span class="hi-stat-label">对抗</span>
                  <span class="hi-stat-val warn">{{ (r.result.adv_accuracy * 100).toFixed(1) }}%</span>
                </div>
                <span class="hi-level" :class="'level-' + r.result.robust_level">
                  {{ r.result.robust_level }}
                </span>
              </div>
            </div>
          </div>
          <button class="clear-btn" @click="clearHistory">清空全部记录</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const tab = ref('profile')
const username = computed(() => localStorage.getItem('username') || 'USER')

const history = computed(() => {
  try { return JSON.parse(localStorage.getItem('evalHistory') || '[]').reverse() }
  catch { return [] }
})

function clearHistory() {
  if (confirm('确认清空所有历史记录？')) localStorage.removeItem('evalHistory')
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=Noto+Sans+SC:wght@400;700&display=swap');

.account-page {
  min-height: calc(100vh - 58px);
  background: #0a0c0f;
  color: #d4d8de;
  font-family: 'Noto Sans SC', sans-serif;
}

.page-header {
  position: relative;
  padding: 48px 48px 40px;
  border-bottom: 1px solid #9ca3af;
  overflow: hidden;
}
.ph-bg {
  position: absolute; inset: 0;
  background-image: linear-gradient(#9ca3af 1px,transparent 1px), linear-gradient(90deg,#9ca3af 1px,transparent 1px);
  background-size: 48px 48px; opacity: 0.2;
}
.ph-content { position: relative; z-index: 1; }
.ph-eyebrow { display: block; font-family: 'Share Tech Mono', monospace; font-size: 0.62rem; letter-spacing: 0.25em; color: #f59e0b; margin-bottom: 8px; }
.ph-title { font-size: 2rem; font-weight: 700; color: #f0f2f5; margin: 0; }

.account-body { max-width: 860px; margin: 0 auto; padding: 40px 48px; }

/* Tabs */
.tabs {
  display: flex;
  gap: 0;
  border: 1px solid #9ca3af;
  border-radius: 2px;
  overflow: hidden;
  width: fit-content;
  margin-bottom: 40px;
}
.tab {
  background: none;
  border: none;
  padding: 8px 28px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.76rem;
  letter-spacing: 0.1em;
  color: #d4d8de;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
  border-right: 1px solid #9ca3af;
}
.tab:last-child { border-right: none; }
.tab.active { background: #f59e0b; color: #0a0c0f; font-weight: 700; }
.tab:not(.active):hover { color: #9ca3af; }

.tab-badge {
  background: rgba(245,158,11,0.2);
  color: #f59e0b;
  font-size: 0.62rem;
  padding: 1px 6px;
  border-radius: 10px;
}
.tab.active .tab-badge { background: rgba(0,0,0,0.15); color: #0a0c0f; }

/* Profile */
.profile-card {
  display: flex;
  gap: 40px;
  align-items: flex-start;
  background: #0d1017;
  border: 1px solid #9ca3af;
  border-radius: 4px;
  padding: 40px;
}

.avatar {
  width: 72px; height: 72px;
  background: rgba(245,158,11,0.1);
  border: 2px solid #f59e0b;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Share Tech Mono', monospace;
  font-size: 1.8rem;
  color: #f59e0b;
  flex-shrink: 0;
}

.profile-rows { display: flex; flex-direction: column; gap: 0; flex: 1; }

.prow {
  display: flex;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid #9ca3af;
  gap: 24px;
}
.prow:last-child { border-bottom: none; }

.prow-label {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.7rem;
  letter-spacing: 0.12em;
  color: #d4d8de;
  text-transform: uppercase;
  width: 120px;
  flex-shrink: 0;
}
.prow-value { font-size: 0.92rem; color: #e8eaed; }
.prow-value.highlight { color: #f59e0b; font-family: 'Share Tech Mono', monospace; font-size: 1rem; }

/* History */
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 80px 0;
  text-align: center;
}
.empty-icon { font-size: 3rem; color: #9ca3af; }
.empty p { font-size: 1rem; color: #d4d8de; margin: 0; }
.empty span { font-size: 0.82rem; color: #9ca3af; font-family: 'Share Tech Mono', monospace; }

.history-list {
  display: flex;
  flex-direction: column;
  gap: 1px;
  background: #9ca3af;
  border: 1px solid #9ca3af;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 20px;
}

.hi-item {
  background: #0d1017;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 24px;
  gap: 24px;
  transition: background 0.2s;
}
.hi-item:hover { background: #111520; }

.hi-left { display: flex; align-items: center; gap: 16px; }

.hi-type {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.65rem;
  letter-spacing: 0.08em;
  padding: 3px 10px;
  border-radius: 2px;
  white-space: nowrap;
  flex-shrink: 0;
}
.type-one  { background: rgba(245,158,11,0.1); color: #f59e0b; border: 1px solid rgba(245,158,11,0.2); }
.type-more { background: rgba(6,182,212,0.1);  color: #06b6d4; border: 1px solid rgba(6,182,212,0.2); }
.type-own  { background: rgba(244,63,94,0.1);  color: #f43f5e; border: 1px solid rgba(244,63,94,0.2); }

.hi-name { font-size: 0.9rem; color: #e8eaed; margin: 0 0 4px; font-family: 'Share Tech Mono', monospace; }
.hi-date { font-size: 0.72rem; color: #d4d8de; margin: 0; }

.hi-right { display: flex; align-items: center; gap: 20px; }

.hi-stat { display: flex; flex-direction: column; align-items: center; gap: 3px; }
.hi-stat-label { font-family: 'Share Tech Mono', monospace; font-size: 0.6rem; letter-spacing: 0.1em; color: #d4d8de; }
.hi-stat-val { font-family: 'Share Tech Mono', monospace; font-size: 0.88rem; color: #e8eaed; }
.hi-stat-val.warn { color: #f59e0b; }

.hi-level {
  font-family: 'Share Tech Mono', monospace;
  font-size: 1.2rem;
  font-weight: 700;
  width: 32px;
  text-align: center;
}
.level-A { color: #22c55e; }
.level-B { color: #06b6d4; }
.level-C { color: #f59e0b; }
.level-D, .level-Poor { color: #f43f5e; }

.clear-btn {
  background: none;
  border: 1px solid #d4d8de;
  color: #d4d8de;
  padding: 8px 20px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.72rem;
  letter-spacing: 0.08em;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.2s;
}
.clear-btn:hover { border-color: #f43f5e; color: #f43f5e; }
</style>