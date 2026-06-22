<template>
  <div class="practice-entry">
    <div class="entry-header">
      <button class="back-btn" @click="$router.push('/garden')">← 返回</button>
      <h2>🏋️ 练习副本</h2>
    </div>
    <p class="subtitle">在练习中学习农业知识，不计入物资消耗</p>

    <div class="practice-list">
      <div v-for="p in practices" :key="p.id" class="practice-card" @click="enter(p.id)">
        <div class="practice-icon">{{ p.icon }}</div>
        <div class="practice-name">{{ p.name }}</div>
        <div class="practice-desc">{{ p.description }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPractices } from '../../api/practice'

const router = useRouter()
const practices = ref([])

onMounted(async () => {
  practices.value = await getPractices()
})

function enter(id) {
  router.push(`/practice/${id}`)
}
</script>

<style scoped>
.practice-entry {
  padding: 20px; padding-top: 60px;
  min-height: 100vh; background: linear-gradient(180deg, #87CEEB, #c9e8c2);
}
.entry-header {
  display: flex; align-items: center; gap: 12px; margin-bottom: 8px;
}
.back-btn {
  background: rgba(255,255,255,0.6); border: none; border-radius: 8px;
  padding: 6px 12px; cursor: pointer; font-size: 14px;
}
h2 { margin: 0; }
.subtitle { color: #555; font-size: 14px; margin-bottom: 16px; }
.practice-list { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.practice-card {
  background: rgba(255,255,255,0.85); border-radius: 12px;
  padding: 16px; text-align: center; cursor: pointer;
  transition: transform 0.2s;
}
.practice-card:hover { transform: scale(1.03); }
.practice-icon { font-size: 40px; }
.practice-name { font-weight: bold; margin-top: 8px; }
.practice-desc { font-size: 12px; color: #666; margin-top: 4px; }
</style>
