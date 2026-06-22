<template>
  <div class="garden-switcher">
    <button
      v-for="g in gardens" :key="g.id"
      :class="{ active: currentId === g.id }"
      @click="$emit('switch', g.id)"
    >
      {{ g.region === 'zhongyuan' ? '🌾' : '🌿' }} {{ g.name }}
    </button>
  </div>
</template>

<script setup>
defineProps({
  gardens: { type: Array, default: () => [] },
  currentId: { type: Number, default: 1 }
})
defineEmits(['switch'])
</script>

<style scoped>
.garden-switcher {
  position: fixed; top: 12px; left: 50%; transform: translateX(-50%);
  display: flex; gap: 8px; z-index: 120;
}
.garden-switcher button {
  padding: 6px 14px; border-radius: 20px; border: none;
  background: rgba(255,255,255,0.8); font-size: 13px; cursor: pointer;
  backdrop-filter: blur(4px);
}
.garden-switcher button.active {
  background: #5c4033; color: white;
}
</style>
