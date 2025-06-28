<template>
    <div class="header">
      <transition :name="isDark ? 'slide-dark' : 'slide-light'" mode="out-in">
        <img
          :key="isDark ? 'dark-logo' : 'light-logo'"
          :src="isDark ? require('@/assets/image/test.png') : require('@/assets/image/3333.svg')"
          alt="fiftyfifty"
          class="fifty-img"

        />
      </transition>
    </div>

    <!-- 메인 이미지 -->
    <section class="hero-image" style="margin-bottom: 30px;">
      <transition :name="isDark ? 'slide-dark' : 'slide-light'" mode="out-in">
        <img
          :key="isDark ? 'dark-hero' : 'light-hero'"
          :src="isDark ? require('@/assets/image/darkmidnight.png') : require('../../assets/image/tofifty.png')"
          class="hero-image1"
          alt="FIFTY FIFTY Members"
          style="border-radius: 20px; width: 60%;"
        />
      </transition>
    </section>

</template>
<script setup lang="ts">
const props = defineProps<{ isDark: boolean }>()
</script>

<style scoped src="@/assets/style/mainpage.css"></style>