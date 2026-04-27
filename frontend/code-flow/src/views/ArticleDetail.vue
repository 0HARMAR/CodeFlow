<template>
  <div v-if="showExplainPopup" class="explain-popup">
    <div class="explain-header">
      <span>🤖 AI 解释</span>
      <span class="close-btn" @click="closeExplain">✖</span>
    </div>
    <div class="explain-content">
      {{ explainText }}
    </div>
  </div>


  <div class="article-detail" v-if="article">
    <div class="article-header">
      <h1>{{ article.title }}</h1>
      <div class="article-meta">
        <span class="date">发布于：{{ article.date }}</span>
        <span class="category">分类：{{ article.category }}</span>
        <span class="author">作者：{{ authorName }}</span>
        <span class="read-time">阅读时间：{{ readTime }}</span>
      </div>
    </div>

    <div class="article-content" ref="contentRef">
      <div v-html="article.content"></div>
    </div>


    <div
        v-if="showExplainBtn"
        class="explain-btn"
        :style="{ top: btnPosition.top + 'px', left: btnPosition.left + 'px' }"
        @click="handleExplain"
    >
      🤖 解释
    </div>


    <div class="like-section">
      <button class="like-btn" @click="toggleLike">
        <span v-if="liked">❤️ liked</span>
        <span v-else>🤍 like</span>
      </button>
      <span class="like-count">like: {{ article.likes }}</span>
    </div>

    <!-- comment  -->
    <div class="comment-section">
      <div v-if="replyingTo" class="replying-tip">
        replying to {{ replyingTo.author }}
        <span @click="replyingTo = null" style="cursor:pointer">cancel</span>
      </div>

      <textarea
        v-model="newComment"
        placeholder="write you comment...">
      </textarea>
      <button @click="submitComment">publish comment</button>
    </div>

    <!-- comment list  -->
    <template v-for="c in comments" :key="c.id">
      <CommentItem
          :comment="c"
          :getCommentAuthorName="getCommentAuthorName"
          @reply="onReply"
      />
    </template>

    <div class="article-actions">
      <router-link to="/articles" class="btn-back">返回列表</router-link>
    </div>
  </div>
  <div v-else>
    Loading...
  </div>
</template>

<script>
import ArticleDetailLogic from './ArticleDetail.js'

export default ArticleDetailLogic
</script>

<style scoped>
.article-detail {
  max-width: 1200px;
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

.like-section {
  text-align: center;
  margin-bottom: 2rem;
}

.like-btn {
  background: none;
  border: 2px solid #ff6b81;
  color: #ff6b81;
  padding: 0.6rem 1.4rem;
  font-size: 1.1rem;
  border-radius: 30px;
  cursor: pointer;
  transition: 0.3s;
}

.like-btn:hover {
  background: #ff6b81;
  color: white;
}

.like-btn:active {
  transform: scale(0.95);
}

.like-count {
  display: inline-block;
  margin-left: 1rem;
  color: #555;
  font-size: 1rem;
}

.comment-section {
  background: #fff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.comment-section button {
  background: #667eea;
  color: white;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
}

.explain-btn {
  position: absolute;
  z-index: 1000;
  padding: 6px 10px;
  font-size: 13px;
  background: #222;
  color: #fff;
  border-radius: 6px;
  cursor: pointer;
  user-select: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.explain-btn:hover {
  background: #444;
}

.explain-popup {
  position: fixed;
  right: 24px;
  bottom: 24px;
  width: 320px;
  max-height: 45vh;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  z-index: 2000;
}

.explain-header {
  padding: 10px 14px;
  font-weight: bold;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
}

.close-btn {
  cursor: pointer;
  color: #999;
}

.explain-content {
  padding: 12px 14px;
  font-size: 14px;
  line-height: 1.6;
  overflow-y: auto;
}


</style>