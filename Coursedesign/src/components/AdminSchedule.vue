<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 排班列表
const scheduleList = ref([])

// 医生列表（用于显示姓名与选择）
const doctorOptions = ref([])

// 科室选项（可按需要调整为从接口获取）
const departmentOptions = ref([
  { value: 'KS001', label: '心血管内科' },
  { value: 'KS002', label: '骨科' },
  { value: 'KS003', label: '儿科' },
  { value: 'KS004', label: '神经内科' },
  { value: 'KS005', label: '妇产科' }
])

// 诊室选项（根据科室动态加载）
const consultingRoomOptions = ref([])

// 筛选
const filterDoctorId = ref('')
const filterDepartmentId = ref('')
const filterDate = ref('')

// 新增 / 编辑弹窗
const dialogVisible = ref(false)
const dialogMode = ref('add') // 'add' | 'edit'

// 当前编辑的排班
const scheduleForm = ref({
  scheduleId: null,
  doctorId: null,
  scheduleDate: '',
  shiftType: '',
  startTime: '',
  endTime: '',
  departmentId: '',
  roomNumber: '',
  maxPatients: null,
  status: 1
})

// 计算属性：带筛选的排班列表
const filteredScheduleList = computed(() => {
  return scheduleList.value.filter(s => {
    if (filterDoctorId.value && s.doctorId !== filterDoctorId.value) return false
    if (filterDepartmentId.value && s.departmentId !== filterDepartmentId.value) return false
    if (filterDate.value) {
      const d1 = new Date(filterDate.value)
      const d2 = new Date(s.scheduleDate)
      if (
        d1.getFullYear() !== d2.getFullYear() ||
        d1.getMonth() !== d2.getMonth() ||
        d1.getDate() !== d2.getDate()
      ) {
        return false
      }
    }
    return true
  })
})

// 加载医生列表
const loadDoctors = async () => {
  try {
    const res = await axios.get('/getAllDoctor')
    if (res.data.code === 0) {
      doctorOptions.value = (res.data.data || []).map(d => ({
        doctorId: d.doctorId,
        name: d.name,
        departmentId: d.departmentId,
        departmentName: d.department?.departmentName || ''
      }))
    }
  } catch (e) {
    console.error('加载医生列表失败:', e)
  }
}

// 加载排班列表
const loadSchedules = async () => {
  try {
    const res = await axios.get('/getAllSchedule')
    if (res.data.code === 0) {
      const list = res.data.data || []
      // 补充医生姓名
      scheduleList.value = list.map(s => {
        const doc = doctorOptions.value.find(d => d.doctorId === s.doctorId)
        return {
          ...s,
          doctorName: doc ? doc.name : `医生ID ${s.doctorId}`,
          departmentName: doc?.departmentName || ''
        }
      })
    } else {
      ElMessage.error('加载排班数据失败：' + (res.data.msg || ''))
    }
  } catch (e) {
    console.error('加载排班失败:', e)
    ElMessage.error('加载排班失败，请稍后重试')
  }
}

// 打开新增排班弹窗
const openAddSchedule = () => {
  dialogMode.value = 'add'
  scheduleForm.value = {
    scheduleId: null,
    doctorId: null,
    scheduleDate: '',
    shiftType: '',
    startTime: '',
    endTime: '',
    departmentId: '',
    roomNumber: '',
    maxPatients: null,
    status: 1
  }
  consultingRoomOptions.value = []
  dialogVisible.value = true
}

// 打开编辑排班弹窗
const openEditSchedule = (row) => {
  dialogMode.value = 'edit'
  scheduleForm.value = {
    scheduleId: row.scheduleId,
    doctorId: row.doctorId,
    scheduleDate: row.scheduleDate,
    shiftType: row.shiftType,
    startTime: row.startTime,
    endTime: row.endTime,
    departmentId: row.departmentId,
    roomNumber: row.roomNumber,
    maxPatients: row.maxPatients,
    status: row.status
  }
  // 根据已有科室加载诊室
  if (scheduleForm.value.departmentId) {
    loadConsultingRooms(scheduleForm.value.departmentId)
  } else {
    consultingRoomOptions.value = []
  }
  dialogVisible.value = true
}

