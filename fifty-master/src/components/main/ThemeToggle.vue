<template>
  <div class="ThemeToggle">
    <div class="image-wrapper">
      <transition name="fade" mode="out-in">
        <img :key="isDark" :src="isDark ? nightImg : dayImg" class="background" alt="Day & Night" />
      </transition>
      <!-- ✅ 버튼을 이미지 wrapper 안에 넣어야 위치가 맞음 -->
      <button class="click-zone day" @click="setDay"></button>
      <button class="click-zone night" @click="setNight"></button>
    </div>

    <button
      v-if="showFloatingToggle"
      @click="$emit('toggle')"
      :class="['theme-toggle', isDark ? 'day-mode' : 'night-mode']"
    >
      {{ isDark ? '☀️' : '🌙' }}
    </button>
  </div>
</template>


<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
const props = defineProps<{ isDark: boolean }>()
const emit = defineEmits(['toggle'])

const setDay = () => {
    if (props.isDark) emit('toggle')
}
const setNight = () => {
    if (!props.isDark) emit('toggle')
}

const dayImg = new URL('@/assets/image/daynight_day.png', import.meta.url).href
const nightImg = new URL('@/assets/image/daynight_night.png', import.meta.url).href

const showFloatingToggle = ref(false)

const handleScroll = () => {
    showFloatingToggle.value = window.scrollY > 800
}

onMounted(() => {
    window.addEventListener('scroll', handleScroll)
})

onBeforeUnmount(() => {
    window.removeEventListener('scroll', handleScroll)
})

</script>

<style scoped>
.ThemeToggle {
    position: relative;
    width: 80%;
    margin: auto;
    overflow: hidden;
}

.image-wrapper {
    position: relative;
    margin: auto;
    width: 60%;
    overflow: hidden;
}

.background {
    width: 100%;
    height: auto;
    display: block;
    user-select: none;
}

/* ✅ fade 효과 */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

.fade-enter-to,
.fade-leave-from {
    opacity: 1;
}

.click-zone {
    position: absolute;
    top: 0;
    bottom: 0;
    width: 50%;
    background-color: transparent;
    border: none;
    cursor: pointer;
    z-index: 2;
}

.click-zone.day {
    left: 0;
}

.click-zone.night {
    right: 0;
}

.theme-toggle {
    position: fixed;
    top: 20px;
    right: 20px;
    width: 48px;
    height: 48px;
    font-size: 24px;
    border: 1px solid #ccc;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    z-index: 1000;
    transition: background-color 0.3s ease, color 0.3s ease;
}

/* ☀️ (밤 모드 → 낮) → 흰 배경 */
.theme-toggle.day-mode {
    background-color: white;
    color: black;
}

/* 🌙 (낮 모드 → 밤) → 검정 배경 */
.theme-toggle.night-mode {
    background-color: #222;
    color: white;
}

.theme-toggle:hover {
    filter: brightness(1.1);
}



</style>
