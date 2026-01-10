import { defineStore } from "pinia"
import axios from "@/utils/axios";

export const useArticleStore = defineStore('article', {
    state: () => ({
        articles: []
    }),

    actions: {
        async fetchArticleById(id) {
            const existing = this.getById(id)
            if (existing) return existing

            const res = await axios.get(`http://localhost:8080/api/articles/${id}`)
            const a = res.data

            const article = {
                id: a.id,
                title: a.title,
                excerpt: a.excerpt,
                content: a.content,
                date: a.date,
                category: a.category,
                createAt: a.createAt,
                updateAt: a.updateAt,
                likes: a.likes || 0,
                authorId: a.authorId,
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
                    createAt: a.createAt,
                    updateAt: a.updateAt,
                    likes: a.likes || 0,
                    authorId: a.authorId,
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
                            createAt: a.createAt,
                            updateAt: a.updateAt,
                            likes: a.likes || 0,
                            authorId: a.authorId,
                        }
                    })
                )

                this.articles = articles
            } catch (e) {
                this.error = e.message
                console.error(e)
            } finally {
                this.loading = false
            }
        },

        async createArticle(articleForm) {
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
                }

                const res = await axios.post('http://localhost:8080/api/articles', articleData)

                const newArticle = {
                    id: res.data.id,
                    ...articleData,
                    data: res.data.publishDate,
                    createAt: res.data.createAt,
                    updateAt: res.data.updateAt
                }

                this.articles.push(newArticle)
            } catch (e) {
                this.error = e.message || 'publish article failed'
                throw e
            }
        },
    },

    getters: {
        getById: (state) => (id) => {
            return state.articles.find(article => article.id === id)
        }
    }
})