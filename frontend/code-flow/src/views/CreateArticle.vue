<template>
  <div class="create-article">
    <!-- left sidebar: metadata -->
    <aside class="editor-sidebar">
      <div class="sidebar-header">
        <h1>New Article</h1>
      </div>

      <div class="sidebar-body">
        <div class="field">
          <label for="title">Title</label>
          <input
            type="text"
            id="title"
            v-model="articleForm.title"
            placeholder="Enter a compelling title..."
            required
          />
        </div>

        <div class="field">
          <label for="category">Category</label>
          <select id="category" v-model="articleForm.category" required>
            <option value="" disabled>Select category</option>
            <option value="技术">Tech</option>
            <option value="生活">Life</option>
            <option value="学习">Learning</option>
            <option value="工作">Work</option>
          </select>
        </div>

        <div class="field">
          <label>Tags</label>
          <div class="tags-input">
            <span v-for="(tag, index) in tags" :key="index" class="tag-pill">
              {{ tag }}
              <button type="button" class="tag-remove" @click="removeTag(index)">&times;</button>
            </span>
            <input
              type="text"
              v-model="tagInput"
              placeholder="Add tag..."
              @keydown.enter.prevent="addTag"
              @keydown="checkComma"
              @blur="addTag"
            />
          </div>
        </div>

        <div class="sidebar-stats">
          <div class="stat">
            <span class="stat-value">{{ wordCount }}</span>
            <span class="stat-label">words</span>
          </div>
          <div class="stat">
            <span class="stat-value">{{ charCount }}</span>
            <span class="stat-label">chars</span>
          </div>
        </div>
      </div>

      <div class="sidebar-footer">
        <button type="button" class="btn-draft" @click="handleSaveDraft" :disabled="submitting">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>
          {{ submitting ? 'Saving...' : 'Save Draft' }}
        </button>
        <button type="button" class="btn-submit" @click="handleSubmit" :disabled="submitting">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 2L11 13"/><path d="M22 2L15 22L11 13L2 9L22 2Z"/></svg>
          {{ submitting ? 'Publishing...' : 'Publish' }}
        </button>
      </div>
    </aside>

    <!-- right: editor -->
    <main class="editor-main">
      <div class="editor-toolbar">
        <div class="toolbar-group">
          <button type="button" @click="editor.chain().focus().toggleBold().run()" :class="{ active: isActive('bold') }" title="Bold">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M6 4h8a4 4 0 0 1 4 4 4 4 0 0 1-4 4H6z"/><path d="M6 12h9a4 4 0 0 1 4 4 4 4 0 0 1-4 4H6z"/></svg>
          </button>
          <button type="button" @click="editor.chain().focus().toggleItalic().run()" :class="{ active: isActive('italic') }" title="Italic">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="4" x2="10" y2="4"/><line x1="14" y1="20" x2="5" y2="20"/><line x1="15" y1="4" x2="9" y2="20"/></svg>
          </button>
          <button type="button" @click="editor.chain().focus().toggleStrike().run()" :class="{ active: isActive('strike') }" title="Strikethrough">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="4" y1="12" x2="20" y2="12"/><path d="M16 6.5A4 4 0 0 0 12 4c-2 0-3.5 1-3.5 3 0 4 7 2 7 7 0 2-1.5 3-3.5 3a4 4 0 0 1-4-2.5"/></svg>
          </button>
          <button type="button" @click="editor.chain().focus().toggleCode().run()" :class="{ active: isActive('code') }" title="Inline code">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg>
          </button>
        </div>

        <span class="toolbar-divider"></span>

        <div class="toolbar-group">
          <button type="button" @click="editor.chain().focus().toggleHeading({ level: 1 }).run()" :class="{ active: isActive('heading', { level: 1 }) }" title="Heading 1">H1</button>
          <button type="button" @click="editor.chain().focus().toggleHeading({ level: 2 }).run()" :class="{ active: isActive('heading', { level: 2 }) }" title="Heading 2">H2</button>
          <button type="button" @click="editor.chain().focus().toggleHeading({ level: 3 }).run()" :class="{ active: isActive('heading', { level: 3 }) }" title="Heading 3">H3</button>
        </div>

        <span class="toolbar-divider"></span>

        <div class="toolbar-group">
          <button type="button" @click="editor.chain().focus().toggleBulletList().run()" :class="{ active: isActive('bulletList') }" title="Bullet list">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="9" y1="6" x2="20" y2="6"/><line x1="9" y1="12" x2="20" y2="12"/><line x1="9" y1="18" x2="20" y2="18"/><circle cx="4" cy="6" r="1.5" fill="currentColor" stroke="none"/><circle cx="4" cy="12" r="1.5" fill="currentColor" stroke="none"/><circle cx="4" cy="18" r="1.5" fill="currentColor" stroke="none"/></svg>
          </button>
          <button type="button" @click="editor.chain().focus().toggleOrderedList().run()" :class="{ active: isActive('orderedList') }" title="Ordered list">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="10" y1="6" x2="21" y2="6"/><line x1="10" y1="12" x2="21" y2="12"/><line x1="10" y1="18" x2="21" y2="18"/><text x="0" y="9.5" font-size="10" font-weight="700" fill="currentColor" stroke="none">1</text><text x="0" y="21.5" font-size="10" font-weight="700" fill="currentColor" stroke="none">2</text></svg>
          </button>
          <button type="button" @click="editor.chain().focus().toggleBlockquote().run()" :class="{ active: isActive('blockquote') }" title="Blockquote">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" stroke="none"><path d="M6 6h3v6H6V9H3V6h3zm12 0h3v6h-3V9h-3V6h3zM6 3v3H3v6h6V3H6zm12 0v3h-3v6h6V3h-3z" opacity="0.25"/><path d="M4 14h6v3H7v3H4v-6zm11 0h6v3h-3v3h-3v-6z"/></svg>
          </button>
          <button type="button" @click="editor.chain().focus().toggleCodeBlock().run()" :class="{ active: isActive('codeBlock') }" title="Code block">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg>
          </button>
        </div>

        <span class="toolbar-divider"></span>

        <div class="toolbar-group">
          <button type="button" @click="editor.chain().focus().undo().run()" title="Undo">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 1 0 2.13-9.36L1 10"/></svg>
          </button>
          <button type="button" @click="editor.chain().focus().redo().run()" title="Redo">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/></svg>
          </button>
        </div>

        <div class="toolbar-spacer"></div>

        <button type="button" class="ai-continue-btn" @click="handleAiContinue" :disabled="aiLoading" title="AI continue writing">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
          {{ aiLoading ? 'Generating...' : 'AI Write' }}
        </button>
      </div>

      <div class="editor-body">
        <EditorContent :editor="editor" class="editor-content" spellcheck="true" />
      </div>
    </main>
  </div>
