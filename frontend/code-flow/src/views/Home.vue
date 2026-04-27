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


    <div class="avatar-wrapper left nazuna">
      <img
          src="../assets/nazuna.png"
          alt="七草荠"
          class="nazuna-avatar"
          @click="toggleNazuna"
      />

      <div v-if="showNazunaBubble" class="speech-bubble left chat-bubble">
        <div class="messages">
          <div v-for="(msg, index) in nazunaMessages" :key="index" :class="msg.role">
            {{ msg.content }}
          </div>
        </div>
        <div class="input-area">
          <input type="text" v-model="userInput" @keyup.enter="sendMessage" placeholder="输入消息..." />
          <button @click="sendMessage" :disabled="isLoading">发送</button>
        </div>
      </div>
    </div>

    <!-- 虹夏卡通形象 -->
    <div class="avatar-wrapper right nijika">
      <img
          src="../assets/nijika.png"
          alt="虹夏"
          class="rainka-avatar"
          @click="toggleNijika"
      />
      <div v-if="showNijikaBubble" class="speech-bubble right chat-bubble">
        <div class="messages">
          <div v-for="(msg, index) in nijikaMessages" :key="index" :class="msg.role">
            {{ msg.content }}
          </div>
        </div>
        <div class="input-area">
          <input type="text" v-model="nijikaInput" @keyup.enter="sendNijikaMessage" placeholder="输入消息..." />
          <button @click="sendNijikaMessage" :disabled="isNijikaLoading">发送</button>
        </div>
      </div>
    </div>


  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'
import {UserAction} from "@/enums/UserAction";

export default {
  name: 'HomePage',
  setup() {
    // 响应式数据
    let featuredArticles = ref([])  // 初始为空，等待后端数据
    let featuredArticlesAction = new Map()
    const showNazunaBubble = ref(false)
    const showNijikaBubble = ref(false)
    const nazunaMessages = ref([]) // 存放和小荠的聊天消息
    const userInput = ref("")      // 用户输入
    const isLoading = ref(false)    // AI 回复加载状态

    // 虹夏
    const nijikaMessages = ref([])
    const nijikaInput = ref("")
    const isNijikaLoading = ref(false)

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


    // 切换小荠对话框显示
    const toggleNazuna = () => {
      showNazunaBubble.value = !showNazunaBubble.value
      showNijikaBubble.value = false
      if (showNazunaBubble.value && nazunaMessages.value.length === 0) {
        // 初始问候
        nazunaMessages.value.push({ role: 'ai', content: '今天天气不错，要写点什么吗？' })
      }
    }

    // 切换虹夏对话框显示
    const toggleNijika = () => {
      showNijikaBubble.value = !showNijikaBubble.value
      showNazunaBubble.value = false
      if (showNijikaBubble.value && nijikaMessages.value.length === 0) {
        nijikaMessages.value.push({ role: 'ai', content: '嗨~想看看最近的文章吗？' })
      }
    }

    // 发送小荠消息
    const sendMessage = async () => {
      if (!userInput.value.trim()) return
      const messageContent = userInput.value.trim()

      // 用户消息加入气泡
      nazunaMessages.value.push({ role: 'user', content: messageContent })
      userInput.value = ""
      isLoading.value = true

      try {
        const response = await axios.post('http://localhost:8080/api/chat', {
          messages: [
            { role: 'system', content: '你是一个可爱的七草荠 AI 助手' },
            // 将用户历史消息也加入，可以提供上下文
            ...nazunaMessages.value.map(msg => ({ role: msg.role === 'ai' ? 'assistant' : 'user', content: msg.content })),
            { role: 'user', content: messageContent }
          ]
        }, {
          headers: {
            'Content-Type': 'application/json'
          }
        })

        // 假设后端返回 { reply: "..." }
        nazunaMessages.value.push({ role: 'ai', content: response.data.reply })
      } catch (error) {
        console.error('AI 请求失败:', error)
        nazunaMessages.value.push({ role: 'ai', content: '嗯…我现在好像有点忙，请稍后再试~' })
      } finally {
        isLoading.value = false
        // 使用 nextTick 的替代方案
        setTimeout(() => {
          const bubble = document.querySelector('.chat-bubble')
          if (bubble) bubble.scrollTop = bubble.scrollHeight
        }, 0)
      }
    }

    // 发送虹夏消息
    const sendNijikaMessage = async () => {
      if (!nijikaInput.value.trim()) return
      const messageContent = nijikaInput.value.trim()
      nijikaMessages.value.push({ role: 'user', content: messageContent })
      nijikaInput.value = ""
      isNijikaLoading.value = true
      try {
        const response = await axios.post('http://localhost:8080/api/chat', {
          messages: [
            { role: 'system', content: '你是一个活泼的虹夏 AI 助手' },
            ...nijikaMessages.value.map(msg => ({ role: msg.role === 'ai' ? 'assistant' : 'user', content: msg.content })),
            { role: 'user', content: messageContent }
          ]
        })
        nijikaMessages.value.push({ role: 'ai', content: response.data.reply })
      } catch (error) {
        nijikaMessages.value.push({ role: 'ai', content: '嗯…我现在有点忙，请稍后再试~' })
      } finally {
        isNijikaLoading.value = false
        setTimeout(() => {
          const bubble = document.querySelector('.avatar-wrapper.right .chat-bubble')
          if (bubble) bubble.scrollTop = bubble.scrollHeight
        }, 0)
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
      showNazunaBubble,
      showNijikaBubble,
      nazunaMessages,
      userInput,
      isLoading,
      nijikaMessages,
      nijikaInput,
      isNijikaLoading,
      loadTopArticles,
      toggleNazuna,
      toggleNijika,
      sendMessage,
      sendNijikaMessage,
      refreshFeatured
    }
  }
}
</script>



<style>
.home {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
  background: white;
  min-height: 100vh;
}

/* Hero 区域 */
.hero-section {
  text-align: center;
  padding: 4rem 2rem;
  background: var(--gradient-primary);
  color: #3E2723;
  border-radius: 8px;
  margin-bottom: 3rem;
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
  position: relative;
  overflow: hidden;
}

/* Hero 中的绿色装饰条 */
.hero-section::after {
  content: "";
  position: absolute;
  width: 120px;
  height: 120px;
  background-color: #A7CEBC; /* 虹夏绿 */
  border-radius: 50%;
  top: -30px;
  right: -30px;
  opacity: 0.6;
  transform: rotate(20deg);
}

/* Hero文字 */
.hero-section h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.hero-section p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
}

