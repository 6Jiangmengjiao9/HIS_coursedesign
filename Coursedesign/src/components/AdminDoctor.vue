<template>
  <div class="common-layout">
    <el-card class="page-card">
          <div class="page-header">
            <div class="page-title">医生信息管理</div>
            <el-button type="success" @click="addDoctor">新增医生</el-button>
          </div>

          <el-row :gutter="16" class="filter-row">
            <el-col :span="8" :xs="24" :sm="12" :md="10">
              <el-input
                v-model="input2"
                class="responsive-input"
                placeholder="请输入医生姓名、科室或职称"
                clearable
              />
            </el-col>
            <el-col :span="8" :xs="24" :sm="12" :md="8">
              <el-cascader 
                :options="departmentOptions" 
                placeholder="请选择科室" 
                clearable 
                v-model="selectedDepartment"
                @change="handleChange"
                style="width: 100%;"
              />
            </el-col>
          </el-row>

          <el-table :data="filteredDoctorList" stripe style="width: 100%" border>
            <el-table-column prop="name" label="医生姓名" width="100" />
            <el-table-column prop="title" label="职称" width="110" />
            <el-table-column prop="departmentName" label="科室" width="140" />
            
            <el-table-column label="详细信息" min-width="320">
              <template #default="scope">
                <div class="doctor-details">
                  <div class="detail-item">
                    <span class="label">科室简介:</span>
                    <span>{{ scope.row.department.introduction || '暂无' }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">科室位置:</span>
                    <span>{{ scope.row.department.location }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">联系电话:</span>
                    <span>{{ scope.row.department.phone }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="scope">
                <el-button type="primary" size="small" @click="editDoctor(scope.row)">
                  <el-icon><EditPen /></el-icon>
                </el-button>
                <el-button type="danger" size="small" @click="deleteDoctor(scope.row.doctorId)">
                  <el-icon><Delete /></el-icon>
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
              :total="totalDoctors"
              @size-change="handlePageSizeChange"
              @current-change="handleCurrentPageChange"
            />
          </div>
    </el-card>
  </div>
  
  <!-- 新增医生表单对话框 -->
  <el-dialog v-model="formVisible" title="新增医生" width="60%" top="5vh">
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
            <el-form-item label="姓名" required>
              <el-input v-model="form.name" placeholder="请输入医生姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职称" required>
              <el-select v-model="form.title" placeholder="请选择职称" style="width: 100%;">
                <el-option label="主任医师" value="主任医师"></el-option>
                <el-option label="副主任医师" value="副主任医师"></el-option>
                <el-option label="主治医师" value="主治医师"></el-option>
                <el-option label="住院医师" value="住院医师"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="医生编号" required>
              <el-input v-model="form.userId" placeholder="请输入医生编号" />
               </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="科室" required>
              <el-select v-model="form.departmentId" placeholder="请选择科室" style="width: 100%;">
                <el-option
                  v-for="dept in departmentOptions"
                  :key="dept.value"
                  :label="dept.label"
                  :value="dept.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
      
      <!-- 用户信息部分 -->
      <el-card shadow="never" style="margin-bottom: 20px;">
        <template #header>
          <div class="card-header">
            <span>用户信息</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" required>
              <el-input v-model="form.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" required>
              <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%;">
                <el-option label="启用" :value="1"></el-option>
                <el-option label="禁用" :value="0"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-input v-model="form.role" disabled placeholder="医生" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
      
      <!-- 用户详细信息部分 -->
      <el-card shadow="never" style="margin-bottom: 20px;">
        <template #header>
          <div class="card-header">
            <span>用户详细信息</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名" required>
              <el-input v-model="form.realname" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%;">
                <el-option label="男" :value="1"></el-option>
                <el-option label="女" :value="2"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="头像">
              <el-input v-model="form.avatar" placeholder="请输入头像URL" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
      
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="cancelForm">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </span>
    </template>
  </el-dialog>
  
  <!-- 编辑医生表单对话框 -->
  <el-dialog v-model="editFormVisible" title="编辑医生" width="60%" top="5vh">
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
    <el-form-item label="姓名" required>
      <el-input v-model="editForm.name" placeholder="请输入医生姓名" />
    </el-form-item>
  </el-col>
  <el-col :span="12">
    <el-form-item label="职称" required>
      <el-select v-model="editForm.title" placeholder="请选择职称" style="width: 100%;">
        <el-option label="主任医师" value="主任医师"></el-option>
        <el-option label="副主任医师" value="副主任医师"></el-option>
        <el-option label="主治医师" value="主治医师"></el-option>
        <el-option label="住院医师" value="住院医师"></el-option>
      </el-select>
    </el-form-item>
  </el-col>
</el-row>
<el-row :gutter="20">
  <el-col :span="12">
    <el-form-item label="用户ID" required>
      <el-input v-model="editForm.userId" placeholder="请输入用户ID" />
    </el-form-item>
  </el-col>
  <el-col :span="12">
    <el-form-item label="科室" required>
      <el-select v-model="editForm.departmentId" placeholder="请选择科室" style="width: 100%;">
        <el-option
          v-for="dept in departmentOptions"
          :key="dept.value"
          :label="dept.label"
          :value="dept.value"
        ></el-option>
      </el-select>
    </el-form-item>
  </el-col>
</el-row>
      </el-card>
      
      <!-- 用户信息部分 -->
      <el-card shadow="never" style="margin-bottom: 20px;">
        <template #header>
          <div class="card-header">
            <span>用户信息</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" required>
              <el-input v-model="editForm.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" required>
              <el-input v-model="editForm.password" type="text" placeholder="请输入密码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="editForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="editForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="editForm.status" placeholder="请选择状态" style="width: 100%;">
                <el-option label="启用" :value="1"></el-option>
                <el-option label="禁用" :value="0"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-input v-model="editForm.roleDisplay" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
      
      <!-- 用户详细信息部分 -->
      <el-card shadow="never" style="margin-bottom: 20px;">
        <template #header>
          <div class="card-header">
            <span>用户详细信息</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名" required>
              <el-input v-model="editForm.realname" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="editForm.gender" placeholder="请选择性别" style="width: 100%;">
                <el-option label="男" :value="1"></el-option>
                <el-option label="女" :value="2"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="头像">
              <el-input v-model="editForm.avatar" placeholder="请输入头像URL" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
    
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="cancelEditForm">取消</el-button>
        <el-button type="primary" @click="updateDoctor">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// 医生列表数据
const doctorList = ref([])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(3)
const totalDoctors = ref(0)

// 科室筛选选项
const departmentOptions = ref([
  { value: 'KS001', label: '心血管内科' },
  { value: 'KS002', label: '骨科' },
  { value: 'KS003', label: '儿科' },
  { value: 'KS004', label: '神经内科' },
  { value: 'KS005', label: '妇产科' }
])

// 绑定值
const selectedDepartment = ref([])

// 搜索关键词
const input2 = ref('')

// 新增医生表单控制
const formVisible = ref(false)
const form = ref({
  userId: '',     // 对应数据库 user_id
  name: '',       // 对应数据库 name
  title: '',      // 对应数据库 title
  departmentId: '', // 对应数据库 department_id
  userinfoId: '',   // 对应数据库 userinfo_id
  // User 表字段
  username: '',
  password: '',
  email: '',
  phone: '',
  status: 1,
  role: 'doctor',
  // UserInfo 表字段
  realname: '',
  gender: 1,
  avatar: ''
})

// 编辑医生表单控制
const editFormVisible = ref(false)
const editForm = ref({
  doctorId: '',
  userId: '',
  name: '',
  title: '',
  departmentId: '',
  userinfoId: '',
  departmentName: '',
  departmentIntroduction: '',
  departmentLocation: '',
  departmentPhone: '',
  // User 表字段
  userIdForUser: '', // User表的id
  username: '',
  password: '',
  email: '',
  phone: '',
  status: 1,
  role: 'doctor',
  roleDisplay: '医生', // 用于显示的角色名称
  // UserInfo 表字段
  realname: '',
  gender: 1,
  avatar: ''
})

// 计算属性：根据筛选条件过滤医生列表
const filteredDoctorList = computed(() => {
  let filteredData = doctorList.value;
  
  // 姓名搜索筛选
  if (input2.value && input2.value.trim() !== '') {
    const searchKeyword = input2.value.trim().toLowerCase();
    filteredData = filteredData.filter(doctor => 
      doctor.name.toLowerCase().includes(searchKeyword)
    );
  }
  
  // 科室筛选
  if (selectedDepartment.value && selectedDepartment.value.length > 0) {
    const selectedDept = selectedDepartment.value[0];
    filteredData = filteredData.filter(doctor => doctor.departmentId === selectedDept);
  }
  
  return filteredData;
})

// 处理选择变化
const handleChange = (value) => {
  console.log('选中的科室:', value)
}

const deleteDoctor = async (doctorId) => {
  try {
    // 先获取医生信息，以便获取关联的 user 和 userinfo id
    const doctor = doctorList.value.find(d => d.doctorId === doctorId)
    if (!doctor) {
      ElMessage.error('未找到医生信息')
      return
    }
    
    // 删除医生
    const doctorResponse = await axios.get('/deleteDoctor', {
      params: { doctorId: doctorId }
    })
    
    if (doctorResponse.data.code === 0) {
      // 删除 UserInfo
      if (doctor.userinfoId) {
        try {
          await axios.get('/deleteUserInfo', {
            params: { id: doctor.userinfoId }
          })
        } catch (error) {
          console.error('删除用户信息失败:', error)
        }
      }
      
      // 删除 User
      if (doctor.user && doctor.user.id) {
        try {
          await axios.get('/deleteUser', {
            params: { id: doctor.user.id }
          })
        } catch (error) {
          console.error('删除用户失败:', error)
        }
      }
      
      // 成功后刷新数据列表
      getDoctors(currentPage.value, pageSize.value)
      ElMessage.success('删除成功')
    } else {
      ElMessage.error('删除失败: ' + doctorResponse.data.msg)
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

// 获取医生数据（使用分页接口）
const getDoctors = async (page = currentPage.value, size = pageSize.value) => {
  // 确保参数是数字类型
  const pageNum = typeof page === 'number' ? page : parseInt(page) || currentPage.value
  const pageSizeNum = typeof size === 'number' ? size : parseInt(size) || pageSize.value
  
  try {
    const response = await axios.get('/getDoctorPage', {
      params: {
        pageNum: pageNum,
        pageSize: pageSizeNum
      }
    })
    if (response.data.code === 0) {
      const list = response.data.data || []
      // 将后端数据转换为表格需要的格式
      doctorList.value = list.map(item => ({
        doctorId: item.doctorId,
        userId: item.userId,
        name: item.name,
        title: item.title,
        departmentId: item.departmentId,
        userinfoId: item.userinfoId,
        departmentName: item.department ? item.department.departmentName : '',
        department: item.department || {},
        userInfo: item.userInfo || {},
        user: item.user || {}
      }))
      totalDoctors.value = response.data.total || 0
      currentPage.value = response.data.pageNum || page
      pageSize.value = response.data.pageSize || size
    } else {
      console.error('数据加载失败:', response.data.msg)
      ElMessage.error('数据加载失败: ' + (response.data.msg || ''))
    }
  } catch (error) {
    console.error('请求失败:', error)
    ElMessage.error('请求失败，请稍后重试')
  }
}

// 处理每页数量变化
const handlePageSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  getDoctors()
}

// 处理当前页变化
const handleCurrentPageChange = (val) => {
  currentPage.value = val
  getDoctors()
}


// 处理搜索
const handleSearch = () => {
  // 搜索逻辑已经在 filteredDoctorList 计算属性中实现
  console.log('搜索关键词:', input2.value);
}

// 打开新增医生表单
const addDoctor = () => {
  formVisible.value = true
  // 初始化表单
  form.value = {
    userId: '',
    name: '',
    title: '',
    departmentId: '',
    username: '',
    password: '',
    email: '',
    phone: '',
    status: 1,
    role: 'doctor', // 固定为doctor
    realname: '',
    gender: 1,
    avatar: ''
  }
}

// 提交表单
const submitForm = async () => {
  // 验证必填字段
  if (!form.value.name || !form.value.title || !form.value.departmentId || 
      !form.value.username || !form.value.password || !form.value.realname) {
    ElMessage.warning('请填写必填信息：姓名、职称、科室、用户名、密码、真实姓名')
    return
  }
  
  try {
    // 1. 先创建 User
    const userData = {
      username: form.value.username,
      password: form.value.password,
      email: form.value.email || '',
      phone: form.value.phone || '',
      status: form.value.status || 1,
      role: form.value.role || 'doctor'
    }
    
    const userResponse = await axios.post('/addUser', userData)
    if (userResponse.data.code !== 0) {
      ElMessage.error('创建用户失败: ' + userResponse.data.msg)
      return
    }
    
    const userId = userResponse.data.data?.id || userResponse.data.data
    
    // 2. 创建 UserInfo
    const userInfoData = {
      userid: userId,
      realname: form.value.realname,
      gender: form.value.gender || 1,
      avatar: form.value.avatar || ''
    }
    
    const userInfoResponse = await axios.post('/addUserInfo', userInfoData)
    if (userInfoResponse.data.code !== 0) {
      ElMessage.error('创建用户信息失败: ' + userInfoResponse.data.msg)
      return
    }
    
    const userinfoId = userInfoResponse.data.data?.id || userInfoResponse.data.data
    
    // 3. 创建 Doctor
    const doctorData = {
      userId: form.value.userId || userId.toString(),
      name: form.value.name,
      title: form.value.title,
      departmentId: form.value.departmentId,
      userinfoId: userinfoId
    }
    
    const doctorResponse = await axios.post('/addDoctor', doctorData)
    
    if (doctorResponse.data.code === 0) {
      ElMessage.success('新增医生成功')
      formVisible.value = false
      resetForm()
      getDoctors(currentPage.value, pageSize.value) // 重新加载数据
    } else {
      ElMessage.error('新增医生失败: ' + doctorResponse.data.msg)
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败: ' + (error.response?.data?.msg || error.message))
  }
}
// 编辑医生信息
const editDoctor = (row) => {
  // 填充表单数据
  const role = row.user?.role || 'doctor'
  editForm.value = {
    doctorId: row.doctorId,
    userId: row.userId,
    name: row.name,
    title: row.title,
    departmentId: row.departmentId,
    userinfoId: row.userinfoId || '', // 保留用于后端更新，但不显示在表单中
    departmentName: row.departmentName,
    departmentIntroduction: row.department.introduction || '',
    departmentLocation: row.department.location || '',
    departmentPhone: row.department.phone || '',
    // User 表字段
    userIdForUser: row.user?.id || '',
    username: row.user?.username || '',
    password: row.user?.password || '', // 显示密码
    email: row.user?.email || '',
    phone: row.user?.phone || '',
    status: row.user?.status || 1,
    role: 'doctor', // 固定为doctor，不允许修改
    roleDisplay: role === 'doctor' ? '医生' : role === 'admin' ? '管理员' : '医生', // 用于显示
    // UserInfo 表字段
    realname: row.userInfo?.realname || '',
    gender: row.userInfo?.gender || 1,
    avatar: row.userInfo?.avatar || ''
  }
  editFormVisible.value = true
}

// 更新医生信息
const updateDoctor = async () => {
  // 添加基本验证
  if (!editForm.value.name || !editForm.value.title || !editForm.value.departmentId || 
      !editForm.value.username || !editForm.value.realname) {
    ElMessage.warning('请填写必填信息：姓名、职称、科室、用户名、真实姓名')
    return
  }
  
  try {
    // 1. 更新 User
    if (editForm.value.userIdForUser) {
      const userData = {
        id: editForm.value.userIdForUser,
        username: editForm.value.username,
        password: editForm.value.password || '', // 如果密码为空则不更新
        email: editForm.value.email || '',
        phone: editForm.value.phone || '',
        status: editForm.value.status || 1,
        role: 'doctor' // 固定为doctor，不允许修改
      }
      
      // 如果密码为空，删除该字段
      if (!userData.password) {
        delete userData.password
      }
      
      const userResponse = await axios.post('/updateUser', userData)
      if (userResponse.data.code !== 0) {
        ElMessage.error('更新用户失败: ' + userResponse.data.msg)
        return
      }
    }
    
    // 2. 更新 UserInfo
    if (editForm.value.userinfoId) {
      const userInfoData = {
        id: editForm.value.userinfoId,
        userid: editForm.value.userIdForUser || editForm.value.userId,
        realname: editForm.value.realname,
        gender: editForm.value.gender || 1,
        avatar: editForm.value.avatar || ''
      }
      
      const userInfoResponse = await axios.post('/updateUserInfo', userInfoData)
      if (userInfoResponse.data.code !== 0) {
        ElMessage.error('更新用户信息失败: ' + userInfoResponse.data.msg)
        return
      }
    }
    
    // 3. 更新 Doctor
    const doctorData = {
      doctorId: editForm.value.doctorId,
      userId: editForm.value.userId,
      name: editForm.value.name,
      title: editForm.value.title,
      departmentId: editForm.value.departmentId,
      userinfoId: editForm.value.userinfoId
    }
    
    const doctorResponse = await axios.post('/updateDoctor', doctorData, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    
    if (doctorResponse.data.code === 0) {
      ElMessage.success('更新医生信息成功')
      editFormVisible.value = false
      getDoctors(currentPage.value, pageSize.value) // 重新加载数据
    } else {
      ElMessage.error('更新医生信息失败: ' + doctorResponse.data.msg)
    }
  } catch (error) {
    console.error('更新失败:', error)
    ElMessage.error('更新失败: ' + (error.response?.data?.msg || error.message))
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
    userId: '',
    name: '',
    title: '',
    departmentId: '',
    username: '',
    password: '',
    email: '',
    phone: '',
    status: 1,
    role: 'doctor',
    realname: '',
    gender: 1,
    avatar: ''
  }
}

// 页面加载时获取数据
onMounted(() => {
  getDoctors()
})
</script>

<style scoped>
.responsive-input {
  width: 100%;
}

.doctor-details {
  padding: 10px;
  background-color: #f5faff;
  border-radius: 4px;
  margin: 5px 0;
}

.detail-item {
  display: flex;
  margin-bottom: 5px;
}

.label {
  font-weight: bold;
  color: #3f5870;
  min-width: 80px;
  margin-right: 10px;
}

.common-layout {
  width: 100%;
  height: 100%;
  overflow: hidden;
  position: relative;
  box-sizing: border-box;
  max-width: 100%;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>