<template>
  <div
    class="modal-overlay"
    @click="closeModalByOverlay"
  >
    <div
      class="modal-content"
      @click.stop
    >
      <div class="modal-header">
        <h2>이미지 등록</h2>
        <button
          class="close-button"
          @click="closeModal"
        >
          ×
        </button>
      </div>

      <div class="modal-body">
        <div class="upload-container">
          <div class="button-group">
            <button
              v-for="(name, num) in categories"
              :key="num"
              type="button"
              :class="{ active: form.crt.includes(Number(num)) }"
              @click="toggleMember(Number(num))"
            >
              {{ name }}
            </button>
          </div>
          <div class="field">
            <label for="true_day">촬영일자:</label>
            <select
              id="true_day"
              v-model="form.true_day"
            >
              <option
                disabled
                value=""
              >
                선택
              </option>
              <option value="2024">
                2024년
              </option>
              <option value="2025">
                2025년
              </option>
            </select>
          </div>

          <div class="field">
            <label>이미지 업로드:</label>
            <input
              type="file"
              accept="image/*"
              @change="onFileChange"
            >
          </div>

          <div
            v-if="previewUrl"
            class="preview"
          >
            <img
              :src="previewUrl"
              alt="preview"
            >
            <button @click="removeImage">
              취소
            </button>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button
          class="submit-button"
          @click="submit"
        >
          업로드
        </button>
        <button
          class="cancel-button"
          @click="closeModal"
        >
          닫기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from '../../api/files' // axios 인스턴스 경로 확인

// props 정의 (부모 컴포넌트로부터 close 이벤트를 받기 위함)
const emit = defineEmits(['close', 'imageAdded'])

const form = ref({
  crt: [],
  true_day: '',
  image: null
})

const previewUrl = ref(null)

const categories = {
  1: '키나',
  2: '샤넬',
  3: '예원',
  4: '하나',
  5: '아테나' 
}

const onFileChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    form.value.image = file
    previewUrl.value = URL.createObjectURL(file)
  }
}

const removeImage = () => {
  form.value.image = null
  previewUrl.value = null
}

const toggleMember = (memberId) => {
  const index = form.value.crt.indexOf(memberId)
  if (index === -1) {
    form.value.crt.push(memberId)
  } else {
    form.value.crt.splice(index, 1)
  }
}

const submit = async () => {
  // 입력값 유효성 검사 (선택된 카테고리, 촬영일, 이미지 파일)
  if (form.value.crt.length === 0) {
    alert('멤버를 선택해주세요.')
    return;
  }
  if (!form.value.true_day) {
    alert('촬영 연도를 선택해주세요.')
    return;
  }
  if (!form.value.image) {
    alert('업로드할 이미지를 선택해주세요.')
    return;
  }

  const formData = new FormData()
  // crt 배열을 쉼표로 구분된 문자열로 변환하여 전송 (서버에서 "1,2" 이런 식으로 받을 경우)
  formData.append('crt', form.value.crt.join(','))
  formData.append('trueDay', form.value.true_day)
  formData.append('data', form.value.image) // 서버에서 MultipartFile 받는 필드명이 data여야 함

  try {
    const res = await axios.insertFile(formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    console.log('업로드 성공:', res.data)
    alert('이미지가 성공적으로 등록되었습니다.')
    emit('imageAdded') // 이미지 등록 성공 시 부모에게 알림
    closeModal() // 모달 닫기
  } catch (err) {
    console.error('업로드 에러 발생:', err)
    alert('이미지 업로드 중 오류가 발생했습니다.')
  }
}

// 모달 닫기 함수
const closeModal = () => {
  // 모달 닫기 전에 폼 데이터 초기화 (선택 사항)
  form.value = {
    crt: [],
    true_day: '',
    image: null
  };
  previewUrl.value = null;
  emit('close'); // 부모 컴포넌트에 닫힘 이벤트 전달
}

// 오버레이 클릭 시 모달 닫기 (이벤트 버블링 방지)
const closeModalByOverlay = (e) => {
  if (e.target.classList.contains('modal-overlay')) {
    closeModal();
  }
}
</script>

<style scoped>
/* 모달 오버레이 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.6); /* 더 어두운 배경 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(3px); /* 선택 사항: 배경 블러 효과 */
}

/* 모달 콘텐츠 박스 스타일 */
.modal-content {
  background-color: white;
  padding: 25px;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  width: 450px; /* 모달의 너비 조정 */
  max-width: 90%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 20px; /* 내부 요소 간 간격 */
  animation: fadeIn 0.3s ease-out; /* 애니메이션 추가 */
}

/* 모달 헤더 */
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #333;
}

.close-button {
  background: none;
  border: none;
  font-size: 2rem;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
}

.close-button:hover {
  color: #333;
}

/* 모달 바디 */
.modal-body {
  flex-grow: 1; /* 남은 공간을 채우도록 */
  overflow-y: auto; /* 내용이 많아지면 스크롤 */
  padding-right: 5px; /* 스크롤바 공간 확보 */
}

.upload-container {
  /* display: flex; /* 이미 flex-direction: column; 되어 있음 */
  flex-direction: column;
  gap: 1.2rem; /* 간격 살짝 늘림 */
  /* max-width: 300px; /* 이 max-width는 이제 .modal-content에 의해 조절되므로 제거하거나 100%로 설정 */
  width: 100%; /* 모달 콘텐츠 너비에 맞춤 */
}

.field label {
  font-weight: bold;
  margin-bottom: 5px;
  color: #555;
}

.field input[type="file"],
.field select {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 0.9rem;
  width: calc(100% - 18px); /* 패딩 고려 */
  box-sizing: border-box;
}

/* 이미지 미리보기 */
.preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  margin-top: 1rem;
}

.preview img {
  max-width: 150px; /* 미리보기 이미지 크기 증가 */
  max-height: 150px;
  border: 2px solid #eee;
  border-radius: 8px;
  object-fit: contain; /* 이미지 비율 유지 */
}

.preview button {
  background: #dc3545; /* 붉은색 버튼 */
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.preview button:hover {
  background: #c82333;
}

/* 버튼 그룹 */
.button-group {
  display: flex;
  flex-wrap: wrap; /* 버튼이 많아지면 다음 줄로 */
  gap: 8px; /* 버튼 간 간격 */
  margin-bottom: 10px;
}

.button-group button {
  padding: 8px 15px;
  border-radius: 5px;
  border: 1px solid #007bff;
  background-color: #fff;
  color: #007bff;
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s, border-color 0.2s;
}

.button-group button.active {
  background-color: #007bff;
  color: white;
}

.button-group button:hover:not(.active) {
  background-color: #e9f5ff;
}

/* 모달 푸터 */
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.modal-footer button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s;
}

.submit-button {
  background-color: #28a745; /* 녹색 */
  color: white;
}

.submit-button:hover {
  background-color: #218838;
}

.cancel-button {
  background-color: #6c757d; /* 회색 */
  color: white;
}

.cancel-button:hover {
  background-color: #5a6268;
}

/* 애니메이션 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>