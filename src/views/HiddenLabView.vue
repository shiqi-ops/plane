<template>
  <div class="hidden-lab-page">
    <!-- 装饰背景 -->
    <div class="lab-bg">
      <div class="bg-radar"></div>
      <div class="bg-glow"></div>
    </div>

    <div class="lab-container">
      <div class="lab-header">
        <div class="header-main">
          <!-- <span class="lock-icon"></span> -->
          <h1 class="lab-title">AegisDrone 隐藏 AI 安全实验室</h1>
        </div>
        <p class="lab-caption">高级 AI 安全分析系统 (Advanced AI Security Analysis System)</p>
      </div>

      <div class="divider"></div>

      <!-- 系统状态栏 -->
      <div class="stats-row">
        <div class="metric-card">
          <span class="m-label">系统状态</span>
          <span class="m-val online">在线 (Online)</span>
        </div>
        <div class="metric-card">
          <span class="m-label">AI 智能体</span>
          <span class="m-val">3个活跃</span>
        </div>
        <div class="metric-card">
          <span class="m-label">GPU 算力</span>
          <span class="m-val">可用</span>
        </div>
        <div class="metric-card">
          <span class="m-label">安全等级</span>
          <span class="m-val high">高 (High)</span>
        </div>
      </div>

      <div class="divider"></div>

      <!-- 文件上传 -->
      <div class="upload-section">
        <label class="upload-label">
          <span class="icon">📷</span> 上传无人机图像
          <input type="file" @change="handleFileUpload" accept="image/*" hidden />
        </label>
        <p v-if="!file" class="upload-hint">支持格式: JPG, PNG, JPEG</p>
      </div>

      <!-- 分析区域 -->
      <div v-if="file" class="analysis-area">
        <div class="analysis-grid">
          <!-- 左侧：图像预览 -->
          <div class="preview-panel">
            <div class="img-wrap">
              <img :src="filePreview" alt="输入图像" />
              <div class="img-overlay">深度扫描中...</div>
            </div>
            <p class="img-caption">输入图像预览</p>
          </div>

          <!-- 右侧：实时日志 -->
          <div class="log-panel">
            <h3 class="panel-title"> AI 实时分析日志</h3>
            <div class="log-box">
              <div v-for="(log, index) in activeLogs" :key="index" class="log-entry">
                <code>{{ log }}</code>
              </div>
              <div v-if="analyzing" class="log-cursor">_</div>
            </div>
          </div>
        </div>

        <div class="divider"></div>

        <!-- 进度与 Agent -->
        <div class="progress-section">
          <div class="main-progress">
            <div class="progress-bar-bg">
              <div class="progress-bar-fill" :style="{ width: overallProgress + '%' }"></div>
            </div>
            <span class="progress-text">{{ overallProgress }}%</span>
          </div>

          <div class="agents-list">
            <!-- Agent 1 -->
            <div class="agent-card" :class="{ active: overallProgress >= 10 }">
              <div class="card-hd">
                <h3>🛡 智能体 1 - 检测原理解释</h3>
                <span v-if="overallProgress >= 100" class="status-ok">分析完成</span>
              </div>
              <p v-if="overallProgress >= 100" class="agent-desc">{{ agent1_analysis }}</p>
            </div>

            <div class="agent-card" :class="{ active: overallProgress >= 30 }">
              <div class="card-hd">
                <h3>🛡 智能体 2 - 安全风险分析</h3>
                <span v-if="overallProgress >= 100" class="status-warn">风险等级：{{ riskLevelCN }}</span>
              </div>
              <p v-if="overallProgress >= 100" class="agent-desc">{{ agent2_analysis }}</p>
            </div>
            <!-- Agent 3 -->
            <div class="agent-card" :class="{ active: overallProgress >= 60 }">
              <div class="card-hd">
                <h3>🛡 智能体 3 - 报告生成</h3>
                <span v-if="overallProgress >= 100" class="status-ok">安全加固建议与结构化报告已生成</span>
              </div>
            </div>
          </div>
        </div>

        <div class="divider"></div>

        <!-- 最终报告 -->
        <div class="report-code">
          <h3 class="panel-title">AI 安全报告摘要</h3>
          <pre><code>
        {{ finalReport }}
          </code></pre>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import api from '../api/index.js'

const file = ref(null)
const filePreview = ref('')
const activeLogs = ref([])
const overallProgress = ref(0)
const analyzing = ref(false)

// 响应式数据变量，用于存储后端返回的内容
const riskLevel = ref('')
const securityScore = ref(0)
const agent1_analysis = ref('')
const agent2_analysis = ref('')
const finalReport = ref('')

