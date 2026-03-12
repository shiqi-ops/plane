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
        <span class="page-num">03</span>
        <h1 class="page-title">自定义模型接入</h1>
        <p class="page-desc">上传你自己训练的 .pth 模型，选择攻击方式，自动生成完整评测报告</p>
      </div>

      <div class="form-sections">

        <!-- 上传模型 -->
        <div class="form-section">
          <h2 class="section-title">上传模型文件</h2>
          <div
            class="upload-zone"
            :class="{ 'upload-zone--active': isDragging, 'upload-zone--done': selectedFile }"
            @dragover.prevent="isDragging = true"
            @dragleave="isDragging = false"
            @drop.prevent="handleDrop"
            @click="fileInput.click()"
          >
            <input ref="fileInput" type="file" accept=".pth" hidden @change="handleFileChange" />
            <div v-if="!selectedFile" class="upload-placeholder">
              <span class="upload-icon">⬡</span>
              <p>点击或拖拽上传 <strong>.pth</strong> 文件</p>
              <p class="upload-hint">仅支持 PyTorch 模型文件</p>
            </div>
            <div v-else class="upload-done">
              <span class="done-icon">✓</span>
              <p class="file-name">{{ selectedFile.name }}</p>
              <p class="file-size">{{ (selectedFile.size / 1024 / 1024).toFixed(2) }} MB</p>
              <button class="clear-btn" @click.stop="clearFile">重新选择</button>
            </div>
          </div>
        </div>

        <!-- 选攻击 -->
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

      <!-- 结果 -->
      <div v-if="result" class="result-box">
        <h2 class="result-title">评测结果</h2>
        <div class="result-grid">
          <div class="result-item">
            <span class="result-label">攻击方式</span>
            <span class="result-value">{{ result.attack }}</span>
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

      <!-- 功能三未测试提示 -->
      <div class="notice">
        <span class="notice-icon">⚠</span>
        功能三目前处于开发阶段，上传路径依赖后端联调完成后才能完整测试
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/index.js'

const router = useRouter()

const fileInput = ref(null)
const selectedFile = ref(null)
const isDragging = ref(false)
const loading = ref(false)
const result = ref(null)

const form = ref({ attack: '', eps: 0.03 })

const attackGroups = [
  { label: '快速攻击', attacks: ['FGSM', 'RFGSM', 'FFGSM'] },
  { label: '迭代攻击', attacks: ['BIM', 'PGD', 'PGDL2'] },
  { label: '动量攻击', attacks: ['MIFGSM', 'NIFGSM'] },
  { label: '输入变换攻击', attacks: ['DIFGSM', 'TIFGSM'] },
  { label: '优化攻击', attacks: ['CW', 'DeepFool'] },
  { label: '黑盒攻击', attacks: ['Square Attack'] },
  { label: '综合攻击', attacks: ['AutoAttack'] },
]

const canSubmit = computed(() => selectedFile.value && form.value.attack)

function handleFileChange(e) {
  selectedFile.value = e.target.files[0] || null
}

function handleDrop(e) {
  isDragging.value = false
  const file = e.dataTransfer.files[0]
  if (file?.name.endsWith('.pth')) {
    selectedFile.value = file
  } else {
    alert('请上传 .pth 格式文件')
  }
}

function clearFile() {
  selectedFile.value = null
  fileInput.value.value = ''
}

async function handleSubmit() {
  loading.value = true
  result.value = null
  try {
    const formData = new FormData()
    formData.append('model_file', selectedFile.value)
    formData.append('attack', form.value.attack)
    formData.append('dataset', 'drone_dataset')
    formData.append('eps', String(form.value.eps))

    const res = await api.post('/evaluate/own', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
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
.brand-icon { color: #f43f5e; font-size: 1.4rem; }
.brand-name em { font-style: normal; color: #f43f5e; }

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
.back-btn:hover { border-color: #f43f5e; color: #f43f5e; }

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
  color: #f43f5e;
  text-transform: uppercase;
  margin-bottom: 16px;
}

/* 上传区 */
.upload-zone {
  border: 1.5px dashed #1e2530;
  border-radius: 6px;
  padding: 48px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  background: #0d1017;
}
.upload-zone:hover, .upload-zone--active {
  border-color: #f43f5e;
  background: #130d0e;
}
.upload-zone--done { border-style: solid; border-color: #f43f5e; }

.upload-icon { font-size: 2rem; color: #374151; display: block; margin-bottom: 12px; }
.upload-placeholder p { color: #6b7280; font-size: 0.9rem; margin: 4px 0; }
.upload-placeholder strong { color: #e8eaed; }
.upload-hint { font-size: 0.78rem !important; color: #374151 !important; }

.upload-done { display: flex; flex-direction: column; align-items: center; gap: 8px; }
.done-icon { font-size: 2rem; color: #f43f5e; }
.file-name { font-family: 'Share Tech Mono', monospace; font-size: 0.9rem; color: #e8eaed; }
.file-size { font-size: 0.8rem; color: #6b7280; }
.clear-btn {
  margin-top: 8px;
  background: none;
  border: 1px solid #374151;
  color: #9ca3af;
  padding: 4px 12px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.72rem;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.2s;
}
.clear-btn:hover { border-color: #f43f5e; color: #f43f5e; }

/* 攻击选择 */
.attack-grid { display: flex; flex-direction: column; gap: 20px; }
.attack-group {}
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
.atk-chip.active { border-color: #f43f5e; color: #f43f5e; background: #130d0e; }

/* eps */
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
.eps-input:focus { border-color: #f43f5e; }
.eps-hint { font-size: 0.8rem; color: #374151; font-family: 'Share Tech Mono', monospace; }

/* 提交按钮 */
.submit-btn {
  margin-top: 40px;
  display: flex;
  align-items: center;
  gap: 10px;
  background: #f43f5e;
  color: #fff;
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
.submit-btn:hover:not(:disabled) { background: #fb7185; }
.submit-btn:disabled { opacity: 0.4; cursor: not-allowed; }

.loading-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: #fff;
  animation: pulse 1s infinite;
}
@keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.3; } }

/* 结果 */
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
  color: #f43f5e;
  padding: 16px 24px;
  border-bottom: 1px solid #1e2530;
  background: #0d1017;
}
.result-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
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
.result-value.warn { color: #f59e0b; }
.result-value.danger { color: #f43f5e; }
.level-A { color: #22c55e; }
.level-B { color: #06b6d4; }
.level-C { color: #f59e0b; }
.level-D, .level-Poor { color: #f43f5e; }

/* 提示 */
.notice {
  margin-top: 32px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  border: 1px solid #2a1f14;
  background: #13100a;
  border-radius: 4px;
  font-size: 0.82rem;
  color: #78716c;
}
.notice-icon { color: #f59e0b; font-size: 1rem; }

@media (max-width: 768px) {
  .content { padding: 32px 24px; }
  .navbar { padding: 18px 24px; }
  .result-grid { grid-template-columns: repeat(2, 1fr); }
  .upload-zone { padding: 32px 24px; }
}
</style>