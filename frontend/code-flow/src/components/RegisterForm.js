import axios from 'axios';

export default {
  name: 'BaseRegisterForm',
  data() {
    return {
      // 默认头像
      defaultAvatar: 'https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&s=150',
      // 头像预览
      avatarPreview: '',
      // 上传的头像文件
      avatarFile: null,
      // 表单数据
      form: {
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
      },
      // 错误信息
      error: '',
      // 加载状态
      loading: false
    }
  },
  methods: {
    // 处理头像上传
    handleAvatarUpload(event) {
      const file = event.target.files[0];
      if (file) {
        // 检查文件类型
        if (!file.type.match('image.*')) {
          this.error = '请上传图片文件';
          return;
        }
        
        // 检查文件大小（限制为5MB）
        if (file.size > 5 * 1024 * 1024) {
          this.error = '图片大小不能超过5MB';
          return;
        }
        
        // 保存文件引用
        this.avatarFile = file;
        
        // 创建文件读取器用于预览
        const reader = new FileReader();
        reader.onload = (e) => {
          this.avatarPreview = e.target.result;
        };
        reader.readAsDataURL(file);
        
        // 清除错误信息
        this.error = '';
      }
    },
    
    // 处理注册
    async handleRegister() {
      // 表单验证
      if (!this.form.username || !this.form.email || !this.form.password || !this.form.confirmPassword) {
        this.error = '请填写所有必填字段';
        return;
      }
      
      // 邮箱格式验证
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.form.email)) {
        this.error = '请输入有效的邮箱地址';
        return;
      }
      
      // 密码一致性验证
      if (this.form.password !== this.form.confirmPassword) {
        this.error = '两次输入的密码不一致';
        return;
      }
      
      // 密码强度验证
      if (this.form.password.length < 6) {
        this.error = '密码长度至少为6位';
        return;
      }
      
      // 清除错误信息
      this.error = '';
      // 设置加载状态
      this.loading = true;
      
      try {
        // 创建FormData对象
        const formData = new FormData();
        formData.append('username', this.form.username);
        formData.append('email', this.form.email);
        formData.append('password', this.form.password);
        
        // 如果有上传的头像文件，添加到FormData中
        if (this.avatarFile) {
          formData.append('avatar', this.avatarFile);
          console.log('包含头像文件的注册请求');
        } else {
          console.log('不包含头像文件的注册请求，将使用默认头像');
        }
        
        // 调用后端注册API
        const response = await axios.post('http://localhost:8080/api/users/register', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        
        console.log('注册成功:', response.data);
        
        // 注册成功后自动登录
        const userData = {
          id: response.data.id,
          username: response.data.username,
          email: response.data.email,
          avatar: response.data.avatar || this.defaultAvatar,
          loggedIn: true,
          token: response.data.token || ''
        };
        
        localStorage.setItem('user', JSON.stringify(userData));
        
        // 跳转到博客主页
        this.$router.push('/');
      } catch (error) {
        console.error('注册失败:', error);
        // 更详细的错误处理
        if (error.response) {
          console.error('错误响应数据:', error.response.data);
          console.error('错误状态码:', error.response.status);
          this.error = error.response.data || '注册失败，请稍后重试';
        } else if (error.request) {
          console.error('请求已发送但没有收到响应:', error.request);
          this.error = '网络连接失败，请检查网络设置';
        } else {
          console.error('请求设置出错:', error.message);
          this.error = '注册失败，请稍后重试';
        }
      } finally {
        // 重置加载状态
        this.loading = false;
      }
    }
  }
}