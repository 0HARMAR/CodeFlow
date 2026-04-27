<template>
  <div class="avatar-wrapper left nazuna">
    <img
        src="../assets/nazuna.png"
        alt="七草荠"
        class="nazuna-avatar"
        @click="toggle"
    />
    <div v-if="showBubble" class="speech-bubble left chat-bubble">
      <div class="messages">
        <div v-for="(msg, index) in messages" :key="index" :class="msg.role">
          {{ msg.content }}
        </div>
      </div>
      <div class="input-area">
        <input type="text" v-model="input" @keyup.enter="send" placeholder="输入消息..." />
        <button @click="send" :disabled="loading">发送</button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import axios from '@/utils/axios'

export default {
  name: 'NazunaChat',
  emits: ['opened'],
  setup(props, { emit }) {
    const showBubble = ref(false)
    const messages = ref([])
    const input = ref('')
    const loading = ref(false)

    const toggle = () => {
      showBubble.value = !showBubble.value
      if (showBubble.value) {
        emit('opened')
        if (messages.value.length === 0) {
          messages.value.push({ role: 'ai', content: '今天天气不错，要写点什么吗？' })
        }
      }
    }

    const send = async () => {
      if (!input.value.trim()) return
      const content = input.value.trim()
      messages.value.push({ role: 'user', content })
      input.value = ''
      loading.value = true
      try {
        const response = await axios.post('http://localhost:8080/api/chat', {
          messages: [
            { role: 'system', content: '你是一个可爱的七草荠 AI 助手' },
            ...messages.value.map(msg => ({ role: msg.role === 'ai' ? 'assistant' : 'user', content: msg.content })),
            { role: 'user', content }
          ]
        })
        messages.value.push({ role: 'ai', content: response.data.reply })
      } catch (error) {
        messages.value.push({ role: 'ai', content: '嗯…我现在好像有点忙，请稍后再试~' })
      } finally {
        loading.value = false
        setTimeout(() => {
          const bubble = document.querySelector('.nazuna .chat-bubble')
          if (bubble) bubble.scrollTop = bubble.scrollHeight
        }, 0)
      }
    }

    // expose close method for external use
    const close = () => { showBubble.value = false }

    return { showBubble, messages, input, loading, toggle, send, close }
  }
}
</script>