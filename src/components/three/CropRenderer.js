import * as THREE from 'three'

const GROWTH_CONFIG = {
  0: { stemH: 0.15, stemR: 0.07, leafR: 0.12, scale: 0.3, label: '种子' },
  1: { stemH: 0.55, stemR: 0.14, leafR: 0.25, scale: 0.5, label: '幼苗' },
  2: { stemH: 0.95, stemR: 0.2, leafR: 0.4, scale: 0.7, label: '成长' },
  3: { stemH: 1.3, stemR: 0.25, leafR: 0.5, scale: 0.9, label: '开花' },
  4: { stemH: 1.6, stemR: 0.3, leafR: 0.6, scale: 1.0, label: '成熟' }
}

const TYPE_COLORS = {
  leafy: { stem: 0x3d6b2e, leaf: 0x5cb85c, fruit: 0x8bc34a },
  gourd: { stem: 0x5a8a3c, leaf: 0x8bc34a, fruit: 0xaed581 },
  solanum: { stem: 0x2e5a1e, leaf: 0x388e3c, fruit: 0xe53935 },
  root: { stem: 0x4a6b2a, leaf: 0x6d8c3c, fruit: 0xff8f00 },
  pumpkin: { stem: 0x3d5a1e, leaf: 0x558b2f, fruit: 0xffa000 },
  bean: { stem: 0x5a7a3a, leaf: 0x7cb342, fruit: 0xaed581 }
}

function getTypeByIcon(icon) {
  if (!icon) return 'leafy'
  const map = { '🥬': 'leafy', '🥒': 'gourd', '🍅': 'solanum', '🍆': 'solanum', '🌶️': 'solanum', '🥕': 'root', '🎃': 'pumpkin', '🫘': 'bean' }
  return map[icon] || 'leafy'
}

function getFruitColor(type, name) {
  if (name === '番茄') return 0xe53935
  if (name === '茄子') return 0x5e35b1
  if (name === '辣椒') return 0xe53935
  return TYPE_COLORS[type]?.fruit || 0xaed581
}

function easeOutBack(t) {
  const c1 = 1.70158
  const c3 = c1 + 1
  return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2)
}

export class CropRenderer {
  constructor(scene) {
    this.scene = scene
    this.crops = []
  }

  plant(seed, x, z) {
    const group = new THREE.Group()
    group.name = `crop-${seed.name}`
    const vegType = getTypeByIcon(seed.icon)
    group.userData = {
      cropType: 'plant',
      seedId: seed.id,
      seedName: seed.name,
      seedIcon: seed.icon,
      vegType,
      growthStage: 0,
      waterNeed: '中',
      daysToHarvest: seed.days || 5,
      fertilized: false,
      plantedAt: Date.now()
    }

    // 土堆（加高加宽，颜色与土壤形成对比）
    const moundGeo = new THREE.CylinderGeometry(0.25, 0.35, 0.12, 8)
    const moundMat = new THREE.MeshLambertMaterial({ color: 0x8B6914 })
    const mound = new THREE.Mesh(moundGeo, moundMat)
    mound.position.y = 0.06
    mound.name = '_mound'
    group.add(mound)

    // 构建 Stage 0 模型
    this._buildModel(group, vegType, seed.name, 0)

    group.position.set(x, 0.05, z)

    // 种植弹跳动画
    group.scale.set(0.01, 0.01, 0.01)
    const start = performance.now()
    const duration = 600
    const animate = (now) => {
      const elapsed = now - start
      const t = Math.min(elapsed / duration, 1)
      const s = easeOutBack(t)
      group.scale.setScalar(s)
      if (t < 1) {
        requestAnimationFrame(animate)
      } else {
        group.scale.setScalar(1)
      }
    }
    requestAnimationFrame(animate)

    // 土粒溅起效果
    this._spawnParticles(x, z)

    this.scene.add(group)
    this.crops.push(group)
    return group
  }

