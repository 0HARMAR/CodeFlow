<template>
  <div class="avatar-wrapper left nazuna">
    <img
        src="../assets/nazuna.png"
        alt="七草荠"
        class="nazuna-avatar"
        @click="toggle"
    />
    <!-- 七草荠聊天面板 -->
    <div v-if="showBubble" class="nazuna-chat-panel" :style="panelStyle">
      <div class="chat-header nazuna-header" @mousedown="onHeaderMouseDown">
        <div class="chat-header-left">
          <img src="../assets/nazuna.png" class="header-avatar" alt="七草荠" />
          <div>
            <div class="chat-title">七草荠 Nazuna</div>
            <div class="chat-subtitle">在线 · CodeFlow 助手</div>
          </div>
        </div>
        <button class="chat-close" @click="showBubble = false">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M4 4L12 12M12 4L4 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>
      </div>

      <div class="chat-messages" ref="nazunaChatRef">
        <div v-for="(msg, index) in messages" :key="index"
             :class="['msg-row', msg.role === 'ai' ? 'msg-ai' : 'msg-user']">
          <img v-if="msg.role === 'ai'" src="../assets/nazuna.png" class="msg-avatar" alt="七草荠" />
          <div :class="['msg-bubble', msg.role === 'ai' ? 'bubble-ai-nazuna' : 'bubble-user-nazuna']">
            <template v-if="msg.role === 'ai'">
              <template v-for="(seg, si) in parseContent(msg.content)" :key="si">
                <router-link v-if="seg.type === 'link'" :to="seg.url">{{ seg.content }}</router-link>
                <span v-else>{{ seg.content }}</span>
              </template>
            </template>
            <template v-else>{{ msg.content }}</template>
          </div>
        </div>
        <!-- 输入中动画 -->
        <div v-if="loading" class="msg-row msg-ai">
          <img src="../assets/nazuna.png" class="msg-avatar" alt="七草荠" />
          <div class="msg-bubble bubble-ai-nazuna typing-bubble">
            <span class="typing-dot-nazuna"></span>
            <span class="typing-dot-nazuna"></span>
            <span class="typing-dot-nazuna"></span>
          </div>
        </div>
      </div>

      <div class="chat-input-row nazuna-input-row">
        <input
            type="text"
            v-model="input"
            @keyup.enter="send"
            placeholder="和荠荠聊聊天吧~"
            :disabled="loading"
        />
        <button
            @click="send"
            :disabled="loading || !input.trim()"
            class="send-btn nazuna-send-btn"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <path d="M22 2L11 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M22 2L15 22L11 13L2 9L22 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>

      <!-- 拖拽缩放手柄 -->
      <div class="resize-grip nazuna-resize-grip">
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
import { parseMarkdownLinks } from '@/utils/markdown'
import { useDraggable } from '@/utils/useDraggable'

export default {
  name: 'NazunaChat',
  emits: ['opened'],
  setup(props, { emit }) {
    const showBubble = ref(false)
    const messages = ref([])
    const input = ref('')
    const loading = ref(false)
    const nazunaChatRef = ref(null)

    const toggle = () => {
      showBubble.value = !showBubble.value
      if (showBubble.value) {
        resetPosition()
        emit('opened')
        if (messages.value.length === 0) {
          messages.value.push({ role: 'ai', content: '你好呀~我是七草荠，可以帮你搜索文章、推荐好文、查看评论哦。想找点什么呢？' })
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

        const response = await axios.post('http://localhost:8080/api/agent/nazuna', {
          messages: apiMessages
        })
        messages.value.push({ role: 'ai', content: response.data.reply })
      } catch (error) {
        messages.value.push({ role: 'ai', content: '嗯…网络好像不太稳定呢，请稍后再试哦~' })
      } finally {
        loading.value = false
        await nextTick()
        if (nazunaChatRef.value) {
          nazunaChatRef.value.scrollTop = nazunaChatRef.value.scrollHeight
        }
      }
    }

    const { panelStyle, onHeaderMouseDown, resetPosition } = useDraggable()
    const parseContent = (text) => parseMarkdownLinks(text)

    const close = () => { showBubble.value = false }

    return { showBubble, messages, input, loading, nazunaChatRef, toggle, send, close, parseContent, panelStyle, onHeaderMouseDown }
  }
}
</script>