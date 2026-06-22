<template>
  <div class="warehouse-view">
    <div class="page-header">
      <button class="back-btn" @click="$router.push('/garden')">← 返回</button>
      <h2>🏠 仓库</h2>
    </div>

    <div class="summary-card">
      <span class="summary-label">总收成价值</span>
      <span class="summary-value">{{ totalValue }}</span>
      <span class="summary-unit">金币</span>
    </div>

    <div v-if="sortedItems.length === 0" class="empty">
      <p>还没有收成，去菜园种点什么吧</p>
    </div>
    <div v-else class="item-list">
      <div v-for="item in sortedItems" :key="item.id" class="item-card">
        <span class="item-icon">{{ item.icon || '🧺' }}</span>
        <div class="item-info">
          <span class="item-name">{{ item.vegetableName || item.name || '蔬菜' }}</span>
          <span class="item-time">{{ item.harvestTime || item.createdAt || '' }}</span>
        </div>
        <div class="item-right">
          <span class="item-stars">{{ stars(item.quality) }}</span>
          <span class="item-qty">x{{ item.quantity || item.count || 1 }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../api'

const items = ref([])

onMounted(async () => {
  try {
    const res = await api.get('/warehouse')
    const data = Array.isArray(res.data) ? res.data : (res.data?.data || [])
    items.value = data
  } catch {
    items.value = [
      { id: 1, vegetableName: '白菜', icon: '🥬', quality: 4, quantity: 12, harvestTime: '2026-06-20' },
      { id: 2, vegetableName: '黄瓜', icon: '🥒', quality: 5, quantity: 8, harvestTime: '2026-06-21' },
      { id: 3, vegetableName: '番茄', icon: '🍅', quality: 3, quantity: 5, harvestTime: '2026-06-19' }
    ]
  }
})

const sortedItems = computed(() =>
  [...items.value].sort((a, b) => (b.quality || 0) - (a.quality || 0))
)

const totalValue = computed(() => {
  const basePrices = { '白菜': 5, '黄瓜': 8, '番茄': 10, '胡萝卜': 6, '菠菜': 4, '辣椒': 12, '茄子': 10, '豆角': 7, '南瓜': 15, '萝卜': 5 }
  return sortedItems.value.reduce((sum, i) => {
    const name = i.vegetableName || i.name || ''
    const base = basePrices[name] || 5
    const quality = i.quality || 3
    return sum + (i.quantity || i.count || 1) * base * (quality / 5 + 0.5)
  }, 0).toFixed(0)
})

function stars(quality) {
  return '⭐'.repeat(Math.min(quality || 3, 5))
}
</script>

<style scoped>
.warehouse-view {
  min-height: 100vh; background: linear-gradient(180deg, #fff8e1, #ffecb3);
  padding: 20px; padding-top: 60px;
}
.page-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.back-btn { background: rgba(0,0,0,0.05); border: none; border-radius: 8px; padding: 6px 12px; cursor: pointer; font-size: 14px; }
h2 { margin: 0; }
.summary-card {
  background: linear-gradient(135deg, #5c4033, #8d6e63); color: white;
  border-radius: 16px; padding: 20px; text-align: center; margin-bottom: 20px;
}
.summary-label { display: block; font-size: 13px; opacity: 0.8; }
.summary-value { font-size: 36px; font-weight: bold; }
.summary-unit { font-size: 13px; opacity: 0.8; margin-left: 4px; }
.empty { text-align: center; padding: 60px 20px; color: #888; }
.item-list { display: flex; flex-direction: column; gap: 8px; }
.item-card {
  display: flex; align-items: center; gap: 12px;
  background: rgba(255,255,255,0.85); border-radius: 12px;
  padding: 12px 16px;
}
.item-icon { font-size: 28px; }
.item-info { flex: 1; display: flex; flex-direction: column; }
.item-name { font-weight: bold; font-size: 14px; }
.item-time { font-size: 11px; color: #999; }
.item-right { text-align: right; }
.item-stars { font-size: 12px; display: block; }
.item-qty { font-size: 13px; color: #666; }
</style>
