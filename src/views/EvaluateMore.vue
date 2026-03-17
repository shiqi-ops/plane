<template>
  <div class="page">
   

    <div class="content">
      <div class="page-header">
        <span class="page-num">02</span>
        <h1 class="page-title">多攻击组合基准</h1>
        <p class="page-desc">固定模型，选择推荐攻击组合，批量评测并自动排名</p>
      </div>

      <div class="form-grid">
        <!-- 选模型 -->
        <div class="form-section">
          <h2 class="section-title">选择模型</h2>
          <div class="radio-group">
            <label v-for="m in models" :key="m.value" class="radio-card" :class="{ active: form.model === m.value }">
              <input type="radio" v-model="form.model" :value="m.value" hidden />
              <span class="radio-name">{{ m.label }}</span>
              <span class="radio-tag">{{ m.tag }}</span>
            </label>
          </div>
        </div>

        <!-- 选攻击组合 -->
        <div class="form-section">
          <h2 class="section-title">选择攻击组合</h2>
          <div class="radio-group">
            <label v-for="g in attackGroups" :key="g.value" class="radio-card combo-card" :class="{ active: form.attack_group === g.value }">
              <input type="radio" v-model="form.attack_group" :value="g.value" hidden />
              <div class="combo-top">
                <span class="radio-name">{{ g.label }}</span>
                <span class="combo-color" :style="{ background: g.color }"></span>
              </div>
              <span class="combo-attacks">{{ g.attacks.join(' · ') }}</span>
            </label>
          </div>
        </div>
      </div>

      <button class="submit-btn" :disabled="!canSubmit || loading" @click="handleSubmit">
        <span v-if="loading" class="loading-dot"></span>
        {{ loading ? '评测中...' : '开始评测' }}
      </button>

      <!-- 结果 -->
     <div v-if="result" class="result-box">
      <h2 class="result-title">评测结果</h2>

      <!-- 基本信息 -->
      <div class="result-meta">
        <span>模型：{{ result.model }}</span>
        <span>数据集：{{ result.dataset }}</span>
        <span>数据量：{{ result.dataset_size }}</span>
        <span>清洁准确率：{{ (result.clean_accuracy * 100).toFixed(2) }}%</span>
        <span class="result-level" :class="'level-' + result.robust_level">
          鲁棒等级：{{ result.robust_level }}
        </span>
      </div>

      <!-- 各攻击结果柱状条 -->
      <div class="section-label">各攻击详情</div>
      <div class="attack-results">
        <div v-for="(atk, i) in result.attack_results" :key="atk.attack" class="atk-row">
          <span class="atk-rank">#{{ i + 1 }}</span>
          <span class="atk-name">{{ atk.attack }}</span>
          <div class="atk-bar-wrap">
            <div class="atk-bar" :style="{ width: (atk.adv_accuracy * 100) + '%' }"></div>
          </div>
          <span class="atk-acc">对抗准确率 {{ (atk.adv_accuracy * 100).toFixed(1) }}%</span>
          <span class="atk-drop">下降 {{ (atk.accuracy_drop * 100).toFixed(1) }}%</span>
          <span class="atk-rate">攻击成功率 {{ (atk.attack_success_rate * 100).toFixed(1) }}%</span>
        </div>
      </div>

      <!-- ranking 排名（按攻击成功率从高到低） -->
      <div class="section-label">攻击强度排名</div>
      <div class="ranking-list">
        <div v-for="(atk, i) in result.ranking" :key="'r' + atk.attack" class="rank-row">
          <span class="rank-medal" :class="'medal-' + (i + 1)">{{ i + 1 }}</span>
          <span class="rank-name">{{ atk.attack }}</span>
          <div class="rank-bar-wrap">
            <div class="rank-bar" :style="{ width: (atk.attack_success_rate * 100) + '%' }"></div>
          </div>
          <span class="rank-rate">{{ (atk.attack_success_rate * 100).toFixed(1) }}%</span>
        </div>
      </div>

      <div class="result-score">
        鲁棒评分：<strong>{{ result.robust_score?.toFixed(2) }}</strong>
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
  { label: '快速攻击评测', value: 'fast', attacks: ['FGSM', 'RFGSM', 'FFGSM'], color: '#f59e0b' },
  { label: '标准鲁棒性评测', value: 'iterative', attacks: ['BIM', 'PGD', 'PGDL2'], color: '#06b6d4' },
  { label: '迁移攻击评测', value: 'transfer', attacks: ['MIFGSM', 'NIFGSM', 'DIFGSM'], color: '#a78bfa' },
  { label: '强攻击评测', value: 'strong', attacks: ['CW', 'DeepFool', 'AutoAttack'], color: '#f43f5e' },
]

