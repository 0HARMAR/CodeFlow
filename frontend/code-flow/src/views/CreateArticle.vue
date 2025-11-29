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
        <div class="editor-toolbar">
          <button @click="editor.chain().focus().toggleBold().run()">B</button>
          <button @click="editor.chain().focus().toggleItalic().run()"><i>I</i></button>
          <button @click="editor.chain().focus().toggleStrike().run()"><s>S</s></button>

          <button @click="editor.chain().focus().toggleHeading({ level: 1 }).run()">H1</button>
          <button @click="editor.chain().focus().toggleHeading({ level: 2 }).run()">H2</button>
          <button @click="editor.chain().focus().toggleHeading({ level: 3 }).run()">H3</button>

          <button @click="editor.chain().focus().toggleBulletList().run()">• 列表</button>
          <button @click="editor.chain().focus().toggleOrderedList().run()">1. 列表</button>

          <button @click="editor.chain().focus().toggleBlockquote().run()">❝ 引用</button>
          <button @click="editor.chain().focus().toggleCodeBlock().run()">{} 代码</button>

          <button @click="editor.chain().focus().undo().run()">↶ 撤销</button>
          <button @click="editor.chain().focus().redo().run()">↷ 重做</button>
        </div>

        <div class="editor-block">
          <EditorContent :editor="editor" class="editor-content"/>
        </div>
      </div>
      
      <div class="form-actions">
        <button type="button" class="btn-cancel" @click="handleCancel">取消</button>
        <button type="submit" class="btn-submit" :disabled="submitting">{{ submitting ? '提交中...' : '发布文章' }}</button>
      </div>
    </form>
  </div>
</template>

<script>
import logic from './CreateArticle.logic'
import {EditorContent} from "@tiptap/vue-3";

export default {
  name: 'CreateArticle',
  components: {EditorContent},
  ...logic
};
</script>

<style scoped>
.create-article {
  max-width: 1200px;
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

.editor-block {
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 10px;
  min-height: 300px;
  background: white;
}

.editor-content {
  min-height: 250px;
}

.editor-content p {
  line-height: 1.7;
}

.editor-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 10px;
  margin-bottom: 10px;
  background: #f7f7f7;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.editor-toolbar button {
  padding: 6px 10px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
  border-radius: 4px;
}

.editor-toolbar button:hover {
  background: #e6e6e6;
}
</style>