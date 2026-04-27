<template>
  <div class="user-center">
    <!-- 顶部个人信息区 -->
    <section class="profile">
      <img
          :src="avatarUrl || user.avatar"
          alt="avatar"
          class="avatar"
      />
      <div class="info">
        <h2 class="username">{{ user.username }}</h2>
        <!-- bio 显示 / 编辑切换 -->
        <div class="bio-wrapper">
          <p v-if="!editingBio">{{ user.bio }}</p>
          <input v-else v-model="newBio" placeholder="编辑你的简介" />
          <button @click="toggleEditBio">
            {{ editingBio ? '保存' : '编辑 bio' }}
          </button>
        </div>
      </div>
    </section>

    <!-- 功能区域 -->
    <section class="actions">
      <button @click="getUserArticles">我的文章</button>
      <button @click="goTo('comments')">我的评论</button>
      <button @click="goTo('settings')">账号设置</button>
    </section>

    <!-- 子路由 / 子模块出口 -->
    <section class="content">
      <div v-if="articles.length === 0">你还没有文章 📝</div>
      <ul v-else class="article-list">
        <li v-for="article in articles" :key="article.id" class="article-item">
          <h3>{{ article.title }}</h3>
          <p>{{ article.excerpt }}</p>
          <small>发布日期：{{ article.createdAt }}</small>
        </li>
      </ul>
    </section>

  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import axios from "@/utils/axios";
import {useArticleStore} from "@/stores/article";
import {useUserStore} from "@/stores/user";

const articleStore = useArticleStore()
const UserStore = useUserStore()

const avatarUrl = ref('')  // 存储 blob URL
const articles = ref([])
const newBio = ref('')
const editingBio = ref(false)

// 先用假数据占位，后面接后端 API
const user = reactive({
  id: -1,
  username: 'CodeFlow User',
  bio: '这个人很懒，还没有写简介',
  avatar: 'https://via.placeholder.com/120'
})

onMounted(async () => {
  try {
    // 1. 获取用户信息
    const me = await axios.get("http://localhost:8080/api/users/me")
    user.id = me.data.id
    user.username = me.data.username
    user.password = me.data.password
    user.bio = me.data.bio
    user.avatar = me.data.avatar
    user.email = me.data.email

    const avatarPath = me.data.avatar

    // 2. 获取头像 Blob
    const response = await axios.get(
        `http://localhost:8080${avatarPath}`,
        { responseType: "blob" }
    )

    // 3. 创建 blob URL
    const blob = new Blob([response.data], { type: response.headers['content-type'] || 'image/jpeg' })
    avatarUrl.value = URL.createObjectURL(blob)

  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
})

async function toggleEditBio() {
  if (editingBio.value) {
    // 保存 bio
    user.bio = newBio.value;
    await UserStore.updateUser(user)
  } else {
    // 编辑前把当前 bio 赋值给输入框
    console.log('保存前的 bio:', user.bio);
    newBio.value = user.bio;
  }
  editingBio.value = !editingBio.value;
}

async function getUserArticles() {
  articles.value = await articleStore.findArticlesByOwnerId(user.id)
}

</script>

<style scoped>
.user-center {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.profile {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.avatar {
  width: 96px;
  height: 96px;
  border-radius: 50%;
}

.info .username {
  margin: 0;
}

.actions {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.actions button {
  padding: 0.5rem 1rem;
  cursor: pointer;
}

.content {
  min-height: 200px;
}
</style>