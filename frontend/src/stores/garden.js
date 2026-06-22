import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useGardenStore = defineStore('garden', () => {
  const gardens = ref([
    { id: 1, name: '中原菜园', region: 'zhongyuan', size: 12 },
    { id: 2, name: '中南菜园', region: 'zhongnan', size: 10 }
  ])
  const currentGarden = ref(gardens.value[0])

  function switchGarden(gardenId) {
    const g = gardens.value.find(g => g.id === gardenId)
    if (g) currentGarden.value = g
  }

  return { gardens, currentGarden, switchGarden }
})
