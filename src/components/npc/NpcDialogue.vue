<template>
  <div class="dialogue-panel" v-if="visible">
    <div class="dialogue-header">
      <span>{{ npcName }}</span>
      <button @click="close">✕</button>
    </div>
    <div class="dialogue-body">
      <p>{{ displayText }}</p>
      <div class="dialogue-actions" v-if="knowledgeLinks.length">
        <button v-for="k in knowledgeLinks" :key="k.id" @click="openKnowledge(k)">
          📖 {{ k.title }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getDialogApi, unlockKnowledgeApi } from '../../api/knowledge'

const props = defineProps({
  npcName: { type: String, default: '' },
  visible: { type: Boolean, default: false },
  triggerEvent: { type: String, default: '日常问候' },
  vegetableId: { type: Number, default: null },
  season: { type: String, default: '' }
})

const emit = defineEmits(['close'])
const displayText = ref('')
const knowledgeLinks = ref([])
let typewriterTimer = null

watch(() => props.visible, async (v) => {
  if (v) {
    const res = await getDialogApi(props.triggerEvent, props.vegetableId, props.season)
    if (res?.found) {
      typewriteText(res.dialog?.dialogText || '')
      knowledgeLinks.value = res.dialog?.knowledgeLinks || []
    } else {
      displayText.value = '...'
      knowledgeLinks.value = []
    }
  } else {
    displayText.value = ''
    knowledgeLinks.value = []
    if (typewriterTimer) clearInterval(typewriterTimer)
  }
})

function typewriteText(text) {
  displayText.value = ''
  let i = 0
  if (typewriterTimer) clearInterval(typewriterTimer)
  typewriterTimer = setInterval(() => {
    if (i < text.length) {
      displayText.value += text[i]
      i++
    } else {
      clearInterval(typewriterTimer)
    }
  }, 50)
}

async function openKnowledge(k) {
  await unlockKnowledgeApi(k.id)
}

function close() {
  emit('close')
}
</script>

<style scoped>
.dialogue-panel {
  position: fixed; bottom: 200px; left: 20px; right: 20px;
  background: rgba(255,255,240,0.95); border-radius: 12px;
  padding: 16px; z-index: 200; box-shadow: 0 4px 20px rgba(0,0,0,0.2);
}
.dialogue-header {
  display: flex; justify-content: space-between;
  font-weight: bold; margin-bottom: 8px;
}
.dialogue-header button {
  background: none; border: none; font-size: 18px; cursor: pointer;
}
.dialogue-body p { line-height: 1.6; min-height: 24px; }
.dialogue-actions { display: flex; gap: 8px; margin-top: 12px; }
.dialogue-actions button {
  padding: 6px 12px; border-radius: 6px;
  background: #5c4033; color: white; border: none; cursor: pointer;
}
</style>
