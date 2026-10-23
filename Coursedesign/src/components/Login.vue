<template>
  <AuthLayout>
    <div class="auth-header">
      <h2>欢迎登录</h2>
      <p>请使用您的账号进入门诊信息系统</p>
    </div>

    <el-form class="auth-form" @keyup.enter="login" autocomplete="off">
      <el-form-item>
        <el-input
          v-model="username"
          size="large"
          placeholder="请输入用户名"
          clearable
          prefix-icon="User"
          autocomplete="off"
        />
      </el-form-item>
      <el-form-item>
        <el-input
          v-model="password"
          size="large"
          type="password"
          placeholder="请输入密码"
          show-password
          clearable
          prefix-icon="Lock"
          autocomplete="new-password"
        />
      </el-form-item>
      <el-form-item>
        <div class="captcha-row">
          <el-input
            v-model="captchaInput"
            size="large"
            placeholder="请输入验证码"
            maxlength="4"
            clearable
          />
          <div class="captcha-box" @click="refreshCaptcha" title="点击刷新验证码">
            <canvas ref="captchaCanvas" class="captcha-canvas"></canvas>
          </div>
        </div>
      </el-form-item>

      <div class="form-options">
        <el-checkbox v-model="remember">记住账号</el-checkbox>
        <span class="form-hint">忘记密码请联系管理员</span>
      </div>

      <el-button
        type="primary"
        size="large"
        round
        class="submit-btn gradient-btn"
        @click="login"
        :loading="loading"
      >
        {{ loading ? '登录中...' : '登录' }}
      </el-button>

      <div class="auth-switch">
        <span>还没有账号？</span>
        <el-link type="primary" :underline="false" @click="goRegister">立即注册</el-link>
      </div>
    </el-form>
  </AuthLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import AuthLayout from './AuthLayout.vue'

const router = useRouter()
const rememberedUsername = localStorage.getItem('rememberedUsername') || ''
const username = ref(rememberedUsername)
const password = ref('')
const role = ref('user')
const loading = ref(false)
const remember = ref(Boolean(rememberedUsername))

const captcha = ref('')
const captchaInput = ref('')
const captchaCanvas = ref(null)

const drawCaptcha = () => {
  const canvas = captchaCanvas.value
  if (!canvas) return

  const ctx = canvas.getContext('2d')
  const width = 110
  const height = 40
  canvas.width = width
  canvas.height = height

  ctx.fillStyle = '#f5faff'
  ctx.fillRect(0, 0, width, height)

  const text = captcha.value || ''
  for (let i = 0; i < text.length; i++) {
    const char = text[i]
    const fontSize = 20 + Math.floor(Math.random() * 4)
    const angle = (Math.random() - 0.5) * 0.6
    const x = 10 + i * 24
    const y = 27 + Math.random() * 6

    ctx.save()
    ctx.translate(x, y)
    ctx.rotate(angle)
    ctx.font = `${fontSize}px sans-serif`
    ctx.fillStyle = `rgb(${100 + Math.random() * 100}, ${80 + Math.random() * 120}, ${80 + Math.random() * 120})`
    ctx.fillText(char, 0, 0)
    ctx.restore()
  }

  for (let i = 0; i < 4; i++) {
    ctx.strokeStyle = `rgba(${50 + Math.random() * 150}, ${50 + Math.random() * 150}, ${50 + Math.random() * 150}, 0.6)`
    ctx.beginPath()
    ctx.moveTo(Math.random() * width, Math.random() * height)
    ctx.lineTo(Math.random() * width, Math.random() * height)
    ctx.stroke()
  }

  for (let i = 0; i < 30; i++) {
    ctx.fillStyle = `rgba(${50 + Math.random() * 150}, ${50 + Math.random() * 150}, ${50 + Math.random() * 150}, 0.7)`
    ctx.beginPath()
    ctx.arc(Math.random() * width, Math.random() * height, 0.8 + Math.random() * 1.2, 0, Math.PI * 2)
    ctx.fill()
  }
}

const generateCaptcha = () => {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  let code = ''
  for (let i = 0; i < 4; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  captcha.value = code
  drawCaptcha()
}

const refreshCaptcha = () => {
  generateCaptcha()
  captchaInput.value = ''
}

const goRegister = () => router.push('/register')

const login = async () => {
  if (!username.value || !password.value) {
    return ElMessage.warning('请输入用户名和密码')
  }
  if (!captchaInput.value) {
    return ElMessage.warning('请输入验证码')
  }
  if (captchaInput.value.toUpperCase() !== captcha.value.toUpperCase()) {
    ElMessage.warning('验证码错误，请重新输入')
    refreshCaptcha()
    return
  }

  loading.value = true
  try {
    const res = await axios.get('/login', {
      params: { username: username.value, password: password.value }
    })
    if (res.data.code === 0) {
      role.value = res.data.data.role
      localStorage.setItem('username', username.value)
      localStorage.setItem('role', role.value)
      localStorage.setItem('token', res.data.token)
      axios.defaults.headers.common.Authorization = `Bearer ${res.data.token}`

      if (remember.value) {
        localStorage.setItem('rememberedUsername', username.value)
      } else {
        localStorage.removeItem('rememberedUsername')
      }

      localStorage.setItem('userData', JSON.stringify(res.data.data))

      if (res.data.data.doctor && res.data.data.doctor.name) {
        localStorage.setItem('doctorName', res.data.data.doctor.name)
        localStorage.setItem('doctorId', res.data.data.doctor.doctorId)
      }

      if (role.value === 'doctor') {
        router.push('/user/overview')
      } else if (role.value === 'patient') {
        router.push('/patient/reservation')
      } else if (role.value === 'cashier') {
        router.push('/cashier')
      } else if (role.value === 'pharmacist') {
        router.push('/pharmacist')
      } else {
        router.push('/admin/patient')
      }
    } else {
      ElMessage.error('用户名或密码错误')
    }
  } catch (err) {
    console.error('登录失败:', err)
    ElMessage.error('登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  generateCaptcha()
})
</script>

<style scoped>
.auth-header {
  text-align: center;
  margin-bottom: 24px;
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
  letter-spacing: 0.2px;
}

.auth-header p {
  margin: 0;
  color: var(--his-text-secondary);
  font-size: 13px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.auth-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.captcha-row {
  display: flex;
  gap: 10px;
  align-items: center;
  width: 100%;
}

.captcha-box {
  width: 112px;
  height: 40px;
  border-radius: 9px;
  overflow: hidden;
  cursor: pointer;
  user-select: none;
  border: 1px solid var(--his-border);
  background: #f7fbff;
  flex-shrink: 0;
}

.captcha-canvas {
  width: 100%;
  height: 100%;
  display: block;
}

.form-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: -2px;
}

.form-options :deep(.el-checkbox__label) {
  color: var(--his-text-secondary);
  font-size: 13px;
}

.form-hint {
  color: var(--his-text-secondary);
  font-size: 12px;
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
  margin-top: 16px;
  text-align: center;
  color: var(--his-text-secondary);
  font-size: 13px;
}

.auth-switch :deep(.el-link) {
  font-size: 13px;
  font-weight: 600;
  margin-left: 4px;
}
</style>
