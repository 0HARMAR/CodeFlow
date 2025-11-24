<template>
    <div class="register-container">
        <div class="register-card">
            <!-- 头部标题 -->
            <div class="register-header">
                <h2 class="register-title">用户注册</h2>
                <p class="register-subtitle">创建新账号，开始您的博客之旅</p>
            </div>
            
            <!-- 头像上传区域 -->
            <div class="avatar-section">
                <div class="avatar-upload">
                    <img :src="avatarPreview || defaultAvatar" alt="用户头像" class="avatar-image">
                    <label class="avatar-upload-label">
                        <input 
                            type="file" 
                            accept="image/*" 
                            @change="handleAvatarUpload"
                            class="avatar-input"
                        >
                        <span class="avatar-upload-icon">📷</span>
                    </label>
                </div>
            </div>

            <!-- 注册表单 -->
            <form @submit.prevent="handleRegister" class="register-form">
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
                        placeholder="请输入密码（至少6位）" 
                        class="form-input"
                        required
                    >
                </div>
                <div class="form-group">
                    <label for="confirmPassword" class="form-label">确认密码</label>
                    <input 
                        type="password" 
                        id="confirmPassword" 
                        v-model="form.confirmPassword" 
                        placeholder="请再次输入密码" 
                        class="form-input"
                        required
                    >
                </div>
                
                <!-- 错误提示 -->
                <div v-if="error" class="error-message">
                    {{ error }}
                </div>
                
                <!-- 注册按钮 -->
                <button 
                    type="submit" 
                    :disabled="loading"
                    class="register-button"
                >
                    {{ loading ? '注册中...' : '注册' }}
                </button>
                
                <!-- 登录链接 -->
                <div class="login-link">
                    <span>已有账号？</span>
                    <router-link to="/login" class="login-btn">立即登录</router-link>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
// 导入RegisterForm组件定义
import RegisterForm from './RegisterForm.js';

// 重命名组件名称以符合Vue多词命名规则
export default {
  ...RegisterForm,
  name: 'UserRegister'
}
</script>

<style scoped>
/* 注册页面样式 */
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 140px); /* 减去导航栏和页脚的高度 */
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 450px;
  text-align: center;
}

.register-header {
  margin-bottom: 30px;
}

.register-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.register-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
}

/* 头像上传样式 */
.avatar-section {
  margin-bottom: 30px;
}

.avatar-upload {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto;
}

.avatar-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #f0f0f0;
}

.avatar-upload-label {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 36px;
  height: 36px;
  background: #667eea;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  transition: background-color 0.3s;
}

.avatar-upload-label:hover {
  background: #764ba2;
}

.avatar-input {
  display: none;
}

.avatar-upload-icon {
  font-size: 16px;
}

/* 表单样式 */
.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  text-align: left;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* 错误信息样式 */
.error-message {
  background-color: #ffebee;
  color: #c62828;
  padding: 12px 16px;
  border-radius: 6px;
  font-size: 14px;
  margin-top: 10px;
}

/* 注册按钮样式 */
.register-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 14px 24px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-top: 10px;
}

.register-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.register-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

/* 登录链接样式 */
.login-link {
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.login-btn {
  color: #667eea;
  font-weight: 600;
  text-decoration: none;
  margin-left: 5px;
  transition: color 0.3s;
}

.login-btn:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-container {
    padding: 20px 10px;
  }
  
  .register-card {
    padding: 30px 20px;
    margin: 0 10px;
  }
  
  .register-title {
    font-size: 24px;
  }
  
  .avatar-upload {
    width: 100px;
    height: 100px;
  }
}
</style>