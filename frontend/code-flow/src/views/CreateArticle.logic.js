// CreateArticle.logic.js
import { ref, reactive, onMounted, onBeforeUnmount, toRaw } from 'vue'
import {useRoute, useRouter} from 'vue-router'
import { Editor } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import { useArticleStore } from '@/stores/article'

export default {
    name: 'CreateArticle',
    setup() {
        // ===== state =====
        const editor = ref(null)

        const articleForm = reactive({
            title: '',
            category: '',
            content: '',
        })

        const submitting = ref(false)
        const error = ref('')
        const aiLoading = ref(false)
        const tagInput = ref('')
        const tags = ref([])

        const router = useRouter()
        const route = useRoute()
        const articleStore = useArticleStore()
        const draftId = Number(route.query.id);

        // ===== lifecycle =====
        onMounted(async () => {
            if (draftId !== -1) {
                let draftData = await articleStore.fetchArticleById(draftId)
                if (draftData) {
                    articleForm.title = draftData.title
                    articleForm.category = draftData.category
                    articleForm.content = draftData.content
                }
            }

            editor.value = new Editor({
                extensions: [StarterKit],
                content: articleForm.content ? articleForm.content : '<p>write your content</p>',
                onUpdate: ({editor}) => {
                    articleForm.content = editor.getHTML()
                },
            })

            const userData = localStorage.getItem('user')
            if (!userData) {
                router.push('/login')
            }
        })

        onBeforeUnmount(() => {
            if (editor.value) {
                editor.value.destroy()
            }
        })

        // ===== methods =====
        const checkComma = (e) => {
            if (e.key === ',') {
                e.preventDefault()
                addTag()
            }
        }

        const addTag = () => {
            const tag = tagInput.value.trim()
            if (!tag) return

            if (!tags.value.includes(tag)) {
                tags.value.push(tag)
            }
            tagInput.value = ''
        }

        const removeTag = (index) => {
            tags.value.splice(index, 1)
        }

        const handleCancel = () => {
            articleForm.title = ''
            articleForm.category = ''
            articleForm.content = ''
            tagInput.value = ''

            if (editor.value) {
                editor.value.commands.clearContent()
            }
        }

        const handleSubmit = async () => {
            submitting.value = true
            error.value = ''

            const articleStore = useArticleStore()

            try {
                console.log(toRaw(tags.value))
                await articleStore.createArticle(articleForm, toRaw(tags.value))
                alert('article publish success')
                router.push('/articles')
            } catch (e) {
                console.error(e)
                error.value = articleStore.error
            } finally {
                submitting.value = false
            }
        }

        const handleAiContinue = async () => {
            if (!editor.value) return

            aiLoading.value = true

            try {
                const content = editor.value.getText()

                const response = await fetch('http://localhost:8080/api/chat', {
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
文章标题：《${articleForm.title}》
文章分类：${articleForm.category}

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
                    editor.value
                        .chain()
                        .focus()
                        .insertContent(data.reply)
                        .run()
                }
            } catch (err) {
                console.error('AI 续写失败', err)
                alert('AI 生成失败，请稍后重试')
            } finally {
                aiLoading.value = false
            }
        }

        const handleSaveDraft = async () => {
            submitting.value = true
            error.value = ''

            const articleStore = useArticleStore()

            try {
                console.log(toRaw(tags.value))
                await articleStore.createArticleDraft(articleForm, toRaw(tags.value))
                alert('article publish success')
                router.push('/articles')
            } catch (e) {
                console.error(e)
                error.value = articleStore.error
            } finally {
                submitting.value = false
            }
        }

        return {
            editor,
            articleForm,
            submitting,
            error,
            aiLoading,
            tagInput,
            tags,
            checkComma,
            addTag,
            removeTag,
            handleCancel,
            handleSubmit,
            handleAiContinue,
            handleSaveDraft,
        }
    },
}
