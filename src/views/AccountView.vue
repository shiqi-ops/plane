<template>
  <div class="account-page">
    <div class="page-header">
      <div class="ph-bg"></div>
      <div class="ph-content">
        <span class="ph-eyebrow">USER CENTER</span>
        <h1 class="ph-title">个人中心</h1>
        <p class="ph-desc">整合用户信息、数据统计与评测任务管理</p>
      </div>
    </div>

    <div class="account-layout">
      <!-- 左侧：个人信息与概览 -->
      <aside class="account-sidebar">
        <div class="sidebar-section">
          <div class="profile-summary">
            <div class="avatar-wrap">
              <div class="avatar">{{ username[0]?.toUpperCase() }}</div>
              <div class="avatar-ring"></div>
            </div>
            <h2 class="user-name">{{ username }}</h2>
            <p class="user-team">团队：{{ teamName }}</p>
            <div class="user-tags">
              <span class="u-tag">高级评测员</span>
              <span class="u-tag">实验室成员</span>
            </div>
          </div>
        </div>

        <div class="sidebar-section">
          <h3 class="side-title">数据概览 (Overview)</h3>
          <div class="mini-stats">
            <div class="m-stat">
              <span class="m-label">累计评测</span>
              <span class="m-val">{{ stats.evalCount }}</span>
            </div>
            <div class="m-stat">
              <span class="m-label">覆盖模型</span>
              <span class="m-val">{{ stats.modelCount }}</span>
            </div>
            <div class="m-stat">
              <span class="m-label">生成报告</span>
              <span class="m-val">{{ stats.reportCount }}</span>
            </div>
            <div class="m-stat">
              <span class="m-label">平均分</span>
              <span class="m-val highlight">{{ stats.avgScore }}</span>
            </div>
          </div>
        </div>

        <div class="sidebar-section">
          <h3 class="side-title">快捷操作 (Actions)</h3>
          <div class="action-list">
            <button class="side-btn" @click="exportSummary">
              <span class="icon">⤓</span> 导出个人汇总报告
            </button>
            <button class="side-btn" @click="tab = 'security'">
              <span class="icon">⚙</span> 账户安全设置
            </button>
            <button class="side-btn danger" @click="clearHistory">
              <span class="icon">×</span> 清空所有历史
            </button>
          </div>
        </div>
      </aside>

      <!-- 右侧：任务管理与设置 -->
      <main class="account-main">
        <div class="main-hd">
          <div class="main-tabs">
            <button class="m-tab" :class="{ active: tab === 'history' }" @click="tab = 'history'">
              评测任务管理
              <span class="m-badge">{{ history.length }}</span>
            </button>
            <button class="m-tab" :class="{ active: tab === 'security' }" @click="tab = 'security'">
              安全设置
            </button>
          </div>
        </div>

        <div class="main-body">
          <!-- 历史记录列表 -->
          <div v-if="tab === 'history'" class="history-container">
            <div class="filter-bar">
              <div class="f-group">
                <label>筛选模型</label>
                <select v-model="filterModel" class="f-select">
                  <option value="">全部模型</option>
                  <option v-for="m in uniqueModels" :key="m" :value="m">{{ m }}</option>
                </select>
              </div>
              <div class="f-group">
                <label>攻击类型</label>
                <input v-model="filterAttack" placeholder="搜索攻击方式..." class="f-input" />
              </div>
            </div>

            <div v-if="filteredHistory.length === 0" class="empty-state">
              <span class="e-icon">◎</span>
              <p>没有找到符合条件的任务记录</p>
            </div>

            <div v-else class="task-grid">
              <div v-for="r in filteredHistory" :key="r.id" class="task-card">
                <div class="card-hd">
                  <span class="task-type" :class="'type-' + r.type">
                    {{ { one: '单攻击', more: '组合', own: '自定义' }[r.type] }}
                  </span>
                  <span class="task-date">{{ r.date }}</span>
                </div>
                <div class="card-body">
                  <div class="task-info">
                    <h4 class="task-name">{{ r.model }}</h4>
                    <p class="task-sub">{{ r.attack ?? r.attack_group }}</p>
                  </div>
                  <div class="health-score">
                    <div class="ring-chart" :style="ringStyle(r.result.robust_score)">
                      <span class="score-num">{{ r.result.robust_score?.toFixed(0) }}</span>
                    </div>
                    <span class="score-label">稳定性指数</span>
                  </div>
                </div>
                <div class="card-footer">
                  <div class="footer-stats">
                    <div class="f-stat">
                      <span class="fs-label">清洁</span>
                      <span class="fs-val">{{ (r.result.clean_accuracy * 100).toFixed(1) }}%</span>
                    </div>
                    <div class="f-stat">
                      <span class="fs-label">对抗</span>
                      <span class="fs-val warn">{{ (r.result.adv_accuracy * 100).toFixed(1) }}%</span>
                    </div>
                  </div>
                  <div class="card-actions">
                    <button class="mini-btn" @click="reEvaluate(r)">重新评测</button>
                    <button class="mini-btn" @click="exportPDF(r)">PDF</button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 安全设置 -->
          <div v-if="tab === 'security'" class="security-container">
            <div class="security-card">
              <h3 class="sec-title">账号安全 (Security)</h3>
              <div class="sec-item">
                <div class="sec-info">
                  <p class="sec-label">登录密码</p>
                  <p class="sec-desc">互联网账号建议定期修改密码，确保安全</p>
                </div>
                <button class="sec-btn" @click="handleSecurity('change-pwd')">修改密码</button>
              </div>
              <div class="sec-item">
                <div class="sec-info">
                  <p class="sec-label">电子邮箱</p>
                  <p class="sec-desc">已绑定：{{ email }}</p>
                </div>
                <button class="sec-btn" @click="handleSecurity('bind-email')">更换邮箱</button>
              </div>
              <div class="sec-item">
                <div class="sec-info">
                  <p class="sec-label">双重认证</p>
                  <p class="sec-desc">未开启。开启后登录需验证码，增加额外安全层</p>
                </div>
                <button class="sec-btn outline" @click="handleSecurity('2fa')">立即开启</button>
              </div>
            </div>

            <div class="security-card">
              <h3 class="sec-title">偏好设置 (Preferences)</h3>
              <div class="sec-item">
                <div class="sec-info">
                  <p class="sec-label">消息通知</p>
                  <p class="sec-desc">评测完成时通过系统消息或邮件通知我</p>
                </div>
                <div class="toggle-switch active"></div>
              </div>
              <div class="sec-item">
                <div class="sec-info">
                  <p class="sec-label">报告公开性</p>
                  <p class="sec-desc">默认将生成的报告设为私有</p>
                </div>
                <div class="toggle-switch active"></div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const tab = ref('history')
