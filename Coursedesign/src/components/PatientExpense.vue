<template>
  <div class="common-layout">
    <el-card class="page-card">
      <div class="page-header">
        <div class="page-title">费用统计</div>
        <el-button type="primary" @click="loadExpenses">刷新数据</el-button>
      </div>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="statistics-row">
        <el-col :span="8" :xs="24" :sm="12" :md="8">
          <el-card class="stat-card stat-paid">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon><Money /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">已收费费用</div>
                <div class="stat-value">¥{{ paidExpense.toFixed(2) }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8" :xs="24" :sm="12" :md="8">
          <el-card class="stat-card stat-count">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon><Tickets /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">已收费处方数</div>
                <div class="stat-value">{{ paidPrescriptionCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8" :xs="24" :sm="12" :md="8">
          <el-card class="stat-card stat-total">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">总处方数</div>
                <div class="stat-value">{{ prescriptionList.length }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 费用明细 -->
      <el-card class="expense-card" v-loading="loading">
        <template #header>
          <div class="card-header">
            <span>费用明细</span>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              @change="handleDateRangeChange"
            />
          </div>
        </template>

        <el-table 
          :data="filteredPrescriptionList" 
          style="width: 100%" 
          stripe
          border
        >
          <el-table-column prop="prescriptionDate" label="日期" width="180" align="center">
            <template #default="scope">
              {{ formatDate(scope.row.prescriptionDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="diagnosis" label="诊断" min-width="150" align="center" show-overflow-tooltip />
          <el-table-column prop="drugInfo" label="药品信息" min-width="200" align="center" show-overflow-tooltip />
          <el-table-column prop="totalFee" label="费用" width="120" align="center">
            <template #default="scope">
              <span class="fee">¥{{ (scope.row.totalFee || 0).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="statusType(scope.row.status)">
                {{ statusText(scope.row.status) }}
              </el-tag>
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
import { Money, Tickets, Document } from '@element-plus/icons-vue'

const loading = ref(false)
const prescriptionList = ref([])
const dateRange = ref(null)

const statusText = (status) => {
  if (status === 0) return '待收费'
  if (status === 1) return '已收费'
  if (status === 2) return '已发药'
  return '未知'
}

const statusType = (status) => {
  if (status === 0) return 'warning'
  if (status === 1) return 'primary'
  if (status === 2) return 'success'
  return 'info'
}

// 已收费或已发药的处方列表
const paidPrescriptionList = computed(() => {
  return prescriptionList.value.filter(item => item.status >= 1)
})

// 已收费处方数量
const paidPrescriptionCount = computed(() => {
  return paidPrescriptionList.value.length
})

// 计算已收费费用
const paidExpense = computed(() => {
  return filteredPaidPrescriptionList.value.reduce((sum, item) => {
    return sum + (parseFloat(item.totalFee) || 0)
  }, 0)
})

// 过滤后的已取药处方列表（用于日期筛选）
const filteredPaidPrescriptionList = computed(() => {
  let list = paidPrescriptionList.value
  
  if (dateRange.value && dateRange.value.length === 2) {
    const startDate = new Date(dateRange.value[0])
    const endDate = new Date(dateRange.value[1])
    endDate.setHours(23, 59, 59, 999)
    
    list = list.filter(item => {
      if (!item.prescriptionDate) return false
      const itemDate = new Date(item.prescriptionDate)
      return itemDate >= startDate && itemDate <= endDate
    })
  }
  
  return list
})

// 费用明细表格使用的列表（只显示已取药的处方）
const filteredPrescriptionList = computed(() => {
  return filteredPaidPrescriptionList.value
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

// 加载费用数据（只获取已取药的处方）
const loadExpenses = async (event) => {
  // 防止事件对象被传递
  if (event && typeof event === 'object' && event.preventDefault) {
    // 这是一个事件对象，忽略它
    event = undefined
  }
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

    if (!patientId) {
      ElMessage.warning('未找到您的患者信息')
      prescriptionList.value = []
      loading.value = false
      return
    }

    // 使用 patientId 查询处方（与 PatientPrescription.vue 相同的方法）
    const response = await axios.get('/getPrescriptionByPatientId', {
      params: { patientId: patientId }
    })
    
    if (response.data.code === 0) {
      const prescriptions = response.data.data || []
      // 按日期倒序排序
      prescriptionList.value = prescriptions.sort((a, b) => {
        const dateA = new Date(a.prescriptionDate || 0)
        const dateB = new Date(b.prescriptionDate || 0)
        return dateB - dateA
      })
    } else {
      ElMessage.error('加载费用数据失败：' + (response.data.msg || ''))
    }
  } catch (error) {
    console.error('获取费用数据失败:', error)
    ElMessage.error('加载费用失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 日期范围变化
const handleDateRangeChange = () => {
  // 自动触发计算属性更新
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

onMounted(() => {
  loadExpenses()
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

.statistics-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  transition: box-shadow 0.2s ease, border-color 0.2s ease;
}

.stat-card:hover {
  border-color: var(--his-border-strong);
  box-shadow: var(--his-shadow-md);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.stat-total .stat-icon {
  background: linear-gradient(135deg, #2f80ed 0%, #56ccf2 100%);
  color: white;
}

.stat-prescription .stat-icon {
  background: linear-gradient(135deg, #1b64c7 0%, #2f80ed 100%);
  color: white;
}

.stat-count .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #8fd3ff 100%);
  color: white;
}

.stat-paid .stat-icon {
  background: linear-gradient(135deg, #56ccf2 0%, #b5d7ff 100%);
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #6b7f95;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1f3b57;
}

.expense-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.fee {
  font-weight: 600;
  color: #f56c6c;
}
</style>

