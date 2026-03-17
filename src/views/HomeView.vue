<template>
  <div class="home">

    <!-- ── Hero ── -->
    <section class="hero">

      <!-- 背景层 -->
      <div class="hero-bg">
        <div class="bg-grid"></div>

        <!-- 主雷达 -->
        <div class="radar">
          <svg class="radar-svg" viewBox="0 0 500 500" xmlns="http://www.w3.org/2000/svg">
            <!-- 同心圆 -->
            <circle cx="250" cy="250" r="60"  fill="none" stroke="rgba(245,158,11,0.12)" stroke-width="1"/>
            <circle cx="250" cy="250" r="110" fill="none" stroke="rgba(245,158,11,0.10)" stroke-width="1"/>
            <circle cx="250" cy="250" r="160" fill="none" stroke="rgba(245,158,11,0.08)" stroke-width="1"/>
            <circle cx="250" cy="250" r="210" fill="none" stroke="rgba(245,158,11,0.06)" stroke-width="1"/>
            <circle cx="250" cy="250" r="245" fill="none" stroke="rgba(245,158,11,0.04)" stroke-width="1"/>
            <!-- 十字线 -->
            <line x1="5"   y1="250" x2="495" y2="250" stroke="rgba(245,158,11,0.08)" stroke-width="0.8"/>
            <line x1="250" y1="5"   x2="250" y2="495" stroke="rgba(245,158,11,0.08)" stroke-width="0.8"/>
            <!-- 斜线 -->
            <line x1="77"  y1="77"  x2="423" y2="423" stroke="rgba(245,158,11,0.05)" stroke-width="0.6"/>
            <line x1="423" y1="77"  x2="77"  y2="423" stroke="rgba(245,158,11,0.05)" stroke-width="0.6"/>
            <!-- 扫描扇形 -->
            <g class="radar-sweep">
              <path d="M250,250 L250,40 A210,210 0 0,1 460,250 Z"
                fill="url(#sweep-grad)" opacity="0.5"/>
            </g>
            <defs>
              <radialGradient id="sweep-grad" cx="0%" cy="50%" r="100%">
                <stop offset="0%"   stop-color="#f59e0b" stop-opacity="0.0"/>
                <stop offset="70%"  stop-color="#f59e0b" stop-opacity="0.04"/>
                <stop offset="100%" stop-color="#f59e0b" stop-opacity="0.18"/>
              </radialGradient>
            </defs>
            <!-- 中心点 -->
            <circle cx="250" cy="250" r="5" fill="#f59e0b" opacity="0.8"/>
            <circle cx="250" cy="250" r="10" fill="none" stroke="#f59e0b" stroke-width="1" opacity="0.4"/>
            <!-- 目标点 -->
            <circle cx="320" cy="160" r="4" fill="#f59e0b" opacity="0.9" class="blip b1"/>
            <circle cx="320" cy="160" r="12" fill="none" stroke="#f59e0b" stroke-width="0.8" opacity="0.5" class="blip-ring r1"/>
            <circle cx="180" cy="300" r="3" fill="#06b6d4" opacity="0.8" class="blip b2"/>
            <circle cx="180" cy="300" r="10" fill="none" stroke="#06b6d4" stroke-width="0.8" opacity="0.5" class="blip-ring r2"/>
            <circle cx="350" cy="310" r="3" fill="#f43f5e" opacity="0.7" class="blip b3"/>
          </svg>
        </div>

        <!-- 脉冲扩散圆 -->
        <div class="pulse-rings">
          <div class="pulse-ring" style="animation-delay:0s"></div>
          <div class="pulse-ring" style="animation-delay:2s"></div>
          <div class="pulse-ring" style="animation-delay:4s"></div>
        </div>

        <!-- 漂浮六边形 -->
        <div class="hex-field">
          <span class="hex" v-for="h in hexes" :key="h.id"
            :style="{
              top: h.top, right: h.right, left: h.left,
              fontSize: h.size, animationDelay: h.delay,
              opacity: h.opacity
            }">⬡</span>
        </div>

        <!-- 数据流竖线 -->
        <div class="data-streams">
          <div class="stream" v-for="s in streams" :key="s.id"
            :style="{ left: s.left, animationDelay: s.delay, height: s.height }"></div>
        </div>

        <div class="scan-h"></div>
        <div class="vignette-left"></div>
        <div class="vignette-bottom"></div>
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
        <p class="sec-subtitle">广泛适用于军事、工业、城市等无人机视觉部署场景</p>
      </div>

      <div class="scenario-grid">
        <div v-for="s in scenarios" :key="s.title" class="sc-card">
          <div class="sc-top">
            <span class="sc-icon" :style="{ color: s.color }">{{ s.icon }}</span>
            <span class="sc-tag">{{ s.tag }}</span>
          </div>
          <h3 class="sc-title">{{ s.title }}</h3>
          <p class="sc-desc">{{ s.desc }}</p>
          <div class="sc-bar" :style="{ background: `linear-gradient(90deg, ${s.color}, transparent)` }"></div>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

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
  { icon: '◉', tag: 'DEFENSE',   color: '#f59e0b', title: '军事侦察',
    desc: '评估无人机视觉系统在对抗干扰下的目标识别鲁棒性，为实战部署提供量化安全依据。' },
  { icon: '◎', tag: 'LOGISTICS', color: '#06b6d4', title: '物流配送',
    desc: '验证低空飞行器在复杂光线与遮挡场景下的检测稳定性，降低配送失误风险。' },
  { icon: '◈', tag: 'SECURITY',  color: '#a78bfa', title: '边境巡逻',
    desc: '测试极端环境下抵抗对抗样本攻击的能力边界，强化安全系统的可靠性与健壮性。' },
  { icon: '⬡', tag: 'URBAN',     color: '#f43f5e', title: '城市监测',
    desc: '分析复杂城市背景下模型的误分类风险，为系统加固提供精确改进方向与量化报告。' },
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
    linear-gradient(rgba(30,37,48,0.6) 1px, transparent 1px),
    linear-gradient(90deg, rgba(30,37,48,0.6) 1px, transparent 1px);
  background-size: 56px 56px;
}

