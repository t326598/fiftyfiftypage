<template>
  <div
    v-if="visible"
    class="popup-overlay"
  >
    <div class="popup-content">
      <img
        :src="popupImage"
        alt="주의사항 팝업"
      >
      <button
        class="popup-close"
        @click="closePopup"
      >
        확인
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import warningImage from '@/assets/image/warning.png'

const visible = ref(true)
const popupImage = warningImage
const closePopup = () => {
  visible.value = false
}

</script>

<style scoped>
.popup-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100vw; height: 100vh;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.popup-content {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  max-width: 90%;
  width: 400px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.3);
}

.popup-content img {
  width: 100%;
  display: block;
}

.popup-close {
  background: #e74c3c;
  color: white;
  padding: 10px;
  border: none;
  width: 100%;
  font-size: 16px;
  cursor: pointer;
}
</style>
