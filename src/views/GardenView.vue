<template>
  <div class="garden-view">
    <GardenScene ref="sceneRef" :region="gardenStore.currentGarden.region" @ready="onSceneReady" />
    <Joystick @move="onJoystick" />
    <ActionBar @action="onAction" />
    <SettingsMenu
      :gardens="gardenStore.gardens"
      :current-garden-id="gardenStore.currentGarden.id"
      :npc-avatar="npcStore.avatar"
      @switch-garden="switchGarden"
      @update-npc-avatar="onAvatarChange"
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
      @action="onPlantDetailAction"
      @close="plantStore.clearSelection()"
    />
    <div class="toast" v-if="toast">{{ toast }}</div>
    <div class="mode-indicator" v-if="activeMode">{{ modeLabel }}</div>
  </div>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue'
import GardenScene from '../components/three/GardenScene.vue'
import Joystick from '../components/ui/Joystick.vue'
import ActionBar from '../components/ui/ActionBar.vue'
import SettingsMenu from './SettingsMenu.vue'
import SeedSelector from '../components/ui/SeedSelector.vue'
import PlantDetail from '../components/ui/PlantDetail.vue'
import NpcDialogue from '../components/npc/NpcDialogue.vue'
import { useNpcStore } from '../stores/npc'
import { useGardenStore } from '../stores/garden'
import { usePlantStore } from '../stores/plant'
import { useKnowledgeStore } from '../stores/knowledge'
import api from '../api'

const npcStore = useNpcStore()
const gardenStore = useGardenStore()
const plantStore = usePlantStore()
const knowledgeStore = useKnowledgeStore()

const sceneRef = ref(null)
const rootActive = ref(false)
const showKnockPrompt = ref(false)
const dialogueVisible = ref(false)
const currentTriggerEvent = ref('日常问候')
const showSeedSelector = ref(false)
const plantingSeed = ref(null)
const activeMode = ref(null)
const toast = ref('')
let toastTimer = null
let checkTimer = null

const modeLabelMap = {
  water: '💧 点击植株浇水',
  fertilize: '🧪 点击植株施肥',
  harvest: '🧺 点击植株采收'
}

const modeLabel = computed(() => modeLabelMap[activeMode.value] || '')

function showToast(msg) {
  toast.value = msg
  if (toastTimer) clearTimeout(toastTimer)
  toastTimer = setTimeout(() => { toast.value = '' }, 2000)
}

function onSceneReady() {
  sceneRef.value?.onSceneClick(handleSceneClick)
  startNpcCheck()
}

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
  const hour = sceneRef.value?.getGameHour?.() ?? new Date().getHours()
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
  if (!action) {
    clearMode()
    return
  }
  switch (action.tool) {
    case 'plant':
      clearMode()
      showSeedSelector.value = true
      break
    case 'water':
      enterMode('water')
      break
    case 'fertilize':
      enterMode('fertilize')
      break
    case 'harvest':
      enterMode('harvest')
      break
    case 'roots':
      clearMode()
      toggleRoots()
      break
  }
}

function onPlantDetailAction(action) {
  if (typeof action === 'string') {
    const tool = action
    if (tool === 'water' || tool === 'fertilize' || tool === 'harvest') {
      enterMode(tool)
    }
  }
  plantStore.clearSelection()
}

function enterMode(mode) {
  activeMode.value = mode
  const cr = sceneRef.value?.cropRenderer
  if (!cr) return
  cr.clearHighlights()
  cr.highlightCrops(c => {
    if (mode === 'water') return true
    if (mode === 'fertilize') return !c.userData?.fertilized
    if (mode === 'harvest') return c.userData?.growthStage >= 4
    return false
  })
}

function clearMode() {
  if (activeMode.value) {
    sceneRef.value?.cropRenderer?.clearHighlights()
    activeMode.value = null
  }
}

