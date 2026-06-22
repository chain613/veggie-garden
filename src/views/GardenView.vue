<template>
  <div class="garden-view">
    <GardenScene ref="sceneRef" :region="gardenStore.currentGarden.region" />
    <GardenSwitcher :gardens="gardenStore.gardens" :current-id="gardenStore.currentGarden.id" @switch="switchGarden" />
    <Joystick @move="onJoystick" />
    <ActionBar @action="onAction" />
    <RootToggle :active="rootActive" @toggle="toggleRoots" />

    <NpcAvatarSelector
      :modelValue="npcStore.avatar"
      @update:modelValue="onAvatarChange"
    />
    <div class="knock-prompt" v-if="showKnockPrompt">
      <button @click="knockOnDoor">👊 敲门请教</button>
    </div>
    <NpcDialogue
      :visible="dialogueVisible"
      :npc-name="npcStore.npcName"
      :trigger-event="currentTriggerEvent"
      @close="dialogueVisible = false"
    />

    <SeedSelector v-if="showSeedSelector" @select="onPlant" @close="showSeedSelector = false" />
    <PlantDetail
      v-if="plantStore.selectedPlant"
      :plant="plantStore.selectedPlant"
      @action="onAction"
      @close="plantStore.clearSelection()"
    />
    <div class="toast" v-if="toast">{{ toast }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import GardenScene from '../components/three/GardenScene.vue'
import Joystick from '../components/ui/Joystick.vue'
import ActionBar from '../components/ui/ActionBar.vue'
import RootToggle from '../components/ui/RootToggle.vue'
import GardenSwitcher from '../components/ui/GardenSwitcher.vue'
import SeedSelector from '../components/ui/SeedSelector.vue'
import PlantDetail from '../components/ui/PlantDetail.vue'
import NpcAvatarSelector from '../components/npc/NpcAvatarSelector.vue'
import NpcDialogue from '../components/npc/NpcDialogue.vue'
import { useNpcStore } from '../stores/npc'
import { useGardenStore } from '../stores/garden'
import { usePlantStore } from '../stores/plant'

const npcStore = useNpcStore()
const gardenStore = useGardenStore()
const plantStore = usePlantStore()

const sceneRef = ref(null)
const rootActive = ref(false)
const showKnockPrompt = ref(false)
const dialogueVisible = ref(false)
const currentTriggerEvent = ref('日常问候')
const showSeedSelector = ref(false)
const plantingSeed = ref(null)
const toast = ref('')
let toastTimer = null
let checkTimer = null

function showToast(msg) {
  toast.value = msg
  if (toastTimer) clearTimeout(toastTimer)
  toastTimer = setTimeout(() => { toast.value = '' }, 2000)
}

onMounted(() => {
  setTimeout(() => {
    if (sceneRef.value?.scene) {
      sceneRef.value.onSceneClick(handleSceneClick)
      startNpcCheck()
    }
  }, 500)
})

onUnmounted(() => {
  if (checkTimer) clearInterval(checkTimer)
})

function startNpcCheck() {
  checkTimer = setInterval(checkNpcTriggers, 2000)
}

function checkNpcTriggers() {
  const npc = sceneRef.value?.npcGroup
  const cam = sceneRef.value?.camera
  if (!npc || !cam) return
  const dist = cam.position.distanceTo(npc.position)
  const hour = new Date().getHours()
  const isNighttime = hour < 5 || hour >= 21
  if (dist < 5) {
    if (isNighttime) {
      showKnockPrompt.value = true
    } else {
      showKnockPrompt.value = false
      if (!dialogueVisible.value) {
        currentTriggerEvent.value = '日常问候'
        dialogueVisible.value = true
      }
    }
  } else {
    showKnockPrompt.value = false
    dialogueVisible.value = false
  }
}

function switchGarden(gardenId) {
  gardenStore.switchGarden(gardenId)
}

function onAvatarChange(avatar) {
  npcStore.setAvatar(avatar)
}

function knockOnDoor() {
  showKnockPrompt.value = false
  currentTriggerEvent.value = '日常问候'
  setTimeout(() => { dialogueVisible.value = true }, 1500)
}

function onAction(action) {
  if (!action) { plantingSeed.value = null; return }
  switch (action.tool) {
    case 'plant':
      plantingSeed.value = null
      showSeedSelector.value = true
      break
    case 'water':
      sceneRef.value?.cropRenderer?.waterAll()
      showToast('💧 浇水完成！')
      break
    case 'fertilize': showToast('🧪 施肥完成，养分满满！'); break
    case 'harvest': showToast('🧺 采收完成，收获满满！'); break
    case 'roots': toggleRoots(); break
  }
}

function onPlant(seed) {
  showSeedSelector.value = false
  plantingSeed.value = seed
  showToast(`📍 请点击田地里想要种植的位置`)
}

function handleSceneClick(result) {
  if (result.type === 'npc') {
    currentTriggerEvent.value = '日常问候'
    dialogueVisible.value = true
    return
  }
  if (result.type === 'ground' && plantingSeed.value) {
    const p = result.point
    sceneRef.value?.cropRenderer?.plant(plantingSeed.value, p.x, p.z)
    plantStore.loadPlants([...plantStore.plants, {
      id: Date.now(),
      vegetableName: plantingSeed.value.name,
      icon: plantingSeed.value.icon,
      growthStage: 0,
      waterNeed: '中',
      daysToHarvest: plantingSeed.value.days || 5
    }])
    showToast(`🌱 种下了 ${plantingSeed.value.name}！`)
    plantingSeed.value = null
  }
}

function onJoystick(x, y) {
  sceneRef.value?.player?.setJoystick(x, y)
}

function toggleRoots() {
  if (sceneRef.value?.rootRenderer) {
    const active = sceneRef.value.rootRenderer.toggle()
    rootActive.value = active
    if (active) {
      sceneRef.value.rootRenderer.renderAllRoots([])
    }
  }
}
</script>

<style scoped>
.garden-view { width: 100%; height: 100vh; position: relative; }
.knock-prompt {
  position: fixed; bottom: 150px; left: 50%; transform: translateX(-50%); z-index: 100;
}
.knock-prompt button {
  padding: 12px 24px; background: #5c4033; color: white;
  border: none; border-radius: 12px; font-size: 16px; cursor: pointer;
}
.toast {
  position: fixed; top: 140px; left: 50%; transform: translateX(-50%);
  background: rgba(0,0,0,0.8); color: white; padding: 10px 24px;
  border-radius: 20px; font-size: 15px; z-index: 700; pointer-events: none;
  animation: toastIn 0.3s ease;
}
@keyframes toastIn {
  from { opacity: 0; transform: translateX(-50%) translateY(-10px); }
  to { opacity: 1; transform: translateX(-50%) translateY(0); }
}
</style>
