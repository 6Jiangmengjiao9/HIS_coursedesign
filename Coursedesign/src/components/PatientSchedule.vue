<template>
  <div class="common-layout">
    <el-card class="page-card">
      <div class="page-header">
        <div class="page-title">医生排班查询</div>
        <el-button type="primary" @click="loadSchedules">刷新数据</el-button>
      </div>

      <el-row :gutter="16" class="filter-row" style="margin-bottom: 20px;">
        <el-col :span="8" :xs="24" :sm="12" :md="8">
          <el-select v-model="filterDepartmentId" placeholder="选择科室" clearable style="width: 100%;" @change="handleDepartmentChange">
            <el-option
              v-for="dept in departmentOptions"
              :key="dept.departmentId"
              :label="dept.departmentName"
              :value="dept.departmentId"
            />
          </el-select>
        </el-col>
        <el-col :span="8" :xs="24" :sm="12" :md="8">
          <el-select v-model="filterDoctorId" placeholder="选择医生" clearable style="width: 100%;">
            <el-option
              v-for="doc in filteredDoctorOptions"
              :key="doc.doctorId"
              :label="doc.name"
              :value="doc.doctorId"
            />
          </el-select>
        </el-col>
        <el-col :span="8" :xs="24" :sm="12" :md="8">
          <el-date-picker
            v-model="filterDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-col>
      </el-row>

      <el-card class="schedule-card">
        <el-table 
          :data="filteredScheduleList" 
          style="width: 100%" 
          v-loading="loading"
          element-loading-text="数据加载中..."
          stripe
          border
        >
          <el-table-column prop="scheduleDate" label="日期" width="120" align="center">
            <template #default="scope">
              {{ formatDate(scope.row.scheduleDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="doctorName" label="医生" width="120" align="center" />
          <el-table-column prop="departmentName" label="科室" width="120" align="center" />
          <el-table-column prop="shiftType" label="班次" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.shiftType === '上午' ? 'success' : 'warning'">
                {{ scope.row.shiftType }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="120" align="center" />
          <el-table-column prop="endTime" label="结束时间" width="120" align="center" />
          <el-table-column prop="roomNumber" label="诊室" width="100" align="center" />
          <el-table-column prop="maxPatients" label="最大预约数" width="120" align="center" />
          <el-table-column label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '可用' : '不可用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center" fixed="right">
            <template #default="scope">
              <el-button 
                type="primary" 
                size="small" 
                @click="goToReservation(scope.row)"
                :disabled="scope.row.status !== 1"
              >
                立即预约
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const scheduleList = ref([])
const doctorOptions = ref([])
const departmentOptions = ref([])

const filterDepartmentId = ref('')
const filterDoctorId = ref('')
const filterDate = ref('')

// 过滤后的医生选项
const filteredDoctorOptions = computed(() => {
  if (!filterDepartmentId.value) {
    return doctorOptions.value
  }
  return doctorOptions.value.filter(doc => doc.departmentId === filterDepartmentId.value)
})

// 过滤后的排班列表
const filteredScheduleList = computed(() => {
  return scheduleList.value.filter(s => {
    if (filterDepartmentId.value && s.departmentId !== filterDepartmentId.value) return false
    if (filterDoctorId.value && s.doctorId !== filterDoctorId.value) return false
    if (filterDate.value) {
      const filterDateStr = filterDate.value.split('T')[0]
      const scheduleDateStr = s.scheduleDate ? s.scheduleDate.split('T')[0] : ''
      if (filterDateStr !== scheduleDateStr) return false
    }
    return true
  })
})

// 加载科室和医生数据
const loadDepartmentsAndDoctors = async () => {
  try {
    const deptResponse = await axios.get('/getAllDepartment')
    if (deptResponse.data.code === 0) {
      const deptData = deptResponse.data.data
      if (typeof deptData === 'string') {
        // 解析Java对象字符串
        departmentOptions.value = parseJavaObjectString(deptData)
      } else {
        departmentOptions.value = deptData || []
      }
    }

    const docResponse = await axios.get('/getAllDoctor')
    if (docResponse.data.code === 0) {
      doctorOptions.value = docResponse.data.data || []
    }
  } catch (error) {
    console.error('加载科室和医生失败:', error)
  }
}

// 解析Java对象字符串
const parseJavaObjectString = (str) => {
  try {
    const departments = []
    const regex = /DepartmentFlat\{departmentId='([^']+)', departmentName='([^']+)', doctorId=(\d+), doctorName='([^']+)'\}/g
    let match
    const seen = new Set()
    
    while ((match = regex.exec(str)) !== null) {
      const deptId = match[1]
      const deptName = match[2]
      const doctorId = parseInt(match[3])
      const doctorName = match[4]
      
      const key = `${deptId}-${deptName}`
      if (!seen.has(key)) {
        seen.add(key)
        departments.push({
          departmentId: deptId,
          departmentName: deptName
        })
      }
      
      // 添加医生信息
      if (!doctorOptions.value.find(d => d.doctorId === doctorId)) {
        doctorOptions.value.push({
          doctorId: doctorId,
          name: doctorName,
          departmentId: deptId
        })
      }
    }
    
    return departments
  } catch (error) {
    console.error('解析失败:', error)
    return []
  }
}

// 加载排班列表
const loadSchedules = async () => {
  loading.value = true
  try {
    const response = await axios.get('/getAllSchedule')
    if (response.data.code === 0) {
      const list = response.data.data || []
      scheduleList.value = list.map(s => {
        const doc = doctorOptions.value.find(d => d.doctorId === s.doctorId)
        const dept = departmentOptions.value.find(d => d.departmentId === s.departmentId)
        return {
          ...s,
          doctorName: doc ? doc.name : `医生ID ${s.doctorId}`,
          departmentName: dept ? dept.departmentName : `科室ID ${s.departmentId}`
        }
      })
    } else {
      ElMessage.error('加载排班数据失败：' + (response.data.msg || ''))
    }
  } catch (error) {
    console.error('获取排班失败:', error)
    ElMessage.error('加载排班失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 科室变化时清空医生筛选
const handleDepartmentChange = () => {
  filterDoctorId.value = ''
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 跳转到预约页面
const goToReservation = (schedule) => {
  router.push('/patient/reservation')
  ElMessage.info('请在新预约中选择该医生和时段')
}

onMounted(() => {
  loadDepartmentsAndDoctors().then(() => {
    loadSchedules()
  })
})
</script>

<style scoped>
.common-layout {
  width: 100%;
  height: 100%;
  overflow: hidden;
  position: relative;
  box-sizing: border-box;
  max-width: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1f3b57;
}

.schedule-card {
  margin-top: 20px;
}
</style>

