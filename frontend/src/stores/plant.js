import { defineStore } from 'pinia'
import { ref } from 'vue'

export const usePlantStore = defineStore('plant', () => {
  const plants = ref([])
  const selectedPlant = ref(null)

  function loadPlants(data) { plants.value = data || [] }
  function selectPlant(plant) { selectedPlant.value = plant }
  function clearSelection() { selectedPlant.value = null }

  return { plants, selectedPlant, loadPlants, selectPlant, clearSelection }
})
