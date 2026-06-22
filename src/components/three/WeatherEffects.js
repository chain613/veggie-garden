import * as THREE from 'three'

export class WeatherEffects {
  constructor(scene) {
    this.scene = scene
    this.rainSystem = null
    this.currentWeather = 'sunny'
  }

  setWeather(weather) {
    this.currentWeather = weather
    this.clearEffects()

    switch (weather) {
      case 'rain':
        this.createRain()
        this.scene.fog.density = 0.008
        break
      case 'wind':
        this.scene.fog.density = 0.003
        break
      case 'fog':
        this.scene.fog.density = 0.015
        break
      default:
        this.scene.fog.density = 0.004
    }
  }

  createRain() {
    const count = 800
    const geo = new THREE.BufferGeometry()
    const positions = new Float32Array(count * 3)
    for (let i = 0; i < count; i++) {
      positions[i * 3] = (Math.random() - 0.5) * 60
      positions[i * 3 + 1] = Math.random() * 30
      positions[i * 3 + 2] = (Math.random() - 0.5) * 60
    }
    geo.setAttribute('position', new THREE.BufferAttribute(positions, 3))
    const mat = new THREE.PointsMaterial({
      color: 0xaaccff,
      size: 0.15,
      transparent: true,
      opacity: 0.6,
      blending: THREE.AdditiveBlending,
      depthWrite: false
    })
    this.rainSystem = new THREE.Points(geo, mat)
    this.rainSystem.name = 'rain'
    this.scene.add(this.rainSystem)
  }

  clearEffects() {
    if (this.rainSystem) {
      this.scene.remove(this.rainSystem)
      this.rainSystem.geometry.dispose()
      this.rainSystem.material.dispose()
      this.rainSystem = null
    }
  }

  update(delta) {
    if (this.currentWeather === 'rain' && this.rainSystem) {
      const positions = this.rainSystem.geometry.attributes.position.array
      for (let i = 0; i < positions.length; i += 3) {
        positions[i + 1] -= 0.3
        if (positions[i + 1] < 0) {
          positions[i + 1] = 25 + Math.random() * 5
        }
      }
      this.rainSystem.geometry.attributes.position.needsUpdate = true
    }
  }

  dispose() {
    this.clearEffects()
  }
}
