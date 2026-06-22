import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useKnowledgeStore = defineStore('knowledge', () => {
  const vegetables = ref([
    { id: 1, name: '白菜', icon: '🥬', season: '春秋', water: '中', light: '中', desc: '喜冷凉，播种后40-60天采收', unlocked: true },
    { id: 2, name: '菠菜', icon: '🥬', season: '春秋', water: '中', light: '中', desc: '耐寒性强，富含铁质', unlocked: true },
    { id: 3, name: '黄瓜', icon: '🥒', season: '春夏', water: '高', light: '高', desc: '需搭架，喜温暖湿润', unlocked: false },
    { id: 4, name: '番茄', icon: '🍅', season: '春夏', water: '中', light: '高', desc: '需打顶打岔，喜光', unlocked: false },
    { id: 5, name: '胡萝卜', icon: '🥕', season: '春秋', water: '中', light: '中', desc: '根茎类，喜疏松土壤', unlocked: false },
    { id: 6, name: '辣椒', icon: '🌶️', season: '春夏', water: '中', light: '高', desc: '喜温暖，不耐寒', unlocked: false },
    { id: 7, name: '茄子', icon: '🍆', season: '春夏', water: '高', light: '高', desc: '喜高温，需充足水分', unlocked: false },
    { id: 8, name: '豆角', icon: '🫘', season: '春夏', water: '中', light: '高', desc: '需搭架，固氮作物', unlocked: false },
    { id: 9, name: '南瓜', icon: '🎃', season: '春夏', water: '中', light: '高', desc: '蔓生，需大空间', unlocked: false },
    { id: 10, name: '萝卜', icon: '🥕', season: '春秋', water: '中', light: '中', desc: '根茎类，生长快', unlocked: false }
  ])

  const unlockedCount = ref(2)

  function unlock(id) {
    const v = vegetables.value.find(v => v.id === id)
    if (v && !v.unlocked) {
      v.unlocked = true
      unlockedCount.value++
    }
  }

  function loadUnlocked(ids) {
    ids.forEach(id => unlock(id))
  }

  return { vegetables, unlockedCount, unlock, loadUnlocked }
})
