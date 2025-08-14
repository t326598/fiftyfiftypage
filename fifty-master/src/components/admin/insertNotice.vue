<template>
  <div
    class="insert-modal-overlay"
    @click.self="closeModal"
  >
    <div class="insert-modal-content">
      <h3>새 공지사항 등록</h3>
      <div class="field">
        <label for="title">제목:</label>
        <input
          id="title"
          v-model="form.title"
          type="text"
        >

        <label for="content">내용:</label>
        <textarea
          id="content"
          v-model="form.content"
        />

        <div class="insert-modal-buttons">
          <button
            class="submit-btn"
            @click="submit"
          >
            등록
          </button>
          <button
            class="cancel-btn"
            @click="closeModal"
          >
            취소
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineEmits } from 'vue'; // defineEmits 임포트
import axios from '../../api/notice';
import Swal from 'sweetalert2';

// emit 함수 정의 (부모 컴포넌트에 이벤트를 보낼 때 사용)
const emits = defineEmits(['close', 'noticeAdded']); // 'close'와 'noticeAdded' 이벤트 정의

const form = ref({
  title: '',
  content: '',
});

const submit = async () => {
  try {
    const res = await axios.insertNotice({
      title: form.value.title,
      content: form.value.content,
    });
    console.log('성공:', res.data);
    await Swal.fire({
      icon: 'success',
      title: '등록 성공',
      text: '공지사항이 성공적으로 등록되었습니다!',
      confirmButtonText: '확인',
    });
    
    // 성공적으로 등록되면 부모에게 알리고 모달을 닫음
    emits('noticeAdded'); // 공지사항이 추가되었음을 부모에게 알림
    emits('close'); // 모달 닫기 요청
    
  } catch (err) {
    console.error('에러 발생:', err);
    Swal.fire({
      icon: 'error',
      title: '등록 실패',
      text: '공지사항 등록 중 오류가 발생했습니다.',
      confirmButtonText: '확인',
    });
  }
};

// 모달 닫기 함수
const closeModal = () => {
  emits('close'); // 'close' 이벤트를 부모 컴포넌트로 보냄
};
</script>

<style scoped>
/* 모달 오버레이 스타일 */
.insert-modal-overlay {
  position: fixed; /* 화면 전체를 덮도록 고정 */
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7); /* 반투명 검은색 배경 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000; /* 다른 요소들보다 위에 나타나도록 높은 z-index 부여 */
}

/* 모달 본문 스타일 */
.insert-modal-content {
  background-color: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3); /* 그림자 효과 */
  width: 450px; /* 너비 조정 */
  max-width: 90%; /* 최대 너비 설정 (반응형 고려) */
  box-sizing: border-box; /* padding이 너비에 포함되도록 */
  animation: fadeInScale 0.3s ease-out; /* 등장 애니메이션 */
}

/* 등장 애니메이션 (선택 사항) */
@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}


.insert-modal-content h3 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.field {
  display: flex;
  flex-direction: column;
}

label {
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

input,
textarea {
  margin-bottom: 15px;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #ddd;
  font-size: 16px;
  resize: vertical; /* textarea만 세로 크기 조절 가능 */
}

textarea {
  min-height: 120px; /* 최소 높이 설정 */
}

.insert-modal-buttons {
  display: flex;
  justify-content: flex-end; /* 버튼들을 오른쪽으로 정렬 */
  gap: 10px; /* 버튼들 사이 간격 */
  margin-top: 20px;
}

.submit-btn,
.cancel-btn {
  padding: 12px 25px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: background-color 0.3s ease;
}

.submit-btn {
  background-color: #007bff; /* 파란색 */
  color: white;
}

.submit-btn:hover {
  background-color: #0056b3;
}

.cancel-btn {
  background-color: #6c757d; /* 회색 */
  color: white;
}

.cancel-btn:hover {
  background-color: #5a6268;
}
</style>