<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 药品列表数据
const drugList = ref([])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(8)
const totalDrugs = ref(0)

// 搜索关键词（按药品名 / 适应症 / 生产厂家等）
const searchKeyword = ref('')

// 药品类型筛选
const drugTypeFilter = ref('')

// 上下架状态筛选
const statusFilter = ref('')

// 新增 / 编辑弹窗控制
const dialogVisible = ref(false)
const dialogMode = ref('add') // 'add' | 'edit'

// 药品表单
const form = ref({
  drugId: null,
  drugName: '',
  drugType: '',
  indication: '',
  applicableDepartment: '',
  dosageForm: '',
  specification: '',
  manufacturer: '',
  unitPrice: '',
  stockQuantity: '',
  status: 1 // 1: 在售, 0: 下架
})

// 计算属性：过滤后的药品列表（前端过滤，仅对当前页数据有效）
const filteredDrugList = computed(() => {
  let filtered = drugList.value

  // 搜索
  if (searchKeyword.value && searchKeyword.value.trim() !== '') {
    const kw = searchKeyword.value.trim().toLowerCase()
    filtered = filtered.filter(d => {
      const name = (d.drugName || '').toLowerCase()
      const indication = (d.indication || '').toLowerCase()
      const manufacturer = (d.manufacturer || '').toLowerCase()
      return (
        name.includes(kw) ||
        indication.includes(kw) ||
        manufacturer.includes(kw)
      )
    })
  }

  // 类型筛选
  if (drugTypeFilter.value) {
    filtered = filtered.filter(d => d.drugType === drugTypeFilter.value)
  }

  // 状态筛选
  if (statusFilter.value !== '') {
    const s = parseInt(statusFilter.value)
    filtered = filtered.filter(d => d.status === s)
  }

  return filtered
})

// 加载药品列表（分页）
const loadDrugs = async (page = currentPage.value, size = pageSize.value) => {
  // 确保参数是数字类型
  const pageNum = typeof page === 'number' ? page : parseInt(page) || currentPage.value
  const pageSizeNum = typeof size === 'number' ? size : parseInt(size) || pageSize.value
  
  try {
    const res = await axios.get('/getDrugPage', {
      params: {
        pageNum: pageNum,
        pageSize: pageSizeNum
      }
    })
    if (res.data.code === 0) {
      drugList.value = res.data.data.map(item => ({
        drugId: item.drugId,
        drugName: item.drugName,
        drugType: item.drugType,
        indication: item.indication,
        applicableDepartment: item.applicableDepartment,
        dosageForm: item.dosageForm,
        specification: item.specification,
        manufacturer: item.manufacturer,
        unitPrice: item.unitPrice,
        stockQuantity: item.stockQuantity,
        status: item.status
      }))
      totalDrugs.value = res.data.total || 0
      currentPage.value = res.data.pageNum || page
      pageSize.value = res.data.pageSize || size
    } else {
      ElMessage.error('加载药品数据失败：' + (res.data.msg || ''))
    }
  } catch (e) {
    console.error('加载药品失败：', e)
    ElMessage.error('加载药品失败，请稍后重试')
  }
}

// 分页事件处理
const handlePageSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadDrugs()
}

const handleCurrentPageChange = (val) => {
  currentPage.value = val
  loadDrugs()
}

// 打开新增药品弹窗
const openAddDrug = () => {
  dialogMode.value = 'add'
  form.value = {
    drugId: null,
    drugName: '',
    drugType: '',
    indication: '',
    applicableDepartment: '',
    dosageForm: '',
    specification: '',
    manufacturer: '',
    unitPrice: '',
    stockQuantity: '',
    status: 1
  }
  dialogVisible.value = true
}

// 打开编辑药品弹窗
const openEditDrug = (row) => {
  dialogMode.value = 'edit'
  form.value = {
    drugId: row.drugId,
    drugName: row.drugName || '',
    drugType: row.drugType || '',
    indication: row.indication || '',
    applicableDepartment: row.applicableDepartment || '',
    dosageForm: row.dosageForm || '',
    specification: row.specification || '',
    manufacturer: row.manufacturer || '',
    unitPrice: row.unitPrice != null ? String(row.unitPrice) : '',
    stockQuantity: row.stockQuantity != null ? String(row.stockQuantity) : '',
    status: row.status != null ? row.status : 1
  }
  dialogVisible.value = true
}

