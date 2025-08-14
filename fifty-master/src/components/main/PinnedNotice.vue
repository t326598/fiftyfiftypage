<template>
  <div class="pinned-notice-wrapper">
    <h2 class="title">
      📌 사이트 이용방법
    </h2>
    <h4 style="color: red;">
      "공부 중인 사람으로 퀄리티가 좋지 못함을 미리 말씀드려용"
    </h4>
    <p
      class="description"
      style="font-size: 30px; color: blue;"
    >
      준비중인 페이지이니 혹시 발견하신 분들은 이렇게 있구나 하고 넘어가주세용..
    </p>
    <p
      class="description"
      style="font-size: 15px; color: black;"
    >
      서버비 무서웡..
    </p>
    <img
      src="@/assets/image/day&night.gif"
      alt="day&night"
      class="notice-image"
    >
    <p
      class="description"
      style="font-size: 30px;"
    >
      메인 Day 와 Night 이미지를 누르시면 각기 다른 컨셉의 배경을 만나보실 수 있어용
    </p>
    <img
      src="@/assets/image/calender.gif"
      alt="day&night"
      class="notice-image"
    >
    <p
      class="description"
      style="font-size: 30px;"
    >
      달력에서 피프티 일정들을 넣을 수 있어용 (안들어간 일정은 연락주시면..)
    </p>

    <img
      src="@/assets/image/video.gif"
      alt="video"
      class="notice-image"
    >
    <p
      class="description"
      style="font-size: 30px;"
    >
      조회수 탑 10 유튜브 영상들을 확인할 수 있고 해당 영상들은 매일 오전 9시 업데이트돼용
    </p>
    <img
      src="@/assets/image/profile.gif"
      alt="video"
      class="notice-image"
    >
    <p
      class="description"
      style="font-size: 30px;"
    >
      프로필 카드를 누르면 간단한 프로필과 이미지 필터를 사용하실 수 있어용
    </p>
    <img
      src="@/assets/image/kakaotalk.jpg"
      alt="kakaotalk"
      class="notice-image"
    >
    <p
      class="description"
      style="font-size: 30px;"
    >
      페이지 에러 각종 문의사항은 우측 하단 카카오톡으로 연락주세용
    </p>
  </div>
</template>

<script setup lang="ts">

</script>

<style scoped>
.pinned-notice-wrapper {
  text-align: center;
  padding: 20px;
}

.notice-image {
  max-width: 100%;
  margin: 16px auto;
  border-radius: 8px;
}

.description {
  font-size: 16px;
  line-height: 1.6;
  white-space: pre-line;
}
</style>
