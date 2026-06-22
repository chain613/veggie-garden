<template>
  <div class="aphid-game">
    <div class="game-header">
      <span>灭蚜虫</span>
      <span>剩余：{{ livingAphids }}</span>
      <span>工具：{{ selectedToolName }}</span>
    </div>

    <div class="game-area" ref="area">
      <div
        v-for="aphid in aphids"
        :key="aphid.id"
        v-show="aphid.alive"
        class="aphid"
        :style="{ left: aphid.x + 'px', top: aphid.y + 'px' }"
        @click="handleAphidClick(aphid)"
      >
        🐛
      </div>

      <div
        v-if="spraying"
        class="spray-effect"
        :style="{ left: sprayX - 25 + 'px', top: sprayY - 25 + 'px' }"
      >
        💨
      </div>
    </div>

    <div class="toolbar">
      <button
        v-for="t in tools"
        :key="t.key"
        :class="{ active: selectedTool === t.key }"
        @click="selectedTool = t.key"
      >
        {{ t.icon }} {{ t.name }}
      </button>
    </div>

    <div class="game-info">
      <p>知识：蚜虫繁殖极快，发现后要尽早清除。未清干净的蚜虫会继续滋生。</p>
    </div>

    <button class="finish-btn" @click="finish">完成灭虫</button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const emit = defineEmits(['result'])

const aphids = ref([])
const selectedTool = ref('baking_soda')
const spraying = ref(false)
const sprayX = ref(0)
const sprayY = ref(0)
const squashedCount = ref(0)

const tools = [
  { key: 'baking_soda', name: '小苏打水', icon: '🧂', area: 30 },
  { key: 'garlic_water', name: '大蒜水', icon: '🧄', area: 25 },
  { key: 'hand', name: '手捉', icon: '✋', area: 10 }
]

const selectedToolName = computed(() => {
  return tools.find(t => t.key === selectedTool.value)?.name || ''
})

const livingAphids = computed(() => aphids.value.filter(a => a.alive).length)

let moveTimer = null

onMounted(() => {
  spawnAphids(10)
  moveTimer = setInterval(() => {
    aphids.value.forEach(a => {
      if (!a.alive) return
      a.x += (Math.random() - 0.5) * 8
      a.y += (Math.random() - 0.5) * 8
      a.x = Math.max(0, Math.min(350, a.x))
      a.y = Math.max(0, Math.min(450, a.y))
    })
  }, 1500)
})

function spawnAphids(count) {
  for (let i = 0; i < count; i++) {
    aphids.value.push({
      id: Date.now() + i,
      x: Math.random() * 300 + 10,
      y: Math.random() * 400 + 10,
      alive: true
    })
  }
}

function handleAphidClick(aphid) {
  if (!aphid.alive) return

  if (selectedTool.value === 'hand') {
    aphid.alive = false
    squashedCount.value++
  } else {
    spraying.value = true
    sprayX.value = aphid.x
    sprayY.value = aphid.y
    setTimeout(() => { spraying.value = false }, 300)

    const tool = tools.find(t => t.key === selectedTool.value)
    const range = tool ? tool.area : 20
    aphids.value.forEach(a => {
      if (!a.alive) return
      const dist = Math.sqrt((a.x - aphid.x) ** 2 + (a.y - aphid.y) ** 2)
      if (dist <= range) {
        a.alive = false
        squashedCount.value++
      }
    })
  }

  // 未清干净按滋生率 1.3-2.0 复发
  const remaining = aphids.value.filter(a => a.alive).length
  if (remaining > 0 && remaining < 5) {
    const rate = 1.3 + Math.random() * 0.7
    const newCount = Math.floor(remaining * rate)
    if (newCount > 0) {
      setTimeout(() => spawnAphids(newCount), 2000)
    }
  }
}

function finish() {
  if (moveTimer) clearInterval(moveTimer)
  const remaining = aphids.value.filter(a => a.alive).length
  const score = remaining === 0 ? '完美清除！' : remaining <= 2 ? '基本清除' : '还需继续'
  emit('result', {
    success: remaining === 0,
    remaining,
    score,
    squashed: squashedCount.value
  })
}
</script>

<style scoped>
.aphid-game {
  position: relative; height: 100vh;
  background: linear-gradient(180deg, #87CEEB, #c9e8c2);
}
.game-header {
  display: flex; justify-content: space-between;
  padding: 10px 20px; background: rgba(0,0,0,0.1);
}
.game-area {
  position: relative; width: 380px; height: 500px;
  margin: 20px auto; background: rgba(255,255,255,0.4);
  border-radius: 12px; overflow: hidden;
  border: 2px solid #7cbd6e;
}
.aphid {
  position: absolute; font-size: 24px; cursor: pointer;
  transition: left 0.3s, top 0.3s;
  user-select: none; z-index: 10;
}
.aphid:hover { transform: scale(1.3); }
.spray-effect {
  position: absolute; font-size: 40px;
  pointer-events: none; z-index: 20;
}
.toolbar {
  display: flex; justify-content: center; gap: 12px; margin: 12px;
}
.toolbar button {
  padding: 8px 16px; border-radius: 8px;
  border: 2px solid #aaa; background: white; cursor: pointer;
}
.toolbar button.active {
  border-color: #5c4033; background: #5c4033; color: white;
}
.game-info {
  padding: 12px 20px; font-size: 14px; color: #333;
}
.finish-btn {
  display: block; width: 200px; margin: 10px auto;
  padding: 12px; background: #5c4033; color: white;
  border: none; border-radius: 8px; font-size: 16px; cursor: pointer;
}
</style>
