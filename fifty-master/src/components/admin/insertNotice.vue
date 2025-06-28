<template>
  <div class="upload-container">
    <div class="field">
      <label for="title">제목:</label>
      <input type="text" v-model="form.title" id="title" />

      <label for="content">내용:</label>
      <textarea v-model="form.content" id="content"></textarea>

      <button @click="submit">업로드</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios  from '../../api/notice'
import Swal from 'sweetalert2'

const form = ref({
  title: '',
  content: '',
})

const submit = async () => {

  try {
    const res = await axios.insertNotice({
          title:  form.value.title,
      content: form.value.content
    })
    console.log('성공:', res.data)
     await Swal.fire({
      icon: 'success',
      title: '등록 성공',
      text: '공지사항이 성공적으로 등록되었습니다!',
      confirmButtonText: '확인',
    })
      window.close()
  } catch (err) {
    console.error('에러 발생:', err)
  }
}


</script>

<style scoped>.upload-container {
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 10px;
  width: 400px;
  margin: auto;
}

.field {
  display: flex;
  flex-direction: column;
}

input,
textarea {
  margin-bottom: 10px;
  padding: 8px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

button {
  padding: 10px;
  background-color: #4285f4;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
</style>
