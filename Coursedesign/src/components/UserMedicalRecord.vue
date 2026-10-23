<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 当前医生名，只做显示用
const doctorName = ref(localStorage.getItem('doctorName') || '')

// 患者列表（当前医生名下）
const patientList = ref([])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(50)
const totalPatients = ref(0)

// 搜索关键词（按姓名 / 病历号 / 联系方式）
const searchKeyword = ref('')

// 病历详情 / 新增 / 编辑弹窗
const dialogVisible = ref(false)
const dialogMode = ref('view') // 'view' | 'add' | 'edit'

// 当前选择的患者
const currentPatient = ref(null)

// 当前选择的病历索引（一个患者可有多条病历）
const selectedCaseIndex = ref(0)

// 是否正在加载指定患者的病例列表
const loadingCases = ref(false)

// 当前正在查看/编辑的病历
const currentCase = computed(() => {
  if (!currentPatient.value) return null
  const list = currentPatient.value.caseList || []
  if (!list.length) return null
  return list[selectedCaseIndex.value] || list[0]
})

// 病历表单（用于新增）
const caseForm = ref({
  patientId: null,
  chiefComplaint: '',
  presentIllness: '',
  pastHistory: '',
  physicalExam: '',
  auxiliaryExam: '',
  diagnosis: ''
})

// 计算属性：过滤后的患者列表
const filteredPatientList = computed(() => {
  const kw = searchKeyword.value.trim().toLowerCase()
  if (!kw) return patientList.value
  return patientList.value.filter(p => {
    const name = (p.name || '').toLowerCase()
    const idCard = (p.idCard || '').toLowerCase()
    const contact = (p.contact || '').toLowerCase()
    return name.includes(kw) || idCard.includes(kw) || contact.includes(kw)
  })
})

// 加载当前医生名下患者 + 病历信息（分页）
const loadPatients = async (page = currentPage.value, size = pageSize.value) => {
  // 确保参数是数字类型
  const pageNum = typeof page === 'number' ? page : parseInt(page) || currentPage.value
  const pageSizeNum = typeof size === 'number' ? size : parseInt(size) || pageSize.value
  
  try {
    const doctorId = localStorage.getItem('doctorId')
    if (!doctorId) {
      ElMessage.warning('未找到医生信息，请重新登录')
      return
    }

    const res = await axios.get('/getPatientPage', {
      params: {
        pageNum: pageNum,
        pageSize: pageSizeNum
      }
    })
    if (res.data.code !== 0) {
      ElMessage.error('加载患者数据失败：' + (res.data.msg || ''))
      return
    }

    const currentDoctorId = parseInt(doctorId)
    // 只保留当前医生的患者，并将“一个患者多条病历”统一整理到 caseList 中
    patientList.value = res.data.data
      .filter(item => item.doctorId === currentDoctorId)
      .map(item => {
        // 后端可能返回：
        // 1) 单条病历：item.caseInfo
        // 2) 多条病历数组：item.caseList 或 item.caseInfoList
        let caseList = []
        if (Array.isArray(item.caseList)) {
          caseList = item.caseList
        } else if (Array.isArray(item.caseInfoList)) {
          caseList = item.caseInfoList
        } else if (item.caseInfo) {
          caseList = [item.caseInfo]
        }

        const latestCase = caseList[0] || null

        return {
          patientId: item.patientId,
          name: item.name,
          gender: item.gender,
          age: item.age,
          address: item.address,
          idCard: item.idCard,
          contact: item.contact,
          createTime: item.createTime,
          // 病历列表 & 最近一次病历（用于表格展示）
          caseList,
          latestCase,
          doctor: item.doctor || null
        }
      })

    // 更新分页信息
    totalPatients.value = res.data.total || 0
    currentPage.value = res.data.pageNum || page
    pageSize.value = res.data.pageSize || size
  } catch (e) {
    console.error('加载患者失败：', e)
    ElMessage.error('加载患者失败，请稍后重试')
  }
}

