<template>
     
 <div class="fixed-buttons">
    <button class="top-button" @click="scrollToTop">
      ⬆️ 최상단
    </button>
    <a
      href="https://open.kakao.com/o/yourChatLink" 
      target="_blank"
      rel="noopener noreferrer"
      class="kakao-chat-button"
    >
      💬 카카오톡 문의
    </a>

  </div>
</template>
<script setup lang="ts">

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth',
  });
};

</script>

<style scoped src="@/assets/style/mainpage.css"></style>