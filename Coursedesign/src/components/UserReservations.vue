<template>
  <div class="common-layout">
    <el-card class="page-card">
      <div class="page-header">
        <div class="page-title">我的患者预约</div>
        <div class="header-actions">
          <el-button type="primary" @click="fetchReservations">刷新数据</el-button>
        </div>
      </div>

      <el-row :gutter="16" class="filter-row" style="margin-bottom: 8px;">
        <el-col :span="8" :xs="24" :sm="12" :md="10">
          <el-input
            v-model="searchKeyword"
            class="responsive-input"
            placeholder="请输入患者姓名或联系电话"
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
          <el-select v-model="visitFilter" placeholder="就诊状态" clearable style="width: 100%;">
            <el-option label="全部" value=""></el-option>
            <el-option label="未就诊" :value="0"></el-option>
            <el-option label="已就诊" :value="1"></el-option>
          </el-select>
        </el-col>
      </el-row>

      <el-card class="reservation-card">
        <el-table 
          :data="filteredReservations" 
          style="width: 100%" 
          v-loading="loading"
          element-loading-text="数据加载中..."
          stripe
          border
        >
          <el-table-column prop="patientName" label="患者姓名" width="120" align="center" />
          <el-table-column prop="patientAge" label="年龄" width="80" align="center" />
          <el-table-column prop="patientPhone" label="联系电话" width="130" align="center" />
          <el-table-column prop="reservationTime" label="预约时间" width="200" align="center">
            <template #default="scope">
              {{ formatReservationTime(scope.row.reservationTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="departmentName" label="科室" width="150" align="center" />
          <el-table-column label="预约状态" width="120" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.reservationStatus === 0 ? 'warning' : 'success'">
                {{ scope.row.reservationStatus === 0 ? '未预约' : '已预约' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="就诊状态" width="120" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.visitStatus === 0 ? 'info' : 'success'">
                {{ scope.row.visitStatus === 0 ? '未就诊' : '已就诊' }}
              </el-tag>
            </template>
          </el-table-column>
          <!-- 操作列 -->
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="scope">
              <el-button 
                type="success" 
                size="small" 
                style="margin-right: 5px;"
                @click="handleMarkVisited(scope.row)"
                :disabled="scope.row.visitStatus === 1"
                v-if="scope.row.reservationStatus === 1"
              >
                标记已就诊
              </el-button>
              <el-button 
                type="primary" 
                size="small" 
                style="margin-right: 5px;"
                @click="openEditReservationDialog(scope.row)"
              >
                编辑
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
            :total="totalReservations"
            @size-change="handlePageSizeChange"
            @current-change="handleCurrentPageChange"
          />
        </div>
      </el-card>
    </el-card>
  </div>
  
  <!-- 编辑预约对话框 -->
  <el-dialog v-model="editReservationDialogVisible" title="编辑预约" width="600px" @close="resetEditForm">
    <el-form :model="editReservationForm" label-width="100px" ref="editReservationFormRef">
      <el-form-item label="患者姓名" prop="patientName">
        <el-input v-model="editReservationForm.patientName" disabled />
      </el-form-item>
      
      <el-form-item label="患者年龄" prop="patientAge">
        <el-input-number v-model="editReservationForm.patientAge" :min="0" :max="150" style="width: 100%;" />
      </el-form-item>
      
      <el-form-item label="联系电话" prop="patientPhone">
        <el-input v-model="editReservationForm.patientPhone" />
      </el-form-item>
      
      <el-form-item label="预约状态" prop="reservationStatus">
        <el-select v-model="editReservationForm.reservationStatus" placeholder="请选择预约状态" style="width: 100%;">
          <el-option label="未预约" :value="0"></el-option>
          <el-option label="已预约" :value="1"></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="就诊状态" prop="visitStatus">
        <el-select v-model="editReservationForm.visitStatus" placeholder="请选择就诊状态" style="width: 100%;">
          <el-option label="未就诊" :value="0"></el-option>
          <el-option label="已就诊" :value="1"></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="预约时间" prop="reservationTime">
        <el-date-picker
          v-model="editReservationForm.reservationTime"
          type="datetime"
          placeholder="请选择预约时间"
          style="width: 100%;"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="editReservationDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEditReservation">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const reservations = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const visitFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(3)
const totalReservations = ref(0)

// 当前医生ID
const currentDoctorId = ref(null)

// 编辑预约对话框相关数据
const editReservationDialogVisible = ref(false)
const currentEditId = ref(null)
const editReservationForm = ref({
  patientName: '',
  patientAge: null,
  patientPhone: '',
  reservationStatus: 1,
  visitStatus: 0,
  reservationTime: ''
})

// 计算属性：过滤预约列表
const filteredReservations = computed(() => {
  let filtered = reservations.value
  
  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(item => 
      (item.patientName && item.patientName.toLowerCase().includes(keyword)) ||
      (item.patientPhone && item.patientPhone.toLowerCase().includes(keyword))
    )
  }
  
  // 预约状态过滤
  if (statusFilter.value !== '') {
    filtered = filtered.filter(item => 
      item.reservationStatus === parseInt(statusFilter.value)
    )
  }
  
  // 就诊状态过滤
  if (visitFilter.value !== '') {
    filtered = filtered.filter(item => 
      item.visitStatus === parseInt(visitFilter.value)
    )
  }
  
  return filtered
})

// 加载预约数据
const fetchReservations = async (page = currentPage.value, size = pageSize.value) => {
  loading.value = true
  try {
    // 获取当前医生ID
    const doctorId = localStorage.getItem('doctorId')
    if (!doctorId) {
      ElMessage.warning('未找到医生信息，请重新登录')
      loading.value = false
      return
    }
    
    currentDoctorId.value = parseInt(doctorId)
    
    // 确保参数是数字类型
    const pageNum = typeof page === 'number' ? page : parseInt(page) || 1
    const pageSizeNum = typeof size === 'number' ? size : parseInt(size) || 5
    
    const response = await axios.get('/getReservationPage', {
      params: {
        pageNum: pageNum,
        pageSize: pageSizeNum
      }
    })
    
    if (response.data.code === 0) {
      const allReservations = response.data.data || []
      // 根据当前医生ID过滤预约
      const myReservations = allReservations.filter(res => 
        res.doctorId === currentDoctorId.value
      )
      
      reservations.value = myReservations
      totalReservations.value = myReservations.length
      currentPage.value = response.data.pageNum || page
      pageSize.value = response.data.pageSize || size
    } else {
      ElMessage.error('加载预约数据失败：' + (response.data.msg || ''))
    }
  } catch (error) {
    console.error('获取预约数据失败:', error)
    ElMessage.error('数据加载失败，请稍后重试')
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

// 打开编辑预约对话框
const openEditReservationDialog = (row) => {
  currentEditId.value = row.id
  editReservationDialogVisible.value = true
  
  // 填充表单数据
  editReservationForm.value = {
    patientName: row.patientName,
    patientAge: row.patientAge,
    patientPhone: row.patientPhone,
    reservationStatus: row.reservationStatus,
    visitStatus: row.visitStatus,
    reservationTime: row.reservationTime
  }
}

// 重置编辑表单
const resetEditForm = () => {
  editReservationForm.value = {
    patientName: '',
    patientAge: null,
    patientPhone: '',
    reservationStatus: 1,
    visitStatus: 0,
    reservationTime: ''
  }
  currentEditId.value = null
}

// 提交编辑预约信息
const submitEditReservation = async () => {
  // 表单验证
  if (!editReservationForm.value.patientName || 
      editReservationForm.value.patientAge === null || 
      !editReservationForm.value.patientPhone || 
      !editReservationForm.value.reservationTime) {
    ElMessage.warning('请填写所有必填项')
    return
  }
  
  try {
    // 获取原始预约记录的医生和科室信息
    const originalReservation = reservations.value.find(r => r.id === currentEditId.value)
    if (!originalReservation) {
      ElMessage.error('未找到预约记录')
      return
    }
    
    const requestData = {
      id: currentEditId.value,
      departmentId: originalReservation.departmentId,
      doctorId: originalReservation.doctorId,
      patientName: editReservationForm.value.patientName,
      patientAge: parseInt(editReservationForm.value.patientAge),
      patientPhone: editReservationForm.value.patientPhone,
      reservationStatus: parseInt(editReservationForm.value.reservationStatus),
      visitStatus: parseInt(editReservationForm.value.visitStatus),
      reservationTime: editReservationForm.value.reservationTime
    }
    
    const response = await axios.post('/updateReservation', requestData, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    
    if (response.data.code === 0) {
      ElMessage.success('预约更新成功')
      editReservationDialogVisible.value = false
      fetchReservations()
    } else {
      ElMessage.error('预约更新失败: ' + response.data.msg)
    }
  } catch (error) {
    console.error('更新预约失败:', error)
    ElMessage.error('预约更新失败，请稍后重试')
  }
}

// 检查并添加患者
const checkAndAddPatient = async (patientName, patientAge, patientPhone) => {
  try {
    // 获取当前医生ID
    const doctorId = localStorage.getItem('doctorId')
    if (!doctorId) {
      console.warn('未找到医生信息')
      return false
    }

    // 获取所有患者，检查是否已存在
    const patientRes = await axios.get('/getAllPatient')
    if (patientRes.data.code === 0) {
      const patients = patientRes.data.data || []
      const currentDoctorId = parseInt(doctorId)
      
      // 检查患者是否已存在（通过姓名和电话匹配，且属于当前医生）
      const existingPatient = patients.find(p => 
        p.name === patientName && 
        p.contact === patientPhone &&
        p.doctorId === currentDoctorId
      )
      
      if (existingPatient) {
        // 患者已存在，不需要添加
        return true
      }
      
      // 患者不存在，添加为新患者
      const patientData = {
        name: patientName,
        age: patientAge || 0,
        contact: patientPhone,
        gender: '', // 预约信息中没有性别，留空
        address: '', // 预约信息中没有地址，留空
        idCard: '', // 预约信息中没有病历号，留空
        doctorId: currentDoctorId
      }
      
      const addRes = await axios.post('/addPatient', patientData, {
        headers: {
          'Content-Type': 'application/json'
        }
      })
      
      if (addRes.data.code === 0) {
        ElMessage.success('患者已自动添加为您的患者')
        return true
      } else {
        console.warn('添加患者失败:', addRes.data.msg)
        return false
      }
    }
    return false
  } catch (error) {
    console.error('检查并添加患者失败:', error)
    return false
  }
}

// 标记已就诊
const handleMarkVisited = async (row) => {
  try {
    await ElMessageBox.confirm('确定要将此预约标记为已就诊吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 获取原始预约记录
    const originalReservation = reservations.value.find(r => r.id === row.id)
    if (!originalReservation) {
      ElMessage.error('未找到预约记录')
      return
    }
    
    const requestData = {
      id: row.id,
      departmentId: originalReservation.departmentId,
      doctorId: originalReservation.doctorId,
      patientName: originalReservation.patientName,
      patientAge: originalReservation.patientAge,
      patientPhone: originalReservation.patientPhone,
      reservationStatus: originalReservation.reservationStatus,
      visitStatus: 1, // 标记为已就诊
      reservationTime: originalReservation.reservationTime
    }
    
    const response = await axios.post('/updateReservation', requestData, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    
    if (response.data.code === 0) {
      // 标记已就诊成功后，检查并添加患者
      await checkAndAddPatient(
        originalReservation.patientName,
        originalReservation.patientAge,
        originalReservation.patientPhone
      )
      
      ElMessage.success('已标记为已就诊')
      fetchReservations()
    } else {
      ElMessage.error('操作失败: ' + response.data.msg)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('标记已就诊失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

onMounted(() => {
  fetchReservations()
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
</style>
