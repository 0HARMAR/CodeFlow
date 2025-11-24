package com.example.codeflow.config;

import com.example.codeflow.model.Article;
import com.example.codeflow.model.User;
import com.example.codeflow.repository.ArticleRepository;
import com.example.codeflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // 初始化用户数据
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("admin");
            user.setEmail("admin@example.com");
            user.setPassword("password"); // 实际应用中应该加密
            user.setAvatar("https://via.placeholder.com/150");
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());
            userRepository.save(user);
        }
        
        // 初始化文章数据
        if (articleRepository.count() == 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            // 创建示例文章
            Article article1 = new Article();
            article1.setTitle("Vue 3 新特性详解");
            article1.setExcerpt("探索 Vue 3 带来的 Composition API、Teleport、Fragments 等新特性...");
            article1.setContent("<h2>Composition API</h2><p>Vue 3 引入了 Composition API，这是一个全新的 API 设计，旨在解决组件逻辑复用和类型推导的问题。</p><p>通过 setup() 函数，我们可以更好地组织组件的逻辑，特别是在处理复杂组件时。</p><h2>Teleport</h2><p>Teleport 允许我们将组件的内容渲染到 DOM 树中的任意位置，这对于创建模态框、通知等组件非常有用。</p><h2>Fragments</h2><p>Vue 3 原生支持多根节点组件，不再需要使用包裹元素。</p><h2>性能优化</h2><p>Vue 3 通过重写虚拟 DOM 和 diff 算法，显著提升了渲染性能。</p>");
            article1.setPublishDate(dateFormat.parse("2023-10-15"));
            article1.setCategory("前端开发");
            article1.setCreatedAt(new Date());
            article1.setUpdatedAt(new Date());
            
            Article article2 = new Article();
            article2.setTitle("前端性能优化实战");
            article2.setExcerpt("如何通过懒加载、缓存策略和代码分割提升你的应用性能...");
            article2.setContent("<h2>懒加载</h2><p>懒加载是一种延迟加载技术，只在需要时才加载资源。这可以显著减少初始加载时间。</p><h2>代码分割</h2><p>将代码分割成更小的块，只加载当前页面所需的代码。</p><h2>缓存策略</h2><p>合理使用浏览器缓存、Service Worker 等技术，可以减少重复请求。</p>");
            article2.setPublishDate(dateFormat.parse("2023-10-10"));
            article2.setCategory("性能优化");
            article2.setCreatedAt(new Date());
            article2.setUpdatedAt(new Date());
            
            Article article3 = new Article();
            article3.setTitle("现代 CSS 技巧大全");
            article3.setExcerpt("掌握 Grid、Flexbox、CSS 变量等现代 CSS 技术，让你的布局更灵活...");
            article3.setContent("<h2>Grid 布局</h2><p>CSS Grid 是一个二维布局系统，让我们可以轻松创建复杂的布局。</p><h2>Flexbox</h2><p>Flexbox 是一个一维布局系统，特别适合于排列元素。</p><h2>CSS 变量</h2><p>CSS 变量允许我们定义可重用的值，使样式更易于维护。</p>");
            article3.setPublishDate(dateFormat.parse("2023-10-05"));
            article3.setCategory("CSS");
            article3.setCreatedAt(new Date());
            article3.setUpdatedAt(new Date());
            
            // 保存文章
            articleRepository.save(article1);
            articleRepository.save(article2);
            articleRepository.save(article3);
        }
    }
}