// 加载某科室下的诊室列表
const loadConsultingRooms = async (departmentId) => {
  if (!departmentId) {
    consultingRoomOptions.value = []
    return
  }
  try {
    const res = await axios.get('/getConsultingRoomByDepartmentId', {
      params: { departmentId }
    })
    if (res.data.code === 0) {
      consultingRoomOptions.value = res.data.data || []
    } else {
      console.error('加载诊室失败：', res.data.msg)
    }
  } catch (e) {
    console.error('加载诊室失败：', e)
  }
}

// 提交排班（新增 / 编辑）
const submitSchedule = async () => {
  if (!scheduleForm.value.doctorId) {
    ElMessage.warning('请选择医生')
    return
  }
  if (!scheduleForm.value.scheduleDate || !scheduleForm.value.shiftType) {
    ElMessage.warning('请填写排班日期和班次')
    return
  }

  try {
    const payload = { ...scheduleForm.value }
    const url =
      dialogMode.value === 'add'
        ? '/addSchedule'
        : '/updateSchedule'

    const res = await axios.post(url, payload, {
      headers: { 'Content-Type': 'application/json' }
    })

    if (res.data.code === 0) {
      ElMessage.success(res.data.msg || (dialogMode.value === 'add' ? '新增排班成功' : '更新排班成功'))
      dialogVisible.value = false
      await loadSchedules()
    } else {
      ElMessage.error(res.data.msg || '操作失败')
    }
  } catch (e) {
    console.error('提交排班失败:', e)
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 删除排班
const deleteSchedule = async (scheduleId) => {
  try {
    const res = await axios.get('/deleteSchedule', {
      params: { scheduleId }
    })
    if (res.data.code === 0) {
      ElMessage.success('删除排班成功')
      await loadSchedules()
    } else {
      ElMessage.error(res.data.msg || '删除排班失败')
    }
  } catch (e) {
    console.error('删除排班失败:', e)
    ElMessage.error('删除排班失败')
  }
}

// 格式化日期
const formatDate = (val) => {
  if (!val) return ''
  const d = new Date(val)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 选择医生时，自动填充其所属科室（管理员仍可手动修改科室），并联动加载诊室
watch(
  () => scheduleForm.value.doctorId,
  (newVal) => {
    if (!newVal) return
    const doc = doctorOptions.value.find(d => d.doctorId === newVal)
    if (doc && !scheduleForm.value.departmentId) {
      scheduleForm.value.departmentId = doc.departmentId || ''
    }
    if (scheduleForm.value.departmentId) {
      loadConsultingRooms(scheduleForm.value.departmentId)
    }
  }
)

// 科室变更时，重新加载对应诊室列表
watch(
  () => scheduleForm.value.departmentId,
  (newVal) => {
    if (newVal) {
      loadConsultingRooms(newVal)
    } else {
      consultingRoomOptions.value = []
    }
  }
)

onMounted(async () => {
  await loadDoctors()
  await loadSchedules()
})
</script>

<template>
  <div class="common-layout">
    <el-card class="page-card">
      <div class="page-header">
        <div class="page-title">医生排班管理</div>
        <el-button type="success" @click="openAddSchedule">新增排班</el-button>
      </div>

      <!-- 筛选区域 -->
      <el-row :gutter="16" class="filter-row">
        <el-col :span="6" :xs="24" :sm="12" :md="6">
          <el-select
            v-model="filterDoctorId"
            placeholder="按医生筛选"
            clearable
            filterable
            style="width: 100%;"
          >
            <el-option
              v-for="doc in doctorOptions"
              :key="doc.doctorId"
              :label="doc.departmentName ? `${doc.departmentName}-${doc.name}` : doc.name"
              :value="doc.doctorId"
            />
          </el-select>
        </el-col>
        <el-col :span="6" :xs="24" :sm="12" :md="6">
          <el-select
            v-model="filterDepartmentId"
            placeholder="按科室筛选"
            clearable
            style="width: 100%;"
          >
            <el-option
              v-for="dept in departmentOptions"
              :key="dept.value"
              :label="dept.label"
              :value="dept.value"
            />
          </el-select>
        </el-col>
        <el-col :span="6" :xs="24" :sm="12" :md="6">
          <el-date-picker
            v-model="filterDate"
            type="date"
            placeholder="按日期筛选"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
            clearable
          />
        </el-col>
      </el-row>

      <!-- 排班列表 -->
      <el-table :data="filteredScheduleList" stripe border style="width: 100%;">
        <el-table-column prop="scheduleId" label="排班ID" width="90" />
        <el-table-column label="医生" width="160">
          <template #default="scope">
            {{ scope.row.departmentName ? `${scope.row.departmentName}-${scope.row.doctorName}` : scope.row.doctorName }}
          </template>
        </el-table-column>
        <el-table-column label="日期" width="140">
          <template #default="scope">
            {{ formatDate(scope.row.scheduleDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="shiftType" label="班次" width="90" />
        <el-table-column label="时间段" width="150">
          <template #default="scope">
            {{ (scope.row.startTime || '').slice(0, 5) }} - {{ (scope.row.endTime || '').slice(0, 5) }}
          </template>
        </el-table-column>
        <el-table-column prop="roomNumber" label="诊室" width="100" />
        <el-table-column prop="maxPatients" label="最大接诊人数" width="130" />
        <el-table-column prop="remainingSlots" label="剩余号源" width="110" />
        <el-table-column label="状态" width="90">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
              {{ scope.row.status === 1 ? '有效' : '取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="openEditSchedule(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="deleteSchedule(scope.row.scheduleId)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增 / 编辑 排班弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '新增排班' : '编辑排班'"
      width="60%"
      top="5vh"
    >
      <el-form
        :model="scheduleForm"
        label-width="120px"
        style="max-height: 60vh; overflow-y: auto;"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="医生" required>
              <el-select
                v-model="scheduleForm.doctorId"
                placeholder="请选择医生"
                filterable
                style="width: 100%;"
              >
                <el-option
                  v-for="doc in doctorOptions"
                  :key="doc.doctorId"
                  :label="doc.departmentName ? `${doc.departmentName}-${doc.name}` : doc.name"
                  :value="doc.doctorId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出诊科室" required>
              <el-select
                v-model="scheduleForm.departmentId"
                placeholder="请选择科室"
                style="width: 100%;"
              >
                <el-option
                  v-for="dept in departmentOptions"
                  :key="dept.value"
                  :label="dept.label"
                  :value="dept.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排班日期" required>
              <el-date-picker
                v-model="scheduleForm.scheduleDate"
                type="date"
                placeholder="请选择日期"
                style="width: 100%;"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班次" required>
              <el-select
                v-model="scheduleForm.shiftType"
                placeholder="请选择班次"
                style="width: 100%;"
              >
                <el-option label="上午" value="上午" />
                <el-option label="下午" value="下午" />
                <el-option label="夜班" value="夜班" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-time-picker
                v-model="scheduleForm.startTime"
                placeholder="开始时间"
                style="width: 100%;"
                format="HH:mm"
                value-format="HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-time-picker
                v-model="scheduleForm.endTime"
                placeholder="结束时间"
                style="width: 100%;"
                format="HH:mm"
                value-format="HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="诊室">
              <el-select
                v-model="scheduleForm.roomNumber"
                placeholder="请选择诊室"
                style="width: 100%;"
                clearable
              >
                <el-option
                  v-for="room in consultingRoomOptions"
                  :key="room.roomId || room.roomNumber"
                  :label="room.roomNumber || room.roomName || ('诊室ID ' + room.roomId)"
                  :value="room.roomNumber || room.roomName || room.roomId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大接诊人数">
              <el-input
                v-model="scheduleForm.maxPatients"
                type="number"
                placeholder="不填则不限制"
                :min="0"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="状态">
          <el-select v-model="scheduleForm.status" style="width: 100%;">
            <el-option label="有效" :value="1" />
            <el-option label="取消" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitSchedule">确 定</el-button>
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
}

.filter-row {
  margin-bottom: 16px;
}
</style>

