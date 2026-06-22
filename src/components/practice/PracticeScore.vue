<template>
  <div class="practice-score">
    <div class="score-card">
      <div class="trophy">🏆</div>
      <h2>练习完成！</h2>

      <div class="score-ring">
        <div class="score-number">{{ score }} / {{ total }}</div>
        <div class="score-label">{{ score >= total ? '完美通关！' : score >= total / 2 ? '不错！' : '继续加油' }}</div>
      </div>

      <div v-if="unlocked.length > 0" class="knowledge-section">
        <h3>📚 解锁知识条目</h3>
        <div class="knowledge-list">
          <div v-for="kid in unlocked" :key="kid" class="knowledge-item">
            <span class="knowledge-icon">📖</span>
            <span>知识 #{{ kid }}</span>
          </div>
        </div>
      </div>

      <div class="actions">
        <button class="retry-btn" @click="$emit('retry')">再练一次</button>
        <button class="back-btn" @click="$router.push('/practice')">返回副本列表</button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  score: { type: Number, default: 0 },
  total: { type: Number, default: 0 },
  unlocked: { type: Array, default: () => [] }
})

defineEmits(['retry'])
</script>

<style scoped>
.practice-score {
  padding: 20px; padding-top: 80px;
  min-height: 100vh; background: linear-gradient(180deg, #fff9c4, #fff176);
  display: flex; justify-content: center;
}
.score-card {
  background: white; border-radius: 20px; padding: 32px;
  max-width: 360px; width: 100%; text-align: center;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}
.trophy { font-size: 64px; }
h2 { margin: 8px 0 20px; }
.score-ring {
  width: 120px; height: 120px; border-radius: 50%;
  background: linear-gradient(135deg, #4caf50, #81c784);
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; margin: 0 auto 20px; color: white;
}
.score-number { font-size: 28px; font-weight: bold; }
.score-label { font-size: 12px; margin-top: 2px; }
.knowledge-section { text-align: left; margin-bottom: 20px; }
.knowledge-section h3 { font-size: 16px; margin-bottom: 8px; }
.knowledge-list { display: flex; flex-wrap: wrap; gap: 8px; }
.knowledge-item {
  background: #e8f5e9; border-radius: 8px; padding: 6px 12px;
  font-size: 14px; display: flex; align-items: center; gap: 4px;
}
.actions { display: flex; flex-direction: column; gap: 10px; }
.retry-btn {
  padding: 12px; background: #4caf50; color: white;
  border: none; border-radius: 10px; font-size: 16px; cursor: pointer;
}
.back-btn {
  padding: 12px; background: #eee; color: #333;
  border: none; border-radius: 10px; font-size: 16px; cursor: pointer;
}
</style>
