<template>
  <div class="action-bar">
    <button
      v-for="tool in tools" :key="tool.key"
      :class="{ active: selected === tool.key }"
      @click="selectTool(tool.key)"
    >
      <span class="tool-icon">{{ tool.icon }}</span>
      <span class="tool-label">{{ tool.label }}</span>
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['action'])

const selected = ref(null)

const tools = [
  { key: 'plant', icon: '🌱', label: '种植' },
  { key: 'water', icon: '💧', label: '浇水' },
  { key: 'fertilize', icon: '🧪', label: '施肥' },
  { key: 'harvest', icon: '🧺', label: '采收' },
  { key: 'roots', icon: '👁️', label: '根系' }
]

function selectTool(key) {
  if (selected.value === key) {
    selected.value = null
  } else {
    selected.value = key
  }
  emit('action', selected.value ? { tool: key } : null)
}
</script>

<style scoped>
.action-bar {
  position: fixed; top: 75px; left: 50%; transform: translateX(-50%);
  display: flex; gap: 10px; z-index: 600;
  background: rgba(255,255,255,0.85); border-radius: 16px;
  padding: 8px 16px; backdrop-filter: blur(8px);
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
.action-bar button {
  display: flex; flex-direction: column; align-items: center;
  gap: 2px; padding: 8px 12px; border: none; border-radius: 12px;
  background: transparent; cursor: pointer; transition: background 0.2s;
  min-width: 52px;
}
.action-bar button:hover { background: rgba(92,64,51,0.08); }
.action-bar button.active { background: #5c4033; }
.action-bar button.active .tool-label { color: white; }
.tool-icon { font-size: 22px; }
.tool-label { font-size: 10px; color: #666; }
</style>
