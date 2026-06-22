<template>
  <div class="weeding-game">
    <div class="game-header">
      <span>🌿 除草</span>
      <span>剩余杂草：{{ remainingWeeds }}</span>
      <span>误拔菜苗：{{ mistakes }}</span>
    </div>

    <div class="game-area" ref="area">
      <div
        v-for="item in items"
        :key="item.id"
        class="plant-item"
        :class="{ weed: item.type === 'weed', crop: item.type === 'crop', pulled: item.pulled }"
        :style="{ left: item.x + 'px', top: item.y + 'px' }"
        @pointerdown="startPull(item)"
        @pointerup="cancelPull"
        @pointerleave="cancelPull"
      >
        <span class="plant-icon">{{ item.pulled ? '✅' : (item.type === 'weed' ? '🌿' : '🌱') }}</span>
        <span class="plant-label">{{ item.name }}</span>
      </div>
    </div>

    <div v-if="showMistake" class="mistake-toast">{{ mistakeText }}</div>

    <button class="finish-btn" @click="finish">完成除草</button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '../../api'

const route = useRoute()
const gardenId = route.query.gardenId

const emit = defineEmits(['result'])

const items = ref([])
const pulledIds = ref([])
const mistakes = ref(0)
const showMistake = ref(false)
const mistakeText = ref('')
let pressTimer = null

onMounted(async () => {
  try {
    const weeds = await api.get(`/weed/garden/${gardenId}`)
    const weedData = Array.isArray(weeds.data) ? weeds.data : (weeds.data.data || [])

    const weedItems = weedData.map(w => ({
      id: w.id,
      dbId: w.id,
      type: 'weed',
      name: '杂草',
      x: Math.random() * 250 + 25,
      y: Math.random() * 350 + 25,
      pulled: false
    }))

    let seedlings = []
    try {
      const plants = await api.get(`/plant/garden/${gardenId}`)
      const plantData = Array.isArray(plants.data) ? plants.data : (plants.data.data || [])
      seedlings = plantData
        .filter(p => (p.growthStage <= 1))
        .slice(0, 4)
        .map(p => ({
          id: 'crop-' + p.id,
          type: 'crop',
          name: p.vegetableName || '菜苗',
          x: Math.random() * 250 + 25,
          y: Math.random() * 350 + 25,
          pulled: false
        }))
    } catch (e) {
      // 菜苗接口可选
    }

    items.value = [...weedItems, ...seedlings].sort(() => Math.random() - 0.5)
  } catch (e) {
    // 如果 API 不可用，生成演示数据
    items.value = Array.from({ length: 10 }, (_, i) => ({
      id: i + 1,
      dbId: i + 1,
      type: 'weed',
      name: '杂草',
      x: Math.random() * 250 + 25,
      y: Math.random() * 350 + 25,
      pulled: false
    }))
    // 混入一些菜苗
    for (let j = 0; j < 3; j++) {
      items.value.push({
        id: 'crop-' + (100 + j),
        type: 'crop',
        name: '菜苗',
        x: Math.random() * 250 + 25,
        y: Math.random() * 350 + 25,
        pulled: false
      })
    }
    items.value.sort(() => Math.random() - 0.5)
  }
})

const remainingWeeds = computed(() =>
  items.value.filter(w => w.type === 'weed' && !w.pulled).length
)

function startPull(item) {
  if (item.pulled) return
  pressTimer = setTimeout(() => {
    if (item.type === 'crop') {
      mistakes.value++
      showMistake.value = true
      mistakeText.value = '这是菜苗，不是杂草！'
      setTimeout(() => { showMistake.value = false }, 2000)
    } else {
      item.pulled = true
      pulledIds.value.push(item.dbId)
    }
  }, 1500)
}

function cancelPull() {
  clearTimeout(pressTimer)
}

async function settleWeeding() {
  if (pulledIds.value.length > 0) {
    try {
      await api.post('/weed/removeBatch', { weedIds: pulledIds.value })
    } catch (e) {
      // 结算失败不阻塞
    }
  }
}

function finish() {
  emit('result', {
    pulledCount: pulledIds.value.length,
    remaining: remainingWeeds.value,
    mistakes: mistakes.value
  })
}

defineExpose({ settleWeeding })
</script>

<style scoped>
.weeding-game {
  position: relative; height: 100vh;
  background: linear-gradient(180deg, #f1f8e9, #dcedc8);
}
.game-header {
  display: flex; justify-content: space-between;
  padding: 10px 20px; background: rgba(0,0,0,0.08);
}
.game-area {
  position: relative; width: 380px; height: 500px;
  margin: 20px auto; background: rgba(255,255,255,0.5);
  border-radius: 12px; overflow: hidden;
  border: 2px solid #8bc34a;
}
.plant-item {
  position: absolute; display: flex; flex-direction: column;
  align-items: center; cursor: pointer; user-select: none;
  font-size: 28px; z-index: 10;
  touch-action: none;
}
.plant-item.pulled { opacity: 0.4; pointer-events: none; }
.plant-label {
  font-size: 11px; background: rgba(0,0,0,0.6); color: white;
  padding: 1px 6px; border-radius: 4px; margin-top: 2px;
}
.mistake-toast {
  position: fixed; top: 30%; left: 50%; transform: translateX(-50%);
  background: rgba(220, 50, 50, 0.9); color: white;
  padding: 12px 24px; border-radius: 8px; font-size: 18px;
  z-index: 100; pointer-events: none;
}
.finish-btn {
  display: block; width: 200px; margin: 10px auto;
  padding: 12px; background: #689f38; color: white;
  border: none; border-radius: 8px; font-size: 16px; cursor: pointer;
}
</style>
