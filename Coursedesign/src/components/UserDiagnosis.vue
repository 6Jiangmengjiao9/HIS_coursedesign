<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 概览数据
const stats = ref({
  patientCount: 0,
  prescriptionCount: 0,
  caseCount: 0
})

// 数据源
const patients = ref([])
const prescriptions = ref([])

// 最近操作时间线
const timeline = computed(() => {
  const items = []

  // 患者创建时间（如果有）
  patients.value.forEach(p => {
    if (p.createTime) {
      items.push({
        type: '患者',
        title: p.name,
        desc: '新增患者',
        time: p.createTime
      })
    }
    if (p.caseInfo?.entryTime) {
      items.push({
        type: '病例',
        title: p.name,
        desc: `录入病例：${p.caseInfo.diagnosis || ''}`,
        time: p.caseInfo.entryTime
      })
    }
  })

  // 处方
  prescriptions.value.forEach(pre => {
    if (pre.prescriptionDate) {
      items.push({
        type: '处方',
        title: pre.patientName || `处方ID ${pre.prescriptionId}`,
        desc: `开具处方：${(pre.drugInfo || '').slice(0, 24)}`,
        time: pre.prescriptionDate
      })
    }
  })

  return items
    .filter(i => i.time)
    .sort((a, b) => new Date(b.time) - new Date(a.time))
    .slice(0, 12)
})

// 最近数据
const recentPatients = computed(() =>
  [...patients.value]
    .sort((a, b) => new Date(b.createTime || 0) - new Date(a.createTime || 0))
    .slice(0, 6)
)

const recentPrescriptions = computed(() =>
  [...prescriptions.value]
    .sort((a, b) => new Date(b.prescriptionDate || 0) - new Date(a.prescriptionDate || 0))
    .slice(0, 6)
)

const recentCases = computed(() =>
  patients.value
    .filter(p => p.caseInfo)
    .sort((a, b) => new Date(b.caseInfo?.entryTime || 0) - new Date(a.caseInfo?.entryTime || 0))
    .slice(0, 6)
)

// 加载数据
const loadData = async () => {
  const doctorId = localStorage.getItem('doctorId')
  if (!doctorId) {
    ElMessage.warning('未找到医生信息，请重新登录')
    return
  }

  try {
    // 患者 + 病例（先加载患者，后面处方需要用到患者姓名）
    const patientRes = await axios.get('/getAllPatient')
    if (patientRes.data.code === 0) {
      const currentDoctorId = parseInt(doctorId)
      patients.value = patientRes.data.data
        .filter(p => p.doctorId === currentDoctorId)
        .map(p => ({
          ...p,
          caseInfo: p.caseInfo || null
        }))
      stats.value.patientCount = patients.value.length
      stats.value.caseCount = patients.value.filter(p => p.caseInfo).length
    } else {
      ElMessage.error('加载患者数据失败：' + (patientRes.data.msg || ''))
    }

    // 处方
    const preRes = await axios.get('/getPrescriptionByDoctorId', {
      params: { doctorId: parseInt(doctorId) }
    })
    if (preRes.data.code === 0) {
      const rawPres = preRes.data.data || []
      // 根据 patientId 关联患者姓名
      prescriptions.value = rawPres.map(pre => {
        const patient = patients.value.find(p => p.patientId === pre.patientId)
        return {
          ...pre,
          patientName: patient ? patient.name : pre.patientName || '未知患者'
        }
      })
      stats.value.prescriptionCount = prescriptions.value.length
    } else {
      ElMessage.error('加载处方数据失败：' + (preRes.data.msg || ''))
    }
  } catch (e) {
    console.error('加载数据失败:', e)
    ElMessage.error('加载数据失败，请稍后重试')
  }
}

