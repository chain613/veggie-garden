<template>
  <nav class="bottom-nav">
    <router-link v-for="tab in tabs" :key="tab.path" :to="tab.path" class="nav-item" active-class="active">
      <span class="nav-icon">{{ tab.icon }}</span>
      <span class="nav-label">{{ tab.label }}</span>
    </router-link>
  </nav>
</template>

<script setup>
const tabs = [
  { path: '/garden', icon: '🌱', label: '菜园' },
  { path: '/inventory', icon: '🎒', label: '背包' },
  { path: '/warehouse', icon: '🏠', label: '仓库' },
  { path: '/knowledge', icon: '📖', label: '知识' },
  { path: '/social', icon: '👥', label: '好友' }
]
</script>

<style scoped>
.bottom-nav {
  position: fixed; bottom: 0; left: 0; right: 0; z-index: 500;
  display: flex; justify-content: space-around; align-items: center;
  background: rgba(255,255,255,0.85); backdrop-filter: blur(12px);
  border-top: 1px solid rgba(0,0,0,0.06); padding: 6px 0 10px;
}
.nav-item {
  display: flex; flex-direction: column; align-items: center;
  gap: 2px; text-decoration: none; padding: 4px 12px;
  border-radius: 10px; transition: background 0.2s;
}
.nav-item:hover { background: rgba(92,64,51,0.06); }
.nav-item.active { background: rgba(92,64,51,0.1); }
.nav-icon { font-size: 22px; }
.nav-label { font-size: 10px; color: #888; }
.nav-item.active .nav-label { color: #5c4033; font-weight: bold; }
</style>