/* 雷达 */
.radar {
  position: absolute;
  right: 4%;
  top: 50%;
  transform: translateY(-50%);
  width: 520px;
  height: 520px;
}
.radar-svg { width: 100%; height: 100%; overflow: visible; }

.radar-sweep {
  transform-origin: 250px 250px;
  animation: sweep 4s linear infinite;
}
@keyframes sweep { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

.blip { animation: blip-pulse 2s ease-in-out infinite; }
.b1 { animation-delay: 0s; }
.b2 { animation-delay: 0.7s; }
.b3 { animation-delay: 1.4s; }
@keyframes blip-pulse { 0%,100%{opacity:0.3;r:3} 50%{opacity:1;r:5} }

.blip-ring { animation: ring-fade 2s ease-out infinite; }
.r1 { animation-delay: 0s; }
.r2 { animation-delay: 0.7s; }
@keyframes ring-fade {
  0%   { r: 8;  opacity: 0.8; }
  100% { r: 22; opacity: 0; }
}

/* 脉冲圆 */
.pulse-rings {
  position: absolute;
  right: calc(4% + 210px);
  top: 50%;
  transform: translate(50%, -50%);
  pointer-events: none;
}
.pulse-ring {
  position: absolute;
  width: 40px; height: 40px;
  border: 1px solid rgba(245,158,11,0.5);
  border-radius: 50%;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  animation: pulse-expand 6s ease-out infinite;
}
@keyframes pulse-expand {
  0%   { width:40px;  height:40px;  opacity:0.6; }
  100% { width:560px; height:560px; opacity:0;   }
}

/* 漂浮六边形 */
.hex-field { position: absolute; inset: 0; }
.hex {
  position: absolute;
  font-family: monospace;
  color: #f59e0b;
  animation: hex-float 14s ease-in-out infinite;
  pointer-events: none;
  line-height: 1;
}
@keyframes hex-float {
  0%,100% { transform: translateY(0) rotate(0deg); }
  33%     { transform: translateY(-24px) rotate(8deg); }
  66%     { transform: translateY(12px) rotate(-5deg); }
}

/* 数据流 */
.data-streams { position: absolute; top: 0; right: 0; left: 0; bottom: 0; overflow: hidden; }
.stream {
  position: absolute;
  top: -150px;
  width: 1px;
  background: linear-gradient(180deg, transparent 0%, rgba(245,158,11,0.4) 50%, transparent 100%);
  animation: stream-fall 5s linear infinite;
}
@keyframes stream-fall { from { top: -150px; } to { top: 110%; } }

/* 水平扫描线 */
.scan-h {
  position: absolute; left: 0; right: 0; height: 1px;
  background: linear-gradient(90deg, transparent 0%, rgba(245,158,11,0.15) 40%, rgba(245,158,11,0.3) 60%, transparent 100%);
  animation: scan-h 11s linear infinite;
}
@keyframes scan-h { 0%{top:0%} 100%{top:100%} }

.vignette-left {
  position: absolute; inset: 0;
  background: linear-gradient(90deg, #0a0c0f 25%, rgba(10,12,15,0.7) 50%, transparent 72%);
}
.vignette-bottom {
  position: absolute; bottom: 0; left: 0; right: 0; height: 160px;
  background: linear-gradient(0deg, #0a0c0f 0%, transparent 100%);
}

/* 文字内容 — 加宽 */
.hero-content {
  position: relative;
  z-index: 1;
  padding: 0 5% 0 8%;
  max-width: 740px; /* 更宽 */
  width: 100%;
}

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
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.sc-card {
  background: #0d1017;
  border: 1px solid #1e2530;
  border-radius: 4px;
  padding: 30px 26px 24px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  transition: border-color 0.25s, transform 0.25s;
  overflow: hidden;
}
.sc-card:hover { border-color: #374151; transform: translateY(-4px); }
.sc-card:hover .sc-bar { opacity: 1; }

.sc-top { display: flex; align-items: center; justify-content: space-between; }
.sc-icon { font-size: 2rem; line-height: 1; }
.sc-tag {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.56rem;
  letter-spacing: 0.14em;
  color: #374151;
  border: 1px solid #1e2530;
  padding: 3px 8px;
}
.sc-title { font-size: 1.05rem; font-weight: 700; color: #e8eaed; margin: 0; }
.sc-desc { font-size: 0.82rem; color: #6b7280; line-height: 1.75; margin: 0; flex: 1; }
.sc-bar {
  height: 2px;
  border-radius: 1px;
  opacity: 0;
  transition: opacity 0.3s;
}

/* 响应式 */
@media (max-width: 1200px) { .scenario-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 900px) {
  .feature-grid { grid-template-columns: 1fr; }
  .hero-content { max-width: 100%; padding: 40px 6%; }
  .radar { opacity: 0.25; right: -60px; }
  .section { padding: 72px 6%; }
}
@media (max-width: 600px) {
  .scenario-grid { grid-template-columns: 1fr; }
  .hero-metrics { flex-wrap: wrap; gap: 24px; }
  .metric { border-right: none; padding-right: 0; margin-right: 0; }
}
</style>