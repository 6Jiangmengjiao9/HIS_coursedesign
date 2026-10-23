<template>
  <AuthLayout wide>
    <div class="auth-header">
      <h2>患者账号注册</h2>
      <p>注册后可预约挂号、查看处方与费用</p>
    </div>

    <el-form class="auth-form" label-position="top" autocomplete="off">
      <div class="form-grid">
        <el-form-item label="用户名">
          <el-input v-model="form.username" size="large" placeholder="请输入用户名" clearable prefix-icon="User" autocomplete="off" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" size="large" type="password" show-password placeholder="请输入密码" clearable prefix-icon="Lock" autocomplete="new-password" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="form.confirmPassword" size="large" type="password" show-password placeholder="请再次输入密码" clearable prefix-icon="Lock" autocomplete="new-password" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realname" size="large" placeholder="请输入真实姓名" clearable prefix-icon="UserFilled" autocomplete="off" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" size="large" placeholder="请选择性别" style="width: 100%;" clearable>
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" size="large" placeholder="请输入手机号" clearable prefix-icon="Iphone" autocomplete="off" />
        </el-form-item>
        <el-form-item label="邮箱" class="span-2">
          <el-input v-model="form.email" size="large" placeholder="请输入邮箱" clearable prefix-icon="Message" autocomplete="off" />
        </el-form-item>
      </div>

      <div class="form-note">
        <el-icon><Lock /></el-icon>
        <span>您的信息仅用于门诊诊疗服务，系统将严格保护个人隐私。</span>
      </div>

      <el-button
        type="primary"
        size="large"
        round
        class="submit-btn gradient-btn"
        @click="updateperson"
        :loading="loading"
      >
        {{ loading ? '注册中...' : '完成注册' }}
      </el-button>

      <div class="auth-switch">
        <span>已有账号？</span>
        <el-link type="primary" :underline="false" @click="goLogin">返回登录</el-link>
      </div>
    </el-form>
  </AuthLayout>
</template>

<script setup>
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AuthLayout from './AuthLayout.vue'

const router = useRouter()

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  realname: '',
  gender: null
})

const loading = ref(false)

const goLogin = () => router.push('/login')

const updateperson = async () => {
  if (!form.username || !form.password || !form.confirmPassword || !form.email
    || !form.phone || !form.realname || !form.gender) {
    return ElMessage.warning('请完整填写注册信息')
  }
  if (form.password.length < 6) {
    return ElMessage.warning('密码长度不能少于 6 位')
  }
  if (form.password !== form.confirmPassword) {
    return ElMessage.warning('两次输入的密码不一致')
  }
  if (!/^1\d{10}$/.test(form.phone)) {
    return ElMessage.warning('请输入正确的手机号')
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    return ElMessage.warning('请输入正确的邮箱地址')
  }

  loading.value = true
  try {
    const res = await axios.get('/register', {
      params: {
        username: form.username,
        password: form.password,
        email: form.email,
        phone: form.phone,
        realname: form.realname,
        gender: form.gender
      }
    })
    if (res.data.code === 0) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error('注册失败：' + (res.data.msg || '请稍后重试'))
    }
  } catch (err) {
    console.error('注册失败:', err)
    ElMessage.error('注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-header {
  text-align: center;
  margin-bottom: 22px;
  display: flex;
  flex-direction: column;
  gap: 9px;
  align-items: center;
}

.auth-header h2 {
  margin: 4px 0 0;
  font-size: 24px;
  font-weight: 700;
  color: var(--his-text);
}

.auth-header p {
  margin: 0;
  color: var(--his-text-secondary);
  font-size: 13px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

.form-grid :deep(.el-form-item) {
  margin-bottom: 12px;
}

.form-grid .span-2 {
  grid-column: 1 / -1;
}

.form-grid :deep(.el-form-item__label) {
  padding-bottom: 5px;
  color: var(--his-text-regular);
  font-size: 13px;
  font-weight: 500;
}

.form-note {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 9px;
  background: #f5faff;
  border: 1px solid #e2eefb;
  color: var(--his-text-secondary);
  font-size: 12px;
  line-height: 1.6;
}

.form-note .el-icon {
  margin-top: 2px;
  color: var(--his-primary);
  flex-shrink: 0;
}

.submit-btn {
  width: 100%;
  height: 46px;
  margin-top: 4px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

.gradient-btn {
  background: linear-gradient(135deg, #2f80ed 0%, #56ccf2 100%);
  border: none;
  box-shadow: 0 10px 24px rgba(47, 128, 237, 0.24);
}

.gradient-btn:hover {
  filter: brightness(1.03);
  box-shadow: 0 12px 28px rgba(47, 128, 237, 0.3);
}

.auth-switch {
  margin-top: 12px;
  text-align: center;
  color: var(--his-text-secondary);
  font-size: 13px;
}

.auth-switch :deep(.el-link) {
  font-size: 13px;
  font-weight: 600;
  margin-left: 4px;
}

@media (max-width: 640px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-grid .span-2 {
    grid-column: auto;
  }
}
</style>
