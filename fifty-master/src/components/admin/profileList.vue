<template>
  <div class="adminTrainerList">
    <div class="container">
      <Header />

      <div class="main">
        <div class="inner d-flex">
          <Sidebar />

          <div class="contentArea">
            <div class="mainTitle d-flex items-center">
              <h1>멤버 프로필 관리</h1>
            </div>

            <div class="profileRow d-flex justify-between">
              <div
                v-for="profile in memberList"
                :key="profile.no"
                class="profileCard text-center"
              >
                <!-- <img :src="`http://localhost:8080/upload/${profile.name}`" :alt="profile.name" class="member-img" /> -->
                <img
                  :src="profile.path"
                  :alt="profile.name"
                  class="member-img"
                >
                <h5 class="mt-2">
                  {{ profile.title }}
                </h5>
                <h5 class="mt-2">
                  {{ profile.subContent }}
                </h5>
                <button
                  class="updateBtn mt-1"
                  @click="editProfile(profile.no)"
                >
                  수정
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <EditProfileModal
    v-if="showModal"
    :profile="selectedProfile"
    @close="closeModal"
    @updated="fetchMemberList"
  />
</template>


<script setup lang="ts">
import Sidebar from '../admin/adminSidebar.vue'
import Header from '../admin/adminHeader.vue'
import axios from "@/api/profile";
import { ref, onMounted} from 'vue'
import EditProfileModal from '@/components/admin/UpdateProfileModal.vue'

const memberList = ref([]);
const selectedProfile = ref(null)
const showModal = ref(false)

const fetchMemberList = async () => {
  try {
    const res = await axios.ListProfile()
    memberList.value = res.data.list
    console.log(memberList.value)
  } catch (err) {
    console.error(err)
  }
}

const editProfile = (profileNo: number) => {
  const profile = memberList.value.find(p => p.no === profileNo)
  if (profile) {
    selectedProfile.value = { ...profile }
    showModal.value = true
  }
}
const closeModal = () => {
  showModal.value = false
  selectedProfile.value = null
}

onMounted(() => {
   fetchMemberList()
})

</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
}

.main {
  display: flex;
  flex: 1;
}

.inner {
  display: flex;
  width: 100%;
}

.contentArea {

  flex: 1;
  padding: 20px;
}

.mainTitle {
  height: 80px;
  padding-left: 20px;
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 20px;
  display: flex;
  align-items: center;

}.profileRow {
  display: flex;
  flex-wrap: nowrap;
  gap: 60px;
  overflow-x: auto;
  padding: 10px 0;
}

.profileCard {
  min-width: 160px;
  max-width: 180px;
  padding: 8px;
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}

.member-img {
  width: 100%;
  height: 300px;
  object-fit: cover;
  border-radius: 4px;
}

.profileCard h5 {
  font-size: 14px;
  margin: 6px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.updateBtn {
  width: 100%;
  background-color: #7c92c0;
  color: #fff;
  border: none;
  padding: 6px 0;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: bold;
  margin-top: 4px;
}

.updateBtn:hover {
  filter: brightness(90%);
}


</style>
