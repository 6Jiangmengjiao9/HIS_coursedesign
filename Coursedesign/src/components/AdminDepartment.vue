<template>
  <div class="common-layout">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>
        
        <!-- 错误状态 -->
        <div v-else-if="error" class="error-container">
          <el-result icon="error" title="数据加载失败" :sub-title="error">
            <template #extra>
              <el-button type="primary" @click="getDepartments">重试</el-button>
            </template>
          </el-result>
        </div>

        <!-- 空数据状态 -->
        <div v-else-if="departmentList.length === 0" class="empty-container">
          <el-result icon="warning" title="暂无数据" sub-title="没有找到科室信息">
            <template #extra>
              <el-button type="primary" @click="getDepartments">刷新</el-button>
            </template>
          </el-result>
        </div>
        
        <!-- 正常显示 -->
        <div v-else class="department-container">
          <!-- 第一行：3个卡牌 -->
          <el-row :gutter="20" class="department-row first-row">
            <el-col 
              v-for="department in firstRowDepartments" 
              :key="department.departmentId" 
              :xs="24" :sm="12" :md="8" :lg="8"
            >
              <el-card 
                shadow="hover" 
                class="department-card fixed-size-card"
              >
                <template #header>
                  <div class="card-header">
                    <el-icon class="department-icon">
                      <OfficeBuilding />
                    </el-icon>
                    <span class="department-name">{{ department.departmentName }}</span>
                  </div>
                </template>
                
                <div class="card-content">
                  <div class="info-item">
                    <span class="label">简介:</span>
                    <span class="value truncate">{{ department.introduction || '暂无介绍' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">位置:</span>
                    <span class="value">{{ department.location || '未知位置' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">电话:</span>
                    <span class="value">{{ department.phone || '暂无电话' }}</span>
                  </div>
                  <!-- 按钮移到电话信息后 -->
                  <div class="button-group">
                    <el-button 
                      type="primary" 
                      size="small" 
                      @click="viewDepartmentDetails(department)"
                    >
                      查看详情
                    </el-button>
                    <el-button 
                      type="success" 
                      size="small" 
                      @click="viewDoctors(department)"
                    >
                      查看医生
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <!-- 第二行：2个卡牌，居中显示 -->
          <el-row :gutter="20" class="department-row second-row" justify="center">
            <el-col 
              v-for="department in secondRowDepartments" 
              :key="department.departmentId" 
              :xs="24" :sm="12" :md="10" :lg="10"
            >
              <el-card 
                shadow="hover" 
                class="department-card fixed-size-card wide-card"
              >
                <template #header>
                  <div class="card-header">
                    <el-icon class="department-icon">
                      <OfficeBuilding />
                    </el-icon>
                    <span class="department-name">{{ department.departmentName }}</span>
                  </div>
                </template>
                
                <div class="card-content">
                  <div class="info-item">
                    <span class="label">简介:</span>
                    <span class="value truncate">{{ department.introduction || '暂无介绍' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">位置:</span>
                    <span class="value">{{ department.location || '未知位置' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">电话:</span>
                    <span class="value">{{ department.phone || '暂无电话' }}</span>
                  </div>
                  <!-- 按钮移到电话信息后 -->
                  <div class="button-group">
                    <el-button 
                      type="primary" 
                      size="small" 
                      @click="viewDepartmentDetails(department)"
                    >
                      查看详情
                    </el-button>
                    <el-button 
                      type="success" 
                      size="small" 
                      @click="viewDoctors(department)"
                    >
                      查看医生
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
        
        <!-- 科室详情对话框 -->
        <el-dialog 
          v-model="dialogVisible" 
          :title="isEditing ? '编辑科室信息' : '科室详情'" 
          width="60%" 
          top="5vh"
        >
          <el-form :model="selectedDepartment" label-width="120px" style="max-height: 60vh; overflow-y: auto;">
            <!-- 基本信息部分 -->
            <el-card shadow="never" style="margin-bottom: 20px;">
              <template #header>
                <div class="card-header">
                  <span>科室基本信息</span>
                </div>
              </template>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="科室名称">
                    <el-input 
                      v-model="selectedDepartment.departmentName" 
                      :disabled="!isEditing" 
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="科室编号">
                    <el-input 
                      v-model="selectedDepartment.departmentId" 
                      disabled 
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="科室位置">
                    <el-input 
                      v-model="selectedDepartment.location" 
                      :disabled="!isEditing" 
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="联系电话">
                    <el-input 
                      v-model="selectedDepartment.phone" 
                      :disabled="!isEditing" 
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item label="科室简介">
                <el-input 
                  v-model="selectedDepartment.introduction" 
                  type="textarea" 
                  :rows="3" 
                  :disabled="!isEditing"
                />
              </el-form-item>
            </el-card>
            
            <!-- 医生信息部分 -->
            <el-card shadow="never" v-if="selectedDepartment.doctors && selectedDepartment.doctors.length > 0">
              <template #header>
                <div class="card-header">
                  <span>科室医生列表</span>
                </div>
              </template>
              <el-table :data="selectedDepartment.doctors" stripe style="width: 100%">
                <el-table-column prop="name" label="医生姓名" width="90" />
                <el-table-column prop="title" label="职称" width="90" />
                <el-table-column prop="userId" label="用户ID" width="120" />
                <el-table-column label="操作">
                  <template #default="scope">
                    <el-button type="primary" size="small" @click="editDoctor(scope.row)">
                      编辑
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>

            <!-- 无医生信息提示 -->
            <el-card shadow="never" v-else>
              <template #header>
                <div class="card-header">
                  <span>科室医生列表</span>
                </div>
              </template>
              <el-empty description="该科室暂无医生信息" />
            </el-card>
          </el-form>
          
          <template #footer>
            <span class="dialog-footer">
              <div v-if="!isEditing">
                <el-button @click="isEditing = true">编辑</el-button>
                <el-button @click="dialogVisible = false">关闭</el-button>
              </div>
              <div v-else>
                <el-button type="primary" @click="updateDepartment">保存</el-button>
                <el-button @click="cancelEdit">取消</el-button>
              </div>
            </span>
          </template>
        </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { OfficeBuilding } from '@element-plus/icons-vue'

// 科室列表数据
const departmentList = ref([])
const loading = ref(false)
const error = ref('')

// 当前选中的科室
const selectedDepartment = ref({})
const dialogVisible = ref(false)
const isEditing = ref(false)

// 计算属性：第一行科室（前3个）
const firstRowDepartments = computed(() => {
  return departmentList.value.slice(0, 3)
})

// 计算属性：第二行科室（后2个）
const secondRowDepartments = computed(() => {
  return departmentList.value.slice(3, 5)
})

// 解析Java对象字符串为JSON对象
const parseJavaObjectString = (javaString) => {
  try {
    // 处理整个数组字符串
    let cleanedString = javaString.trim()
    
    // 移除开头和结尾的方括号（如果有）
    if (cleanedString.startsWith('[') && cleanedString.endsWith(']')) {
      cleanedString = cleanedString.substring(1, cleanedString.length - 1)
    }
    
    // 分割各个对象
    const objectStrings = cleanedString.split('), ')
    const objects = []
    
    for (let objStr of objectStrings) {
      // 清理每个对象字符串
      objStr = objStr.trim()
      
      // 移除 DepartmentFlat( 前缀和 ) 后缀
      if (objStr.startsWith('DepartmentFlat(')) {
        objStr = objStr.substring('DepartmentFlat('.length)
      }
      if (objStr.endsWith(')')) {
        objStr = objStr.substring(0, objStr.length - 1)
      }
      
      // 解析键值对
      const keyValuePairs = objStr.split(', ')
      const obj = {}
      
      for (let pair of keyValuePairs) {
        const equalsIndex = pair.indexOf('=')
        if (equalsIndex > -1) {
          const key = pair.substring(0, equalsIndex)
          let value = pair.substring(equalsIndex + 1)
          
          // 处理空值
          if (value === 'null') {
            value = null
          }
          
          obj[key] = value
        }
      }
      
      objects.push(obj)
    }
    
    return objects
  } catch (err) {
    console.error('解析Java对象字符串失败:', err)
    throw new Error('数据格式解析失败: ' + err.message)
  }
}

// 获取科室数据
const getDepartments = async () => {
  loading.value = true
  error.value = ''
  
  try {
    console.log('开始请求科室数据...')
    const response = await axios.get('/getAllDepartment')
    console.log('原始响应数据:', response)
    
    let data = response.data
    
    // 深度检查数据结构
    console.log('响应数据类型:', typeof data)
    console.log('响应数据内容:', data)
    
    let rawData = []
    
    // 处理字符串响应（Java对象toString格式）
    if (typeof data === 'string') {
      console.log('检测到字符串格式响应，尝试解析Java对象...')
      rawData = parseJavaObjectString(data)
    } 
    // 处理JSON响应
    else if (data && typeof data === 'object') {
      // 检查常见的响应格式
      if (data.code !== undefined && data.code === 0 && Array.isArray(data.data)) {
        rawData = data.data
        console.log('使用 code=0 格式的数据')
      } else if (data.code !== undefined && Array.isArray(data.data)) {
        rawData = data.data
        console.log('使用 code 格式的数据')
      } else if (Array.isArray(data.data)) {
        rawData = data.data
        console.log('使用 data 数组格式')
      } else if (Array.isArray(data.list)) {
        rawData = data.list
        console.log('使用 list 数组格式')
      } else if (Array.isArray(data.result)) {
        rawData = data.result
        console.log('使用 result 数组格式')
      } else if (Array.isArray(data)) {
        rawData = data
        console.log('数据为直接数组格式')
      } else if (Object.keys(data).length > 0) {
        // 尝试将对象转换为数组
        rawData = [data]
        console.log('将对象转换为数组')
      } else {
        console.log('数据对象为空')
      }
    }
    
    console.log('处理后的数据:', rawData)
    
    // 验证数据有效性
    if (!Array.isArray(rawData)) {
      throw new Error('数据格式不正确，期望数组但得到: ' + typeof rawData)
    }
    
    if (rawData.length === 0) {
      console.log('数据为空数组，显示空状态')
      departmentList.value = []
      ElMessage.warning('暂无科室数据')
      return
    }
    
    // 处理科室数据
    departmentList.value = processDepartmentData(rawData)
    console.log('最终科室列表:', departmentList.value)
    
    ElMessage.success(`成功加载 ${departmentList.value.length} 个科室信息`)
    
  } catch (err) {
    console.error('请求失败:', err)
    console.error('错误详情:', err.response ? err.response.data : '无响应数据')
    
    if (err.response) {
      error.value = `服务器错误: ${err.response.status} - ${err.response.statusText}`
    } else if (err.request) {
      error.value = '网络连接失败，请检查网络连接或服务器状态'
    } else {
      error.value = err.message || '未知错误'
    }
    
    ElMessage.error('获取科室信息失败: ' + error.value)
  } finally {
    loading.value = false
  }
}

// 处理科室数据
const processDepartmentData = (rawData) => {
  // 检查数据是否已经是分组的科室结构
  const firstItem = rawData[0]
  
  // 如果数据已经包含科室信息和医生数组，直接返回
  if (firstItem.doctors !== undefined) {
    console.log('数据已经是分组结构，直接返回')
    return rawData
  }
  
  // 检查是否是扁平化的科室-医生数据
  const hasDoctorFields = firstItem.doctorId !== undefined || firstItem.doctorName !== undefined
  
  if (hasDoctorFields) {
    console.log('检测到扁平化科室-医生数据，进行分组处理')
    return processFlatDepartmentData(rawData)
  }
  
  // 如果只是基本的科室信息，没有医生数据
  console.log('检测到基本科室信息数据')
  return rawData.map(dept => ({
    ...dept,
    doctors: [] // 添加空的医生数组
  }))
}

// 处理扁平化的科室医生数据格式
const processFlatDepartmentData = (flatData) => {
  const departmentMap = new Map()
  
  flatData.forEach(item => {
    const deptId = item.departmentId
    
    if (!departmentMap.has(deptId)) {
      // 创建新的科室条目
      departmentMap.set(deptId, {
        departmentId: deptId,
        departmentName: item.departmentName || '未知科室',
        introduction: item.introduction || '',
        location: item.location || '',
        phone: item.phone || '',
        doctors: []
      })
    }
    
    const department = departmentMap.get(deptId)
    
    // 如果有医生信息，添加到科室的医生列表
    if (item.doctorId || item.doctorName) {
      department.doctors.push({
        id: item.doctorId,
        name: item.doctorName || '未知医生',
        title: item.title || '',
        userId: item.userId || ''
      })
    }
  })
  
  return Array.from(departmentMap.values())
}

// 查看科室详情
const viewDepartmentDetails = (department) => {
  selectedDepartment.value = {
    ...department,
    doctors: department.doctors || [] // 确保doctors数组存在
  }
  isEditing.value = false // 重置编辑状态
  dialogVisible.value = true
}

// 查看科室医生信息
const viewDoctors = (department) => {
  selectedDepartment.value = {
    ...department,
    doctors: department.doctors || []
  }
  dialogVisible.value = true
}

// 更新科室信息
const updateDepartment = async () => {
  try {
    const updateData = {
      departmentId: selectedDepartment.value.departmentId,
      departmentName: selectedDepartment.value.departmentName,
      introduction: selectedDepartment.value.introduction,
      location: selectedDepartment.value.location,
      phone: selectedDepartment.value.phone
    }
    
    const response = await axios.post('/updateDepartment', updateData)
    
    if (response.data) {
      ElMessage.success('科室信息更新成功')
      isEditing.value = false
      // 重新加载科室列表
      await getDepartments()
    } else {
      ElMessage.error('科室信息更新失败')
    }
  } catch (err) {
    console.error('更新失败:', err)
    ElMessage.error('更新科室信息时发生错误')
  }
}

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false
  // 恢复原始数据
  viewDepartmentDetails(selectedDepartment.value)
}

// 编辑医生信息
const editDoctor = (doctor) => {
  ElMessage.info(`编辑医生: ${doctor.name}`)
  // 这里可以打开编辑医生的对话框
}

// 页面加载时获取数据
onMounted(() => {
  getDepartments()
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

.department-container {
  padding: 0 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.department-row {
  margin-bottom: 24px;
  display: flex;
  justify-content: center;
}

.first-row {
  justify-content: space-between;
}

.second-row {
  justify-content: center;
}

.department-card {
  transition: all 0.3s ease;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.department-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.fixed-size-card {
  height: 320px;
  width: 380px;
}

.wide-card {
  width: 420px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 12px 0 8px;
  border-bottom: 1px solid #f0f0f0;
}

.department-icon {
  margin-right: 10px;
  color: #2f80ed;
  font-size: 20px;
}

.department-name {
  font-size: 18px;
  font-weight: 600;
  color: #1f3b57;
}

.card-content {
  padding: 16px 0;
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.info-item {
  display: flex;
  margin-bottom: 14px;
  font-size: 14px;
  line-height: 1.5;
}

.label {
  font-weight: 600;
  color: #3f5870;
  min-width: 45px;
  margin-right: 10px;
  flex-shrink: 0;
}

.value {
  color: #6b7f95;
  word-break: break-word;
  flex: 1;
}

.truncate {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  overflow: hidden;
  text-overflow: ellipsis;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}

.loading-container, .error-container, .empty-container {
  padding: 40px 20px;
  text-align: center;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .department-container {
    max-width: 1000px;
    padding: 0 15px;
  }
  
  .fixed-size-card {
    width: 340px;
    height: 320px;
  }
  
  .wide-card {
    width: 400px;
  }
}

@media (max-width: 992px) {
  .department-container {
    max-width: 800px;
  }
  
  .fixed-size-card {
    width: 320px;
    height: 320px;
  }
  
  .wide-card {
    width: 360px;
  }
  
  .card-content {
    height: 190px;
  }
}

@media (max-width: 768px) {
  .department-container {
    padding: 0 10px;
  }
  
  .department-row {
    flex-direction: column;
    align-items: center;
  }
  
  .fixed-size-card, .wide-card {
    width: 100%;
    max-width: 350px;
    margin-bottom: 16px;
  }
  
  .card-content {
    height: 180px;
  }
  
  .department-name {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .fixed-size-card, .wide-card {
    height: 300px;
    margin-bottom: 12px;
  }
  
  .card-content {
    height: 170px;
    padding: 12px 0;
  }
  
  .info-item {
    margin-bottom: 10px;
    font-size: 13px;
  }
}

/* 美化滚动条 */
.card-content::-webkit-scrollbar {
  width: 4px;
}

.card-content::-webkit-scrollbar-track {
  background: #eef6ff;
  border-radius: 2px;
}

.card-content::-webkit-scrollbar-thumb {
  background: #c9dff3;
  border-radius: 2px;
}

.card-content::-webkit-scrollbar-thumb:hover {
  background: #a9c8e6;
}

/* 动画效果 */
.department-card {
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 为第二行卡片添加延迟动画 */
.second-row .department-card {
  animation-delay: 0.2s;
}
</style>