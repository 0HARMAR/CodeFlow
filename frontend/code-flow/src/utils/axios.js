import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const instance = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 500000
})

// 请求拦截器：自动加 JWT
instance.interceptors.request.use(config => {
    const authStore = useAuthStore()
    if (authStore.token) {
        config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
})

// 响应拦截器：token 过期处理
instance.interceptors.response.use(
    res => res,
    err => {
        if (err.response?.status === 401) {
            const authStore = useAuthStore()
            authStore.clearAuth()
            window.location.href = '/login'
        }
        return Promise.reject(err)
    }
)

export default instance
