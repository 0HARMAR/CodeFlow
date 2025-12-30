import { defineStore } from "pinia";
import axios from 'axios'

export const useCommentStore = defineStore('comment', {
    state: () => ({
        comments: [],
        loading: false
    }),

    actions: {
        async fetchComments(articleId) {
            this.loading = true
            try {
                const res = await axios.get(
                    `http://localhost:8080/api/comments/article/${articleId}`
                )
                this.comments = res.data
            } catch (err) {
                console.error('获取评论失败', err)
            } finally {
                this.loading = false
            }
        },

        async addComment(payload) {
            /**
             * payload 示例：
             * {
             *   articleId: 1,
             *   userId: 2,
             *   parentId: null,
             *   replyToUserId: null,
             *   content: "写得很好"
             * }
             */
            try {
                await axios.post('http://localhost:8080/api/comments', payload)
                // 重新拉取评论（最简单、最稳）
                await this.fetchComments(payload.articleId)
            } catch (err) {
                console.error('发表评论失败', err)
                throw err
            }
        },

        // 删除评论
        async deleteComment(commentId, articleId) {
            try {
                await axios.delete(`http://localhost:8080/api/comments/${commentId}`)
                await this.fetchComments(articleId)
            } catch (err) {
                console.error('删除评论失败', err)
            }
        }
    }
})
