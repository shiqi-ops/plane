<template>
  <div class="page">
   

    <div class="content">
      <div class="page-header">
        <span class="page-num">01</span>
        <h1 class="page-title">单模型 × 单攻击</h1>
        <p class="page-desc">选择一个模型，搭配任意一种攻击方法，快速获取鲁棒性分数与可视化对比图</p>
      </div>

      <div class="form-sections">

        <!-- 选模型 -->
        <div class="form-section">
          <h2 class="section-title">选择模型</h2>
          <div class="radio-group">
            <label
              v-for="m in models"
              :key="m.value"
              class="radio-card"
              :class="{ active: form.model === m.value }"
            >
              <input type="radio" v-model="form.model" :value="m.value" hidden />
              <span class="radio-name">{{ m.label }}</span>
              <span class="radio-tag">{{ m.tag }}</span>
            </label>
          </div>
        </div>

        <!-- 选攻击方式 -->
        <div class="form-section">
          <h2 class="section-title">选择攻击方式</h2>
          <div class="attack-grid">
            <div v-for="group in attackGroups" :key="group.label" class="attack-group">
              <p class="group-label">{{ group.label }}</p>
              <div class="attack-options">
                <label
                  v-for="atk in group.attacks"
                  :key="atk"
                  class="atk-chip"
                  :class="{ active: form.attack === atk }"
                >
                  <input type="radio" v-model="form.attack" :value="atk" hidden />
                  {{ atk }}
                </label>
              </div>
            </div>
          </div>
        </div>

        <!-- eps -->
        <div class="form-section">
          <h2 class="section-title">Eps 参数</h2>
          <div class="eps-row">
            <input
              v-model.number="form.eps"
              type="number"
              step="0.01"
              min="0.01"
              max="0.1"
              class="eps-input"
            />
            <span class="eps-hint">推荐值：0.03</span>
          </div>
        </div>
      </div>

      <button class="submit-btn" :disabled="!canSubmit || loading" @click="handleSubmit">
        <span v-if="loading" class="loading-dot"></span>
        {{ loading ? '评测中...' : '开始评测' }}
      </button>

      <!-- 结果展示 -->
