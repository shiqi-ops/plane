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
              <span>AI 解读报告</span>
              <button class="copy-btn" @click="copyText(interpretation)">复制</button>
            </div>
            <div class="interp-body">{{ interpretation }}</div>
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
const interpretation = ref('')

async function handleInterpret() {
  interpretLoading.value = true
  interpretation.value = ''
  try {
    const res = await api.post('/assistant/interpret', { report: selectedReport.value })
    interpretation.value = res.data.interpretation
  } catch {
    interpretation.value = '解读失败，请检查后端连接。'
  } finally {
    interpretLoading.value = false
  }
}

function copyText(text) {
  navigator.clipboard.writeText(text)
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
    const res = await api.post('/assistant/chat', { message: content })
    messages.value.push({ id: ++msgId, role: 'ai', content: res.data.reply })
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
  border-bottom: 1px solid #9ca3af;
  overflow: hidden;
}
.ph-bg {
  position: absolute; inset: 0;
  background-image: linear-gradient(#9ca3af 1px,transparent 1px), linear-gradient(90deg,#9ca3af 1px,transparent 1px);
  background-size: 48px 48px;
  opacity: 0.2;
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
.ph-desc { color: #d4d8de; font-size: 0.88rem; margin: 0; }

.assistant-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1px;
  background: #9ca3af;
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
  border-bottom: 1px solid #9ca3af;
}
.panel-num {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.62rem;
  letter-spacing: 0.2em;
  color: #d4d8de;
  display: block;
  margin-bottom: 8px;
}
.panel-title { font-size: 1.1rem; font-weight: 700; color: #e8eaed; margin: 0 0 6px; }
.panel-desc { font-size: 0.82rem; color: #d4d8de; margin: 0; }

.panel-body {
  padding: 32px 40px;
  display: flex;
  flex-direction: column;
  gap: 20px;
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
  border: 1px solid #9ca3af;
  color: #e8eaed;
  padding: 10px 14px;
  font-family: 'Noto Sans SC', sans-serif;
  font-size: 0.88rem;
  border-radius: 2px;
  outline: none;
  transition: border 0.2s;
}
.field-select:focus { border-color: #f59e0b; }
.field-select option { background: #0d1017; }

.report-preview {
  background: #0d1017;
  border: 1px solid #9ca3af;
  border-radius: 4px;
  overflow: hidden;
}
.preview-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  border-bottom: 1px solid #9ca3af;
  font-size: 0.84rem;
}
.preview-row:last-child { border-bottom: none; }
.preview-row span:first-child { color: #d4d8de; font-family: 'Share Tech Mono', monospace; font-size: 0.72rem; letter-spacing: 0.08em; }
.preview-row span:last-child { color: #e8eaed; font-family: 'Share Tech Mono', monospace; }
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
  border: 1px solid #9ca3af;
  border-radius: 4px;
  overflow: hidden;
}
.interp-hd {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  border-bottom: 1px solid #9ca3af;
  background: #0d1017;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.72rem;
  letter-spacing: 0.1em;
  color: #f59e0b;
}
.copy-btn {
  background: none;
  border: 1px solid #d4d8de;
  color: #9ca3af;
  padding: 3px 10px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.68rem;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.2s;
}
.copy-btn:hover { border-color: #f59e0b; color: #f59e0b; }

.interp-body {
  padding: 16px;
  font-size: 0.85rem;
  color: #9ca3af;
  line-height: 1.8;
  white-space: pre-wrap;
}

/* Chat */
.chat-panel { background: #0a0c0f; }

.chat-messages {
  flex: 1;
  padding: 24px 40px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 360px;
  max-height: 480px;
}

.chat-empty { display: flex; flex-direction: column; gap: 16px; margin: auto 0; }
.empty-hint { font-size: 0.85rem; color: #d4d8de; font-family: 'Share Tech Mono', monospace; letter-spacing: 0.05em; }

.suggestions { display: flex; flex-direction: column; gap: 8px; }
.suggest-btn {
  background: #0d1017;
  border: 1px solid #9ca3af;
  color: #d4d8de;
  padding: 10px 16px;
  text-align: left;
  font-family: 'Noto Sans SC', sans-serif;
  font-size: 0.82rem;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.2s;
}
.suggest-btn:hover { border-color: #d4d8de; color: #d4d8de; }

.msg { display: flex; }
.msg.user { justify-content: flex-end; }
.msg.ai   { justify-content: flex-start; }

.bubble {
  max-width: 75%;
  padding: 10px 14px;
  border-radius: 4px;
  font-size: 0.88rem;
  line-height: 1.6;
}
.user .bubble {
  background: rgba(245,158,11,0.12);
  border: 1px solid rgba(245,158,11,0.2);
  color: #e8eaed;
}
.ai .bubble {
  background: #0d1017;
  border: 1px solid #9ca3af;
  color: #9ca3af;
}

.typing { display: flex; align-items: center; gap: 4px; padding: 14px; }
.typing span {
  width: 6px; height: 6px;
  background: #d4d8de;
  border-radius: 50%;
  animation: typing 1.2s infinite;
}
.typing span:nth-child(2) { animation-delay: 0.2s; }
.typing span:nth-child(3) { animation-delay: 0.4s; }
@keyframes typing { 0%,60%,100%{transform:translateY(0)} 30%{transform:translateY(-6px)} }

.chat-input-row {
  display: flex;
  gap: 0;
  border-top: 1px solid #9ca3af;
}
.chat-input {
  flex: 1;
  background: #0d1017;
  border: none;
  border-right: 1px solid #9ca3af;
  color: #e8eaed;
  padding: 16px 20px;
  font-family: 'Noto Sans SC', sans-serif;
  font-size: 0.88rem;
  outline: none;
}
.chat-input::placeholder { color: #d4d8de; }

.send-btn {
  background: #0d1017;
  border: none;
  color: #f59e0b;
  padding: 0 28px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.78rem;
  letter-spacing: 0.1em;
  cursor: pointer;
  transition: background 0.2s;
}
.send-btn:hover:not(:disabled) { background: #13171f; }
.send-btn:disabled { opacity: 0.3; cursor: not-allowed; }

@media (max-width: 900px) {
  .assistant-layout { grid-template-columns: 1fr; }
}
</style>