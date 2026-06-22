<template>
  <div class="seed-selector-overlay" @click.self="$emit('close')">
    <div class="seed-selector">
      <h3>🌱 选择种子</h3>
      <div class="seed-grid">
        <div v-for="s in seeds" :key="s.id" class="seed-card" @click="$emit('select', s)">
          <span class="seed-icon">{{ s.icon }}</span>
          <span class="seed-name">{{ s.name }}</span>
        </div>
      </div>
      <button class="cancel-btn" @click="$emit('close')">取消</button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useKnowledgeStore } from '../../stores/knowledge'

defineEmits(['select', 'close'])

const store = useKnowledgeStore()
const seeds = computed(() => store.vegetables.filter(v => v.unlocked))
</script>

<style scoped>
.seed-selector-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.5);
  display: flex; align-items: flex-end; justify-content: center; z-index: 300;
}
.seed-selector {
  background: white; border-radius: 16px 16px 0 0; width: 100%;
  max-width: 400px; padding: 24px 20px 32px;
}
h3 { text-align: center; margin: 0 0 16px; }
.seed-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; margin-bottom: 16px; }
.seed-card {
  display: flex; flex-direction: column; align-items: center; gap: 4px;
  padding: 12px 8px; border: 2px solid #e0e0e0; border-radius: 12px; cursor: pointer;
}
.seed-card:hover { border-color: #4caf50; background: #f1f8e9; }
.seed-icon { font-size: 28px; }
.seed-name { font-size: 12px; color: #666; }
.cancel-btn {
  display: block; width: 100%; padding: 12px; border: 1px solid #ddd;
  border-radius: 10px; background: white; font-size: 14px; cursor: pointer;
}
</style>
