<template>
  <div ref="zoneRef" class="joystick-zone">
    <div class="joystick-base">
      <div class="joystick-stick" :style="stickStyle"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const emit = defineEmits(['move'])
const zoneRef = ref(null)
const dx = ref(0), dy = ref(0)
const activeId = ref(-1)

const stickStyle = computed(() => ({
  transform: `translate(${dx.value * 30}px, ${dy.value * 30}px)`
}))

function onPointerDown(e) {
  activeId.value = e.pointerId
  zoneRef.value.setPointerCapture(e.pointerId)
  updateJoystick(e)
}

function onPointerMove(e) {
  if (activeId.value === -1) return
  updateJoystick(e)
}

function onPointerUp(e) {
  activeId.value = -1
  dx.value = 0; dy.value = 0
  emit('move', 0, 0)
}

function updateJoystick(e) {
  const rect = zoneRef.value.getBoundingClientRect()
  const cx = rect.left + rect.width / 2
  const cy = rect.top + rect.height / 2
  dx.value = Math.max(-1, Math.min(1, (e.clientX - cx) / 50))
  dy.value = Math.max(-1, Math.min(1, (e.clientY - cy) / 50))
  emit('move', dx.value, -dy.value)
}

onMounted(() => {
  const el = zoneRef.value
  el.addEventListener('pointerdown', onPointerDown)
  el.addEventListener('pointermove', onPointerMove)
  el.addEventListener('pointerup', onPointerUp)
  el.addEventListener('pointercancel', onPointerUp)
})

onUnmounted(() => {
  const el = zoneRef.value
  if (el) {
    el.removeEventListener('pointerdown', onPointerDown)
    el.removeEventListener('pointermove', onPointerMove)
    el.removeEventListener('pointerup', onPointerUp)
    el.removeEventListener('pointercancel', onPointerUp)
  }
})
</script>

<style scoped>
.joystick-zone {
  position: fixed; bottom: 80px; right: 40px;
  width: 120px; height: 120px; touch-action: none; z-index: 600;
}
.joystick-base {
  width: 100%; height: 100%; border-radius: 50%;
  background: rgba(255,255,255,0.3); border: 2px solid rgba(255,255,255,0.5);
  display: flex; align-items: center; justify-content: center;
}
.joystick-stick {
  width: 50px; height: 50px; border-radius: 50%;
  background: rgba(255,255,255,0.7);
  transition: transform 0.05s linear;
}
</style>