function toGridCoord(worldX, worldZ) {
  const gridX = Math.round(worldX + 5.5)
  const gridZ = Math.round(worldZ + 5.5)
  return {
    gridX: Math.max(0, Math.min(15, gridX)),
    gridZ: Math.max(0, Math.min(15, gridZ))
  }
}

function onPlant(seed) {
  showSeedSelector.value = false
  plantingSeed.value = seed
  showToast(`📍 请点击田地里想要种植的位置`)
}

async function handleSceneClick(result) {
  if (result.type === 'npc') {
    clearMode()
    currentTriggerEvent.value = '日常问候'
    dialogueVisible.value = true
    return
  }

  if (result.type === 'crop' && result.crop) {
    if (activeMode.value) {
      applyActionToCrop(result.crop)
      return
    }
    const ud = result.crop.userData || {}
    plantStore.selectPlant({
      id: ud.seedId || result.crop.id,
      vegetableName: ud.seedName,
      icon: ud.seedIcon,
      growthStage: ud.growthStage || 0,
      waterNeed: ud.waterNeed || '中',
      daysToHarvest: ud.daysToHarvest,
      _cropGroup: result.crop
    })
    return
  }

  if (result.type === 'ground' && plantingSeed.value) {
    const p = result.point
    const { gridX, gridZ } = toGridCoord(p.x, p.z)
    console.log('[GardenView] planting at', p.x, p.z, 'sceneRef:', !!sceneRef.value, 'cropRenderer:', !!sceneRef.value?.cropRenderer)
    const cropGroup = sceneRef.value?.cropRenderer?.plant(plantingSeed.value, p.x, p.z)
    console.log('[GardenView] cropGroup returned:', !!cropGroup)

    let backendPlantId = null
    try {
      const res = await api.post('/garden/plant', {
        seedId: plantingSeed.value.id,
        gridX,
        gridZ,
      })
      backendPlantId = res.data?.id
      if (cropGroup && backendPlantId) {
        cropGroup.userData.plantId = backendPlantId
      }
    } catch {}

    plantStore.loadPlants([...plantStore.plants, {
      id: backendPlantId || Date.now(),
      vegetableName: plantingSeed.value.name,
      icon: plantingSeed.value.icon,
      growthStage: 0,
      waterNeed: '中',
      daysToHarvest: plantingSeed.value.days || 5,
      gridX,
      gridZ
    }])

    knowledgeStore.unlock(plantingSeed.value.id)

    showToast(`🌱 种下了 ${plantingSeed.value.name}！`)
    plantingSeed.value = null
  }
}

async function applyActionToCrop(cropGroup) {
  const ud = cropGroup.userData || {}
  const cr = sceneRef.value?.cropRenderer
  const mode = activeMode.value

  if (mode === 'water') {
    cr?.waterPlant(cropGroup)
    showToast(`💧 已给 ${ud.seedName || '植株'} 浇水！`)
    try { await api.post('/garden/water', { plantId: ud.plantId }) } catch {}
  } else if (mode === 'fertilize') {
    ud.fertilized = true
    showToast(`🧪 已给 ${ud.seedName || '植株'} 施肥！`)
    try { await api.post('/garden/fertilize', { plantId: ud.plantId }) } catch {}
  } else if (mode === 'harvest') {
    cr?.removeCrop(cropGroup)
    plantStore.loadPlants(plantStore.plants.filter(p => p.id !== ud.seedId || p.gridX !== ud._gridX))
    showToast(`🧺 收获了 ${ud.seedName || '植株'}！`)
    try { await api.post('/garden/harvest', { plantId: ud.plantId }) } catch {}
  }

  clearMode()
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
.mode-indicator {
  position: fixed; bottom: 200px; left: 50%; transform: translateX(-50%);
  background: rgba(92, 64, 51, 0.9); color: #ffd54f;
  padding: 8px 20px; border-radius: 20px; font-size: 14px;
  z-index: 650;
}
@keyframes toastIn {
  from { opacity: 0; transform: translateX(-50%) translateY(-10px); }
  to { opacity: 1; transform: translateX(-50%) translateY(0); }
}
</style>
