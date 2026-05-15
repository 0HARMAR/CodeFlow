<template>
  <div class="creator-center">
    <!-- 左侧：操作区 + 草稿箱 -->
    <div class="left-panel">
      <h2 class="title">创作中心</h2>
      <button class="btn primary" @click="goWrite">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        Write Article
      </button>
      <div class="sidebar-drafts">
        <DraftBox />
      </div>
    </div>

    <!-- 右侧：我的文章 -->
    <div class="right-panel">
      <MyArticles />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import DraftBox from "@/views/DraftBox.vue";
import MyArticles from "@/views/MyArticles.vue";

const router = useRouter()

const goWrite = () => {
  // 跳转到你已有的 createArticle.vue
  router.push({ name: 'CreateArticle' ,query: { id: -1 }})
}
</script>

<style scoped>
.creator-center {
  display: flex;
  gap: 0;
  height: calc(100vh - 60px);
  overflow: hidden;
}

/* ---- left ---- */

.left-panel {
  width: 280px;
  min-width: 280px;
  flex-shrink: 0;
  padding: 2rem 1.25rem 1rem;
  background: #fafbfc;
  border-right: 1px solid #eef0f4;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.title {
  font-size: 1.3rem;
  font-weight: 800;
  color: #1a1a2e;
  margin: 0 0 1.25rem;
  letter-spacing: -0.3px;
  flex-shrink: 0;
}

.btn.primary {
  width: 100%;
  padding: 0.65rem 0;
  font-size: 0.9rem;
  font-weight: 700;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  transition: transform 0.15s, box-shadow 0.15s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  flex-shrink: 0;
  margin-bottom: 1.25rem;
}

.btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
}

.btn.primary:active {
  transform: scale(0.97);
}

.sidebar-drafts {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.sidebar-drafts :deep(.draft-box) {
  max-width: none;
  margin: 0;
  padding: 0;
}

.sidebar-drafts :deep(.title) {
  font-size: 0.85rem;
  font-weight: 700;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 0.75rem;
}

.sidebar-drafts :deep(.draft-item) {
  padding: 10px 12px;
  margin-bottom: 6px;
}

.sidebar-drafts :deep(.draft-title) {
  font-size: 0.85rem;
  margin-bottom: 2px;
}

.sidebar-drafts :deep(.draft-time) {
  font-size: 0.7rem;
}

.sidebar-drafts :deep(.draft-actions) {
  flex-direction: column;
  gap: 4px;
}

.sidebar-drafts :deep(.btn) {
  padding: 4px 8px;
  font-size: 0.7rem;
}

/* ---- right ---- */

.right-panel {
  flex: 1;
  min-width: 0;
  overflow-y: auto;
  padding: 1.5rem 2rem;
}
</style>