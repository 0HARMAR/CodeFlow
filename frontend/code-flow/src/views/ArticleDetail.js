// src/views/ArticleDetail.js
import { useArticleStore } from "@/stores/article";
import { useCommentStore } from "@/stores/comment";
import { useUserStore } from "@/stores/user";
import { computed, onMounted, ref, watch } from "vue";
import CommentItem from "@/views/CommentItem.vue";

export default {
    name: 'ArticleDetail',
    components: {CommentItem},
    props: {
        id: {
            type: String,
            required: true
        }
    },

    setup(props) {
        const store = useArticleStore();
        const commentStore = useCommentStore();
        const comments = computed(() => commentStore.comments)
        const newComment = ref('')
        const replyingTo = ref(null) // 当前回复的评论
        const userStore = useUserStore()
        const author = ref(null)
        const commentAuthors = ref({})

        const article = ref(null)

        watch(
            () => article.value?.authorId,
            async (authorId) => {
                if (authorId) {
                    author.value = await userStore.findUserById(authorId)
                }
            },
            { immediate: true }
        )

        watch(
            () => comments.value,
            async (list) => {
                if (!list || !list.length) return

                const userIds = [...new Set(list.map(c => c.userId))]

                for (const userId of userIds) {
                    if (!commentAuthors.value[userId]) {
                        const user = await userStore.findUserById(userId)
                        commentAuthors.value[userId] = user
                    }
                }
            },
            { immediate: true }
        )

        const loading = computed(() => store.loading)
        const error = computed(() => store.error)

        onMounted(async () => {
            article.value = await store.fetchArticleById(Number(props.id))

            if (article.value?.id) {
                await commentStore.fetchComments(article.value.id)
                comments.value = commentStore.comments
            }

            if (article.value?.authorId) {
                author.value = await userStore.findUserById(article.value.authorId)
            }
        })

        const authorName = computed(() => {
            return author.value?.username || 'unknown'
        })

        const getCommentAuthorName = (userId) => {
            return commentAuthors.value[userId]?.username || 'unknown'
        }

        const toggleLike = async () => {
            if (article.value) {
                await store.toggleLike(article.value)
            }
        }

        const onReply = (comment) => {
            replyingTo.value = comment
        }

        const submitComment = async () => {
            if (!newComment.value.trim()) {
                alert('comment cannot be empty')
                return
            }

            const userData = localStorage.getItem('user')
            const user = JSON.parse(userData)

            const payload = {
                articleId: article.value.id,
                userId: user.id,
                parentId: replyingTo.value ? replyingTo.value.id : null,
                replyToUserId: replyingTo.value ? replyingTo.value.userId : null,
                content: newComment.value
            }

            try {
                await commentStore.addComment(payload)
                newComment.value = ''
                replyingTo.value = null
            } catch (e) {
                alert('failed to publish comment')
            }
        }

        return {
            article,
            newComment,
            loading,
            error,
            toggleLike,
            submitComment,
            onReply,
            comments,
            replyingTo,
            authorName,
            getCommentAuthorName
        }
    }
};
