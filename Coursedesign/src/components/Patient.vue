<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// 患者列表数据
const patientList = ref([])

// 性别筛选选项
const options = ref([
  {
    value: 'gender',
    label: '性别',
    children: [
      { value: 'male', label: '男' },
      { value: 'female', label: '女' }
    ]
  }
])

// 绑定值
const value = ref([])

// 搜索关键词
const input2 = ref('')

// 病例详情对话框控制
const dialogVisible = ref(false)
const currentCaseInfo = ref({})

// 新增患者表单控制
const formVisible = ref(false)
const form = ref({
  name: '',
  gender: '',
  age: '',
  address: '',
  idCard: '',
  contact: '',
  chiefComplaint: '',
  presentIllness: '',
  pastHistory: '',
  physicalExam: '',
  auxiliaryExam: '',
  diagnosis: '',
  entryTime: ''
})

// 编辑患者表单控制
const editFormVisible = ref(false)
const editForm = ref({
  patientId: '',
  name: '',
  gender: '',
  age: '',
  address: '',
  idCard: '',
  contact: '',
  chiefComplaint: '',
  presentIllness: '',
  pastHistory: '',
  physicalExam: '',
  auxiliaryExam: '',
  diagnosis: '',
  entryTime: ''
})

// 计算属性：根据筛选条件过滤患者列表
const filteredPatientList = computed(() => {
  let filteredData = patientList.value;
  
  // 姓名搜索筛选
  if (input2.value && input2.value.trim() !== '') {
    const searchKeyword = input2.value.trim().toLowerCase();
    filteredData = filteredData.filter(patient => 
      patient.name.toLowerCase().includes(searchKeyword) ||
      patient.idCard.toLowerCase().includes(searchKeyword) ||
      patient.contact.toLowerCase().includes(searchKeyword)
    );
  }
  
  // 性别筛选
  if (value.value && value.value.length > 0) {
    const selectedGender = value.value[value.value.length - 1];
    if (selectedGender === 'male') {
      filteredData = filteredData.filter(patient => patient.gender === '男');
    } else if (selectedGender === 'female') {
      filteredData = filteredData.filter(patient => patient.gender === '女');
    }
  }
  
  return filteredData;
})

// 处理选择变化
const handleChange = (value) => {
  console.log('选中的值:', value)
}

const deletePatient = (patientId) => {
  // 直接发送删除请求，不进行确认对话框
  axios.get('/deletePatient', {
    params: {
      patientId: patientId
    }
  })
  .then(response => {
    // 成功后直接刷新数据列表
    getdata();
    ElMessage.success('删除成功');
  })
  .catch(error => {
    console.error('删除失败:', error);
    ElMessage.error('删除失败');
  });
}

// 获取患者数据
const getdata = async () => {
  try {
    const response = await axios.get('/getAllPatient')
    if (response.data.code === 0) {
      // 将后端数据转换为表格需要的格式
      patientList.value = response.data.data.map(item => ({
        patientId: item.patientId,
        name: item.name,
        gender: item.gender,
        age: item.age,
        address: item.address,
        idCard: item.idCard,
        contact: item.contact,
        caseInfo: item.caseInfo || {} // 添加病例信息
      }))
    } else {
      console.error('数据加载失败:', response.data.msg)
    }
  } catch (error) {
    console.error('请求失败:', error)
  }
}

// 处理搜索
const handleSearch = () => {
  // 搜索逻辑已经在 filteredPatientList 计算属性中实现
  console.log('搜索关键词:', input2.value);
}

// 查看病例详情
const viewCaseInfo = (row) => {
  currentCaseInfo.value = row.caseInfo || {}
  dialogVisible.value = true
}

// 打开新增患者表单
const addpatient = () => {
  formVisible.value = true
}

