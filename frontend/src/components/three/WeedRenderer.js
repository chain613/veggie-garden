import * as THREE from 'three'

export class WeedRenderer {
  constructor(scene) {
    this.scene = scene
    this.weedGroup = new THREE.Group()
    this.weedGroup.name = 'weeds'
    scene.add(this.weedGroup)
  }

  renderWeeds(weeds) {
    while (this.weedGroup.children.length > 0) {
      this.weedGroup.remove(this.weedGroup.children[0])
    }

    for (const w of weeds) {
      const posX = w.gridX - 7
      const posZ = w.gridY - 7

      const group = new THREE.Group()

      const stemGeo = new THREE.CylinderGeometry(0.02, 0.02, 0.3, 4)
      const stemMat = new THREE.MeshLambertMaterial({ color: 0x5a7d3a })
      const stem = new THREE.Mesh(stemGeo, stemMat)
      stem.position.y = 0.15
      group.add(stem)

      const stage = w.growthStage || 0
      const leafSize = 0.08 + stage * 0.06
      const leafGeo = new THREE.ConeGeometry(leafSize, 0.2, 6)
      const leafMat = new THREE.MeshLambertMaterial({
        color: stage >= 2 ? 0x8b7a3a : 0x5a8a3a
      })
      const leaf = new THREE.Mesh(leafGeo, leafMat)
      leaf.position.y = 0.35
      group.add(leaf)

      group.position.set(posX + 2, 0.05, posZ + 2)
      this.weedGroup.add(group)
    }
  }

  clear() {
    while (this.weedGroup.children.length > 0) {
      this.weedGroup.remove(this.weedGroup.children[0])
    }
  }
}
