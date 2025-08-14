<template>
  <div :class="['mainbody', { 'dark-mode': isDark }]">
    <WarningPopup />
    <MainHeader :is-dark="isDark" />
    
    <ThemeToggle
      :is-dark="isDark"
      @toggle="toggleTheme"
    />
    <CommunityLinks />

    <CalendarView :is-dark="isDark" />

    <NoticeBoard />
    <hr class="section-divider">
    <YoutubeRankingSlider />

    <h1 style="font-size: 20px; margin-bottom: 20px; ">
      <a
        style="text-decoration: none;"
        href="/youtubeVideo"
      >피프티 최신영상 보러가기</a>
    </h1>
    <hr class="section-divider">
    <MemberGallery @select="onSelectMember" />
    <ProfileDetailModal
      v-if="selectedMember"
      :member="selectedMember"
      @close="selectedMember = null"
    />

    <FixedButtons @top="scrollToTop" />
    <FooterBar />
    <CheckToday />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import WarningPopup from '@/components/main/WarningPopup.vue'
import ThemeToggle from '@/components/main/ThemeToggle.vue'
import MainHeader from '@/components/main/MainHeader.vue'
import CommunityLinks from '@/components/main/CommunityLinks.vue'
import CalendarView from '@/components/main/CalendarView.vue'
import NoticeBoard from '@/components/main/NoticeBoard.vue'
import YoutubeRankingSlider from '@/components/main/YoutubeRankingSlider.vue'
import MemberGallery from '@/components/main/MemberGallery.vue'
import FixedButtons from '@/components/main/FixedButtons.vue'
import FooterBar from '@/components/main/FooterBar.vue'
import CheckToday from '@/components/main/CheckToday.vue'

const isDark = ref(false)
const selectedMember = ref<any>(null)

function toggleTheme() {
  isDark.value = !isDark.value
}

function onSelectMember(member: any) {
  selectedMember.value = member
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>



<style scoped>
.dark-calendar .fc {
  color: #ffffff;
}

.dark-calendar .fc-event {
  border-color: #555555;
  color: #ffffff;
}

.footer {
  background-color: #f4f4f4;
  padding: 20px 40px;
  text-align: center;
  border-top: 1px solid #ccc;
}

.footer-content {
  max-width: 960px;
  margin: 0 auto;
}

.footer p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin-top: 10px;
  display: flex;
  justify-content: center;
  gap: 15px;
}

.footer-links li a {
  text-decoration: none;
  color: #007acc;
  font-size: 14px;
}

.footer-links li a:hover {
  text-decoration: underline;
}
.fixed-buttons {
  position: fixed;
  bottom: 20px;
  right: 20px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
  z-index: 1000;
}

