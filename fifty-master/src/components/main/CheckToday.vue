<script setup>
import { onMounted } from 'vue';
import axios from '@/api/today';

onMounted(async () => {
  try {
    await axios.checkToday({
      withCredentials: true  
    });
  } catch (error) {
    console.error('투데이 방문자 증가 실패:', error);
  }
});
</script>

<template>
  <div>

  </div>
</template>