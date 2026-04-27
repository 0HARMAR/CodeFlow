<template>
  <div class="draft-box">
    <h1 class="title">草稿箱</h1>

    <!-- 空状态 -->
    <div class="empty-state" v-if="drafts.length === 0">
      <p>📭 还没有草稿</p>
      <span>写文章的时候记得保存哦～</span>
    </div>

    <!-- 草稿列表 -->
    <ul class="draft-list" v-else>
      <li
          class="draft-item"
          v-for="draft in drafts"
          :key="draft.id"
      >
        <div class="draft-info">
          <h3 class="draft-title">
            {{ draft.title || '未命名草稿' }}
          </h3>
          <p class="draft-time">
            最后编辑：{{ draft.updateAt }}
          </p>
        </div>

        <div class="draft-actions">
          <button class="btn edit" @click="continueEdit(draft)">继续编辑</button>
          <button class="btn delete">删除</button>
        </div>
      </li>
    </ul>
  </div>
</template>


<script>
import { useArticleStore } from "@/stores/article";
import { ref, onMounted } from "vue";
import {useRouter} from "vue-router";

export default {
  name: "DraftBox",
  setup() {
    const articleStore = useArticleStore();
    const drafts = ref([]);
    const router = useRouter();

    onMounted(async () => {
      const articles = await articleStore.fetchArticles();
      for (let i = 0; i < articles.length; i++) {
        const article = articles[i];
        if (article.status === 'DRAFT') {
          drafts.value.push(article);
        }
      }
    });

    const continueEdit = (draft) => {
      router.push({ name: 'CreateArticle', query: { id: draft.id }})
    };

    return {
      drafts,
      continueEdit
    };
  }
};
</script>


<style scoped>
.draft-box {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #999;
}

.empty-state p {
  font-size: 18px;
  margin-bottom: 8px;
}

/* 草稿列表 */
.draft-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.draft-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 12px;
}

.draft-title {
  margin: 0 0 4px;
  font-size: 18px;
}

.draft-time {
  margin: 0;
  font-size: 14px;
  color: #888;
}

/* 操作按钮 */
.draft-actions {
  display: flex;
  gap: 8px;
}

.btn {
  padding: 6px 12px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
}

.btn.edit {
  background-color: #409eff;
  color: #fff;
}

.btn.delete {
  background-color: #f56c6c;
  color: #fff;
}
</style>
