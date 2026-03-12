<template>
  <div class="page">
    <header class="navbar">
      <div class="navbar-brand">
        <span class="brand-icon">⬡</span>
        <span class="brand-name">DRONE<em>EVAL</em></span>
      </div>
      <button class="back-btn" @click="router.push('/home')">← 返回</button>
    </header>

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
      <div v-if="result" class="result-box">
        <h2 class="result-title">评测结果</h2>
        <div class="result-grid">
          <div class="result-item">
            <span class="result-label">模型</span>
            <span class="result-value">{{ result.model }}</span>
          </div>
          <div class="result-item">
            <span class="result-label">攻击方式</span>
            <span class="result-value">{{ result.attack }}</span>
          </div>
          <div class="result-item">
            <span class="result-label">Eps</span>
            <span class="result-value">{{ result.eps }}</span>
          </div>
          <div class="result-item">
            <span class="result-label">清洁准确率</span>
            <span class="result-value">{{ (result.clean_accuracy * 100).toFixed(2) }}%</span>
          </div>
          <div class="result-item">
            <span class="result-label">对抗准确率</span>
            <span class="result-value warn">{{ (result.adv_accuracy * 100).toFixed(2) }}%</span>
          </div>
          <div class="result-item">
            <span class="result-label">准确率下降</span>
            <span class="result-value danger">{{ (result.accuracy_drop * 100).toFixed(2) }}%</span>
          </div>
          <div class="result-item">
            <span class="result-label">鲁棒评分</span>
            <span class="result-value">{{ result.robust_score?.toFixed(4) }}</span>
          </div>
          <div class="result-item">
            <span class="result-label">鲁棒等级</span>
            <span class="result-value" :class="'level-' + result.robust_level">
              {{ result.robust_level }}
            </span>
          </div>
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

const form = ref({ model: '', attack: '', eps: 0.03 })
const loading = ref(false)
const result = ref(null)

const canSubmit = computed(() => form.value.model && form.value.attack)

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
  border-bottom: 1px solid #1e2530;
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
  color: #374151;
}
.page-title { font-size: 2rem; font-weight: 700; color: #f0f2f5; margin: 8px 0; }
.page-desc { color: #6b7280; font-size: 0.9rem; }

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
  border: 1px solid #1e2530;
  border-radius: 4px;
  background: #0d1017;
  cursor: pointer;
  transition: all 0.2s;
}
.radio-card:hover { border-color: #374151; }
.radio-card.active { border-color: #f59e0b; background: #151009; }

.radio-name { font-size: 0.95rem; color: #e8eaed; flex: 1; }
.radio-tag {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.68rem;
  color: #6b7280;
  background: #13171f;
  border: 1px solid #1e2530;
  padding: 2px 8px;
  border-radius: 2px;
}

/* ── 攻击选择 ── */
.attack-grid { display: flex; flex-direction: column; gap: 20px; }

.group-label {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.68rem;
  letter-spacing: 0.12em;
  color: #374151;
  text-transform: uppercase;
  margin-bottom: 8px;
}
.attack-options { display: flex; flex-wrap: wrap; gap: 8px; }

.atk-chip {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.78rem;
  padding: 6px 14px;
  border: 1px solid #1e2530;
  border-radius: 2px;
  background: #0d1017;
  color: #9ca3af;
  cursor: pointer;
  transition: all 0.15s;
  letter-spacing: 0.05em;
}
.atk-chip:hover { border-color: #374151; color: #e8eaed; }
.atk-chip.active { border-color: #f59e0b; color: #f59e0b; background: #151009; }

/* ── Eps ── */
.eps-row { display: flex; align-items: center; gap: 16px; }
.eps-input {
  background: #0d1017;
  border: 1px solid #1e2530;
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
  color: #374151;
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
  border: 1px solid #1e2530;
  border-radius: 4px;
  overflow: hidden;
}
.result-title {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.8rem;
  letter-spacing: 0.15em;
  color: #f59e0b;
  padding: 16px 24px;
  border-bottom: 1px solid #1e2530;
  background: #0d1017;
}
.result-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1px;
  background: #1e2530;
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
  color: #374151;
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

/* ── 响应式 ── */
@media (max-width: 768px) {
  .content { padding: 32px 24px; }
  .navbar { padding: 18px 24px; }
  .result-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>