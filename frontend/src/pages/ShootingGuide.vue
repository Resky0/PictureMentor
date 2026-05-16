<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { guideApi, type ShootingAdvice } from '@/api'

const router = useRouter()
const videoRef = ref<HTMLVideoElement | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)
const stream = ref<MediaStream | null>(null)
const advice = ref<ShootingAdvice | null>(null)
const isLoading = ref(false)
const cameraError = ref('')
const useFallback = ref(false)
const isFrontCamera = ref(false)
const cameras = ref<MediaDeviceInfo[]>([])
const showCameraMenu = ref(false)

const stopStream = () => {
  if (stream.value) {
    stream.value.getTracks().forEach(track => track.stop())
    stream.value = null
  }
}

const getOptimalConstraints = (facingMode: string) => {
  const isMobile = /Mobi|Android|iPhone|iPad|iPod/i.test(navigator.userAgent)
  return {
    audio: false,
    video: {
      facingMode,
      width: { ideal: isMobile ? 1280 : 1920 },
      height: { ideal: isMobile ? 720 : 1080 }
    }
  }
}

const startCamera = async (facingMode = 'environment') => {
  stopStream()
  cameraError.value = ''

  if (!navigator.mediaDevices?.getUserMedia) {
    cameraError.value = '当前浏览器不支持摄像头访问，请使用相册上传'
    useFallback.value = true
    return
  }

  try {
    const constraints = getOptimalConstraints(facingMode)
    stream.value = await navigator.mediaDevices.getUserMedia(constraints)
    if (videoRef.value) {
      videoRef.value.srcObject = stream.value
    }
    useFallback.value = false
  } catch (error: any) {
    console.error('摄像头启动失败:', error)
    if (error.name === 'NotAllowedError') {
      cameraError.value = '摄像头权限被拒绝，请在浏览器设置中允许摄像头访问，或使用相册上传'
    } else if (error.name === 'NotFoundError') {
      cameraError.value = '未检测到摄像头设备，请使用相册上传'
    } else if (error.name === 'NotReadableError') {
      cameraError.value = '摄像头被其他应用占用，请关闭后重试'
    } else {
      cameraError.value = '无法访问摄像头，请检查权限设置或使用相册上传'
    }
    useFallback.value = true
  }
}

const switchCamera = () => {
  const newFacing = isFrontCamera.value ? 'environment' : 'user'
  isFrontCamera.value = !isFrontCamera.value
  showCameraMenu.value = false
  startCamera(newFacing)
}

const selectCamera = (deviceId: string) => {
  showCameraMenu.value = false
  stopStream()
  cameraError.value = ''

  navigator.mediaDevices.getUserMedia({
    video: { deviceId: { exact: deviceId } }
  }).then(s => {
    stream.value = s
    if (videoRef.value) {
      videoRef.value.srcObject = s
    }
    useFallback.value = false
  }).catch(err => {
    cameraError.value = '切换摄像头失败: ' + err.message
  })
}

const detectCameras = async () => {
  try {
    const devices = await navigator.mediaDevices.enumerateDevices()
    cameras.value = devices.filter(d => d.kind === 'videoinput')
  } catch {
    cameras.value = []
  }
}

const onFileSelected = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    getAdvice()
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

const retryCamera = () => {
  useFallback.value = false
  cameraError.value = ''
  startCamera(isFrontCamera.value ? 'user' : 'environment')
}

onMounted(() => {
  detectCameras()
  startCamera('environment')
  getAdvice()
})

onUnmounted(() => {
  stopStream()
})
</script>

<template>
  <div class="min-h-screen bg-black pb-24">
    <div class="relative">
      <video
        v-if="!useFallback"
        ref="videoRef"
        autoplay
        playsinline
        webkit-playsinline
        class="w-full h-screen object-cover"
      />

      <div v-else class="w-full h-screen bg-gray-900 flex flex-col items-center justify-center text-white px-6">
        <div class="text-6xl mb-6">📷</div>
        <p class="text-lg text-center mb-2">无法开启摄像头</p>
        <p class="text-sm text-gray-400 text-center mb-6">{{ cameraError }}</p>
        <div class="flex flex-col gap-3 w-full max-w-xs">
          <button
            @click="retryCamera"
            class="bg-white/20 hover:bg-white/30 rounded-xl py-3 font-semibold transition"
          >
            🔄 重试摄像头
          </button>
          <label class="bg-primary-pink hover:opacity-90 rounded-xl py-3 font-semibold text-center cursor-pointer transition">
            🖼️ 从相册选择
            <input
              ref="fileInputRef"
              type="file"
              accept="image/*"
              class="hidden"
              @change="onFileSelected"
            />
          </label>
        </div>
      </div>

      <div v-if="!useFallback" class="absolute inset-0 pointer-events-none">
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

      <div v-if="!useFallback" class="absolute top-24 right-4 flex flex-col gap-2">
        <div class="relative">
          <button
            @click="showCameraMenu = !showCameraMenu"
            class="w-10 h-10 bg-black/50 backdrop-blur-sm rounded-full flex items-center justify-center text-white hover:bg-black/70 transition"
            title="切换摄像头"
          >
            🔄
          </button>
          <div
            v-if="showCameraMenu"
            class="absolute right-0 top-12 bg-white rounded-xl shadow-lg py-2 min-w-[200px] z-10"
          >
            <button
              @click="switchCamera"
              class="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
            >
              <span>{{ isFrontCamera ? '📷' : '🤳' }}</span>
              <span>{{ isFrontCamera ? '切换到后置' : '切换到前置' }}</span>
            </button>
            <div v-if="cameras.length > 1" class="border-t border-gray-100 pt-1">
              <div class="px-4 py-1 text-xs text-gray-400">所有摄像头</div>
              <button
                v-for="cam in cameras"
                :key="cam.deviceId"
                @click="selectCamera(cam.deviceId)"
                class="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 truncate"
              >
                📹 {{ cam.label || `摄像头 ${cam.deviceId.slice(0, 8)}...` }}
              </button>
            </div>
          </div>
        </div>
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