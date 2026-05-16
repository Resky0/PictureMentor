import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/pages/Home.vue'
import PhotoScoring from '@/pages/PhotoScoring.vue'
import ShootingGuide from '@/pages/ShootingGuide.vue'
import History from '@/pages/History.vue'
import Profile from '@/pages/Profile.vue'
import Login from '@/pages/Login.vue'
import Register from '@/pages/Register.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/register',
      name: 'register',
      component: Register
    },
    {
      path: '/',
      name: 'home',
      component: Home,
      meta: { requiresAuth: true }
    },
    {
      path: '/scoring',
      name: 'scoring',
      component: PhotoScoring,
      meta: { requiresAuth: true }
    },
    {
      path: '/guide',
      name: 'guide',
      component: ShootingGuide,
      meta: { requiresAuth: true }
    },
    {
      path: '/history',
      name: 'history',
      component: History,
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: Profile,
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
