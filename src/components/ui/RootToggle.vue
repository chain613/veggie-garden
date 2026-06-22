<template>
  <button class="root-toggle" :class="{ active: active }" @click="$emit('toggle')">
    🔍 根系
  </button>
</template>

<script setup>
defineProps({ active: Boolean })
defineEmits(['toggle'])
</script>

<style scoped>
.root-toggle {
  position: fixed; bottom: 160px; left: 40px;
  padding: 8px 16px; border-radius: 20px;
  background: rgba(255,255,255,0.8); border: none;
  font-size: 14px; cursor: pointer; z-index: 100;
}
.root-toggle.active {
  background: #5c4033; color: white;
}
</style>
