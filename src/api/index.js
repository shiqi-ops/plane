import axios from 'axios'
import router from '@/router/index.js'
const api = axios.create({
  baseURL: 'http://6ddc7640.r39.cpolar.top', 
  timeout: 0,
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}` 
  return config
})


api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401 && !err.config.url.includes('/auth/login')) {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      // router.push('/login')
    }
    return Promise.reject(err)
  }
)

export default api