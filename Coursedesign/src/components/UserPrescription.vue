<template>
  <div class="prescription-page">
    <el-card>
      <div class="page-header">
        <div>
          <h2>处方开具与管理</h2>
          <p>当前医生：{{ doctorName || '未知医生' }}</p>
        </div>
        <div>
          <el-button @click="loadAll">刷新</el-button>
          <el-button type="primary" @click="openCreate">新增处方</el-button>
        </div>
      </div>

      <el-table :data="prescriptions" v-loading="loading" stripe border>
        <el-table-column prop="prescriptionId" label="处方ID" width="90" />
        <el-table-column prop="patientId" label="患者ID" width="90" />
        <el-table-column prop="diagnosis" label="诊断" min-width="150" />
        <el-table-column prop="drugInfo" label="药品明细" min-width="220" />
        <el-table-column label="金额" width="120">
          <template #default="scope">¥{{ money(scope.row.totalFee) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="scope">
            <el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
            <el-button
              size="small"
              type="danger"
              :disabled="scope.row.status !== 0"
              @click="removePrescription(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && prescriptions.length === 0" description="暂无处方" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增处方" width="720px">
      <el-form label-width="100px">
        <el-form-item label="挂号单" required>
          <el-select v-model="form.registerId" placeholder="请选择待接诊挂号单" style="width: 100%">
            <el-option
              v-for="item in registers"
              :key="item.id"
              :label="`${item.patientName}（${item.departmentName || ''} ${formatDate(item.reservationTime)}）`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="诊断" required>
          <el-input v-model="form.diagnosis" type="textarea" :rows="2" placeholder="请输入诊断结果" />
        </el-form-item>
        <el-form-item label="药品明细" required>
          <div class="items">
            <div v-for="(item, index) in form.items" :key="index" class="item-row">
              <el-select v-model="item.medicineId" filterable placeholder="选择药品" style="flex: 2">
                <el-option
                  v-for="drug in drugs"
                  :key="drug.drugId"
                  :label="`${drug.drugName}（库存 ${drug.stockQuantity}，¥${money(drug.unitPrice)}）`"
                  :value="drug.drugId"
                  :disabled="drug.status !== 1"
                />
              </el-select>
              <el-input-number v-model="item.quantity" :min="1" :max="999" style="flex: 1" />
              <el-button type="danger" plain @click="removeItem(index)" :disabled="form.items.length === 1">
                删除
              </el-button>
            </div>
            <el-button type="primary" plain @click="addItem">添加药品</el-button>
          </div>
        </el-form-item>
        <el-alert
          type="info"
          :closable="false"
          :title="`预计金额：¥${money(previewTotal)}（最终金额由后端按药品档案单价计算）`"
        />
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">提交处方</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="处方详情" width="620px">
      <el-descriptions v-if="currentPrescription" :column="1" border>
        <el-descriptions-item label="处方ID">{{ currentPrescription.prescriptionId }}</el-descriptions-item>
        <el-descriptions-item label="诊断">{{ currentPrescription.diagnosis || '无' }}</el-descriptions-item>
        <el-descriptions-item label="金额">¥{{ money(currentPrescription.totalFee) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(currentPrescription.status) }}</el-descriptions-item>
        <el-descriptions-item label="药品">
          <div v-for="item in currentPrescription.items || []" :key="item.itemId">
            {{ item.medicineName }} × {{ item.quantity }}（¥{{ money(item.unitPrice) }}）
          </div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const doctorName = ref(localStorage.getItem('doctorName') || '')
const doctorId = Number(localStorage.getItem('doctorId') || 0)
const prescriptions = ref([])
const registers = ref([])
const drugs = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const currentPrescription = ref(null)

const form = ref({
  registerId: null,
  diagnosis: '',
  items: [{ medicineId: null, quantity: 1 }]
})

const previewTotal = computed(() => {
  return form.value.items.reduce((sum, item) => {
    const drug = drugs.value.find((row) => row.drugId === item.medicineId)
    return sum + (drug ? Number(drug.unitPrice || 0) * Number(item.quantity || 0) : 0)
  }, 0)
})

const loadAll = async () => {
  loading.value = true
  try {
    const [prescriptionRes, registerRes, drugRes] = await Promise.all([
      axios.get('/getPrescriptionByDoctorId', { params: { doctorId } }),
      axios.get('/api/register-records/mine', { params: { registerStatus: 0 } }),
      axios.get('/getAllDrug')
    ])
    prescriptions.value = prescriptionRes.data.data || []
    registers.value = registerRes.data.data || []
    drugs.value = drugRes.data.data || []
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '加载处方数据失败')
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  form.value = {
    registerId: null,
    diagnosis: '',
    items: [{ medicineId: null, quantity: 1 }]
  }
  dialogVisible.value = true
}

const addItem = () => {
  form.value.items.push({ medicineId: null, quantity: 1 })
}

const removeItem = (index) => {
  form.value.items.splice(index, 1)
}

const submit = async () => {
  if (!form.value.registerId) {
    ElMessage.warning('请选择挂号单')
    return
  }
  if (!form.value.diagnosis) {
    ElMessage.warning('请填写诊断')
    return
  }
  const items = form.value.items.filter((item) => item.medicineId && item.quantity > 0)
  if (items.length === 0) {
    ElMessage.warning('请至少选择一种药品')
    return
  }
  submitting.value = true
  try {
    const register = registers.value.find((item) => item.id === form.value.registerId)
    await axios.post('/addPrescription', {
      registerId: form.value.registerId,
      patientId: register?.patientId,
      doctorId,
      diagnosis: form.value.diagnosis,
      items
    })
    ElMessage.success('处方开具成功')
    dialogVisible.value = false
    await loadAll()
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '处方开具失败')
  } finally {
    submitting.value = false
  }
}

const viewDetail = async (row) => {
  try {
    const res = await axios.get(`/api/prescriptions/${row.prescriptionId}`)
    currentPrescription.value = res.data.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '加载处方详情失败')
  }
}

const removePrescription = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除处方 #${row.prescriptionId}？`, '删除确认', { type: 'warning' })
    await axios.get('/deletePrescription', { params: { prescriptionId: row.prescriptionId } })
    ElMessage.success('删除成功')
    await loadAll()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.msg || '删除失败')
    }
  }
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

const money = (value) => Number(value || 0).toFixed(2)

const formatDate = (value) => {
  if (!value) return ''
  return new Date(value).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

onMounted(loadAll)
</script>

<style scoped>
.prescription-page {
  padding: 4px;
}
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 16px;
}
.page-header h2 {
  margin: 0 0 4px;
  font-size: 20px;
}
.page-header p {
  margin: 0;
  color: #6b7f95;
  font-size: 13px;
}
.items {
  width: 100%;
}
.item-row {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}
</style>
