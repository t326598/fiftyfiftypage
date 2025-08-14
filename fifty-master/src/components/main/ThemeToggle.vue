<template>
  <div class="ThemeToggle">
    <div class="image-wrapper">
      <!-- ✅ 이미지 두 개 렌더링 + opacity로 전환 -->
      <img
        :src="dayImg"
        class="background"
        :class="{ visible: !isDark }"
        alt="Day"
      />
      <img
        :src="nightImg"
        class="background"
        :class="{ visible: isDark }"
        alt="Night"
      />

      <!-- ✅ 버튼은 별도 z-index로 위에 위치 -->
      <button class="click-zone day" @click="setDay" />
      <button class="click-zone night" @click="setNight" />
    </div>

    <!-- ✅ 상단 고정 테마 토글 버튼 -->
    <button
      v-if="showFloatingToggle"
      :class="['theme-toggle', isDark ? 'day-mode' : 'night-mode']"
      @click="$emit('toggle')"
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

  // ✅ 이미지 미리 로딩
  const preloadDay = new Image()
  preloadDay.src = dayImg

  const preloadNight = new Image()
  preloadNight.src = nightImg
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
  height: auto;
  max-height: 300px;
  min-height: 50px;
  aspect-ratio: 16 / 9; /* ✅ 비율 고정 */
  overflow: hidden;
}

/* ✅ 이미지 두 개 겹쳐놓고 opacity로 전환 */
.background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
  user-select: none;
  opacity: 0;
  transition: opacity 0.5s ease;
  z-index: 1;
}

.background.visible {
  opacity: 1;
  z-index: 2;
}

/* ✅ 클릭 가능한 반반 영역 (버튼) */
.click-zone {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 50%;
  background-color: transparent;
  border: none;
  cursor: pointer;
  z-index: 3; /* 이미지보다 위 */
}

.click-zone.day {
  left: 0;
}

.click-zone.night {
  right: 0;
}

/* ✅ 화면 상단 고정 테마 토글 버튼 */
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

.theme-toggle.day-mode {
  background-color: white;
  color: black;
}

.theme-toggle.night-mode {
  background-color: #222;
  color: white;
}

.theme-toggle:hover {
  filter: brightness(1.1);
}
</style>
