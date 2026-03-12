<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <header class="navbar">
      <div class="navbar-brand">
        <span class="brand-icon">⬡</span>
        <span class="brand-name">DRONE<em>EVAL</em></span>
      </div>
      <div class="navbar-right">
        <span class="user-tag">{{ username }}</span>
        <button class="logout-btn" @click="handleLogout">退出</button>
      </div>
    </header>

    <!-- 主标题区 -->
    <section class="hero">
      <div class="hero-grid-bg"></div>
      <p class="hero-sub">无人机视觉模型 · 对抗鲁棒性评测平台</p>
      <h1 class="hero-title">选择评测模式</h1>
      <p class="hero-desc">
        支持三种工作流：单次精准评测 / 多攻击组合基准 / 自定义模型接入
      </p>
    </section>

    <!-- 三大功能卡片 -->
    <section class="cards">
      <div
        v-for="card in cards"
        :key="card.route"
        class="card"
        :class="'card--' + card.color"
        @click="router.push(card.route)"
      >
        <div class="card-number">{{ card.number }}</div>
        <div class="card-icon">{{ card.icon }}</div>
        <h2 class="card-title">{{ card.title }}</h2>
        <p class="card-desc">{{ card.desc }}</p>
        <ul class="card-tags">
          <li v-for="tag in card.tags" :key="tag">{{ tag }}</li>
        </ul>
        <div class="card-arrow">START →</div>
      </div>
    </section>

    <!-- 底部状态栏 -->
    <footer class="statusbar">
      <span>模型可用：ResNet18 · MobileNetV2 · EfficientNet-b0</span>
      <span>攻击方法：17 种</span>
      <span>数据集：drone_dataset</span>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const username = computed(() => localStorage.getItem('username') || 'USER')

function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  router.push('/login')
}

const cards = [
  {
    number: '01',
    icon: '◎',
    title: '单模型 × 单攻击',
    desc: '选择一个模型，搭配任意一种攻击方法，快速获取鲁棒性分数与可视化对比图。',
    tags: ['FGSM', 'PGD', 'CW', 'AutoAttack', '...共17种'],
    route: '/evaluate/one',
    color: 'amber',
  },
  {
    number: '02',
    icon: '◈',
    title: '多攻击组合基准',
    desc: '固定模型，选择推荐攻击组合（快速 / 标准 / 迁移 / 强攻击），批量评测并排名。',
    tags: ['快速攻击', '标准鲁棒性', '迁移攻击', '强攻击'],
    route: '/evaluate/more',
    color: 'cyan',
  },
  {
    number: '03',
    icon: '⬡',
    title: '自定义模型接入',
    desc: '上传你自己训练的 .pth 模型，选择攻击方式，自动生成完整评测报告。',
    tags: ['上传 .pth', '自选攻击', '自动报告'],
    route: '/evaluate/own',
    color: 'rose',
  },
]
</script>

<style scoped>
/* ── 全局基础 ── */
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=Noto+Sans+SC:wght@400;700&display=swap');

.home {
  min-height: 100vh;
  background-color: #0a0c0f;
  color: #d4d8de;
  font-family: 'Noto Sans SC', sans-serif;
  display: flex;
  flex-direction: column;
}

/* ── 导航栏 ── */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 48px;
  border-bottom: 1px solid #1e2530;
  background: #0a0c0f;
  position: sticky;
  top: 0;
  z-index: 10;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 1.1rem;
  letter-spacing: 0.15em;
  color: #e8eaed;
}

.brand-icon {
  color: #f59e0b;
  font-size: 1.4rem;
}

.brand-name em {
  font-style: normal;
  color: #f59e0b;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-tag {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.8rem;
  color: #6b7280;
  letter-spacing: 0.1em;
  background: #13171f;
  border: 1px solid #1e2530;
  padding: 4px 12px;
  border-radius: 2px;
}

.logout-btn {
  background: none;
  border: 1px solid #374151;
  color: #9ca3af;
  padding: 5px 14px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.78rem;
  letter-spacing: 0.08em;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.2s;
}

.logout-btn:hover {
  border-color: #f59e0b;
  color: #f59e0b;
}

/* ── Hero 区 ── */
.hero {
  position: relative;
  padding: 72px 48px 56px;
  overflow: hidden;
}

.hero-grid-bg {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(#1e2530 1px, transparent 1px),
    linear-gradient(90deg, #1e2530 1px, transparent 1px);
  background-size: 48px 48px;
  opacity: 0.35;
  pointer-events: none;
}

.hero-sub {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.75rem;
  letter-spacing: 0.2em;
  color: #f59e0b;
  text-transform: uppercase;
  margin: 0 0 12px;
}

.hero-title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 700;
  color: #f0f2f5;
  margin: 0 0 16px;
  letter-spacing: -0.02em;
  position: relative;
}

.hero-desc {
  font-size: 0.95rem;
  color: #6b7280;
  margin: 0;
  max-width: 520px;
  line-height: 1.7;
}

/* ── 卡片区 ── */
.cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1px;
  background: #1e2530;
  flex: 1;
  border-top: 1px solid #1e2530;
}

.card {
  background: #0d1017;
  padding: 48px 40px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: background 0.2s;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  opacity: 0;
  transition: opacity 0.2s;
}

.card--amber::before { background: #f59e0b; }
.card--cyan::before  { background: #06b6d4; }
.card--rose::before  { background: #f43f5e; }

.card:hover { background: #111520; }
.card:hover::before { opacity: 1; }

.card-number {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.7rem;
  letter-spacing: 0.2em;
  color: #374151;
}

.card-icon {
  font-size: 2rem;
  line-height: 1;
  transition: transform 0.2s;
}

.card--amber .card-icon { color: #f59e0b; }
.card--cyan  .card-icon { color: #06b6d4; }
.card--rose  .card-icon { color: #f43f5e; }

.card:hover .card-icon { transform: scale(1.1); }

.card-title {
  font-size: 1.2rem;
  font-weight: 700;
  color: #e8eaed;
  margin: 0;
  letter-spacing: -0.01em;
}

.card-desc {
  font-size: 0.88rem;
  color: #6b7280;
  margin: 0;
  line-height: 1.7;
  flex: 1;
}

.card-tags {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.card-tags li {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.68rem;
  letter-spacing: 0.05em;
  padding: 3px 8px;
  border-radius: 2px;
  background: #13171f;
  border: 1px solid #1e2530;
  color: #9ca3af;
}

.card-arrow {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.75rem;
  letter-spacing: 0.15em;
  opacity: 0;
  transform: translateX(-6px);
  transition: all 0.2s;
  margin-top: 8px;
}

.card--amber .card-arrow { color: #f59e0b; }
.card--cyan  .card-arrow { color: #06b6d4; }
.card--rose  .card-arrow { color: #f43f5e; }

.card:hover .card-arrow {
  opacity: 1;
  transform: translateX(0);
}

/* ── 底部状态栏 ── */
.statusbar {
  display: flex;
  gap: 32px;
  padding: 14px 48px;
  border-top: 1px solid #1e2530;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.7rem;
  letter-spacing: 0.08em;
  color: #374151;
  background: #0a0c0f;
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .cards {
    grid-template-columns: 1fr;
  }
  .navbar, .hero, .statusbar {
    padding-left: 24px;
    padding-right: 24px;
  }
  .statusbar {
    flex-direction: column;
    gap: 8px;
  }
}
</style>

