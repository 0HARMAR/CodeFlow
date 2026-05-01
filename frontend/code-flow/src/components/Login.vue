<template>
    <div class="login-container">
        <!-- 装饰背景 -->
        <div class="bg-shapes">
            <div class="shape shape-1"></div>
            <div class="shape shape-2"></div>
            <div class="shape shape-3"></div>
        </div>

        <div class="login-card">
            <!-- 头部标题 -->
            <div class="login-header">
                <div class="login-icon-wrapper">
                    <svg class="login-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                        <circle cx="12" cy="7" r="4"></circle>
                    </svg>
                </div>
                <h2 class="login-title">用户登录</h2>
                <p class="login-subtitle">欢迎回来，请登录您的账号</p>
            </div>

            <!-- 登录表单 -->
            <form @submit.prevent="handleLogin" class="login-form">
                <div class="form-group">
                    <label for="username" class="form-label">用户名</label>
                    <div class="input-wrapper">
                        <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                        <input
                            type="text"
                            id="username"
                            v-model="form.username"
                            placeholder="请输入用户名"
                            class="form-input"
                            required
                        >
                    </div>
                </div>
                <div class="form-group">
                    <label for="email" class="form-label">邮箱</label>
                    <div class="input-wrapper">
                        <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <rect x="2" y="4" width="20" height="16" rx="2"></rect>
                            <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"></path>
                        </svg>
                        <input
                            type="email"
                            id="email"
                            v-model="form.email"
                            placeholder="请输入邮箱"
                            class="form-input"
                            required
                        >
                    </div>
                </div>
                <div class="form-group">
                    <label for="password" class="form-label">密码</label>
                    <div class="input-wrapper">
                        <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                            <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                        </svg>
                        <input
                            type="password"
                            id="password"
                            v-model="form.password"
                            placeholder="请输入密码"
                            class="form-input"
                            required
                        >
                    </div>
                </div>

                <!-- 记住我和忘记密码 -->
                <div class="form-options">
                    <div class="remember-me">
                        <input type="checkbox" id="remember" v-model="form.remember" class="checkbox-input">
                        <label for="remember" class="checkbox-label">记住我</label>
                    </div>
                    <a href="#" class="forgot-password">忘记密码？</a>
                </div>

                <!-- 错误提示 -->
                <div v-if="error" class="error-message">
                    <svg class="error-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="12" y1="8" x2="12" y2="12"></line>
                        <line x1="12" y1="16" x2="12.01" y2="16"></line>
                    </svg>
                    {{ error }}
                </div>

                <!-- 登录按钮 -->
                <button
                    type="submit"
                    :disabled="loading"
                    class="login-button"
                >
                    <span v-if="!loading">登录</span>
                    <span v-else class="loading-content">
                        <span class="spinner"></span>
                        登录中...
                    </span>
                </button>

                <!-- 注册链接 -->
                <div class="register-link">
                    <span>还没有账号？</span>
                    <router-link to="/register" class="register-btn">立即注册</router-link>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
// 导入LoginForm组件定义
import LoginForm from './LoginForm.js';
// 重命名组件名称以符合Vue多词命名规则
export default {
  ...LoginForm,
  name: 'LoginForm'
}
</script>

<style src="./Login.css" scoped></style>