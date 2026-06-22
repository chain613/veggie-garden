<template>
  <component :is="gameComponent" ref="gameRef" @result="onResult" />
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AphidGame from '../components/minigame/AphidGame.vue'
import SnailGame from '../components/minigame/SnailGame.vue'
import WeedingGame from '../components/minigame/WeedingGame.vue'
import BirdGame from '../components/minigame/BirdGame.vue'

const route = useRoute()
const router = useRouter()

const gameMap = {
  aphid: AphidGame,
  snail: SnailGame,
  weeding: WeedingGame,
  bird: BirdGame
}

const gameComponent = computed(() => gameMap[route.params.type] || AphidGame)
const gameRef = ref(null)

async function onResult(result) {
  if (route.params.type === 'weeding' && gameRef.value?.settleWeeding) {
    await gameRef.value.settleWeeding()
  }
  router.push('/garden')
}
</script>
