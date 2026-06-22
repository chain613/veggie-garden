<template>
  <div class="snail-game">
    <div class="game-header">
      <span>🐌 捉蜗牛</span>
      <span>已捉：{{ caught }}</span>
      <span>剩余：{{ remaining }}</span>
    </div>

    <div class="game-area" ref="area">
      <div
        v-for="snail in snails"
        :key="snail.id"
        class="snail"
        :class="{ out: snail.out, hidden: !snail.out && !snail.caught }"
        :style="{ left: snail.x + 'px', top: snail.y + 'px' }"
        @click="catchSnail(snail.id)"
      >
        {{ snail.caught ? '✅' : (snail.out ? '🐌' : '🐚') }}
      </div>
    </div>

    <div class="game-info">
      <p>知识：蜗牛喜欢潮湿环境，清晨和雨后最活跃。它们缩回壳里时捉不到哦！</p>
    </div>

    <button class="finish-btn" @click="finish">完成捉蜗牛</button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const emit = defineEmits(['result'])

const snails = ref([])
const caught = ref(0)
let timers = []

const remaining = computed(() => snails.value.filter(s => !s.caught).length)

onMounted(() => {
  for (let i = 0; i < 8; i++) {
    snails.value.push({
      id: i,
      x: Math.random() * 320 + 20,
      y: Math.random() * 420 + 20,
      out: true,
      timer: Math.random() * 3000 + 2000,
      caught: false
    })
  }

  snails.value.forEach((snail, idx) => {
    const t = setInterval(() => {
      if (snail.caught) return
      snail.out = !snail.out
    }, snail.timer)
    timers.push(t)
  })
})

onUnmounted(() => {
  timers.forEach(t => clearInterval(t))
})

function catchSnail(id) {
  const snail = snails.value.find(s => s.id === id)
  if (snail && snail.out && !snail.caught) {
    snail.caught = true
    caught.value++
  }
}

function finish() {
  timers.forEach(t => clearInterval(t))
  const remainingCount = snails.value.filter(s => !s.caught).length
  emit('result', {
    success: remainingCount === 0,
    caught: caught.value,
    remaining: remainingCount
  })
}
</script>

<style scoped>
.snail-game {
  position: relative; height: 100vh;
  background: linear-gradient(180deg, #e8f5e9, #c8e6c9);
}
.game-header {
  display: flex; justify-content: space-between;
  padding: 10px 20px; background: rgba(0,0,0,0.08);
}
.game-area {
  position: relative; width: 380px; height: 500px;
  margin: 20px auto; background: rgba(255,255,255,0.5);
  border-radius: 12px; overflow: hidden;
  border: 2px solid #66bb6a;
}
.snail {
  position: absolute; font-size: 28px; cursor: pointer;
  user-select: none; z-index: 10;
  transition: transform 0.2s;
}
.snail.out:hover { transform: scale(1.3); }
.snail.hidden { opacity: 0.3; cursor: default; }
.game-info {
  padding: 12px 20px; font-size: 14px; color: #333;
}
.finish-btn {
  display: block; width: 200px; margin: 10px auto;
  padding: 12px; background: #4caf50; color: white;
  border: none; border-radius: 8px; font-size: 16px; cursor: pointer;
}
</style>
