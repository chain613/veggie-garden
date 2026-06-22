<template>
  <div class="visit-view">
    <div class="visit-top-bar">
      <button class="back-btn" @click="$router.push('/social')">← 返回</button>
      <span class="visit-title">正在参观 <strong>{{ ownerName }}</strong> 的菜园</span>
    </div>

    <GardenScene ref="sceneRef" :region="region" />

    <div class="visit-bottom-bar">
      <span>📷 只读模式</span>
      <button class="leave-btn" @click="$router.push('/social')">返回好友列表</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import GardenScene from '../components/three/GardenScene.vue'
import { visitGarden } from '../api/social'

const route = useRoute()
const gardenId = route.params.gardenId
const sceneRef = ref(null)
const ownerName = ref('好友')
const region = ref('zhongyuan')

onMounted(async () => {
  const data = await visitGarden(gardenId)
  if (data.owner) ownerName.value = data.owner
  if (data.region) region.value = data.region
})
</script>

<style scoped>
.visit-view {
  width: 100%; height: 100vh; position: relative; overflow: hidden;
}
.visit-top-bar {
  position: fixed; top: 0; left: 0; right: 0; z-index: 100;
  display: flex; align-items: center; gap: 12px;
  padding: 12px 16px; background: rgba(255,255,255,0.9);
  backdrop-filter: blur(8px);
}
.back-btn {
  background: rgba(0,0,0,0.05); border: none; border-radius: 8px;
  padding: 6px 12px; cursor: pointer; font-size: 14px;
}
.visit-title { font-size: 14px; color: #333; }
.visit-bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0; z-index: 100;
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 20px; background: rgba(255,255,255,0.9);
  backdrop-filter: blur(8px); font-size: 13px; color: #888;
}
.leave-btn {
  padding: 8px 20px; background: #5c4033; color: white;
  border: none; border-radius: 10px; cursor: pointer;
}
</style>
