<template>
  <div class="common-layout">
    <el-card class="page-card">
      <div class="page-header">
        <div class="page-title">我的病历</div>
        <el-button type="primary" @click="loadCaseInfo" :loading="loading">刷新数据</el-button>
      </div>

      <template v-if="caseList.length">
        <!-- 病历选择：同一患者多条病历，按主诉/时间区分 -->
        <div class="case-selector">
          <span class="case-selector-label">选择就诊记录：</span>
          <el-radio-group v-model="selectedCaseIndex" size="small">
            <el-radio-button
              v-for="(c, index) in caseList"
              :key="c.recordId || index"
              :label="index"
              :title="(c.chiefComplaint || '无主诉') + ' ｜ ' + (formatDate(c.entryTime) || '')"
            >
              {{ (c.chiefComplaint && c.chiefComplaint.length > 12)
                ? c.chiefComplaint.slice(0, 12) + '...'
                : (c.chiefComplaint || '无主诉') }}
            </el-radio-button>
          </el-radio-group>
        </div>

        <el-card class="case-card">
          <template #header>
            <div class="card-header">
              <span>病历信息</span>
              <el-tag type="success">已建立病历（共 {{ caseList.length }} 条）</el-tag>
            </div>
          </template>
          
          <el-descriptions :column="1" border>
            <el-descriptions-item label="主诉" :span="2">
              <div class="case-content">{{ currentCase?.chiefComplaint || '未填写' }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="现病史" :span="2">
              <div class="case-content">{{ currentCase?.presentIllness || '未填写' }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="既往史" :span="2">
              <div class="case-content">{{ currentCase?.pastHistory || '未填写' }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="体格检查" :span="2">
              <div class="case-content">{{ currentCase?.physicalExam || '未填写' }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="辅助检查" :span="2">
              <div class="case-content">{{ currentCase?.auxiliaryExam || '未填写' }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="诊断" :span="2">
              <div class="case-content diagnosis">{{ currentCase?.diagnosis || '未填写' }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="就诊时间" v-if="currentCase?.entryTime">
              {{ formatDate(currentCase.entryTime) }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </template>

      <el-empty v-else description="暂无病历信息" :image-size="200">
        <template #description>
          <p>您目前还没有病历记录</p>
          <p class="empty-tip">病历信息需要由医生在就诊时为您建立</p>
        </template>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 病历列表 & 当前选中的病历
const caseList = ref([])
const selectedCaseIndex = ref(0)
const currentCase = computed(() => {
  if (!caseList.value.length) return null
  return caseList.value[selectedCaseIndex.value] || caseList.value[0]
})
const loading = ref(false)
const currentPatient = ref(null)

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

// 加载病历信息
const loadCaseInfo = async () => {
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
      ElMessage.warning('未找到患者信息')
      caseInfo.value = null
      loading.value = false
      return
    }
    
    // 通过 patientId 获取该患者的全部病历列表
    const response = await axios.get('/getCasesByPatientId', {
      params: { patientId }
    })
    
    if (response.data.code === 0) {
      const list = Array.isArray(response.data.data) ? response.data.data : []
      caseList.value = list
      if (!list.length) {
        ElMessage.info('当前暂无病历记录')
      }
      selectedCaseIndex.value = 0
    } else {
      ElMessage.error('加载病历数据失败：' + (response.data.msg || ''))
      caseList.value = []
    }
  } catch (error) {
    console.error('获取病历失败:', error)
    ElMessage.error('加载病历失败，请稍后重试')
  } finally {
    loading.value = false
  }
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
  loadCaseInfo()
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

.case-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.case-content {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
  color: #3f5870;
}

.case-content.diagnosis {
  font-weight: 500;
  color: #1f3b57;
  font-size: 15px;
}

.empty-tip {
  color: #6b7f95;
  font-size: 14px;
  margin-top: 10px;
}
</style>

