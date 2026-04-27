<template>
  <div class="creator-center">
    <!-- 左侧：操作区 -->
    <div class="left-panel">
      <h2 class="title">创作中心</h2>
      <div class="actions">
        <button class="btn primary" @click="goWrite">
          ✍️ 写文章
        </button>
      </div>
    </div>

    <!-- 右侧：草稿箱 -->
    <div class="right-panel">
      <DraftBox />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import DraftBox from "@/views/DraftBox.vue";

const router = useRouter()

const goWrite = () => {
  // 跳转到你已有的 createArticle.vue
  router.push({ name: 'CreateArticle' ,query: { id: -1 }})
}
</script>

<style scoped>
.creator-center {
  display: flex;
  gap: 24px;
  padding: 24px;
}

/* 左侧 */
.left-panel {
  width: 260px;
  flex-shrink: 0;
}

.title {
  margin-bottom: 16px;
}

/* 右侧 */
.right-panel {
  flex: 1;
  min-width: 0; /* 防止 flex 溢出 */
}

/* 按钮样式（示例） */
.btn.primary {
  width: 100%;
  padding: 10px 0;
  font-size: 16px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  background-color: #409eff;
  color: #fff;
}
</style>