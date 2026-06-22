<template>
  <div class="inventory-view">
    <div class="page-header">
      <button class="back-btn" @click="$router.push('/garden')">← 返回</button>
      <h2>🎒 背包</h2>
    </div>

    <div class="tabs">
      <button v-for="t in tabs" :key="t.key" :class="{ active: activeTab === t.key }" @click="activeTab = t.key">
        {{ t.icon }} {{ t.label }}
      </button>
    </div>

    <div v-if="filteredItems.length === 0" class="empty">
      <p>背包空空，去商店看看</p>
    </div>
    <div v-else class="item-list">
      <div v-for="item in filteredItems" :key="item.id" class="item-card" @click="selected = item">
        <span class="item-icon">{{ item.icon || '📦' }}</span>
        <div class="item-info">
          <span class="item-name">{{ item.name }}</span>
          <span class="item-desc">{{ item.description || '' }}</span>
        </div>
        <span class="item-qty">x{{ item.quantity || item.count || 1 }}</span>
      </div>
    </div>

    <div v-if="selected" class="action-overlay" @click.self="selected = null">
      <div class="action-sheet">
        <h4>{{ selected.name }}</h4>
        <p class="action-desc">{{ selected.description || '' }}</p>
        <button class="action-btn" @click="selected = null">使用</button>
        <button class="action-btn danger" @click="discardItem">丢弃</button>
        <button class="action-btn cancel" @click="selected = null">取消</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../api'

const activeTab = ref('seed')
const selected = ref(null)
const items = ref([])

const tabs = [
  { key: 'seed', label: '种子', icon: '🌱' },
  { key: 'tool', label: '工具', icon: '🔧' },
  { key: 'fertilizer', label: '肥料', icon: '🧪' },
  { key: 'pesticide', label: '农药', icon: '💊' }
]

const filteredItems = computed(() => items.value.filter(i => i.category === activeTab.value))

onMounted(async () => {
  try {
    const res = await api.get('/inventory')
    const data = Array.isArray(res.data) ? res.data : (res.data?.data || [])
    items.value = data.map(i => ({ ...i, category: i.category || guessCategory(i) }))
  } catch {
    items.value = [
      { id: 1, name: '白菜种子', icon: '🌱', category: 'seed', quantity: 5, description: '春秋播种，40-60天采收' },
      { id: 2, name: '黄瓜种子', icon: '🌱', category: 'seed', quantity: 3, description: '需搭架，喜温暖' },
      { id: 3, name: '小铲子', icon: '🔧', category: 'tool', quantity: 1, description: '翻土除草用' },
      { id: 4, name: '有机肥', icon: '🧪', category: 'fertilizer', quantity: 10, description: '天然肥料，养地又养菜' }
    ]
  }
})

function guessCategory(item) {
  const name = item.name || item.itemName || ''
  if (/种子|seed/.test(name)) return 'seed'
  if (/肥|fertilizer/.test(name)) return 'fertilizer'
  if (/药|pesticide/.test(name)) return 'pesticide'
  return 'tool'
}

function discardItem() {
  items.value = items.value.filter(i => i.id !== selected.value?.id)
  selected.value = null
}
</script>

<style scoped>
.inventory-view {
  min-height: 100vh; background: linear-gradient(180deg, #e8f5e9, #c8e6c9);
  padding: 20px; padding-top: 60px;
}
.page-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.back-btn { background: rgba(255,255,255,0.6); border: none; border-radius: 8px; padding: 6px 12px; cursor: pointer; font-size: 14px; }
h2 { margin: 0; }
.tabs { display: flex; gap: 6px; margin-bottom: 16px; }
.tabs button {
  flex: 1; padding: 8px; border-radius: 10px; border: none;
  background: rgba(255,255,255,0.6); font-size: 13px; cursor: pointer;
}
.tabs button.active { background: #4caf50; color: white; }
.empty { text-align: center; padding: 60px 20px; color: #888; }
.item-list { display: flex; flex-direction: column; gap: 8px; }
.item-card {
  display: flex; align-items: center; gap: 12px;
  background: rgba(255,255,255,0.85); border-radius: 12px;
  padding: 12px 16px; cursor: pointer;
}
.item-icon { font-size: 28px; }
.item-info { flex: 1; display: flex; flex-direction: column; }
.item-name { font-weight: bold; font-size: 14px; }
.item-desc { font-size: 12px; color: #888; }
.item-qty { font-size: 14px; color: #666; }
.action-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.4);
  display: flex; align-items: flex-end; justify-content: center; z-index: 300;
}
.action-sheet {
  background: white; border-radius: 16px 16px 0 0; width: 100%;
  max-width: 400px; padding: 24px 20px 32px;
}
.action-sheet h4 { text-align: center; margin: 0 0 4px; }
.action-desc { text-align: center; font-size: 13px; color: #888; margin-bottom: 16px; }
.action-btn {
  display: block; width: 100%; padding: 14px; border-radius: 10px;
  border: none; background: #f5f5f5; font-size: 15px; cursor: pointer; margin-bottom: 8px;
}
.action-btn.danger { color: #e53935; }
.action-btn.cancel { background: white; border: 1px solid #ddd; color: #666; }
</style>
