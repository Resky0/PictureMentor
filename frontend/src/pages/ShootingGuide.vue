<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { guideApi, type ShootingAdvice } from '@/api'

const router = useRouter()
const videoRef = ref<HTMLVideoElement | null>(null)
const stream = ref<MediaStream | null>(null)
const advice = ref<ShootingAdvice | null>(null)
const isLoading = ref(false)

const startCamera = async () => {
  try {
    stream.value = await navigator.mediaDevices.getUserMedia({ 
      video: { facingMode: 'environment' } 
    })
    if (videoRef.value) {
      videoRef.value.srcObject = stream.value
    }
  } catch (error) {
    console.error('无法访问摄像头:', error)
    alert('无法访问摄像头，请检查权限设置')
  }
}

const getAdvice = async () => {
  isLoading.value = true
  try {
    const res = await guideApi.getAdvice('')
    advice.value = res.data
  } catch (error) {
    console.error('获取建议失败:', error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  startCamera()
  getAdvice()
})

onUnmounted(() => {
  if (stream.value) {
    stream.value.getTracks().forEach(track => track.stop())
  }
})
</script>

<template>
  <div class="min-h-screen bg-black pb-24">
    <div class="relative">
      <video 
        ref="videoRef" 
        autoplay 
        playsinline
        class="w-full h-screen object-cover"
      />
      
      <div class="absolute inset-0 pointer-events-none">
        <svg class="w-full h-full" viewBox="0 0 100 100" preserveAspectRatio="none">
          <line x1="33.3" y1="0" x2="33.3" y2="100" stroke="rgba(255,255,255,0.3)" stroke-width="0.5" />
          <line x1="66.6" y1="0" x2="66.6" y2="100" stroke="rgba(255,255,255,0.3)" stroke-width="0.5" />
          <line x1="0" y1="33.3" x2="100" y2="33.3" stroke="rgba(255,255,255,0.3)" stroke-width="0.5" />
          <line x1="0" y1="66.6" x2="100" y2="66.6" stroke="rgba(255,255,255,0.3)" stroke-width="0.5" />
        </svg>
      </div>

      <div class="absolute top-0 left-0 right-0 gradient-bg text-white py-6 px-4">
        <button @click="router.push('/')" class="mb-2 text-white hover:opacity-80">
          ← 返回
        </button>
        <h1 class="font-display text-2xl font-bold">拍摄指导</h1>
        <p class="text-sm opacity-90 mt-1">实时获得专业拍摄建议</p>
      </div>

      <div v-if="advice" class="absolute bottom-24 left-4 right-4">
        <div class="bg-white/95 backdrop-blur-sm rounded-2xl p-6 card-shadow">
          <div class="flex items-center justify-between mb-4">
            <h3 class="font-display text-lg font-semibold text-secondary-gray">
              📸 拍摄建议
            </h3>
            <button 
              @click="getAdvice"
              :disabled="isLoading"
              class="text-primary-pink hover:opacity-80 disabled:opacity-50"
            >
              {{ isLoading ? '刷新中...' : '↻ 刷新' }}
            </button>
          </div>
          
          <div class="space-y-4">
            <div class="flex items-start gap-3">
              <span class="text-2xl">🎯</span>
              <div>
                <div class="font-semibold text-secondary-gray">构图</div>
                <div class="text-sm text-gray-600">{{ advice.composition }}</div>
              </div>
            </div>
            
            <div class="flex items-start gap-3">
              <span class="text-2xl">☀️</span>
              <div>
                <div class="font-semibold text-secondary-gray">曝光</div>
                <div class="text-sm text-gray-600">{{ advice.exposure }}</div>
              </div>
            </div>
            
            <div class="flex items-start gap-3">
              <span class="text-2xl">👁️</span>
              <div>
                <div class="font-semibold text-secondary-gray">对焦</div>
                <div class="text-sm text-gray-600">{{ advice.focus }}</div>
              </div>
            </div>

            <div class="pt-3 border-t border-gray-200">
              <div class="font-semibold text-secondary-gray mb-2">💡 小贴士</div>
              <ul class="space-y-1">
                <li v-for="(tip, index) in advice.tips" :key="index" class="text-sm text-gray-600">
                  • {{ tip }}
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
        <button @click="router.push('/guide')" class="flex flex-col items-center text-primary-pink">
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
