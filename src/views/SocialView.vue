<template>
  <div class="social-view">
    <div class="social-header">
      <button class="back-btn" @click="$router.push('/garden')">← 返回</button>
      <h2>👥 好友</h2>
    </div>

    <!-- 搜索添加 -->
    <div class="search-bar">
      <input
        v-model="searchPhone"
        type="text"
        placeholder="输入手机号搜索添加好友"
        @keyup.enter="handleAddFriend"
      />
      <button class="add-btn" :disabled="!searchPhone.trim()" @click="handleAddFriend">添加</button>
    </div>
    <p v-if="searchError" class="error-msg">{{ searchError }}</p>
    <p v-if="searchOk" class="ok-msg">{{ searchOk }}</p>

    <!-- 好友列表 -->
    <div class="section">
      <h3>好友列表</h3>
      <div v-if="store.loading" class="loading">加载中...</div>
      <div v-else-if="store.friends.length === 0" class="empty">
        <p>暂无好友</p>
        <p class="hint">输入手机号添加好友</p>
      </div>
      <div v-else class="friend-list">
        <div
          v-for="f in store.friends" :key="f.id"
          class="friend-item" @click="selectedFriend = f"
        >
          <span class="friend-avatar">{{ f.avatar || '👤' }}</span>
          <div class="friend-info">
            <span class="friend-name">{{ f.nickname }}</span>
            <span class="friend-phone">{{ f.phone }}</span>
          </div>
          <span class="friend-online">{{ f.onlineAt }}</span>
        </div>
      </div>
    </div>

    <!-- 访客记录 -->
    <div class="section">
      <h3>最近访客</h3>
      <div v-if="visitors.length === 0" class="empty">暂无访客</div>
      <div v-else class="visitor-list">
        <div v-for="v in visitors" :key="v.id" class="visitor-item">
          <span class="visitor-avatar">{{ v.avatar || '👤' }}</span>
          <div class="visitor-info">
            <span class="visitor-name">{{ v.nickname }}</span>
            <span class="visitor-msg" v-if="v.message">{{ v.message }}</span>
          </div>
          <span class="visitor-time">{{ v.visitedAt }}</span>
        </div>
      </div>
    </div>

    <!-- 好友操作菜单 -->
    <div v-if="selectedFriend" class="action-overlay" @click.self="selectedFriend = null">
      <div class="action-sheet">
        <h4>{{ selectedFriend.nickname }}</h4>
        <button class="action-btn" @click="visitFriend">🏡 参观菜园</button>
        <button class="action-btn" @click="showGift = true">🎁 送礼物</button>
        <button class="action-btn danger" @click="handleRemoveFriend">🗑 删除好友</button>
        <button class="action-btn cancel" @click="selectedFriend = null">取消</button>
      </div>
    </div>

    <!-- 送礼弹窗 -->
    <GiftDialog
      v-if="showGift && selectedFriend"
      :friend-id="selectedFriend.id"
      :friend-name="selectedFriend.nickname"
      @close="showGift = false"
      @sent="onGiftSent"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useSocialStore } from '../stores/social'
import { getVisitors } from '../api/social'
import GiftDialog from '../components/social/GiftDialog.vue'

const router = useRouter()
const store = useSocialStore()

const searchPhone = ref('')
const searchError = ref('')
const searchOk = ref('')
const selectedFriend = ref(null)
const showGift = ref(false)
const visitors = ref([])

onMounted(() => {
  store.fetchFriends()
  fetchVisitors()
})

async function fetchVisitors() {
  visitors.value = await getVisitors()
}

async function handleAddFriend() {
  const phone = searchPhone.value.trim()
  if (!phone) return
  searchError.value = ''
  searchOk.value = ''

  if (!/^1\d{10}$/.test(phone)) {
    searchError.value = '请输入正确的11位手机号'
    return
  }

  const result = await store.doAddFriend(phone)
  if (result.ok) {
    searchOk.value = '添加成功！'
    searchPhone.value = ''
    setTimeout(() => { searchOk.value = '' }, 2000)
  } else {
    searchError.value = result.error || '添加失败，请重试'
  }
}

function visitFriend() {
  if (selectedFriend.value) {
    router.push(`/social/visit/${selectedFriend.value.id}`)
  }
  selectedFriend.value = null
}

async function handleRemoveFriend() {
  if (selectedFriend.value) {
    await store.doRemoveFriend(selectedFriend.value.id)
  }
  selectedFriend.value = null
}

function onGiftSent({ item, qty }) {
  // 可选：显示赠送成功提示
}
</script>

<style scoped>
.social-view {
  min-height: 100vh; background: linear-gradient(180deg, #e8f5e9, #c8e6c9);
  padding: 20px; padding-top: 60px;
}
.social-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.back-btn {
  background: rgba(255,255,255,0.6); border: none; border-radius: 8px;
  padding: 6px 12px; cursor: pointer; font-size: 14px;
}
h2 { margin: 0; }

.search-bar { display: flex; gap: 8px; margin-bottom: 8px; }
.search-bar input {
  flex: 1; padding: 10px 14px; border: 2px solid #c8e6c9;
  border-radius: 10px; font-size: 14px; outline: none;
}
.search-bar input:focus { border-color: #66bb6a; }
.add-btn {
  padding: 10px 20px; background: #4caf50; color: white;
  border: none; border-radius: 10px; cursor: pointer;
}
.add-btn:disabled { background: #ccc; cursor: default; }
.error-msg { color: #e53935; font-size: 13px; margin-bottom: 4px; }
.ok-msg { color: #4caf50; font-size: 13px; margin-bottom: 4px; }

.section { margin-top: 20px; }
.section h3 { font-size: 16px; margin-bottom: 10px; color: #333; }
.loading, .empty { text-align: center; padding: 24px; color: #999; }
.hint { font-size: 13px; color: #bbb; margin-top: 4px; }

.friend-list, .visitor-list {
  display: flex; flex-direction: column; gap: 8px;
}
.friend-item, .visitor-item {
  display: flex; align-items: center; gap: 12px;
  background: rgba(255,255,255,0.85); border-radius: 12px;
  padding: 12px 16px; cursor: pointer;
}
.friend-item:hover { background: rgba(255,255,255,1); }
.friend-avatar, .visitor-avatar { font-size: 32px; }
.friend-info, .visitor-info { flex: 1; display: flex; flex-direction: column; }
.friend-name, .visitor-name { font-weight: bold; font-size: 15px; }
.friend-phone, .visitor-msg { font-size: 12px; color: #888; }
.friend-online, .visitor-time { font-size: 12px; color: #aaa; }

.action-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.4);
  display: flex; align-items: flex-end; justify-content: center; z-index: 250;
}
.action-sheet {
  background: white; border-radius: 16px 16px 0 0; width: 100%;
  max-width: 400px; padding: 24px 20px 32px;
  display: flex; flex-direction: column; gap: 8px;
}
.action-sheet h4 { text-align: center; margin: 0 0 8px; }
.action-btn {
  padding: 14px; border-radius: 10px; border: none;
  font-size: 15px; cursor: pointer; background: #f5f5f5; text-align: center;
}
.action-btn:hover { background: #e8e8e8; }
.action-btn.danger { color: #e53935; }
.action-btn.cancel { background: white; color: #666; border: 1px solid #ddd; }
</style>
