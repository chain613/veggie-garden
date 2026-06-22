import * as THREE from 'three'

export class RootRenderer {
  constructor(scene) {
    this.scene = scene
    this.rootGroup = new THREE.Group()
    this.rootGroup.name = 'rootSystem'
    this.rootGroup.visible = false
    scene.add(this.rootGroup)
    this.visible = false
  }

  toggle() {
    this.visible = !this.visible
    this.rootGroup.visible = this.visible

    const soil = this.scene.getObjectByName('soil')
    if (soil) {
      soil.material.transparent = true
      soil.material.opacity = this.visible ? 0.3 : 1.0
      soil.material.needsUpdate = true
    }
    return this.visible
  }

  renderAllRoots(plants) {
    while (this.rootGroup.children.length > 0) {
      this.rootGroup.remove(this.rootGroup.children[0])
    }

    for (const plant of plants) {
      this.renderPlantRoots(plant)
    }

    this.detectOverlaps(plants)
  }

  renderPlantRoots(plant) {
    const rootMass = parseFloat(plant.rootMass) || 0.05
    const anchorX = plant.gridX
    const anchorZ = plant.gridY
    const branches = Math.floor(rootMass * 5) + 3
    const maxRadius = rootMass * 0.8

    const rootColor = this.getRootColor(plant)

    for (let i = 0; i < branches; i++) {
      const angle = (i / branches) * Math.PI * 2 + (Math.random() - 0.5) * 0.5
      const length = maxRadius * (0.5 + Math.random() * 0.5)

      const curve = new THREE.CubicBezierCurve3(
        new THREE.Vector3(anchorX, -0.1, anchorZ),
        new THREE.Vector3(anchorX + Math.cos(angle) * length * 0.3, -0.4, anchorZ + Math.sin(angle) * length * 0.3),
        new THREE.Vector3(anchorX + Math.cos(angle) * length * 0.7, -0.8, anchorZ + Math.sin(angle) * length * 0.7),
        new THREE.Vector3(anchorX + Math.cos(angle) * length, -1.2, anchorZ + Math.sin(angle) * length)
      )

      const thickness = rootMass * 0.03 + 0.01
      const tubeGeo = new THREE.TubeGeometry(curve, 12, thickness, 6, false)
      const tubeMat = new THREE.MeshLambertMaterial({ color: rootColor })
      const tube = new THREE.Mesh(tubeGeo, tubeMat)
      this.rootGroup.add(tube)

      const subBranches = Math.floor(Math.random() * 3) + 1
      for (let j = 0; j < subBranches; j++) {
        const midPoint = curve.getPoint(0.4 + Math.random() * 0.5)
        const subAngle = angle + (Math.random() - 0.5) * 1.2
        const subLength = length * (0.2 + Math.random() * 0.3)
        const subGeo = new THREE.BufferGeometry().setFromPoints([
          midPoint,
          new THREE.Vector3(
            midPoint.x + Math.cos(subAngle) * subLength,
            -1.2 - Math.random() * 0.4,
            midPoint.z + Math.sin(subAngle) * subLength
          )
        ])
        const subLine = new THREE.Line(subGeo,
          new THREE.LineBasicMaterial({ color: rootColor, transparent: true, opacity: 0.5 }))
        this.rootGroup.add(subLine)
      }
    }
  }

  getRootColor(plant) {
    const health = parseFloat(plant.healthScore) || 1.0
    const micro = parseFloat(plant.microIndex) || 0.8

    if (health > 0.8 && micro > 0.8) return 0xe8d5b7
    if (health > 0.5) return 0xc4a882
    return 0x8b6b4a
  }

  detectOverlaps(plants) {
    const overlapGeo = new THREE.SphereGeometry(0.2, 8, 8)
    const overlapMat = new THREE.MeshBasicMaterial({
      color: 0xff3333,
      transparent: true,
      opacity: 0.6
    })

    for (let i = 0; i < plants.length; i++) {
      for (let j = i + 1; j < plants.length; j++) {
        const a = plants[i], b = plants[j]
        const dx = a.gridX - b.gridX
        const dy = a.gridY - b.gridY
        const dist = Math.sqrt(dx * dx + dy * dy)
        const overlapDist = (parseFloat(a.rootMass) + parseFloat(b.rootMass)) * 0.8

        if (dist < overlapDist) {
          const midX = (a.gridX + b.gridX) / 2
          const midZ = (a.gridY + b.gridY) / 2
          const marker = new THREE.Mesh(overlapGeo, overlapMat.clone())
          marker.position.set(midX, -0.8, midZ)
          marker.userData = { blink: true, phase: Math.random() * Math.PI * 2 }
          this.rootGroup.add(marker)
        }
      }
    }
  }

  update(time) {
    this.rootGroup.children.forEach(child => {
      if (child.userData?.blink) {
        const alpha = 0.3 + Math.sin(time * 3 + child.userData.phase) * 0.5
        child.material.opacity = Math.max(0, alpha)
      }
    })
  }
}
