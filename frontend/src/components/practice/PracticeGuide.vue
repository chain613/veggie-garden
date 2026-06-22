<template>
  <div class="practice-guide">
    <div class="guide-header">
      <button class="back-btn" @click="$router.push('/practice')">← 副本列表</button>
    </div>

    <div class="npc-bubble">
      <div class="npc-avatar">👩‍🌾</div>
      <div class="npc-text">
        <p><strong>农艺师小绿：</strong>欢迎来到「{{ practice?.name }}」！</p>
        <p>{{ practice?.description }}</p>
        <p>本副本共 <strong>{{ practice?.steps?.length || 0 }}</strong> 个步骤，准备好了就开始吧！</p>
      </div>
    </div>

    <button class="start-btn" @click="$emit('start')">开始练习</button>
  </div>
</template>

<script setup>
defineProps({
  practice: { type: Object, default: null }
})

defineEmits(['start'])
</script>

<style scoped>
.practice-guide {
  padding: 20px; padding-top: 60px;
  min-height: 100vh; background: linear-gradient(180deg, #e8f5e9, #c8e6c9);
  display: flex; flex-direction: column; align-items: center;
}
.guide-header {
  width: 100%; margin-bottom: 24px;
}
.back-btn {
  background: rgba(255,255,255,0.6); border: none; border-radius: 8px;
  padding: 6px 12px; cursor: pointer; font-size: 14px;
}
.npc-bubble {
  display: flex; gap: 16px; background: white; border-radius: 16px;
  padding: 24px; max-width: 380px; box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.npc-avatar { font-size: 48px; }
.npc-text p { margin: 0 0 8px; line-height: 1.6; }
.start-btn {
  margin-top: 32px; width: 200px; padding: 14px;
  background: #4caf50; color: white; border: none;
  border-radius: 12px; font-size: 18px; cursor: pointer;
}
</style>
