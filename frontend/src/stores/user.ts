import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User } from '@/api'

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null)

  const setUser = (userData: User) => {
    user.value = userData
  }

  const clearUser = () => {
    user.value = null
  }

  return {
    user,
    setUser,
    clearUser
  }
})