// 根据患者ID加载该患者的全部病例列表
const fetchCasesByPatientId = async (patientId) => {
  if (!patientId) return []
  loadingCases.value = true
  try {
    const res = await axios.get('/getCasesByPatientId', {
      params: { patientId }
    })
    if (res.data.code !== 0) {
      ElMessage.error('加载该患者病历失败：' + (res.data.msg || ''))
      return []
    }
    return Array.isArray(res.data.data) ? res.data.data : []
  } catch (e) {
    console.error('按患者ID加载病历失败：', e)
    ElMessage.error('加载该患者病历失败，请稍后重试')
    return []
  } finally {
    loadingCases.value = false
  }
}

// 分页事件处理
const handlePageSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadPatients()
}

const handleCurrentPageChange = (val) => {
  currentPage.value = val
  loadPatients()
}

// 打开“查看病历”弹窗（根据 patientId 调接口查全部病历）
const viewCase = async (row) => {
  const caseList = await fetchCasesByPatientId(row.patientId)
  if (!caseList.length) {
    ElMessage.warning('该患者暂无病历可查看')
    return
  }
  currentPatient.value = {
    ...row,
    caseList
  }
  selectedCaseIndex.value = 0 // 默认查看最新一条
  dialogMode.value = 'view'
  dialogVisible.value = true
}

// 打开“新增病历”弹窗
const openAddCase = (row) => {
  // 允许一个患者拥有多条病历，这里不再做“已有病历则禁止新增”的限制
  currentPatient.value = row
  dialogMode.value = 'add'
  selectedCaseIndex.value = 0
  caseForm.value = {
    patientId: row.patientId,
    chiefComplaint: '',
    presentIllness: '',
    pastHistory: '',
    physicalExam: '',
    auxiliaryExam: '',
    diagnosis: ''
  }
  dialogVisible.value = true
}

// 打开“编辑病历”弹窗（根据 patientId 调接口查全部病历）
const openEditCase = async (row) => {
  const caseList = await fetchCasesByPatientId(row.patientId)
  if (!caseList.length) {
    ElMessage.warning('该患者暂无病历可编辑')
    return
  }
  // 使用接口返回的完整病例列表，确保数据最新
  currentPatient.value = {
    ...row,
    caseList
  }
  // 默认编辑最新一条病历（这里约定接口按时间降序返回）
  selectedCaseIndex.value = 0
  const c = caseList[selectedCaseIndex.value]
  dialogMode.value = 'edit'
  caseForm.value = {
    patientId: row.patientId,
    chiefComplaint: c?.chiefComplaint || '',
    presentIllness: c?.presentIllness || '',
    pastHistory: c?.pastHistory || '',
    physicalExam: c?.physicalExam || '',
    auxiliaryExam: c?.auxiliaryExam || '',
    diagnosis: c?.diagnosis || ''
  }
  dialogVisible.value = true
}

