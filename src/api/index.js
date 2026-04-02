import axios from 'axios'
import router from '@/router'
const api = axios.create({
  baseURL: 'https://7c45feff.r39.cpolar.top', 
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
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      router.push('/login')
    }
    return Promise.reject(err)
  }
)

export default api