</template>

<script>
import { computed } from 'vue'
import logic from './CreateArticle.logic'
import { EditorContent } from "@tiptap/vue-3"

export default {
  name: 'CreateArticle',
  components: { EditorContent },
  setup() {
    const ctx = logic.setup()

    const isActive = (name, attrs) => {
      void ctx.tick.value
      return ctx.editor.value?.isActive(name, attrs ?? {}) ?? false
    }

    const wordCount = computed(() => {
      const text = ctx.editor.value?.getText() || ''
      return text.trim() ? text.trim().split(/\s+/).length : 0
    })

    const charCount = computed(() => {
      return ctx.editor.value?.getText()?.length || 0
    })

    return { ...ctx, isActive, wordCount, charCount }
  }
};
</script>

<style scoped>
.create-article {
  display: flex;
  height: calc(100vh - 60px); /* minus navbar */
  overflow: hidden;
}

/* ======== sidebar ======== */

.editor-sidebar {
  width: 320px;
  min-width: 320px;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #eef0f4;
  background: #fafbfc;
  overflow-y: auto;
}

.sidebar-header {
  padding: 1.5rem 1.5rem 0.75rem;
}

.sidebar-header h1 {
  font-size: 1.3rem;
  font-weight: 800;
  color: #1a1a2e;
  margin: 0;
  letter-spacing: -0.3px;
}

.sidebar-body {
  flex: 1;
  padding: 0 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.field label {
  display: block;
  font-size: 0.75rem;
  font-weight: 700;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 0.35rem;
}

.field input,
.field select {
  width: 100%;
  padding: 0.6rem 0.75rem;
  border: 1.5px solid #e2e6ed;
  border-radius: 8px;
  font-size: 0.9rem;
  color: #1f2937;
  background: #fff;
  transition: border-color 0.2s, box-shadow 0.2s;
  outline: none;
  font-family: inherit;
}

.field input:focus,
.field select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.field input::placeholder {
  color: #c0c6d0;
}

#title {
  font-size: 1rem;
  font-weight: 600;
  padding: 0.65rem 0.75rem;
}

/* ---- tags ---- */

.tags-input {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 5px;
  padding: 0.4rem 0.6rem;
  border: 1.5px solid #e2e6ed;
  border-radius: 8px;
  background: #fff;
  min-height: 40px;
  transition: border-color 0.2s, box-shadow 0.2s;
  cursor: text;
}

