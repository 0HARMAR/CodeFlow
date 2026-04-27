// src/views/ArticleDetail.js
import {useArticleReadTracker} from "@/views/useArticleReadTracker";
import { useArticleStore } from "@/stores/article";
import { useCommentStore } from "@/stores/comment";
import { useUserStore } from "@/stores/user";
import {computed, onMounted, ref, watch, onBeforeUnmount, onUnmounted} from "vue";
import CommentItem from "@/views/CommentItem.vue";
import {UserAction} from "@/enums/UserAction";
import axios from "@/utils/axios";

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
        const showExplainPopup = ref(false);
        const explainText = ref("");
        const userStore = useUserStore()
        const author = ref(null)
        const commentAuthors = ref({})

        const article = ref(null)

        // 新增响应式数据
        const showExplainBtn = ref(false)
        const selectedText = ref("")
        const btnPosition = ref({
            top: 0,
            left: 0,
        })

        const contentRef = ref(null)
        const sessionSeconds = ref(0)
        useArticleReadTracker(
            () => article.value?.id,
            contentRef,
            (session) => {
                console.log('[outside] 一次阅读（回调）:', session)
                sessionSeconds.value = session
            }
        )
        const readTime = ref(0)
        const lastReadAt = ref('')

        let currentAction = UserAction.IMPRESSION

        const updateAction = (action) => {
            if (action.level >= currentAction.level) currentAction = action
            console.log(currentAction.level)
        }

        // 处理文本选择的方法
        const handleTextSelect = () => {
            const selection = window.getSelection();
            const text = selection.toString().trim();

            if (!text) {
                showExplainBtn.value = false;
                return;
            }

            // 只在文章内容区域生效
            const articleContent = document.querySelector(".article-content");
            if (!articleContent || !articleContent.contains(selection.anchorNode)) {
                showExplainBtn.value = false;
                return;
            }

            const range = selection.getRangeAt(0);
            const rect = range.getBoundingClientRect();

            selectedText.value = text;
            showExplainBtn.value = true;

            // 按钮位置：选区下方偏右
            btnPosition.value = {
                top: rect.bottom + window.scrollY + 6,
                left: rect.right + window.scrollX - 60,
            };
        }

        // 处理解释的方法
        const handleExplain = async () => {
            const selected = selectedText.value;
            if (!selected) return;

            const content = article.value.content;

            showExplainBtn.value = false;
            window.getSelection().removeAllRanges();

            // 先显示 loading
            showExplainPopup.value = true;
            explainText.value = "AI 正在思考中…";

            try {
                const response = await fetch("http://localhost:3000/chat", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        messages: [
                            {
                                role: "system",
                                content: "你是一名博客 AI 助手，负责解释文章内容。",
                            },
                            {
                                role: "user",
                                content: `
下面是一篇博客文章内容，请你结合上下文，
解释用户选中的部分。

要求：
- 只解释选中的内容
- 结合文章上下文
- 用通俗、准确的语言
- 不要复述原文

【文章内容】
${content}

【用户选中的内容】
${selected}
            `.trim(),
                            },
                        ],
                    }),
                });

                const data = await response.json();
                explainText.value = data.reply;
                console.log(data.reply)
            } catch (err) {
                explainText.value = "解释失败，请稍后重试。";
                console.error(err);
            }
        };

        const closeExplain = () => {
            showExplainPopup.value = false;
        };

        // 监听作者ID变化
        watch(
            () => article.value?.authorId,
            async (authorId) => {
                if (authorId) {
                    author.value = await userStore.findUserById(authorId)
                }
            },
            { immediate: true }
        )

        // 监听评论变化
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

        // 组件挂载时添加事件监听器
        onMounted(async () => {
            document.addEventListener("mouseup", handleTextSelect);

            article.value = await store.fetchArticleById(Number(props.id))

            if (article.value?.id) {
                await commentStore.fetchComments(article.value.id)
                comments.value = commentStore.comments
            }

            if (article.value?.authorId) {
                author.value = await userStore.findUserById(article.value.authorId)
            }

            const res = await axios.get(`http://localhost:8080/api/read?articleId=${article.value.id}`)
            readTime.value = res.data.totalSeconds
            lastReadAt.value = res.data.lastReadAt

            updateAction(UserAction.CLICK)
        })

        onUnmounted(() => {
            if (sessionSeconds.value >= 30) {
                updateAction(UserAction.READ)
            } else if (readTime.value >= 60) {
                const now = new Date();
                const standardizedDate = lastReadAt.value.replace(' ', 'T') + '.000Z';
                const dateObj = new Date(standardizedDate);

                const diffDay = Math.floor((now - dateObj) / (1000 * 60 * 60 * 24))
                if (diffDay >= 1) {
                    updateAction(UserAction.READ)
                }
            }

            const raw = localStorage.getItem('actions')
            const actions = raw ? JSON.parse(raw) : []
            const actionsMap = new Map(actions)
            actionsMap.set(String(article.value.id), currentAction)
            localStorage.setItem('actions', JSON.stringify(Array.from(actionsMap)))
        })

        // 组件卸载前移除事件监听器
        onBeforeUnmount(() => {
            document.removeEventListener("mouseup", handleTextSelect);
        })

        const loading = computed(() => store.loading)
        const error = computed(() => store.error)

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
            updateAction(UserAction.FAVORITE)
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
            getCommentAuthorName,
            // 文本选择功能相关
            showExplainBtn,
            selectedText,
            btnPosition,
            handleTextSelect,
            handleExplain,
            closeExplain,
            showExplainPopup,
            explainText,
            contentRef,
            readTime
        }
    }
};
