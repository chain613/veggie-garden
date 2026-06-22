import * as THREE from 'three'

export class CropRenderer {
  constructor(scene) {
    this.scene = scene
    this.crops = []
  }

  plant(seed, x, z) {
    const group = new THREE.Group()
    group.name = `crop-${seed.name}`
    group.userData = {
      cropType: 'plant',
      seedId: seed.id,
      seedName: seed.name,
      seedIcon: seed.icon,
      growthStage: 0,
      waterNeed: '中',
      daysToHarvest: seed.days || 5,
      fertilized: false,
      plantedAt: Date.now()
    }

    const stemGeo = new THREE.CylinderGeometry(0.08, 0.1, 0.6, 8)
    const stemMat = new THREE.MeshLambertMaterial({ color: 0x4a7c2e })
    const stem = new THREE.Mesh(stemGeo, stemMat)
    stem.position.y = 0.3
    group.add(stem)

    const leafGeo = new THREE.SphereGeometry(0.2, 8, 8)
    leafGeo.scale(1, 0.5, 1)
    const leafMat = new THREE.MeshLambertMaterial({ color: 0x5cb85c })
    const leaves = new THREE.Mesh(leafGeo, leafMat)
    leaves.position.y = 0.65
    group.add(leaves)

    group.position.set(x, 0.05, z)
    this.scene.add(group)
    this.crops.push(group)
    return group
  }

  waterPlant(cropGroup) {
    cropGroup.children.forEach(child => {
      if (child.material?.color) {
        child.material.color.setHex(0x3cb84c)
      }
    })
    if (cropGroup.userData) {
      cropGroup.userData.watered = true
    }
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
      c.children.forEach(child => {
        if (child.material?.emissive) {
          child.material.emissive.setHex(eligible ? 0x444400 : 0x000000)
        }
      })
      // Add highlight ring for eligible crops
      if (eligible && !c.userData._highlightRing) {
        const ringGeo = new THREE.RingGeometry(0.35, 0.45, 32)
        ringGeo.rotateX(-Math.PI / 2)
        const ringMat = new THREE.MeshBasicMaterial({ color: 0xffff00, side: THREE.DoubleSide, transparent: true, opacity: 0.7 })
        const ring = new THREE.Mesh(ringGeo, ringMat)
        ring.position.y = 0.07
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
      c.children.forEach(child => {
        if (child.material?.emissive) {
          child.material.emissive.setHex(0x000000)
        }
      })
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