<!-- 体检单结果 -->
<div v-if="result" class="report-card">

  <!-- 报告头 -->
  <div class="report-header">
    <div class="report-header-left">
      <div class="report-logo">⬡</div>
      <div>
        <div class="report-title">模型鲁棒性评测报告</div>
        <div class="report-subtitle">DRONE EVAL · MODEL ROBUSTNESS REPORT</div>
      </div>
    </div>
    <div class="report-header-right">
      <div class="report-id">NO. {{ reportId }}</div>
      <div class="report-date">{{ reportDate }}</div>
    </div>
  </div>

  <!-- 基本信息栏 -->
  <div class="report-info-bar">
    <div class="info-item">
      <span class="info-label">受测模型</span>
      <span class="info-value">{{ result.model }}</span>
    </div>
    <div class="info-divider"></div>
    <div class="info-item">
      <span class="info-label">攻击方法</span>
      <span class="info-value">{{ result.attack }}</span>
    </div>
    <div class="info-divider"></div>
    <div class="info-item">
      <span class="info-label">数据集</span>
      <span class="info-value">{{ result.dataset }}</span>
    </div>
    <div class="info-divider"></div>
    <div class="info-item">
      <span class="info-label">扰动强度 ε</span>
      <span class="info-value">{{ result.eps }}</span>
    </div>
  </div>

  <!-- 综合评级 -->
  <div class="report-grade-section">
    <div class="grade-left">
      <div class="grade-label">综合鲁棒等级</div>
      <div class="grade-value" :class="'grade-' + result.robust_level">
        {{ result.robust_level }}
      </div>
      <div class="grade-desc">{{ gradeDesc(result.robust_level) }}</div>
    </div>
    <div class="grade-right">
      <div class="grade-score-label">鲁棒评分</div>
      <div class="grade-score">{{ result.robust_score?.toFixed(4) }}</div>
      <div class="grade-score-bar-wrap">
        <div class="grade-score-bar"
          :style="{ width: Math.min(result.robust_score * 100, 100) + '%' }"
          :class="'bar-' + result.robust_level">
        </div>
      </div>
    </div>
  </div>

  <!-- 检测项目表 -->
  <div class="report-section-title">
    <span>▌</span> 检测项目明细
  </div>

  <div class="report-table">
    <div class="rt-head">
      <span>检测项目</span>
      <span>检测值</span>
      <span>参考范围</span>
      <span>状态</span>
    </div>

    <div class="rt-row">
      <span class="rt-name">清洁准确率 <em>Clean Accuracy</em></span>
      <span class="rt-val">{{ (result.clean_accuracy * 100).toFixed(2) }}%</span>
      <span class="rt-ref">≥ 70%</span>
      <span class="rt-status" :class="result.clean_accuracy >= 0.7 ? 'status-ok' : 'status-warn'">
        {{ result.clean_accuracy >= 0.7 ? '正常' : '偏低' }}
      </span>
    </div>

    <div class="rt-row">
      <span class="rt-name">对抗准确率 <em>Adv Accuracy</em></span>
      <span class="rt-val warn">{{ (result.adv_accuracy * 100).toFixed(2) }}%</span>
      <span class="rt-ref">≥ 50%</span>
      <span class="rt-status" :class="result.adv_accuracy >= 0.5 ? 'status-ok' : 'status-danger'">
        {{ result.adv_accuracy >= 0.5 ? '正常' : '异常' }}
      </span>
    </div>

    <div class="rt-row">
      <span class="rt-name">准确率下降幅度 <em>Accuracy Drop</em></span>
      <span class="rt-val danger">{{ (result.accuracy_drop * 100).toFixed(2) }}%</span>
      <span class="rt-ref">≤ 20%</span>
      <span class="rt-status" :class="result.accuracy_drop <= 0.2 ? 'status-ok' : 'status-danger'">
        {{ result.accuracy_drop <= 0.2 ? '正常' : '异常' }}
      </span>
    </div>

    <div class="rt-row">
      <span class="rt-name">鲁棒评分 <em>Robust Score</em></span>
      <span class="rt-val">{{ result.robust_score?.toFixed(4) }}</span>
      <span class="rt-ref">≥ 0.5</span>
      <span class="rt-status" :class="result.robust_score >= 0.5 ? 'status-ok' : 'status-warn'">
        {{ result.robust_score >= 0.5 ? '合格' : '不合格' }}
      </span>
    </div>
  </div>

  <!-- 可视化指标条 -->
  <div class="report-section-title">
    <span>▌</span> 指标可视化
  </div>

  <div class="report-bars">
    <div class="bar-item">
      <div class="bar-item-head">
        <span class="bar-name">清洁准确率</span>
        <span class="bar-pct">{{ (result.clean_accuracy * 100).toFixed(1) }}%</span>
      </div>
      <div class="bar-track">
        <div class="bar-fill fill-green" :style="{ width: (result.clean_accuracy * 100) + '%' }"></div>
        <div class="bar-threshold" style="left: 70%"></div>
      </div>
    </div>

    <div class="bar-item">
      <div class="bar-item-head">
        <span class="bar-name">对抗准确率</span>
        <span class="bar-pct warn">{{ (result.adv_accuracy * 100).toFixed(1) }}%</span>
      </div>
      <div class="bar-track">
        <div class="bar-fill fill-amber" :style="{ width: (result.adv_accuracy * 100) + '%' }"></div>
        <div class="bar-threshold" style="left: 50%"></div>
      </div>
    </div>

    <div class="bar-item">
      <div class="bar-item-head">
        <span class="bar-name">准确率下降</span>
        <span class="bar-pct danger">{{ (result.accuracy_drop * 100).toFixed(1) }}%</span>
      </div>
      <div class="bar-track">
        <div class="bar-fill fill-red" :style="{ width: (result.accuracy_drop * 100) + '%' }"></div>
        <div class="bar-threshold" style="left: 20%"></div>
      </div>
    </div>
  </div>

  <!-- 图片区 -->
  <template v-if="result.curve_path || result.compare_path">
    <div class="report-section-title"><span>▌</span> 可视化图表</div>
    <div class="report-images">
      <div class="img-block" v-if="result.curve_path">
        <div class="img-label">鲁棒性曲线 Robustness Curve</div>
        <div class="img-wrap">
          <img :src="imgUrl(result.curve_path)" alt="鲁棒性曲线" class="result-img" />
        </div>
      </div>
      <div class="img-block" v-if="result.compare_path">
        <div class="img-label">原始 vs 对抗样本</div>
        <div class="img-wrap">
          <img :src="imgUrl(result.compare_path)" alt="对比图" class="result-img" />
        </div>
      </div>
    </div>
  </template>

  <!-- 报告结论 -->
  <div class="report-conclusion">
    <div class="conclusion-icon" :class="'grade-' + result.robust_level">
      {{ conclusionIcon(result.robust_level) }}
    </div>
    <div class="conclusion-text">
      <div class="conclusion-title">评测结论</div>
      <div class="conclusion-body">{{ conclusionText(result) }}</div>
    </div>
  </div>

  <!-- 报告底部 -->
  <div class="report-footer">
    <span>DRONE EVAL 无人机鲁棒性评测平台</span>
    <span>本报告由系统自动生成，仅供参考</span>
    <span>{{ reportDate }}</span>
  </div>