// 中文映射
const riskLevelCN = computed(() => {
  const map = { 
    'Low': '低 (Low)', 
    'Medium': '中 (Medium)', 
    'High': '高 (High)',
    '低': '低 (Low)',
    '中': '中 (Medium)',
    '高': '高 (High)'
  }
  return map[riskLevel.value] || riskLevel.value || '待分析'
})

const logs = [
  "正在加载无人机视觉模型资源...",
  "正在初始化对抗风险分析智能体集群...",
  "正在对无人机图像进行深度特征提取...",
  "正在执行目标检测与分类识别...",
  "正在基于FeatureSqueezing算法扫描对抗扰动...",
  "正在分析标签不一致与判别逻辑异常...",
  "正在汇总多智能体分析结果..."
]

function handleFileUpload(e) {
  const f = e.target.files[0]
  if (!f) return
  file.value = f
  filePreview.value = URL.createObjectURL(f)
  startAnalysis()
}

async function startAnalysis() {
  if (!file.value) return
  
  analyzing.value = true
  activeLogs.value = []
  overallProgress.value = 0
  
  // 1. 模拟前端日志滚动过程（异步执行，不阻塞请求）
  const logPromise = (async () => {
    for (const log of logs) {
      activeLogs.value.push(log)
      // 模拟进度条增长
      if (overallProgress.value < 90) overallProgress.value += 12
      await new Promise(r => setTimeout(r, 600))
    }
  })()

  // 2. 准备 FormData
  const formData = new FormData()
  // 【关键】这里的 key 必须和后端接口文档保持一致 (file)
  formData.append('file', file.value) 

  try {
    // 【关键】使用 api 实例发送请求
    const response = await api.post('/agent/fast_api', formData, {
    })

    const data = response.data

    // 等待前端日志动画模拟完成后，再显示真实结果
    await logPromise
    
    // 3. 映射后端数据
    // 注意：请根据后端 AgentResult 类的实际字段名进行微调
    riskLevel.value = data.risk_level || data.riskLevel
    securityScore.value = data.risk_score ? parseFloat(data.risk_score).toFixed(2) : '0.00'
    agent1_analysis.value = data.agent1_analysis || ''
    agent2_analysis.value = data.agent2_analysis || ''
    finalReport.value = data.final_report || data.finalReport

    overallProgress.value = 100
    activeLogs.value.push("分析完成：检测结果已汇总。")
    
  } catch (error) {
    console.error("分析失败:", error)
    // 细化错误提示
    const errorMsg = error.response?.data?.message || "无法连接到远程分析服务器"
    activeLogs.value.push(`错误：${errorMsg} (状态码: ${error.response?.status || '网络错误'})`)
  } finally {
    analyzing.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=Noto+Sans+SC:wght@400;700&display=swap');

.hidden-lab-page {
  min-height: 100vh;
  background: radial-gradient(circle at top, #020617, #000000);
  color: white;
  font-family: 'Noto Sans SC', sans-serif;
  position: relative;
  overflow-x: hidden;
  padding: 60px 40px;
}

.lab-bg {
  position: absolute; inset: 0;
  pointer-events: none;
}
.bg-radar {
  position: absolute; top: 50%; left: 50%;
  width: 1000px; height: 1000px;
  transform: translate(-50%, -50%);
  border: 1px solid rgba(0, 247, 255, 0.05);
  border-radius: 50%;
}
.bg-glow {
  position: absolute; top: 0; left: 50%;
  width: 600px; height: 400px;
  transform: translateX(-50%);
  background: radial-gradient(circle, rgba(0, 247, 255, 0.1) 0%, transparent 70%);
  filter: blur(50px);
}

.lab-container {
  position: relative;
  z-index: 10;
  max-width: 1200px;
  margin: 0 auto;
}

.lab-header { text-align: center; margin-bottom: 40px; }
.header-main { display: flex; align-items: center; justify-content: center; gap: 16px; margin-bottom: 8px; }
.lock-icon { font-size: 2.5rem; }
.lab-title {
  font-family: 'Share Tech Mono', monospace;
  font-size: 2.5rem;
  color: #00f7ff;
  text-shadow: 0 0 15px rgba(0, 247, 255, 0.5);
  margin: 0;
}
.lab-caption { color: #64748b; font-size: 1.1rem; letter-spacing: 0.1em; }

.divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0, 247, 255, 0.2), transparent);
  margin: 40px 0;
}

/* Stats */
.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; }
.metric-card {
  background: rgba(17, 24, 39, 0.7);
  backdrop-filter: blur(10px);
  padding: 24px;
  border-radius: 12px;
  border: 1px solid rgba(0, 247, 255, 0.2);
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.m-label { color: #94a3b8; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.05em; }
.m-val { font-family: 'Share Tech Mono', monospace; font-size: 1.5rem; font-weight: 700; color: #f8fafc; }
.m-val.online { color: #22c55e; }
.m-val.high { color: #00f7ff; }

/* Upload */
.upload-section { display: flex; flex-direction: column; align-items: center; gap: 16px; }
.upload-label {
  background: rgba(0, 247, 255, 0.1);
  border: 1px dashed #00f7ff;
  color: #00f7ff;
  padding: 16px 40px;
  border-radius: 8px;
  cursor: pointer;
  font-family: 'Share Tech Mono', monospace;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 12px;
}
.upload-label:hover { background: rgba(0, 247, 255, 0.2); box-shadow: 0 0 20px rgba(0, 247, 255, 0.2); }
.upload-hint { color: #475569; font-size: 0.85rem; }

/* Analysis */
.analysis-grid { display: grid; grid-template-columns: 1fr 2fr; gap: 40px; margin-top: 40px; }

.preview-panel { display: flex; flex-direction: column; gap: 16px; }
.img-wrap {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(0, 247, 255, 0.3);
  background: #000;
}
.img-wrap img { width: 100%; height: auto; display: block; opacity: 0.8; }
.img-overlay {
  position: absolute; inset: 0;
  display: flex; align-items: center; justify-content: center;
  background: rgba(0, 0, 0, 0.4);
  color: #00f7ff;
  font-family: 'Share Tech Mono', monospace;
  font-size: 1.2rem;
  animation: pulse 2s infinite;
}
@keyframes pulse { 0%, 100% { opacity: 0.3; } 50% { opacity: 0.8; } }
.img-caption { text-align: center; color: #64748b; font-size: 0.9rem; }

.log-panel { display: flex; flex-direction: column; gap: 16px; }
.panel-title { font-size: 1.25rem; color: #00f7ff; margin: 0; font-weight: 700; }
.log-box {
  background: rgba(0, 0, 0, 0.6);
  border: 1px solid #1e293b;
  border-radius: 8px;
  padding: 24px;
  height: 300px;
  overflow-y: auto;
  font-family: 'Share Tech Mono', monospace;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.log-entry { color: #22c55e; font-size: 0.95rem; }
.log-cursor { color: #00f7ff; animation: blink 1s infinite; }
@keyframes blink { 0%, 100% { opacity: 0; } 50% { opacity: 1; } }

/* Progress */
.main-progress { display: flex; align-items: center; gap: 20px; margin-bottom: 40px; }
.progress-bar-bg { flex: 1; height: 12px; background: #1e293b; border-radius: 6px; overflow: hidden; }
.progress-bar-fill { height: 100%; background: linear-gradient(90deg, #00f7ff, #3b82f6); transition: width 0.1s linear; }
.progress-text { font-family: 'Share Tech Mono', monospace; color: #00f7ff; font-weight: 700; width: 50px; }

.agents-list { display: flex; flex-direction: column; gap: 16px; }
.agent-card {
  background: rgba(17, 24, 39, 0.5);
  padding: 20px 32px;
  border-radius: 12px;
  border: 1px solid rgba(0, 247, 255, 0.1);
  transition: all 0.5s;
  opacity: 0.5;
}
.agent-card.active { opacity: 1; border-color: rgba(0, 247, 255, 0.3); background: rgba(17, 24, 39, 0.7); }
.agent-desc {
  margin-top: 12px;
  font-size: 0.9rem;
  color: #94a3b8;
  line-height: 1.6;
  border-left: 2px solid #00f7ff;
  padding-left: 12px;
}
.card-hd { display: flex; justify-content: space-between; align-items: center; }
.card-hd h3 { font-size: 1.1rem; color: #e2e8f0; margin: 0; }

.status-ok { color: #22c55e; font-family: 'Share Tech Mono', monospace; font-size: 0.9rem; }
.status-warn { color: #f59e0b; font-family: 'Share Tech Mono', monospace; font-size: 0.9rem; }

/* Final Report */
.final-report { margin-top: 40px; animation: slideUp 0.8s ease-out; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

.report-title { font-size: 1.8rem; color: #00f7ff; margin-bottom: 24px; }
.report-metrics { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; margin-bottom: 32px; }

.report-code { margin-bottom: 32px; }
.report-code pre {
  background: rgba(0, 0, 0, 0.4);
  border: 1px solid #1e293b;
  padding: 24px;
  border-radius: 8px;
  color: #94a3b8;
  font-family: 'Share Tech Mono', monospace;
  line-height: 1.8;
}

.complete-msg { color: #22c55e; font-weight: 700; text-align: center; font-size: 1.1rem; }

.low { color: #22c55e !important; }
.medium { color: #f59e0b !important; }
.high { color: #f43f5e !important; }

.fade-enter-active, .fade-leave-active { transition: opacity 0.5s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
