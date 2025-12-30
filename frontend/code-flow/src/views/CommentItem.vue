<template>
  <div class="comment-item" :style="{ marginLeft: depth * 20 + 'px' }">
    <div class="comment-header">
      <div class="author-date">
        <span class="comment-author">{{ getCommentAuthorName(comment.userId) }}</span>
        <span class="comment-date">{{ comment.createTime }}</span>
      </div>
      <button class="reply-btn" @click="$emit('reply', comment)">reply</button>
    </div>
    <div class="comment-content">{{ comment.content }}</div>

    <div v-if="comment.children && comment.children.length">
      <CommentItem
          v-for="child in comment.children"
          :key="child.id"
          :comment="child"
          :depth="depth + 1"
          @reply="$emit('reply', $event)"
          :getCommentAuthorName="getCommentAuthorName"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: "CommentItem",
  props: {
    comment: Object,
    depth: {
      type: Number,
      default: 0
    },
    getCommentAuthorName: Function
  }
}
</script>

<style scoped>
.comment-section textarea {
  width: 100%;
  min-height: 90px;
  padding: 0.8rem;
  border-radius: 6px;
  border: 1px solid #ddd;
  margin-bottom: 0.8rem;
}

.comment-section button {
  background: #667eea;
  color: white;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
}

.comment-item {
  padding: 1rem 0;
  border-top: 1px solid #eee;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
  color: #999;
  margin-bottom: 0.3rem;
}

.author-date {
  display: flex;
  gap: 0.5rem; /* 作者和日期间距 */
}

.reply-btn {
  background: none;
  border: 1px solid #667eea;
  color: #667eea;
  padding: 0.2rem 0.6rem;
  font-size: 0.8rem;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.reply-btn:hover {
  background: #667eea;
  color: white;
}

.reply-btn:active {
  transform: scale(0.95);
}

.comment-actions button {
  background: none;
  border: 1px solid #667eea;
  color: #667eea;
  padding: 0.2rem 0.6rem;
  font-size: 0.8rem;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.comment-actions button:hover {
  background: #667eea;
  color: white;
}

.comment-actions button:active {
  transform: scale(0.95);
}

</style>