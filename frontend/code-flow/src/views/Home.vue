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



  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'
import {UserAction} from "@/enums/UserAction";

export default {
  name: 'HomePage',
  setup() {
    let featuredArticles = ref([])
    let featuredArticlesAction = new Map()

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
      loadTopArticles,
      refreshFeatured
    }
  }
}
</script>



<style>
@import '@/styles/home-layout.css';
</style>

