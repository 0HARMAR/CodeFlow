<template>
    <div class="login-container">
        <div class="login-card">
            <!-- 头部标题 -->
            <div class="login-header">
                <h2 class="login-title">用户登录</h2>
                <p class="login-subtitle">欢迎回来，请登录您的账号</p>
            </div>
            


            <!-- 登录表单 -->
            <form @submit.prevent="handleLogin" class="login-form">
                <div class="form-group">
                    <label for="username" class="form-label">用户名</label>
                    <input 
                        type="text" 
                        id="username" 
                        v-model="form.username" 
                        placeholder="请输入用户名" 
                        class="form-input"
                        required
                    >
                </div>
                <div class="form-group">
                    <label for="email" class="form-label">邮箱</label>
                    <input 
                        type="email" 
                        id="email" 
                        v-model="form.email" 
                        placeholder="请输入邮箱" 
                        class="form-input"
                        required
                    >
                </div>
                <div class="form-group">
                    <label for="password" class="form-label">密码</label>
                    <input 
                        type="password" 
                        id="password" 
                        v-model="form.password" 
                        placeholder="请输入密码" 
                        class="form-input"
                        required
                    >
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
                    {{ error }}
                </div>
                
                <!-- 登录按钮 -->
                <button 
                    type="submit" 
                    :disabled="loading"
                    class="login-button"
                >
                    {{ loading ? '登录中...' : '登录' }}
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