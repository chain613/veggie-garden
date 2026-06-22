import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useNpcStore = defineStore('npc', () => {
  const npcName = ref(localStorage.getItem('npcName') || '老王')
  const avatar = ref(localStorage.getItem('npcAvatar') || 'old_farmer')

  watch(npcName, (v) => localStorage.setItem('npcName', v))
  watch(avatar, (v) => localStorage.setItem('npcAvatar', v))

  function setNpcName(name) { npcName.value = name }
  function setAvatar(a) { avatar.value = a }

  return { npcName, avatar, setNpcName, setAvatar }
})