const formatDate = (val) => {
  if (!val) return ''
  const d = new Date(val)
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="common-layout">
    <el-card class="page-card">
      <div class="page-header">
        <div class="page-title">诊断与操作总览</div>
        <el-button type="primary" @click="loadData">刷新数据</el-button>
      </div>

      <!-- 概览卡片 -->
      <el-row :gutter="16" class="overview-row">
        <el-col :span="6" :xs="12">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-value">{{ stats.patientCount }}</div>
            <div class="stat-label">我的患者</div>
          </el-card>
        </el-col>
        <el-col :span="6" :xs="12">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-value">{{ stats.caseCount }}</div>
            <div class="stat-label">病例记录</div>
          </el-card>
        </el-col>
        <el-col :span="6" :xs="12">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-value">{{ stats.prescriptionCount }}</div>
            <div class="stat-label">处方记录</div>
          </el-card>
        </el-col>
        <el-col :span="6" :xs="12">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-value">{{ timeline.length }}</div>
            <div class="stat-label">最近操作</div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" style="margin-top: 16px;">
        <!-- 最近处方 -->
        <el-col :span="12" :xs="24">
          <el-card>
            <template #header>
              <div class="card-header">最近处方</div>
            </template>
            <div class="card-body-scroll">
              <el-table :data="recentPrescriptions" size="small" border>
                <el-table-column prop="prescriptionId" label="处方ID" width="100" />
                <el-table-column prop="patientName" label="患者" width="120" />
                <el-table-column prop="diagnosis" label="诊断" min-width="160" show-overflow-tooltip />
                <el-table-column prop="drugInfo" label="药品" min-width="160" show-overflow-tooltip />
                <el-table-column label="日期" width="160">
                  <template #default="scope">
                    {{ formatDate(scope.row.prescriptionDate) }}
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-card>
        </el-col>

        <!-- 最近病例 -->
        <el-col :span="12" :xs="24">
          <el-card>
            <template #header>
              <div class="card-header">最近病例</div>
            </template>
            <div class="card-body-scroll">
              <el-table :data="recentCases" size="small" border>
                <el-table-column prop="name" label="患者" width="120" />
                <el-table-column prop="caseInfo.diagnosis" label="诊断" min-width="160" show-overflow-tooltip />
                <el-table-column prop="caseInfo.chiefComplaint" label="主诉" min-width="160" show-overflow-tooltip />
                <el-table-column label="录入时间" width="160">
                  <template #default="scope">
                    {{ formatDate(scope.row.caseInfo?.entryTime) }}
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" style="margin-top: 16px;">
        <!-- 最近患者 -->
        <el-col :span="12" :xs="24">
          <el-card>
            <template #header>
              <div class="card-header">最近新增患者</div>
            </template>
            <div class="card-body-scroll">
              <el-table :data="recentPatients" size="small" border>
                <el-table-column prop="name" label="患者" width="120" />
                <el-table-column prop="gender" label="性别" width="80" />
                <el-table-column prop="age" label="年龄" width="80" />
                <el-table-column prop="idCard" label="病历号" min-width="160" show-overflow-tooltip />
                <el-table-column label="创建时间" width="160">
                  <template #default="scope">
                    {{ formatDate(scope.row.createTime) }}
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-card>
        </el-col>

        <!-- 最近操作时间线 -->
        <el-col :span="12" :xs="24">
          <el-card>
            <template #header>
              <div class="card-header">最近操作</div>
            </template>
            <div class="timeline-wrapper">
              <el-timeline>
                <el-timeline-item
                  v-for="(item, idx) in timeline"
                  :key="idx"
                  :timestamp="formatDate(item.time)"
                  placement="top"
                >
                  <div class="timeline-item">
                    <span class="tag">{{ item.type }}</span>
                    <span class="title">{{ item.title }}</span>
                    <span class="desc">{{ item.desc }}</span>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-card>
        </el-col>
      </el-row>
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

.overview-row {
  margin-bottom: 8px;
}

.stat-card {
  text-align: center;
  height: 110px;
}

.stat-value {
  font-size: 30px;
  font-weight: 700;
  color: #2f80ed;
}

.stat-label {
  margin-top: 8px;
  color: #6b7f95;
}

.card-header {
  font-weight: 600;
}

.timeline-item {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.timeline-item .tag {
  background: #ecf5ff;
  color: #2f80ed;
  padding: 2px 8px;
  border-radius: 6px;
  font-size: 12px;
}

.timeline-item .title {
  font-weight: 600;
}

.timeline-item .desc {
  color: #666;
}

.timeline-wrapper {
  max-height: 260px;
  overflow-y: auto;
  padding-right: 4px;
}

.card-body-scroll {
  max-height: 260px;
  overflow-y: auto;
}
</style>