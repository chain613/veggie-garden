import * as THREE from 'three'

export class PlayerController {
  constructor(camera, scene, targetEl) {
    this.camera = camera
    this.scene = scene
    this.targetEl = targetEl || document
    this.moveSpeed = 0.35
    this.rotateSpeed = 0.01

    this.camera.position.set(0, 1.6, 8)
    this.euler = new THREE.Euler(0, Math.PI, 0, 'YXZ')
    this.camera.rotation.copy(this.euler)

    this.keys = { forward: false, back: false, left: false, right: false }
    this.joystickX = 0
    this.joystickY = 0
    this.isPointerDown = false
    this.lastPointerX = 0
    this.lastPointerY = 0
    this.pointerDownX = 0
    this.pointerDownY = 0
    this.totalDragX = 0
    this.totalDragY = 0
    this.clickCallback = null

    this.createPlayerMarker()

    this.initKeyboard()
    this.initTouch()
  }

  createPlayerMarker() {
    const ringGeo = new THREE.RingGeometry(0.25, 0.4, 32)
    ringGeo.rotateX(-Math.PI / 2)
    const ringMat = new THREE.MeshBasicMaterial({ color: 0xffffff, side: THREE.DoubleSide, transparent: true, opacity: 0.6 })
    this.marker = new THREE.Mesh(ringGeo, ringMat)
    this.marker.position.copy(this.camera.position)
    this.marker.position.y = 0.05
    this.scene.add(this.marker)

    const dotGeo = new THREE.CircleGeometry(0.08, 16)
    dotGeo.rotateX(-Math.PI / 2)
    const dotMat = new THREE.MeshBasicMaterial({ color: 0x44ff44, side: THREE.DoubleSide })
    this.markerDot = new THREE.Mesh(dotGeo, dotMat)
    this.markerDot.position.y = 0.06
    this.marker.add(this.markerDot)
  }

  initKeyboard() {
    const onDown = e => {
      switch (e.key.toLowerCase()) {
        case 'w': this.keys.forward = true; break
        case 's': this.keys.back = true; break
        case 'a': this.keys.left = true; break
        case 'd': this.keys.right = true; break
      }
    }
    const onUp = e => {
      switch (e.key.toLowerCase()) {
        case 'w': this.keys.forward = false; break
        case 's': this.keys.back = false; break
        case 'a': this.keys.left = false; break
        case 'd': this.keys.right = false; break
      }
    }
    document.addEventListener('keydown', onDown)
    document.addEventListener('keyup', onUp)
  }

  initTouch() {
    const el = this.targetEl

    el.addEventListener('pointerdown', e => {
      this.isPointerDown = true
      this.lastPointerX = e.clientX
      this.lastPointerY = e.clientY
      this.pointerDownX = e.clientX
      this.pointerDownY = e.clientY
      this.totalDragX = 0
      this.totalDragY = 0
    })

    el.addEventListener('pointermove', e => {
      if (!this.isPointerDown) return
      const dx = e.clientX - this.lastPointerX
      const dy = e.clientY - this.lastPointerY
      this.totalDragX += Math.abs(dx)
      this.totalDragY += Math.abs(dy)
      this.euler.y -= dx * this.rotateSpeed
      this.euler.x = Math.max(-Math.PI / 3, Math.min(Math.PI / 3,
        this.euler.x - dy * this.rotateSpeed * 0.5))
      this.camera.quaternion.setFromEuler(this.euler)
      this.lastPointerX = e.clientX
      this.lastPointerY = e.clientY
    })

    el.addEventListener('pointerup', e => {
      this.isPointerDown = false
      if (this.totalDragX < 5 && this.totalDragY < 5 && this.clickCallback) {
        this.clickCallback(e)
      }
    })
    el.addEventListener('pointercancel', () => { this.isPointerDown = false })
    el.addEventListener('pointerleave', () => { this.isPointerDown = false })
  }

  onClick(cb) {
    this.clickCallback = cb
  }

  setJoystick(x, y) {
    this.joystickX = x
    this.joystickY = y
  }

  update() {
    const direction = new THREE.Vector3()
    if (this.keys.forward || this.joystickY > 0.1) direction.z -= 1
    if (this.keys.back || this.joystickY < -0.1) direction.z += 1
    if (this.keys.left || this.joystickX < -0.1) direction.x -= 1
    if (this.keys.right || this.joystickX > 0.1) direction.x += 1

    direction.applyQuaternion(this.camera.quaternion)
    direction.y = 0
    direction.normalize()

    this.camera.position.addScaledVector(direction, this.moveSpeed)

    this.camera.position.x = Math.max(-30, Math.min(30, this.camera.position.x))
    this.camera.position.z = Math.max(-30, Math.min(30, this.camera.position.z))

    if (this.marker) {
      this.marker.position.x = this.camera.position.x
      this.marker.position.z = this.camera.position.z
    }
  }
}
