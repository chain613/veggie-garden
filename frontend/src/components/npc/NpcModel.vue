<template>
  <div class="npc-label" v-if="showLabel" :style="labelStyle">{{ npcName }}</div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as THREE from 'three'

const props = defineProps({
  scene: { type: Object, required: true },
  avatar: { type: String, default: 'old_farmer' },
  camera: { type: Object, default: null }
})

const emit = defineEmits(['interact'])
const showLabel = ref(true)
const npcName = ref(getNpcName())
const labelStyle = ref({})

let npcGroup = null, nameSprite = null
let labelUpdateTimer = null

onMounted(() => {
  createNpcModel()
  labelUpdateTimer = setInterval(updateLabelPosition, 100)
})

onUnmounted(() => {
  if (npcGroup && props.scene) {
    props.scene.remove(npcGroup)
  }
  if (labelUpdateTimer) clearInterval(labelUpdateTimer)
})

function createNpcModel() {
  npcGroup = new THREE.Group()
  npcGroup.name = 'npc'

  // 大球身体
  const bodyGeo = new THREE.SphereGeometry(1.2, 32, 32)
  const bodyMat = new THREE.MeshBasicMaterial({ color: getAvatarColor() })
  const body = new THREE.Mesh(bodyGeo, bodyMat)
  body.position.y = 1.5
  npcGroup.add(body)

  // 地面光环
  const ringGeo = new THREE.RingGeometry(1.0, 1.3, 32)
  ringGeo.rotateX(-Math.PI / 2)
  const ringMat = new THREE.MeshBasicMaterial({ color: 0xffff00, side: THREE.DoubleSide })
  const ring = new THREE.Mesh(ringGeo, ringMat)
  ring.position.y = 0.05
  npcGroup.add(ring)

  // 名字标签 — canvas 不依赖 roundRect
  const canvas = document.createElement('canvas')
  canvas.width = 256; canvas.height = 64
  const ctx = canvas.getContext('2d')
  ctx.fillStyle = 'rgba(0,0,0,0.8)'
  ctx.fillRect(10, 8, 236, 48)
  ctx.fillStyle = '#ffff00'
  ctx.font = 'bold 28px sans-serif'
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  ctx.fillText(npcName.value, 128, 32)
  const tex = new THREE.CanvasTexture(canvas)
  tex.minFilter = THREE.LinearFilter
  const spriteMat = new THREE.SpriteMaterial({ map: tex, transparent: true, depthTest: false })
  nameSprite = new THREE.Sprite(spriteMat)
  nameSprite.position.y = 3.2
  nameSprite.scale.set(3, 0.75, 1)
  npcGroup.add(nameSprite)

  npcGroup.position.set(0, 0, 4)
  props.scene.add(npcGroup)
}

function updateLabelPosition() {
  if (!npcGroup || !props.camera) return
  const pos = npcGroup.position.clone()
  pos.y += 3.8
  pos.project(props.camera)
  const x = (pos.x * 0.5 + 0.5) * window.innerWidth
  const y = (-pos.y * 0.5 + 0.5) * window.innerHeight
  const behindCamera = pos.z > 1
  labelStyle.value = {
    left: x + 'px',
    top: y + 'px',
    opacity: behindCamera ? 0 : 1
  }
}

function getAvatarColor() {
  switch (props.avatar) {
    case 'old_farmer': return 0x6b7b8d
    case 'beauty': return 0xffc0cb
    case 'handsome': return 0x87ceeb
    default: return 0x6b7b8d
  }
}

function getNpcName() {
  switch (props.avatar) {
    case 'old_farmer': return '老王'
    case 'beauty': return '小慧'
    case 'handsome': return '阿杰'
    default: return '老农'
  }
}

function standUp() {
  if (!npcGroup) return
  const startY = npcGroup.position.y
  const targetY = startY + 1.0
  const duration = 600
  const start = Date.now()
  function animate() {
    const elapsed = Date.now() - start
    const progress = Math.min(elapsed / duration, 1)
    npcGroup.position.y = startY + (targetY - startY) * progress
    if (progress < 1) requestAnimationFrame(animate)
  }
  animate()
  showLabel.value = true
}

function sitDown() {
  if (!npcGroup) return
  const startY = npcGroup.position.y
  const targetY = startY - 1.0
  const duration = 600
  const start = Date.now()
  function animate() {
    const elapsed = Date.now() - start
    const progress = Math.min(elapsed / duration, 1)
    npcGroup.position.y = startY + (targetY - startY) * progress
    if (progress < 1) requestAnimationFrame(animate)
  }
  animate()
}

function knockDoor() {
  if (!npcGroup) return
  npcGroup.visible = true
  standUp()
  npcName.value = '来了来了...'
  setTimeout(() => { npcName.value = getNpcName() }, 2000)
}

defineExpose({ standUp, sitDown, knockDoor, getNpcName, npcGroup })
</script>

<style scoped>
.npc-label {
  position: fixed;
  transform: translate(-50%, -100%);
  background: rgba(0,0,0,0.7); color: #ffdd00; padding: 4px 14px;
  border-radius: 10px; font-size: 14px; font-weight: bold;
  pointer-events: none; z-index: 50;
  transition: opacity 0.3s;
  white-space: nowrap;
}
</style>
