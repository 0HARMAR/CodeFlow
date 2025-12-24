// CreateArticle.logic.js
import { Editor } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import { useArticleStore } from "@/stores/article";

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
            this.submitting = true
            this.error = ''

            const articleStore = useArticleStore()

            try {
                await articleStore.createArticle(this.articleForm)
                alert('article publish success')
                this.$router.push('/articles')
            } catch(e) {
                console.error(e)
                this.error = articleStore.error
            } finally {
                this.submitting = false
            }
        }
    }
};
