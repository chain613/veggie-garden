import api from './index'

export function loginApi(phone) {
  return api.post('/user/login', { phone }).then(r => r.data)
}

export function getProfile() {
  return api.get('/user/profile').then(r => r.data)
}
