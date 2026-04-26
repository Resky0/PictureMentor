<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { historyApi, type Score } from '@/api'

const router = useRouter()
const historyList = ref<Score[]>([])
const isLoading = ref(false)
const expandedId = ref<number | null>(null)

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

const toggleExpand = (id: number) => {
  expandedId.value = expandedId.value === id ? null : id
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

const getScoreLabel = (score: number) => {
  if (score >= 90) return '专业级'
  if (score >= 75) return '优秀'
  if (score >= 60) return '良好'
  if (score >= 40) return '一般'
  return '需改进'
}

const parseSuggestions = (suggestions: string): string[] => {
  try {
    return JSON.parse(suggestions)
  } catch {
    return []
  }
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
          class="bg-white rounded-2xl overflow-hidden card-shadow"
        >
          <div class="flex" @click="toggleExpand(item.id)">
            <div v-if="item.photoUrl" class="w-28 h-28 flex-shrink-0">
              <img
                :src="item.photoUrl"
                alt="照片"
                class="w-full h-full object-cover"
              />
            </div>
            <div v-else class="w-28 h-28 flex-shrink-0 bg-gray-100 flex items-center justify-center">
              <span class="text-3xl">📷</span>
            </div>

            <div class="flex-1 p-4 min-w-0">
              <div class="flex items-center justify-between mb-2">
                <div class="flex items-center gap-2">
                  <div
                    class="w-10 h-10 rounded-lg flex items-center justify-center"
                    :class="getScoreBg(item.totalScore)"
                  >
                    <span
                      class="text-lg font-bold font-display"
                      :class="getScoreColor(item.totalScore)"
                    >
                      {{ item.totalScore }}
                    </span>
                  </div>
                  <span
                    class="text-xs font-semibold px-2 py-0.5 rounded-full"
                    :class="[getScoreBg(item.totalScore), getScoreColor(item.totalScore)]"
                  >
                    {{ getScoreLabel(item.totalScore) }}
                  </span>
                </div>
                <button
                  @click.stop="deleteHistory(item.id)"
                  class="text-gray-300 hover:text-red-500 p-1 transition-colors"
                >
                  🗑️
                </button>
              </div>

              <div class="flex gap-3 text-xs text-gray-500">
                <span>构图 <b :class="getScoreColor(item.compositionScore)">{{ item.compositionScore }}</b></span>
                <span>光线 <b :class="getScoreColor(item.lightingScore)">{{ item.lightingScore }}</b></span>
                <span>色彩 <b :class="getScoreColor(item.colorScore)">{{ item.colorScore }}</b></span>
                <span>焦点 <b :class="getScoreColor(item.focusScore)">{{ item.focusScore }}</b></span>
              </div>

              <div class="flex items-center justify-between mt-2">
                <span class="text-xs text-gray-400">
                  {{ new Date(item.createdAt).toLocaleDateString('zh-CN') }}
                </span>
                <span class="text-xs text-gray-400">
                  {{ expandedId === item.id ? '收起 ▲' : '展开 ▼' }}
                </span>
              </div>
            </div>
          </div>

          <div
            v-if="expandedId === item.id"
            class="border-t border-gray-100 px-4 py-4"
          >
            <div v-if="item.photoUrl" class="mb-4">
              <img
                :src="item.photoUrl"
                alt="照片大图"
                class="w-full max-h-80 object-contain rounded-xl bg-gray-50"
              />
            </div>

            <div class="grid grid-cols-4 gap-3 mb-4">
              <div class="bg-secondary-beige rounded-xl p-3 text-center">
                <div :class="getScoreColor(item.compositionScore)" class="text-xl font-bold">
                  {{ item.compositionScore }}
                </div>
                <div class="text-xs text-gray-500">构图</div>
              </div>
              <div class="bg-secondary-beige rounded-xl p-3 text-center">
                <div :class="getScoreColor(item.lightingScore)" class="text-xl font-bold">
                  {{ item.lightingScore }}
                </div>
                <div class="text-xs text-gray-500">光线</div>
              </div>
              <div class="bg-secondary-beige rounded-xl p-3 text-center">
                <div :class="getScoreColor(item.colorScore)" class="text-xl font-bold">
                  {{ item.colorScore }}
                </div>
                <div class="text-xs text-gray-500">色彩</div>
              </div>
              <div class="bg-secondary-beige rounded-xl p-3 text-center">
                <div :class="getScoreColor(item.focusScore)" class="text-xl font-bold">
                  {{ item.focusScore }}
                </div>
                <div class="text-xs text-gray-500">焦点</div>
              </div>
            </div>

            <div class="mb-3">
              <h4 class="text-sm font-semibold text-gray-700 mb-1">📝 分析</h4>
              <p class="text-sm text-gray-600 leading-relaxed">{{ item.analysis }}</p>
            </div>

            <div v-if="parseSuggestions(item.suggestions).length > 0">
              <h4 class="text-sm font-semibold text-gray-700 mb-1">💡 建议</h4>
              <ul class="space-y-1.5">
                <li
                  v-for="(suggestion, index) in parseSuggestions(item.suggestions)"
                  :key="index"
                  class="flex items-start gap-2 text-sm"
                >
                  <span class="w-5 h-5 rounded-full bg-primary-pink text-white flex items-center justify-center text-xs flex-shrink-0 mt-0.5">
                    {{ index + 1 }}
                  </span>
                  <span class="text-gray-600">{{ suggestion }}</span>
                </li>
              </ul>
            </div>
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
