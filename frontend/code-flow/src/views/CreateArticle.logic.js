// CreateArticle.logic.js
import { Editor } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import { useArticleStore } from "@/stores/article";
import {toRaw} from "vue";

export default {
    name: 'CreateArticle',
    data() {
        return {
            editor: null,
            articleForm: {
                title: '',
                category: '',
                content: '',
            },
            submitting: false,
            error: '',
            aiLoading: false,
            tagInput: '',
            tags: []
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

    methods: {
        checkComma(e) {
            if (e.key === ',') {
                e.preventDefault();   // 只阻止逗号
                this.addTag();
            }
        },

        addTag() {
            const tag = this.tagInput.trim();
            if (!tag) return;

            if (!this.tags.includes(tag)) {
                this.tags.push(tag);
            }
            this.tagInput = '';
        },

        removeTag(index) {
            this.tags.splice(index, 1);
        },

        handleCancel() {
            // 重置表单
            this.articleForm = { title: '', category: '', content: '', tags: [] };
            this.tagInput = '';
            if (this.editor) this.editor.commands.clearContent();
        },
        async handleSubmit() {
            this.submitting = true
            this.error = ''

            const articleStore = useArticleStore()

            try {
                console.log(toRaw(this.tags))
                await articleStore.createArticle(this.articleForm, toRaw(this.tags))
                alert('article publish success')
                this.$router.push('/articles')
            } catch(e) {
                console.error(e)
                this.error = articleStore.error
            } finally {
                this.submitting = false
            }
        },

        async handleAiContinue() {
            if (!this.editor) return

            this.aiLoading = true

            try {
                const content = this.editor.getText()

                const response = await fetch('http://localhost:3000/chat', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        messages: [
                            {
                                role: 'system',
                                content: '你是一个专业的中文博客写作助手，帮助用户自然地续写文章内容。',
                            },
                            {
                                role: 'user',
                                content: `
文章标题：《${this.articleForm.title}》
文章分类：${this.articleForm.category}

这是我已经写的内容：
"""
${content}
"""

请基于以上内容，继续往下写一小段（2~4 句话），保持使用的语言与用户的相同，直接输出正文，不要解释。
            `.trim(),
                            },
                        ],
                    }),
                })

                const data = await response.json()

                if (data.reply) {
                    this.editor
                        .chain()
                        .focus()
                        .insertContent(data.reply)
                        .run()
                }
            } catch (err) {
                console.error('AI 续写失败', err)
                alert('AI 生成失败，请稍后重试')
            } finally {
                this.aiLoading = false
            }
        }

    }
};
