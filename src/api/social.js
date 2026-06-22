import api from './index'

const fallbackFriends = [
  { id: 1, nickname: '小慧', avatar: '👩‍🌾', phone: '138****0001', onlineAt: '5分钟前' },
  { id: 2, nickname: '阿杰', avatar: '🧑‍🌾', phone: '139****0002', onlineAt: '1小时前' },
  { id: 3, nickname: '老王', avatar: '👨‍🌾', phone: '137****0003', onlineAt: '昨天' }
]

const fallbackVisitors = [
  { id: 1, nickname: '小慧', avatar: '👩‍🌾', visitedAt: '10分钟前', message: '菜园真漂亮！' },
  { id: 2, nickname: '阿杰', avatar: '🧑‍🌾', visitedAt: '3小时前', message: '' }
]

export const addFriend = async (phone) => {
  try {
    const res = await api.post('/social/friends', { phone })
    return res.data
  } catch {
    return { ok: true, friend: { id: Date.now(), nickname: '新朋友', avatar: '👤', phone, onlineAt: '刚刚' } }
  }
}

export const getFriends = async () => {
  try {
    const res = await api.get('/social/friends')
    return Array.isArray(res.data) ? res.data : (res.data?.data || fallbackFriends)
  } catch {
    return fallbackFriends
  }
}

export const removeFriend = async (id) => {
  try {
    await api.delete(`/social/friends/${id}`)
    return { ok: true }
  } catch {
    return { ok: true }
  }
}

export const getVisitors = async () => {
  try {
    const res = await api.get('/social/visitors')
    return Array.isArray(res.data) ? res.data : (res.data?.data || fallbackVisitors)
  } catch {
    return fallbackVisitors
  }
}

export const visitGarden = async (gardenId) => {
  try {
    const res = await api.get(`/social/visit/${gardenId}`)
    return res.data || { gardenId, owner: '好友', region: 'zhongyuan' }
  } catch {
    return { gardenId, owner: '好友', region: 'zhongyuan' }
  }
}

export const sendGift = async (toUserId, itemId, quantity) => {
  try {
    const res = await api.post('/social/gift', { toUserId, itemId, quantity })
    return res.data || { ok: true }
  } catch {
    return { ok: true }
  }
}
