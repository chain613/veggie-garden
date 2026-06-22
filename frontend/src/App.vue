<template>
  <router-view />
  <BottomNav v-if="showNav" />
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import BottomNav from './components/ui/BottomNav.vue'

const route = useRoute()

const hideNavPaths = ['/login', '/minigame']
const showNav = computed(() => {
  const path = route.path
  return !hideNavPaths.some(p => path.startsWith(p))
})
</script>
