import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import MainLayout from '../layouts/MainLayout.vue'
import HomeView from '../views/HomeView.vue'
import RobustnessView from '../views/RobustnessView.vue'
import EvaluateOne from '../views/EvaluateOne.vue'
import EvaluateMore from '../views/EvaluateMore.vue'
import EvaluateOwn from '../views/EvaluateOwn.vue'
import AssistantView from '../views/AssistantView.vue'
import AccountView from '../views/AccountView.vue'
import AlgorithmView from '../views/AlgorithmView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: LoginView },
    {
      path: '/',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        { path: 'home', component: HomeView },
        { path: 'algorithm', component: AlgorithmView },
        { path: 'robustness', component: RobustnessView },
        { path: 'evaluate/one', component: EvaluateOne },
        { path: 'evaluate/more', component: EvaluateMore },
        { path: 'evaluate/own', component: EvaluateOwn },
        { path: 'assistant', component: AssistantView },
        { path: 'account', component: AccountView },
      ]
    }
  ]
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) return '/login'
})

export default router