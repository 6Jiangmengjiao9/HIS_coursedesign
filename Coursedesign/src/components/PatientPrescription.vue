<template>
  <div class="common-layout">
    <el-card class="page-card">
      <div class="page-header">
        <div class="page-title">我的处方</div>
        <el-button type="primary" @click="loadPrescriptions">刷新数据</el-button>
      </div>

      <el-row :gutter="16" class="filter-row" style="margin-bottom: 8px;">
        <el-col :span="8" :xs="24" :sm="12" :md="10">
          <el-input
            v-model="searchKeyword"
            class="responsive-input"
            placeholder="请输入诊断或药品信息"
            clearable
          />
        </el-col>
        <el-col :span="6" :xs="24" :sm="6" :md="5">
          <el-select v-model="statusFilter" placeholder="处方状态" clearable style="width: 100%;">
            <el-option label="全部" value=""></el-option>
            <el-option label="待收费" :value="0"></el-option>
            <el-option label="已收费" :value="1"></el-option>
            <el-option label="已发药" :value="2"></el-option>
          </el-select>
        </el-col>
      </el-row>

      <el-card class="prescription-card">
        <el-table 
          :data="pagedPrescriptionList" 
          style="width: 100%" 
          v-loading="loading"
          element-loading-text="数据加载中..."
          stripe
          border
        >
          <el-table-column prop="prescriptionDate" label="处方日期" width="180" align="center">
            <template #default="scope">
              {{ formatDate(scope.row.prescriptionDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="diagnosis" label="诊断" min-width="150" align="center" show-overflow-tooltip />
          <el-table-column prop="drugInfo" label="药品信息" min-width="200" align="center" show-overflow-tooltip>
            <template #default="scope">
              <div class="drug-info">{{ scope.row.drugInfo || '无' }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="totalFee" label="总费用" width="120" align="center">
            <template #default="scope">
              <span class="fee">¥{{ scope.row.totalFee || '0.00' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="处方状态" width="120" align="center">
            <template #default="scope">
              <el-tag :type="statusType(scope.row.status)">
                {{ statusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewPrescriptionDetail(scope.row)">
                查看详情
              </el-button>
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
            :total="filteredPrescriptionList.length"
            @size-change="handlePageSizeChange"
            @current-change="handleCurrentPageChange"
          />
        </div>
      </el-card>
    </el-card>

    <!-- 处方详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="处方详情" width="700px">
      <el-descriptions :column="1" border v-if="currentPrescription">
        <el-descriptions-item label="处方日期">
          {{ formatDateTime(currentPrescription.prescriptionDate) }}
        </el-descriptions-item>
        <el-descriptions-item label="诊断">
          <div class="prescription-detail-content">{{ currentPrescription.diagnosis || '未填写' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="药品信息">
          <div class="prescription-detail-content">{{ currentPrescription.drugInfo || '无' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="总费用">
          <span class="fee-large">¥{{ currentPrescription.totalFee || '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="处方状态">
          <el-tag :type="statusType(currentPrescription.status)">
            {{ statusText(currentPrescription.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const prescriptionList = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(3)
const totalPrescriptions = ref(0)

// 当前患者信息
const currentPatient = ref(null)

// 处方详情对话框
const detailDialogVisible = ref(false)
const currentPrescription = ref(null)

// 计算属性：过滤处方列表
const filteredPrescriptionList = computed(() => {
  let filtered = prescriptionList.value
  
  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(item => 
      (item.diagnosis && item.diagnosis.toLowerCase().includes(keyword)) ||
      (item.drugInfo && item.drugInfo.toLowerCase().includes(keyword))
    )
  }
  
  // 状态筛选
  if (statusFilter.value !== '') {
    filtered = filtered.filter(item => 
      item.status === parseInt(statusFilter.value)
    )
  }
  
  return filtered
})

// 分页后的处方列表
const pagedPrescriptionList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredPrescriptionList.value.slice(start, end)
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
    
    // 获取patientId
    const patientId = await getPatientIdByUserId(userId)
    
    currentPatient.value = {
      userId: userId,
      patientId: patientId,
      name: patientName,
      contact: patientContact
    }
  } catch (error) {
    console.error('获取患者信息失败:', error)
    ElMessage.error('获取患者信息失败，请稍后重试')
  }
}

// 加载处方数据
const loadPrescriptions = async (page = currentPage.value, size = pageSize.value) => {
  loading.value = true
  try {
    // 先获取患者信息
    await fetchCurrentPatient()
    
    if (!currentPatient.value) {
      ElMessage.warning('未找到患者信息，请重新登录')
      loading.value = false
      return
    }

    // 使用patientId查询处方
    const patientId = currentPatient.value.patientId

    if (!patientId) {
      ElMessage.warning('未找到您的患者信息，请重新登录')
      prescriptionList.value = []
      totalPrescriptions.value = 0
      loading.value = false
      return
    }

    // 使用 patientId 查询处方
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
      totalPrescriptions.value = prescriptionList.value.length
    } else {
      ElMessage.error('加载处方数据失败：' + (response.data.msg || ''))
    }
  } catch (error) {
    console.error('获取处方失败:', error)
    ElMessage.error('加载处方失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理每页数量变化
const handlePageSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

// 处理当前页变化
const handleCurrentPageChange = (val) => {
  currentPage.value = val
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

// 查看处方详情
const viewPrescriptionDetail = (row) => {
  currentPrescription.value = row
  detailDialogVisible.value = true
}

onMounted(() => {
  loadPrescriptions()
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

.prescription-card {
  margin-top: 20px;
}

.responsive-input {
  width: 100%;
}

.drug-info {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.5;
}

.fee {
  font-weight: 600;
  color: #f56c6c;
  font-size: 15px;
}

.fee-large {
  font-weight: 600;
  color: #f56c6c;
  font-size: 18px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.filter-row {
  margin-bottom: 16px;
}

.prescription-detail-content {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
  color: #3f5870;
}
</style>

