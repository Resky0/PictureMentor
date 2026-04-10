<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { historyApi, type Score } from '@/api'

const router = useRouter()
const historyList = ref<Score[]>([])
const isLoading = ref(false)

const loadHistory = async () => {
  isLoading.value = true
  try {
    const res = await historyApi.list()
    historyList.value = res.data.items
  } catch (error) {
    console.error('加载历史记录失败:', error)
  } finally {
    isLoading.value = false
  }
}

const deleteHistory = async (id: number) => {
  if (!confirm('确定要删除这条记录吗？')) return
  
  try {
    await historyApi.delete(id)
    historyList.value = historyList.value.filter(item => item.id !== id)
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败，请重试')
  }
}

const getScoreColor = (score: number) => {
  if (score >= 80) return 'text-green-500'
  if (score >= 60) return 'text-yellow-500'
  return 'text-red-500'
}

const getScoreBg = (score: number) => {
  if (score >= 80) return 'bg-green-100'
  if (score >= 60) return 'bg-yellow-100'
  return 'bg-red-100'
}

onMounted(() => {
  loadHistory()
})
</script>

<template>
  <div class="min-h-screen bg-secondary-beige pb-24">
    <div class="gradient-bg text-white py-8 px-4">
      <div class="max-w-4xl mx-auto">
        <button @click="router.push('/')" class="mb-4 text-white hover:opacity-80">
          ← 返回
        </button>
        <h1 class="font-display text-3xl font-bold">历史记录</h1>
        <p class="mt-2 opacity-90">查看你的进步轨迹</p>
      </div>
    </div>

    <div class="max-w-4xl mx-auto px-4 py-8">
      <div v-if="isLoading" class="text-center py-12">
        <div class="text-4xl animate-spin inline-block">⏳</div>
        <p class="text-gray-500 mt-4">加载中...</p>
      </div>

      <div v-else-if="historyList.length === 0" class="text-center py-16">
        <div class="text-6xl mb-4">📷</div>
        <h3 class="font-display text-xl font-semibold text-secondary-gray mb-2">
          还没有历史记录
        </h3>
        <p class="text-gray-500 mb-6">开始上传照片，记录你的成长吧！</p>
        <button 
          @click="router.push('/scoring')"
          class="btn-primary px-6 py-3 rounded-full text-white font-semibold"
        >
          去评分
        </button>
      </div>

      <div v-else class="space-y-4">
        <div 
          v-for="item in historyList" 
          :key="item.id"
          class="bg-white rounded-2xl p-6 card-shadow"
        >
          <div class="flex items-start justify-between">
            <div class="flex items-start gap-4 flex-1">
              <div 
                class="w-16 h-16 rounded-xl flex items-center justify-center flex-shrink-0"
                :class="getScoreBg(item.totalScore)"
              >
                <span 
                  class="text-2xl font-bold font-display"
                  :class="getScoreColor(item.totalScore)"
                >
                  {{ item.totalScore }}
                </span>
              </div>
              
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-2">
                  <span class="text-gray-400 text-sm">
                    {{ new Date(item.createdAt).toLocaleDateString('zh-CN') }}
                  </span>
                </div>
                
                <div class="flex gap-4 text-sm">
                  <div class="text-gray-500">
                    构图: <span :class="getScoreColor(item.compositionScore)" class="font-semibold">{{ item.compositionScore }}</span>
                  </div>
                  <div class="text-gray-500">
                    光线: <span :class="getScoreColor(item.lightingScore)" class="font-semibold">{{ item.lightingScore }}</span>
                  </div>
                  <div class="text-gray-500">
                    色彩: <span :class="getScoreColor(item.colorScore)" class="font-semibold">{{ item.colorScore }}</span>
                  </div>
                </div>
                
                <p class="text-gray-600 text-sm mt-2 line-clamp-2">
                  {{ item.analysis }}
                </p>
              </div>
            </div>
            
            <button 
              @click="deleteHistory(item.id)"
              class="text-gray-400 hover:text-red-500 p-2"
            >
              🗑️
            </button>
          </div>
        </div>
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
        <button @click="router.push('/history')" class="flex flex-col items-center text-primary-pink">
          <span class="text-2xl">📊</span>
          <span class="text-xs mt-1">历史</span>
        </button>
        <button @click="router.push('/profile')" class="flex flex-col items-center text-gray-500">
          <span class="text-2xl">👤</span>
          <span class="text-xs mt-1">我的</span>
        </button>
      </div>
    </nav>
  </div>
</template>