</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/index.js'

const router = useRouter()

const models = [
  { label: 'ResNet18', value: 'resnet18', tag: 'baseline' },
  { label: 'MobileNetV2', value: 'mobilenetv2', tag: '轻量' },
  { label: 'EfficientNet-b0', value: 'efficientnet', tag: '高精度' },
]

const attackGroups = [
  { label: '快速攻击', attacks: ['FGSM', 'RFGSM', 'FFGSM'] },
  { label: '迭代攻击', attacks: ['BIM', 'PGD', 'PGDL2'] },
  { label: '动量攻击', attacks: ['MIFGSM', 'NIFGSM'] },
  { label: '输入变换攻击', attacks: ['DIFGSM', 'TIFGSM'] },
  { label: '优化攻击', attacks: ['CW', 'DeepFool'] },
  { label: '黑盒攻击', attacks: ['Square Attack'] },
  { label: '综合攻击', attacks: ['AutoAttack'] },
]

const reportId = computed(() => 'RPT' + Date.now().toString().slice(-8))
const reportDate = computed(() => new Date().toLocaleString('zh-CN'))
const form = ref({ model: '', attack: '', eps: 0.03 })
const loading = ref(false)
// const result = ref(null)
const result = ref({
  model: 'resnet18',
  dataset: 'drone_dataset',
  attack: 'FGSM',
  eps: 0.03,
  clean_accuracy: 0.7068,
  adv_accuracy: 0.2308,
  accuracy_drop: 0.4760,
  robust_score: 0.3265,
  robust_level: 'D',
  curve_path: null,    // 暂时没图
  compare_path: null,
})
function gradeDesc(level) {
  const map = {
    'A': '模型鲁棒性优秀，具备较强对抗攻击能力',
    'B': '模型鲁棒性良好，对常见攻击有一定抵御能力',
    'C': '模型鲁棒性一般，建议进行对抗训练加固',
    'D': '模型鲁棒性较差，面对攻击时性能严重下降',
    'Poor': '模型鲁棒性极差，不建议在安全敏感场景部署',
  }
  return map[level] || '暂无评级说明'
}

function conclusionIcon(level) {
  const map = { 'A': '✓', 'B': '✓', 'C': '⚠', 'D': '✗', 'Poor': '✗' }
  return map[level] || '—'
}

function conclusionText(r) {
  const drop = (r.accuracy_drop * 100).toFixed(1)
  const adv = (r.adv_accuracy * 100).toFixed(1)
  return `模型 ${r.model} 在 ${r.attack} 攻击（ε=${r.eps}）下，准确率从 ${(r.clean_accuracy * 100).toFixed(1)}% 下降至 ${adv}%，降幅达 ${drop}%，鲁棒等级为 ${r.robust_level}。${gradeDesc(r.robust_level)}。`
}

