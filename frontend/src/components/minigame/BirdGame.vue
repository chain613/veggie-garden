<template>
  <div class="bird-game">
    <div class="game-header">
      <span>🐦 赶鸟</span>
      <span>驱赶数：{{ score }}</span>
      <span>{{ hasScarecrow ? '稻草人已放置' : '未放置稻草人' }}</span>
    </div>

    <div class="game-area" ref="area">
      <div
        v-for="bird in birds"
        :key="bird.id"
        class="bird"
        :style="{ left: bird.x + 'px', top: bird.y + 'px' }"
        @pointerdown.prevent="swipeAtBird(bird.id)"
      >
        🐦
      </div>

      <div
        v-if="hasScarecrow"
        class="scarecrow"
        :style="{ left: scarecrowX + 'px', top: scarecrowY + 'px' }"
      >
        🧑‍🌾
      </div>

      <div v-if="hasScarecrow" class="scarecrow-range"
        :style="{ left: (scarecrowX - 80) + 'px', top: (scarecrowY - 80) + 'px' }"
      ></div>
    </div>

    <div class="toolbar">
      <button @click="placeScarecrow" :disabled="hasScarecrow">
        🧑‍🌾 放置稻草人
      </button>
    </div>

    <div class="game-info">
      <p>知识：稻草人可以持续驱赶鸟类，保护庄稼。滑动飞鸟即可驱赶。</p>
    </div>

    <button class="finish-btn" @click="finish">完成赶鸟</button>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const emit = defineEmits(['result'])

const birds = ref([])
const score = ref(0)
const hasScarecrow = ref(false)
const scarecrowX = ref(200)
const scarecrowY = ref(300)

let spawnTimer = null
let moveTimer = null

onMounted(() => {
  spawnBird()
  spawnTimer = setInterval(spawnBird, 5000)
  moveTimer = setInterval(moveBirds, 2000)
})

onUnmounted(() => {
  if (spawnTimer) clearInterval(spawnTimer)
  if (moveTimer) clearInterval(moveTimer)
})

function spawnBird() {
  if (birds.value.length >= 6) return
  birds.value.push({
    id: Date.now(),
    x: Math.random() * 300,
    y: Math.random() * 200,
    targetX: Math.random() * 300,
    targetY: Math.random() * 200
  })
}

function moveBirds() {
  birds.value.forEach(b => {
    b.targetX = Math.random() * 300
    b.targetY = Math.random() * 200
    // 稻草人范围内自动飞走
    if (hasScarecrow.value) {
      const dist = Math.sqrt((b.x - scarecrowX.value) ** 2 + (b.y - scarecrowY.value) ** 2)
      if (dist < 100) {
        b.targetX = Math.random() * 300
        b.targetY = Math.random() * 200
      }
    }
    // 平滑移动
    b.x += (b.targetX - b.x) * 0.3
    b.y += (b.targetY - b.y) * 0.3
  })

  // 稻草人持续驱鸟
  if (hasScarecrow.value) {
    birds.value = birds.value.filter(b => {
      const dist = Math.sqrt((b.x - scarecrowX.value) ** 2 + (b.y - scarecrowY.value) ** 2)
      if (dist < 80) {
        score.value++
        return false
      }
      return true
    })
  }
}

function swipeAtBird(id) {
  const idx = birds.value.findIndex(b => b.id === id)
  if (idx !== -1) {
    birds.value.splice(idx, 1)
    score.value++
  }
}

function placeScarecrow() {
  hasScarecrow.value = true
  scarecrowX.value = 100 + Math.random() * 200
  scarecrowY.value = 100 + Math.random() * 250
}

function finish() {
  if (spawnTimer) clearInterval(spawnTimer)
  if (moveTimer) clearInterval(moveTimer)
  emit('result', {
    score: score.value,
    birdsRemaining: birds.value.length,
    usedScarecrow: hasScarecrow.value
  })
}
</script>

<style scoped>
.bird-game {
  position: relative; height: 100vh;
  background: linear-gradient(180deg, #bbdefb, #e3f2fd);
}
.game-header {
  display: flex; justify-content: space-between;
  padding: 10px 20px; background: rgba(0,0,0,0.08);
}
.game-area {
  position: relative; width: 380px; height: 500px;
  margin: 20px auto; background: rgba(255,255,255,0.4);
  border-radius: 12px; overflow: hidden;
  border: 2px solid #42a5f5;
}
.bird {
  position: absolute; font-size: 28px; cursor: pointer;
  user-select: none; z-index: 10;
  transition: left 0.5s, top 0.5s;
}
.bird:hover { transform: scale(1.3); }
.scarecrow {
  position: absolute; font-size: 36px; z-index: 5;
  pointer-events: none;
}
.scarecrow-range {
  position: absolute; width: 200px; height: 200px;
  border-radius: 50%; border: 2px dashed rgba(139, 69, 19, 0.4);
  background: rgba(139, 69, 19, 0.05); pointer-events: none; z-index: 4;
}
.toolbar {
  display: flex; justify-content: center; gap: 12px; margin: 12px;
}
.toolbar button {
  padding: 8px 16px; border-radius: 8px;
  border: 2px solid #aaa; background: white; cursor: pointer;
}
.toolbar button:disabled {
  opacity: 0.5; cursor: default;
}
.game-info {
  padding: 12px 20px; font-size: 14px; color: #333;
}
.finish-btn {
  display: block; width: 200px; margin: 10px auto;
  padding: 12px; background: #1e88e5; color: white;
  border: none; border-radius: 8px; font-size: 16px; cursor: pointer;
}
</style>
