<template>
  <div class="common-layout">
    <el-card class="page-card">
      <div class="page-header">
        <div class="page-title">我的就诊历史</div>
        <el-button type="primary" @click="loadHistory">刷新数据</el-button>
      </div>

      <el-row :gutter="16" class="filter-row" style="margin-bottom: 20px;">
        <el-col :span="8" :xs="24" :sm="12" :md="8">
          <el-select v-model="typeFilter" placeholder="记录类型" clearable style="width: 100%;">
            <el-option label="全部" value=""></el-option>
            <el-option label="预约记录" value="reservation"></el-option>
            <el-option label="病历记录" value="case"></el-option>
            <el-option label="处方记录" value="prescription"></el-option>
          </el-select>
        </el-col>
        <el-col :span="8" :xs="24" :sm="12" :md="8">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
            @change="handleDateRangeChange"
          />
        </el-col>
      </el-row>

      <el-card class="history-card" v-loading="loading" element-loading-text="数据加载中...">
        <div v-if="filteredHistory.length > 0" class="history-timeline-wrapper">
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in filteredHistory"
              :key="index"
              :timestamp="formatDateTime(item.time)"
              :type="getTimelineType(item.type)"
              placement="top"
            >
              <template #icon>
                <el-icon>
                  <AlarmClock v-if="item.type === 'reservation'" />
                  <Document v-else-if="item.type === 'case'" />
                  <Tickets v-else-if="item.type === 'prescription'" />
                  <Calendar v-else />
                </el-icon>
              </template>
              <el-card class="history-item-card" :class="`history-${item.type}`">
                <div class="history-item-header">
                  <el-tag :type="getTagType(item.type)" size="large">
                    {{ getTypeLabel(item.type) }}
                  </el-tag>
                  <span class="history-time">{{ formatDateTime(item.time) }}</span>
                </div>
                
                <!-- 预约记录 -->
                <div v-if="item.type === 'reservation'" class="history-content">
                  <div class="history-title">预约就诊</div>
                  <el-descriptions :column="2" border size="small">
                    <el-descriptions-item label="科室">{{ item.data.departmentName || '未知' }}</el-descriptions-item>
                    <el-descriptions-item label="医生">{{ item.data.doctorName || '未知' }}</el-descriptions-item>
                    <el-descriptions-item label="预约状态">
                      <el-tag :type="item.data.reservationStatus === 0 ? 'warning' : 'success'" size="small">
                        {{ item.data.reservationStatus === 0 ? '未预约' : '已预约' }}
                      </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="就诊状态">
                      <el-tag :type="item.data.visitStatus === 0 ? 'info' : 'success'" size="small">
                        {{ item.data.visitStatus === 0 ? '未就诊' : '已就诊' }}
                      </el-tag>
                    </el-descriptions-item>
                  </el-descriptions>
                </div>

                <!-- 病历记录 -->
                <div v-if="item.type === 'case'" class="history-content">
                  <div class="history-title">病历记录</div>
                  <el-descriptions :column="1" border size="small">
                    <el-descriptions-item label="主诉">
                      <div class="case-text">{{ item.data.chiefComplaint || '未填写' }}</div>
                    </el-descriptions-item>
                    <el-descriptions-item label="诊断">
                      <div class="case-text diagnosis-text">{{ item.data.diagnosis || '未填写' }}</div>
                    </el-descriptions-item>
                    <el-descriptions-item label="现病史" v-if="item.data.presentIllness">
                      <div class="case-text">{{ item.data.presentIllness }}</div>
                    </el-descriptions-item>
                  </el-descriptions>
                </div>

                <!-- 处方记录 -->
                <div v-if="item.type === 'prescription'" class="history-content">
                  <div class="history-title">处方开具</div>
                  <el-descriptions :column="2" border size="small">
                    <el-descriptions-item label="诊断" :span="2">
                      <div class="prescription-text">{{ item.data.diagnosis || '未填写' }}</div>
                    </el-descriptions-item>
                    <el-descriptions-item label="药品信息" :span="2">
                      <div class="prescription-text">{{ item.data.drugInfo || '无' }}</div>
                    </el-descriptions-item>
                    <el-descriptions-item label="总费用">
                      <span class="fee-text">¥{{ item.data.totalFee || '0.00' }}</span>
                    </el-descriptions-item>
                    <el-descriptions-item label="处方状态">
                      <el-tag :type="item.data.status === 0 ? 'warning' : (item.data.status === 1 ? 'primary' : 'success')" size="small">
                        {{ item.data.status === 0 ? '待收费' : (item.data.status === 1 ? '已收费' : '已发药') }}
                      </el-tag>
                    </el-descriptions-item>
                  </el-descriptions>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>

        <el-empty v-else description="暂无就诊记录" :image-size="200">
          <template #description>
            <p>您目前还没有就诊记录</p>
            <p class="empty-tip">就诊记录包括预约、病历和处方信息</p>
          </template>
        </el-empty>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Calendar, Document, Tickets, AlarmClock } from '@element-plus/icons-vue'

const loading = ref(false)
const typeFilter = ref('')
const dateRange = ref(null)

// 当前患者信息
const currentPatient = ref(null)

// 历史记录
const historyList = ref([])

// 计算属性：过滤后的历史记录
const filteredHistory = computed(() => {
  let filtered = historyList.value

  // 类型过滤
  if (typeFilter.value) {
    filtered = filtered.filter(item => item.type === typeFilter.value)
  }

  // 日期范围过滤
  if (dateRange.value && dateRange.value.length === 2) {
    const startDate = new Date(dateRange.value[0])
    const endDate = new Date(dateRange.value[1])
    endDate.setHours(23, 59, 59, 999) // 包含结束日期的整天

    filtered = filtered.filter(item => {
      const itemDate = new Date(item.time)
      return itemDate >= startDate && itemDate <= endDate
    })
  }

  return filtered
})

