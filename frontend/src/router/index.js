import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'login', component: () => import('../views/LoginView.vue') },
  { path: '/garden', name: 'garden', component: () => import('../views/GardenView.vue') },
  { path: '/inventory', name: 'inventory', component: () => import('../views/InventoryView.vue') },
  { path: '/knowledge', name: 'knowledge', component: () => import('../views/KnowledgeView.vue') },
  { path: '/social', name: 'social', component: () => import('../views/SocialView.vue') },
  { path: '/social/visit/:gardenId', name: 'visit', component: () => import('../views/VisitView.vue') },
  { path: '/warehouse', name: 'warehouse', component: () => import('../views/WarehouseView.vue') },
  { path: '/minigame/:type', name: 'minigame', component: () => import('../views/MiniGameView.vue') },
  { path: '/practice', name: 'practiceList', component: () => import('../components/practice/PracticeEntry.vue') },
  { path: '/practice/:type', name: 'practice', component: () => import('../views/PracticeView.vue') },
  { path: '/', redirect: '/login' }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
