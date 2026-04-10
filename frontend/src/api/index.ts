import request from '@/utils/request'

export interface User {
  id: number
  username: string
  avatar?: string
  email?: string
  createdAt: string
}

export interface Photo {
  id: number
  userId: number
  url: string
  thumbnailUrl: string
  uploadTime: string
}

export interface Score {
  id: number
  photoId: number
  totalScore: number
  compositionScore: number
  lightingScore: number
  colorScore: number
  focusScore: number
  suggestions: string
  analysis: string
  createdAt: string
}

export interface ShootingAdvice {
  composition: string
  exposure: string
  focus: string
  tips: string[]
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export const photoApi = {
  upload: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post<ApiResponse<{ id: number; url: string; thumbnailUrl: string }>>('/photos/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export const scoringApi = {
  analyze: (photoId: number) => {
    return request.post<ApiResponse<Score>>('/scoring/analyze', { photoId })
  }
}

export const historyApi = {
  list: (page = 1, size = 10) => {
    return request.get<ApiResponse<{ items: Score[]; total: number }>>('/history', { params: { page, size } })
  },
  delete: (id: number) => {
    return request.delete<ApiResponse<{ success: boolean }>>(`/history/${id}`)
  }
}

export const guideApi = {
  getAdvice: (imageData: string) => {
    return request.post<ApiResponse<ShootingAdvice>>('/guide/advice', { imageData })
  }
}

export const userApi = {
  getProfile: () => {
    return request.get<ApiResponse<User>>('/user/profile')
  },
  updateProfile: (data: Partial<User>) => {
    return request.put<ApiResponse<User>>('/user/profile', data)
  }
}
