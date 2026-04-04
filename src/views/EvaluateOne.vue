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
              <div class="rh-main-title">模型鲁棒性评测报告</div>
              <div class="rh-sub-title">SINGLE MODEL ROBUSTNESS EVALUATION REPORT</div>
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
            <span class="ib-label">攻击方法</span>
            <span class="ib-val">{{ result.attack }}</span>
          </div>
          <div class="ib-item">
            <span class="ib-label">扰动强度 ε</span>
            <span class="ib-val">{{ result.eps }}</span>
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
            <div class="grade-desc">根据该攻击算法下的模型表现综合评定</div>
          </div>
        </div>

        <!-- 详细数据表 -->
        <div class="report-section-title"><span>▌</span> 评测项目明细 (Evaluation Details)</div>
        <div class="report-table-wrap">
          <table class="report-table">
            <thead>
              <tr>
                <th>检测项目</th>
                <th>检测值</th>
                <th>参考范围</th>
                <th>状态</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td class="td-name">清洁准确率 (Clean Accuracy)</td>
                <td>{{ (result.clean_accuracy * 100).toFixed(2) }}%</td>
                <td>≥ 70%</td>
                <td>
                  <span class="status-badge" :class="result.clean_accuracy >= 0.7 ? 'ok' : 'warn'">
                    {{ result.clean_accuracy >= 0.7 ? '正常' : '偏低' }}
                  </span>
                </td>
              </tr>
              <tr>
                <td class="td-name">对抗准确率 (Adv Accuracy)</td>
                <td class="td-adv">{{ (result.adv_accuracy * 100).toFixed(2) }}%</td>
                <td>≥ 50%</td>
                <td>
                  <span class="status-badge" :class="result.adv_accuracy >= 0.5 ? 'ok' : 'danger'">
                    {{ result.adv_accuracy >= 0.5 ? '正常' : '异常' }}
                  </span>
                </td>
              </tr>
              <tr>
                <td class="td-name">准确率下降 (Accuracy Drop)</td>
                <td class="td-drop">{{ (result.accuracy_drop * 100).toFixed(2) }}%</td>
                <td>≤ 20%</td>
                <td>
                  <span class="status-badge" :class="result.accuracy_drop <= 0.2 ? 'ok' : 'danger'">
                    {{ result.accuracy_drop <= 0.2 ? '正常' : '异常' }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 可视化分析 -->
        <div class="report-section-title"><span>▌</span> 可视化图表分析 (Visual Analysis)</div>
        <div class="report-visuals">
          <!-- 原始 vs 对抗样本 -->
          <div class="visual-block" v-if="result.compare_path">
            <div class="visual-label">1. 原始图像 (Original) vs 对抗样本 (Adversarial)</div>
            <div class="visual-img-wrap">
              <img :src="result.compare_path" alt="对比图" />
            </div>
            <p class="visual-desc">
              对比分析：左侧为原始输入图像，右侧为添加扰动（ε={{ result.eps }}）后的对抗样本。虽然视觉差异极小，但已成功诱导模型产生错误判断。
            </p>
          </div>

          <!-- 鲁棒性性能曲线 -->
          <div class="visual-block" v-if="result.curve_path">
            <div class="visual-label">2. 鲁棒性性能曲线 (Robustness Curve)</div>
            <div class="visual-img-wrap">
              <img :src="result.curve_path" alt="鲁棒性曲线" />
            </div>
            <p class="visual-desc">
              曲线分析显示：随着扰动强度（eps）逐步提升，模型准确率呈现下降趋势，反映了模型随扰动增强时的鲁棒性衰减规律。
            </p>
          </div>
        </div>

        <!-- 结论 -->
        <div class="report-conclusion">
          <div class="conclusion-content">
            <div class="conclusion-label">综合评测结论:</div>
            <div class="conclusion-text">
              {{ conclusionText(result) }}
            </div>
          </div>
          <!-- 导出按钮 -->
          <div v-if="result.download_url" class="export-wrap">
            <button class="export-btn" @click="handleDownload">
              <span class="export-icon">⤓</span> 导出详细评测报告 
            </button>
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
const result = ref(null)
// const result = ref({
//   model: 'ResNet18',
//   dataset: 'drone_dataset',
//   attack: 'FGSM',
//   eps: 0.03,
//   clean_accuracy: 0.7068,
//   adv_accuracy: 0.2308,
//   accuracy_drop: 0.4760,
//   robust_score: 0.3265,
//   robust_level: 'D',
//   curve_path: 'mock_curve.png',    // 模拟图片路径
//   compare_path: 'mock_compare.png',
// })
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

// ── 下载报告 ──────────────────────────────────
function handleDownload() {
  if (!result.value?.download_url) return
  const baseUrl = 'http://localhost:8080/files'
  const rawPath = result.value.download_url
  const finalUrl = rawPath.startsWith('/') ? `${baseUrl}${rawPath}` : `${baseUrl}/${rawPath}`
  
  // 创建 a 标签模拟点击下载
  const link = document.createElement('a')
  link.href = finalUrl
  link.setAttribute('download', rawPath.split('/').pop())
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
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

/* ── 结果报告（体检单样式） ── */
.report-card {
  margin-top: 60px;
  background: #0d1017;
  border: 1px solid #1e2530;
  border-top: 4px solid #f59e0b;
  border-radius: 4px;
  position: relative;
  padding: 48px;
  box-shadow: 0 32px 64px -16px rgba(0, 0, 0, 0.6);
}

/* 装饰角 */
.corner { position: absolute; width: 16px; height: 16px; border-color: #f59e0b; border-style: solid; opacity: 0.6; }
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
.rh-logo { font-size: 2.4rem; color: #f59e0b; }
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
  border-top-color: #f59e0b;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle, rgba(245,158,11,0.05) 0%, transparent 70%);
}
.score-num { font-family: 'Share Tech Mono', monospace; font-size: 3rem; color: #f59e0b; line-height: 1; }
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
.report-section-title span { color: #f59e0b; font-size: 1.1rem; }

/* 详细表格 */
.report-table-wrap { margin-bottom: 56px; border: 1px solid #1e2530; border-radius: 2px; }
.report-table { width: 100%; border-collapse: collapse; font-size: 0.85rem; }
.report-table th { background: rgba(255,255,255,0.02); color: #4b5563; font-weight: 700; text-align: left; padding: 14px 20px; border-bottom: 1px solid #1e2530; }
.report-table td { padding: 14px 20px; border-bottom: 1px solid #1e2530; color: #9ca3af; }
.td-name { font-family: 'Share Tech Mono', monospace; color: #f0f2f5; font-weight: 700; }
.td-adv { color: #f59e0b; }
.td-drop { color: #f43f5e; }

.status-badge {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.7rem;
  padding: 3px 10px;
  border-radius: 2px;
}
.status-badge.ok { background: rgba(16, 185, 129, 0.1); color: #10b981; border: 1px solid rgba(16, 185, 129, 0.2); }
.status-badge.warn { background: rgba(245, 158, 11, 0.1); color: #f59e0b; border: 1px solid rgba(245, 158, 11, 0.2); }
.status-badge.danger { background: rgba(244, 63, 94, 0.1); color: #f43f5e; border: 1px solid rgba(244, 63, 94, 0.2); }

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
  border-left: 3px solid #f59e0b;
  margin: 0;
}

/* 结论 */
.report-conclusion {
  background: rgba(245,158,11,0.03);
  border: 1px solid rgba(245,158,11,0.1);
  padding: 32px;
  border-radius: 4px;
  margin-bottom: 48px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 40px;
}
.conclusion-content { flex: 1; }
.conclusion-label { font-size: 0.9rem; font-weight: 700; color: #f59e0b; margin-bottom: 12px; }
.conclusion-text { font-size: 0.88rem; color: #9ca3af; line-height: 1.8; }
.conclusion-text strong { color: #f0f2f5; }

.export-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #f59e0b;
  border: none;
  color: #0a0c0f;
  padding: 12px 24px;
  border-radius: 2px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.85rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}
.export-btn:hover {
  background: #fbbf24;
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(245, 158, 11, 0.3);
}
.export-icon { font-size: 1.2rem; }

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
  .report-table-wrap { overflow-x: auto; }
}

@media (max-width: 768px) {
  .content { padding: 32px 24px; }
}
</style>