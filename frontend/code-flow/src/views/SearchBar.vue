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
        this.$router.push({ path: "/articles", query: { search: trimmedQuery } });
      }
    },
  },
};
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  border: 1px solid #ccc;
  border-radius: 4px;
  overflow: hidden;
}

.search-bar input {
  flex: 1;
  border: none;
  padding: 4px 8px;
  outline: none;
}

.search-bar button {
  background-color: #4caf50;
  border: none;
  color: white;
  padding: 4px 10px;
  cursor: pointer;
}

.search-bar button:hover {
  background-color: #45a049;
}
</style>
