<template>
  <div class="avatar-selector">
    <button
      v-for="a in avatars" :key="a.key"
      :class="{ active: modelValue === a.key }"
      @click="$emit('update:modelValue', a.key)"
    >
      {{ a.emoji }} {{ a.label }}
    </button>
  </div>
</template>

<script setup>
defineProps({
  modelValue: { type: String, default: 'old_farmer' }
})

defineEmits(['update:modelValue'])

const avatars = [
  { key: 'old_farmer', label: '老农', emoji: '👨‍🌾' },
  { key: 'beauty', label: '美女', emoji: '👩‍🌾' },
  { key: 'handsome', label: '帅哥', emoji: '🧑‍🌾' }
]
</script>

<style scoped>
.avatar-selector {
  position: fixed; top: 80px; right: 12px;
  display: flex; flex-direction: column; gap: 8px; z-index: 100;
}
.avatar-selector button {
  padding: 8px 12px; border-radius: 8px; border: none;
  background: rgba(255,255,255,0.8); cursor: pointer;
}
.avatar-selector button.active {
  background: #5c4033; color: white;
}
</style>
