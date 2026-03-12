import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import EvaluateOne from '../views/EvaluateOne.vue'
import EvaluateMore from '../views/EvaluateMore.vue'
import EvaluateOwn from '../views/EvaluateOwn.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: LoginView },
    { path: '/home', component: HomeView, meta: { requiresAuth: true } },
    { path: '/evaluate/one', component: EvaluateOne, meta: { requiresAuth: true } },
    { path: '/evaluate/more', component: EvaluateMore, meta: { requiresAuth: true } },
    { path: '/evaluate/own', component: EvaluateOwn, meta: { requiresAuth: true } },
  ]
})

// 未登录跳回登录页
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) return '/login'
})

export default router