.kakao-chat-button,
.top-button {
  padding: 12px 16px;
  border-radius: 50px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  font-weight: bold;
  font-size: 14px;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

/* 카카오톡 버튼 스타일 */
.kakao-chat-button {
  background-color: #fae100;
  color: #000;
}

.kakao-chat-button:hover {
  background-color: #ffe000;
  transform: translateY(-2px);
}

/* 최상단 버튼 스타일 */
.top-button {
  background-color: #333;
  color: white;
  border: none;
}

.top-button:hover {
  background-color: #555;
  transform: translateY(-2px);
}


.section-divider {
  border: none;
  height: 2px;
  background: linear-gradient(to right, #ccc, #eee, #ccc);
  margin: 3vh auto;
  width: 80%;
}

.gallery-section {
  background-color: #f9f9f9;
}


.rank-badge {
  position: absolute;
  top: 2px;
  left: 9px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: bold;
}

.top-highlight {
  background-color: #ffcc00;
  color: #000;
}

.top-regular {
  background-color: #e0e0e0;
  color: #333;
}

.calendar-legend {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  justify-content: flex-end;
  width: 100%;
}

.calendar-wrapper {
  display: flex;
  justify-content: center;
  height: 100%;
  max-height: 800px;
  margin-top: 10px;
}



.calendar-content {
  padding: 50px;
  color: #000000;
  font-weight: bold;
  position: relative;
  z-index: 1;
  width: 50%;
  overflow: hidden;
  /* 이 부분이 중요! 내부 내용만 보여줍니다 */
  border-radius: 5%;
}

.calendar-background {
  position: absolute;
  filter: blur(2px);
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  opacity: 0.3;
  z-index: 0;
}


.notice-title {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 16px;
  color: #ff4d6d;
}

.notice {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  /* 수평 정렬 */
  padding: 5px;
  text-align: center;
  /* 텍스트 중앙 정렬 */
}

.notice-list {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  /* 수평 정렬 */
  padding: 5px;
  text-align: center;
  /* 텍스트 중앙 정렬 */
  color: black;
  font-size: 20px;
  list-style: none;
}

.notice-item {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  margin-bottom: 5px;
  cursor: pointer;
  transition: background 0.2s;
  border-radius: 8px;
}

.notice-item:hover {
  background-color: #f5f5f5;
  transition: transform 0.3s ease;
}

.notice-card {
 background: rgba(255, 255, 255, 0.25);
  border: 1px solid #f8bbd0;
  padding-bottom: 10px;
  border-radius: 1rem;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  width: 60%;
  margin-top: 20px;
}


/* 팝업 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal-content1 {
  background-color: white;
  color: black;
  padding: 40px;
  border-radius: 16px;
  width: 50%;
  max-width: 800px;
  max-height: 80%;
  overflow-y: auto;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  animation: slideBounceIn 0.4s ease;
}

.modal-content h2 {
  margin-top: 0;
  font-size: 28px;
}

.modal-content .content {
  margin: 24px 0;
  font-size: 18px;
  white-space: pre-wrap;
}

.modal-content .meta {
  font-size: 14px;
  color: #666;
  margin-bottom: 24px;
}

/* 애니메이션 */
@keyframes slideBounceIn {
  0% {
    transform: translateX(-100%) scale(0.95);
    opacity: 0;
  }
  60% {
    transform: translateX(10%) scale(1.02);
    opacity: 1;
  }
  80% {
    transform: translateX(-5%) scale(0.98);
  }
  100% {
    transform: translateX(0) scale(1);
  }
}

.fifty-img {
  margin: 150px 0 0px 0px;
  position: relative;
  width: 50%;
  min-width: 600px;
  height: auto;
  z-index: 2;
  
}

.title, .views{
  color:black
}

.hero-image1 {
  min-width: 600px;
   max-width: 1200px;
   max-height: 469.7px;
   margin: -30px 0 0 0;
  z-index: 1;
  position: relative;
}

.mainbody {
  position: relative;
  z-index: 10;
  transition: background 1s ease-in-out;
     background: linear-gradient(to bottom,#ffffff 0%, #fdd7ef 8%,  #f7e0ee 85%, #ffffff 100%);
  background-size: 100% auto;
  height: 100%;
   overflow-x: auto;
    &.dark-mode {
    background-image: none;
    background-color: #333;
    z-index: 11;
    height: 100%;
    color: white;
  }
}

.mainbody::before {
  content: '';
  position: absolute;
  top: 0; left: 0;
  width: 100%;
  height: 100%;
  transform: translateX(-100%);
  transition: transform 1s ease-in-out;
  z-index: -1;
}

.mainbody.dark-mode::before {
  transform: translateX(0);
}
/* 🌞 → 🌙 : 왼쪽에서 들어오기 */
.slide-dark-enter-from {
  opacity: 0;
  transform: translateX(-50px);
}
.slide-dark-enter-active {
  transition: all 0.6s ease;
}
.slide-dark-leave-to {
  opacity: 0;
  transform: translateX(50px);
}
.slide-dark-leave-active {
  transition: all 0.6s ease;
}

/* 🌙 → 🌞 : 오른쪽에서 들어오기 */
.slide-light-enter-from {
  opacity: 0;
  transform: translateX(50px);
}
.slide-light-enter-active {
  transition: all 0.6s ease;
}
.slide-light-leave-to {
  opacity: 0;
  transform: translateX(-50px);
}
.slide-light-leave-active {
  transition: all 0.6s ease;
}


.theme-toggle {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1000;
  background: transparent;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

.profile-box {
  background-color: white;
  border-radius: 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  gap: 40px;
  margin: 30px auto;
  width: 1000px;
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.7s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  transform: translateY(-20px);
  opacity: 0;
}

.slide-down-leave-from {
  transform: translateY(0);
  opacity: 1;
}


.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}


.profile-box-content {
  display: flex;
  justify-content: space-around;
  text-align: left;
  padding: 20px;
  width: 100%;
}

.close-btn {
  background-color: #f5b8cc;
  color: rgb(0, 0, 0);
  border: none;
  padding: 10px 20px;
  border-radius: 12px;
  margin: 20px auto;
  display: block;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.close-btn:hover {
  background-color: #ec407a;
  cursor: pointer;
}

.member-text {
  color: #333;
  font-size: 18px;
  font-weight: bold;
}

h1 {
   color: #ff4d6d;
   font-size: 30px;
  letter-spacing: 1px;

}

.mainbody h1 {
  margin: 0;
}

.rank {
  height: 400px;
}

/* 한줄로 나열 */
.community {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 20px;
  max-width: 1500px;
  margin: 0 auto;
  margin-bottom: 40px;
  padding: 30px 0;
  box-sizing: border-box;
}


.community a {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  text-decoration: none;
  color: rgb(252, 252, 252);
  width: 100%;
  max-width: 300px;
  padding: 10px;
  background-image: linear-gradient(to bottom, #7D64BD, #B166B6 25%, #CF8AB4 80%);
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.calander {
  margin: 30px auto;
  width: 80%;
  max-width: 1200px;
  overflow-x: auto;
  font-weight: bold;
  color: black;
}

.profile {
  height: 400px;
}

.card-image-wrapper {
  margin-top: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.card-image {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 60px;
  padding: 20px;
}

.card-grid {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 20px;
}

.profile-filter-section {
  background: linear-gradient(to bottom, #ffdeeb, #faf5f6);
  padding: 1px;
  width: 1300px;
  margin: auto;
  border-radius: 50px;
}

.member-card {
  border-radius: 20px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  padding: 10px;
  background: #fff0f6;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  width: 200px;
  height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.member-card-image{
    border-radius: 20px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
    width: 250px;
  height: 350px;
}

@media (max-width: 1380px) {
  .header1 {

    font-size: 120px;
  }

  .profile-box {
    margin-top: 20px;
    overflow: hidden;
    width: 800px;
  }

  .profile-filter-section {
    background: linear-gradient(to bottom, #fda4c8, #fce4ec);
    padding: 10px;
    width: 1000px;
    margin: auto;
    border-radius: 50px;
  }

  .community {
    width: 100%;
    grid-template-columns: 1fr;
  }

  .card-image {
    grid-template-columns: repeat(4, 1fr);
  }

  .pagination button {
    padding: 12px 25px;
    font-size: 16px;
  }
}

@media (max-width: 1080px) {
  .card-image {
    grid-template-columns: repeat(3, 1fr);
  }

  .pagination button {
    padding: 12px 25px;
    font-size: 16px;
  }

  .profile-box {
    margin-top: 20px;
    overflow: hidden;
    width: 600px;
  }

  .profile-filter-section {
    background: linear-gradient(to bottom, #fda4c8, #fce4ec);
    padding: 10px;
    width: 800px;
    margin: auto;
    border-radius: 50px;
  }
}

@media (max-width: 920px) {
  .community {
    width: 100%;
    grid-template-columns: 1fr;
  }

  .card-image {
    grid-template-columns: repeat(2, 1fr);
  }

  .pagination button {
    padding: 12px 25px;
    font-size: 16px;
  }

  .profile-box {
    margin-top: 20px;
    overflow: hidden;
    width: 300px;
  }

  .profile-filter-section {
    background: linear-gradient(to bottom, #fda4c8, #fce4ec);
    padding: 10px;
    width: 500px;
    margin: auto;
    border-radius: 50px;
  }

  @media (max-width: 520px) {
    .community {
      grid-template-columns: 1fr;
    }

    .card-image {
      grid-template-columns: 1fr;
    }

    .pagination button {
      padding: 10px 20px;
      font-size: 14px;
    }

    .profile-box {
      font-size: 10px;
      margin-top: 20px;
      overflow: hidden;
      width: 300px;
    }

    .profile-filter-section {
      background: linear-gradient(to bottom, #fda4c8, #fce4ec);
      padding: 10px;
      width: 400px;
      margin: auto;
      border-radius: 50px;
    }

    .profile-box-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      gap: 15px;
      padding: 20px;
    }
  }

  @media (max-width: 440px) {
    .calendar-wrapper{
      width: 450px;
      margin-left: -12%;
    }
    .fc {
      font-size: 11px;
    }

  .fc-header-toolbar {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
  }
  .fc .fc-button {
    font-size: 10px !important;
    padding: 2px 6px !important;
  }

    .fc-list-day {
      font-size: 12px;
      padding: 4px 6px;
    }

    .fc-list-event-title,
    .fc-list-event-time {
      font-size: 11px;
    }

    .fc-header-toolbar {
      flex-wrap: wrap;
      justify-content: center;
    }

    #calendar {
      padding: 5px;
      max-width: 100%;
    }
  }

}

.pagination button {
  background-color: white;
  color: black;
  font-size: 18px;
  padding: 10px 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  margin-bottom: 100px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.pagination button:hover {
  background-color: #f0f0f0;
}

.pagination button:disabled {
  background-color: #e0e0e0;
  cursor: not-allowed;
}

.pagination span {
  font-size: 18px;
  margin: 0 10px;
}

.member-card-image:hover {
  transform: scale(1.05);
}

.member-card:hover,
.member-card-image:hover {
  transform: scale(1.05);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
}

.member-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 15px;
}

.social-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.modal-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
}

.modal-img {
  height: 500px;
  object-fit: contain;
  border-radius: 20px;
}

.modal-close {
  position: absolute;
  top: 10px;
  right: 20px;
  font-size: 28px;
  background: none;
  color: white;
  border: none;
  cursor: pointer;
}

.slider-container {
  overflow-x: auto;
  width: 100%;
  padding-bottom: 20px;
  -ms-overflow-style: none;  /* IE/Edge */
  scrollbar-width: none;     /* Firefox */
}

.slider-container::-webkit-scrollbar {
  display: none; 
}

.slider-wrapper {
  display: flex;
  justify-content: flex-start; /* 왼쪽부터 붙이기 */
  padding: 0 20px;
  gap: 20px; /* slide 사이 간격도 가능 */
  width: max-content; /* 내용 너비만큼만 */
}


.slide-item {
  flex: 0 0 auto;
  width: 320px;
  padding: 10px;
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: transform 0.3s ease;

}

.slide-item:hover {
  transform: scale(1.2) !important;
  cursor: pointer;

}



.thumbnail {
  width: 280px;
  height: 158px;
  object-fit: cover;
  border-radius: 6px;
  margin-bottom: 6px;
}

.info {
  width: 280px;
}

.title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.views {
  font-size: 14px;
  color: #666;
}


.dark-mode h1,
.dark-mode h2,
.dark-mode h3,
.dark-mode p,
.dark-mode span,
.dark-mode li,
.dark-mode a {
  color: white;
}

.dark-mode .footer {
  background-color: #333;
  color: white;
}

.dark-mode .footer {
  color: #ccc;
}

.dark-mode .modal-content1,
.dark-mode .notice-card,
.dark-mode .calendar-wrapper,
.dark-mode .profile-box-content,
.dark-mode .card-grid {
  color: white;
}

.dark-mode .member-profile,
.dark-mode .member-img {
  border: 1px solid #333;
}

.dark-mode .rank-badge {
  color: #fff; /* 밝은 글자색 */
}
.dark-mode .rank-badge.top-highlight {
  background-color: #bb86fc; /* 강조 색상 (보라색 계열 예시) */
}

.dark-mode .rank-badge.top-regular {
  color: #333; /* 일반 색상 */
}
.dark-mode .rank-badge .info p {
  color: #ffffff; /* 일반 색상 */
}


</style>