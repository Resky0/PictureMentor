import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/pages/Home.vue'
import PhotoScoring from '@/pages/PhotoScoring.vue'
import ShootingGuide from '@/pages/ShootingGuide.vue'
import History from '@/pages/History.vue'
import Profile from '@/pages/Profile.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/scoring',
      name: 'scoring',
      component: PhotoScoring
    },
    {
      path: '/guide',
      name: 'guide',
      component: ShootingGuide
    },
    {
      path: '/history',
      name: 'history',
      component: History
    },
    {
      path: '/profile',
      name: 'profile',
      component: Profile
    }
  ]
})

export default router
