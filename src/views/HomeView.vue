<template>
  <div class="home">

    <!-- ── Hero ── -->
    <section class="hero">

      <!-- 全屏背景层 -->
      <div class="hero-bg">
        <!-- 轮播图占据整个背景 -->
        <div class="hero-carousel-full">
          <div class="carousel-track" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
            <div v-for="(img, idx) in carouselImages" :key="idx" class="slide">
              <img :src="img" alt="Drone View" />
              <!-- 核心：多重渐变叠加，实现从左下角文字区向背景图片的平滑过渡 -->
              <div class="slide-overlay-complex"></div>
            </div>
          </div>
          
          <!-- 轮播指示器 -->
          <div class="carousel-dots">
            <span v-for="(_, idx) in carouselImages" :key="idx" 
                  class="dot" :class="{ active: currentSlide === idx }"
                  @click="currentSlide = idx"></span>
          </div>
        </div>

        <div class="bg-grid"></div>
        <div class="scan-h"></div>
      </div>

      <!-- 文字内容 -->
      <div class="hero-content">
        <div class="hero-badge">
          <span class="badge-dot"></span>
          ADVERSARIAL ROBUSTNESS EVALUATION PLATFORM
        </div>

        <h1 class="hero-title">
          无人机视觉模型<br>
          <em>鲁棒性评测平台</em>
        </h1>

        <p class="hero-sub">
          一站式 AI 模型对抗攻击测评与可视化分析系统<br>
          支持 17 种攻击算法 · 3 大主流模型 · 全流程自动报告
        </p>

        <div class="hero-actions">
          <button class="cta-primary" @click="router.push('/robustness')">
            开始鲁棒性检测 <span>→</span>
          </button>
          <button class="cta-secondary" @click="router.push('/assistant')">
            智能助手
          </button>
        </div>

        <div class="hero-metrics">
          <div class="metric" v-for="m in metrics" :key="m.label">
            <span class="metric-num">{{ m.num }}</span>
            <span class="metric-label">{{ m.label }}</span>
          </div>
        </div>
      </div>

    </section>

    <!-- ── 核心功能介绍 ── -->
    <section class="section features-sec">
      <div class="sec-header">
        <div class="sec-eyebrow-row">
          <span class="sec-eyebrow">CORE FEATURES</span>
          <div class="sec-rule"></div>
        </div>
        <h2 class="sec-title">核心功能</h2>
        <p class="sec-subtitle">三大评测工作流，覆盖从快速验证到深度基准测试的全场景需求</p>
      </div>

      <div class="feature-grid">
        <div v-for="f in features" :key="f.num" class="fc" :class="'fc-' + f.accent">
          <div class="fc-head">
            <span class="fc-num">{{ f.num }}</span>
            <span class="fc-icon" :style="{ color: f.color }">{{ f.icon }}</span>
          </div>
          <h3 class="fc-title">{{ f.title }}</h3>
          <p class="fc-desc">{{ f.desc }}</p>
          <div class="fc-details">
            <div v-for="d in f.details" :key="d.label" class="fc-detail">
              <span class="fd-label">{{ d.label }}</span>
              <span class="fd-val">{{ d.val }}</span>
            </div>
          </div>
          <div class="fc-tags">
            <span v-for="t in f.tags" :key="t" :style="{ borderColor: f.color + '33', color: f.color }">
              {{ t }}
            </span>
          </div>
        </div>
      </div>
    </section>

    <!-- ── 应用场景 ── -->
    <section class="section scenarios-sec">
      <div class="sec-header">
        <div class="sec-eyebrow-row">
          <span class="sec-eyebrow">USE CASES</span>
          <div class="sec-rule"></div>
        </div>
        <h2 class="sec-title">应用场景</h2>
        <p class="sec-subtitle">从军事侦察到自动驾驶，本系统为多领域 AI 部署提供全方位安全护航</p>
      </div>

      <div class="scenario-grid">
        <div v-for="s in scenarios" :key="s.title" class="sc-card">
          <!-- 背景图片层 -->
          <div class="sc-card-bg" :style="{ backgroundImage: `url(${s.bg})` }"></div>
          <!-- 渐变遮罩层，确保文字可读 -->
          <div class="sc-card-overlay"></div>
          
          <div class="sc-content-wrap">
            <div class="sc-top">
              <span class="sc-icon" :style="{ color: s.color }">{{ s.icon }}</span>
              <span class="sc-tag">{{ s.tag }}</span>
            </div>
            
            <div class="sc-main">
              <h3 class="sc-title">{{ s.title }}</h3>
              <p class="sc-desc">{{ s.desc }}</p>
            </div>

            <div class="sc-info-grid">
              <div class="sc-info-box">
                <span class="sib-label">技术挑战</span>
                <ul class="sib-list">
                  <li v-for="c in s.challenges" :key="c">{{ c }}</li>
                </ul>
              </div>
              <div class="sc-info-box">
                <span class="sib-label">安全风险</span>
                <ul class="sib-list">
                  <li v-for="r in s.risks" :key="r">{{ r }}</li>
                </ul>
              </div>
            </div>

            <div class="sc-footer">
              <div class="sc-role">
                <span class="scr-label">系统作用：</span>
                <span class="scr-val">{{ s.role }}</span>
              </div>
              <div class="sc-value">
                <span class="scv-label">应用价值：</span>
                <span class="scv-val">{{ s.value }}</span>
              </div>
            </div>
          </div>

          <div class="sc-bar" :style="{ background: `linear-gradient(90deg, ${s.color}, transparent)` }"></div>
        </div>
      </div>
    </section>

    <!-- ── 总体价值总结 ── -->
    <section class="section summary-sec">
      <div class="summary-card">
        <div class="summary-bg-dots"></div>
        <div class="summary-content">
          <div class="summary-header">
            <h2 class="summary-title">核心意义</h2>
            <div class="summary-divider"></div>
          </div>
          <p class="summary-text">
            本系统面向多个实际应用场景，从<strong>单模型评测、多攻击分析到综合可视化与模型对比</strong>，构建了一套完整的鲁棒性评测体系。
          </p>
          <div class="summary-goals">
            <div class="goal-item">
              <span class="goal-icon">↑</span>
              <span class="goal-label">从“功能验证”到“安全评估”的升级</span>
            </div>
            <div class="goal-item">
              <span class="goal-icon">⇄</span>
              <span class="goal-label">从“单一测试”到“系统分析”的转变</span>
            </div>
            <div class="goal-item">
              <span class="goal-icon">↯</span>
              <span class="goal-label">从“模型效果”到“模型可靠性”的深入</span>
            </div>
          </div>
          <div class="summary-footer">
            让 AI 模型不仅“能用”，更<strong>“可靠、安全、可落地”</strong>
          </div>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted, onUnmounted } from 'vue'