const username = computed(() => localStorage.getItem('username') || 'USER')
const teamName = ref('无人机鲁棒性实验室 (Drone-Lab)')
const email = ref('shiqifu@drone-eval.com')

// 历史记录
const history = computed(() => {
  try { return JSON.parse(localStorage.getItem('evalHistory') || '[]').reverse() }
  catch { return [] }
})

// 统计数据
const stats = computed(() => {
  const list = history.value
  const models = new Set(list.map(h => h.model))
  const totalScore = list.reduce((acc, h) => acc + (h.result?.robust_score || 0), 0)
  return {
    evalCount: list.length,
    modelCount: models.size,
    reportCount: list.length,
    avgScore: list.length ? (totalScore / list.length).toFixed(1) : '0.0'
  }
})

// 筛选
const filterModel = ref('')
const filterAttack = ref('')
const filteredHistory = computed(() => {
  return history.value.filter(h => {
    const matchModel = !filterModel.value || h.model === filterModel.value
    const attackStr = (h.attack || h.attack_group || '').toLowerCase()
    const matchAttack = !filterAttack.value || attackStr.includes(filterAttack.value.toLowerCase())
    return matchModel && matchAttack
  })
})

const uniqueModels = computed(() => [...new Set(history.value.map(h => h.model))])

// 环形图样式
function ringStyle(score) {
  const percentage = Math.min(Math.max(score, 0), 100)
  let color = '#f43f5e' // D/Poor
  if (score >= 80) color = '#22c55e' // A
  else if (score >= 60) color = '#06b6d4' // B
  else if (score >= 40) color = '#f59e0b' // C
  
  return {
    background: `conic-gradient(${color} ${percentage}%, #1e2530 ${percentage}%)`
  }
}

