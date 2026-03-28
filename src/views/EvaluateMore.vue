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

      <!-- 评测结果：体检单样式 -->
      <div v-if="result" class="report-card">
        <!-- 装饰角 -->
        <div class="corner tl"></div><div class="corner tr"></div>
        <div class="corner bl"></div><div class="corner br"></div>

        <!-- 报告头部 -->
        <div class="report-header">
          <div class="rh-brand">
            <span class="rh-logo">⬡</span>
            <div class="rh-title-group">
              <div class="rh-main-title">模型鲁棒性基准评测报告</div>
              <div class="rh-sub-title">BENCHMARK ROBUSTNESS EVALUATION REPORT</div>
            </div>
          </div>
          <div class="rh-meta">
            <div class="rh-meta-item"><span>报告编号:</span> #{{ reportId }}</div>
            <div class="rh-meta-item"><span>生成日期:</span> {{ reportDate }}</div>
          </div>
        </div>

        <!-- 基本信息条 -->
        <div class="info-bar">
          <div class="ib-item">
            <span class="ib-label">测试模型</span>
            <span class="ib-val">{{ result.model }}</span>
          </div>
          <div class="ib-item">
            <span class="ib-label">评估数据集</span>
            <span class="ib-val">{{ result.dataset }}</span>
          </div>
          <div class="ib-item">
            <span class="ib-label">数据规模</span>
            <span class="ib-val">{{ result.dataset_size }} 样本</span>
          </div>
          <div class="ib-item">
            <span class="ib-label">清洁准确率</span>
            <span class="ib-val">{{ (result.clean_accuracy * 100).toFixed(2) }}%</span>
          </div>
        </div>

        <!-- 核心评分区 -->
        <div class="score-section">
          <div class="score-circle">
            <div class="score-num">{{ result.robust_score?.toFixed(1) }}</div>
            <div class="score-label">ROBUST SCORE</div>
          </div>
          <div class="grade-box">
            <div class="grade-label">综合鲁棒等级评定</div>
            <div class="grade-val" :class="'level-' + result.robust_level">{{ result.robust_level }}</div>
            <div class="grade-desc">根据多个攻击算法下的性能表现加权计算得出</div>
          </div>
        </div>

        <!-- 详细数据表 -->
        <div class="report-section-title"><span>▌</span> 攻击组合评测明细 (Attack Details)</div>
        <div class="report-table-wrap">
          <table class="report-table">
            <thead>
              <tr>
                <th>攻击方法</th>
                <th>清洁准确率</th>
                <th>对抗准确率</th>
                <th>准确率下降</th>
                <th>攻击成功率</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="atk in result.attack_results" :key="atk.attack">
                <td class="td-name">{{ atk.attack }}</td>
                <td>{{ (atk.clean_accuracy * 100).toFixed(2) }}%</td>
                <td class="td-adv">{{ (atk.adv_accuracy * 100).toFixed(2) }}%</td>
                <td class="td-drop">{{ (atk.accuracy_drop * 100).toFixed(2) }}%</td>
                <td class="td-rate">{{ (atk.attack_success_rate * 100).toFixed(2) }}%</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 攻击强度排名 -->
        <div class="report-section-title"><span>▌</span> 攻击强度排名 (Strength Ranking)</div>
        <div class="ranking-list">
          <div v-for="(atk, i) in result.ranking" :key="'r' + atk.attack" class="rank-row">
            <span class="rank-num">{{ i + 1 }}</span>
            <span class="rank-name">{{ atk.attack }}</span>
            <div class="rank-bar-bg">
              <div class="rank-bar-fill" :style="{ width: (atk.attack_success_rate * 100) + '%' }"></div>
            </div>
            <span class="rank-val">{{ (atk.attack_success_rate * 100).toFixed(1) }}%</span>
          </div>
        </div>

        <!-- 可视化分析 -->
        <div class="report-section-title"><span>▌</span> 可视化图表分析 (Visual Analysis)</div>
        <div class="report-visuals">
          <!-- Attack Bar -->
          <div class="visual-block" v-if="result.bar_path">
            <div class="visual-label">1. 攻击鲁棒性对比柱状图 (Attack Bar)</div>
            <div class="visual-img-wrap">
              <img :src="imgUrl(result.bar_path)" alt="Attack Bar" />
            </div>
            <p class="visual-desc">
              本图以柱状图形式直观展示了三种攻击方法下模型的对抗样本准确率，可清晰对比不同攻击对模型性能的影响程度。
            </p>
          </div>

          <!-- Robustness Curve -->
          <div class="visual-block" v-if="result.curve_path">
            <div class="visual-label">2. 鲁棒性曲线 (Robustness Curve)</div>
            <div class="visual-img-wrap">
              <img :src="imgUrl(result.curve_path)" alt="Robustness Curve" />
            </div>
            <p class="visual-desc">
              本图以折线图形式呈现了模型在不同扰动强度（Eps）下的准确率变化趋势，反映了模型随扰动增强时的鲁棒性衰减规律。
            </p>
          </div>

          <!-- Heatmap -->
          <div class="visual-block" v-if="result.heatmap_path">
            <div class="visual-label">3. 攻击热力图 (Heatmap)</div>
            <div class="visual-img-wrap">
              <img :src="imgUrl(result.heatmap_path)" alt="Heatmap" />
            </div>
            <p class="visual-desc">
              本图以热力图形式展示了不同扰动强度与攻击方法组合下的模型准确率分布，通过颜色深浅直观体现准确率差异，便于快速定位高风险扰动-攻击组合。
            </p>
          </div>
        </div>

        <!-- 结论 -->
        <div class="report-conclusion">
          <div class="conclusion-label">综合评测结论:</div>
          <div class="conclusion-text">
            经过对所选攻击组合的批量压力测试，该模型表现出<strong>{{ result.robust_level === 'A' || result.robust_level === 'B' ? '良好' : '较弱' }}</strong>的防御能力。
            在 {{ result.ranking[0]?.attack }} 攻击下性能下降最为显著，建议针对该类攻击进行针对性对抗训练加固。
          </div>
        </div>

        <!-- 报告页脚 -->
        <div class="report-footer">
          <div class="rf-left">DRONE ROBUSTNESS EVALUATION SYSTEM</div>
          <div class="rf-right">CONFIDENTIAL / INTERNAL USE ONLY</div>
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

