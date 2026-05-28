<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const username = ref('')
const password = ref('')
const isLoading = ref(false)
const errorMsg = ref('')

const handleSubmit = async () => {
  if (!username.value || !password.value) {
    errorMsg.value = '请输入用户名和密码'
    return
  }
  
  isLoading.value = true
  errorMsg.value = ''
  
  try {
    const res = await userApi.login(username.value, password.value)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
    userStore.setUser(res.data.user)
    router.push('/')
  } catch (error: any) {
    errorMsg.value = error.response?.data?.message || '登录失败，请重试'
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen gradient-bg flex items-center justify-center px-4">
    <div class="bg-white rounded-3xl p-8 w-full max-w-md card-shadow">
      <div class="text-center mb-8">
        <div class="text-5xl mb-3">📸</div>
        <h1 class="font-display text-2xl font-bold text-secondary-gray">
          欢迎回来
        </h1>
        <p class="text-gray-500 mt-1">
          登录后开始你的摄影之旅
        </p>
      </div>

      <div v-if="errorMsg" class="bg-red-50 text-red-500 text-sm p-3 rounded-xl mb-4 text-center">
        {{ errorMsg }}
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
          <input
            v-model="username"
            type="text"
            placeholder="请输入用户名"
            class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-primary-pink focus:ring-2 focus:ring-primary-pink/20 outline-none transition"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
          <input
            v-model="password"
            type="password"
            placeholder="请输入密码"
            class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-primary-pink focus:ring-2 focus:ring-primary-pink/20 outline-none transition"
          />
        </div>

        <button
          type="submit"
          :disabled="isLoading"
          class="btn-primary w-full py-3 rounded-xl text-white font-semibold disabled:opacity-50"
        >
          {{ isLoading ? '登录中...' : '登录' }}
        </button>
      </form>

      <div class="mt-6 text-center">
        <router-link to="/register" class="text-primary-pink text-sm hover:underline">
          还没有账号？去注册
        </router-link>
      </div>
    </div>
  </div>
</template>