  _buildModel(group, vegType, name, stage) {
    // 清除旧模型（保留土堆和光环）
    const toRemove = []
    group.children.forEach(c => {
      if (c.name !== '_mound' && !c.name?.startsWith('_highlight')) {
        toRemove.push(c)
      }
    })
    toRemove.forEach(c => group.remove(c))

    const cfg = GROWTH_CONFIG[stage] || GROWTH_CONFIG[0]
    const colors = TYPE_COLORS[vegType] || TYPE_COLORS.leafy

    if (stage === 0) {
      // 嫩芽（直径0.16，确保远距离可见）
      const sproutGeo = new THREE.ConeGeometry(0.08, 0.16, 8)
      const sproutMat = new THREE.MeshLambertMaterial({ color: colors.leaf })
      const sprout = new THREE.Mesh(sproutGeo, sproutMat)
      sprout.position.y = 0.16
      sprout.name = '_sprout'
      group.add(sprout)
      // 两片子叶
      for (let i = 0; i < 2; i++) {
        const leafletGeo = new THREE.SphereGeometry(0.07, 6, 4)
        const leaflet = new THREE.Mesh(leafletGeo, sproutMat)
        leaflet.position.set((i === 0 ? 0.06 : -0.06), 0.13, 0)
        leaflet.scale.set(1, 0.3, 1)
        leaflet.name = '_leaflet'
        group.add(leaflet)
      }
      return
    }

    // 茎秆
    const stemGeo = new THREE.CylinderGeometry(cfg.stemR * 0.7, cfg.stemR, cfg.stemH, 8)
    const stemMat = new THREE.MeshLambertMaterial({ color: colors.stem })
    const stem = new THREE.Mesh(stemGeo, stemMat)
    stem.position.y = cfg.stemH / 2 + 0.05
    stem.name = '_stem'
    group.add(stem)

    // 叶片簇（多层）
    for (let i = 0; i < 3; i++) {
      const leafGeo = new THREE.SphereGeometry(cfg.leafR, 8, 6)
      const leafMat = new THREE.MeshLambertMaterial({ color: colors.leaf })
      const leaf = new THREE.Mesh(leafGeo, leafMat)
      const angle = (i / 3) * Math.PI * 2
      leaf.position.set(
        Math.cos(angle) * cfg.leafR * 0.5,
        cfg.stemH * 0.4 + i * cfg.stemH * 0.18 + 0.05,
        Math.sin(angle) * cfg.leafR * 0.5
      )
      leaf.scale.set(1, 0.6, 1)
      leaf.name = '_leaf'
      group.add(leaf)
    }

    // 顶部叶冠
    const crownGeo = new THREE.SphereGeometry(cfg.leafR, 8, 8)
    const crownMat = new THREE.MeshLambertMaterial({ color: colors.leaf })
    const crown = new THREE.Mesh(crownGeo, crownMat)
    crown.position.y = cfg.stemH + cfg.leafR * 0.3 + 0.05
    crown.scale.set(1, 0.5, 1)
    crown.name = '_crown'
    group.add(crown)

    // Stage 4: 果实
    if (stage === 4) {
      const fruitColor = getFruitColor(vegType, name)
      const fruitGeo = new THREE.SphereGeometry(cfg.leafR * 0.55, 8, 8)
      const fruitMat = new THREE.MeshLambertMaterial({ color: fruitColor })
      for (let i = 0; i < 2; i++) {
        const fruit = new THREE.Mesh(fruitGeo, fruitMat)
        fruit.position.set(
          (i === 0 ? 0.15 : -0.15),
          cfg.stemH * 0.65 + 0.05,
          0.1
        )
        fruit.name = '_fruit'
        group.add(fruit)
      }
    }
  }

  updateGrowthStage(cropGroup, stage) {
    if (!cropGroup || !cropGroup.userData) return
    const ud = cropGroup.userData
    ud.growthStage = Math.max(0, Math.min(4, stage))
    this._buildModel(cropGroup, ud.vegType, ud.seedName, ud.growthStage)
  }

  _spawnParticles(x, z) {
    const count = 6
    const particles = []
    for (let i = 0; i < count; i++) {
      const geo = new THREE.SphereGeometry(0.03, 4, 4)
      const mat = new THREE.MeshBasicMaterial({ color: 0x5c4033 })
      const p = new THREE.Mesh(geo, mat)
      p.position.set(x, 0.05, z)
      p.userData = {
        velX: (Math.random() - 0.5) * 0.12,
        velY: Math.random() * 0.1 + 0.04,
        velZ: (Math.random() - 0.5) * 0.12,
        life: 1.0
      }
      this.scene.add(p)
      particles.push(p)
    }

    const start = performance.now()
    const duration = 500
    const animate = (now) => {
      const dt = Math.min((now - start) / duration, 1)
      let alive = false
      particles.forEach(p => {
        if (p.userData.life <= 0) return
        p.userData.velY -= 0.002
        p.position.x += p.userData.velX
        p.position.y += p.userData.velY
        p.position.z += p.userData.velZ
        p.userData.life -= 0.025
        p.material.opacity = p.userData.life
        p.material.transparent = true
        if (p.userData.life > 0) alive = true
      })
      if (alive && dt < 1) {
        requestAnimationFrame(animate)
      } else {
        particles.forEach(p => this.scene.remove(p))
      }
    }
    requestAnimationFrame(animate)
  }

  waterPlant(cropGroup) {
    const originals = []
    cropGroup.children.forEach(child => {
      if (child.material?.color && child.name !== '_mound') {
        originals.push({ child, color: child.material.color.getHex() })
        child.material.color.setHex(0x3cb84c)
      }
    })
    if (cropGroup.userData) {
      cropGroup.userData.watered = true
    }
    setTimeout(() => {
      originals.forEach(({ child, color }) => {
        if (child.material?.color) child.material.color.setHex(color)
      })
    }, 1000)
  }

  waterAll() {
    this.crops.forEach(c => this.waterPlant(c))
  }

  getCropAt(x, z) {
    const threshold = 0.6
    for (const crop of this.crops) {
      const dx = crop.position.x - x
      const dz = crop.position.z - z
      if (Math.sqrt(dx * dx + dz * dz) < threshold) {
        return crop
      }
    }
    return null
  }

  highlightCrops(filterFn) {
    this.crops.forEach(c => {
      const eligible = filterFn ? filterFn(c) : true
      if (eligible && !c.userData._highlightRing) {
        const ringGeo = new THREE.RingGeometry(0.35, 0.45, 32)
        ringGeo.rotateX(-Math.PI / 2)
        const ringMat = new THREE.MeshBasicMaterial({ color: 0xffff00, side: THREE.DoubleSide, transparent: true, opacity: 0.7 })
        const ring = new THREE.Mesh(ringGeo, ringMat)
        ring.position.y = 0.07
        ring.name = '_highlightRing'
        c.add(ring)
        c.userData._highlightRing = ring
      } else if (!eligible && c.userData._highlightRing) {
        c.remove(c.userData._highlightRing)
        c.userData._highlightRing = null
      }
    })
  }

  clearHighlights() {
    this.crops.forEach(c => {
      if (c.userData._highlightRing) {
        c.remove(c.userData._highlightRing)
        c.userData._highlightRing = null
      }
    })
  }

  removeCrop(cropGroup) {
    const idx = this.crops.indexOf(cropGroup)
    if (idx >= 0) this.crops.splice(idx, 1)
    this.scene.remove(cropGroup)
  }
}
