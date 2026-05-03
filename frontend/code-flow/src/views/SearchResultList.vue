<template>
  <div class="search-results">
    <h1 class="page-title">
      <template v-if="keyword">搜索结果：{{ keyword }}</template>
      <template v-else>搜索文章</template>
    </h1>

    <div v-if="loading" class="loading">搜索中...</div>

    <div v-else-if="articles.length === 0" class="empty">
      <template v-if="keyword">
        <p>未找到与 "{{ keyword }}" 相关的文章</p>
        <span>试试其他关键词吧～</span>
      </template>
      <template v-else>
        <p>输入关键词搜索文章</p>
      </template>
    </div>

    <div v-else class="articles">
      <div class="article-item" v-for="article in articles" :key="article.id">
        <div class="views">👁 {{ article.views }}</div>
        <router-link :to="`/article/${article.id}`">
          <h2>{{ article.title }}</h2>
          <p class="excerpt">{{ article.excerpt }}</p>
          <div class="article-meta">
            <span class="date">{{ article.date }}</span>
            <span class="author" v-if="authorMap[article.authorId]">{{ authorMap[article.authorId] }}</span>
            <span class="likes">赞 {{ article.likes }}</span>
            <span class="category">{{ article.category }}</span>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { useArticleStore } from "@/stores/article";
import { reactive, ref, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import axios from "@/utils/axios";

export default {
  name: "SearchResultList",
  setup() {
    const store = useArticleStore();
    const route = useRoute();
    const articles = ref([]);
    const loading = ref(false);
    const keyword = ref("");
    const authorMap = reactive({});

    const doSearch = async () => {
      const q = route.query.keyword;
      keyword.value = q || "";
      if (!q) {
        articles.value = [];
        return;
      }
      loading.value = true;
      try {
        const results = await store.searchArticles(q);
        articles.value = results || [];
        for (const article of articles.value) {
          if (article.authorId && !authorMap[article.authorId]) {
            loadAuthor(article.authorId);
          }
        }
      } finally {
        loading.value = false;
      }
    };

    const loadAuthor = async (id) => {
      if (authorMap[id]) return;
      try {
        const res = await axios.get(`/api/users/${id}`);
        authorMap[id] = res.data.username;
      } catch (e) {
        authorMap[id] = "未知";
      }
    };

    onMounted(doSearch);

    watch(() => route.query.keyword, doSearch);

    return { articles, loading, keyword, authorMap };
  }
};
</script>

<style scoped>
.search-results {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.page-title {
  font-size: 2rem;
  margin-bottom: 2rem;
  text-align: center;
  color: #333;
}

.loading, .empty {
  text-align: center;
  padding: 3rem 0;
  color: #999;
}

.empty p {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
}

.articles {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.article-item {
  position: relative;
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.article-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.views {
  position: absolute;
  top: 1rem;
  right: 1rem;
  font-size: 0.85rem;
  color: #555;
  background: rgba(0, 0, 0, 0.05);
  padding: 0.3rem 0.6rem;
  border-radius: 12px;
}

.article-item a {
  text-decoration: none;
  color: inherit;
}

.article-item h2 {
  font-size: 1.5rem;
  margin-bottom: 0.75rem;
  color: #333;
}

.excerpt {
  color: #666;
  line-height: 1.6;
  margin-bottom: 0.75rem;
}

.article-meta {
  display: flex;
  gap: 1rem;
  color: #999;
  font-size: 0.85rem;
  flex-wrap: wrap;
}

.likes { color: #e74c3c; font-weight: bold; }
.category {
  background: #f0f0f0;
  padding: 0.15rem 0.6rem;
  border-radius: 1rem;
}
</style>
