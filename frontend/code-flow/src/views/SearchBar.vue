<template>
  <div class="search-bar">
    <input
        type="text"
        v-model="query"
        placeholder="搜索文章..."
        @keyup.enter="handleSearch"
    />
    <button @click="handleSearch">🔍</button>
  </div>
</template>

<script>
export default {
  name: "SearchBar",
  data() {
    return {
      query: "",
    };
  },
  methods: {
    handleSearch() {
      const trimmedQuery = this.query.trim();
      if (trimmedQuery) {
        // 跳转到文章列表页，并带上搜索参数
        this.$router.push({ path: "/search", query: { keyword: trimmedQuery } });
      }
    },
  },
};
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 220px;

  background: #fff;
  border-radius: 999px;
  padding: 4px 8px;              /* ↓ 原来 6px 10px */

  border: 1px solid #e5e7eb;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.05);

  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.search-bar:focus-within {
  border-color: #4caf50;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.15);
}

.search-bar input {
  flex: 1;
  border: none;
  outline: none;

  padding: 4px 8px;              /* ↓ 原来 6px 10px */
  font-size: 13px;               /* ↓ 原来 14px */
  background: transparent;

  color: #333;
}

.search-bar button {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 30px;                   /* ↓ 原来 36px */
  height: 27px;                  /* ↓ 原来 36px */
  border-radius: 50%;

  border: none;
  background: #4caf50;
  color: white;
  font-size: 14px;               /* ↓ 原来 16px */

  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease;
}

.search-bar button:hover {
  background-color: #43a047;
}

.search-bar button:active {
  transform: scale(0.95);
}

</style>
