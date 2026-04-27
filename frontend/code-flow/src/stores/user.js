import { defineStore } from 'pinia'
import axios from '@/utils/axios'

export const useUserStore = defineStore('user', {
    state: () => ({
        users: [],
    }),

    actions: {
        // 注册
        async register(userData) {
            try {
                const res = await axios.post('/api/users/register', userData)
                return res.data
            } catch (err) {
                console.error('register error:', err)
                throw err
            }
        },

        // 根据 id 查用户
        async findUserById(id) {
            console.log('🧠 findUserById called with:', id)
            const res = await axios.get(`http://localhost:8080/api/users/${id}`)
            console.log('🧠 user api result:', res.data)
            return res.data
        },

        async updateUser(userData) {
            console.log('🧠 updateUser called with:', userData)
            const res = await axios.post(`http://localhost:8080/api/users/update`, userData)
            console.log('🧠 user api result:', res.data)
            return res.data
        },
    },
})
