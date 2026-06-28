import { createRouter, createWebHistory } from 'vue-router'
import { reactive } from 'vue'
import { api } from '@/api/client'
import ConcertsView from '@/views/ConcertsView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import MyBookingsView from '@/views/MyBookingsView.vue'

export const authState = reactive({
  user: null,
  checked: false
})

async function ensureAuthChecked() {
  if (authState.checked) {
    return
  }

  try {
    authState.user = await api.me()
  } catch (error) {
    authState.user = null
  } finally {
    authState.checked = true
  }
}

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/concerts' },
    { path: '/concerts', name: 'concerts', component: ConcertsView },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/register', name: 'register', component: RegisterView },
    {
      path: '/my-bookings',
      name: 'my-bookings',
      component: MyBookingsView,
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach(async (to) => {
  await ensureAuthChecked()

  if (to.meta.requiresAuth && !authState.user) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if ((to.name === 'login' || to.name === 'register') && authState.user) {
    return { name: 'concerts' }
  }
})

export default router
