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
      <h3 class="comment-section-title">Comments ({{ comments.length }})</h3>

      <div v-if="replyingTo" class="replying-tip">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="9 17 4 12 9 7"/>
          <path d="M20 18v-2a4 4 0 0 0-4-4H4"/>
        </svg>
        Replying to <strong>{{ replyingToName }}</strong>
        <button class="replying-cancel" @click="replyingTo = null">Cancel</button>
      </div>

      <div class="comment-input-row">
        <textarea
          v-model="newComment"
          placeholder="Share your thoughts..."
          rows="3">
        </textarea>
        <button class="submit-comment-btn" @click="submitComment" :disabled="!newComment.trim()">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 2L11 13"/>
            <path d="M22 2L15 22L11 13L2 9L22 2Z"/>
          </svg>
          Publish
        </button>
      </div>
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
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  border: 1px solid #e8ecf1;
  margin-bottom: 2rem;
}

.comment-section-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 1rem 0;
}

.replying-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #667eea;
  background: rgba(102, 126, 234, 0.07);
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 8px;
  padding: 8px 14px;
  margin-bottom: 12px;
}

.replying-tip strong {
  color: #4f46e5;
}

.replying-cancel {
  margin-left: auto;
  background: none;
  border: none;
  color: #ef4444;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  padding: 2px 8px;
  border-radius: 4px;
  transition: background 0.15s;
}

.replying-cancel:hover {
  background: rgba(239, 68, 68, 0.08);
}

.comment-input-row {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.comment-input-row textarea {
  width: 100%;
  min-height: 90px;
  padding: 12px 14px;
  border: 1.5px solid #e2e6ed;
  border-radius: 10px;
  font-size: 14px;
  line-height: 1.6;
  resize: vertical;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
  font-family: inherit;
  background: #fafbfc;
}

.comment-input-row textarea:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: #fff;
}

.comment-input-row textarea::placeholder {
  color: #c0c6d0;
}

.submit-comment-btn {
  align-self: flex-end;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  padding: 10px 22px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s, opacity 0.15s;
}

.submit-comment-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(102, 126, 234, 0.35);
}

.submit-comment-btn:active:not(:disabled) {
  transform: scale(0.97);
}

.submit-comment-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
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