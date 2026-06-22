import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getFriends, addFriend, removeFriend } from '../api/social'

export const useSocialStore = defineStore('social', () => {
  const friends = ref([])
  const loading = ref(false)

  async function fetchFriends() {
    loading.value = true
    try {
      friends.value = await getFriends()
    } finally {
      loading.value = false
    }
  }

  async function doAddFriend(phone) {
    const result = await addFriend(phone)
    if (result.ok) {
      await fetchFriends()
    }
    return result
  }

  async function doRemoveFriend(id) {
    await removeFriend(id)
    friends.value = friends.value.filter(f => f.id !== id)
  }

  return { friends, loading, fetchFriends, doAddFriend, doRemoveFriend }
})
