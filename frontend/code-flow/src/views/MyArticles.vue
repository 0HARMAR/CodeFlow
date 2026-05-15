<template>
  <div class="my-articles">
    <h2 class="section-title">My Articles</h2>

    <div class="empty-state" v-if="articles.length === 0">
      <p>No published articles yet</p>
      <span>Start writing and share your ideas with the world!</span>
    </div>

    <div class="article-list" v-else>
      <article
        class="article-card"
        v-for="a in articles"
        :key="a.id"
      >
        <div class="card-body">
          <div class="card-meta">
            <span class="category-badge">{{ a.category || 'Uncategorized' }}</span>
            <span class="card-date">{{ formatTime(a.date || a.createAt) }}</span>
          </div>
          <h3 class="card-title">
            <router-link :to="`/article/${a.id}`">{{ a.title || 'Untitled' }}</router-link>
          </h3>
          <p class="card-excerpt" v-if="a.excerpt">{{ a.excerpt }}</p>
          <div class="card-stats">
            <span class="stat">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
              {{ a.likes || 0 }}
            </span>
            <span class="stat">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
              {{ a.views || 0 }}
            </span>
          </div>
        </div>
        <div class="card-actions">
          <button class="btn-edit" @click="editArticle(a)" title="Edit">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
          </button>
          <button class="btn-delete" @click="deleteArticle(a)" title="Delete">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
          </button>
        </div>
      </article>
    </div>
  </div>
</template>

<script>
import { useArticleStore } from "@/stores/article";
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";

export default {
  name: "MyArticles",
  setup() {
    const articleStore = useArticleStore();
    const articles = ref([]);
    const router = useRouter();

    onMounted(async () => {
      const userData = localStorage.getItem('user');
      if (!userData) return;
      const user = JSON.parse(userData);
      const all = await articleStore.findArticlesByOwnerId(user.id);
      articles.value = (all || []).filter(a => a.status !== 'DRAFT');
    });

    const formatTime = (val) => {
      if (!val) return '';
      const d = new Date(val);
      if (isNaN(d.getTime())) return '';
      const pad = (n) => String(n).padStart(2, '0');
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
    };

    const editArticle = (article) => {
      router.push({ name: 'CreateArticle', query: { id: article.id } });
    };

    const deleteArticle = async (article) => {
      if (!confirm('Delete this article?')) return;
      try {
        await articleStore.deleteArticle(article.id);
        articles.value = articles.value.filter(a => a.id !== article.id);
      } catch (e) {
        alert('Delete failed, please try again');
      }
    };

    return { articles, formatTime, editArticle, deleteArticle };
  }
};
</script>

<style scoped>
.my-articles {
  padding: 24px;
}

.section-title {
  font-size: 1.2rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 1rem;
}

/* empty */
.empty-state {
  text-align: center;
  padding: 48px 0;
  color: #9ca3af;
}
.empty-state p { font-size: 1rem; margin: 0 0 4px; color: #6b7280; }
.empty-state span { font-size: 0.85rem; }

/* list */
.article-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.article-card {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 14px 18px;
  background: #fff;
  border: 1px solid #e8ecf1;
  border-radius: 10px;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.article-card:hover {
  border-color: #d1d5e0;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}

.card-body { flex: 1; min-width: 0; }

.card-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.category-badge {
  font-size: 0.7rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.3px;
  padding: 2px 8px;
  border-radius: 10px;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.card-date {
  font-size: 0.75rem;
  color: #9ca3af;
}

.card-title {
  font-size: 1rem;
  font-weight: 700;
  margin: 0 0 4px;
  line-height: 1.4;
}

.card-title a {
  color: #1f2937;
  text-decoration: none;
  transition: color 0.15s;
}

.card-title a:hover { color: #667eea; }

.card-excerpt {
  font-size: 0.85rem;
  color: #9ca3af;
  margin: 0 0 8px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-stats {
  display: flex;
  gap: 14px;
}

.card-stats .stat {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 0.78rem;
  color: #9ca3af;
}

/* actions */
.card-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
  margin-left: 12px;
}

.btn-edit,
.btn-delete {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s;
  background: transparent;
  color: #9ca3af;
}

.btn-edit:hover { background: rgba(102, 126, 234, 0.1); color: #667eea; }
.btn-delete:hover { background: rgba(239, 68, 68, 0.1); color: #ef4444; }
</style>
