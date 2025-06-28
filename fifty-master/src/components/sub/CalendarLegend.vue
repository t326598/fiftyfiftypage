<template>
  <div class="calendar-legend">
    <div
      v-for="(name, key) in categories"
      :key="key"
      class="legend-item"
      :style="{ backgroundColor: getCategoryColor(Number(key)) }"
    >
      {{ name }}
    </div>
  </div>
</template>

<script setup lang="ts">
const categories: Record<number, string> = {
  1: '음방',
  2: '자컨',
  3: '방송',
  4: '공연',
  5: '팬미팅',
  6: '곡발매',
}

function getCategoryColor(crt: number) {
  switch (crt) {
    case 1: return "#ff6b81";
    case 2: return "#4dabf7";
    case 3: return "#51cf66";
    case 4: return "#f4a261";
    case 5: return "#ff7f50";
    case 6: return "#8e44ad";
    default: return "#adb5bd";
  }
}
</script>

<style scoped>
.calendar-legend {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}
.legend-item {
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.9rem;
  user-select: none;
}
</style>
