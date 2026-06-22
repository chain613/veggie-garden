<template>
  <div class="plant-detail-overlay" @click.self="$emit('close')">
    <div class="plant-detail">
      <div class="detail-header">
        <span class="plant-icon">{{ plant.icon || '🌱' }}</span>
        <div>
          <h3>{{ plant.name || plant.vegetableName || '植株' }}</h3>
          <span class="stage">{{ stageText }}</span>
        </div>
        <button class="close-btn" @click="$emit('close')">✕</button>
      </div>
      <div class="detail-body">
        <div class="stat-row"><span>生长阶段</span><span>{{ stageText }}</span></div>
        <div class="stat-row"><span>需水量</span><span>{{ plant.waterNeed || '中' }}</span></div>
        <div class="stat-row"><span>距收获</span><span>{{ plant.daysToHarvest ? plant.daysToHarvest + '天' : '--' }}</span></div>
      </div>
      <div class="detail-actions">
        <button @click="$emit('action','water')">💧 浇水</button>
        <button @click="$emit('action','fertilize')">🧪 施肥</button>
        <button @click="$emit('action','harvest')">🧺 采收</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  plant: { type: Object, default: () => ({}) }
})

defineEmits(['action', 'close'])

const stageText = computed(() => {
  const s = props.plant.growthStage
  if (s === undefined || s === null) return '幼苗'
  const stages = ['种子', '幼苗', '成长', '开花', '成熟']
  return stages[s] || '成长中'
})
</script>

<style scoped>
.plant-detail-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.4);
  display: flex; align-items: flex-end; justify-content: center; z-index: 280;
}
.plant-detail {
  background: white; border-radius: 16px 16px 0 0; width: 100%;
  max-width: 400px; padding: 24px 20px 32px;
}
.detail-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.plant-icon { font-size: 40px; }
.detail-header h3 { margin: 0; }
.stage { font-size: 12px; color: #888; }
.close-btn { margin-left: auto; background: none; border: none; font-size: 20px; cursor: pointer; }
.detail-body { margin-bottom: 16px; }
.stat-row {
  display: flex; justify-content: space-between; padding: 8px 0;
  border-bottom: 1px solid #f0f0f0; font-size: 14px;
}
.detail-actions { display: flex; gap: 10px; }
.detail-actions button {
  flex: 1; padding: 10px; border: none; border-radius: 10px;
  font-size: 14px; cursor: pointer; background: #f5f5f5;
}
.detail-actions button:hover { background: #e8f5e9; }
</style>