.tags-input:focus-within {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.tags-input > input {
  border: none !important;
  background: transparent !important;
  padding: 2px 4px !important;
  font-size: 0.85rem !important;
  flex: 1;
  min-width: 60px;
  outline: none;
  box-shadow: none !important;
}

.tag-pill {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 3px 9px;
  border-radius: 16px;
  font-size: 0.75rem;
  font-weight: 600;
  white-space: nowrap;
  animation: tag-in 0.2s ease-out;
}

@keyframes tag-in {
  from { transform: scale(0.8); opacity: 0; }
  to   { transform: scale(1);   opacity: 1; }
}

.tag-remove {
  background: none;
  border: none;
  color: rgba(255,255,255,0.7);
  font-size: 0.9rem;
  cursor: pointer;
  padding: 0;
  line-height: 1;
  transition: color 0.15s;
}

.tag-remove:hover { color: #fff; }

/* ---- stats ---- */

.sidebar-stats {
  display: flex;
  gap: 1rem;
  padding: 0.5rem 0;
}

.stat {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.stat-value {
  font-size: 1.1rem;
  font-weight: 700;
  color: #374151;
}

.stat-label {
  font-size: 0.75rem;
  color: #9ca3af;
}

/* ---- sidebar footer ---- */

.sidebar-footer {
  padding: 1rem 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  border-top: 1px solid #eef0f4;
}

.btn-draft,
.btn-submit {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 0.6rem 1rem;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  width: 100%;
}

.btn-draft {
  background: #fff;
  color: #4b5563;
  border: 1.5px solid #e2e6ed;
}

.btn-draft:hover:not(:disabled) {
  background: #f3f4f6;
  border-color: #d1d5db;
}

.btn-submit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
}

.btn-submit:active:not(:disabled) { transform: scale(0.97); }

.btn-draft:disabled,
.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ======== editor ======== */

.editor-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background: #fff;
}

.editor-toolbar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 2px;
  padding: 10px 16px;
  background: #fafbfc;
  border-bottom: 1px solid #eef0f4;
  flex-shrink: 0;
}

.toolbar-group {
  display: flex;
  align-items: center;
  gap: 2px;
}

.toolbar-divider {
  width: 1px;
  height: 22px;
  background: #dde1e7;
  margin: 0 6px;
}

.toolbar-spacer {
  flex: 1;
}

.editor-toolbar button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: 6px;
  cursor: pointer;
  color: #4b5563;
  font-size: 13px;
  font-weight: 600;
  transition: background 0.15s, color 0.15s;
}

.editor-toolbar button:hover {
  background: #e8ecf4;
  color: #1f2937;
}

.editor-toolbar button.active {
  background: #667eea;
  color: #fff;
}

.ai-continue-btn {
  width: auto !important;
  padding: 0 12px !important;
  gap: 5px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: #fff !important;
  font-size: 12px !important;
  border-radius: 20px !important;
  font-weight: 600 !important;
}

.ai-continue-btn:hover:not(:disabled) { opacity: 0.9; }

.ai-continue-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ---- editor body ---- */

.editor-body {
  flex: 1;
  overflow-y: auto;
  padding: 2rem 0;
}

.editor-content {
  padding: 0 2rem;
  font-size: 1.05rem;
  line-height: 1.85;
  color: #374151;
  outline: none;
}

.editor-content :deep(h1) {
  font-size: 2rem;
  font-weight: 800;
  margin: 2rem 0 0.75rem;
  color: #111827;
  letter-spacing: -0.5px;
  line-height: 1.3;
}

.editor-content :deep(h2) {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 1.75rem 0 0.5rem;
  color: #1f2937;
  letter-spacing: -0.3px;
}

.editor-content :deep(h3) {
  font-size: 1.2rem;
  font-weight: 700;
  margin: 1.5rem 0 0.5rem;
  color: #374151;
}

.editor-content :deep(p) {
  margin: 0 0 1rem;
}

.editor-content :deep(ul),
.editor-content :deep(ol) {
  padding-left: 1.5rem;
  margin: 0.75rem 0;
}

.editor-content :deep(li) {
  margin-bottom: 0.35rem;
}

.editor-content :deep(blockquote) {
  border-left: 3px solid #667eea;
  padding: 0.75rem 1.25rem;
  margin: 1.25rem 0;
  background: #f8f9fc;
  border-radius: 0 8px 8px 0;
  color: #6b7280;
  font-style: italic;
}

.editor-content :deep(code) {
  background: #f1f3f8;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.9em;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  color: #e11d48;
}

.editor-content :deep(pre) {
  background: #1e1e2e;
  color: #cdd6f4;
  padding: 1.25rem 1.5rem;
  border-radius: 10px;
  overflow-x: auto;
  font-size: 0.9rem;
  line-height: 1.6;
  margin: 1.25rem 0;
}

.editor-content :deep(pre code) {
  background: none;
  color: inherit;
  padding: 0;
  font-size: inherit;
}

.editor-content :deep(img) {
  max-width: 100%;
  border-radius: 8px;
  margin: 1.25rem 0;
}

.editor-content :deep(a) {
  color: #667eea;
  text-decoration: underline;
  text-underline-offset: 2px;
}

.editor-content :deep(.ProseMirror) {
  outline: none !important;
  min-height: 100%;
}

.editor-content :deep(hr) {
  border: none;
  border-top: 1px solid #e5e7eb;
  margin: 2rem 0;
}
</style>
