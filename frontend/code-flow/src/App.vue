<template>
  <div id="app">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="container">
        <router-link to="/" class="logo">
          <span>CodeFlow</span>
        </router-link>
        <div class="nav-links">
          <!-- 搜索栏 -->
          <SearchBar />
          <router-link to="/" class="nav-link home-page-btn">首页</router-link>

          <router-link v-if="user" to="/creation-center" class="nav-link create-article-btn">创作中心</router-link>
          <!-- 黑夜模式按钮 -->
          <button class="dark-toggle-btn" @click="toggleDarkMode">
            {{ isDark ? '☀️ 日间' : '🌙 夜间' }}
          </button>

          <router-link to="/about" class="nav-link about">关于</router-link>
          
          <!-- 登录状态显示 -->
          <div v-if="user" class="user-menu-container">
            <div class="user-menu-trigger" @click="toggleUserMenu">
              <img alt="用户头像" :src="avatarSrc" class="user-avatar">
              <span class="user-name">{{ user.username }}</span>
              <span class="menu-arrow" :class="{ 'rotate': showUserMenu }">▼</span>
            </div>
            <!-- 用户下拉菜单 -->
            <div v-if="showUserMenu" class="user-dropdown-menu">
              <router-link
                  to="/user-center"
                  class="menu-item"
                  @click="showUserMenu = false"
              >
                个人中心
              </router-link>
              <a href="#" class="menu-item">我的文章</a>
              <a href="#" class="menu-item">设置</a>
              <div class="menu-divider"></div>
              <a href="#" class="menu-item logout" @click="handleLogout">退出登录</a>
              <a href="#" class="menu-item delete-account" @click="handleDeleteAccount">注销账号</a>
            </div>
          </div>
          
          <!-- 未登录状态 -->
          <router-link v-else to="/login" class="nav-link login-btn">登录</router-link>
        </div>
      </div>
    </nav>
    
    <!-- 路由视图 -->
    <main class="main-content">
      <router-view/>
    </main>
    
    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <p>&copy; {{ new Date().getFullYear() }} 我的博客系统 | 使用 Vue 3 构建</p>
      </div>
    </footer>

    <!-- 全局助手形象 -->
    <NijikaChat />
    <NazunaChat />
  </div>
</template>