// 提交表单（新增 / 编辑）
const submitForm = async () => {
  // 基本校验
  if (!form.value.drugName) {
    ElMessage.warning('请填写药品名称')
    return
  }
  if (!form.value.drugType) {
    ElMessage.warning('请填写药品分类')
    return
  }
  if (!form.value.unitPrice || parseFloat(form.value.unitPrice) <= 0) {
    ElMessage.warning('请填写有效的单价')
    return
  }
  if (form.value.stockQuantity === '' || parseInt(form.value.stockQuantity) < 0) {
    ElMessage.warning('请填写有效的库存数量')
    return
  }

  try {
    const payload = {
      drugId: form.value.drugId,
      drugName: form.value.drugName,
      drugType: form.value.drugType,
      indication: form.value.indication,
      applicableDepartment: form.value.applicableDepartment,
      dosageForm: form.value.dosageForm,
      specification: form.value.specification,
      manufacturer: form.value.manufacturer,
      unitPrice: parseFloat(form.value.unitPrice),
      stockQuantity: parseInt(form.value.stockQuantity),
      status: form.value.status
    }

    const url =
      dialogMode.value === 'add'
        ? '/addDrug'
        : '/updateDrug'

    const res = await axios.post(url, payload, {
      headers: { 'Content-Type': 'application/json' }
    })

    if (res.data.code === 0) {
      ElMessage.success(res.data.msg || (dialogMode.value === 'add' ? '新增药品成功' : '更新药品成功'))
      dialogVisible.value = false
      await loadDrugs()
    } else {
      ElMessage.error(res.data.msg || '操作失败')
    }
  } catch (e) {
    console.error('提交药品失败：', e)
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 删除药品
const deleteDrug = (drugId) => {
  axios
    .get('/deleteDrug', {
      params: { drugId }
    })
    .then((response) => {
      if (response.data.code === 0) {
        ElMessage.success('删除成功')
        loadDrugs()
      } else {
        ElMessage.error(response.data.msg || '删除失败')
      }
    })
    .catch((error) => {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    })
}

// 状态显示
const formatStatus = (status) => {
  return status === 1 ? '在售' : '下架'
}

const getStatusTagType = (status) => {
  return status === 1 ? 'success' : 'info'
}

onMounted(() => {
  loadDrugs()
})
</script>

<template>
  <div class="common-layout">
    <el-card class="page-card">
      <div class="page-header">
        <div class="page-title">药品管理</div>
        <el-button type="success" @click="openAddDrug">新增药品</el-button>
      </div>

      <!-- 筛选区域 -->
      <el-row :gutter="16" class="filter-row">
        <el-col :span="8" :xs="24" :sm="12" :md="10">
          <el-input
            v-model="searchKeyword"
            class="responsive-input"
            placeholder="请输入药品名称、适应症或生产厂家"
            clearable
          />
        </el-col>
        <el-col :span="6" :xs="24" :sm="12" :md="6">
          <el-input
            v-model="drugTypeFilter"
            class="responsive-input"
            placeholder="按药品分类筛选（如 抗生素、解热镇痛等）"
            clearable
          />
        </el-col>
        <el-col :span="4" :xs="24" :sm="12" :md="4">
          <el-select
            v-model="statusFilter"
            placeholder="按状态筛选"
            clearable
            style="width: 100%;"
          >
            <el-option label="全部" value="" />
            <el-option label="在售" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-col>
      </el-row>

      <!-- 药品表格 -->
      <el-table :data="filteredDrugList" stripe style="width: 100%" border>
        <el-table-column prop="drugName" label="药品名称" min-width="140" />
        <el-table-column prop="drugType" label="分类" width="120" />
        <el-table-column prop="dosageForm" label="剂型" width="100" />
        <el-table-column prop="specification" label="规格" min-width="160" show-overflow-tooltip />
        <el-table-column prop="manufacturer" label="生产厂家" min-width="180" show-overflow-tooltip />
        <el-table-column prop="indication" label="适应症" min-width="220" show-overflow-tooltip />
        <el-table-column
          prop="applicableDepartment"
          label="适用科室"
          min-width="160"
          show-overflow-tooltip
        />
        <el-table-column prop="unitPrice" label="单价(元)" width="110">
          <template #default="scope">
            ¥{{ parseFloat(scope.row.unitPrice || 0).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="stockQuantity" label="库存" width="90" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" size="small">
              {{ formatStatus(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEditDrug(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="deleteDrug(scope.row.drugId)">
              删除
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
          :total="totalDrugs"
          @size-change="handlePageSizeChange"
          @current-change="handleCurrentPageChange"
        />
      </div>
    </el-card>

    <!-- 新增 / 编辑药品弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '新增药品' : '编辑药品'"
      width="60%"
      top="5vh"
    >
      <el-form :model="form" label-width="120px" style="max-height: 60vh; overflow-y: auto;">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药品名称" required>
              <el-input v-model="form.drugName" placeholder="请输入药品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="药品分类" required>
              <el-input v-model="form.drugType" placeholder="如 抗生素、解热镇痛等" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="剂型">
              <el-input v-model="form.dosageForm" placeholder="如 片剂、胶囊、注射液等" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格">
              <el-input v-model="form.specification" placeholder="如 0.25g*24片/盒" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生产厂家">
              <el-input v-model="form.manufacturer" placeholder="请输入生产厂家" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="适用科室">
              <el-input v-model="form.applicableDepartment" placeholder="多个科室用逗号分隔" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="适应症">
          <el-input
            v-model="form.indication"
            type="textarea"
            :rows="3"
            placeholder="请输入适应症描述"
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单价(元)" required>
              <el-input
                v-model="form.unitPrice"
                type="number"
                placeholder="请输入单价"
                :min="0"
                :step="0.01"
              >
                <template #prefix>¥</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存数量" required>
              <el-input
                v-model="form.stockQuantity"
                type="number"
                placeholder="请输入库存数量"
                :min="0"
                :step="1"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%;">
            <el-option label="在售" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
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
  align-items: center;
  margin-bottom: 16px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.filter-row {
  margin-bottom: 16px;
}

.responsive-input {
  width: 100%;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>


