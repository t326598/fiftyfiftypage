<template>
  <!-- Sidebar -->
  <div class="sidebar">
    <div class="sidebar-header">
      <RouterLink to="/admin/schedule">
        <h2>관리</h2>
      </RouterLink>
    </div>
    <nav>
      <ul class="main-menu">
        <li
          @mouseover="handleMouseOver"
          @mouseout="handleMouseOut"
        >
          <h3>일정</h3>
          <ul class="sub-menu">
            <li>
              <RouterLink to="/admin/plan">
                일정 목록
              </RouterLink>
            </li>
          </ul>
        </li>
        <li
          @mouseover="handleMouseOver"
          @mouseout="handleMouseOut"
        >
          <h3>이미지 관리</h3>
          <ul class="sub-menu">
            <li>
              <RouterLink to="/admin/imageList">
                이미지 목록
              </RouterLink>
            </li>
          </ul>
        </li>
        <li
          @mouseover="handleMouseOver"
          @mouseout="handleMouseOut"
        >
          <h3>사용자 현황</h3>
          <ul class="sub-menu">
            <li>
              <RouterLink to="/admin/todayChart">
                사용자 현황
              </RouterLink>
            </li>
          </ul>
        </li>
        <li
          @mouseover="handleMouseOver"
          @mouseout="handleMouseOut"
        >
          <h3>공지사항</h3>
          <ul class="sub-menu">
            <li>
              <RouterLink to="/admin/noticeList">
                공지사항 목록
              </RouterLink>
            </li>
          </ul>
        </li>
        <li
          @mouseover="handleMouseOver"
          @mouseout="handleMouseOut"
        >
          <h3>프로필 관리</h3>
          <ul class="sub-menu">
            <li>
              <RouterLink to="/admin/profileList">
                프로필 설정
              </RouterLink>
            </li>
          </ul>
        </li>
        <li
          @mouseover="handleMouseOver"
          @mouseout="handleMouseOut"
        >
          <h3>달력 관리</h3>
          <ul class="sub-menu">
            <li>
              <RouterLink to="/admin/calendarList">
                달력 보기
              </RouterLink>
            </li>
          </ul>
        </li>
      </ul>
    </nav>
  </div>

  <!-- Slot for content -->
  <div class="main">
    <slot />
  </div>
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
    
const handleMouseOver = (e: MouseEvent) => {
  const subMenu = (e.currentTarget as HTMLElement).querySelector('.sub-menu')
  if (subMenu) subMenu.classList.add('active')
}

const handleMouseOut = (e: MouseEvent) => {
  const subMenu = (e.currentTarget as HTMLElement).querySelector('.sub-menu')
  if (subMenu && !subMenu.querySelector('a.current')) {
    subMenu.classList.remove('active')
  }
}

const updateCurrentMenu = () => {
  const currentUrl = route.path
  const menuLinks = document.querySelectorAll('.sub-menu a')
  menuLinks.forEach((link) => {
    const el = link as HTMLAnchorElement
    if (el.getAttribute('href') === currentUrl) {
      el.classList.add('current')
      el.closest('.sub-menu')?.classList.add('active')
    } else {
      el.classList.remove('current')
    }
  })
}

onMounted(updateCurrentMenu)
watch(() => route.path, updateCurrentMenu)

</script>

<style scoped>

/* Sidebar */
.sidebar * {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  list-style: none;
  font: inherit;
}

.sidebar a {
  text-decoration: none;
  color: black;
}

.sidebar {
  width: 280px;
  height: 90vh;
  border-right: 1px solid #D8D8D8;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #F7F8F9;
  top: 75px;
  left: 0;
}

.sidebar-header {
  display: flex;
  align-items: center;
  font-weight: bold;
  font-size: 22px;
  width: 250px;
  height: 100px;
  border-bottom: 1px solid #D8D8D8;
  padding-left: 20px;
}

.sidebar nav {
  width: 250px;
  margin-top: 20px;
  margin-left: 30px;
  padding-left: 20px;
}

.main-menu li {
  width: 150px;
  line-height: 60px;
}

.main-menu h3:hover {
  cursor: pointer;
  font-weight: bold;
  text-shadow: 2px 2px 5px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.sub-menu {
  margin-left: 30px;
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.5s ease-out;
}

.sub-menu.active {
  max-height: 300px;
  transition: max-height 0.5s ease-in;
}

.sub-menu li {
  line-height: 40px;
}

.sub-menu li:hover,
.current {
  font-weight: bold;
  text-shadow: 2px 2px 5px rgba(0, 0, 0, 0.3);
}

.main {
  margin-left: 280px;
  background-color: #F7F8F9;
}
</style>