const router = useRouter()

const carouselImages = [
  '/004.jpg',
  '/003.jpg',
  '/002.jpg',
  '/001.jpg'
]

const currentSlide = ref(0)
let timer = null

const startCarousel = () => {
  timer = setInterval(() => {
    currentSlide.value = (currentSlide.value + 1) % carouselImages.length
  }, 4000)
}

onMounted(() => {
  startCarousel()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const metrics = [
  { num: '3',   label: '可用模型' },
  { num: '17',  label: '攻击算法' },
  { num: '4',   label: '预设套餐' },
  { num: '∞',   label: '自定义场景' },
]

const hexes = [
  { id:1, top:'6%',  right:'2%',  size:'11rem', delay:'0s',   opacity:'0.06' },
  { id:2, top:'60%', right:'18%', size:'5rem',  delay:'2.2s', opacity:'0.05' },
  { id:3, top:'28%', right:'38%', size:'7rem',  delay:'4s',   opacity:'0.04' },
  { id:4, top:'14%', right:'28%', size:'3.5rem',delay:'1.1s', opacity:'0.05' },
  { id:5, top:'75%', right:'5%',  size:'4rem',  delay:'3s',   opacity:'0.04' },
  { id:6, top:'42%', right:'10%', size:'2.5rem',delay:'0.5s', opacity:'0.05' },
]

const streams = [
  { id:1, left:'62%', delay:'0s',   height:'120px' },
  { id:2, left:'70%', delay:'1.2s', height:'80px' },
  { id:3, left:'78%', delay:'0.6s', height:'150px' },
  { id:4, left:'85%', delay:'2s',   height:'100px' },
  { id:5, left:'91%', delay:'0.3s', height:'60px' },
]

const features = [
  {
    num: '01', icon: '◎', accent: 'amber', color: '#f59e0b',
    title: '单模型 × 单攻击',
    desc: '自由选择三大主流模型中的任意一个，搭配 17 种攻击算法之一，快速完成单次精准评测。结果以「体检单」形式呈现，清晰标注鲁棒等级与准确率变化。',
    details: [
      { label: '可选模型', val: 'ResNet18 · MobileNetV2 · EfficientNet-b0' },
      { label: '攻击方法', val: 'FGSM · PGD · CW · AutoAttack 等 17 种' },
      { label: '输出结果', val: '鲁棒评分 + 等级 + 曲线图 + 对比图' },
    ],
    tags: ['快速评测', '单次攻击', '体检报告'],
  },
  {
    num: '02', icon: '◈', accent: 'cyan', color: '#06b6d4',
    title: '多攻击组合基准',
    desc: '选定模型后，从四种预设攻击套餐中选择一种，平台自动运行组合内全部攻击算法，并按攻击成功率自动排名，输出综合鲁棒性基准报告。',
    details: [
      { label: '快速攻击', val: 'FGSM · RFGSM · FFGSM' },
      { label: '标准鲁棒性', val: 'BIM · PGD · PGDL2' },
      { label: '迁移 / 强攻击', val: 'MIFGSM · NIFGSM 等 / CW · AutoAttack 等' },
    ],
    tags: ['批量评测', '自动排名', '综合报告'],
  },
  {
    num: '03', icon: '⬡', accent: 'rose', color: '#f43f5e',
    title: '自定义模型接入',
    desc: '上传自己训练的 .pth 权重文件，选择攻击方式与参数，平台全自动完成推理评测，生成可下载的完整评测报告，无需任何额外配置。',
    details: [
      { label: '支持格式', val: 'PyTorch .pth 权重文件' },
      { label: '数据集', val: 'drone_dataset（无人机/鸟/飞机/直升机）' },
      { label: '输出内容', val: 'JSON报告 + 柱状图 + 鲁棒曲线' },
    ],
    tags: ['自定义上传', '全流程自动', '可下载报告'],
  },
]

const scenarios = [
  { 
    icon: '◉', tag: 'MILITARY', color: '#f59e0b', title: '军事侦察',
    bg: '/005.jpg',
    desc: '承担前沿侦察、战场监视等关键任务，深度依赖视觉模型识别目标。',
    challenges: ['光照剧烈变化', '目标伪装与欺骗', '毫秒级实时响应'],
    risks: ['对抗攻击导致识别错误', '微小扰动引发模型误判'],
    role: '系统性评估模型在极端对抗环境下的稳定性与弱点。',
    value: '为军事系统提供安全性验证，支撑高可靠 AI 部署。'
  },
  { 
    icon: '◎', tag: 'LOGISTICS', color: '#06b6d4', title: '物流配送',
    bg: '/006.jpg',
    desc: '在城市末端物流中，依靠视觉完成路径识别、投递点定位等。',
    challenges: ['高楼遮挡与信号波动', '动态行人/车辆干扰', '降落点识别高度依赖'],
    risks: ['对抗扰动导致路径规划错误', '投递点识别失败'],
    role: '分析模型在不同攻击强度下的路径识别与避障能力。',
    value: '提高配送任务成功率，降低复杂环境下的飞行风险。'
  },
  { 
    icon: '◈', tag: 'AUTONOMOUS', color: '#a78bfa', title: '车载自动驾驶',
    bg: '/007.jpg',
    desc: '核心的安全关键系统，处理行人识别、交通标志及障碍物检测。',
    challenges: ['全天候多场景适应', '极高精度与实时性', '长尾极端工况覆盖'],
    risks: ['交通标志识别错误', '行人检测失败导致安全事故'],
    role: '系统测试模型在对抗条件下的稳定性及对决策的影响。',
    value: '提升系统可靠性，为模型优化与安全认证提供量化依据。'
  },
  { 
    icon: '⬡', tag: 'SECURITY', color: '#f43f5e', title: '园区安防',
    bg: '/008.jpg',
    desc: '应用于智慧园区监控与边境巡逻，实现实时监控与异常行为分析。',
    challenges: ['长时间稳定运行', '复杂背景下的异常识别', '昼夜光影剧烈变化'],
    risks: ['恶意者利用对抗样本绕过检测', '误判导致漏检/误报'],
    role: '检测模型在对抗干扰下的稳定性，评估系统真实安全性。',
    value: '增强抗干扰能力，降低漏报风险，提升监控系统可信度。'
  },
]
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=Noto+Sans+SC:wght@400;700&display=swap');

.home {
  background: #0a0c0f;
  color: #d4d8de;
  font-family: 'Noto Sans SC', sans-serif;
}

/* ══════════════════ HERO ══════════════════ */
.hero {
  position: relative;
  min-height: calc(100vh - 64px);
  display: flex;
  align-items: center;
  overflow: hidden;
}

.hero-bg { position: absolute; inset: 0; pointer-events: none; }

.bg-grid {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(rgba(30,37,48,0.3) 1px, transparent 1px),
    linear-gradient(90deg, rgba(30,37,48,0.3) 1px, transparent 1px);
  background-size: 56px 56px;
  pointer-events: none;
  z-index: 0; /* ⭐ 核心：移到最底层，不再挡住图片 */
}

/* 全屏轮播图样式 */
.hero-carousel-full {
  position: absolute;
  inset: 0;
  z-index: 1; /* ⭐ 核心：位于网格之上 */
}

.carousel-track {
  display: flex;
  width: 100%;
  height: 100%;
  transition: transform 1.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide {
  min-width: 100%;
  height: 100%;
  position: relative;
}

.slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 核心：多重渐变叠加，实现从左下角文字区向背景图片的平滑过渡 */
.slide-overlay-complex {
  position: absolute;
  inset: 0;
  background: 
    /* 1. 左侧深向右渐隐 (保护标题文字) */
    linear-gradient(90deg, #0a0c0f 15%, rgba(10,12,15,0.8) 35%, transparent 60%),
    /* 2. 底部向上渐隐 (保护左下角指标和页脚融合) */
    linear-gradient(0deg, #0a0c0f 10%, rgba(10,12,15,0.5) 30%, transparent 50%),
    /* 3. 左下角核心深色遮罩 (强化文字背景) */
    radial-gradient(circle at 0% 100%, rgba(10,12,15,0.9) 0%, transparent 65%);
}

.carousel-dots {
  position: absolute;
  bottom: 40px;
  right: 50px;
  display: flex;
  gap: 12px;
  z-index: 10;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255,255,255,0.2);
  cursor: pointer;
  transition: all 0.3s;
}

.dot.active {
  background: #f59e0b;
  transform: scale(1.2);
  box-shadow: 0 0 15px rgba(245,158,11,0.6);
}

/* 水平扫描线 */
.scan-h {
  position: absolute; left: 0; right: 0; height: 1px;
  background: linear-gradient(90deg, transparent 0%, rgba(245,158,11,0.1) 40%, rgba(245,158,11,0.2) 60%, transparent 100%);
  animation: scan-h 11s linear infinite;
  z-index: 4;
}
@keyframes scan-h { 0%{top:0%} 100%{top:100%} }

/* 文字内容 - 调整层级与间距 */
.hero-content {
  position: relative;
  z-index: 5;
  padding: 0 5% 0 8%;
  max-width: 800px;
  width: 100%;
  pointer-events: none; /* 穿透文字点击点点 */
}
.hero-content * { pointer-events: auto; } /* 恢复交互 */

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.6rem;
  letter-spacing: 0.2em;
  color: #f59e0b;
  border: 1px solid rgba(245,158,11,0.2);
  background: rgba(245,158,11,0.04);
  padding: 5px 14px;
  margin-bottom: 28px;
}
.badge-dot {
  width: 6px; height: 6px;
  background: #f59e0b;
  border-radius: 50%;
  animation: dot-blink 1.5s ease-in-out infinite;
}
@keyframes dot-blink { 0%,100%{opacity:1} 50%{opacity:0.2} }

.hero-title {
  font-size: clamp(2.6rem, 5vw, 4.2rem); /* 更大 */
  font-weight: 700;
  color: #f0f2f5;
  line-height: 1.18;
  margin: 0 0 24px;
  letter-spacing: -0.025em;
}
.hero-title em { font-style: normal; color: #f59e0b; }

.hero-sub {
  font-size: 1.05rem;
  color: #6b7280;
  margin: 0 0 44px;
  line-height: 1.8;
}

.hero-actions { display: flex; gap: 14px; margin-bottom: 56px; flex-wrap: wrap; }

.cta-primary {
  display: inline-flex;
  align-items: center;
  gap: 14px;
  background: #f59e0b;
  color: #0a0c0f;
  border: none;
  padding: 16px 44px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.9rem;
  letter-spacing: 0.1em;
  font-weight: 700;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.25s;
}
.cta-primary span { transition: transform 0.2s; }
.cta-primary:hover { background: #fbbf24; }
.cta-primary:hover span { transform: translateX(6px); }

.cta-secondary {
  display: inline-flex;
  align-items: center;
  background: none;
  color: #9ca3af;
  border: 1px solid #374151;
  padding: 16px 36px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.88rem;
  letter-spacing: 0.1em;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.2s;
}
.cta-secondary:hover { border-color: #f59e0b; color: #f59e0b; }

/* 指标 */
.hero-metrics { display: flex; gap: 0; }
.metric {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding-right: 36px;
  margin-right: 36px;
  border-right: 1px solid #1e2530;
}
.metric:last-child { border-right: none; padding-right: 0; margin-right: 0; }
.metric-num {
  font-family: 'Share Tech Mono', monospace;
  font-size: 2.4rem;
  color: #f59e0b;
  line-height: 1;
}
.metric-label { font-size: 0.68rem; color: #374151; letter-spacing: 0.12em; }

/* ══════════════════ SECTIONS ══════════════════ */
.section { padding: 100px 8%; }

.sec-header { margin-bottom: 56px; }
.sec-eyebrow-row { display: flex; align-items: center; gap: 16px; margin-bottom: 10px; }
.sec-eyebrow {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.6rem;
  letter-spacing: 0.28em;
  color: #374151;
  white-space: nowrap;
}
.sec-rule { width: 56px; height: 1px; background: #1e2530; }
.sec-title {
  font-size: 2rem;
  font-weight: 700;
  color: #f0f2f5;
  margin: 0 0 10px;
  letter-spacing: -0.02em;
}
.sec-subtitle { font-size: 0.9rem; color: #6b7280; margin: 0; line-height: 1.7; }

/* 功能卡片 — 纯介绍不可点击 */
.feature-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1px;
  background: #1e2530;
  border: 1px solid #1e2530;
  border-radius: 4px;
  overflow: hidden;
}

.fc {
  background: #0d1017;
  padding: 44px 40px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: relative;
}
.fc::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 2px;
}
.fc-amber::before { background: #f59e0b; }
.fc-cyan::before  { background: #06b6d4; }
.fc-rose::before  { background: #f43f5e; }

.fc-head { display: flex; align-items: center; justify-content: space-between; }
.fc-num {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.62rem;
  letter-spacing: 0.2em;
  color: #374151;
}
.fc-icon { font-size: 2.2rem; }

.fc-title { font-size: 1.15rem; font-weight: 700; color: #e8eaed; margin: 0; }
.fc-desc { font-size: 0.86rem; color: #6b7280; line-height: 1.8; margin: 0; }

.fc-details {
  display: flex;
  flex-direction: column;
  gap: 0;
  border: 1px solid #1e2530;
  border-radius: 2px;
  overflow: hidden;
}
.fc-detail {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  padding: 9px 14px;
  border-bottom: 1px solid #1e2530;
  font-size: 0.78rem;
}
.fc-detail:last-child { border-bottom: none; }
.fd-label {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.65rem;
  letter-spacing: 0.08em;
  color: #374151;
  white-space: nowrap;
  padding-top: 1px;
  min-width: 60px;
}
.fd-val { color: #9ca3af; line-height: 1.5; }

.fc-tags { display: flex; flex-wrap: wrap; gap: 6px; }
.fc-tags span {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.62rem;
  padding: 3px 10px;
  background: rgba(255,255,255,0.02);
  border: 1px solid;
  border-radius: 2px;
}

/* 场景 */
.scenarios-sec { background: #080a0d; border-top: 1px solid #1e2530; }

.scenario-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 改为 2 列 */
  gap: 24px;
}

.sc-card {
  background: #0d1017;
  border: 1px solid #1e2530;
  border-radius: 4px;
  padding: 32px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  min-height: 480px;
}

.sc-card-bg {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  transition: transform 0.5s ease;
  z-index: 0;
}

.sc-card-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(13, 16, 23, 0.7) 0%, rgba(13, 16, 23, 0.95) 100%);
  z-index: 1;
  transition: background 0.3s ease;
}

.sc-content-wrap {
  position: relative;
  z-index: 2;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.sc-card:hover .sc-card-bg {
  transform: scale(1.1);
}

.sc-card:hover .sc-card-overlay {
  background: linear-gradient(180deg, rgba(13, 16, 23, 0.5) 0%, rgba(13, 16, 23, 0.9) 100%);
}

.sc-card:hover { 
  border-color: #374151; 
  transform: translateY(-6px);
  box-shadow: 0 12px 32px -12px rgba(0,0,0,0.5);
}
.sc-card:hover .sc-bar { opacity: 1; height: 3px; }

.sc-top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 4px; }
.sc-icon { font-size: 2.4rem; line-height: 1; }
.sc-tag {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.6rem;
  letter-spacing: 0.14em;
  color: #4b5563;
  border: 1px solid #1e2530;
  padding: 4px 10px;
  background: rgba(255,255,255,0.02);
}

.sc-main { display: flex; flex-direction: column; gap: 8px; }
.sc-title { font-size: 1.3rem; font-weight: 700; color: #f0f2f5; margin: 0; }
.sc-desc { font-size: 0.9rem; color: #9ca3af; line-height: 1.6; margin: 0; }

.sc-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  padding: 20px 0;
  border-top: 1px solid #1e2530;
  border-bottom: 1px solid #1e2530;
}
.sc-info-box { display: flex; flex-direction: column; gap: 10px; }
.sib-label {
  font-size: 0.7rem;
  color: #4b5563;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}
.sib-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.sib-list li {
  font-size: 0.8rem;
  color: #6b7280;
  padding-left: 12px;
  position: relative;
}
.sib-list li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 7px;
  width: 4px;
  height: 4px;
  background: #374151;
  border-radius: 50%;
}

.sc-footer { display: flex; flex-direction: column; gap: 12px; }
.sc-role, .sc-value { font-size: 0.82rem; line-height: 1.5; }
.scr-label, .scv-label { color: #4b5563; font-weight: 700; }
.scr-val, .scv-val { color: #8a8f98; }

.sc-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  opacity: 0.4;
  transition: all 0.3s;
}

/* ══════════════════ SUMMARY ══════════════════ */
.summary-sec {
  padding-bottom: 140px;
  background: #080a0d;
}
.summary-card {
  background: linear-gradient(145deg, #0d1017, #0a0c0f);
  border: 1px solid #1e2530;
  border-radius: 4px;
  padding: 64px;
  position: relative;
  overflow: hidden;
  text-align: center;
}
.summary-bg-dots {
  position: absolute; inset: 0;
  background-image: radial-gradient(#1e2530 1px, transparent 1px);
  background-size: 24px 24px;
  opacity: 0.3;
}
.summary-content { position: relative; z-index: 1; max-width: 800px; margin: 0 auto; }

.summary-header { margin-bottom: 32px; }
.summary-title {
  font-size: 1.8rem;
  color: #f0f2f5;
  margin-bottom: 16px;
  letter-spacing: 0.1em;
}
.summary-divider {
  width: 60px; height: 2px; background: #f59e0b; margin: 0 auto;
}

.summary-text {
  font-size: 1.1rem;
  color: #9ca3af;
  line-height: 1.8;
  margin-bottom: 48px;
}
.summary-text strong { color: #f0f2f5; }

.summary-goals {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 56px;
}
.goal-item {
  background: rgba(255,255,255,0.02);
  border: 1px solid #1e2530;
  padding: 24px 16px;
  border-radius: 2px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
}
.goal-icon { font-size: 1.5rem; color: #f59e0b; }
.goal-label { font-size: 0.85rem; color: #6b7280; line-height: 1.5; }

.summary-footer {
  font-size: 1.2rem;
  color: #6b7280;
}
.summary-footer strong { color: #f59e0b; font-weight: 700; margin-left: 8px; }

/* 响应式 */
@media (max-width: 1200px) { 
  .scenario-grid { grid-template-columns: 1fr; }
  .summary-goals { grid-template-columns: 1fr; }
}
@media (max-width: 900px) {
  .feature-grid { grid-template-columns: 1fr; }
  .hero-content { max-width: 100%; padding: 40px 6%; }
  .radar { opacity: 0.25; right: -60px; }
  .section { padding: 72px 6%; }
  .summary-card { padding: 40px 24px; }
}
@media (max-width: 600px) {
  .hero-metrics { flex-wrap: wrap; gap: 24px; }
  .metric { border-right: none; padding-right: 0; margin-right: 0; }
  .sc-info-grid { grid-template-columns: 1fr; }
}
</style>