<template>
  <div ref="container" class="garden-scene"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as THREE from 'three'
import { createTerrain } from './Terrain.js'
import { createSky } from './SkyDome.js'
import { createFarmhouse } from './Farmhouse.js'
import { createFence } from './Fence.js'
import { createTrees } from './Trees.js'
import { PlayerController } from './PlayerController.js'
import { RootRenderer } from './RootRenderer.js'
import { WeedRenderer } from './WeedRenderer.js'
import { WeatherEffects } from './WeatherEffects.js'
import { CropRenderer } from './CropRenderer.js'

const emit = defineEmits(['ready'])

const container = ref(null)
let scene, camera, renderer, animationId
let skyData, player, rootRenderer, weedRenderer, weather, cropRenderer
let npcGroup = null, groundPlane = null
let sceneClickCallback = null
const raycaster = new THREE.Raycaster()
const mouse = new THREE.Vector2()

const props = defineProps({
  region: { type: String, default: 'zhongyuan' }
})

onMounted(() => {
  initScene()
  animate()
  window.addEventListener('resize', onResize)

  emit('ready', { scene, camera, cropRenderer, npcGroup, player, rootRenderer, weedRenderer, weather })
})

onUnmounted(() => {
  cancelAnimationFrame(animationId)
  window.removeEventListener('resize', onResize)
  renderer.dispose()
})

function getGameTime() {
  return Date.now() * 0.0001
}

function getGameHour() {
  const t = getGameTime()
  const sunY = Math.sin(t)
  // Map sun position to hour: sunY=0 at sunrise(6) and sunset(18), peak at noon(12)
  const angle = Math.atan2(sunY, Math.cos(t))
  let hour = (angle / Math.PI) * 12 + 12
  if (hour < 0) hour += 24
  if (hour >= 24) hour -= 24
  return hour
}

function initScene() {
  const el = container.value

  scene = new THREE.Scene()
  scene.fog = new THREE.Fog(0xc9e8c2, 30, 120)

  camera = new THREE.PerspectiveCamera(60, el.clientWidth / el.clientHeight, 0.1, 200)

  renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.setSize(el.clientWidth, el.clientHeight)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  renderer.shadowMap.enabled = true
  el.appendChild(renderer.domElement)

  scene.add(new THREE.AmbientLight(0xffffff, 0.5))
  const sunLight = new THREE.DirectionalLight(0xfff5e6, 1.0)
  sunLight.position.set(50, 80, 30)
  sunLight.castShadow = true
  sunLight.shadow.mapSize.set(1024, 1024)
  scene.add(sunLight)

  const terrain = createTerrain(scene, props.region)
  groundPlane = terrain.ground
  skyData = createSky(scene)
  createFarmhouse(scene)
  createFence(scene)
  createTrees(scene, props.region)

  player = new PlayerController(camera, scene, el)
  player.onClick(onSceneClickInternal)

  rootRenderer = new RootRenderer(scene)
  weedRenderer = new WeedRenderer(scene)
  weather = new WeatherEffects(scene)
  cropRenderer = new CropRenderer(scene)

  createNpc()
}

function createNpc() {
  npcGroup = new THREE.Group()
  npcGroup.name = 'npc'

  const bodyGeo = new THREE.SphereGeometry(1.0, 32, 32)
  const bodyMat = new THREE.MeshBasicMaterial({ color: 0x4488cc })
  const body = new THREE.Mesh(bodyGeo, bodyMat)
  body.position.y = 1.2
  npcGroup.add(body)

  const headGeo = new THREE.SphereGeometry(0.3, 16, 16)
  const headMat = new THREE.MeshBasicMaterial({ color: 0x336699 })
  const head = new THREE.Mesh(headGeo, headMat)
  head.position.y = 2.4
  npcGroup.add(head)

  const ringGeo = new THREE.RingGeometry(0.8, 1.0, 32)
  ringGeo.rotateX(-Math.PI / 2)
  const ringMat = new THREE.MeshBasicMaterial({ color: 0xffff00, side: THREE.DoubleSide })
  const ring = new THREE.Mesh(ringGeo, ringMat)
  ring.position.y = 0.05
  npcGroup.add(ring)

  npcGroup.position.set(0, 0, 4)
  scene.add(npcGroup)
}

function animate() {
  animationId = requestAnimationFrame(animate)
  player.update()
  updateSky()
  if (rootRenderer?.visible) {
    rootRenderer.update(Date.now() * 0.001)
  }
  if (weather) weather.update(0.016)
  renderer.render(scene, camera)
}

function updateSky() {
  if (skyData) {
    const time = getGameTime()
    skyData.sun.position.x = 30 * Math.cos(time)
    skyData.sun.position.y = 35 * Math.sin(time)
    skyData.sun.visible = skyData.sun.position.y > 0
    skyData.moon.position.x = -30 * Math.cos(time)
    skyData.moon.position.y = -35 * Math.sin(time)
    skyData.moon.visible = skyData.moon.position.y > 0
  }
}

function onResize() {
  const el = container.value
  camera.aspect = el.clientWidth / el.clientHeight
  camera.updateProjectionMatrix()
  renderer.setSize(el.clientWidth, el.clientHeight)
}

function onSceneClickInternal(e) {
  const rect = renderer.domElement.getBoundingClientRect()
  mouse.x = ((e.clientX - rect.left) / rect.width) * 2 - 1
  mouse.y = -((e.clientY - rect.top) / rect.height) * 2 + 1
  raycaster.setFromCamera(mouse, camera)

  // 先检测 NPC
  if (npcGroup) {
    const npcHits = raycaster.intersectObjects(npcGroup.children, true)
    if (npcHits.length > 0) {
      if (sceneClickCallback) sceneClickCallback({ type: 'npc', point: npcHits[0].point })
      return
    }
  }

  // 检测作物
  if (cropRenderer && cropRenderer.crops.length > 0) {
    const cropHits = raycaster.intersectObjects(cropRenderer.crops, true)
    if (cropHits.length > 0) {
      let cropGroup = cropHits[0].object
      while (cropGroup && cropGroup.parent && cropGroup.parent !== scene) {
        cropGroup = cropGroup.parent
      }
      if (cropGroup && cropGroup.userData?.cropType === 'plant') {
        if (sceneClickCallback) sceneClickCallback({ type: 'crop', crop: cropGroup, point: cropHits[0].point })
        return
      }
    }
  }

  // 检测地面
  if (groundPlane) {
    const groundHits = raycaster.intersectObject(groundPlane)
    if (groundHits.length > 0) {
      if (sceneClickCallback) sceneClickCallback({ type: 'ground', point: groundHits[0].point })
    }
  }
}

function onSceneClick(cb) {
  sceneClickCallback = cb
}

defineExpose({
  player, rootRenderer, weedRenderer, scene, camera, weather, cropRenderer, npcGroup,
  onSceneClick, getGameTime, getGameHour
})
</script>

<style scoped>
.garden-scene { width: 100%; height: 100vh; overflow: hidden; touch-action: none; }
</style>
