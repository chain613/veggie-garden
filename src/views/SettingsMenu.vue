<template>
  <div class="settings-menu">
    <button class="gear-btn" @click="open = true">⚙️</button>

    <div v-if="open" class="settings-overlay" @click.self="open = false">
      <div class="settings-panel">
        <h3>⚙️ 设置</h3>

        <div class="settings-section">
          <h4>菜园切换</h4>
          <GardenSwitcher
            :gardens="gardens"
            :current-id="currentGardenId"
            @switch="$emit('switchGarden', $event)"
          />
        </div>

        <div class="settings-section">
          <h4>NPC 形象</h4>
          <NpcAvatarSelector
            :modelValue="npcAvatar"
            @update:modelValue="$emit('updateNpcAvatar', $event)"
          />
        </div>

        <button class="close-btn" @click="open = false">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import GardenSwitcher from '../components/ui/GardenSwitcher.vue'
import NpcAvatarSelector from '../components/npc/NpcAvatarSelector.vue'

defineProps({
  gardens: { type: Array, default: () => [] },
  currentGardenId: { type: Number, default: 1 },
  npcAvatar: { type: String, default: 'old_farmer' }
})

defineEmits(['switchGarden', 'updateNpcAvatar'])

const open = ref(false)
</script>

<style scoped>
.gear-btn {
  position: fixed; top: 80px; right: 16px; z-index: 620;
  width: 40px; height: 40px; border-radius: 50%;
  border: none; background: rgba(255,255,255,0.85);
  font-size: 22px; cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex; align-items: center; justify-content: center;
}
.settings-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.4);
  display: flex; align-items: flex-end; justify-content: center; z-index: 800;
}
.settings-panel {
  background: white; border-radius: 16px 16px 0 0;
  width: 100%; max-width: 400px;
  padding: 24px 20px 32px;
}
.settings-panel h3 { margin: 0 0 16px; text-align: center; }
.settings-section { margin-bottom: 16px; }
.settings-section h4 { font-size: 14px; color: #666; margin-bottom: 8px; }
.close-btn {
  display: block; width: 100%; padding: 12px; margin-top: 8px;
  border: 1px solid #ddd; border-radius: 10px;
  background: white; font-size: 14px; cursor: pointer;
}
</style>
