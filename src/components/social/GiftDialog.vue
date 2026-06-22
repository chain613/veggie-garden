<template>
  <div class="gift-dialog-overlay" @click.self="$emit('close')">
    <div class="gift-dialog">
      <div class="gift-header">
        <h3>🎁 送礼物给 {{ friendName }}</h3>
        <button class="close-btn" @click="$emit('close')">✕</button>
      </div>

      <div class="gift-body">
        <div v-if="inventory.length === 0" class="empty-hint">背包中暂无可赠送的物品</div>

        <div class="inventory-list">
          <div
            v-for="item in inventory" :key="item.id"
            class="inventory-item"
            :class="{ selected: selectedItem?.id === item.id }"
            @click="selectedItem = item"
          >
            <span class="item-icon">{{ item.icon || '📦' }}</span>
            <span class="item-name">{{ item.name || item.itemName || '物品' }}</span>
            <span class="item-count">x{{ item.quantity || item.count || 1 }}</span>
          </div>
        </div>

        <div v-if="selectedItem" class="quantity-selector">
          <label>数量：</label>
          <button @click="qty > 1 && qty--">−</button>
          <span class="qty">{{ qty }}</span>
          <button @click="qty < maxQty && qty++">+</button>
        </div>
      </div>

      <div class="gift-footer">
        <button class="cancel-btn" @click="$emit('close')">取消</button>
        <button class="send-btn" :disabled="!selectedItem" @click="doSend">确认赠送</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../../api'

const props = defineProps({
  friendId: { type: [Number, String], required: true },
  friendName: { type: String, default: '' }
})

const emit = defineEmits(['close', 'sent'])
const inventory = ref([])
const selectedItem = ref(null)
const qty = ref(1)

const maxQty = computed(() => selectedItem.value?.quantity || selectedItem.value?.count || 1)

onMounted(async () => {
  try {
    const res = await api.get('/inventory')
    const data = Array.isArray(res.data) ? res.data : (res.data?.data || [])
    inventory.value = data.filter(i => (i.quantity || i.count || 0) > 0)
  } catch {
    inventory.value = []
  }
})

async function doSend() {
  if (!selectedItem.value) return
  const { sendGift } = await import('../../api/social')
  await sendGift(props.friendId, selectedItem.value.id, qty.value)
  emit('sent', { item: selectedItem.value, qty: qty.value })
  emit('close')
}
</script>

<style scoped>
.gift-dialog-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.5);
  display: flex; align-items: center; justify-content: center; z-index: 300;
}
.gift-dialog {
  background: white; border-radius: 16px; width: 340px; max-height: 80vh;
  display: flex; flex-direction: column;
}
.gift-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 20px; border-bottom: 1px solid #eee;
}
.gift-header h3 { margin: 0; font-size: 16px; }
.close-btn { background: none; border: none; font-size: 18px; cursor: pointer; }
.gift-body { padding: 16px 20px; flex: 1; overflow-y: auto; }
.empty-hint { text-align: center; color: #999; padding: 24px; }
.inventory-list { display: flex; flex-direction: column; gap: 8px; max-height: 280px; overflow-y: auto; }
.inventory-item {
  display: flex; align-items: center; gap: 10px; padding: 10px;
  border: 2px solid #eee; border-radius: 10px; cursor: pointer;
}
.inventory-item.selected { border-color: #4caf50; background: #f1f8e9; }
.item-icon { font-size: 24px; }
.item-name { flex: 1; font-size: 14px; }
.item-count { font-size: 13px; color: #888; }
.quantity-selector {
  display: flex; align-items: center; justify-content: center; gap: 12px;
  margin-top: 16px;
}
.quantity-selector button {
  width: 36px; height: 36px; border-radius: 50%;
  border: 2px solid #ddd; background: white; font-size: 18px; cursor: pointer;
}
.qty { font-size: 18px; font-weight: bold; min-width: 24px; text-align: center; }
.gift-footer {
  display: flex; gap: 10px; padding: 16px 20px; border-top: 1px solid #eee;
}
.cancel-btn {
  flex: 1; padding: 10px; border-radius: 10px;
  border: 1px solid #ddd; background: white; cursor: pointer;
}
.send-btn {
  flex: 2; padding: 10px; border-radius: 10px;
  border: none; background: #4caf50; color: white; font-size: 15px; cursor: pointer;
}
.send-btn:disabled { background: #ccc; cursor: default; }
</style>