/* 按钮 */
.btn-primary {
  display: inline-block;
  background: #3E2723;
  color: #FFD54F;
  padding: 0.8rem 2rem;
  border-radius: 4px;
  font-weight: bold;
  text-decoration: none;
  transition: all 0.3s;
}

.btn-primary:hover {
  background: #5D4037;
  color: #A7CEBC; /* 鼠标悬停按钮文字用辅助绿 */
}

/* 精选文章区标题 */
.featured-section h2 {
  font-size: 2rem;
  margin-bottom: 2rem;
  text-align: center;
  color: #3E2723;
  border-bottom: 3px solid #A7CEBC; /* 标题下划线用辅助绿 */
  display: inline-block;
  padding-bottom: 0.5rem;
}

/* 文章卡片 */
.article-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.article-card {
  background: var(--card-color);
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  padding: 1.5rem;
  transition: transform 0.3s, box-shadow 0.3s, border-color 0.3s;
  border: 2px solid transparent;
}

.article-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.15);
  border-color: #A7CEBC; /* 鼠标悬停卡片边框变绿 */
}

.article-card a {
  text-decoration: none;
  color: #3E2723;
}

.article-card h3 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: var(--card-color-h3);
}

.article-card p {
  color: #795548;
  margin-bottom: 1rem;
  line-height: 1.6;
}

.article-card .date {
  color: #A1887F;
  font-size: 0.9rem;
}

.rainka-avatar {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 130px;
  height: auto;
  cursor: pointer;
  z-index: 1000;
  transition: transform 0.3s;
}

.rainka-avatar:hover {
  transform: scale(1.1) rotate(-5deg); /* 悬停动画 */
}

.nazuna-avatar {
  position: fixed;
  bottom: 20px;
  left: 20px;
  width: 140px;
  height: auto;
  cursor: pointer;
  z-index: 1000;
  transition: transform 0.3s;
}

.nazuna-avatar:hover {
  transform: scale(1.1) rotate(5deg);
}

.avatar-wrapper {
  position: fixed;
  bottom: 24px;
  z-index: 1000;
}

.avatar-wrapper.left {
  left: 24px;
}

.avatar-wrapper.right {
  right: 24px;
}

.speech-bubble {
  position: absolute;
  bottom: 40px;
  max-width: 220px;
  padding: 12px 16px;
  background: #ffffff;
  color: #3E2723;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  animation: bubble-pop 0.25s ease-out;
  white-space: normal;
}


.speech-bubble.left {
  left: 120px; /* 头像宽度 + 间距 */
}

.speech-bubble.left::after {
  content: "";
  position: absolute;
  left: -8px;
  top: 24px;
  border-width: 8px 8px 8px 0;
  border-style: solid;
  border-color: transparent #ffffff transparent transparent;
}


.speech-bubble.right {
  right: 120px;
}

.speech-bubble.right::after {
  content: "";
  position: absolute;
  right: -8px;
  top: 24px;
  border-width: 8px 0 8px 8px;
  border-style: solid;
  border-color: transparent transparent transparent #ffffff;
}


@keyframes bubble-pop {
  from {
    opacity: 0;
    transform: translateX(8px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

.chat-bubble {
  max-width: 250px;
  max-height: 300px;
  overflow-y: auto;
  padding: 10px;
}

.messages {
  margin-bottom: 8px;
}

.messages .user {
  text-align: right;
  color: #3E2723;
  margin-bottom: 4px;
}

.messages .ai {
  text-align: left;
  color: #A7CEBC;
  margin-bottom: 4px;
}

.input-area {
  display: flex;
  gap: 4px;
}

.input-area input {
  flex: 1;
  padding: 6px 8px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

.input-area button {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  background-color: #A7CEBC;
  color: #3E2723;
  cursor: pointer;
}

.input-area button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.featured-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.refresh-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 30px;
  transition: transform 0.4s ease;
}

.refresh-btn:hover {
  transform: rotate(180deg);
}

</style>

