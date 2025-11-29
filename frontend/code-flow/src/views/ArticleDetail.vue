<template>
  <div class="article-detail">
    <div class="article-header">
      <h1>{{ article.title }}</h1>
      <div class="article-meta">
        <span class="date">发布于：{{ article.date }}</span>
        <span class="category">分类：{{ article.category }}</span>
      </div>
    </div>
    
    <div class="article-content">
      <div v-html="article.content"></div>
    </div>
    
    <div class="article-actions">
      <router-link to="/articles" class="btn-back">返回列表</router-link>
    </div>
  </div>
</template>

<script>
import ArticleDetailLogic from './ArticleDetail.js'

export default ArticleDetailLogic
</script>

<style scoped>
.article-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.article-header {
  margin-bottom: 2rem;
  text-align: center;
}

.article-header h1 {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 1rem;
}

.article-meta {
  display: flex;
  justify-content: center;
  gap: 2rem;
  color: #999;
  font-size: 0.9rem;
}

.category {
  background: #f0f0f0;
  padding: 0.2rem 0.8rem;
  border-radius: 16px;
}

.article-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.article-content h2 {
  font-size: 1.8rem;
  margin: 2rem 0 1rem;
  color: #333;
}

.article-content p {
  line-height: 1.8;
  color: #666;
  margin-bottom: 1rem;
}

.article-actions {
  text-align: center;
}

.btn-back {
  display: inline-block;
  background: #667eea;
  color: white;
  padding: 0.8rem 2rem;
  border-radius: 4px;
  text-decoration: none;
  transition: background 0.3s;
}

.btn-back:hover {
  background: #5a67d8;
}
</style>