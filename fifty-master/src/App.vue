<template>
  <router-view></router-view> <!-- 라우터가 해당 위치에 컴포넌트를 렌더링합니다 -->
</template>

<script lang="ts">
import { defineComponent } from 'vue';
// HomePage는 더 이상 이곳에서 임포트할 필요 없습니다.
// 라우터가 알아서 HomePage 컴포넌트를 렌더링하기 때문입니다.
//d
export default defineComponent({
  name: 'App',
});
</script>

<style>
.mainbody {
  color: white;
  min-height: 100vh;
}
#app {
  text-align: center;
}
</style>