// 通过userId获取patientId
const getPatientIdByUserId = async (userId) => {
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
      
      return patient ? patient.patientId : null
    }
    
    return null
  } catch (error) {
    console.error('通过userId获取patientId失败:', error)
    return null
  }
}

// 加载就诊历史
const loadHistory = async () => {
  loading.value = true
  try {
    const userData = localStorage.getItem('userData')
    if (!userData) {
      ElMessage.warning('未找到用户信息，请重新登录')
      loading.value = false
      return
    }
    
    const data = JSON.parse(userData)
    const userId = data.id
    
    if (!userId) {
      ElMessage.warning('未找到用户ID')
      loading.value = false
      return
    }
    
    // 通过userId获取patientId
    const patientId = await getPatientIdByUserId(userId)

    const allHistory = []

    if (!patientId) {
      ElMessage.warning('未找到患者信息')
      historyList.value = []
      loading.value = false
      return
    }

    // 1. 加载预约记录
    try {
      const reservationRes = await axios.get('/getReservationPage', {
        params: { pageNum: parseInt(1), pageSize: parseInt(1000) }
      })
      if (reservationRes.data.code === 0) {
        const reservations = reservationRes.data.data || []
        // 通过patientId过滤预约（如果预约记录中有patientId字段）
        // 否则通过姓名和电话匹配（兼容旧数据）
        const myReservations = reservations.filter(res => {
          if (res.patientId && res.patientId === patientId) {
            return true
          }
          // 兼容旧数据：暂时保留，但主要使用patientId
          return false
        })
        myReservations.forEach(res => {
          allHistory.push({
            type: 'reservation',
            time: res.reservationTime,
            data: res
          })
        })
      }
    } catch (error) {
      console.error('加载预约记录失败:', error)
    }

    // 2. 加载病历记录
    try {
      const patientRes = await axios.get('/getAllPatient')
      if (patientRes.data.code === 0) {
        const patients = patientRes.data.data || []
        const patient = patients.find(p => p.patientId === patientId)
        if (patient && patient.caseInfo) {
          allHistory.push({
            type: 'case',
            time: patient.caseInfo.entryTime || patient.createTime,
            data: patient.caseInfo
          })
        }
      }
    } catch (error) {
      console.error('加载病历记录失败:', error)
    }

    // 3. 加载处方记录
    if (patientId) {
      try {
        const prescriptionRes = await axios.get('/getPrescriptionByPatientId', {
          params: { patientId: patientId }
        })
        if (prescriptionRes.data.code === 0) {
          const prescriptions = prescriptionRes.data.data || []
          prescriptions.forEach(pre => {
            allHistory.push({
              type: 'prescription',
              time: pre.prescriptionDate,
              data: pre
            })
          })
        }
      } catch (error) {
        console.error('加载处方记录失败:', error)
      }
    }

    // 按时间倒序排序
    historyList.value = allHistory.sort((a, b) => {
      const dateA = new Date(a.time || 0)
      const dateB = new Date(b.time || 0)
      return dateB - dateA
    })

  } catch (error) {
    console.error('加载就诊历史失败:', error)
    ElMessage.error('加载就诊历史失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 日期范围变化处理
const handleDateRangeChange = () => {
  // 过滤逻辑已在 computed 中实现
}

// 获取时间线类型
const getTimelineType = (type) => {
  const typeMap = {
    reservation: 'primary',
    case: 'success',
    prescription: 'warning'
  }
  return typeMap[type] || 'primary'
}

// 获取标签类型
const getTagType = (type) => {
  const typeMap = {
    reservation: 'primary',
    case: 'success',
    prescription: 'warning'
  }
  return typeMap[type] || 'info'
}

// 获取类型标签
const getTypeLabel = (type) => {
  const labelMap = {
    reservation: '预约记录',
    case: '病历记录',
    prescription: '处方记录'
  }
  return labelMap[type] || '未知'
}

// 格式化日期时间
const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadHistory()
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

.history-card {
  margin-top: 20px;
}

.history-timeline-wrapper {
  max-height: calc(100vh - 300px);
  overflow-y: auto;
  padding: 20px;
}

.history-item-card {
  margin-bottom: 10px;
  transition: all 0.3s;
}

.history-item-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.history-reservation {
  border-left: 4px solid #2f80ed;
}

.history-case {
  border-left: 4px solid #2f80ed;
}

.history-prescription {
  border-left: 4px solid #e6a23c;
}

.history-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.history-time {
  color: #6b7f95;
  font-size: 13px;
}

.history-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f3b57;
  margin-bottom: 12px;
}

.history-content {
  margin-top: 8px;
}

.case-text {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
  color: #3f5870;
}

.diagnosis-text {
  font-weight: 500;
  color: #1f3b57;
}

.prescription-text {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
  color: #3f5870;
}

.fee-text {
  font-weight: 600;
  color: #f56c6c;
  font-size: 15px;
}

.filter-row {
  margin-bottom: 16px;
}

.empty-tip {
  color: #6b7f95;
  font-size: 14px;
  margin-top: 10px;
}

/* 时间线样式优化 */
:deep(.el-timeline-item__timestamp) {
  color: #6b7f95;
  font-size: 13px;
}

:deep(.el-timeline-item__node) {
  width: 16px;
  height: 16px;
}
</style>

