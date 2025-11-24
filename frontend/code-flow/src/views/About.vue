<template>
  <div class="about">
    <h1>关于博客</h1>
    <div class="about-content">
      <p>欢迎访问我的个人博客系统！这里是我分享技术见解、学习心得和生活感悟的地方。</p>
      
      <h2>博客简介</h2>
      <p>本博客系统是基于 Vue 3 + Vue Router 构建的现代化前端应用。我希望通过这个平台，能够记录我的成长历程，同时也为其他开发者提供一些有价值的信息和资源。</p>
      
      <h2>内容主题</h2>
      <ul class="topics">
        <li>前端开发（Vue、React、JavaScript 等）</li>
        <li>后端技术（Node.js、数据库等）</li>
        <li>DevOps 和工具链</li>
        <li>性能优化和最佳实践</li>
        <li>技术学习和职业发展</li>
      </ul>
      
      <h2>联系我</h2>
      <p>如果您有任何问题或建议，欢迎随时联系我！</p>
      <div class="contact-info">
        <p>邮箱：contact@example.com</p>
        <p>GitHub：https://github.com/example</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AboutPage'
}
</script>

<style scoped>
.about {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.about h1 {
  font-size: 2.5rem;
  margin-bottom: 2rem;
  text-align: center;
  color: #333;
}

.about-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.about-content h2 {
  font-size: 1.8rem;
  margin: 2rem 0 1rem;
  color: #333;
  border-bottom: 2px solid #667eea;
  padding-bottom: 0.5rem;
}

.about-content p {
  line-height: 1.8;
  color: #666;
  margin-bottom: 1rem;
}

.topics {
  list-style: none;
  padding: 0;
  margin-bottom: 1.5rem;
}

.topics li {
  padding: 0.8rem 0;
  border-bottom: 1px solid #f0f0f0;
  color: #666;
  position: relative;
  padding-left: 1.5rem;
}

.topics li:before {
  content: '•';
  position: absolute;
  left: 0;
  color: #667eea;
  font-size: 1.5rem;
}

.contact-info p {
  margin-bottom: 0.5rem;
}

.contact-info a {
  color: #667eea;
  text-decoration: none;
}

.contact-info a:hover {
  text-decoration: underline;
}
</style>