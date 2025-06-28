<template>
  <div class="p-6 bg-gray-100 min-h-screen" style="margin-bottom: 100px;">
    <h2 class="text-2xl font-bold mb-4">방문자 통계 대시보드</h2>

    <!-- 요약 카드 -->
    <div class="grid grid-cols-3 gap-4 mb-8">
      <div class="bg-white p-4 rounded shadow">
        <p class="text-sm text-gray-600">총 방문자</p>
        <p class="text-2xl font-bold">{{ (totalStats.totalCount ?? 0) }}</p>
      </div>
      <div class="bg-white p-4 rounded shadow">
        <p class="text-sm text-gray-600">오늘 방문자</p>
        <p class="text-2xl font-bold">{{ (todayStats.totalCount ?? 0) }}</p>
      </div>
      <div class="bg-white p-4 rounded shadow">
        <p class="text-sm text-gray-600">오늘 재방문자</p>
        <p class="text-2xl font-bold">{{ ((todayStats.totalCount ?? 0) - (todayStats.uniqueCount ?? 0)) }}</p>
      </div>
    </div>

    <!-- 원형 차트 -->
    <div class="bg-white p-4 rounded shadow mb-8">
      <h3 class="text-lg font-semibold mb-2">재방문자 비율</h3>
      <canvas ref="pieChartRef"></canvas>
    </div>

    <!-- 라인 차트 -->
    <div class="bg-white p-4 rounded shadow">
      <h3 class="text-lg font-semibold mb-2">최근 7일 방문자 추이</h3>
      <canvas ref="lineChartRef"></canvas>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import Chart from 'chart.js/auto';
import axios from '@/api/today';

const todayStats = ref({ totalCount: 0, uniqueCount: 0 });
const totalStats = ref({ totalCount: 0, uniqueCount: 0 });
const weeklyStats = ref([]);

const start = new Date(Date.now() - 6 * 86400000).toISOString().slice(0, 10);
const end = new Date().toISOString().slice(0, 10);

const pieChartRef = ref(null);
const lineChartRef = ref(null);

let pieChartInstance = null;
let lineChartInstance = null;

onMounted(async () => {
  try {
    const todayRes = await axios.today();
    const totalRes = await axios.totalStats();
    const weekRes = await axios.stats(start, end);

    todayStats.value = todayRes.data ?? { totalCount: 0, uniqueCount: 0 };
    totalStats.value = totalRes.data ?? { totalCount: 0, uniqueCount: 0 };
    weeklyStats.value = Array.isArray(weekRes.data) ? weekRes.data : [];

    renderCharts();
  } catch (error) {
    console.error('통계 데이터를 불러오는 중 오류 발생:', error);
    todayStats.value = { totalCount: 0, uniqueCount: 0 };
    totalStats.value = { totalCount: 0, uniqueCount: 0 };
    weeklyStats.value = [];
    renderCharts();
  }
});

function renderCharts() {
  if (!pieChartRef.value || !lineChartRef.value) {
    console.warn('캔버스 엘리먼트를 찾을 수 없습니다.');
    return;
  }

  if (pieChartInstance) pieChartInstance.destroy();
  if (lineChartInstance) lineChartInstance.destroy();

  const reVisitors = (todayStats.value.totalCount ?? 0) - (todayStats.value.uniqueCount ?? 0);
  const newVisitors = todayStats.value.uniqueCount ?? 0;

  let pieData = [
    reVisitors > 0 ? reVisitors : 0,
    newVisitors > 0 ? newVisitors : 0
  ];
  if (pieData.every(v => v === 0)) {
    pieData = [1, 1];
  }

  pieChartInstance = new Chart(pieChartRef.value.getContext('2d'), {
    type: 'doughnut',
    data: {
      labels: ['재방문자', '신규 방문자'],
      datasets: [{
        data: pieData,
        backgroundColor: ['#f87171', '#60a5fa']
      }]
    },
    options: {
      responsive: true,
      plugins: {
        tooltip: {
          enabled: true,
          callbacks: {
            label(context) {
              const label = context.label || '';
              const value = context.parsed || 0;
              if (pieData.every(v => v === 1)) {
                return `${label}: 0`;
              }
              return `${label}: ${value}`;
            }
          }
        }
      }
    }
  });

  // 변경된 라벨 포맷: 날짜를 MM-DD 형태로 변환해서 라벨로 사용
  const labels = weeklyStats.value.length > 0
    ? weeklyStats.value.map(v => {
        const d = new Date(v.visitDate);
        return `${d.getMonth() + 1}-${d.getDate()}`;
      })
    : [new Date().toISOString().slice(5, 10)];

  const totalCounts = weeklyStats.value.length > 0
    ? weeklyStats.value.map(v => v.totalCount)
    : [0];

  const uniqueCounts = weeklyStats.value.length > 0
    ? weeklyStats.value.map(v => v.uniqueCount)
    : [0];

  lineChartInstance = new Chart(lineChartRef.value.getContext('2d'), {
    type: 'line',
    data: {
      labels,
      datasets: [{
        label: '총 방문자 수',
        data: totalCounts,
        borderColor: '#3b82f6',
        fill: false,
        tension: 0.3 // 부드러운 곡선
      }, {
        label: '신규 방문자 수',
        data: uniqueCounts,
        borderColor: '#10b981',
        fill: false,
        tension: 0.3
      }]
    },
    options: {
      responsive: true,
      scales: {  // 축 옵션 추가
        x: {
          display: true,
          title: {
            display: true,
            text: '날짜'
          }
        },
        y: {
          display: true,
          beginAtZero: true,
          title: {
            display: true,
            text: '방문자 수'
          }
        }
      },
      plugins: {
        legend: {
          position: 'top',
        },
        tooltip: {
          enabled: true
        }
      }
    }
  });
}
</script>

<style scoped>
canvas {
  margin: auto;
  width: 30%;
  height: 300px; 
}
</style>
