// CreateArticle.logic.js
import axios from 'axios';
import { Editor } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'

export default {
    name: 'CreateArticle',
    data() {
        return {
            editor: null,
            articleForm: {
                title: '',
                category: '',
                content: ''
            },
            submitting: false,
            error: ''
        };
    },

    mounted() {
        this.editor = new Editor({
            extensions: [StarterKit],
            content: '<p>now wirte your article</p>',
            onUpdate: ({ editor }) => {
                this.articleForm.content = editor.getHTML();
            }
        })

        const userData = localStorage.getItem('user');
        if (!userData) {
            this.$router.push('/login');
        }
    },

    beforeUnmount() {
        if (this.$router.push('/login'));
    },

    methods: {
        async handleSubmit() {
            this.submitting = true;
            this.error = '';

            try {
                const userData = localStorage.getItem('user');
                const user = JSON.parse(userData);

                const articleData = {
                    title: this.articleForm.title,
                    category: this.articleForm.category,
                    content: this.articleForm.content,
                    publishDate: new Date(),
                    authorId: user.id
                };

                await axios.post('http://localhost:8080/api/articles', articleData);

                alert('文章发布成功！');
                this.$router.push('/articles');
            } catch (error) {
                console.error('发布文章失败:', error);
                this.error = '发布文章失败，请稍后重试';
            } finally {
                this.submitting = false;
            }
        },

        handleCancel() {
            if (this.articleForm.title || this.articleForm.content) {
                if (confirm('确定要放弃编辑吗？已输入的内容将会丢失。')) {
                    this.$router.push('/articles');
                }
            } else {
                this.$router.push('/articles');
            }
        }
    }
};