<script>
import axios from '@/utils/axios';
import {useAuthStore} from "@/stores/auth";
import SearchBar from "@/views/SearchBar.vue";
import NijikaChat from "@/components/NijikaChat.vue";
import NazunaChat from "@/components/NazunaChat.vue";
export default {
  name: 'App',
  components: {SearchBar, NijikaChat, NazunaChat},
  data() {
    return {
      user: null,
      avatarSrc: '',
      showUserMenu: false,
      routeChangeHandler: null,
      isDark: false
    }
  },
  setup() {
    const authStore = useAuthStore();
    return { authStore };
  },
  mounted() {
    // 检查用户登录状态
    this.checkUserLoginStatus();
    // 监听点击页面其他区域关闭下拉菜单
    document.addEventListener('click', this.handleDocumentClick);
    // 监听路由变化，确保登录后立即更新用户信息
    this.routeChangeHandler = this.$router.afterEach(this.handleRouteChange);

    // 读取黑夜模式
    const dark = localStorage.getItem('dark-mode');
    if (dark === 'true') {
      this.enableDarkMode();
    }
  },
  beforeUnmount() {
    // 移除事件监听器
    document.removeEventListener('click', this.handleDocumentClick);
    // 移除路由监听器
    if (this.routeChangeHandler) {
      this.routeChangeHandler();
    }
  },
  methods: {
    toggleDarkMode() {
      this.isDark ? this.disableDarkMode() : this.enableDarkMode();
    },

    enableDarkMode() {
      this.isDark = true;
      document.documentElement.classList.add('dark');
      localStorage.setItem('dark-mode', 'true');
    },

    disableDarkMode() {
      this.isDark = false;
      document.documentElement.classList.remove('dark');
      localStorage.setItem('dark-mode', 'false');
    },
    // 检查用户登录状态
    checkUserLoginStatus() {
      const userData = localStorage.getItem('user');
      if (userData) {
        try {
          const localUser = JSON.parse(userData);
          if (localUser.loggedIn && localUser.id) {
            // 从后端获取最新的用户信息
            axios.get(`http://localhost:8080/api/users/${localUser.id}`)
              .then(response => {
                const user = response.data;
                // 确保头像URL存在且有效
                if (!user.avatar || user.avatar === '') {
                  user.avatar = 'https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&s=150';
                  this.avatarSrc = user.avatar;
                } else if (!user.avatar.startsWith('http')) {
                  // 如果头像URL不是完整的http地址，添加服务器地址前缀
                  const token = this.authStore.token;
                  fetch(`http://localhost:8080${user.avatar}`, {
                    headers: {
                      Authorization: `Bearer ${token}`
                    }
                  })
                      .then(res => res.blob())
                      .then(blob => {
                        this.avatarSrc = URL.createObjectURL(blob);
                      })
                } else {
                  this.avatarSrc = user.avatar;
                }
                // 合并本地和远程的用户信息
                user.loggedIn = true;
                user.remember = localUser.remember;

                // 更新localStorage和当前状态
                localStorage.setItem('user', JSON.stringify(user));
                this.user = user;
              })
              .catch(error => {
                console.error('获取用户信息失败:', error);
                // 如果获取失败，使用本地存储的信息作为后备
                if (!localUser.avatar || localUser.avatar === '') {
                  localUser.avatar = 'https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&s=150';
                }
                this.avatarSrc = localUser.avatar;
                this.user = localUser;
              });
          }
        } catch (e) {
          console.error('解析用户数据失败:', e);
        }
      }
    },
    
    // 处理路由变化，每次路由切换时检查登录状态
    handleRouteChange() {
      this.checkUserLoginStatus();
    },
    
    // 切换用户菜单显示状态
    toggleUserMenu(event) {
      // 阻止事件冒泡，防止点击菜单时关闭
      if (event) {
        event.stopPropagation();
      }
      this.showUserMenu = !this.showUserMenu;
    },
    
    // 处理点击文档其他区域
    handleDocumentClick(event) {
      // 检查点击是否在用户菜单容器内
      const userMenuContainer = document.querySelector('.user-menu-container');
      if (userMenuContainer && !userMenuContainer.contains(event.target)) {
        this.showUserMenu = false;
      }
    },
    
    // 处理退出登录
    handleLogout(event) {
      event.preventDefault();
      // 清除localStorage中的用户信息
      localStorage.removeItem('user');
      // 重置用户状态
      this.user = null;
      this.avatarSrc = '';
      this.showUserMenu = false;
      // 刷新页面或跳转到登录页
      this.$router.push('/');
    },
    
    // 处理注销账号（删除数据库中的账号）
    handleDeleteAccount(event) {
      event.preventDefault();
      // 显示确认对话框
      if (confirm('确定要注销账号吗？此操作将删除您的所有数据，且不可恢复！')) {
        // 获取当前用户信息
        const userData = localStorage.getItem('user');
        console.log('用户数据:', userData);
        if (userData) {
          const user = JSON.parse(userData);
          console.log('解析后的用户ID:', user.id);
          // 向后端发送删除账号的请求
          axios.post('http://localhost:8080/api/users/delete-account', {
            userId: user.id
          })
          .then(response => {
            console.log('删除账号响应:', response);
            // 清除localStorage中的用户信息
            localStorage.removeItem('user');
            // 重置用户状态
            this.user = null;
            this.showUserMenu = false;
            // 提示用户账号已注销
            alert('账号已成功注销');
            // 跳转到登录页
            this.$router.push('/login');
          })
          .catch(error => {
            console.error('注销账号失败，完整错误信息:', error);
            if (error.response) {
              // 服务器返回了错误响应
              console.error('错误状态码:', error.response.status);
              console.error('错误响应体:', error.response.data);
              alert(`注销账号失败: ${error.response.data || '服务器错误'}`);
            } else if (error.request) {
              // 请求已发出但没有收到响应
              console.error('没有收到响应:', error.request);
              alert('无法连接到服务器，请检查网络连接');
            } else {
              // 请求设置时发生错误
              console.error('请求错误:', error.message);
              alert('请求错误: ' + error.message);
            }
          });
        }
      }
    }
  }
}
</script>

<style src="./App.css"></style>
<style>
@import '@/styles/nijika-chat.css';
@import '@/styles/nazuna-chat.css';
</style>
