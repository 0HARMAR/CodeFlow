import axios from 'axios';

export default {
  name: 'BaseLoginForm',
  data() {
    return {
      // 表单数据
      form: {
        username: '',
        email: '',
        password: '',
        remember: false
      },
      // 错误信息
      error: '',
      // 加载状态
      loading: false
    }
  },
  methods: {
    
    // 处理登录
    async handleLogin() {
      // 表单验证
      if (!this.form.username || !this.form.email || !this.form.password) {
        this.error = '请填写所有必填字段';
        return;
      }
      
      // 邮箱格式验证
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.form.email)) {
        this.error = '请输入有效的邮箱地址';
        return;
      }
      
      // 清除错误信息
      this.error = '';
      // 设置加载状态
      this.loading = true;
      
      try {
        // 调用后端登录API
        const response = await axios.post('http://localhost:8080/api/users/login', {
          username: this.form.username,
          email: this.form.email,
          password: this.form.password
        });
        
        console.log('登录成功:', response.data);
        
        // 存储登录状态
        const userData = {
          id: response.data.id,
          username: response.data.username,
          email: response.data.email,
          avatar: response.data.avatar || 'https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&s=150',
          loggedIn: true,
          remember: this.form.remember,
          token: response.data.token || ''
        };
        
        localStorage.setItem('user', JSON.stringify(userData));
        
        // 如果选择记住我，可以使用localStorage存储更长时间
        if (this.form.remember) {
          localStorage.setItem('rememberedUser', JSON.stringify({
            username: this.form.username,
            email: this.form.email
          }));
        }
        
        // 跳转到博客主页
        this.$router.push('/');
      } catch (error) {
        console.error('登录失败:', error);
        this.error = error.response?.data?.message || '登录失败，请检查用户名和密码';
      } finally {
        // 重置加载状态
        this.loading = false;
      }
    }
  },
  
  // 组件挂载时检查是否有记住的用户信息
  mounted() {
    const rememberedUser = localStorage.getItem('rememberedUser');
    if (rememberedUser) {
      try {
        const userData = JSON.parse(rememberedUser);
        this.form.username = userData.username || '';
        this.form.email = userData.email || '';
        this.form.remember = true;
      } catch (e) {
        console.error('解析记住的用户信息失败:', e);
      }
    }
  }
}