// 提交新增 / 编辑病历
const submitCase = async () => {
  if (!caseForm.value.patientId) {
    ElMessage.warning('未找到患者信息，无法提交病历')
    return
  }
  if (!caseForm.value.chiefComplaint || !caseForm.value.diagnosis) {
    ElMessage.warning('请至少填写“主诉”和“诊断”')
    return
  }

  try {
    if (dialogMode.value === 'add') {
      // 新增病历
      const payload = {
        ...caseForm.value,
        entryTime: new Date().toISOString()
      }

      const res = await axios.post(
        '/addMedicalRecordByPatientId',
        payload,
        { headers: { 'Content-Type': 'application/json' } }
      )

      if (res.data.code === 0) {
        ElMessage.success(res.data.msg || '新增病历成功')
        dialogVisible.value = false
        await loadPatients() // 重新加载，刷新病例信息
      } else {
        ElMessage.error(res.data.msg || '新增病历失败')
      }
    } else if (dialogMode.value === 'edit') {
      // 编辑病历：调用 /updateMedicalRecordByPatientId，仅提交当前选中的 Case 信息
      const p = currentPatient.value
      const c = currentCase.value
      if (!c) {
        ElMessage.error('未找到需要编辑的病历记录')
        return
      }
      const payload = {
        recordId: c.recordId, // mapper 中别名 mr_id → Case.recordId
        patientId: p.patientId,
        chiefComplaint: caseForm.value.chiefComplaint,
        presentIllness: caseForm.value.presentIllness,
        pastHistory: caseForm.value.pastHistory,
        physicalExam: caseForm.value.physicalExam,
        auxiliaryExam: caseForm.value.auxiliaryExam,
        diagnosis: caseForm.value.diagnosis,
        entryTime: c.entryTime || new Date().toISOString()
      }

      const res = await axios.post(
        '/updateMedicalRecordByPatientId',
        payload,
        { headers: { 'Content-Type': 'application/json' } }
      )

      if (res.data.code === 0) {
        ElMessage.success(res.data.msg || '更新病历成功')
        dialogVisible.value = false
        await loadPatients()
      } else {
        ElMessage.error(res.data.msg || '更新病历失败')
      }
    }
  } catch (e) {
    console.error('病历操作失败：', e)
    ElMessage.error('操作失败，请稍后重试')
  }
}

onMounted(() => {
  loadPatients()
})
</script>

