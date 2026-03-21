<template>
  <div class="algorithm-page">
    <!-- 背景层 -->
    <div class="bg-layer">
      <div class="bg-grid"></div>
      <div class="bg-glow"></div>
    </div>

    <div class="content-wrap">
      <!-- 页面标题区 -->
      <header class="page-header">
        <div class="header-badge">TECHNICAL SPECIFICATION</div>
        <h1 class="page-title">算法与模型介绍</h1>
        <p class="page-subtitle">无人机视觉模型鲁棒性检测 · 技术底座</p>
      </header>

      <!-- 1. 内置模型板块 -->
      <section class="section">
        <div class="sec-header">
          <h2 class="sec-title">内置模型板块</h2>
          <p class="sec-desc">精选适用于无人机视觉场景的轻量化/高精度模型，覆盖不同算力与精度需求</p>
        </div>
        <div class="accordion-list">
          <div v-for="m in models" :key="m.name" 
               class="accordion-item" :class="{ active: expandedId === m.name }">
            <div class="accordion-header" @click="toggleExpand(m.name)">
              <div class="ah-left">
                <span class="ah-icon">◈</span>
                <span class="ah-title">{{ m.name }}</span>
                <span class="ah-tag">{{ m.pos }}</span>
              </div>
              <div class="ah-right">
                <span class="ah-arrow"></span>
              </div>
            </div>
            <div class="accordion-content">
              <div class="ac-inner">
                <div class="ac-grid">
                  <div class="ac-item">
                    <span class="ac-label">核心参数</span>
                    <p class="ac-val">{{ m.params }}</p>
                  </div>
                  <div class="ac-item">
                    <span class="ac-label">适用场景</span>
                    <p class="ac-val">{{ m.scenarios }}</p>
                  </div>
                  <div class="ac-item">
                    <span class="ac-label">核心优势</span>
                    <p class="ac-val">{{ m.advantage }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 2. 攻击方法板块 -->
      <section class="section">
        <div class="sec-header">
          <h2 class="sec-title">攻击方法板块</h2>
          <p class="sec-desc">覆盖主流对抗攻击算法，从快速迭代到优化攻击，全面评估模型鲁棒性</p>
        </div>
        
        <div v-for="(group, category) in attackGroups" :key="category" class="attack-category">
          <h3 class="category-title">{{ group.label }}</h3>
          <div class="accordion-list">
            <div v-for="a in group.list" :key="a.name" 
                 class="accordion-item" :class="{ active: expandedId === a.name }">
              <div class="accordion-header" @click="toggleExpand(a.name)">
                <div class="ah-left">
                  <span class="ah-icon">◈</span>
                  <span class="ah-title">{{ a.name }}</span>
                  <span class="ah-tag">{{ group.label }}</span>
                </div>
                <div class="ah-right">
                  <span class="ah-arrow"></span>
                </div>
              </div>
              <div class="accordion-content">
                <div class="ac-inner">
                  <div class="ac-grid">
                    <div class="ac-item">
                      <span class="ac-label">原理简介</span>
                      <p class="ac-val">{{ a.principle }}</p>
                    </div>
                    <div class="ac-item">
                      <span class="ac-label">实现原理</span>
                      <p class="ac-val">{{ a.detail_principle }}</p>
                    </div>
                    <div class="ac-item">
                      <span class="ac-label">算法特点</span>
                      <p class="ac-val">{{ a.features }}</p>
                    </div>
                    <div class="ac-item">
                      <span class="ac-label">扰动效果</span>
                      <p class="ac-val">{{ a.effect }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 3. 攻击组合说明板块 -->
      <section class="section">
        <div class="sec-header">
          <h2 class="sec-title">攻击组合说明板块</h2>
          <p class="sec-desc">针对不同评测需求预设 4 类攻击组合，平衡效率、全面性与实用性</p>
        </div>
        <div class="accordion-list">
          <div v-for="c in combinations" :key="c.name" 
               class="accordion-item" :class="{ active: expandedId === c.name }">
            <div class="accordion-header" @click="toggleExpand(c.name)">
              <div class="ah-left">
                <span class="ah-icon">◈</span>
                <span class="ah-title">{{ c.name }}</span>
                <span class="ah-tag">COMBO</span>
              </div>
              <div class="ah-right">
                <span class="ah-arrow"></span>
              </div>
            </div>
            <div class="accordion-content">
              <div class="ac-inner">
                <div class="ac-grid">
                  <div class="ac-item">
                    <span class="ac-label">组合定位</span>
                    <p class="ac-val">{{ c.pos }}</p>
                  </div>
                  <div class="ac-item">
                    <span class="ac-label">包含攻击</span>
                    <p class="ac-val">{{ c.attacks }}</p>
                  </div>
                  <div class="ac-item">
                    <span class="ac-label">适用场景</span>
                    <p class="ac-val">{{ c.scenarios }}</p>
                  </div>
                  <div class="ac-item">
                    <span class="ac-label">组合逻辑</span>
                    <p class="ac-val">{{ c.logic }}</p>
                  </div>
                  <div class="ac-item">
                    <span class="ac-label">优劣势</span>
                    <p class="ac-val">{{ c.pros_cons }}</p>
                  </div>
                  <div class="ac-item">
                    <span class="ac-label">推荐参数</span>
                    <p class="ac-val">{{ c.params }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const expandedId = ref(null)

function toggleExpand(id) {
  expandedId.value = expandedId.value === id ? null : id
}

const models = [
  {
    name: 'ResNet18',
    pos: '基准模型（baseline）',
    features: '结构简洁、性能稳定，适合作为鲁棒性测试基线',
    params: '参数量 11.7M，推理速度 32 FPS，ImageNet Top-1 准确率 70.7%',
    scenarios: '无人机视觉任务 baseline 测试、通用场景鲁棒性评估',
    advantage: '训练稳定、易于部署，是对抗攻击研究的标准基线模型'
  },
  {
    name: 'MobileNetV2',
    pos: '轻量型模型',
    features: '极致轻量化，适配低算力边缘设备',
    params: '参数量 2.3M，推理速度 45 FPS，ImageNet Top-1 准确率 72.0%',
    scenarios: '小型无人机、便携设备、算力受限的边缘端部署',
    advantage: '体积小、速度快，在资源有限环境下仍能保持较好识别精度'
  },
  {
    name: 'EfficientNet-b0',
    pos: '高精度模型',
    features: '平衡精度与效率，在轻量化基础上实现更高准确率',
    params: '参数量 5.3M，推理速度 28 FPS，ImageNet Top-1 准确率 77.1%',
    scenarios: '对精度要求较高的无人机视觉任务（如电力巡检、目标识别）',
    advantage: '通过复合缩放优化模型结构，在参数量可控的前提下显著提升精度'
  }
]

const attackGroups = {
  fast: {
    label: '快速攻击类',
    list: [
      {
        name: 'FGSM',
        principle: '沿梯度方向添加微小扰动',
        detail_principle: '通过损失函数梯度计算扰动方向，一步生成对抗样本',
        features: '速度极快、扰动微小、可迁移性强',
        effect: '肉眼几乎不可察的噪点，高效突破模型识别'
      },
      {
        name: 'RFGSM',
        principle: '随机初始化扰动后执行 FGSM',
        detail_principle: '在 FGSM 基础上增加随机扰动初始化，提升攻击泛化性',
        features: '比 FGSM 更稳定，对抗样本迁移性更强',
        effect: '轻微噪点，适合跨模型泛化测试'
      },
      {
        name: 'FFGSM',
        principle: '聚焦梯度方向的快速攻击',
        detail_principle: '优化梯度计算方式，减少冗余计算，提升攻击效率',
        features: '速度快、扰动可控，适合批量快速评测',
        effect: '低噪点扰动，兼顾隐蔽性与攻击效果'
      }
    ]
  },
  iterative: {
    label: '迭代攻击类',
    list: [
      {
        name: 'BIM',
        principle: '多步小梯度更新扰动',
        detail_principle: '将 FGSM 拆分为多步迭代，逐步累积扰动',
        features: '攻击成功率更高，鲁棒性评估更全面',
        effect: '中等强度扰动，视觉可辨，适合深度评测'
      },
      {
        name: 'PGD',
        principle: '带投影约束的迭代攻击',
        detail_principle: '在 BIM 基础上增加扰动范围约束，保证扰动可控',
        features: '当前最常用的鲁棒性评估基准攻击，结果稳定可靠',
        effect: '中等强度扰动，是标准鲁棒性测试的首选方法'
      },
      {
        name: 'PGDL2',
        principle: '基于 L2 范数约束的 PGD 变体',
        detail_principle: '使用 L2 范数限制扰动幅度，更贴近真实视觉干扰',
        features: '扰动更平滑，适合模拟真实环境噪声',
        effect: '柔和的视觉扰动，更贴近实际场景干扰'
      }
    ]
  },
  momentum: {
    label: '动量攻击类',
    list: [
      {
        name: 'MIFGSM',
        principle: '引入动量累积梯度方向',
        detail_principle: '在迭代过程中累积梯度动量，提升攻击稳定性与迁移性',
        features: '对抗样本跨模型泛化能力强，适合黑盒攻击场景',
        effect: '中等强度扰动，迁移攻击效果显著'
      },
      {
        name: 'NIFGSM',
        principle: '基于梯度归一化的动量攻击',
        detail_principle: '对梯度进行归一化处理，增强梯度方向稳定性',
        features: '攻击更稳定，对不同模型结构适应性更强',
        effect: '温和扰动，适合跨模型泛化测试'
      }
    ]
  },
  input: {
    label: '输入变换攻击类',
    list: [
      {
        name: 'DIFGSM',
        principle: '结合输入多样性提升迁移性',
        detail_principle: '对输入图像进行随机变换后计算梯度，增强攻击泛化性',
        features: '跨模型迁移能力极强，适合黑盒鲁棒性测试',
        effect: '轻微扰动，在未知模型上仍能保持高攻击成功率'
      },
      {
        name: 'TIFGSM',
        principle: '结合平移变换的动量攻击',
        detail_principle: '引入图像平移变换，模拟真实场景中的视角变化',
        features: '更贴近无人机实际飞行中的视觉干扰，测试结果更具实用价值',
        effect: '结合平移与噪点，模拟真实环境干扰'
      }
    ]
  },
  optimization: {
    label: '优化攻击类',
    list: [
      {
        name: 'CW',
        principle: '基于优化的最小扰动攻击',
        detail_principle: '通过优化目标函数，最小化扰动同时最大化模型误分类',
        features: '扰动极小、隐蔽性极强，攻击效果稳定',
        effect: '几乎不可察的精细扰动，适合高隐蔽性攻击测试'
      },
      {
        name: 'DeepFool',
        principle: '寻找最小扰动使样本跨越分类边界',
        detail_principle: '迭代计算样本到分类边界的最小距离，生成最优扰动',
        features: '扰动极小、理论最优，是评估模型鲁棒性下界的标准方法',
        effect: '极轻微扰动，高效突破模型识别'
      }
    ]
  },
  blackbox: {
    label: '黑盒/综合攻击类',
    list: [
      {
        name: 'Square Attack',
        principle: '基于随机搜索的无梯度黑盒攻击',
        detail_principle: '无需梯度信息，通过随机方块扰动搜索最优攻击',
        features: '完全黑盒场景适用，不依赖模型内部信息',
        effect: '方块状局部扰动，隐蔽性中等，适合真实黑盒环境'
      },
      {
        name: 'AutoAttack',
        principle: '自动组合多种攻击的鲁棒性评估工具',
        detail_principle: '自动集成多种强攻击，全面评估模型鲁棒性',
        features: '无需手动选择攻击，自动完成全面评测，结果权威可靠',
        effect: '组合多种扰动类型，是当前最严格的鲁棒性评估标准'
      }
    ]
  }
}

const combinations = [
  {
    name: '① 快速攻击组合',
    pos: '效率优先，快速初筛',
    attacks: 'FGSM、RFGSM、FFGSM',
    scenarios: '模型快速迭代、批量测试、初步鲁棒性筛查',
    logic: '以快速攻击为核心，在保证基本攻击效果的前提下最大化评测效率',
    pros_cons: '✅ 速度极快，适合批量测试；❌ 评估不够全面，仅能反映基础鲁棒性',
    params: 'eps=0.03，迭代步数=1'
  },
  {
    name: '② 标准鲁棒性攻击组合',
    pos: '平衡效率与全面性',
    attacks: 'BIM、PGD、PGDL2',
    scenarios: '常规鲁棒性评估、学术论文实验、正式评测',
    logic: '以迭代攻击为核心，全面覆盖不同扰动约束与迭代方式',
    pros_cons: '✅ 全面可靠，结果稳定；❌ 耗时中等，不适合超大规模批量测试',
    params: 'eps=0.03，迭代步数=10'
  },
  {
    name: '③ 迁移攻击组合',
    pos: '侧重跨模型泛化性',
    attacks: 'MIFGSM、NIFGSM、DIFGSM',
    scenarios: '黑盒攻击测试、模型泛化能力评估、跨设备鲁棒性验证',
    logic: '以动量/输入变换攻击为核心，重点提升对抗样本跨模型迁移能力',
    pros_cons: '✅ 跨模型迁移性极强，适合黑盒场景；❌ 针对性稍弱，对单模型攻击效率略低',
    params: 'eps=0.03，迭代步数=10'
  },
  {
    name: '④ 强攻击组合',
    pos: '极限压力测试',
    attacks: 'CW、DeepFool、AutoAttack',
    scenarios: '高安全需求场景、极限鲁棒性测试、模型抗干扰能力验证',
    logic: '以优化攻击与综合攻击为核心，全面挖掘模型鲁棒性极限',
    pros_cons: '✅ 攻击强度极大，能暴露模型最薄弱环节；❌ 耗时久、扰动明显，不适合快速测试',
    params: 'eps=0.03，AutoAttack 自动配置'
  }
]
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=Noto+Sans+SC:wght@400;700&display=swap');

.algorithm-page {
  min-height: calc(100vh - 64px);
  background: #05070a;
  color: #d4d8de;
  font-family: 'Noto Sans SC', sans-serif;
  position: relative;
  overflow-x: hidden;
}

/* ── 背景 ── */
.bg-layer { position: absolute; inset: 0; pointer-events: none; }
.bg-grid {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(rgba(245, 158, 11, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(245, 158, 11, 0.05) 1px, transparent 1px);
  background-size: 50px 50px;
}
.bg-glow {
  position: absolute; top: 20%; left: 50%;
  width: 1000px; height: 1000px;
  transform: translateX(-50%);
  background: radial-gradient(circle, rgba(245, 158, 11, 0.03) 0%, transparent 70%);
}

.content-wrap {
  position: relative;
  z-index: 10;
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 24px 100px;
}

/* ── 标题区 ── */
.page-header { text-align: center; margin-bottom: 80px; }
.header-badge {
  display: inline-block;
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.7rem;
  color: #f59e0b;
  border: 1px solid rgba(245, 158, 11, 0.3);
  padding: 4px 12px;
  letter-spacing: 0.2em;
  margin-bottom: 16px;
}
.page-title {
  font-size: 2.8rem;
  font-weight: 700;
  color: #f0f2f5;
  margin: 0 0 12px;
}
.page-subtitle {
  font-size: 1.1rem;
  color: #6b7280;
  letter-spacing: 0.05em;
}

/* ── 板块通用 ── */
.section { margin-bottom: 100px; }
.sec-header { margin-bottom: 40px; border-left: 4px solid #f59e0b; padding-left: 20px; }
.sec-title { font-size: 1.8rem; color: #f0f2f5; margin-bottom: 8px; }
.sec-desc { font-size: 0.95rem; color: #6b7280; }

.attack-category { margin-top: 48px; }
.category-title {
  font-size: 1.1rem;
  color: #f59e0b;
  font-family: 'Share Tech Mono', monospace;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.category-title::after { content: ''; flex: 1; height: 1px; background: rgba(245, 158, 11, 0.1); }

/* ── 手风琴布局 ── */
.accordion-list {
  display: flex;
  flex-direction: column;
  gap: 1px;
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.1);
  border-radius: 4px;
  overflow: hidden;
}

.accordion-item {
  background: rgba(13, 16, 23, 0.6);
  transition: all 0.3s ease;
}

.accordion-header {
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: background 0.3s;
}
.accordion-header:hover { background: rgba(245, 158, 11, 0.05); }

.ah-left { display: flex; align-items: center; gap: 20px; }
.ah-icon { font-size: 1.4rem; color: #f59e0b; width: 24px; text-align: center; }
.ah-title { font-size: 1.2rem; font-weight: 700; color: #f0f2f5; min-width: 140px; }
.ah-tag {
  font-family: 'Share Tech Mono', monospace;
  font-size: 0.65rem;
  color: #4b5563;
  border: 1px solid rgba(75, 85, 99, 0.3);
  padding: 2px 10px;
  letter-spacing: 0.05em;
}

.ah-arrow {
  width: 12px; height: 12px;
  border-right: 2px solid #4b5563;
  border-bottom: 2px solid #4b5563;
  transform: rotate(45deg);
  transition: all 0.3s;
  margin-right: 10px;
}

/* 激活状态 */
.accordion-item.active { background: rgba(13, 16, 23, 0.9); }
.accordion-item.active .accordion-header { border-bottom: 1px solid rgba(245, 158, 11, 0.1); }
.accordion-item.active .ah-arrow { transform: rotate(-135deg); border-color: #f59e0b; }
.accordion-item.active .ah-title { color: #f59e0b; }

/* 内容区域 */
.accordion-content {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}
.accordion-item.active .accordion-content { max-height: 800px; }

.ac-inner { padding: 32px 32px 48px 76px; }

.ac-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
}

.ac-item { display: flex; flex-direction: column; gap: 12px; }
.ac-label { font-size: 0.75rem; color: #f59e0b; font-weight: 700; letter-spacing: 0.1em; text-transform: uppercase; }
.ac-val { font-size: 0.9rem; color: #9ca3af; line-height: 1.8; margin: 0; }

/* 响应式 */
@media (max-width: 1024px) {
  .ac-grid { grid-template-columns: repeat(2, 1fr); gap: 30px; }
}
@media (max-width: 768px) {
  .ah-left { gap: 12px; flex-wrap: wrap; }
  .ah-title { min-width: auto; }
  .ac-inner { padding: 24px; }
  .ac-grid { grid-template-columns: 1fr; gap: 20px; }
  .page-title { font-size: 2rem; }
}
</style>
