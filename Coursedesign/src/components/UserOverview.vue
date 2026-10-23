<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 医生信息
const doctorName = ref(localStorage.getItem('doctorName') || '')

// 概览统计
const stats = ref({
  patientCount: 0,
  caseCount: 0,
  prescriptionCount: 0,
  todayReservations: 0 // 这里沿用字段名，实际含义为“近期预约数”
})

// 数据源
const patients = ref([])
const prescriptions = ref([])
const todayReservationList = ref([])

// 最近数据（简略展示）
const recentPatients = computed(() =>
  [...patients.value]
    .sort((a, b) => new Date(b.createTime || 0) - new Date(a.createTime || 0))
    .slice(0, 4)
)

const recentPrescriptions = computed(() =>
  [...prescriptions.value]
    .sort((a, b) => new Date(b.prescriptionDate || 0) - new Date(a.prescriptionDate || 0))
    .slice(0, 4)
)

// 工具：只比较日期部分
const isSameDate = (d1, d2) => {
  const dd1 = new Date(d1)
  const dd2 = new Date(d2)
  return (
    dd1.getFullYear() === dd2.getFullYear() &&
    dd1.getMonth() === dd2.getMonth() &&
    dd1.getDate() === dd2.getDate()
  )
}

const formatDateTime = (val) => {
  if (!val) return ''
  const d = new Date(val)
  return d.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatTime = (val) => {
  if (!val) return ''
  const d = new Date(val)
  return d.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载数据
const loadOverview = async () => {
  const doctorId = localStorage.getItem('doctorId')
  if (!doctorId) {
    ElMessage.warning('未找到医生信息，请重新登录')
    return
  }
  const did = parseInt(doctorId)

  try {
    // 患者 + 病例
    const patientRes = await axios.get('/getAllPatient')
    if (patientRes.data.code === 0) {
      patients.value = patientRes.data.data
        .filter(p => p.doctorId === did)
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
      params: { doctorId: did }
    })
    if (preRes.data.code === 0) {
      const raw = preRes.data.data || []
      prescriptions.value = raw.map(pre => {
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

    // 近期预约（按医生过滤 + 按时间倒序取最近 10 条）
    try {
      const resvRes = await axios.get('/getAllReservation')
      if (resvRes.data.code === 0) {
        const all = resvRes.data.data || []
        const list = all
          .filter(r => r.doctorId === did)
          .filter(r => r.reservationTime || r.createTime || r.visitTime)
          .sort((a, b) => {
            const ta = new Date(a.reservationTime || a.createTime || a.visitTime)
            const tb = new Date(b.reservationTime || b.createTime || b.visitTime)
            return tb - ta
          })
          .slice(0, 10)

        todayReservationList.value = list
        stats.value.todayReservations = list.length
      }
    } catch (e) {
      console.error('加载预约数据失败:', e)
    }
  } catch (e) {
    console.error('概览数据加载失败:', e)
    ElMessage.error('概览数据加载失败，请稍后重试')
  }
}

// 快捷跳转
const goTo = (path) => {
  router.push(path)
}

onMounted(() => {
  loadOverview()
})
</script>

<template>
  <div class="common-layout">
    <el-card class="page-card">
      <div class="page-header">
    <div>
          <div class="page-title">系统概述</div>
          <div class="page-subtitle">
            欢迎回来，{{ doctorName || '医生' }}。这里汇总了你近期的患者管理、病例与处方情况。
          </div>
        </div>
        <el-button type="primary" @click="loadOverview">刷新概览</el-button>
      </div>

      <!-- 概览统计 -->
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
            <div class="stat-value">{{ stats.todayReservations }}</div>
            <div class="stat-label">近期预约</div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 中间两列：今日预约 & 快捷入口 -->
      <el-row :gutter="16" style="margin-top: 16px;">
        <el-col :span="12" :xs="24">
          <el-card>
            <template #header>
              <div class="card-header">近期预约</div>
            </template>
            <div class="card-body-scroll">
              <el-table :data="todayReservationList" size="small" border>
                <el-table-column prop="patientName" label="患者" width="120" />
                <el-table-column prop="departmentName" label="科室" width="120" show-overflow-tooltip />
                <el-table-column prop="doctorName" label="医生" width="100" />
                <el-table-column label="时间" width="120">
                  <template #default="scope">
                    {{ formatTime(scope.row.reservationTime || scope.row.createTime) }}
                  </template>
                </el-table-column>
                <el-table-column prop="reservationStatus" label="状态" width="100">
                  <template #default="scope">
                    <el-tag type="success" v-if="scope.row.visitStatus === 1">已就诊</el-tag>
                    <el-tag type="warning" v-else>待就诊</el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-card>
        </el-col>

        <el-col :span="12" :xs="24">
          <el-card>
            <template #header>
              <div class="card-header">常用功能快捷入口</div>
            </template>
            <div class="quick-links">
              <el-button type="primary" plain @click="goTo('/user/patient')">
                患者信息管理
              </el-button>
              <el-button type="primary" plain @click="goTo('/user/medical-record')">
                病例录入与查询
              </el-button>
              <el-button type="primary" plain @click="goTo('/user/prescription')">
                处方管理
              </el-button>
              <el-button type="primary" plain @click="goTo('/user/diagnosis')">
                诊断与操作总览
              </el-button>
            </div>

            <el-divider />

            <div class="mini-block">
              <div class="mini-title">最近患者</div>
              <div class="mini-list-wrapper">
                <ul class="mini-list">
                  <li v-for="p in recentPatients" :key="p.patientId">
                    <span class="name">{{ p.name }}</span>
                    <span class="meta">
                      {{ p.gender }} / {{ p.age }}岁 · {{ formatDateTime(p.createTime) }}
                    </span>
                  </li>
                </ul>
              </div>
            </div>

            <div class="mini-block">
              <div class="mini-title">最近处方</div>
              <div class="mini-list-wrapper">
                <ul class="mini-list">
                  <li v-for="pre in recentPrescriptions" :key="pre.prescriptionId">
                    <span class="name">{{ pre.patientName }}</span>
                    <span class="meta">
                      {{ pre.diagnosis || '诊断未填写' }} · {{ formatDateTime(pre.prescriptionDate) }}
                    </span>
                  </li>
                </ul>
              </div>
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

.page-subtitle {
  margin-top: 4px;
  font-size: 13px;
  color: #3f5870;
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

.card-body-scroll {
  max-height: 260px;
  overflow-y: auto;
}

.quick-links {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}

.mini-block {
  margin-top: 8px;
}

.mini-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}

.mini-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.mini-list-wrapper {
  max-height: 180px;
  overflow-y: auto;
}

.mini-list li {
  display: flex;
  flex-direction: column;
  padding: 4px 0;
  border-bottom: 1px dashed #e6f0fa;
}

.mini-list li:last-child {
  border-bottom: none;
}

.mini-list .name {
  font-weight: 500;
}

.mini-list .meta {
  font-size: 12px;
  color: #6b7f95;
}
  </style>