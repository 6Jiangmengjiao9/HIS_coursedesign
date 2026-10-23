<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 当前医生 ID 和姓名
const myDoctorId = ref(null)
const myDoctorName = ref(localStorage.getItem('doctorName') || '')

// 全部排班数据
const scheduleList = ref([])

// 医生映射表：doctorId -> name
const doctorNameMap = ref({})

// 加载排班和医生信息
const loadSchedule = async () => {
  const doctorIdStr = localStorage.getItem('doctorId')
  if (!doctorIdStr) {
    ElMessage.warning('未找到医生信息，请重新登录')
    return
  }
  myDoctorId.value = parseInt(doctorIdStr)

  try {
    // 获取所有排班
    const schRes = await axios.get('/getAllSchedule')
    if (schRes.data.code === 0) {
      scheduleList.value = schRes.data.data || []
    } else {
      ElMessage.error('加载排班数据失败：' + (schRes.data.msg || ''))
    }

    // 获取医生列表，用于显示医生姓名
    try {
      const docRes = await axios.get('/getAllDoctor')
      if (docRes.data.code === 0) {
        const map = {}
        ;(docRes.data.data || []).forEach(d => {
          map[d.doctorId] = d.name
        })
        doctorNameMap.value = map
      }
    } catch (e) {
      console.error('加载医生列表失败:', e)
    }
  } catch (e) {
    console.error('加载排班失败:', e)
    ElMessage.error('加载排班失败，请稍后重试')
  }
}

// 工具：只比较日期部分
const sameDay = (d1, d2) => {
  const a = new Date(d1)
  const b = new Date(d2)
  return (
    a.getFullYear() === b.getFullYear() &&
    a.getMonth() === b.getMonth() &&
    a.getDate() === b.getDate()
  )
}

// 所有要展示的日期（按时间升序）
const displayDates = computed(() => {
  const set = new Set(
    scheduleList.value
      .filter(s => s.status === 1) // 只显示有效排班
      .map(s => s.scheduleDate)
      .filter(Boolean)
  )
  return Array.from(set).sort((a, b) => new Date(a) - new Date(b))
})

// 班次类型的顺序
const shiftOrder = { '上午': 1, '下午': 2, '夜班': 3 }

// 所有班次类型（按固定顺序）
const shiftTypes = computed(() => {
  const set = new Set(scheduleList.value.map(s => s.shiftType).filter(Boolean))
  const list = Array.from(set)
  return list.sort((a, b) => (shiftOrder[a] || 99) - (shiftOrder[b] || 99))
})

// 某一天、某班次下的所有排班
const getCellSchedules = (date, shift) => {
  return scheduleList.value
    .filter(s => s.status === 1)
    .filter(s => s.shiftType === shift && s.scheduleDate && sameDay(s.scheduleDate, date))
}

const formatDate = (d) => {
  const date = new Date(d)
  const weekMap = ['日', '一', '二', '三', '四', '五', '六']
  return `${(date.getMonth() + 1).toString().padStart(2, '0')}-${date
    .getDate()
    .toString()
    .padStart(2, '0')} 周${weekMap[date.getDay()]}`
}

onMounted(() => {
  loadSchedule()
})
</script>

<template>
  <div class="common-layout">
    <el-card class="page-card">
      <div class="page-header">
  <div>
          <div class="page-title">排班课表</div>
          <div class="page-subtitle">
            {{ myDoctorName || '医生' }} 的出诊排班（深色高亮为本人的排班）
          </div>
        </div>
        <el-button type="primary" @click="loadSchedule">刷新排班</el-button>
      </div>

      <div v-if="displayDates.length === 0" class="empty-tip">
        暂无排班数据。
      </div>

      <div v-else class="schedule-wrapper">
        <table class="schedule-table">
          <thead>
            <tr>
              <th class="time-col">班次 / 日期</th>
              <th v-for="d in displayDates" :key="d">
                {{ formatDate(d) }}
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="shift in shiftTypes" :key="shift">
              <td class="time-col">{{ shift }}</td>
              <td v-for="d in displayDates" :key="d">
                <div class="cell-content">
                  <div
                    v-for="s in getCellSchedules(d, shift)"
                    :key="s.scheduleId"
                    :class="['schedule-item', { mine: s.doctorId === myDoctorId }]"
                  >
                    <div class="line-1">
                      <span class="doctor-name">
                        {{ doctorNameMap[s.doctorId] || ('医生ID ' + s.doctorId) }}
                      </span>
                      <span class="room" v-if="s.roomNumber">· {{ s.roomNumber }}</span>
                    </div>
                    <div class="line-2">
                      <span class="time">
                        {{ (s.startTime || '').slice(0, 5) }} - {{ (s.endTime || '').slice(0, 5) }}
                      </span>
                      <span class="dept" v-if="s.departmentId">（{{ s.departmentId }}）</span>
                    </div>
                    <div class="line-3" v-if="s.maxPatients">
                      最多接诊 {{ s.maxPatients }} 人
                    </div>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.common-layout {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
}

.page-card {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.page-subtitle {
  margin-top: 4px;
  font-size: 13px;
  color: #3f5870;
}

.empty-tip {
  padding: 40px 0;
  text-align: center;
  color: #6b7f95;
}

.schedule-wrapper {
  overflow-x: auto;
}

.schedule-table {
  width: 100%;
  border-collapse: collapse;
  background-color: #fff;
}

.schedule-table th,
.schedule-table td {
  border: 1px solid #e6f0fa;
  padding: 8px;
  vertical-align: top;
}

.schedule-table th {
  background-color: #f5faff;
  font-weight: 600;
  text-align: center;
}

.time-col {
  width: 120px;
  text-align: center;
  font-weight: 600;
  background-color: #f5faff;
}

.cell-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-height: 40px;
}

.schedule-item {
  padding: 4px 6px;
  border-radius: 4px;
  background: #f5faff;
  font-size: 12px;
  color: #3f5870;
}

.schedule-item.mine {
  background: #ecf5ff;
  border: 1px solid #2f80ed;
  color: #1f3b57;
}

.line-1 {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 600;
}

.doctor-name {
  color: #1f3b57;
}

.schedule-item.mine .doctor-name {
  color: #2f80ed;
}

.line-2,
.line-3 {
  margin-top: 2px;
}

.time {
  color: #3f5870;
}

.room,
.dept {
  color: #6b7f95;
}
</style>

