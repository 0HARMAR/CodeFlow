import { defineStore } from "pinia"
import axios from "@/utils/axios";

export const useArticleStore = defineStore('article', {
    state: () => ({
        articles: []
    }),

    actions: {
        async fetchArticleById(id) {
            const res = await axios.get(`http://localhost:8080/api/articles/${id}`)
            const a = res.data

            const article = {
                id: a.id,
                title: a.title,
                excerpt: a.excerpt,
                content: a.content,
                date: a.date,
                category: a.category,
                createAt: a.createdAt,
                updateAt: a.updatedAt,
                likes: a.likes || 0,
                authorId: a.authorId,
                views: a.views || 0,
                status: a.status
            }

            this.articles.push(article)
            return article
        },

        async findArticlesByOwnerId(ownerId) {
            const res = await axios.get(`http://localhost:8080/api/articles/user/${ownerId}`)
            return res.data.map(a => {
                return {
                    id: a.id,
                    title: a.title,
                    excerpt: a.excerpt,
                    content: a.content,
                    date: a.date,
                    category: a.category,
                    createAt: a.createdAt,
                    updateAt: a.updatedAt,
                    likes: a.likes || 0,
                    authorId: a.authorId,
                    views: a.views || 0,
                    status: a.status
                }
            })
        },

        async toggleLike(article) {
            const oldLikes = article.likes
            article.likes++

            try {
                await axios.put(`http://localhost:8080/api/articles/${article.id}`,
                    { ...article }
                )
            } catch (e) {
                article.likes = oldLikes
                throw e
            }
        },

        async fetchArticles() {
            this.loading = true
            this.error = ''

            try {
                const res = await axios.get('http://localhost:8080/api/articles')

                const articles = await Promise.all(
                    res.data.map(async (a) => {
                        return {
                            id: a.id,
                            title: a.title,
                            excerpt: a.excerpt,
                            content: a.content,
                            date: a.date,
                            category: a.category,
                            createAt: a.createdAt,
                            updateAt: a.updatedAt,
                            likes: a.likes || 0,
                            authorId: a.authorId,
                            views: a.views || 0,
                            status: a.status
                        }
                    })
                )

                this.articles = articles
                return articles
            } catch (e) {
                this.error = e.message
                console.error(e)
            } finally {
                this.loading = false
            }
        },

        async createArticle(articleForm, tags) {
            this.error = ''
            try {
                const userData = localStorage.getItem('user');
                if (!userData) throw new Error('未登录')

                const user = JSON.parse(userData);
                const articleData = {
                    title: articleForm.title,
                    category: articleForm.category,
                    content: articleForm.content,
                    authorId: user.id,
                    likes: 0,
                    status: 'PUBLISHED',
                }

                const res = await axios.post('http://localhost:8080/api/articles', articleData)

                const newArticle = {
                    id: res.data.id,
                    ...articleData,
                    data: res.data.publishDate,
                    createAt: res.data.createdAt,
                    updateAt: res.data.updatedAt
                }

                this.articles.push(newArticle)

                // create tags
                const tagRes = await axios.post(`http://localhost:8080/api/tags`, tags)

                // relate tags to article
                const articleId = newArticle.id
                const tagIds = tagRes.data.map(tag => tag.id)
                axios.post(`http://localhost:8080/api/articles/tags?articleId=${articleId}`, tagIds)
            } catch (e) {
                this.error = e.message || 'publish article failed'
                throw e
            }
        },

        async createArticleDraft(articleForm, tags) {
            this.error = ''
            try {
                const userData = localStorage.getItem('user');
                if (!userData) throw new Error('未登录')

                const user = JSON.parse(userData);
                const articleData = {
                    title: articleForm.title,
                    category: articleForm.category,
                    content: articleForm.content,
                    authorId: user.id,
                    likes: 0,
                    status: 'DRAFT',
                }

                const res = await axios.post('http://localhost:8080/api/articles', articleData)

                const newArticle = {
                    id: res.data.id,
                    ...articleData,
                    data: res.data.publishDate,
                    createAt: res.data.createdAt,
                    updateAt: res.data.updatedAt
                }

                this.articles.push(newArticle)

                // create tags
                const tagRes = await axios.post(`http://localhost:8080/api/tags`, tags)

                // relate tags to article
                const articleId = newArticle.id
                const tagIds = tagRes.data.map(tag => tag.id)
                axios.post(`http://localhost:8080/api/articles/tags?articleId=${articleId}`, tagIds)
            } catch (e) {
                this.error = e.message || 'publish article draft failed'
                throw e
            }
        },

        async deleteArticle(id) {
            this.error = ''
            try {
                await axios.delete(`http://localhost:8080/api/articles/${id}`)
                this.articles = this.articles.filter(a => a.id !== id)
            } catch (e) {
                this.error = e.message || '删除失败'
                throw e
            }
        },

        async searchArticles(keyword) {
            this.loading = true
            this.error = ''
            try {
                const res = await axios.get(`http://localhost:8080/api/articles/search?keyword=${keyword}`)
                this.articles = await Promise.all(
                    res.data.map(async (a) => {
                        return {
                            id: a.id,
                            title: a.title,
                            excerpt: a.excerpt,
                            content: a.content,
                            date: a.date,
                            category: a.category,
                            createAt: a.createdAt,
                            updateAt: a.updatedAt,
                            likes: a.likes || 0,
                            authorId: a.authorId,
                            views: a.views || 0,
                        }
                    })
                )
            } catch (e) {
                this.error = e.message
                console.error(e)
            } finally {
                this.loading = false
            }
            return this.articles
        },

        async getRecommendArticles(size) {
            this.loading = true
            this.error = ''

            try {
                const res = await axios.get(`http://localhost:8080/api/recommend?size=${size}`)
                const articles = await Promise.all(
                    res.data.map(async (a) => {
                        return {
                            id: a.id,
                            title: a.title,
                            excerpt: a.excerpt,
                            content: a.content,
                            date: a.date,
                            category: a.category,
                            createAt: a.createdAt,
                            updateAt: a.updatedAt,
                            likes: a.likes || 0,
                            authorId: a.authorId,
                            views: a.views || 0,
                            status: a.status
                        }
                    })
                )
                this.articles = articles
                return articles
            } catch (e) {
                this.error = e.message
                console.error(e)
            }
        }
    },

    getters: {
        getById: (state) => (id) => {
            return state.articles.find(article => article.id === id)
        }
    }
})