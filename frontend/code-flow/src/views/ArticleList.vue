<template>
  <div class="article-list">
    <h1>博客文章列表</h1>

    <div class="articles">
      <div class="article-item" v-for="article in store.articles" :key="article.id">
        <router-link :to="`/article/${article.id}`">
          <h2>{{ article.title }}</h2>
          <p class="excerpt">{{ article.excerpt }}</p>
          <div class="article-meta">
            <span class="date">发布于：{{ article.date }}</span>
            <span class="author">作者：{{ authorMap[article.authorId] }}</span>
            <span class="likes">赞：{{ article.likes }}</span>
            <span class="category">分类：{{ article.category }}</span>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import { useArticleStore } from "../stores/article";
import axios from "@/utils/axios";

const store = useArticleStore()

const authorMap = reactive({})

onMounted(async () => {
  if (store.articles.length === 0) {
    await store.fetchArticles()
  }

  for (const article of store.articles) {
    loadAuthor(article.authorId)
  }
})

const loadAuthor = async (id) => {
  if (authorMap[id]) return

  const res = await axios.get(`http://localhost:8080/api/users/${id}`)
  console.log(res.data.username)
  authorMap[id] = res.data.username
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

.author, .likes {
  margin-left: 1rem;
  display: flex;
  align-items: center;
}
.likes {
  color: #e74c3c;
  font-weight: bold;
}
</style>