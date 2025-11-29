// src/views/ArticleDetail.js
import axios from 'axios';

export default {
    name: 'ArticleDetail',
    props: {
        id: {
            type: String,
            required: true
        }
    },
    data() {
        return {
            article: {
                id: '',
                title: '',
                date: '',
                category: '',
                content: ''
            },
            loading: false,
            error: ''
        }
    },
    mounted() {
        this.fetchArticle()
    },
    methods: {
        async fetchArticle() {
            this.loading = true;
            this.error = '';

            try {
                const response = await axios.get(`http://localhost:8080/api/articles/${this.id}`);
                const articleData = response.data;

                this.article = {
                    id: articleData.id,
                    title: articleData.title,
                    date: articleData.date || new Date(articleData.publishDate || new Date()).toLocaleDateString('zh-CN', {
                        year: 'numeric',
                        month: '2-digit',
                        day: '2-digit'
                    }),
                    category: articleData.category,
                    content: this.formatContent(articleData.content)
                };
            } catch (error) {
                console.error('获取文章详情失败:', error);
                this.error = '获取文章详情失败，请稍后重试';
            } finally {
                this.loading = false;
            }
        },
        formatContent(content) {
            return content;
        }
    }
};
