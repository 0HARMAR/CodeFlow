<template>
  <div class="comment-item" :class="{ 'is-nested': depth > 0 }" :style="{ marginLeft: depth > 0 ? '32px' : '0' }">
    <div class="comment-card">
      <div class="comment-avatar" :style="{ background: avatarColor }">
        {{ avatarLetter }}
      </div>
      <div class="comment-body">
        <div class="comment-meta">
          <span class="comment-author">{{ authorName }}</span>
          <span class="comment-dot">·</span>
          <span class="comment-time">{{ timeAgo }}</span>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
        <div class="comment-footer">
          <button class="reply-btn" @click="$emit('reply', comment)">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="9 17 4 12 9 7"/>
              <path d="M20 18v-2a4 4 0 0 0-4-4H4"/>
            </svg>
            Reply
          </button>
        </div>
      </div>
    </div>

    <div v-if="comment.children && comment.children.length" class="nested-comments">
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
import { computed } from 'vue'

const AVATAR_COLORS = [
  '#6C5CE7', '#00B894', '#FDCB6E', '#E17055', '#0984E3',
  '#D63031', '#00CEC9', '#A29BFE', '#FD79A8', '#636E72',
  '#F39C12', '#2ECC71', '#E74C3C', '#3498DB', '#9B59B6',
]

export default {
  name: "CommentItem",
  props: {
    comment: Object,
    depth: {
      type: Number,
      default: 0
    },
    getCommentAuthorName: Function
  },
  setup(props) {
    const authorName = computed(() => {
      return props.getCommentAuthorName
        ? props.getCommentAuthorName(props.comment.userId)
        : 'unknown'
    })

    const avatarLetter = computed(() => {
      return (authorName.value || '?').charAt(0).toUpperCase()
    })

    const avatarColor = computed(() => {
      let hash = 0
      const name = authorName.value || 'unknown'
      for (let i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash)
      }
      return AVATAR_COLORS[Math.abs(hash) % AVATAR_COLORS.length]
    })

    const timeAgo = computed(() => {
      const t = props.comment.createTime
      if (!t) return ''
      const date = new Date(t.replace(' ', 'T') + (t.includes('Z') ? '' : 'Z'))
      if (isNaN(date.getTime())) return t
      const now = Date.now()
      const diff = now - date.getTime()
      const sec = Math.floor(diff / 1000)
      if (sec < 60) return 'just now'
      const min = Math.floor(sec / 60)
      if (min < 60) return `${min}m ago`
      const hr = Math.floor(min / 60)
      if (hr < 24) return `${hr}h ago`
      const days = Math.floor(hr / 24)
      if (days < 30) return `${days}d ago`
      const months = Math.floor(days / 30)
      if (months < 12) return `${months}mo ago`
      return `${Math.floor(months / 12)}y ago`
    })

    return { authorName, avatarLetter, avatarColor, timeAgo }
  }
}
</script>

<style scoped>
.comment-item {
  margin-bottom: 2px;
}

.nested-comments {
  position: relative;
  margin-top: 2px;
  padding-left: 0;
}

.comment-card {
  display: flex;
  gap: 12px;
  padding: 14px 16px;
  background: #fafbfc;
  border: 1px solid #e8ecf1;
  border-radius: 10px;
  transition: background 0.15s, border-color 0.15s;
}

.comment-card:hover {
  background: #f5f7fa;
  border-color: #dde1e7;
}

.is-nested .comment-card {
  border-left: 3px solid #667eea;
  border-radius: 0 10px 10px 0;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  min-width: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.5px;
  user-select: none;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
  font-size: 13px;
}

.comment-author {
  font-weight: 600;
  color: #222;
}

.comment-dot {
  color: #c0c6d0;
}

.comment-time {
  color: #9ca3af;
  font-size: 12px;
}

.comment-content {
  font-size: 14px;
  line-height: 1.65;
  color: #4b5563;
  white-space: pre-wrap;
  word-break: break-word;
  margin-bottom: 8px;
}

.comment-footer {
  display: flex;
  align-items: center;
}

.reply-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  background: none;
  border: none;
  color: #9ca3af;
  font-size: 12px;
  font-weight: 500;
  padding: 3px 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s;
}

.reply-btn:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.08);
}

.reply-btn:active {
  transform: scale(0.95);
}
</style>
