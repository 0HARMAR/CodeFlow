<template>
  <div class="home">
    <div class="hero-section">
      <h1>欢迎来到CodeFlow</h1>
      <p>分享技术见解和生活感悟</p>
      <router-link to="/articles" class="btn-primary">浏览文章</router-link>
    </div>
    
    <div class="featured-section">
      <h2>精选文章</h2>
      <div class="article-cards">
        <div class="article-card" v-for="article in featuredArticles" :key="article.id">
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
export default {
  name: 'HomePage',
  data() {
    return {
      featuredArticles: [
        {
          id: 1,
          title: 'Vue 3 新特性详解',
          excerpt: '探索 Vue 3 带来的 Composition API、Teleport、Fragments 等新特性...',
          date: '2023-10-15'
        },
        {
          id: 2,
          title: '前端性能优化实战',
          excerpt: '如何通过懒加载、缓存策略和代码分割提升你的应用性能...',
          date: '2023-10-10'
        },
        {
          id: 3,
          title: '现代 CSS 技巧大全',
          excerpt: '掌握 Grid、Flexbox、CSS 变量等现代 CSS 技术，让你的布局更灵活...',
          date: '2023-10-05'
        }
      ]
    }
  }
}
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.hero-section {
  text-align: center;
  padding: 4rem 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 3rem;
}

.hero-section h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.hero-section p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
}

.btn-primary {
  display: inline-block;
  background: white;
  color: #667eea;
  padding: 0.8rem 2rem;
  border-radius: 4px;
  font-weight: bold;
  text-decoration: none;
  transition: background 0.3s;
}

.btn-primary:hover {
  background: #f0f0f0;
}

.featured-section h2 {
  font-size: 2rem;
  margin-bottom: 2rem;
  text-align: center;
}

.article-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.article-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  transition: transform 0.3s, box-shadow 0.3s;
}

.article-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.article-card a {
  text-decoration: none;
  color: #333;
}

.article-card h3 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #333;
}

.article-card p {
  color: #666;
  margin-bottom: 1rem;
  line-height: 1.6;
}

.article-card .date {
  color: #999;
  font-size: 0.9rem;
}
</style>