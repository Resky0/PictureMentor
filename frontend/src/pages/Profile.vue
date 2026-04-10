<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userApi, type User } from '@/api'

const router = useRouter()
const user = ref<User | null>(null)
const isEditing = ref(false)
const editUsername = ref('')

const loadProfile = async () => {
  try {
    const res = await userApi.getProfile()
    user.value = res.data
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

const saveProfile = async () => {
  if (!user.value) return
  
  try {
    const res = await userApi.updateProfile({ username: editUsername.value })
    user.value = res.data
    isEditing.value = false
  } catch (error) {
    console.error('更新用户信息失败:', error)
    alert('更新失败，请重试')
  }
}

const startEdit = () => {
  if (user.value) {
    editUsername.value = user.value.username
    isEditing.value = true
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<template>
  <div class="min-h-screen bg-secondary-beige pb-24">
    <div class="gradient-bg text-white py-16 px-4">
      <div class="max-w-4xl mx-auto text-center">
        <button @click="router.push('/')" class="absolute top-4 left-4 text-white hover:opacity-80">
          ← 返回
        </button>
        <div class="w-24 h-24 bg-white/20 rounded-full mx-auto mb-6 flex items-center justify-center text-5xl">
          👤
        </div>
        <div v-if="user" class="space-y-2">
          <div v-if="!isEditing" class="flex items-center justify-center gap-3">
            <h1 class="font-display text-2xl font-bold">{{ user.username }}</h1>
            <button @click="startEdit" class="text-white/80 hover:text-white">✏️</button>
          </div>
          <div v-else class="flex items-center justify-center gap-3">
            <input 
              v-model="editUsername"
              class="px-4 py-2 rounded-lg text-secondary-gray w-48"
              @keyup.enter="saveProfile"
            />
            <button @click="saveProfile" class="text-white/80 hover:text-white">✓</button>
            <button @click="isEditing = false" class="text-white/80 hover:text-white">✕</button>
          </div>
          <p class="opacity-90">{{ user.email }}</p>
        </div>
      </div>
    </div>

    <div class="max-w-4xl mx-auto px-4 -mt-8">
      <div class="bg-white rounded-2xl p-6 card-shadow">
        <h3 class="font-display text-lg font-semibold text-secondary-gray mb-6">
          我的信息
        </h3>
        
        <div class="space-y-4">
          <div class="flex items-center justify-between py-3 border-b border-gray-100">
            <div class="flex items-center gap-3">
              <span class="text-2xl">📸</span>
              <span class="text-gray-600">照片评分</span>
            </div>
            <span class="text-gray-400">→</span>
          </div>
          
          <div class="flex items-center justify-between py-3 border-b border-gray-100">
            <div class="flex items-center gap-3">
              <span class="text-2xl">📊</span>
              <span class="text-gray-600">历史记录</span>
            </div>
            <span class="text-gray-400">→</span>
          </div>
          
          <div class="flex items-center justify-between py-3 border-b border-gray-100">
            <div class="flex items-center gap-3">
              <span class="text-2xl">⚙️</span>
              <span class="text-gray-600">设置</span>
            </div>
            <span class="text-gray-400">→</span>
          </div>
          
          <div class="flex items-center justify-between py-3">
            <div class="flex items-center gap-3">
              <span class="text-2xl">❓</span>
              <span class="text-gray-600">帮助与反馈</span>
            </div>
            <span class="text-gray-400">→</span>
          </div>
        </div>
      </div>
      
      <div class="mt-6 text-center">
        <p class="text-gray-400 text-sm">PhotoMentor v1.0.0</p>
        <p class="text-gray-400 text-sm mt-1">让每一张照片都不再被嫌弃</p>
      </div>
    </div>

    <nav class="fixed bottom-0 left-0 right-0 bg-white border-t shadow-lg">
      <div class="max-w-md mx-auto flex justify-around py-3">
        <button @click="router.push('/')" class="flex flex-col items-center text-gray-500">
          <span class="text-2xl">🏠</span>
          <span class="text-xs mt-1">首页</span>
        </button>
        <button @click="router.push('/scoring')" class="flex flex-col items-center text-gray-500">
          <span class="text-2xl">📸</span>
          <span class="text-xs mt-1">评分</span>
        </button>
        <button @click="router.push('/guide')" class="flex flex-col items-center text-gray-500">
          <span class="text-2xl">🎯</span>
          <span class="text-xs mt-1">指导</span>
        </button>
        <button @click="router.push('/history')" class="flex flex-col items-center text-gray-500">
          <span class="text-2xl">📊</span>
          <span class="text-xs mt-1">历史</span>
        </button>
        <button @click="router.push('/profile')" class="flex flex-col items-center text-primary-pink">
          <span class="text-2xl">👤</span>
          <span class="text-xs mt-1">我的</span>
        </button>
      </div>
    </nav>
  </div>
</template>
