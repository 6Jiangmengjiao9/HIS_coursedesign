<template>
  <div class="admin-shell">
    <el-row class="topbar" align="middle" justify="space-between">
      <div class="brand">
        <div class="logo-container">
          <img src="/hospital-logo.svg" alt="医院标志" class="hospital-logo" />
        </div>
        <div class="brand-text">
          <h3>门诊管理后台</h3>
          <p class="subtitle">仁心医院 · 门诊信息系统</p>
        </div>
      </div>
      <div class="user-actions">
        <template v-if="username != ''">
          <el-dropdown>
            <span class="el-dropdown-link">
              <el-icon style="margin-right: 8px;"><UserIcon /></el-icon>
              {{ username }}
              <el-icon class="el-icon--right">
                <ArrowDown />
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>修改密码</el-dropdown-item>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button @click="gotoLogin">登录</el-button>
          <el-button type="primary" @click="gotoRegister">注册</el-button>
        </template>
      </div>
    </el-row>

    <el-container class="body-container">
      <el-aside width="240px" class="aside">
        <admin-left />
      </el-aside>
      <el-main class="main-area">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import AdminLeft from './AdminLeft.vue'
import { User as UserIcon, ArrowDown } from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
// 优先显示医生姓名，如果没有则显示用户名
const username = ref('')

// 初始化用户名显示
const initUsername = () => {
  const doctorName = localStorage.getItem('doctorName')
  let realName = ''
  try {
    const userData = JSON.parse(localStorage.getItem('userData') || '{}')
    realName = userData.userInfo?.realname || ''
  } catch (e) {
    realName = ''
  }
  username.value = doctorName || realName || localStorage.getItem('username') || ''
}

onMounted(() => {
  initUsername()
})

function gotoLogin() {
  router.push('/login')
}

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  localStorage.removeItem('doctorName')
  localStorage.removeItem('doctorId')
  localStorage.removeItem('userData')
  username.value = ''
  router.push('/login')
}

function gotoRegister() {
  router.push('/register')
}
</script>

<style scoped>
.admin-shell {
  min-height: 100vh;
  background: transparent;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.topbar {
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid var(--his-border);
  border-radius: var(--his-radius);
  padding: 14px 22px;
  box-shadow: var(--his-shadow-sm);
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
}

.logo-container {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  padding: 9px;
  background: linear-gradient(135deg, #2f80ed 0%, #56ccf2 100%);
  box-shadow: 0 6px 16px rgba(47, 128, 237, 0.22);
}

.hospital-logo {
  width: 100%;
  height: 100%;
  filter: brightness(0) invert(1);
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand h3 {
  margin: 0;
  font-size: 19px;
  font-weight: 700;
  color: var(--his-text);
}

.subtitle {
  margin: 3px 0 0;
  color: var(--his-text-secondary);
  font-size: 12px;
  letter-spacing: 0.02em;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: var(--his-text);
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background 0.18s ease, color 0.18s ease;
}

.el-dropdown-link:hover {
  background: var(--his-primary-light);
  color: var(--his-primary-dark);
}

.body-container {
  background: var(--his-surface);
  border-radius: var(--his-radius);
  border: 1px solid var(--his-border);
  box-shadow: var(--his-shadow-md);
  overflow: hidden;
  min-height: calc(100vh - 140px);
}

.aside {
  background: #f7fbff;
}

.main-area {
  background: #f5faff;
  padding: 20px;
}

@media (max-width: 900px) {
  .topbar {
    padding: 12px 16px;
  }

  .body-container {
    min-height: auto;
  }

  .main-area {
    padding: 14px;
  }
}
</style>