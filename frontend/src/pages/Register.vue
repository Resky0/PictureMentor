<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const isLoading = ref(false)
const errorMsg = ref('')

const handleSubmit = async () => {
  if (!username.value || !password.value || !confirmPassword.value) {
    errorMsg.value = '请填写所有字段'
    return
  }
  
  if (password.value !== confirmPassword.value) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }
  
  if (password.value.length < 6) {
    errorMsg.value = '密码长度不能少于6位'
    return
  }
  
  isLoading.value = true
  errorMsg.value = ''
  
  try {
    const res = await userApi.register(username.value, password.value)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
    userStore.setUser(res.data.user)
    router.push('/')
  } catch (error: any) {
    errorMsg.value = error.response?.data?.message || '注册失败，请重试'
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
          创建账号
        </h1>
        <p class="text-gray-500 mt-1">
          注册后享受专业摄影指导
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
            placeholder="请输入密码（至少6位）"
            class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-primary-pink focus:ring-2 focus:ring-primary-pink/20 outline-none transition"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">确认密码</label>
          <input
            v-model="confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-primary-pink focus:ring-2 focus:ring-primary-pink/20 outline-none transition"
          />
        </div>

        <button
          type="submit"
          :disabled="isLoading"
          class="btn-primary w-full py-3 rounded-xl text-white font-semibold disabled:opacity-50"
        >
          {{ isLoading ? '注册中...' : '注册' }}
        </button>
      </form>

      <div class="mt-6 text-center">
        <router-link to="/login" class="text-primary-pink text-sm hover:underline">
          已有账号？去登录
        </router-link>
      </div>
    </div>
  </div>
</template>