// const reportId = computed(() => 'BCH' + Date.now().toString().slice(-8))
// const reportDate = computed(() => new Date().toLocaleString('zh-CN'))
const reportId = ref('') 
const reportDate = ref('') 

const form = ref({ model: '', attack_group: '' })
const loading = ref(false)
const result = ref(null)
// const result = ref({
//   model: 'ResNet18',
//   dataset: 'drone_dataset',
//   dataset_size: 1000,
//   clean_accuracy: 0.7068,
//   robust_score: 45.82,
//   robust_level: 'C',
//   attack_results: [
//     { attack: 'FGSM', clean_accuracy: 0.7068, adv_accuracy: 0.3521, accuracy_drop: 0.3547, attack_success_rate: 0.5018 },
//     { attack: 'RFGSM', clean_accuracy: 0.7068, adv_accuracy: 0.3105, accuracy_drop: 0.3963, attack_success_rate: 0.5607 },
//     { attack: 'FFGSM', clean_accuracy: 0.7068, adv_accuracy: 0.3842, accuracy_drop: 0.3226, attack_success_rate: 0.4564 }
//   ],
//   ranking: [
//     { attack: 'RFGSM', attack_success_rate: 0.5607 },
//     { attack: 'FGSM', attack_success_rate: 0.5018 },
//     { attack: 'FFGSM', attack_success_rate: 0.4564 }
//   ],
//   bar_path: 'mock_bar.png',
//   curve_path: 'mock_curve.png',
//   heatmap_path: 'mock_heatmap.png'
// })

const canSubmit = computed(() => form.value.model && form.value.attack_group)

function imgUrl(path) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  const filename = path.split('/').pop()
  return `${import.meta.env.VITE_API_BASE}/results/${filename}`
}

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

/* ── 结果报告（体检单样式） ── */
.report-card {
  margin-top: 60px;
  background: #0d1017;
  border: 1px solid #1e2530;
  border-top: 4px solid #06b6d4;
  border-radius: 4px;
  position: relative;
  padding: 48px;
  box-shadow: 0 32px 64px -16px rgba(0, 0, 0, 0.6);
}

