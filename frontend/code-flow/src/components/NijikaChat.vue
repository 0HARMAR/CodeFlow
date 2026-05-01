<template>
  <div class="avatar-wrapper right nijika">
    <img
        src="../assets/nijika.png"
        alt="虹夏"
        class="rainka-avatar"
        @click="toggle"
    />
    <!-- 虹夏聊天面板 -->
    <div v-if="showBubble" class="nijika-chat-panel">
      <div class="chat-header">
        <div class="chat-header-left">
          <img src="../assets/nijika.png" class="header-avatar" alt="虹夏" />
          <div>
            <div class="chat-title">虹夏 Nijika</div>
            <div class="chat-subtitle">在线 · CodeFlow 助手</div>
          </div>
        </div>
        <button class="chat-close" @click="showBubble = false">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M4 4L12 12M12 4L4 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>
      </div>

      <div class="chat-messages" ref="chatRef">
        <div v-for="(msg, index) in messages" :key="index"
             :class="['msg-row', msg.role === 'ai' ? 'msg-ai' : 'msg-user']">
          <img v-if="msg.role === 'ai'" src="../assets/nijika.png" class="msg-avatar" alt="虹夏" />
          <div :class="['msg-bubble', msg.role === 'ai' ? 'bubble-ai' : 'bubble-user']">
            {{ msg.content }}
          </div>
        </div>
        <!-- 输入中动画 -->
        <div v-if="loading" class="msg-row msg-ai">
          <img src="../assets/nijika.png" class="msg-avatar" alt="虹夏" />
          <div class="msg-bubble bubble-ai typing-bubble">
            <span class="typing-dot"></span>
            <span class="typing-dot"></span>
            <span class="typing-dot"></span>
          </div>
        </div>
      </div>

      <div class="chat-input-row">
        <input
            type="text"
            v-model="input"
            @keyup.enter="send"
            placeholder="和虹夏聊聊天吧~"
            :disabled="loading"
        />
        <button
            @click="send"
            :disabled="loading || !input.trim()"
            class="send-btn"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <path d="M22 2L11 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M22 2L15 22L11 13L2 9L22 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>

      <!-- 拖拽缩放手柄 -->
      <div class="resize-grip">
        <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
          <path d="M10 2V10H2" stroke="#ccc" stroke-width="1.5" stroke-linecap="round"/>
        </svg>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, nextTick } from 'vue'
import axios from '@/utils/axios'

export default {
  name: 'NijikaChat',
  emits: ['opened'],
  setup(props, { emit }) {
    const showBubble = ref(false)
    const messages = ref([])
    const input = ref('')
    const loading = ref(false)
    const chatRef = ref(null)

    const toggle = () => {
      showBubble.value = !showBubble.value
      if (showBubble.value) {
        emit('opened')
        if (messages.value.length === 0) {
          messages.value.push({ role: 'ai', content: '嗨~我是虹夏！我可以帮你搜索文章、推荐好文、查看评论哦~想看点什么呢？' })
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
        const apiMessages = messages.value.map(msg => ({
          role: msg.role === 'ai' ? 'assistant' : 'user',
          content: msg.content
        }))

        const response = await axios.post('http://localhost:8080/api/agent', {
          messages: apiMessages
        })
        messages.value.push({ role: 'ai', content: response.data.reply })
      } catch (error) {
        messages.value.push({ role: 'ai', content: '呜…信号不太好呢，请稍后再试~' })
      } finally {
        loading.value = false
        await nextTick()
        if (chatRef.value) {
          chatRef.value.scrollTop = chatRef.value.scrollHeight
        }
      }
    }

    return { showBubble, messages, input, loading, chatRef, toggle, send }
  }
}
</script>
