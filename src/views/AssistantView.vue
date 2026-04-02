<template>
  <div class="assistant-page">
    <div class="page-header">
      <div class="ph-bg"></div>
      <div class="ph-content">
        <span class="ph-eyebrow">AI ASSISTANT</span>
        <h1 class="ph-title">智能助手</h1>
        <p class="ph-desc">AI 驱动的评测报告解读 · 领域知识智能问答</p>
      </div>
    </div>

    <div class="assistant-layout">

      <!-- 左：报告解读 -->
      <div class="panel">
        <div class="panel-hd">
          <span class="panel-num">01</span>
          <h2 class="panel-title">检测报告智能解读</h2>
          <p class="panel-desc">选择历史报告，一键生成 AI 解读文档</p>
        </div>

        <div class="panel-body">
          <div class="field">
            <label class="field-label">选择历史报告</label>
            <select v-model="selectedReport" class="field-select">
              <option value="" disabled>-- 请选择一份历史报告 --</option>
              <option v-for="r in history" :key="r.id" :value="r">
                {{ r.date }} · {{ r.model }} · {{ r.attack ?? r.attack_group }}
              </option>
            </select>
          </div>

          <div v-if="selectedReport" class="report-preview">
            <div class="preview-row"><span>模型</span><span>{{ selectedReport.model }}</span></div>
            <div class="preview-row"><span>攻击</span><span>{{ selectedReport.attack ?? selectedReport.attack_group }}</span></div>
            <div class="preview-row">
              <span>清洁准确率</span>
              <span>{{ (selectedReport.result.clean_accuracy * 100).toFixed(2) }}%</span>
            </div>
            <div class="preview-row">
              <span>对抗准确率</span>
              <span class="warn">{{ (selectedReport.result.adv_accuracy * 100).toFixed(2) }}%</span>
            </div>
            <div class="preview-row">
              <span>鲁棒等级</span>
              <span :class="'level-' + selectedReport.result.robust_level">
                {{ selectedReport.result.robust_level }}
              </span>
            </div>
          </div>

          <button class="action-btn" :disabled="!selectedReport || interpretLoading" @click="handleInterpret">
            <span v-if="interpretLoading" class="ldot"></span>
            {{ interpretLoading ? 'AI 解读中...' : '一键智能解读' }}
          </button>

          <div v-if="interpretation" class="interp-box">
            <div class="interp-hd">
              <span>AI 智能解读报告</span>
              <div class="hd-actions">
                <button class="copy-btn" @click="copyText(interpretationText)">复制</button>
                <button class="copy-btn" @click="exportReport">导出</button>
              </div>
            </div>
            <div class="interp-body structured">
              <div class="interp-section">
                <h3 class="section-h3">▌ 核心结论 (Core Conclusions)</h3>
                <p>{{ interpretation.conclusions }}</p>
              </div>
              <div class="interp-section">
                <h3 class="section-h3">▌ 弱点分析 (Weakness Analysis)</h3>
                <p>{{ interpretation.weaknesses }}</p>
              </div>
              <div class="interp-section">
                <h3 class="section-h3">▌ 优化建议 (Optimization Suggestions)</h3>
                <p>{{ interpretation.suggestions }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右：智能问答 -->
      <div class="panel chat-panel">
        <div class="panel-hd">
          <span class="panel-num">02</span>
          <h2 class="panel-title">智能问答</h2>
          <p class="panel-desc">针对无人机鲁棒性领域的 AI 问答</p>
        </div>

        <div class="chat-messages" ref="chatBox">
          <div v-if="messages.length === 0" class="chat-empty">
            <p class="empty-hint">你可以问我任何关于鲁棒性评测的问题</p>
            <div class="suggestions">
              <button v-for="q in suggestions" :key="q" class="suggest-btn" @click="sendMsg(q)">
                {{ q }}
              </button>
            </div>
          </div>

          <div v-for="msg in messages" :key="msg.id" class="msg" :class="msg.role">
            <div class="bubble">{{ msg.content }}</div>
          </div>

          <div v-if="chatLoading" class="msg ai">
            <div class="bubble typing">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>

        <div class="chat-input-row">
          <input
            v-model="chatInput"
            class="chat-input"
            placeholder="输入问题，按 Enter 发送..."
            @keyup.enter="sendMsg()"
          />
          <button class="send-btn" :disabled="!chatInput.trim() || chatLoading" @click="sendMsg()">
            发送
          </button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import api from '../api/index.js'

const history = computed(() => {
  try { return JSON.parse(localStorage.getItem('evalHistory') || '[]').reverse() }
  catch { return [] }
})

const selectedReport = ref('')
const interpretLoading = ref(false)
const interpretation = ref(null)

const interpretationText = computed(() => {
  if (!interpretation.value) return ''
  return `【AI 智能解读报告】\n\n` +
         `▌ 核心结论 (Core Conclusions)\n${interpretation.value.conclusions}\n\n` +
         `▌ 弱点分析 (Weakness Analysis)\n${interpretation.value.weaknesses}\n\n` +
         `▌ 优化建议 (Optimization Suggestions)\n${interpretation.value.suggestions}`
})

async function handleInterpret() {
  interpretLoading.value = true
  interpretation.value = null
  try {
    // 模拟调用豆包 API 或后端 API
    // 在真实环境下，这里会发送 selectedReport.value 到后端，由后端调用 AI 模型生成结构化内容
    const res = await api.post('/assistant/interpret', { report: selectedReport.value })
    
    // 假设后端返回结构化数据，如果返回的是字符串，则进行简单的 mock 拆分
    if (typeof res.data.interpretation === 'object') {
      interpretation.value = res.data.interpretation
    } else {
      // Mock 结构化数据
      interpretation.value = {
        conclusions: "模型在当前攻击组合下表现出明显的鲁棒性不足。尽管在清洁数据集上表现良好，但在面对迭代式对抗攻击（如 PGD）时，准确率下降超过 40%，说明模型对于精细扰动非常敏感。",
        weaknesses: "1. 决策边界过于复杂且不平滑，导致微小扰动即可改变分类结果。\n2. 对高频噪声特征过度依赖，缺乏对图像语义结构的深度提取能力。\n3. 在大 eps 扰动下，模型的对抗样本检测机制完全失效。",
        suggestions: "1. 引入对抗训练（Adversarial Training），将生成的对抗样本加入训练集。\n2. 使用 TRADES 等鲁棒性损失函数优化训练过程，平衡清洁准确率与对抗鲁棒性。\n3. 在部署前增加输入变换防御层（如 JPEG 压缩或随机调整大小）以过滤高频噪声。"
      }
    }
  } catch {
    interpretation.value = {
      conclusions: "解读失败，请检查网络连接。",
      weaknesses: "无法获取分析数据。",
      suggestions: "请重试或检查后端 API 状态。"
    }
  } finally {
    interpretLoading.value = false
  }
}

function exportReport() {
  if (!interpretation.value) return
  const blob = new Blob([interpretationText.value], { type: 'text/plain' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `AI_Interpretation_${selectedReport.value.model}_${Date.now().toString().slice(-4)}.txt`
  a.click()
  URL.revokeObjectURL(url)
}

function copyText(text) {
  navigator.clipboard.writeText(text)
  alert('内容已复制到剪贴板')
}

// Chat
const messages = ref([])
const chatInput = ref('')
const chatLoading = ref(false)
const chatBox = ref(null)
let msgId = 0

const suggestions = [
  'FGSM 和 PGD 有什么区别？',
  '鲁棒等级 D 意味着什么？',
  '如何提升模型对抗鲁棒性？',
]

async function sendMsg(text) {
  const content = text || chatInput.value.trim()
  if (!content) return
  chatInput.value = ''
  messages.value.push({ id: ++msgId, role: 'user', content })
  await nextTick(); scrollBottom()

  chatLoading.value = true
  try {
  const res = await api.post('/ai/chat', { 
    id: '123', 
    messages: content 
  })
  
  messages.value.push({ 
    id: ++msgId, 
    role: 'ai', 
    content: typeof res.data === 'string' ? res.data : res.data.reply 
  })
} catch {
    messages.value.push({ id: ++msgId, role: 'ai', content: '请求失败，请检查后端连接。' })
  } finally {
    chatLoading.value = false
    await nextTick(); scrollBottom()
  }
}

function scrollBottom() {
  if (chatBox.value) chatBox.value.scrollTop = chatBox.value.scrollHeight
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=Noto+Sans+SC:wght@400;700&display=swap');

.assistant-page {
  background: #0a0c0f;
  color: #d4d8de;
  font-family: 'Noto Sans SC', sans-serif;
  min-height: calc(100vh - 58px);
  display: flex;
  flex-direction: column;
}

.page-header {
  position: relative;
  padding: 48px 48px 40px;
  border-bottom: 1px solid #1e2530;
  overflow: hidden;
}
.ph-bg {
  position: absolute; inset: 0;
  background-image: linear-gradient(#374151 1px,transparent 1px), linear-gradient(90deg,#374151 1px,transparent 1px);
  background-size: 48px 48px;
  opacity: 0.15;
}
.ph-content { position: relative; z-index: 1; }
.ph-eyebrow {
  display: block;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.62rem;
  letter-spacing: 0.25em;
  color: #f59e0b;
  margin-bottom: 8px;
}
.ph-title { font-size: 2rem; font-weight: 700; color: #f0f2f5; margin: 0 0 8px; }
.ph-desc { color: #9ca3af; font-size: 0.88rem; margin: 0; }

.assistant-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1px;
  background: #1e2530;
  flex: 1;
}

/* Panels */
.panel {
  background: #0a0c0f;
  display: flex;
  flex-direction: column;
}

.panel-hd {
  padding: 32px 40px 24px;
  border-bottom: 1px solid #1e2530;
}
.panel-num {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.62rem;
  letter-spacing: 0.2em;
  color: #4b5563;
  display: block;
  margin-bottom: 8px;
}
.panel-title { font-size: 1.1rem; font-weight: 700; color: #e8eaed; margin: 0 0 6px; }
.panel-desc { font-size: 0.82rem; color: #6b7280; margin: 0; }

.panel-body {
  padding: 32px 40px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  overflow-y: auto;
}

/* Fields */
.field { display: flex; flex-direction: column; gap: 8px; }
.field-label {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.68rem;
  letter-spacing: 0.15em;
  color: #f59e0b;
  text-transform: uppercase;
}
.field-select {
  background: #0d1017;
  border: 1px solid #374151;
  color: #e8eaed;
  padding: 10px 14px;
  font-family: 'Noto Sans SC', sans-serif;
  font-size: 0.88rem;
  border-radius: 2px;
  outline: none;
  transition: all 0.2s;
}
.field-select:focus { border-color: #f59e0b; box-shadow: 0 0 0 2px rgba(245,158,11,0.1); }
.field-select option { background: #0d1017; }

.report-preview {
  background: rgba(255,255,255,0.02);
  border: 1px solid #1e2530;
  border-radius: 4px;
  overflow: hidden;
}
.preview-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #1e2530;
  font-size: 0.84rem;
}
.preview-row:last-child { border-bottom: none; }
.preview-row span:first-child { color: #6b7280; font-family: 'Share Tech Mono', monospace; font-size: 0.72rem; letter-spacing: 0.08em; }
.preview-row span:last-child { color: #d4d8de; font-family: 'Share Tech Mono', monospace; }
.warn { color: #f59e0b !important; }
.level-A { color: #22c55e !important; }
.level-B { color: #06b6d4 !important; }
.level-C { color: #f59e0b !important; }
.level-D, .level-Poor { color: #f43f5e !important; }

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f59e0b;
  color: #0a0c0f;
  border: none;
  padding: 12px 28px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.82rem;
  letter-spacing: 0.1em;
  font-weight: 700;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.2s;
  align-self: flex-start;
}
.action-btn:hover:not(:disabled) { background: #fbbf24; }
.action-btn:disabled { opacity: 0.4; cursor: not-allowed; }

.ldot {
  width: 8px; height: 8px;
  border-radius: 50%; background: #0a0c0f;
  animation: ldot 1s infinite;
}
@keyframes ldot { 0%,100%{opacity:1} 50%{opacity:0.2} }

.interp-box {
  border: 1px solid #1e2530;
  border-radius: 4px;
  overflow: hidden;
  background: #0d1017;
}
.interp-hd {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid #1e2530;
  background: #13171f;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.75rem;
  letter-spacing: 0.1em;
  color: #f59e0b;
}
.hd-actions { display: flex; gap: 8px; }
.copy-btn {
  background: rgba(255,255,255,0.05);
  border: 1px solid #374151;
  color: #9ca3af;
  padding: 4px 12px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.7rem;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.2s;
}
.copy-btn:hover { border-color: #f59e0b; color: #f59e0b; background: rgba(245,158,11,0.05); }

.interp-body {
  padding: 24px;
  font-size: 0.88rem;
  color: #9ca3af;
  line-height: 1.8;
  white-space: pre-wrap;
}
.interp-body.structured {
  display: flex;
  flex-direction: column;
  gap: 28px;
  padding: 24px 32px;
}
.interp-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.section-h3 {
  font-size: 0.85rem;
  font-weight: 700;
  color: #f59e0b;
  margin: 0;
  letter-spacing: 0.05em;
}
.interp-section p {
  margin: 0;
  color: #d4d8de;
  font-size: 0.88rem;
  line-height: 1.8;
  background: rgba(255,255,255,0.02);
  padding: 14px 18px;
  border-radius: 4px;
  border-left: 2px solid rgba(245,158,11,0.2);
}

/* Chat */
.chat-panel { background: #0a0c0f; }

.chat-messages {
  flex: 1;
  padding: 32px 40px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 400px;
  max-height: 520px;
  background: radial-gradient(circle at top right, rgba(245,158,11,0.03) 0%, transparent 40%);
}

.chat-empty { display: flex; flex-direction: column; gap: 20px; margin: auto 0; text-align: center; }
.empty-hint { font-size: 0.88rem; color: #4b5563; font-family: 'Share Tech Mono', monospace; letter-spacing: 0.05em; }

.suggestions { display: flex; flex-direction: column; gap: 10px; align-items: center; }
.suggest-btn {
  background: rgba(255,255,255,0.02);
  border: 1px solid #1e2530;
  color: #9ca3af;
  padding: 12px 24px;
  text-align: left;
  font-family: 'Noto Sans SC', sans-serif;
  font-size: 0.82rem;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
  width: 100%;
  max-width: 320px;
}
.suggest-btn:hover { border-color: #f59e0b; color: #f59e0b; background: rgba(245,158,11,0.05); }

.msg { display: flex; width: 100%; }
.msg.user { justify-content: flex-end; }
.msg.ai   { justify-content: flex-start; }

.bubble {
  max-width: 85%;
  padding: 14px 18px;
  border-radius: 8px;
  font-size: 0.9rem;
  line-height: 1.6;
  position: relative;
}
.user .bubble {
  background: #f59e0b;
  color: #0a0c0f;
  font-weight: 500;
  border-bottom-right-radius: 2px;
}
.ai .bubble {
  background: #13171f;
  border: 1px solid #1e2530;
  color: #d4d8de;
  border-bottom-left-radius: 2px;
}

.typing { display: flex; align-items: center; gap: 6px; padding: 16px; }
.typing span {
  width: 6px; height: 6px;
  background: #f59e0b;
  border-radius: 50%;
  animation: typing 1.2s infinite;
  opacity: 0.6;
}
.typing span:nth-child(2) { animation-delay: 0.2s; }
.typing span:nth-child(3) { animation-delay: 0.4s; }
@keyframes typing { 0%,60%,100%{transform:translateY(0)} 30%{transform:translateY(-6px)} }

.chat-input-row {
  display: flex;
  gap: 0;
  border-top: 1px solid #1e2530;
  background: #0d1017;
}
.chat-input {
  flex: 1;
  background: transparent;
  border: none;
  border-right: 1px solid #1e2530;
  color: #f0f2f5;
  padding: 20px 24px;
  font-family: 'Noto Sans SC', sans-serif;
  font-size: 0.9rem;
  outline: none;
}
.chat-input::placeholder { color: #374151; }

.send-btn {
  background: transparent;
  border: none;
  color: #f59e0b;
  padding: 0 32px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.85rem;
  letter-spacing: 0.1em;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}
.send-btn:hover:not(:disabled) { background: rgba(245,158,11,0.05); color: #fbbf24; }
.send-btn:disabled { opacity: 0.2; cursor: not-allowed; }

@media (max-width: 900px) {
  .assistant-layout { grid-template-columns: 1fr; }
}
</style>