function clearHistory() {
  if (confirm('确认清空所有历史记录？此操作不可撤销。')) {
    localStorage.removeItem('evalHistory')
    window.location.reload()
  }
}

function reEvaluate(task) {
  const path = task.type === 'more' ? '/evaluate-more' : '/evaluate-one'
  router.push(path)
}

function exportPDF(task) {
  alert(`正在为任务 [#${task.id}] 生成 PDF 详细报告...`)
}

function handleSecurity(action) {
  alert(`功能开发中：执行 ${action}`)
}

function exportSummary() {
  alert('正在汇总所有评测数据，生成个人年度鲁棒性分析报告...')
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=Noto+Sans+SC:wght@400;700&display=swap');

.account-page {
  min-height: calc(100vh - 58px);
  background: #0a0c0f;
  color: #d4d8de;
  font-family: 'Noto Sans SC', sans-serif;
  display: flex;
  flex-direction: column;
}

.page-header {
  position: relative;
  padding: 48px 48px 40px;
  border-bottom: 1px solid #1e2530;
  overflow: hidden;
}
.ph-bg {
  position: absolute; inset: 0;
  background-image: linear-gradient(#374151 1px,transparent 1px), linear-gradient(90deg,#374151 1px,transparent 1px);
  background-size: 48px 48px; opacity: 0.15;
}
.ph-content { position: relative; z-index: 1; }
.ph-eyebrow { display: block; font-family: 'Share Tech Mono', monospace; font-size: 0.62rem; letter-spacing: 0.25em; color: #f59e0b; margin-bottom: 8px; }
.ph-title { font-size: 2rem; font-weight: 700; color: #f0f2f5; margin: 0 0 8px; }
.ph-desc { color: #6b7280; font-size: 0.88rem; margin: 0; }

.account-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  flex: 1;
  background: #1e2530;
  gap: 1px;
}

/* Sidebar */
.account-sidebar {
  background: #0a0c0f;
  padding: 40px;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.sidebar-section { display: flex; flex-direction: column; gap: 20px; }
.side-title {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.68rem;
  letter-spacing: 0.15em;
  color: #4b5563;
  text-transform: uppercase;
  margin: 0;
}

.profile-summary { text-align: center; }
.avatar-wrap { position: relative; width: 88px; height: 88px; margin: 0 auto 20px; }
.avatar {
  width: 100%; height: 100%;
  background: rgba(245,158,11,0.05);
  border: 2px solid #f59e0b;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-family: 'Share Tech Mono', monospace; font-size: 2.2rem; color: #f59e0b;
}
.avatar-ring {
  position: absolute; inset: -6px;
  border: 1px dashed rgba(245,158,11,0.3);
  border-radius: 50%;
  animation: rotate 10s linear infinite;
}
@keyframes rotate { from { transform: rotate(0); } to { transform: rotate(360deg); } }

.user-name { font-size: 1.25rem; font-weight: 700; color: #f0f2f5; margin: 0 0 4px; }
.user-team { font-size: 0.8rem; color: #6b7280; margin: 0 0 16px; }
.user-tags { display: flex; justify-content: center; gap: 8px; }
.u-tag {
  font-size: 0.62rem; color: #9ca3af; background: #13171f;
  border: 1px solid #1e2530; padding: 2px 8px; border-radius: 2px;
}

.mini-stats { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.m-stat {
  background: rgba(255,255,255,0.02);
  border: 1px solid #1e2530;
  padding: 16px; border-radius: 4px;
  display: flex; flex-direction: column; gap: 4px;
}
.m-label { font-size: 0.65rem; color: #4b5563; }
.m-val { font-family: 'Share Tech Mono', monospace; font-size: 1.1rem; color: #d4d8de; }
.m-val.highlight { color: #f59e0b; }

.action-list { display: flex; flex-direction: column; gap: 8px; }
.side-btn {
  background: #0d1017; border: 1px solid #1e2530;
  color: #9ca3af; padding: 10px 16px; border-radius: 2px;
  text-align: left; font-size: 0.8rem; cursor: pointer;
  transition: all 0.2s; display: flex; align-items: center; gap: 12px;
}
.side-btn .icon { color: #f59e0b; font-size: 1rem; }
.side-btn:hover { background: #13171f; border-color: #374151; color: #f0f2f5; }
.side-btn.danger:hover { border-color: #f43f5e; color: #f43f5e; }

/* Main Content */
.account-main { background: #0a0c0f; display: flex; flex-direction: column; }

.main-hd {
  padding: 0 40px; border-bottom: 1px solid #1e2530;
  background: #0d1017;
}
.main-tabs { display: flex; gap: 32px; }
.m-tab {
  background: none; border: none; padding: 20px 0;
  font-family: 'Share Tech Mono', monospace; font-size: 0.85rem;
  letter-spacing: 0.1em; color: #4b5563; cursor: pointer;
  position: relative; transition: color 0.2s;
}
.m-tab.active { color: #f59e0b; }
.m-tab.active::after {
  content: ''; position: absolute; bottom: 0; left: 0; right: 0;
  height: 2px; background: #f59e0b;
}
.m-badge {
  font-size: 0.6rem; background: rgba(245,158,11,0.1); color: #f59e0b;
  padding: 1px 6px; border-radius: 10px; margin-left: 6px;
}

.main-body { padding: 40px; flex: 1; overflow-y: auto; }

/* History Section */
.filter-bar { display: flex; gap: 24px; margin-bottom: 32px; background: rgba(255,255,255,0.02); padding: 20px; border-radius: 4px; border: 1px solid #1e2530; }
.f-group { display: flex; flex-direction: column; gap: 8px; }
.f-group label { font-family: 'Share Tech Mono', monospace; font-size: 0.65rem; color: #4b5563; text-transform: uppercase; }
.f-select, .f-input {
  background: #0d1017; border: 1px solid #374151; color: #d4d8de;
  padding: 8px 12px; border-radius: 2px; outline: none; font-size: 0.85rem;
  transition: border 0.2s;
}
.f-select:focus, .f-input:focus { border-color: #f59e0b; }
.f-input { width: 200px; }

.task-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 24px; }
.task-card {
  background: #0d1017; border: 1px solid #1e2530; border-radius: 4px;
  padding: 20px; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex; flex-direction: column; gap: 20px;
}
.task-card:hover {
  transform: translateY(-4px); border-color: #374151;
  box-shadow: 0 12px 24px -12px rgba(0,0,0,0.5);
}

.card-hd { display: flex; justify-content: space-between; align-items: center; }
.task-type {
  font-family: 'Share Tech Mono', monospace; font-size: 0.65rem;
  padding: 2px 8px; border-radius: 2px;
}
.type-one { background: rgba(245,158,11,0.1); color: #f59e0b; }
.type-more { background: rgba(6,182,212,0.1); color: #06b6d4; }
.type-own { background: rgba(244,63,94,0.1); color: #f43f5e; }
.task-date { font-size: 0.75rem; color: #4b5563; font-family: 'Share Tech Mono', monospace; }

.card-body { display: flex; justify-content: space-between; align-items: center; }
.task-name { font-size: 1.1rem; font-weight: 700; color: #f0f2f5; margin: 0 0 4px; }
.task-sub { font-size: 0.8rem; color: #6b7280; margin: 0; font-family: 'Share Tech Mono', monospace; }

.health-score { text-align: center; }
.ring-chart {
  width: 56px; height: 56px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  position: relative; margin-bottom: 4px;
}
.ring-chart::after {
  content: ''; position: absolute; inset: 4px;
  background: #0d1017; border-radius: 50%;
}
.score-num {
  position: relative; z-index: 1; font-family: 'Share Tech Mono', monospace;
  font-size: 1.1rem; color: #f0f2f5; font-weight: 700;
}
.score-label { font-size: 0.6rem; color: #4b5563; display: block; }

.card-footer {
  display: flex; justify-content: space-between; align-items: center;
  padding-top: 16px; border-top: 1px solid #1e2530;
}
.footer-stats { display: flex; gap: 16px; }
.f-stat { display: flex; flex-direction: column; gap: 2px; }
.fs-label { font-size: 0.6rem; color: #4b5563; }
.fs-val { font-family: 'Share Tech Mono', monospace; font-size: 0.85rem; color: #d4d8de; }
.fs-val.warn { color: #f59e0b; }

.card-actions { display: flex; gap: 8px; }
.mini-btn {
  background: transparent; border: 1px solid #374151; color: #9ca3af;
  padding: 4px 10px; font-size: 0.72rem; border-radius: 2px; cursor: pointer;
  transition: all 0.2s;
}
.mini-btn:hover { border-color: #f59e0b; color: #f59e0b; background: rgba(245,158,11,0.05); }

/* Security Section */
.security-container { display: flex; flex-direction: column; gap: 32px; max-width: 800px; }
.security-card {
  background: #0d1017; border: 1px solid #1e2530; border-radius: 4px;
  padding: 32px;
}
.sec-title { font-size: 1rem; font-weight: 700; color: #f0f2f5; margin: 0 0 24px; border-bottom: 1px solid #1e2530; padding-bottom: 12px; }
.sec-item { display: flex; justify-content: space-between; align-items: center; padding: 20px 0; border-bottom: 1px solid #1e2530; }
.sec-item:last-child { border-bottom: none; }
.sec-label { font-size: 0.95rem; color: #e8eaed; font-weight: 700; margin: 0 0 4px; }
.sec-desc { font-size: 0.82rem; color: #6b7280; margin: 0; }

.sec-btn {
  background: #1e2530; border: 1px solid #374151; color: #d4d8de;
  padding: 8px 16px; font-size: 0.82rem; border-radius: 2px; cursor: pointer;
  transition: all 0.2s;
}
.sec-btn:hover { border-color: #f59e0b; color: #f59e0b; }
.sec-btn.outline { border-color: #f59e0b; color: #f59e0b; background: transparent; }

.toggle-switch {
  width: 36px; height: 20px; background: #1e2530; border-radius: 10px;
  position: relative; cursor: pointer;
}
.toggle-switch::after {
  content: ''; position: absolute; left: 2px; top: 2px;
  width: 16px; height: 16px; background: #4b5563; border-radius: 50%;
  transition: all 0.2s;
}
.toggle-switch.active { background: rgba(245,158,11,0.2); }
.toggle-switch.active::after { left: 18px; background: #f59e0b; }

.empty-state { text-align: center; padding: 80px 0; }
.e-icon { font-size: 3rem; color: #1e2530; margin-bottom: 16px; display: block; }

@media (max-width: 1100px) {
  .account-layout { grid-template-columns: 1fr; }
  .account-sidebar { border-right: none; border-bottom: 1px solid #1e2530; }
}
</style>