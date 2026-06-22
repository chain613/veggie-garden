import * as THREE from 'three'

export function createSky(scene) {
  // 天穹
  const skyGeo = new THREE.SphereGeometry(50, 32, 32)
  const skyMat = new THREE.ShaderMaterial({
    uniforms: {
      topColor: { value: new THREE.Color(0x0077ff) },
      bottomColor: { value: new THREE.Color(0xc9e8c2) },
      offset: { value: 20 },
      exponent: { value: 0.6 }
    },
    vertexShader: `
      varying vec3 vWorldPosition;
      void main() {
        vec4 worldPosition = modelMatrix * vec4(position, 1.0);
        vWorldPosition = worldPosition.xyz;
        gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
      }`,
    fragmentShader: `
      uniform vec3 topColor;
      uniform vec3 bottomColor;
      uniform float offset;
      uniform float exponent;
      varying vec3 vWorldPosition;
      void main() {
        float h = normalize(vWorldPosition + offset).y;
        gl_FragColor = vec4(mix(bottomColor, topColor,
          max(pow(max(h, 0.0), exponent), 0.0)), 1.0);
      }`,
    side: THREE.BackSide
  })
  const sky = new THREE.Mesh(skyGeo, skyMat)
  sky.name = 'skyDome'
  scene.add(sky)

  // 太阳 — 大而亮，不受雾影响
  const sunGeo = new THREE.SphereGeometry(4, 32, 32)
  const sunMat = new THREE.MeshBasicMaterial({ color: 0xffee88, fog: false })
  const sun = new THREE.Mesh(sunGeo, sunMat)
  sun.position.set(30, 35, -30)
  sun.name = 'sun'
  scene.add(sun)

  // 月亮
  const moonGeo = new THREE.SphereGeometry(2, 32, 32)
  const moonMat = new THREE.MeshBasicMaterial({ color: 0xeeeecc, fog: false })
  const moon = new THREE.Mesh(moonGeo, moonMat)
  moon.position.set(-30, 35, -30)
  moon.visible = false
  moon.name = 'moon'
  scene.add(moon)

  return { sky, sun, moon }
}
