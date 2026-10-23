<template>
  <div class="workspace">
    <header class="topbar">
      <div>
        <h2>收费工作站</h2>
        <p>当前用户：{{ username }}</p>
      </div>
      <el-button @click="logout">退出登录</el-button>
    </header>

    <el-card>
      <div class="toolbar">
        <span>待收费处方</span>
        <el-button type="primary" @click="loadData">刷新</el-button>
      </div>
      <el-table :data="prescriptions" v-loading="loading" stripe border>
        <el-table-column prop="prescriptionId" label="处方ID" width="90" />
        <el-table-column label="患者" width="140">
          <template #default="scope">{{ patientName(scope.row.patientId) }}</template>
        </el-table-column>
        <el-table-column label="医生" width="140">
          <template #default="scope">{{ doctorName(scope.row.doctorId) }}</template>
        </el-table-column>
        <el-table-column prop="diagnosis" label="诊断" min-width="160" />
        <el-table-column prop="drugInfo" label="药品明细" min-width="220" />
        <el-table-column label="金额" width="120">
          <template #default="scope">¥{{ money(scope.row.totalFee) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="success" size="small" @click="charge(scope.row)">收费</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && prescriptions.length === 0" description="暂无待收费处方" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = localStorage.getItem('username') || ''
const prescriptions = ref([])
const patients = ref([])
const doctors = ref([])
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const [pendingRes, patientRes, doctorRes] = await Promise.all([
      axios.get('/api/prescriptions/pending'),
      axios.get('/getAllPatient'),
      axios.get('/getAllDoctor')
    ])
    prescriptions.value = pendingRes.data.data || []
    patients.value = patientRes.data.data || []
    doctors.value = doctorRes.data.data || []
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '加载待收费处方失败')
  } finally {
    loading.value = false
  }
}

const patientName = (patientId) => {
  const patient = patients.value.find((item) => item.patientId === patientId)
  return patient ? patient.name : `患者#${patientId}`
}

const doctorName = (doctorId) => {
  const doctor = doctors.value.find((item) => item.doctorId === doctorId)
  if (!doctor) return `医生#${doctorId}`
  const department = doctor.department?.departmentName
  return department ? `${department}-${doctor.name}` : doctor.name
}

const money = (value) => Number(value || 0).toFixed(2)

const charge = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认收取处方 #${row.prescriptionId} 的费用 ¥${money(row.totalFee)}？`,
      '收费确认',
      { type: 'warning' }
    )
    await axios.post(`/api/charges/${row.prescriptionId}`)
    ElMessage.success('收费成功')
    await loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.msg || '收费失败')
    }
  }
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  localStorage.removeItem('username')
  localStorage.removeItem('userData')
  localStorage.removeItem('doctorName')
  localStorage.removeItem('doctorId')
  delete axios.defaults.headers.common.Authorization
  router.push('/login')
}

onMounted(loadData)
</script>

<style scoped>
.workspace {
  min-height: 100vh;
  padding: 24px;
  background: transparent;
  box-sizing: border-box;
}
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
  padding: 16px 22px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid var(--his-border);
  border-radius: var(--his-radius);
  box-shadow: var(--his-shadow-sm);
}
.topbar h2 {
  margin: 0 0 4px;
  color: var(--his-text);
}
.topbar p {
  margin: 0;
  color: var(--his-text-secondary);
  font-size: 13px;
}
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  font-weight: 600;
}
</style>
