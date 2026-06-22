import * as THREE from 'three'

export function createFence(scene) {
  const fenceGroup = new THREE.Group()
  fenceGroup.name = 'fence'

  const postGeo = new THREE.CylinderGeometry(0.08, 0.1, 1.2, 8)
  const postMat = new THREE.MeshLambertMaterial({ color: 0x8b6b4a })
  const railGeo = new THREE.BoxGeometry(1.5, 0.06, 0.06)
  const railMat = new THREE.MeshLambertMaterial({ color: 0xa0825a })

  // 院子围栏区域: 从 playerHouse 到 npcHouse，前方延伸
  const cx = 1, cz = -5
  const w = 20, d = 8  // 宽×深

  for (let x = -w / 2; x <= w / 2; x += 1.5) {
    // 前排
    addPost(x, cz + d / 2)
    // 后排
    addPost(x, cz - d / 2)
    // 横杆前排
    if (x < w / 2) {
      addRail(x + 0.75, cz + d / 2)
      addRail(x + 0.75, cz - d / 2)
    }
  }
  for (let z = -d / 2; z <= d / 2; z += 1.5) {
    // 左排
    addPost(-w / 2, cz + z)
    if (z < d / 2) addRail(-w / 2, cz + z + 0.75)
    // 右排
    addPost(w / 2, cz + z)
    if (z < d / 2) addRail(w / 2, cz + z + 0.75)
  }

  scene.add(fenceGroup)

  function addPost(x, z) {
    const post = new THREE.Mesh(postGeo, postMat)
    post.position.set(cx + x, 0.6, z)
    post.castShadow = true
    fenceGroup.add(post)
  }

  function addRail(x, z) {
    const rail = new THREE.Mesh(railGeo, railMat)
    rail.position.set(cx + x, 0.55, z)
    fenceGroup.add(rail)
    // 上横杆
    const railTop = new THREE.Mesh(railGeo, railMat)
    railTop.position.set(cx + x, 0.95, z)
    fenceGroup.add(railTop)
  }
}
