// src/views/ArticleDetail.js
import { useArticleStore } from "@/stores/article";
import {computed, onMounted} from "vue";

export default {
    name: 'ArticleDetail',
    props: {
        id: {
            type: String,
            required: true
        }
    },

    setup(props) {
        const store = useArticleStore();

        const article = computed(() =>
            store.getById(Number(props.id)))

        const loading = computed(() => store.loading)
        const error = computed(() => store.error)

        onMounted(async () => {
            if (!article.value) {
                await store.fetchArticleById(Number(props.id))
            }
        })

        const toggleLike = async () => {
            if (article.value) {
                await store.toggleLike(article.value)
            }
        }

        return {
            article,
            loading,
            error,
            toggleLike
        }
    }
};