function imgUrl(path) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  const filename = path.split('/').pop()
  return `${import.meta.env.VITE_API_BASE}/results/${filename}`
}
const canSubmit = computed(() => form.value.model && form.value.attack)
function saveHistory(entry) {
  const list = JSON.parse(localStorage.getItem('evalHistory') || '[]')
  list.push({
    id: Date.now(),
    date: new Date().toLocaleDateString('zh-CN'),
    ...entry,
  })
  localStorage.setItem('evalHistory', JSON.stringify(list))
}
async function handleSubmit() {
  loading.value = true
  result.value = null
  try {
    const payload = {
      model: form.value.model,
      attack: form.value.attack,
      dataset: 'drone_dataset',
      eps: String(form.value.eps),
    }
    const res = await api.post('/evaluate/one', payload)
    result.value = res.data
    saveHistory({ type: 'one', model: form.value.model, attack: form.value.attack, result: res.data })
  } catch (e) {
    alert('评测失败，请检查后端连接')
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
  color: #d4d8de;
  font-family: 'Noto Sans SC', sans-serif;
}

/* ── 导航栏 ── */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 48px;
  border-bottom: 1px solid #9ca3af;
  position: sticky;
  top: 0;
  background: #0a0c0f;
  z-index: 10;
}
.navbar-brand {
  font-family: 'Share Tech Mono', monospace;
  font-size: 1.1rem;
  letter-spacing: 0.15em;
  color: #e8eaed;
  display: flex;
  align-items: center;
  gap: 8px;
}
.brand-icon { color: #f59e0b; font-size: 1.4rem; }
.brand-name em { font-style: normal; color: #f59e0b; }

.back-btn {
  background: none;
  border: 1px solid #d4d8de;
  color: #9ca3af;
  padding: 5px 14px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.78rem;
  letter-spacing: 0.08em;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.2s;
}
.back-btn:hover { border-color: #f59e0b; color: #f59e0b; }

/* ── 内容区 ── */
.content {
  max-width: 900px;
  margin: 0 auto;
  padding: 56px 48px;
}

.page-header { margin-bottom: 48px; }
.page-num {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.7rem;
  letter-spacing: 0.2em;
  color: #d4d8de;
}
.page-title { font-size: 2rem; font-weight: 700; color: #f0f2f5; margin: 8px 0; }
.page-desc { color: #d4d8de; font-size: 0.9rem; }

.form-sections { display: flex; flex-direction: column; gap: 40px; }

.section-title {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.75rem;
  letter-spacing: 0.15em;
  color: #f59e0b;
  text-transform: uppercase;
  margin-bottom: 16px;
}

/* ── 模型选择 ── */
.radio-group { display: flex; flex-direction: column; gap: 8px; }

.radio-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 20px;
  border: 1px solid #9ca3af;
  border-radius: 4px;
  background: #0d1017;
  cursor: pointer;
  transition: all 0.2s;
}
.radio-card:hover { border-color: #d4d8de; }
.radio-card.active { border-color: #f59e0b; background: #151009; }

.radio-name { font-size: 0.95rem; color: #e8eaed; flex: 1; }
.radio-tag {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.68rem;
  color: #d4d8de;
  background: #13171f;
  border: 1px solid #9ca3af;
  padding: 2px 8px;
  border-radius: 2px;
}

/* ── 攻击选择 ── */
.attack-grid { display: flex; flex-direction: column; gap: 20px; }

.group-label {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.68rem;
  letter-spacing: 0.12em;
  color: #d4d8de;
  text-transform: uppercase;
  margin-bottom: 8px;
}
.attack-options { display: flex; flex-wrap: wrap; gap: 8px; }

.atk-chip {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.78rem;
  padding: 6px 14px;
  border: 1px solid #9ca3af;
  border-radius: 2px;
  background: #0d1017;
  color: #9ca3af;
  cursor: pointer;
  transition: all 0.15s;
  letter-spacing: 0.05em;
}
.atk-chip:hover { border-color: #d4d8de; color: #e8eaed; }
.atk-chip.active { border-color: #f59e0b; color: #f59e0b; background: #151009; }

/* ── Eps ── */
.eps-row { display: flex; align-items: center; gap: 16px; }
.eps-input {
  background: #0d1017;
  border: 1px solid #9ca3af;
  color: #e8eaed;
  padding: 10px 16px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.9rem;
  border-radius: 2px;
  width: 120px;
  outline: none;
  transition: border 0.2s;
}
.eps-input:focus { border-color: #f59e0b; }
.eps-hint {
  font-size: 0.8rem;
  color: #d4d8de;
  font-family: 'Share Tech Mono', monospace;
}

/* ── 提交按钮 ── */
.submit-btn {
  margin-top: 40px;
  display: flex;
  align-items: center;
  gap: 10px;
  background: #f59e0b;
  color: #0a0c0f;
  border: none;
  padding: 14px 40px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.9rem;
  letter-spacing: 0.1em;
  cursor: pointer;
  border-radius: 2px;
  font-weight: 700;
  transition: all 0.2s;
}
.submit-btn:hover:not(:disabled) { background: #fbbf24; }
.submit-btn:disabled { opacity: 0.4; cursor: not-allowed; }

.loading-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: #0a0c0f;
  animation: pulse 1s infinite;
}
@keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.3; } }

/* ── 结果 ── */
.result-box {
  margin-top: 48px;
  border: 1px solid #9ca3af;
  border-radius: 4px;
  overflow: hidden;
}
.result-title {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.8rem;
  letter-spacing: 0.15em;
  color: #f59e0b;
  padding: 16px 24px;
  border-bottom: 1px solid #9ca3af;
  background: #0d1017;
}
.result-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1px;
  background: #9ca3af;
}
.result-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 20px 24px;
  background: #0d1017;
}
.result-label {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.68rem;
  letter-spacing: 0.1em;
  color: #d4d8de;
  text-transform: uppercase;
}
.result-value {
  font-size: 1.1rem;
  font-weight: 700;
  color: #e8eaed;
  font-family: 'Share Tech Mono', monospace;
}
.result-value.warn   { color: #f59e0b; }
.result-value.danger { color: #f43f5e; }
.level-A { color: #22c55e; }
.level-B { color: #06b6d4; }
.level-C { color: #f59e0b; }
.level-D, .level-Poor { color: #f43f5e; }

/* ── 图片区域 ── */
.result-images {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1px;
  background: #9ca3af;
  border-top: 1px solid #9ca3af;
}

.img-block {
  background: #0d1017;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.img-label {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.68rem;
  letter-spacing: 0.12em;
  color: #d4d8de;
  text-transform: uppercase;
}

.img-wrap {
  background: #080a0d;
  border: 1px solid #9ca3af;
  border-radius: 2px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

.result-img {
  width: 100%;
  height: auto;
  display: block;
}

.img-error {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.72rem;
  color: #d4d8de;
  letter-spacing: 0.08em;
}

@media (max-width: 768px) {
  .result-images { grid-template-columns: 1fr; }
}
/* ══ 体检单 ══ */
.report-card {
  margin-top: 48px;
  border: 1px solid #1e2530;
  border-radius: 4px;
  overflow: hidden;
  background: #0d1017;
}

/* 报告头 */
.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  border-bottom: 2px solid #f59e0b;
  background: #080a0d;
}
.report-header-left { display: flex; align-items: center; gap: 16px; }
.report-logo { font-size: 2rem; color: #f59e0b; }
.report-title {
  font-size: 1.2rem;
  font-weight: 700;
  color: #f0f2f5;
  letter-spacing: 0.05em;
}
.report-subtitle {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.6rem;
  letter-spacing: 0.18em;
  color: #9ca3af;
  margin-top: 4px;
}
.report-header-right { text-align: right; }
.report-id {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.75rem;
  color: #f59e0b;
  letter-spacing: 0.1em;
}
.report-date {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.65rem;
  color: #9ca3af;
  margin-top: 4px;
}

/* 基本信息栏 */
.report-info-bar {
  display: flex;
  align-items: center;
  padding: 16px 32px;
  background: #0a0c0f;
  border-bottom: 1px solid #1e2530;
  gap: 0;
}
.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}
.info-divider {
  width: 1px;
  height: 32px;
  background: #1e2530;
  margin: 0 24px;
  flex-shrink: 0;
}
.info-label {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.72rem;
  letter-spacing: 0.14em;
  color: #9ca3af;
  text-transform: uppercase;
}
.info-value {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.98rem;
  color: #e8eaed;
}

/* 综合评级 */
.report-grade-section {
  display: flex;
  align-items: center;
  padding: 28px 32px;
  gap: 48px;
  border-bottom: 1px solid #1e2530;
  background: #080a0d;
}
.grade-left { display: flex; flex-direction: column; gap: 8px; align-items: center; }
.grade-label {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.76rem;
  letter-spacing: 0.15em;
  color: #9ca3af;
  text-transform: uppercase;
}
.grade-value {
  font-family: 'Share Tech Mono', monospace;
  font-size: 4rem;
  font-weight: 700;
  line-height: 1;
}
.grade-A { color: #22c55e; }
.grade-B { color: #06b6d4; }
.grade-C { color: #f59e0b; }
.grade-D { color: #f43f5e; }
.grade-Poor { color: #f43f5e; }

.grade-desc { font-size: 0.78rem; color: #9ca3af; text-align: center; max-width: 140px; line-height: 1.5; }

.grade-right { flex: 1; display: flex; flex-direction: column; gap: 10px; }
.grade-score-label {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.66rem;
  letter-spacing: 0.15em;
  color: #9ca3af;
}
.grade-score {
  font-family: 'Share Tech Mono', monospace;
  font-size: 2rem;
  color: #e8eaed;
  font-weight: 700;
}
.grade-score-bar-wrap {
  height: 6px;
  background: #1e2530;
  border-radius: 3px;
  overflow: hidden;
  max-width: 320px;
}
.grade-score-bar {
  height: 100%;
  border-radius: 3px;
  transition: width 1s ease;
}
.bar-A { background: #22c55e; }
.bar-B { background: #06b6d4; }
.bar-C { background: #f59e0b; }
.bar-D { background: #f43f5e; }
.bar-Poor { background: #f43f5e; }

/* 章节标题 */
.report-section-title {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.78rem;
  letter-spacing: 0.15em;
  color: #9ca3af;
  padding: 16px 32px 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  border-top: 1px solid #1e2530;
}
.report-section-title span:first-child { color: #f59e0b; }

/* 检测表格 */
.report-table {
  margin: 0 32px 16px;
  border: 1px solid #1e2530;
  border-radius: 2px;
  overflow: hidden;
}
.rt-head {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 80px;
  padding: 10px 16px;
  background: #080a0d;
  border-bottom: 1px solid #1e2530;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.68rem;
  letter-spacing: 0.12em;
  color: #9ca3af;
  text-transform: uppercase;
}
.rt-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 80px;
  padding: 14px 16px;
  border-bottom: 1px solid #1e2530;
  align-items: center;
  transition: background 0.15s;
}
.rt-row:last-child { border-bottom: none; }
.rt-row:hover { background: #0a0c0f; }

.rt-name { font-size: 0.88rem; color: #d4d8de; }
.rt-name em { display: block; font-style: normal; font-family: 'Share Tech Mono', monospace; font-size: 0.62rem; color: #9ca3af; letter-spacing: 0.05em; margin-top: 2px; }
.rt-val { font-family: 'Share Tech Mono', monospace; font-size: 0.9rem; color: #e8eaed; font-weight: 700; }
.rt-val.warn { color: #f59e0b; }
.rt-val.danger { color: #f43f5e; }
.rt-ref { font-family: 'Share Tech Mono', monospace; font-size: 0.75rem; color: #9ca3af; }

.rt-status {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.7rem;
  letter-spacing: 0.08em;
  padding: 3px 10px;
  border-radius: 2px;
  text-align: center;
  width: fit-content;
}
.status-ok     { background: rgba(34,197,94,0.1);  color: #22c55e; border: 1px solid rgba(34,197,94,0.2); }
.status-warn   { background: rgba(245,158,11,0.1); color: #f59e0b; border: 1px solid rgba(245,158,11,0.2); }
.status-danger { background: rgba(244,63,94,0.1);  color: #f43f5e; border: 1px solid rgba(244,63,94,0.2); }

/* 指标条 */
.report-bars { padding: 8px 32px 20px; display: flex; flex-direction: column; gap: 16px; }
.bar-item { display: flex; flex-direction: column; gap: 6px; }
.bar-item-head { display: flex; justify-content: space-between; align-items: center; }
.bar-name { font-size: 0.82rem; color: #9ca3af; }
.bar-pct { font-family: 'Share Tech Mono', monospace; font-size: 0.82rem; color: #e8eaed; }
.bar-pct.warn { color: #f59e0b; }
.bar-pct.danger { color: #f43f5e; }

.bar-track {
  position: relative;
  height: 8px;
  background: #1e2530;
  border-radius: 4px;
  overflow: visible;
}
.bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 1s ease;
}
.fill-green { background: #22c55e; }
.fill-amber { background: #f59e0b; }
.fill-red   { background: #f43f5e; }

/* 阈值刻度线 */
.bar-threshold {
  position: absolute;
  top: -4px;
  width: 2px;
  height: 16px;
  background: #9ca3af;
  border-radius: 1px;
}
.bar-threshold::after {
  content: '基准';
  position: absolute;
  top: 18px;
  left: 50%;
  transform: translateX(-50%);
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.55rem;
  color: #9ca3af;
  white-space: nowrap;
}

/* 图片 */
.report-images {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  padding: 8px 32px 20px;
}
.img-block { display: flex; flex-direction: column; gap: 8px; }
.img-label { font-family: 'Share Tech Mono', monospace; font-size: 0.62rem; letter-spacing: 0.1em; color: #374151; text-transform: uppercase; }
.img-wrap { background: #080a0d; border: 1px solid #1e2530; border-radius: 2px; overflow: hidden; min-height: 160px; display: flex; align-items: center; justify-content: center; }
.result-img { width: 100%; height: auto; display: block; }

/* 结论 */
.report-conclusion {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin: 0 32px 0;
  padding: 20px 24px;
  border: 1px solid #1e2530;
  border-radius: 2px;
  background: #080a0d;
  border-top: 1px solid #1e2530;
}
.conclusion-icon {
  font-size: 1.8rem;
  font-weight: 700;
  font-family: 'Share Tech Mono', monospace;
  flex-shrink: 0;
  width: 40px;
  text-align: center;
}
.conclusion-title {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.65rem;
  letter-spacing: 0.15em;
  color: #9ca3af;
  margin-bottom: 8px;
  text-transform: uppercase;
}
.conclusion-body { font-size: 0.88rem; color: #9ca3af; line-height: 1.8; }

/* 报告页脚 */
.report-footer {
  display: flex;
  justify-content: space-between;
  padding: 14px 32px;
  border-top: 1px solid #1e2530;
  margin-top: 20px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.6rem;
  letter-spacing: 0.08em;
  color: #9ca3af;
  background: #080a0d;
}

@media (max-width: 768px) {
  .report-images { grid-template-columns: 1fr; }
  .report-info-bar { flex-wrap: wrap; gap: 16px; }
  .info-divider { display: none; }
  .rt-head, .rt-row { grid-template-columns: 2fr 1fr 60px; }
  .rt-ref { display: none; }
}
/* ── 响应式 ── */
@media (max-width: 768px) {
  .content { padding: 32px 24px; }
  .navbar { padding: 18px 24px; }
  .result-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>