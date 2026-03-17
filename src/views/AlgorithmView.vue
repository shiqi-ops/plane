<template>
  <div class="coming-soon">
    <div class="cs-icon">◈</div>
    <h1>算法与模型介绍</h1>
    <p>该模块正在建设中，敬请期待</p>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=Noto+Sans+SC:wght@400;700&display=swap');
.coming-soon {
  min-height: calc(100vh - 58px);
  background: #0a0c0f;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  font-family: 'Noto Sans SC', sans-serif;
}
.cs-icon { font-size: 4rem; color: #9ca3af; }
h1 { font-size: 1.4rem; color: #d4d8de; margin: 0; }
p  { font-size: 0.85rem; color: #9ca3af; font-family: 'Share Tech Mono', monospace; letter-spacing: 0.1em; margin: 0; }
</style>