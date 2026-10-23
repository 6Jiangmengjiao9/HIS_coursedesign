<template>
  <div class="common-layout">
    <el-card class="page-card">
      <div class="page-header">
        <div class="page-title">个人信息管理</div>
        <el-button type="primary" @click="handleEdit" v-if="!isEditing">编辑信息</el-button>
        <div v-else>
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </div>
      </div>

      <el-card class="profile-card" v-loading="loading">
        <el-tabs v-model="activeTab">
          <!-- 账户信息 -->
          <el-tab-pane label="账户信息" name="account">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="用户名">
                <el-input v-if="isEditing" v-model="formData.username" placeholder="请输入用户名" />
                <span v-else>{{ formData.username || '未填写' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="邮箱">
                <el-input v-if="isEditing" v-model="formData.email" placeholder="请输入邮箱" />
                <span v-else>{{ formData.email || '未填写' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="手机号" :span="2">
                <el-input v-if="isEditing" v-model="formData.phone" placeholder="请输入手机号" />
                <span v-else>{{ formData.phone || '未填写' }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </el-tab-pane>
          
          <!-- 个人信息 -->
          <el-tab-pane label="个人信息" name="info">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="真实姓名">
                <el-input v-if="isEditing" v-model="formData.realname" placeholder="请输入真实姓名" />
                <span v-else>{{ formData.realname || '未填写' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="性别">
                <el-select v-if="isEditing" v-model="formData.gender" placeholder="请选择性别" style="width: 100%;">
                  <el-option label="男" :value="1" />
                  <el-option label="女" :value="0" />
                </el-select>
                <span v-else>{{ formData.gender === 1 ? '男' : formData.gender === 0 ? '女' : '未填写' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="头像URL" :span="2">
                <el-input v-if="isEditing" v-model="formData.avatar" placeholder="请输入头像URL" />
                <span v-else>{{ formData.avatar || '未填写' }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </el-tab-pane>
          
          <!-- 修改密码 -->
          <el-tab-pane label="修改密码" name="password">
            <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="120px" style="max-width: 600px; margin-top: 20px;">
              <el-form-item label="旧密码" prop="oldPassword">
                <el-input 
                  v-model="passwordForm.oldPassword" 
                  type="password" 
                  placeholder="请输入当前密码"
                  show-password
                  clearable
                />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input 
                  v-model="passwordForm.newPassword" 
                  type="password" 
                  placeholder="请输入新密码（留空则不修改）"
                  show-password
                  clearable
                />
              </el-form-item>
              <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input 
                  v-model="passwordForm.confirmPassword" 
                  type="password" 
                  placeholder="请再次输入新密码"
                  show-password
                  clearable
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">修改密码</el-button>
                <el-button @click="resetPasswordForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const isEditing = ref(false)
const activeTab = ref('account')
const formData = ref({
  // User 表字段
  id: null,
  username: '',
  email: '',
  phone: '',
  // UserInfo 表字段
  userInfoId: null,
  userid: null,
  realname: '',
  gender: null, // 0-女, 1-男
  avatar: ''
})

const originalData = ref({
  password: '' // 保存原密码
})

// 密码修改相关
const passwordFormRef = ref(null)
const passwordLoading = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (passwordForm.value.newPassword && !value) {
    callback(new Error('请再次输入新密码'))
  } else if (passwordForm.value.newPassword && value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 同步更新Patient表中的患者信息
const syncPatientInfo = async () => {
  try {
    // 获取所有患者，找到当前用户对应的患者记录
    const patientRes = await axios.get('/getAllPatient')
    if (patientRes.data.code === 0) {
      const patients = patientRes.data.data || []
      
      // 尝试通过realname和phone匹配患者
      const realname = formData.value.realname || ''
      const phone = formData.value.phone || ''
      
      // 先尝试精确匹配
      let patient = patients.find(p => 
        (p.name === realname || p.name === formData.value.username) &&
        (p.contact === phone || !phone || !p.contact)
      )
      
      // 如果找不到，尝试只匹配name
      if (!patient && realname) {
        patient = patients.find(p => p.name === realname || p.name === formData.value.username)
      }
      
      // 如果找到了患者记录，同步更新
      if (patient && patient.patientId) {
        const updatePatientData = {
          patientId: patient.patientId,
          name: realname || formData.value.username,
          contact: phone,
          gender: patient.gender || '',
          age: patient.age || null,
          address: patient.address || '',
          idCard: patient.idCard || '',
          doctorId: patient.doctorId || null,
          caseInfo: patient.caseInfo || {}
        }
        
        await axios.post('/updatePatient', updatePatientData, {
          headers: {
            'Content-Type': 'application/json'
          }
        })
        // 静默更新，不显示错误（因为可能患者记录不存在）
      }
    }
  } catch (error) {
    // 静默处理错误，不影响主流程
    console.log('同步Patient信息失败（可能患者记录不存在）:', error)
  }
}

// 刷新用户数据到localStorage（更新后调用）
const refreshUserData = async () => {
  try {
    const userId = formData.value.id
    if (!userId) return

    // 重新获取完整的 User 信息
    const userResponse = await axios.get('/getUserById', {
      params: { id: userId }
    })

    if (userResponse.data.code === 0 && userResponse.data.data) {
      const user = userResponse.data.data
      
      // 获取 UserInfo 信息
      const userInfoResponse = await axios.get('/getUserInfoByUserId', {
        params: { userId: userId }
      })
      
      let userInfo = null
      if (userInfoResponse.data.code === 0 && userInfoResponse.data.data) {
        userInfo = userInfoResponse.data.data
      }
      
      // 构建完整的用户数据对象，保留所有原有字段
      const currentUserData = localStorage.getItem('userData')
      let existingData = {}
      if (currentUserData) {
        try {
          existingData = JSON.parse(currentUserData)
        } catch (e) {
          console.error('解析现有userData失败:', e)
        }
      }
      
      // 更新用户数据，保留所有原有字段（如role, status, create_time等）
      const realname = userInfo ? userInfo.realname : (existingData.realname || existingData.name || user.username)
      const updatedUserData = {
        ...existingData, // 保留所有原有字段
        id: user.id,
        username: user.username,
        email: user.email,
        phone: user.phone,
        contact: user.phone, // 同时设置contact字段，用于匹配Patient表
        password: user.password, // 保留密码
        status: user.status !== undefined ? user.status : existingData.status,
        role: user.role || existingData.role || 'patient',
        create_time: user.create_time || existingData.create_time,
        update_time: user.update_time || existingData.update_time,
        userInfo: userInfo ? {
          id: userInfo.id,
          userid: userInfo.userid,
          realname: userInfo.realname,
          gender: userInfo.gender,
          avatar: userInfo.avatar
        } : existingData.userInfo,
        realname: realname,
        name: realname // 同时设置name字段，用于匹配Patient表
      }
      
      // 更新localStorage
      localStorage.setItem('userData', JSON.stringify(updatedUserData))
    }
  } catch (error) {
    console.error('刷新用户数据失败:', error)
    // 即使失败也不影响主流程，只记录错误
  }
}

// 获取当前用户信息
const fetchUserInfo = async () => {
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

    // 先获取完整的 User 信息（包括密码）
    const userResponse = await axios.get('/getUserById', {
      params: { id: userId }
    })

    if (userResponse.data.code === 0 && userResponse.data.data) {
      const user = userResponse.data.data
      formData.value.id = user.id
      formData.value.username = user.username || ''
      formData.value.email = user.email || ''
      formData.value.phone = user.phone || ''
      // 保存原密码，更新时需要使用
      originalData.value.password = user.password || ''
    } else {
      // 如果获取失败，使用 localStorage 中的数据
      formData.value.id = userId
      formData.value.username = data.username || ''
      formData.value.email = data.email || ''
      formData.value.phone = data.phone || ''
    }

    // 查询 UserInfo 信息
    const response = await axios.get('/getUserInfoByUserId', {
      params: { userId: userId }
    })

    if (response.data.code === 0 && response.data.data) {
      const userInfo = response.data.data
      formData.value.userInfoId = userInfo.id
      formData.value.userid = userInfo.userid
      formData.value.realname = userInfo.realname || ''
      formData.value.gender = userInfo.gender !== null && userInfo.gender !== undefined ? userInfo.gender : null
      formData.value.avatar = userInfo.avatar || ''
    } else {
      // 如果没有 UserInfo，使用 userData 中的信息
      if (data.userInfo) {
        formData.value.userInfoId = data.userInfo.id || null
        formData.value.userid = data.userInfo.userid || userId
        formData.value.realname = data.userInfo.realname || ''
        formData.value.gender = data.userInfo.gender !== null && data.userInfo.gender !== undefined ? data.userInfo.gender : null
        formData.value.avatar = data.userInfo.avatar || ''
      }
    }

    originalData.value = { ...formData.value }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('加载用户信息失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 编辑信息
const handleEdit = () => {
  isEditing.value = true
  originalData.value = { ...formData.value }
}

// 取消编辑
const handleCancel = () => {
  isEditing.value = false
  formData.value = { ...originalData.value }
}

// 保存信息
const handleSave = async () => {
  if (!formData.value.username) {
    ElMessage.warning('请输入用户名')
    return
  }
  
  if (!formData.value.id) {
    ElMessage.warning('未找到用户ID，无法更新')
    return
  }

  loading.value = true
  try {
    // 更新 User 表（始终使用原密码，密码修改在单独的标签页处理）
    const updateUserData = {
      id: formData.value.id,
      username: formData.value.username,
      email: formData.value.email || '',
      phone: formData.value.phone || '',
      password: originalData.value.password || '', // 使用原密码，密码修改在单独功能中处理
      status: 1, // 默认状态
      role: 'patient' // 患者角色
    }

    const userResponse = await axios.post('/updateUser', updateUserData, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    
    if (userResponse.data.code !== 0) {
      ElMessage.error('更新账户信息失败：' + (userResponse.data.msg || ''))
      loading.value = false
      return
    }

    // 更新或创建 UserInfo 表
    let userInfoResponse
    if (formData.value.userInfoId) {
      // 更新已有的 UserInfo
      const updateUserInfoData = {
        id: formData.value.userInfoId,
        userid: formData.value.userid || formData.value.id,
        realname: formData.value.realname || '',
        gender: formData.value.gender,
        avatar: formData.value.avatar || ''
      }
      userInfoResponse = await axios.post('/updateUserInfo', updateUserInfoData, {
        headers: {
          'Content-Type': 'application/json'
        }
      })
    } else {
      // 创建新的 UserInfo
      const addUserInfoData = {
        userid: formData.value.id,
        realname: formData.value.realname || '',
        gender: formData.value.gender,
        avatar: formData.value.avatar || ''
      }
      userInfoResponse = await axios.post('/addUserInfo', addUserInfoData, {
        headers: {
          'Content-Type': 'application/json'
        }
      })
    }
    
    if (userInfoResponse.data.code === 0) {
      ElMessage.success('更新个人信息成功')
      isEditing.value = false
      
      // 尝试同步更新Patient表中的患者信息
      await syncPatientInfo()
      
      // 重新获取完整的用户数据并更新localStorage
      await refreshUserData()
      
      // 重新加载表单数据
      await fetchUserInfo()
    } else {
      ElMessage.error('更新个人信息失败：' + (userInfoResponse.data.msg || ''))
    }
  } catch (error) {
    console.error('更新失败:', error)
    ElMessage.error('更新失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) {
      return false
    }
    
    // 如果新密码为空，则不修改密码
    if (!passwordForm.value.newPassword) {
      ElMessage.info('新密码为空，密码将保持不变')
      resetPasswordForm()
      return
    }
    
    passwordLoading.value = true
    try {
      // 验证旧密码是否正确
      const verifyResponse = await axios.get('/login', {
        params: {
          username: formData.value.username,
          password: passwordForm.value.oldPassword
        }
      })
      
      if (verifyResponse.data.code !== 0) {
        ElMessage.error('旧密码不正确')
        passwordLoading.value = false
        return
      }
      
      // 更新密码
      const updateUserData = {
        id: formData.value.id,
        username: formData.value.username,
        email: formData.value.email || '',
        phone: formData.value.phone || '',
        password: passwordForm.value.newPassword, // 使用新密码
        status: 1,
        role: 'patient'
      }
      
      const response = await axios.post('/updateUser', updateUserData, {
        headers: {
          'Content-Type': 'application/json'
        }
      })
      
      if (response.data.code === 0) {
        ElMessage.success('密码修改成功')
        // 更新原密码
        originalData.value.password = passwordForm.value.newPassword
        resetPasswordForm()
        // 刷新用户数据到localStorage
        await refreshUserData()
      } else {
        ElMessage.error('密码修改失败：' + (response.data.msg || ''))
      }
    } catch (error) {
      console.error('修改密码失败:', error)
      ElMessage.error('修改密码失败，请稍后重试')
    } finally {
      passwordLoading.value = false
    }
  })
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate()
  }
}

onMounted(() => {
  fetchUserInfo()
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

.profile-card {
  margin-top: 20px;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
  width: 120px;
}

:deep(.el-descriptions__content) {
  color: #3f5870;
}
</style>

