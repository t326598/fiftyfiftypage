<template>
  <div
    class="modal-overlay"
    @click.self="close"
  >
    <div class="modal-content">
      <h2>일정 백그라운드</h2>

      <div class="form-group">
        <label>이미지</label>
        <input
          type="file"
          @change="onImageChange"
        >
        <img
          v-if="previewUrl"
          :src="previewUrl"
          class="preview"
        >
      </div>

      <div class="actions">
        <button @click="submit">
          저장
        </button>
        <button @click="close">
          닫기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import axios from "@/api/files"

const props = defineProps<{
  months: any
}>()

const emit = defineEmits(['close', 'updated'])

const form = ref({
  id: props.months.id,
  month: props.months.month,
  file: null as File | null,
  filePath: props.months.name
})

const previewUrl = ref<string | null>(props.months.path)

const onImageChange = (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (file) {
    form.value.file = file
    previewUrl.value = URL.createObjectURL(file)
  }
}


const uploadFile = async (file: File): Promise<string> => {
  const formData = new FormData()
  formData.append('no', props.months.fileNo)
  formData.append('data', file)
  formData.append('oldFilePath', props.months.path)
  console.log([...formData.entries()]); 
  const response = await axios.updateFiles( formData)
  console.log(response.data)


  return response.data.filePath
}



const submit = async  () => {
  try {

let newFilePath = form.value.filePath
    if (form.value.file) {
      newFilePath = await uploadFile(form.value.file)
    }

    console.log("나옴?")

    emit('updated')
    emit('close')
  } catch (err) {
    console.error(err)
  }
}

const close = () => emit('close')

</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  background: white;
  padding: 24px;
  width: 400px;
  border-radius: 8px;
}
.form-group {
  margin-bottom: 16px;
}
.preview {
  width: 100%;
  object-fit: cover;
  margin-top: 8px;
}
.actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
