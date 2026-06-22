<template>
  <div class="knowledge-view">
    <div class="page-header">
      <button class="back-btn" @click="$router.push('/garden')">← 返回</button>
      <h2>📖 知识图鉴</h2>
    </div>

    <div class="veggie-grid">
      <div
        v-for="v in store.vegetables" :key="v.id"
        class="veggie-card"
        :class="{ locked: !v.unlocked }"
        @click="selectVeg(v)"
      >
        <span class="veggie-icon">{{ v.unlocked ? v.icon : '❓' }}</span>
        <span class="veggie-name">{{ v.unlocked ? v.name : '???' }}</span>
      </div>
    </div>

    <div class="progress-bar">已解锁 {{ store.unlockedCount }} / {{ store.vegetables.length }}</div>

    <div v-if="selected" class="detail-overlay" @click.self="selected = null">
      <div class="detail-panel" v-if="selected.unlocked">
        <span class="detail-icon">{{ selected.icon }}</span>
        <h3>{{ selected.name }}</h3>
        <div class="detail-grid">
          <div class="detail-item"><span>季节</span><span>{{ selected.season }}</span></div>
          <div class="detail-item"><span>需水量</span><span>{{ selected.water }}</span></div>
          <div class="detail-item"><span>需光量</span><span>{{ selected.light }}</span></div>
          <div class="detail-item"><span>管理</span><span>{{ selected.desc }}</span></div>
        </div>
        <button class="close-panel" @click="selected = null">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useKnowledgeStore } from '../stores/knowledge'

const store = useKnowledgeStore()
const selected = ref(null)

function selectVeg(v) {
  if (v.unlocked) selected.value = v
}
</script>

<style scoped>
.knowledge-view {
  min-height: 100vh; background: linear-gradient(180deg, #1a1a2e, #16213e);
  padding: 20px; padding-top: 60px; color: #e0e0e0;
}
.page-header { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }
.back-btn { background: rgba(255,255,255,0.1); border: none; border-radius: 8px; padding: 6px 12px; cursor: pointer; color: white; font-size: 14px; }
h2 { margin: 0; }
.veggie-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.veggie-card {
  display: flex; flex-direction: column; align-items: center; gap: 6px;
  padding: 20px 12px; border-radius: 16px; cursor: pointer;
  background: rgba(255,255,255,0.08); transition: transform 0.2s;
}
.veggie-card:hover:not(.locked) { transform: scale(1.05); background: rgba(255,255,255,0.14); }
.veggie-card.locked { opacity: 0.4; cursor: default; }
.veggie-icon { font-size: 36px; }
.veggie-name { font-size: 13px; }
.progress-bar { text-align: center; margin-top: 20px; padding: 10px; background: rgba(255,255,255,0.06); border-radius: 10px; font-size: 14px; }

.detail-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.7);
  display: flex; align-items: flex-end; justify-content: center; z-index: 300;
}
.detail-panel {
  background: #222; color: white;
  border-radius: 20px 20px 0 0; width: 100%; max-width: 400px;
  padding: 32px 24px; text-align: center;
}
.detail-icon { font-size: 56px; }
.detail-panel h3 { margin: 8px 0 16px; }
.detail-grid { display: flex; flex-direction: column; gap: 8px; margin-bottom: 20px; }
.detail-item { display: flex; justify-content: space-between; padding: 8px 12px; background: rgba(255,255,255,0.06); border-radius: 8px; font-size: 14px; }
.close-panel { margin-top: 8px; padding: 10px 32px; background: #444; color: white; border: none; border-radius: 10px; cursor: pointer; }
</style>
