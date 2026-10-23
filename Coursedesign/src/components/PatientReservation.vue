<template>
  <div class="common-layout">
    <el-card class="page-card">
      <div class="page-header">
        <div class="page-title">我的预约</div>
        <div class="header-actions">
          <el-button type="primary" @click="fetchReservations">刷新数据</el-button>
          <el-button type="success" @click="openAddReservationDialog">新增预约</el-button>
        </div>
      </div>

      <el-row :gutter="16" class="filter-row" style="margin-bottom: 8px;">
        <el-col :span="8" :xs="24" :sm="12" :md="10">
          <el-input
            v-model="searchKeyword"
            class="responsive-input"
            placeholder="请输入医生姓名或科室名称"
            clearable
          />
        </el-col>
        <el-col :span="6" :xs="24" :sm="6" :md="5">
          <el-select v-model="statusFilter" placeholder="预约状态" clearable style="width: 100%;">
            <el-option label="全部" value=""></el-option>
            <el-option label="未预约" :value="0"></el-option>
            <el-option label="已预约" :value="1"></el-option>
          </el-select>
        </el-col>
        <el-col :span="6" :xs="24" :sm="6" :md="5">
          <el-select v-model="registerFilter" placeholder="挂号状态" clearable style="width: 100%;">
            <el-option label="全部" value=""></el-option>
            <el-option label="待就诊" :value="0"></el-option>
            <el-option label="已开方" :value="1"></el-option>
            <el-option label="已完成" :value="2"></el-option>
          </el-select>
        </el-col>
      </el-row>

      <el-card class="reservation-card">
        <el-table 
          :data="pagedReservations" 
          style="width: 100%" 
          v-loading="loading"
          element-loading-text="数据加载中..."
          stripe
          border
        >
          <el-table-column prop="reservationTime" label="预约时间" width="200" align="center">
            <template #default="scope">
              {{ formatReservationTime(scope.row.reservationTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="departmentName" label="科室" width="150" align="center" />
          <el-table-column prop="doctorName" label="医生" width="120" align="center" />
          <el-table-column label="预约状态" width="120" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.reservationStatus === 0 ? 'warning' : 'success'">
                {{ scope.row.reservationStatus === 0 ? '未预约' : '已预约' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="挂号状态" width="120" align="center">
            <template #default="scope">
              <el-tag :type="registerStatusType(scope.row.registerStatus)">
                {{ registerStatusText(scope.row.registerStatus) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页组件 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[3, 5, 10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="filteredReservations.length"
            @size-change="handlePageSizeChange"
            @current-change="handleCurrentPageChange"
          />
        </div>
      </el-card>
    </el-card>
  </div>
  
  <!-- 新增预约对话框 -->
  <el-dialog v-model="addReservationDialogVisible" title="新增预约" width="600px" @close="resetAddReservationForm">
    <el-form :model="addReservationForm" label-width="100px" ref="addReservationFormRef">
      <el-form-item label="科室" prop="departmentId" required>
        <el-select v-model="addReservationForm.departmentId" placeholder="请选择科室" style="width: 100%;" @change="handleDepartmentChange">
          <el-option
            v-for="dept in departmentOptions"
            :key="dept.value"
            :label="dept.label"
            :value="dept.value"
          ></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="医生" prop="doctorId" required>
        <el-select 
          v-model="addReservationForm.doctorId" 
          placeholder="请选择医生" 
          style="width: 100%;"
          :disabled="!addReservationForm.departmentId"
          @change="handleDoctorChange"
        >
          <el-option
            v-for="doctor in filteredDoctors"
            :key="doctor.doctorId"
            :label="doctor.departmentName ? `${doctor.departmentName}-${doctor.name}` : doctor.name"
            :value="doctor.doctorId"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="出诊班次" prop="scheduleId" required>
        <el-select
          v-model="addReservationForm.scheduleId"
          placeholder="请选择出诊班次"
          style="width: 100%;"
          :disabled="!addReservationForm.doctorId"
        >
          <el-option
            v-for="s in availableSchedules"
            :key="s.scheduleId"
            :label="formatScheduleOption(s)"
            :value="s.scheduleId"
          />
        </el-select>
        <div class="schedule-hint" v-if="addReservationForm.doctorId && availableSchedules.length === 0">
          该医生暂无可预约班次
        </div>
      </el-form-item>
      
      <el-form-item label="患者姓名" prop="patientName" required>
        <el-input v-model="addReservationForm.patientName" placeholder="请输入患者姓名" disabled />
      </el-form-item>
      
      <el-form-item label="患者年龄" prop="patientAge" required>
        <el-input-number v-model="addReservationForm.patientAge" :min="0" :max="150" style="width: 100%;" />
      </el-form-item>
      
      <el-form-item label="联系电话" prop="patientPhone" required>
        <el-input v-model="addReservationForm.patientPhone" placeholder="请输入联系电话" disabled />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="addReservationDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReservation">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const reservations = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const registerFilter = ref('')
const scheduleList = ref([])
const currentPage = ref(1)
const pageSize = ref(3)
const totalReservations = ref(0)

// 新增预约对话框相关数据
const addReservationDialogVisible = ref(false)
const addReservationForm = ref({
  departmentId: '',
  doctorId: '',
  scheduleId: null,
  patientName: '',
  patientAge: null,
  patientPhone: '',
  reservationStatus: 1,
  visitStatus: 0
})

// 科室选项
const departmentOptions = ref([])
// 所有医生数据（按科室分组）
const doctorsByDepartment = ref(new Map())

// 当前患者信息
const currentPatient = ref(null)

// 计算属性：过滤预约列表
const filteredReservations = computed(() => {
  let filtered = reservations.value
  
  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(item => 
      (item.doctorName && item.doctorName.toLowerCase().includes(keyword)) ||
      (item.departmentName && item.departmentName.toLowerCase().includes(keyword))
    )
  }
  
  // 预约状态过滤
  if (statusFilter.value !== '') {
    filtered = filtered.filter(item => 
      item.reservationStatus === parseInt(statusFilter.value)
    )
  }
  
  // 挂号状态过滤
  if (registerFilter.value !== '') {
    filtered = filtered.filter(item => 
      item.registerStatus === parseInt(registerFilter.value)
    )
  }
  
  return filtered
})

const pagedReservations = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredReservations.value.slice(start, start + pageSize.value)
})

// 根据选择的科室过滤医生
const filteredDoctors = computed(() => {
  if (!addReservationForm.value.departmentId) {
    return []
  }
  return doctorsByDepartment.value.get(addReservationForm.value.departmentId) || []
})

const availableSchedules = computed(() => {
  const doctorId = Number(addReservationForm.value.doctorId)
  if (!doctorId) return []
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return scheduleList.value
    .filter(item => Number(item.doctorId) === doctorId)
    .filter(item => item.status === 1 && Number(item.remainingSlots) > 0)
    .filter(item => {
      if (!item.scheduleDate) return false
      const date = new Date(item.scheduleDate)
      date.setHours(0, 0, 0, 0)
      return date >= today
    })
    .sort((a, b) => {
      const dateDiff = new Date(a.scheduleDate) - new Date(b.scheduleDate)
      if (dateDiff !== 0) return dateDiff
      return (a.startTime || '').localeCompare(b.startTime || '')
    })
})

// 加载预约数据
const fetchReservations = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/register-records/my')
    if (response.data.code === 0) {
      reservations.value = response.data.data || []
      totalReservations.value = reservations.value.length
      if (pagedReservations.value.length === 0 && currentPage.value > 1) {
        currentPage.value = 1
      }
    } else {
      ElMessage.error('加载预约数据失败：' + (response.data.msg || ''))
    }
  } catch (error) {
    console.error('获取预约数据失败:', error)
    ElMessage.error(error.response?.data?.msg || '数据加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理每页数量变化
const handlePageSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchReservations()
}

// 处理当前页变化
const handleCurrentPageChange = (val) => {
  currentPage.value = val
  fetchReservations()
}

// 格式化预约时间
const formatReservationTime = (time) => {
  if (!time) return '无预约时间'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 解析Java对象toString格式的字符串
const parseJavaObjectString = (javaString) => {
  try {
    // 处理整个数组字符串
    let cleanedString = javaString.trim()
    
    // 移除开头和结尾的方括号（如果有）
    if (cleanedString.startsWith('[') && cleanedString.endsWith(']')) {
      cleanedString = cleanedString.substring(1, cleanedString.length - 1)
    }
    
    // 分割各个对象
    const objectStrings = cleanedString.split('), ')
    const objects = []
    
    for (let objStr of objectStrings) {
      // 清理每个对象字符串
      objStr = objStr.trim()
      
      // 移除 DepartmentFlat( 前缀和 ) 后缀
      if (objStr.startsWith('DepartmentFlat(')) {
        objStr = objStr.substring('DepartmentFlat('.length)
      }
      if (objStr.endsWith(')')) {
        objStr = objStr.substring(0, objStr.length - 1)
      }
      
      // 解析键值对
      const keyValuePairs = objStr.split(', ')
      const obj = {}
      
      for (let pair of keyValuePairs) {
        const equalsIndex = pair.indexOf('=')
        if (equalsIndex > -1) {
          const key = pair.substring(0, equalsIndex)
          let value = pair.substring(equalsIndex + 1)
          
          // 处理空值
          if (value === 'null') {
            value = null
          }
          
          obj[key] = value
        }
      }
      
      objects.push(obj)
    }
    
    return objects
  } catch (err) {
    console.error('解析Java对象字符串失败:', err)
    return []
  }
}

// 从 getAllDepartment 接口获取所有科室和医生信息
const fetchAllDepartmentsAndDoctors = async () => {
  try {
    const response = await axios.get('/getAllDepartment')
    
    let rawData = []
    
    // 处理响应数据
    if (typeof response.data === 'string') {
      // 如果是字符串格式，尝试解析Java对象toString格式
      rawData = parseJavaObjectString(response.data)
      if (rawData.length === 0) {
        // 如果解析失败，尝试JSON解析
        try {
          rawData = JSON.parse(response.data)
        } catch (e) {
          console.warn('无法解析字符串格式的响应')
          ElMessage.warning('科室数据格式异常，请检查后端接口')
          return
        }
      }
    } else if (response.data && typeof response.data === 'object') {
      // 检查是否是标准Result格式
      if (response.data.code !== undefined && response.data.code === 0 && Array.isArray(response.data.data)) {
        rawData = response.data.data
      } else if (Array.isArray(response.data)) {
        rawData = response.data
      } else if (Array.isArray(response.data.data)) {
        rawData = response.data.data
      }
    }
    
    if (!Array.isArray(rawData) || rawData.length === 0) {
      console.warn('科室数据为空或格式不正确')
      ElMessage.warning('暂无科室数据')
      return
    }
    
    // 处理扁平化的科室-医生数据
    const departmentMap = new Map()
    const doctorMap = new Map()
    
    rawData.forEach(item => {
      const deptId = item.departmentId
      
      // 处理科室数据
      if (deptId && !departmentMap.has(deptId)) {
        departmentMap.set(deptId, {
          value: deptId,
          label: item.departmentName || '未知科室'
        })
      }
      
      // 处理医生数据
      if (item.doctorId && item.doctorName) {
        const doctor = {
          doctorId: parseInt(item.doctorId) || item.doctorId,
          name: item.doctorName,
          departmentId: deptId,
          departmentName: item.departmentName || '',
          title: item.title || ''
        }
        
        // 按科室分组存储医生
        if (!doctorMap.has(deptId)) {
          doctorMap.set(deptId, [])
        }
        
        // 避免重复添加同一医生
        const doctors = doctorMap.get(deptId)
        if (!doctors.find(d => d.doctorId === doctor.doctorId)) {
          doctors.push(doctor)
        }
      } else if (deptId && !doctorMap.has(deptId)) {
        // 即使没有医生，也要为科室创建空的医生数组
        doctorMap.set(deptId, [])
      }
    })
    
    // 转换为数组格式
    departmentOptions.value = Array.from(departmentMap.values())
    doctorsByDepartment.value = doctorMap
    
    console.log('科室选项:', departmentOptions.value)
    console.log('医生数据:', doctorsByDepartment.value)
    
  } catch (error) {
    console.error('获取科室和医生数据失败:', error)
    ElMessage.error('获取科室和医生数据失败，请稍后重试')
  }
}

const fetchSchedules = async () => {
  try {
    const response = await axios.get('/getAllSchedule')
    if (response.data.code === 0) {
      scheduleList.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取排班信息失败:', error)
    ElMessage.error('获取排班信息失败，请稍后重试')
  }
}

const formatScheduleDate = (value) => {
  if (!value) return ''
  const date = new Date(value)
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${date.getMonth() + 1}月${date.getDate()}日 ${weekdays[date.getDay()]}`
}

const formatScheduleOption = (schedule) => {
  const start = (schedule.startTime || '').slice(0, 5)
  const end = (schedule.endTime || '').slice(0, 5)
  return `${formatScheduleDate(schedule.scheduleDate)} ${schedule.shiftType} ${start}-${end}（余 ${schedule.remainingSlots}）`
}

const registerStatusText = (status) => {
  if (status === 0) return '待就诊'
  if (status === 1) return '已开方'
  if (status === 2) return '已完成'
  return '未知'
}

const registerStatusType = (status) => {
  if (status === 0) return 'warning'
  if (status === 1) return 'primary'
  if (status === 2) return 'success'
  return 'info'
}

// 通过userId获取患者档案
const getPatientByUserId = async (userId) => {
  if (!userId) return null
  
  try {
    // 先获取用户信息
    const userResponse = await axios.get('/getUserById', {
      params: { id: userId }
    })
    
    if (userResponse.data.code !== 0 || !userResponse.data.data) {
      return null
    }
    
    const user = userResponse.data.data
    const userPhone = user.phone || ''
    
    // 获取UserInfo信息
    const userInfoResponse = await axios.get('/getUserInfoByUserId', {
      params: { userId: userId }
    })
    
    const userRealname = userInfoResponse.data.code === 0 && userInfoResponse.data.data
      ? userInfoResponse.data.data.realname
      : user.username
    
    // 通过realname和phone匹配Patient表
    const patientRes = await axios.get('/getAllPatient')
    if (patientRes.data.code === 0) {
      const patients = patientRes.data.data || []
      const patient = patients.find(p => {
        const pName = (p.name || '').trim()
        const pContact = (p.contact || '').trim()
        const uName = (userRealname || '').trim()
        const uPhone = (userPhone || '').trim()
        
        if (uPhone) {
          return pName === uName && (pContact === uPhone || !pContact)
        } else {
          return pName === uName
        }
      })
      
      return patient || null
    }
    
    return null
  } catch (error) {
    console.error('通过userId获取患者档案失败:', error)
    return null
  }
}

// 获取当前患者信息（基于userId）
const fetchCurrentPatient = async () => {
  try {
    const userData = localStorage.getItem('userData')
    if (!userData) {
      ElMessage.warning('未找到用户信息，请重新登录')
      return
    }
    
    const data = JSON.parse(userData)
    const userId = data.id
    
    if (!userId) {
      ElMessage.warning('未找到用户ID')
      return
    }
    
    // 获取用户信息
    const userResponse = await axios.get('/getUserById', {
      params: { id: userId }
    })
    
    if (userResponse.data.code !== 0 || !userResponse.data.data) {
      ElMessage.warning('获取用户信息失败')
      return
    }
    
    const user = userResponse.data.data
    
    // 获取UserInfo信息
    const userInfoResponse = await axios.get('/getUserInfoByUserId', {
      params: { userId: userId }
    })
    
    const userInfo = userInfoResponse.data.code === 0 && userInfoResponse.data.data
      ? userInfoResponse.data.data
      : null
    
    const patientName = userInfo?.realname || user.username || ''
    const patientContact = user.phone || ''
    
    const patient = await getPatientByUserId(userId)
    
    currentPatient.value = {
      userId: userId,
      patientId: patient?.patientId || null,
      name: patient?.name || patientName,
      contact: patient?.contact || patientContact,
      age: patient?.age ?? null
    }
    
    addReservationForm.value.patientName = currentPatient.value.name
    addReservationForm.value.patientPhone = currentPatient.value.contact
    addReservationForm.value.patientAge = currentPatient.value.age
  } catch (error) {
    console.error('获取患者信息失败:', error)
    ElMessage.error('获取患者信息失败，请稍后重试')
  }
}

// 打开新增预约对话框
const openAddReservationDialog = () => {
  addReservationDialogVisible.value = true
  resetAddReservationForm()
  fetchCurrentPatient()
  fetchSchedules()
}

// 重置新增预约表单
const resetAddReservationForm = () => {
  addReservationForm.value = {
    departmentId: '',
    doctorId: '',
    scheduleId: null,
    patientName: currentPatient.value?.name || '',
    patientAge: currentPatient.value?.age ?? null,
    patientPhone: currentPatient.value?.contact || '',
    reservationStatus: 1,
    visitStatus: 0
  }
}

// 科室选择变化时清空医生和班次
const handleDepartmentChange = () => {
  addReservationForm.value.doctorId = ''
  addReservationForm.value.scheduleId = null
}

// 医生变化时清空班次
const handleDoctorChange = () => {
  addReservationForm.value.scheduleId = null
}

// 提交预约信息
const submitReservation = async () => {
  if (!currentPatient.value?.patientId) {
    ElMessage.warning('未找到您的患者档案，请联系管理员完善资料')
    return
  }
  if (!addReservationForm.value.departmentId
      || !addReservationForm.value.doctorId
      || !addReservationForm.value.scheduleId) {
    ElMessage.warning('请选择科室、医生和出诊班次')
    return
  }
  if (!addReservationForm.value.patientName
      || addReservationForm.value.patientAge === null
      || !addReservationForm.value.patientPhone) {
    ElMessage.warning('请完善患者信息')
    return
  }

  try {
    const requestData = {
      patientId: currentPatient.value.patientId,
      scheduleId: addReservationForm.value.scheduleId,
      departmentId: addReservationForm.value.departmentId,
      doctorId: parseInt(addReservationForm.value.doctorId),
      patientName: addReservationForm.value.patientName,
      patientAge: parseInt(addReservationForm.value.patientAge),
      patientPhone: addReservationForm.value.patientPhone,
      reservationStatus: 1,
      visitStatus: 0
    }

    const response = await axios.post('/api/register-records', requestData)
    if (response.data.code === 0) {
      ElMessage.success('挂号成功')
      addReservationDialogVisible.value = false
      fetchReservations()
      fetchSchedules()
    } else {
      ElMessage.error('挂号失败：' + (response.data.msg || '请稍后重试'))
    }
  } catch (error) {
    console.error('提交预约失败:', error)
    ElMessage.error(error.response?.data?.msg || '挂号失败，请稍后重试')
  }
}

onMounted(async () => {
  await fetchCurrentPatient()
  fetchReservations()
  fetchAllDepartmentsAndDoctors()
  fetchSchedules()
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

.reservation-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
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

.header-actions {
  display: flex;
  gap: 10px;
}

.responsive-input {
  width: 100%;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.filter-row {
  margin-bottom: 16px;
}

.schedule-hint {
  margin-top: 6px;
  font-size: 12px;
  color: var(--his-warning);
}
</style>