// 提交表单
const submitForm = async () => {
  // 添加基本验证
  if (!form.value.name || !form.value.gender || !form.value.age) {
    ElMessage.warning('请填写必填信息：姓名、性别、年龄')
    return
  }
  
  try {
    // 构造发送到后端的数据结构，按照Apifox的格式
    const postData = {
      name: form.value.name,
      gender: form.value.gender,
      age: parseInt(form.value.age) || 0,
      address: form.value.address,
      idCard: form.value.idCard,
      contact: form.value.contact,
       caseInfo: {chiefComplaint: form.value.chiefComplaint,
      presentIllness: form.value.presentIllness,
      pastHistory: form.value.pastHistory,
      physicalExam: form.value.physicalExam,
      auxiliaryExam: form.value.auxiliaryExam,
      diagnosis: form.value.diagnosis,
      entryTime: form.value.entryTime
       }
    }

    // 发送到后端
    const response = await axios.post('/addPatient', postData)
    
    if (response.data.code === 0) {
      ElMessage.success('新增患者成功')
      formVisible.value = false
      resetForm()
      getdata() // 重新加载数据
    } else {
      ElMessage.error('新增患者失败: ' + response.data.msg)
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  }
}

// 编辑患者信息
const editPatient = (row) => {
  // 填充表单数据
  editForm.value = {
    patientId: row.patientId,
    name: row.name,
    gender: row.gender,
    age: row.age,
    address: row.address,
    idCard: row.idCard,
    contact: row.contact,
    chiefComplaint: row.caseInfo?.chiefComplaint || '',
    presentIllness: row.caseInfo?.presentIllness || '',
    pastHistory: row.caseInfo?.pastHistory || '',
    physicalExam: row.caseInfo?.physicalExam || '',
    auxiliaryExam: row.caseInfo?.auxiliaryExam || '',
    diagnosis: row.caseInfo?.diagnosis || '',
    entryTime: row.caseInfo?.entryTime || ''
  }
  editFormVisible.value = true
}

// 更新患者信息
// 更新患者信息
const updatePatient = async () => {
  // 添加基本验证
  if (!editForm.value.name || !editForm.value.gender || !editForm.value.age) {
    ElMessage.warning('请填写必填信息：姓名、性别、年龄')
    return
  }
  
  try {
    // 构造发送到后端的数据结构，与后端Patient模型匹配
    const postData = {
      patientId: editForm.value.patientId,
      name: editForm.value.name,
      gender: editForm.value.gender,
      age: parseInt(editForm.value.age) || 0,
      address: editForm.value.address,
      idCard: editForm.value.idCard,
      contact: editForm.value.contact,
      caseInfo: {
        patientId: editForm.value.patientId,
        chiefComplaint: editForm.value.chiefComplaint,
        presentIllness: editForm.value.presentIllness,
        pastHistory: editForm.value.pastHistory,
        physicalExam: editForm.value.physicalExam,
        auxiliaryExam: editForm.value.auxiliaryExam,
        diagnosis: editForm.value.diagnosis,
        entryTime: editForm.value.entryTime
      }
    }

    // 发送到后端更新接口，确保使用POST方法并发送请求体
    const response = await axios.post('/updatePatient', postData, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    
    if (response.data.code === 0) {
      ElMessage.success('更新患者信息成功')
      editFormVisible.value = false
      getdata() // 重新加载数据
    } else {
      ElMessage.error('更新患者信息失败: ' + response.data.msg)
    }
  } catch (error) {
    console.error('更新失败:', error)
    ElMessage.error('更新失败')
  }
}

// 取消表单
const cancelForm = () => {
  formVisible.value = false
  resetForm()
}

// 取消编辑表单
const cancelEditForm = () => {
  editFormVisible.value = false
}

// 重置表单
const resetForm = () => {
  form.value = {
    name: '',
    gender: '',
    age: '',
    address: '',
    idCard: '',
    contact: '',
    chiefComplaint: '',
    presentIllness: '',
    pastHistory: '',
    physicalExam: '',
    auxiliaryExam: '',
    diagnosis: '',
    entryTime: ''
  }
}

// 页面加载时获取数据
onMounted(() => {
  getdata()
})
</script>

<template>
  <div class="common-layout">
    <el-container>
      <el-main>
        <el-card class="page-card">
          <div class="page-header">
            <div class="page-title">患者信息管理</div>
            <el-button type="success" @click="addpatient">新增患者</el-button>
          </div>

          <el-row :gutter="16" class="filter-row">
            <el-col :span="8" :xs="24" :sm="12" :md="10">
              <el-input
                v-model="input2"
                class="responsive-input"
                placeholder="请输入患者姓名、病历号或联系方式"
                clearable
              />
            </el-col>
            <el-col :span="8" :xs="24" :sm="12" :md="8">
              <el-cascader 
                :options="options" 
                placeholder="请选择性别" 
                clearable 
                v-model="value"
                @change="handleChange"
                style="width: 100%;"
              />
            </el-col>
          </el-row>

          <el-table :data="filteredPatientList" stripe style="width: 100%" border>
            <el-table-column prop="name" label="患者姓名" width="100" />
            <el-table-column prop="gender" label="患者性别" width="100" />
            <el-table-column prop="age" label="患者年龄" width="100" />
            <el-table-column prop="address" label="患者住址" min-width="200" show-overflow-tooltip />
            <el-table-column prop="idCard" label="患者病历号" min-width="200" show-overflow-tooltip />
            <el-table-column prop="contact" label="患者联系方式" min-width="160" show-overflow-tooltip />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                <el-button plain type="primary" size="small" @click="viewCaseInfo(scope.row)">
                  <el-icon><View /></el-icon>
                </el-button>
                <el-button type="primary" size="small" @click="editPatient(scope.row)">
                  <el-icon><EditPen /></el-icon>
                </el-button>
                <el-button type="danger" size="small" @click="deletePatient(scope.row.patientId)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-main>
    </el-container>
  </div>
  
  <!-- 病例详情对话框 -->
  <el-dialog v-model="dialogVisible" title="患者病例详情" width="60%">
    <el-descriptions border :column="1">
      <el-descriptions-item label="主诉">{{ currentCaseInfo.chiefComplaint }}</el-descriptions-item>
      <el-descriptions-item label="现病史">{{ currentCaseInfo.presentIllness }}</el-descriptions-item>
      <el-descriptions-item label="既往史">{{ currentCaseInfo.pastHistory }}</el-descriptions-item>
      <el-descriptions-item label="体格检查">{{ currentCaseInfo.physicalExam }}</el-descriptions-item>
      <el-descriptions-item label="辅助检查">{{ currentCaseInfo.auxiliaryExam }}</el-descriptions-item>
      <el-descriptions-item label="诊断">{{ currentCaseInfo.diagnosis }}</el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
      </span>
    </template>
  </el-dialog>
  
  <!-- 新增患者表单对话框 -->
  <el-dialog v-model="formVisible" title="新增患者" width="60%" top="5vh">
    <el-form :model="form" label-width="120px" style="max-height: 60vh; overflow-y: auto;">
      <!-- 基本信息部分 -->
      <el-card shadow="never" style="margin-bottom: 20px;">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="患者姓名">
              <el-input v-model="form.name" placeholder="请输入患者姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%;">
                <el-option label="男" value="男"></el-option>
                <el-option label="女" value="女"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input v-model="form.age" type="number" placeholder="请输入年龄" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="病历号">
              <el-input v-model="form.idCard" placeholder="请输入病历号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系方式">
              <el-input v-model="form.contact" placeholder="请输入联系方式" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="住址">
              <el-input v-model="form.address" placeholder="请输入住址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入院时间">
              <el-date-picker
                v-model="form.entryTime"
                type="date"
                placeholder="请选择入院时间"
                style="width: 100%;"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
      
      <!-- 病例信息部分 -->
      <el-card shadow="never">
        <template #header>
          <div class="card-header">
            <span>病例信息</span>
          </div>
        </template>
        <el-form-item label="主诉">
          <el-input 
            v-model="form.chiefComplaint" 
            type="textarea" 
            :rows="2" 
            placeholder="请输入主诉"
          />
        </el-form-item>
        <el-form-item label="现病史">
          <el-input 
            v-model="form.presentIllness" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入现病史"
          />
        </el-form-item>
        <el-form-item label="既往史">
          <el-input 
            v-model="form.pastHistory" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入既往史"
          />
        </el-form-item>
        <el-form-item label="体格检查">
          <el-input 
            v-model="form.physicalExam" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入体格检查结果"
          />
        </el-form-item>
        <el-form-item label="辅助检查">
          <el-input 
            v-model="form.auxiliaryExam" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入辅助检查结果"
          />
        </el-form-item>
        <el-form-item label="诊断">
          <el-input 
            v-model="form.diagnosis" 
            type="textarea" 
            :rows="2" 
            placeholder="请输入诊断结果"
          />
        </el-form-item>
      </el-card>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="cancelForm">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </span>
    </template>
  </el-dialog>
  
  <!-- 编辑患者表单对话框 -->
  <el-dialog v-model="editFormVisible" title="编辑患者" width="60%" top="5vh">
    <el-form :model="editForm" label-width="120px" style="max-height: 60vh; overflow-y: auto;">
      <!-- 基本信息部分 -->
      <el-card shadow="never" style="margin-bottom: 20px;">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="患者姓名">
              <el-input v-model="editForm.name" placeholder="请输入患者姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="editForm.gender" placeholder="请选择性别" style="width: 100%;">
                <el-option label="男" value="男"></el-option>
                <el-option label="女" value="女"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input v-model="editForm.age" type="number" placeholder="请输入年龄" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="病历号">
              <el-input v-model="editForm.idCard" placeholder="请输入病历号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系方式">
              <el-input v-model="editForm.contact" placeholder="请输入联系方式" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="住址">
              <el-input v-model="editForm.address" placeholder="请输入住址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入院时间">
              <el-date-picker
                v-model="editForm.entryTime"
                type="date"
                placeholder="请选择入院时间"
                style="width: 100%;"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
      
      <!-- 病例信息部分 -->
      <el-card shadow="never">
        <template #header>
          <div class="card-header">
            <span>病例信息</span>
          </div>
        </template>
        <el-form-item label="主诉">
          <el-input 
            v-model="editForm.chiefComplaint" 
            type="textarea" 
            :rows="2" 
            placeholder="请输入主诉"
          />
        </el-form-item>
        <el-form-item label="现病史">
          <el-input 
            v-model="editForm.presentIllness" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入现病史"
          />
        </el-form-item>
        <el-form-item label="既往史">
          <el-input 
            v-model="editForm.pastHistory" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入既往史"
          />
        </el-form-item>
        <el-form-item label="体格检查">
          <el-input 
            v-model="editForm.physicalExam" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入体格检查结果"
          />
        </el-form-item>
        <el-form-item label="辅助检查">
          <el-input 
            v-model="editForm.auxiliaryExam" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入辅助检查结果"
          />
        </el-form-item>
        <el-form-item label="诊断">
          <el-input 
            v-model="editForm.diagnosis" 
            type="textarea" 
            :rows="2" 
            placeholder="请输入诊断结果"
          />
        </el-form-item>
      </el-card>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="cancelEditForm">取消</el-button>
        <el-button type="primary" @click="updatePatient">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped>
.responsive-input {
  width: 100%;
}

.common-layout {
  background: transparent;
}
</style>