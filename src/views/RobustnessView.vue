<template>
  <div class="robustness-page">
    <div class="page-header">
      <div class="ph-bg"></div>
      <div class="ph-content">
        <span class="ph-eyebrow">ROBUSTNESS TEST</span>
        <h1 class="ph-title">鲁棒性测试</h1>
        <p class="ph-desc">选择评测模式，对无人机视觉模型进行对抗攻击测试</p>
      </div>
    </div>

    <section class="cards">
      <div
        v-for="card in cards" :key="card.route"
        class="card" :class="'card--' + card.color"
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

    <footer class="statusbar">
      <span>模型可用：ResNet18 · MobileNetV2 · EfficientNet-b0</span>
      <span>攻击方法：17 种</span>
      <span>数据集：drone_dataset</span>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

const cards = [
  {
    number: '01', icon: '◎', color: 'amber',
    title: '单模型 × 单攻击',
    desc: '选择一个模型，搭配任意一种攻击方法，快速获取鲁棒性分数，结果以体检单形式呈现。',
    tags: ['FGSM', 'PGD', 'CW', 'AutoAttack', '...共17种'],
    route: '/evaluate/one',
  },
  {
    number: '02', icon: '◈', color: 'cyan',
    title: '多攻击组合基准',
    desc: '固定模型，选择推荐攻击组合（快速 / 标准 / 迁移 / 强攻击），批量评测并排名。',
    tags: ['快速攻击', '标准鲁棒性', '迁移攻击', '强攻击'],
    route: '/evaluate/more',
  },
  {
    number: '03', icon: '⬡', color: 'rose',
    title: '自定义模型接入',
    desc: '上传你自己训练的 .pth 模型，选择攻击方式，自动生成完整评测报告。',
    tags: ['上传 .pth', '自选攻击', '自动报告'],
    route: '/evaluate/own',
  },
]
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=Noto+Sans+SC:wght@400;700&display=swap');

.robustness-page {
  min-height: calc(100vh - 58px);
  background: #0a0c0f;
  color: #d4d8de;
  font-family: 'Noto Sans SC', sans-serif;
  display: flex;
  flex-direction: column;
}

.page-header {
  position: relative;
  padding: 56px 48px 48px;
  overflow: hidden;
  border-bottom: 1px solid #9ca3af;
}

.ph-bg {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(#9ca3af 1px, transparent 1px),
    linear-gradient(90deg, #9ca3af 1px, transparent 1px);
  background-size: 48px 48px;
  opacity: 0.25;
}

.ph-content { position: relative; z-index: 1; }

.ph-eyebrow {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.65rem;
  letter-spacing: 0.25em;
  color: #f59e0b;
  display: block;
  margin-bottom: 10px;
}

.ph-title {
  font-size: 2.2rem;
  font-weight: 700;
  color: #f0f2f5;
  margin: 0 0 10px;
  letter-spacing: -0.02em;
}

.ph-desc { color: #d4d8de; font-size: 0.92rem; margin: 0; }

/* Cards — same logic as old HomeView */
.cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1px;
  background: #9ca3af;
  flex: 1;

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
  top: 0; left: 0; right: 0;
  height: 2px;
  opacity: 0;
  transition: opacity 0.2s;
}
.card--amber::before { background: #f59e0b; }
.card--cyan::before  { background: #06b6d4; }
.card--rose::before  { background: #f43f5e; }

.card:hover { background: #111520; }
.card:hover::before { opacity: 1; }
.card:hover .card-icon { transform: scale(1.1); }

.card-number { font-family: 'Share Tech Mono', monospace; font-size: 0.65rem; letter-spacing: 0.2em; color: #d4d8de; }
.card-icon { font-size: 2rem; line-height: 1; transition: transform 0.2s; }
.card--amber .card-icon { color: #f59e0b; }
.card--cyan  .card-icon { color: #06b6d4; }
.card--rose  .card-icon { color: #f43f5e; }

.card-title { font-size: 1.2rem; font-weight: 700; color: #e8eaed; margin: 0; }
.card-desc { font-size: 0.88rem; color: #d4d8de; margin: 0; line-height: 1.75; flex: 1; }

.card-tags { list-style: none; padding: 0; margin: 0; display: flex; flex-wrap: wrap; gap: 6px; }
.card-tags li {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.65rem;
  padding: 3px 8px;
  background: #13171f;
  border: 1px solid #9ca3af;
  color: #9ca3af;
}

.card-arrow {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.75rem;
  letter-spacing: 0.15em;
  opacity: 0;
  transform: translateX(-6px);
  transition: all 0.2s;
}
.card--amber .card-arrow { color: #f59e0b; }
.card--cyan  .card-arrow { color: #06b6d4; }
.card--rose  .card-arrow { color: #f43f5e; }
.card:hover .card-arrow { opacity: 1; transform: translateX(0); }

.statusbar {
  display: flex;
  gap: 32px;
  padding: 14px 48px;
  border-top: 1px solid #9ca3af;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.68rem;
  letter-spacing: 0.08em;
  color: #d4d8de;
}

@media (max-width: 768px) {
  .cards { grid-template-columns: 1fr; }
  .page-header, .statusbar { padding-left: 24px; padding-right: 24px; }
  .statusbar { flex-direction: column; gap: 8px; }
}
</style>