/* 装饰角 */
.corner { position: absolute; width: 16px; height: 16px; border-color: #06b6d4; border-style: solid; opacity: 0.6; }
.tl { top: 12px; left: 12px; border-width: 2px 0 0 2px; }
.tr { top: 12px; right: 12px; border-width: 2px 2px 0 0; }
.bl { bottom: 12px; left: 12px; border-width: 0 0 2px 2px; }
.br { bottom: 12px; right: 12px; border-width: 0 2px 2px 0; }

/* 报告头 */
.report-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  border-bottom: 1px solid #1e2530;
  padding-bottom: 24px;
  margin-bottom: 24px;
}
.rh-brand { display: flex; align-items: center; gap: 16px; }
.rh-logo { font-size: 2.4rem; color: #06b6d4; }
.rh-main-title { font-size: 1.5rem; font-weight: 700; color: #f0f2f5; margin-bottom: 4px; }
.rh-sub-title { font-family: 'Share Tech Mono', monospace; font-size: 0.7rem; color: #4b5563; letter-spacing: 0.1em; }
.rh-meta { text-align: right; font-family: 'Share Tech Mono', monospace; font-size: 0.75rem; color: #4b5563; }
.rh-meta span { color: #374151; margin-right: 8px; }

/* 基本信息条 */
.info-bar {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  background: rgba(255,255,255,0.02);
  padding: 20px 24px;
  border-radius: 2px;
  margin-bottom: 40px;
}
.ib-item { display: flex; flex-direction: column; gap: 6px; }
.ib-label { font-size: 0.65rem; color: #4b5563; font-weight: 700; text-transform: uppercase; }
.ib-val { font-size: 0.9rem; color: #d4d8de; font-weight: 700; }

/* 核心评分 */
.score-section {
  display: flex;
  align-items: center;
  gap: 60px;
  padding: 0 24px;
  margin-bottom: 56px;
}
.score-circle {
  width: 140px; height: 140px;
  border: 4px solid #1e2530;
  border-top-color: #06b6d4;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle, rgba(6, 182, 212, 0.05) 0%, transparent 70%);
}
.score-num { font-family: 'Share Tech Mono', monospace; font-size: 3rem; color: #06b6d4; line-height: 1; }
.score-label { font-size: 0.6rem; color: #4b5563; margin-top: 4px; letter-spacing: 0.1em; }

.grade-box { flex: 1; }
.grade-label { font-size: 0.85rem; color: #6b7280; margin-bottom: 8px; }
.grade-val { font-family: 'Share Tech Mono', monospace; font-size: 4rem; font-weight: 700; line-height: 1; margin-bottom: 12px; }
.grade-desc { font-size: 0.75rem; color: #4b5563; }

.level-A { color: #10b981; }
.level-B { color: #3b82f6; }
.level-C { color: #f59e0b; }
.level-D { color: #f43f5e; }

/* 小节标题 */
.report-section-title {
  font-size: 0.9rem;
  font-weight: 700;
  color: #f0f2f5;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.report-section-title span { color: #06b6d4; font-size: 1.1rem; }

/* 详细表格 */
.report-table-wrap { margin-bottom: 56px; border: 1px solid #1e2530; border-radius: 2px; }
.report-table { width: 100%; border-collapse: collapse; font-size: 0.85rem; }
.report-table th { background: rgba(255,255,255,0.02); color: #4b5563; font-weight: 700; text-align: left; padding: 14px 20px; border-bottom: 1px solid #1e2530; }
.report-table td { padding: 14px 20px; border-bottom: 1px solid #1e2530; color: #9ca3af; }
.td-name { font-family: 'Share Tech Mono', monospace; color: #f0f2f5; font-weight: 700; }
.td-adv { color: #06b6d4; }
.td-drop { color: #f43f5e; }
.td-rate { color: #d4d8de; }

/* 排名 */
.ranking-list { margin-bottom: 56px; display: flex; flex-direction: column; gap: 12px; padding: 0 12px; }
.rank-row { display: flex; align-items: center; gap: 20px; }
.rank-num { font-family: 'Share Tech Mono', monospace; font-size: 1rem; color: #4b5563; width: 24px; }
.rank-name { font-family: 'Share Tech Mono', monospace; font-size: 0.9rem; color: #d4d8de; width: 100px; }
.rank-bar-bg { flex: 1; height: 4px; background: #1e2530; border-radius: 2px; overflow: hidden; }
.rank-bar-fill { height: 100%; background: linear-gradient(90deg, #06b6d4, #f43f5e); border-radius: 2px; }
.rank-val { font-family: 'Share Tech Mono', monospace; font-size: 0.9rem; color: #06b6d4; width: 60px; text-align: right; }

/* 可视化分析 */
.report-visuals { display: flex; flex-direction: column; gap: 40px; margin-bottom: 56px; }
.visual-block { display: flex; flex-direction: column; gap: 16px; }
.visual-label { font-size: 0.85rem; color: #9ca3af; font-weight: 700; }
.visual-img-wrap {
  background: #080a0d;
  border: 1px solid #1e2530;
  border-radius: 4px;
  overflow: hidden;
  min-height: 240px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.visual-img-wrap img { width: 100%; height: auto; max-height: 500px; object-fit: contain; }
.visual-desc {
  font-size: 0.8rem;
  color: #6b7280;
  line-height: 1.6;
  padding: 12px 16px;
  background: rgba(255,255,255,0.01);
  border-left: 3px solid #06b6d4;
  margin: 0;
}

/* 结论 */
.report-conclusion {
  background: rgba(6, 182, 212, 0.03);
  border: 1px solid rgba(6, 182, 212, 0.1);
  padding: 24px;
  border-radius: 4px;
  margin-bottom: 48px;
}
.conclusion-label { font-size: 0.9rem; font-weight: 700; color: #06b6d4; margin-bottom: 12px; }
.conclusion-text { font-size: 0.88rem; color: #9ca3af; line-height: 1.8; }
.conclusion-text strong { color: #f0f2f5; }

/* 页脚 */
.report-footer {
  display: flex;
  justify-content: space-between;
  border-top: 1px solid #1e2530;
  padding-top: 24px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.65rem;
  color: #374151;
  letter-spacing: 0.1em;
}

@media (max-width: 900px) {
  .report-card { padding: 32px 24px; }
  .info-bar { grid-template-columns: repeat(2, 1fr); }
  .score-section { flex-direction: column; gap: 32px; text-align: center; }
  .ac-grid { grid-template-columns: 1fr; }
  .report-table-wrap { overflow-x: auto; }
}

@media (max-width: 768px) {
  .content { padding: 32px 24px; }
  .navbar { padding: 18px 24px; }
  .atk-acc, .atk-rate { display: none; }
}
</style>