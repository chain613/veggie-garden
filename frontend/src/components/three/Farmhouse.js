import * as THREE from 'three'

export function createFarmhouse(scene) {
  // 玩家小屋
  const houseGroup = new THREE.Group()
  const wallGeo = new THREE.BoxGeometry(4, 3, 5)
  const wallMat = new THREE.MeshLambertMaterial({ color: 0xf5deb3 })
  const walls = new THREE.Mesh(wallGeo, wallMat)
  walls.position.y = 1.5
  walls.castShadow = true
  walls.receiveShadow = true
  houseGroup.add(walls)

  // 屋顶
  const roofGeo = new THREE.ConeGeometry(3.5, 2, 4)
  const roofMat = new THREE.MeshLambertMaterial({ color: 0x8b4513 })
  const roof = new THREE.Mesh(roofGeo, roofMat)
  roof.position.y = 4
  roof.rotation.y = Math.PI / 4
  roof.castShadow = true
  houseGroup.add(roof)

  houseGroup.position.set(-8, 0, -5)
  houseGroup.name = 'playerHouse'
  scene.add(houseGroup)

  // NPC 小屋
  const npcHouse = houseGroup.clone()
  npcHouse.position.set(10, 0, -5)
  npcHouse.name = 'npcHouse'
  scene.add(npcHouse)
}