const form = ref({ model: '', attack_group: '' })
const loading = ref(false)
const result = ref(null)

const canSubmit = computed(() => form.value.model && form.value.attack_group)

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
      attack_group: form.value.attack_group,
      dataset: 'drone_dataset',
      eps: '0.03',
    }
    const res = await api.post('/evaluate/more', payload)
    result.value = res.data
    saveHistory({ type: 'more', model: form.value.model, attack_group: form.value.attack_group, result: res.data })
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
.page-title {
  font-size: 2rem;
  font-weight: 700;
  color: #f0f2f5;
  margin: 8px 0;
}
.page-desc { color: #d4d8de; font-size: 0.9rem; }

.form-grid {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.section-title {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.75rem;
  letter-spacing: 0.15em;
  color: #06b6d4;
  text-transform: uppercase;
  margin-bottom: 16px;
}

.radio-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

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
.radio-card.active { border-color: #06b6d4; background: #0b1921; }

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

.combo-card { flex-direction: column; align-items: flex-start; gap: 8px; }
.combo-top { display: flex; align-items: center; gap: 10px; width: 100%; }
.combo-color { width: 8px; height: 8px; border-radius: 50%; margin-left: auto; }
.combo-attacks {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.72rem;
  color: #d4d8de;
  letter-spacing: 0.05em;
}

.submit-btn {
  margin-top: 40px;
  display: flex;
  align-items: center;
  gap: 10px;
  background: #06b6d4;
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
.submit-btn:hover:not(:disabled) { background: #22d3ee; }
.submit-btn:disabled { opacity: 0.4; cursor: not-allowed; }

.loading-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: #0a0c0f;
  animation: pulse 1s infinite;
}
@keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.3; } }

/* 结果区 */
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
  color: #06b6d4;
  padding: 16px 24px;
  border-bottom: 1px solid #9ca3af;
  background: #0d1017;
}
.result-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  padding: 16px 24px;
  font-size: 0.85rem;
  color: #9ca3af;
  border-bottom: 1px solid #9ca3af;
  background: #0d1017;
}
.result-level { font-weight: 700; }
.level-A { color: #22c55e; }
.level-B { color: #06b6d4; }
.level-C { color: #f59e0b; }
.level-D, .level-Poor { color: #f43f5e; }

.attack-results { padding: 16px 24px; display: flex; flex-direction: column; gap: 12px; }
.atk-row {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 0.85rem;
}
.atk-rank {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.7rem;
  color: #d4d8de;
  width: 24px;
}
.atk-name {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.8rem;
  color: #e8eaed;
  width: 100px;
}
.atk-bar-wrap {
  flex: 1;
  height: 6px;
  background: #9ca3af;
  border-radius: 3px;
  overflow: hidden;
}
.atk-bar {
  height: 100%;
  background: #06b6d4;
  border-radius: 3px;
  transition: width 0.6s ease;
}
.atk-acc { color: #9ca3af; font-size: 0.8rem; width: 140px; text-align: right; }
.atk-rate { color: #d4d8de; font-size: 0.78rem; width: 140px; text-align: right; }


.section-label {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.65rem;
  letter-spacing: 0.18em;
  color: #d4d8de;
  text-transform: uppercase;
  padding: 12px 24px 4px;
  border-top: 1px solid #9ca3af;
}

.atk-drop { color: #f43f5e; font-size: 0.78rem; width: 100px; text-align: right; }

.ranking-list { padding: 8px 24px 16px; display: flex; flex-direction: column; gap: 10px; }

.rank-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.rank-medal {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.75rem;
  font-weight: 700;
  width: 24px;
  text-align: center;
}
.medal-1 { color: #f59e0b; }
.medal-2 { color: #9ca3af; }
.medal-3 { color: #a16207; }
.rank-medal:not(.medal-1):not(.medal-2):not(.medal-3) { color: #d4d8de; }

.rank-name {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.8rem;
  color: #e8eaed;
  width: 100px;
}

.rank-bar-wrap {
  flex: 1;
  height: 6px;
  background: #9ca3af;
  border-radius: 3px;
  overflow: hidden;
}

.rank-bar {
  height: 100%;
  background: linear-gradient(90deg, #06b6d4, #f59e0b);
  border-radius: 3px;
  transition: width 0.6s ease;
}

.rank-rate {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.8rem;
  color: #9ca3af;
  width: 56px;
  text-align: right;
}


.result-score {
  padding: 16px 24px;
  border-top: 1px solid #9ca3af;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.82rem;
  color: #d4d8de;
}
.result-score strong { color: #06b6d4; font-size: 1rem; }

@media (max-width: 768px) {
  .content { padding: 32px 24px; }
  .navbar { padding: 18px 24px; }
  .atk-acc, .atk-rate { display: none; }
}
</style>