<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { photoApi, scoringApi, type Score } from '@/api'

const router = useRouter()
const selectedFile = ref<File | null>(null)
const previewUrl = ref<string>('')
const isLoading = ref(false)
const scoreResult = ref<Score | null>(null)
const suggestionsList = ref<string[]>([])

const onFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    selectedFile.value = target.files[0]
    previewUrl.value = URL.createObjectURL(selectedFile.value)
    scoreResult.value = null
  }
}

const onDrop = (event: DragEvent) => {
  event.preventDefault()
  if (event.dataTransfer?.files && event.dataTransfer.files[0]) {
    selectedFile.value = event.dataTransfer.files[0]
    previewUrl.value = URL.createObjectURL(selectedFile.value)
    scoreResult.value = null
  }
}

const onDragOver = (event: DragEvent) => {
  event.preventDefault()
}

const analyzePhoto = async () => {
  if (!selectedFile.value) return

  isLoading.value = true
  try {
    const uploadRes = await photoApi.upload(selectedFile.value)
    const photoId = uploadRes.data.id

    const scoreRes = await scoringApi.analyze(photoId)
    scoreResult.value = scoreRes.data
    
    try {
      suggestionsList.value = JSON.parse(scoreResult.value.suggestions)
    } catch {
      suggestionsList.value = []
    }
  } catch (error) {
    console.error('分析失败:', error)
    alert('分析失败，请重试')
  } finally {
    isLoading.value = false
  }
}

const getScoreColor = (score: number) => {
  if (score >= 80) return 'text-green-500'
  if (score >= 60) return 'text-yellow-500'
  return 'text-red-500'
}

const calculateCircumference = 100 * 2 * Math.PI
</script>

<template>
  <div class="min-h-screen bg-secondary-beige pb-24">
    <div class="gradient-bg text-white py-8 px-4">
      <div class="max-w-4xl mx-auto">
        <button @click="router.push('/')" class="mb-4 text-white hover:opacity-80">
          ← 返回
        </button>
        <h1 class="font-display text-3xl font-bold">照片评分</h1>
        <p class="mt-2 opacity-90">AI智能分析，获得专业评分和建议</p>
      </div>
    </div>

    <div class="max-w-4xl mx-auto px-4 py-8">
      <div 
        v-if="!previewUrl"
        @drop="onDrop"
        @dragover="onDragOver"
        class="bg-white rounded-2xl p-12 text-center card-shadow border-2 border-dashed border-primary-pink/30"
      >
        <div class="text-6xl mb-4">📷</div>
        <h3 class="font-display text-xl font-semibold mb-2 text-secondary-gray">
          上传照片
        </h3>
        <p class="text-gray-500 mb-6">点击或拖拽照片到这里</p>
        <label class="btn-primary px-6 py-3 rounded-full text-white cursor-pointer inline-block">
          选择照片
          <input 
            type="file" 
            accept="image/*" 
            class="hidden" 
            @change="onFileChange"
          />
        </label>
      </div>

      <div v-else class="space-y-6">
        <div class="bg-white rounded-2xl overflow-hidden card-shadow">
          <img :src="previewUrl" alt="预览" class="w-full max-h-96 object-cover" />
        </div>

        <div class="flex gap-4">
          <button 
            @click="analyzePhoto"
            :disabled="isLoading"
            class="btn-primary flex-1 py-4 rounded-xl text-white font-semibold disabled:opacity-50"
          >
            {{ isLoading ? '分析中...' : '开始分析' }}
          </button>
          <button 
            @click="() => { previewUrl = ''; selectedFile = null; scoreResult = null }"
            class="px-6 py-4 rounded-xl border-2 border-gray-300 font-semibold text-gray-600"
          >
            重新选择
          </button>
        </div>

        <div v-if="scoreResult" class="space-y-6">
          <div class="bg-white rounded-2xl p-8 card-shadow">
            <h3 class="font-display text-xl font-semibold mb-6 text-secondary-gray text-center">
              评分结果
            </h3>
            <div class="flex justify-center mb-8">
              <div class="relative w-48 h-48">
                <svg class="w-full h-full score-ring" viewBox="0 0 240 240">
                  <circle
                    cx="120"
                    cy="120"
                    r="100"
                    fill="none"
                    stroke="#e5e7eb"
                    stroke-width="12"
                  />
                  <circle
                    cx="120"
                    cy="120"
                    r="100"
                    fill="none"
                    :stroke="scoreResult.totalScore >= 80 ? '#10b981' : scoreResult.totalScore >= 60 ? '#f59e0b' : '#ef4444'"
                    stroke-width="12"
                    stroke-linecap="round"
                    :stroke-dasharray="calculateCircumference"
                    :stroke-dashoffset="calculateCircumference * (1 - scoreResult.totalScore / 100)"
                  />
                </svg>
                <div class="absolute inset-0 flex flex-col items-center justify-center">
                  <span 
                    class="text-5xl font-bold font-display"
                    :class="getScoreColor(scoreResult.totalScore)"
                  >
                    {{ scoreResult.totalScore }}
                  </span>
                  <span class="text-gray-500">总分</span>
                </div>
              </div>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div class="bg-secondary-beige rounded-xl p-4 text-center">
                <div :class="getScoreColor(scoreResult.compositionScore)" class="text-2xl font-bold">
                  {{ scoreResult.compositionScore }}
                </div>
                <div class="text-sm text-gray-600">构图</div>
              </div>
              <div class="bg-secondary-beige rounded-xl p-4 text-center">
                <div :class="getScoreColor(scoreResult.lightingScore)" class="text-2xl font-bold">
                  {{ scoreResult.lightingScore }}
                </div>
                <div class="text-sm text-gray-600">光线</div>
              </div>
              <div class="bg-secondary-beige rounded-xl p-4 text-center">
                <div :class="getScoreColor(scoreResult.colorScore)" class="text-2xl font-bold">
                  {{ scoreResult.colorScore }}
                </div>
                <div class="text-sm text-gray-600">色彩</div>
              </div>
              <div class="bg-secondary-beige rounded-xl p-4 text-center">
                <div :class="getScoreColor(scoreResult.focusScore)" class="text-2xl font-bold">
                  {{ scoreResult.focusScore }}
                </div>
                <div class="text-sm text-gray-600">焦点</div>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-2xl p-6 card-shadow">
            <h3 class="font-display text-lg font-semibold mb-4 text-secondary-gray">
              📝 分析报告
            </h3>
            <p class="text-gray-600 leading-relaxed">{{ scoreResult.analysis }}</p>
          </div>

          <div v-if="suggestionsList.length > 0" class="bg-white rounded-2xl p-6 card-shadow">
            <h3 class="font-display text-lg font-semibold mb-4 text-secondary-gray">
              💡 改进建议
            </h3>
            <ul class="space-y-3">
              <li 
                v-for="(suggestion, index) in suggestionsList" 
                :key="index"
                class="flex items-start gap-3"
              >
                <span class="w-6 h-6 rounded-full bg-primary-pink text-white flex items-center justify-center text-sm flex-shrink-0">
                  {{ index + 1 }}
                </span>
                <span class="text-gray-600">{{ suggestion }}</span>
              </li>
            </ul>
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
        <button @click="router.push('/scoring')" class="flex flex-col items-center text-primary-pink">
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
        <button @click="router.push('/profile')" class="flex flex-col items-center text-gray-500">
          <span class="text-2xl">👤</span>
          <span class="text-xs mt-1">我的</span>
        </button>
      </div>
    </nav>
  </div>
</template>
