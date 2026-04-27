<template>
  <div class="home">
    <div class="hero-section">
      <h1>欢迎来到CodeFlow</h1>
      <p>分享技术见解和生活感悟</p>
      <router-link to="/articles" class="btn-primary">浏览文章</router-link>
    </div>

    <div class="featured-section">
      <div class="featured-header">
        <h2>推荐文章</h2>

        <!-- 刷新按钮 -->
        <button class="refresh-btn" @click="refreshFeatured">
          🔄
        </button>
      </div>

      <div class="article-cards">
        <div
            class="article-card"
            v-for="article in featuredArticles"
            :key="article.id"
        >
          <router-link :to="`/article/${article.id}`">
            <h3>{{ article.title }}</h3>
            <p>{{ article.excerpt }}</p>
            <span class="date">{{ article.date }}</span>
          </router-link>
        </div>
      </div>
    </div>


    <NazunaChat @opened="showNijikaBubble = false" />

    <!-- 虹夏卡通形象 -->
    <div class="avatar-wrapper right nijika">
      <img
          src="../assets/nijika.png"
          alt="虹夏"
          class="rainka-avatar"
          @click="toggleNijika"
      />
      <!-- 虹夏聊天面板 -->
      <div v-if="showNijikaBubble" class="nijika-chat-panel">
        <div class="chat-header">
          <div class="chat-header-left">
            <img src="../assets/nijika.png" class="header-avatar" alt="虹夏" />
            <div>
              <div class="chat-title">虹夏 Nijika</div>
              <div class="chat-subtitle">在线 · CodeFlow 助手</div>
            </div>
          </div>
          <button class="chat-close" @click="showNijikaBubble = false">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
              <path d="M4 4L12 12M12 4L4 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>

        <div class="chat-messages" ref="nijikaChatRef">
          <div v-for="(msg, index) in nijikaMessages" :key="index"
               :class="['msg-row', msg.role === 'ai' ? 'msg-ai' : 'msg-user']">
            <img v-if="msg.role === 'ai'" src="../assets/nijika.png" class="msg-avatar" alt="虹夏" />
            <div :class="['msg-bubble', msg.role === 'ai' ? 'bubble-ai' : 'bubble-user']">
              {{ msg.content }}
            </div>
          </div>
          <!-- 输入中动画 -->
          <div v-if="isNijikaLoading" class="msg-row msg-ai">
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
              v-model="nijikaInput"
              @keyup.enter="sendNijikaMessage"
              placeholder="和虹夏聊聊天吧~"
              :disabled="isNijikaLoading"
          />
          <button
              @click="sendNijikaMessage"
              :disabled="isNijikaLoading || !nijikaInput.trim()"
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


  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import axios from '@/utils/axios'
import {UserAction} from "@/enums/UserAction";
import NazunaChat from '@/components/NazunaChat.vue'

export default {
  name: 'HomePage',
  components: { NazunaChat },
  setup() {
    let featuredArticles = ref([])
    let featuredArticlesAction = new Map()
    const showNijikaBubble = ref(false)

    // 虹夏
    const nijikaMessages = ref([])
    const nijikaInput = ref("")
    const isNijikaLoading = ref(false)
    const nijikaChatRef = ref(null)

    // 加载推荐文章
    const loadTopArticles = async () => {
      try {
        const refreshData = getRefreshData(true)
        const refreshArticles = await axios.post(`http://localhost:8080/api/recommend/refresh?size=20`,refreshData)
        featuredArticles.value = refreshArticles.data

        // ① 先读已有 actions
        const raw = localStorage.getItem('actions')
        const existing = raw ? new Map(JSON.parse(raw)) : new Map()

        // ② 只给“没有记录”的文章补 IMPRESSION
        for (const article of featuredArticles.value) {
          const id = String(article.id)
          if (!existing.has(id)) {
            existing.set(id, UserAction.IMPRESSION)
          }
        }

        // ③ 回写
        featuredArticlesAction = existing
        localStorage.setItem('actions', JSON.stringify(Array.from(existing)))
      } catch (error) {
        console.error('加载文章失败:', error)
      }
    }


    const toggleNijika = () => {
      showNijikaBubble.value = !showNijikaBubble.value
      if (showNijikaBubble.value && nijikaMessages.value.length === 0) {
        nijikaMessages.value.push({ role: 'ai', content: '嗨~我是虹夏！我可以帮你搜索文章、推荐好文、查看评论哦~想看点什么呢？' })
      }
    }

    // 发送虹夏消息（使用 Agent 端点，支持工具调用）
    const sendNijikaMessage = async () => {
      if (!nijikaInput.value.trim()) return
      const messageContent = nijikaInput.value.trim()
      nijikaMessages.value.push({ role: 'user', content: messageContent })
      nijikaInput.value = ""
      isNijikaLoading.value = true
      try {
        // 将所有历史消息转为 API 格式发送给 Agent
        const apiMessages = nijikaMessages.value.map(msg => ({
          role: msg.role === 'ai' ? 'assistant' : 'user',
          content: msg.content
        }))

        const response = await axios.post('http://localhost:8080/api/agent', {
          messages: apiMessages
        })
        nijikaMessages.value.push({ role: 'ai', content: response.data.reply })
      } catch (error) {
        nijikaMessages.value.push({ role: 'ai', content: '呜…信号不太好呢，请稍后再试~' })
      } finally {
        isNijikaLoading.value = false
        await nextTick()
        if (nijikaChatRef.value) {
          nijikaChatRef.value.scrollTop = nijikaChatRef.value.scrollHeight
        }
      }
    }

    const getRefreshData = (init) => {
      const raw = localStorage.getItem(`actions`);
      const parsed = JSON.parse(raw)
      featuredArticlesAction = new Map(parsed)
      if (init) {
        const refreshData = Array.from(featuredArticlesAction.entries()).map(([articleId,]) => ({
          articleId: articleId,
          action: 'IMPRESSION'
        }));
        return refreshData
      }
      const refreshData = Array.from(featuredArticlesAction.entries()).map(([articleId, action]) => ({
        articleId: articleId,
        action: getActionName(action)
      }));

      return refreshData
    }

    const refreshFeatured = async () => {
      const refreshData = getRefreshData(false)
      const refreshArticles = await axios.post(`http://localhost:8080/api/recommend/refresh?size=20`,refreshData)
      console.log(refreshArticles.data)
      featuredArticles.value = refreshArticles.data
    }

    const getActionName = (actionObj) => {
      for (const [key, value] of Object.entries(UserAction)) {
        if (value.level == actionObj.level) {
          return key;
        }
      }
      return null; // 如果没找到
    }


    // 组件挂载后加载文章
    onMounted(() => {
      loadTopArticles()
    })

    // 返回需要在模板中使用的属性和方法
    return {
      featuredArticles,
      showNijikaBubble,
      nijikaMessages,
      nijikaInput,
      isNijikaLoading,
      nijikaChatRef,
      loadTopArticles,
      toggleNijika,
      sendNijikaMessage,
      refreshFeatured
    }
  }
}
</script>



<style>
@import '@/styles/home-layout.css';
@import '@/styles/nijika-chat.css';
@import '@/styles/nazuna-chat.css';
</style>

