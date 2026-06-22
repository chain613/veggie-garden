import * as THREE from 'three'

export function createTerrain(scene, region) {
  // 主地面（草地）
  const groundGeo = new THREE.PlaneGeometry(80, 80)
  const groundMat = new THREE.MeshLambertMaterial({ color: 0x7cbd6e })
  const ground = new THREE.Mesh(groundGeo, groundMat)
  ground.rotation.x = -Math.PI / 2
  ground.receiveShadow = true
  ground.name = 'ground'
  scene.add(ground)

  // 菜地区域（深色土壤，15×15 格）
  const soilGeo = new THREE.PlaneGeometry(15, 15)
  const soilMat = new THREE.MeshLambertMaterial({ color: 0x5c4033 })
  const soil = new THREE.Mesh(soilGeo, soilMat)
  soil.rotation.x = -Math.PI / 2
  soil.position.set(2, 0.02, 2)
  soil.receiveShadow = true
  soil.name = 'soil'
  scene.add(soil)

  // 网格线
  createGrid(scene)

  // 远景田地
  createDistantField(scene, region)

  return { ground, soil }
}

function createGrid(scene) {
  const gridSize = 15
  const cellSize = 1
  const material = new THREE.LineBasicMaterial({ color: 0x8b7355, transparent: true, opacity: 0.3 })

  for (let i = 0; i <= gridSize; i++) {
    const offset = i - gridSize / 2
    // 横向线
    const hGeo = new THREE.BufferGeometry().setFromPoints([
      new THREE.Vector3(2 - gridSize / 2, 0.03, 2 + offset),
      new THREE.Vector3(2 + gridSize / 2, 0.03, 2 + offset)
    ])
    scene.add(new THREE.Line(hGeo, material))
    // 纵向线
    const vGeo = new THREE.BufferGeometry().setFromPoints([
      new THREE.Vector3(2 + offset, 0.03, 2 - gridSize / 2),
      new THREE.Vector3(2 + offset, 0.03, 2 + gridSize / 2)
    ])
    scene.add(new THREE.Line(vGeo, material))
  }

  // 高亮中心点
  const dotGeo = new THREE.SphereGeometry(0.15, 8, 8)
  const dotMat = new THREE.MeshBasicMaterial({ color: 0x8b4513 })
  for (let i = 0; i <= gridSize; i++) {
    for (let j = 0; j <= gridSize; j++) {
      const dot = new THREE.Mesh(dotGeo, dotMat)
      dot.position.set(2 + i - gridSize / 2, 0.04, 2 + j - gridSize / 2)
      scene.add(dot)
    }
  }
}

function createDistantField(scene, region) {
  const isZhongyuan = region === 'zhongyuan'
  const fieldColor = isZhongyuan ? 0xd4a843 : 0x4a7c3f

  const fieldGeo = new THREE.PlaneGeometry(60, 20)
  const fieldMat = new THREE.MeshLambertMaterial({ color: fieldColor })
  const field = new THREE.Mesh(fieldGeo, fieldMat)
  field.rotation.x = -Math.PI / 2
  field.position.set(0, 0.01, -25)
  field.receiveShadow = true
  field.name = 'distantField'
  scene.add(field)
}
