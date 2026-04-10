<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const features = [
  {
    title: '照片评分',
    description: 'AI智能分析，给你的照片专业评分',
    icon: '📸',
    route: '/scoring'
  },
  {
    title: '拍摄指导',
    description: '实时取景，获得专业拍摄建议',
    icon: '🎯',
    route: '/guide'
  },
  {
    title: '历史记录',
    description: '查看进步轨迹，对比学习成长',
    icon: '📊',
    route: '/history'
  }
]

const navigateTo = (route: string) => {
  router.push(route)
}
</script>

<template>
  <div class="min-h-screen bg-secondary-beige">
    <div class="gradient-bg text-white py-16 px-4">
      <div class="max-w-4xl mx-auto text-center">
        <h1 class="font-display text-5xl md:text-6xl font-bold mb-6 animate-float">
          PhotoMentor
        </h1>
        <p class="text-xl md:text-2xl mb-8 opacity-95">
          让每一张照片都不再被嫌弃
        </p>
        <button 
          @click="navigateTo('/scoring')"
          class="btn-primary px-8 py-4 rounded-full text-lg font-semibold text-white"
        >
          开始体验
        </button>
      </div>
    </div>

    <div class="max-w-6xl mx-auto px-4 py-16">
      <h2 class="font-display text-3xl font-bold text-center mb-12 text-secondary-gray">
        核心功能
      </h2>
      <div class="grid md:grid-cols-3 gap-8">
        <div 
          v-for="feature in features" 
          :key="feature.title"
          @click="navigateTo(feature.route)"
          class="bg-white rounded-2xl p-8 card-shadow cursor-pointer hover:shadow-lg transition-all duration-300"
        >
          <div class="text-5xl mb-4">{{ feature.icon }}</div>
          <h3 class="font-display text-xl font-semibold mb-3 text-secondary-gray">
            {{ feature.title }}
          </h3>
          <p class="text-gray-600">
            {{ feature.description }}
          </p>
        </div>
      </div>
    </div>

    <nav class="fixed bottom-0 left-0 right-0 bg-white border-t shadow-lg">
      <div class="max-w-md mx-auto flex justify-around py-3">
        <button @click="navigateTo('/')" class="flex flex-col items-center text-primary-pink">
          <span class="text-2xl">🏠</span>
          <span class="text-xs mt-1">首页</span>
        </button>
        <button @click="navigateTo('/scoring')" class="flex flex-col items-center text-gray-500">
          <span class="text-2xl">📸</span>
          <span class="text-xs mt-1">评分</span>
        </button>
        <button @click="navigateTo('/guide')" class="flex flex-col items-center text-gray-500">
          <span class="text-2xl">🎯</span>
          <span class="text-xs mt-1">指导</span>
        </button>
        <button @click="navigateTo('/history')" class="flex flex-col items-center text-gray-500">
          <span class="text-2xl">📊</span>
          <span class="text-xs mt-1">历史</span>
        </button>
        <button @click="navigateTo('/profile')" class="flex flex-col items-center text-gray-500">
          <span class="text-2xl">👤</span>
          <span class="text-xs mt-1">我的</span>
        </button>
      </div>
    </nav>
    <div class="h-20"></div>
  </div>
</template>
