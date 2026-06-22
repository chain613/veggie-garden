import * as THREE from 'three'

export function createTrees(scene, region) {
  const isZhongyuan = region === 'zhongyuan'
  const trunkColor = isZhongyuan ? 0x8b7355 : 0x6b5b4f
  const leafColor = isZhongyuan ? 0x4a7c3f : 0x3d6b34

  const treePositions = [
    [-9, 0, 12], [0, 0, 12], [9, 0, 12],
    [12, 0, -2], [12, 0, 8], [-12, 0, -2], [-12, 0, 8]
  ]

  treePositions.forEach(([x, y, z]) => {
    createTree(scene, x, y, z, trunkColor, leafColor)
  })
}

function createTree(scene, x, y, z, trunkColor, leafColor) {
  const treeGroup = new THREE.Group()

  const trunkGeo = new THREE.CylinderGeometry(0.2, 0.3, 3, 8)
  const trunkMat = new THREE.MeshLambertMaterial({ color: trunkColor })
  const trunk = new THREE.Mesh(trunkGeo, trunkMat)
  trunk.position.y = 1.5
  trunk.castShadow = true
  treeGroup.add(trunk)

  for (let i = 0; i < 3; i++) {
    const crownGeo = new THREE.SphereGeometry(1.8 - i * 0.4, 8, 8)
    const crownMat = new THREE.MeshLambertMaterial({ color: leafColor })
    const crown = new THREE.Mesh(crownGeo, crownMat)
    crown.position.y = 3 + i * 1.2
    crown.position.x = (Math.random() - 0.5) * 0.5
    crown.castShadow = true
    treeGroup.add(crown)
  }

  treeGroup.position.set(x, y, z)
  treeGroup.name = 'tree'
  scene.add(treeGroup)
}
