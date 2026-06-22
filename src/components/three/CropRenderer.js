import * as THREE from 'three'

export class CropRenderer {
  constructor(scene) {
    this.scene = scene
    this.crops = []
  }

  plant(seed, x, z) {
    const group = new THREE.Group()
    group.name = `crop-${seed.name}`

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

  waterAll() {
    this.crops.forEach(c => {
      c.children.forEach(child => {
        if (child.material?.color) {
          child.material.color.setHex(0x3cb84c)
        }
      })
    })
  }
}
