<template>
  <div class="create-article">
    <h1>写文章</h1>
    
    <form @submit.prevent="handleSubmit" class="article-form">
      <div class="form-group">
        <label for="title">文章标题</label>
        <input 
          type="text" 
          id="title" 
          v-model="articleForm.title" 
          placeholder="请输入文章标题"
          required
        >
      </div>
      
      <div class="form-group">
        <label for="category">文章分类</label>
        <select id="category" v-model="articleForm.category" required>
          <option value="">请选择分类</option>
          <option value="技术">技术</option>
          <option value="生活">生活</option>
          <option value="学习">学习</option>
          <option value="工作">工作</option>
        </select>
      </div>
      
      <div class="form-group">
        <label for="content">文章内容</label>
        <textarea 
          id="content" 
          v-model="articleForm.content" 
          placeholder="请输入文章内容"
          rows="15"
          required
        ></textarea>
      </div>
      
      <div class="form-actions">
        <button type="button" class="btn-cancel" @click="handleCancel">取消</button>
        <button type="submit" class="btn-submit" :disabled="submitting">{{ submitting ? '提交中...' : '发布文章' }}</button>
      </div>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'CreateArticle',
  data() {
    return {
      articleForm: {
        title: '',
        category: '',
        content: ''
      },
      submitting: false,
      error: ''
    }
  },
  mounted() {
    // 检查用户是否登录
    const userData = localStorage.getItem('user');
    if (!userData) {
      // 如果未登录，重定向到登录页
      this.$router.push('/login');
    }
  },
  methods: {
    async handleSubmit() {
      this.submitting = true;
      this.error = '';
      
      try {
        const userData = localStorage.getItem('user');
        const user = JSON.parse(userData);
        
        // 准备提交数据
        const articleData = {
          title: this.articleForm.title,
          category: this.articleForm.category,
          content: this.articleForm.content,
          publishDate: new Date(),
          authorId: user.id
        };
        
        // 调用后端API提交文章
        await axios.post('http://localhost:8080/api/articles', articleData);
        
        // 提交成功，重定向到文章列表页
        alert('文章发布成功！');
        this.$router.push('/articles');
      } catch (error) {
        console.error('发布文章失败:', error);
        this.error = '发布文章失败，请稍后重试';
      } finally {
        this.submitting = false;
      }
    },
    handleCancel() {
      // 询问用户是否放弃编辑
      if (this.articleForm.title || this.articleForm.content) {
        if (confirm('确定要放弃编辑吗？已输入的内容将会丢失。')) {
          this.$router.push('/articles');
        }
      } else {
        this.$router.push('/articles');
      }
    }
  }
}
</script>

<style scoped>
.create-article {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.create-article h1 {
  font-size: 2.5rem;
  margin-bottom: 2rem;
  text-align: center;
  color: #333;
}

.article-form {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #333;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-group textarea {
  resize: vertical;
  min-height: 300px;
  font-family: inherit;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.btn-cancel,
.btn-submit {
  padding: 0.8rem 2rem;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-cancel {
  background-color: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
}

.btn-cancel:hover {
  background-color: #e0e0e0;
}

.btn-submit {
  background-color: #667eea;
  color: white;
  border: none;
}

.btn-submit:hover:not(:disabled) {
  background-color: #5a67d8;
}

.btn-submit:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error-message {
  background-color: #ffebee;
  color: #c62828;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}
</style>