<template>
  <div class="common-layout">
    <el-card class="page-card">
      <div class="page-header">
        <div>
          <div class="page-title">病例录入与查询</div>
          <div class="page-subtitle">
            当前医生：
            <span class="doctor-name">{{ doctorName || '未知医生' }}</span>
          </div>
        </div>
      </div>

      <!-- 搜索栏 -->
      <el-row :gutter="16" class="filter-row">
        <el-col :span="8" :xs="24" :sm="12" :md="10">
          <el-input
            v-model="searchKeyword"
            class="responsive-input"
            placeholder="请输入患者姓名、病历号或联系方式"
            clearable
          />
        </el-col>
      </el-row>

      <!-- 患者 + 病历列表 -->
      <el-table :data="filteredPatientList" stripe style="width: 100%" border>
        <el-table-column prop="name" label="患者姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column
          prop="idCard"
          label="病历号"
          min-width="180"
          show-overflow-tooltip
        />
        <el-table-column
          prop="contact"
          label="联系方式"
          min-width="140"
          show-overflow-tooltip
        />
        <el-table-column
          label="最近主诉"
          min-width="200"
          show-overflow-tooltip
        >
          <template #default="scope">
            {{ scope.row.latestCase?.chiefComplaint || '暂无病历' }}
          </template>
        </el-table-column>
        <el-table-column
          label="最近诊断"
          min-width="200"
          show-overflow-tooltip
        >
          <template #default="scope">
            {{ scope.row.latestCase?.diagnosis || '暂无病历' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="viewCase(scope.row)"
              :disabled="!scope.row.caseList || !scope.row.caseList.length"
            >
              查看病历
            </el-button>
            <el-button
              type="success"
              size="small"
              @click="openAddCase(scope.row)"
            >
              新增病历
            </el-button>
            <el-button
              type="warning"
              size="small"
              @click="openEditCase(scope.row)"
              :disabled="!scope.row.caseList || !scope.row.caseList.length"
            >
              编辑病历
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
          :total="totalPatients"
          @size-change="handlePageSizeChange"
          @current-change="handleCurrentPageChange"
        />
      </div>
    </el-card>

    <!-- 病历详情 / 新增弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'view' ? '病历详情' : (dialogMode === 'add' ? '新增病历' : '编辑病历')"
      width="70%"
      top="5vh"
    >
      <template v-if="currentPatient">
        <div class="patient-info">
          <span>患者：{{ currentPatient.name }}</span>
          <span>性别：{{ currentPatient.gender }}</span>
          <span>年龄：{{ currentPatient.age }}</span>
          <span>病历号：{{ currentPatient.idCard }}</span>
        </div>

        <el-divider />

        <!-- 病历选择（一个患者多条病历，按主诉区分） -->
        <template v-if="currentPatient.caseList && currentPatient.caseList.length">
          <div class="case-selector">
            <span class="case-selector-label">选择病历（按主诉）：</span>
            <el-radio-group v-model="selectedCaseIndex" size="small">
              <el-radio-button
                v-for="(c, index) in currentPatient.caseList"
                :key="c.recordId || index"
                :label="index"
                :title="(c.chiefComplaint || '无主诉') + ' ｜ ' + (c.entryTime || '')"
              >
                <!-- 显示主诉的前若干字，便于区分不同病历 -->
                {{ (c.chiefComplaint && c.chiefComplaint.length > 12) ? (c.chiefComplaint.slice(0, 12) + '...') : (c.chiefComplaint || '无主诉') }}
              </el-radio-button>
            </el-radio-group>
          </div>

          <el-divider />
        </template>

        <!-- 查看模式：只读显示 -->
        <template v-if="dialogMode === 'view'">
          <el-descriptions
            title="当前病历"
            :column="1"
            border
            class="case-descriptions"
          >
            <el-descriptions-item label="主诉">
              {{ currentCase?.chiefComplaint || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="现病史">
              {{ currentCase?.presentIllness || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="既往史">
              {{ currentCase?.pastHistory || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="体格检查">
              {{ currentCase?.physicalExam || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="辅助检查">
              {{ currentCase?.auxiliaryExam || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="诊断">
              {{ currentCase?.diagnosis || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="录入时间">
              {{ currentCase?.entryTime || '暂无' }}
            </el-descriptions-item>
          </el-descriptions>
        </template>

        <!-- 新增模式：可编辑表单 -->
        <template v-else>
          <el-form
            :model="caseForm"
            label-width="120px"
            class="case-form"
          >
            <el-form-item label="主诉">
              <el-input
                v-model="caseForm.chiefComplaint"
                type="textarea"
                :rows="2"
                placeholder="请输入主诉（必填）"
              />
            </el-form-item>
            <el-form-item label="现病史">
              <el-input
                v-model="caseForm.presentIllness"
                type="textarea"
                :rows="3"
                placeholder="请输入现病史"
              />
            </el-form-item>
            <el-form-item label="既往史">
              <el-input
                v-model="caseForm.pastHistory"
                type="textarea"
                :rows="3"
                placeholder="请输入既往史"
              />
            </el-form-item>
            <el-form-item label="体格检查">
              <el-input
                v-model="caseForm.physicalExam"
                type="textarea"
                :rows="3"
                placeholder="请输入体格检查结果"
              />
            </el-form-item>
            <el-form-item label="辅助检查">
              <el-input
                v-model="caseForm.auxiliaryExam"
                type="textarea"
                :rows="3"
                placeholder="请输入辅助检查结果"
              />
            </el-form-item>
            <el-form-item label="诊断">
              <el-input
                v-model="caseForm.diagnosis"
                type="textarea"
                :rows="2"
                placeholder="请输入诊断（必填）"
              />
            </el-form-item>
          </el-form>
        </template>
      </template>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关 闭</el-button>
          <el-button
            v-if="dialogMode === 'add' || dialogMode === 'edit'"
            type="primary"
            @click="submitCase"
          >
            提 交
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.common-layout {
  background: transparent;
  width: 100%;
  height: 100%;
  overflow: hidden;
  position: relative;
  box-sizing: border-box;
  max-width: 100%;
}

.page-card {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 4px;
}

.page-subtitle {
  font-size: 13px;
  color: #6b7f95;
}

.doctor-name {
  color: #2f80ed;
  font-weight: 500;
}

.filter-row {
  margin-bottom: 16px;
}

.responsive-input {
  width: 100%;
}

.patient-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  color: #3f5870;
}

.case-form {
  max-height: 60vh;
  overflow-y: auto;
}

.case-descriptions {
  max-height: 60vh;
  overflow-y: auto;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
