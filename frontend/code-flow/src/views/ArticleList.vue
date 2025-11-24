<template>
  <div class="article-list">
    <h1>博客文章列表</h1>
    
    <div class="articles">
      <div class="article-item" v-for="article in articles" :key="article.id">
        <router-link :to="`/article/${article.id}`">
          <h2>{{ article.title }}</h2>
          <p class="excerpt">{{ article.excerpt }}</p>
          <div class="article-meta">
            <span class="date">发布于：{{ article.date }}</span>
            <span class="category">分类：{{ article.category }}</span>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'ArticleList',
  data() {
    return {
      articles: [],
      loading: false,
      error: ''
    }
  },
  mounted() {
    this.fetchArticles();
  },
  methods: {
    async fetchArticles() {
      this.loading = true;
      this.error = '';
      
      try {
        // 调用后端API获取文章列表
        const response = await axios.get('http://localhost:8080/api/articles');
        
        // 将后端返回的数据转换为前端需要的格式
        this.articles = response.data.map(article => ({
          id: article.id,
          title: article.title,
          excerpt: article.content.substring(0, 100) + '...',
          date: article.date || new Date(article.publishDate || new Date()).toLocaleDateString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
          }),
          category: article.category
        }));
      } catch (error) {
        console.error('获取文章列表失败:', error);
        this.error = '获取文章列表失败，请稍后重试';
      } finally {
        this.loading = false;
      }
    }
  }
}
</script>

<style scoped>
.article-list {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.article-list h1 {
  font-size: 2.5rem;
  margin-bottom: 2rem;
  text-align: center;
  color: #333;
}

.articles {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.article-item {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.article-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.article-item a {
  text-decoration: none;
  color: inherit;
}

.article-item h2 {
  font-size: 1.8rem;
  margin-bottom: 1rem;
  color: #333;
}

.article-item .excerpt {
  color: #666;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 0.9rem;
}

.date, .category {
  display: flex;
  align-items: center;
}

.category {
  background: #f0f0f0;
  padding: 0.2rem 0.8rem;
  border-radius: 16px;
}
</style>