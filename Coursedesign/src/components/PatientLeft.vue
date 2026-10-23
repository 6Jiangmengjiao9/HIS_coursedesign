<template>
  <div class="left-container">
    <div class="logo">
      <el-icon class="logo-icon"><OfficeBuilding /></el-icon>
      <h1 class="system-title">患者服务</h1>
    </div>
    <el-menu
      :default-active="activeMenu"
      class="menu-container"
      background-color="transparent"
      text-color="#333"
      active-text-color="#2f80ed"
      @select="handleMenuSelect"
    >
      <el-menu-item index="/patient/reservation" class="menu-item">
        <el-icon><AlarmClock /></el-icon>
        <span>我的预约</span>
      </el-menu-item>
      <el-menu-item index="/patient/case" class="menu-item">
        <el-icon><Document /></el-icon>
        <span>我的病历</span>
      </el-menu-item>
      <el-menu-item index="/patient/prescription" class="menu-item">
        <el-icon><Tickets /></el-icon>
        <span>我的处方</span>
      </el-menu-item>
      <el-menu-item index="/patient/history" class="menu-item">
        <el-icon><Clock /></el-icon>
        <span>就诊历史</span>
      </el-menu-item>
      <el-menu-item index="/patient/schedule" class="menu-item">
        <el-icon><Calendar /></el-icon>
        <span>医生排班</span>
      </el-menu-item>
      <el-menu-item index="/patient/expense" class="menu-item">
        <el-icon><Money /></el-icon>
        <span>费用统计</span>
      </el-menu-item>
      <el-menu-item index="/patient/profile" class="menu-item">
        <el-icon><User /></el-icon>
        <span>个人信息</span>
      </el-menu-item>
      <el-menu-item index="/patient/ai-chat" class="menu-item">
        <el-icon><ChatDotRound /></el-icon>
        <span>AI 智能问诊</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  OfficeBuilding,
  AlarmClock,
  Document,
  Tickets,
  Clock,
  Calendar,
  Money,
  User,
  ChatDotRound
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)

const handleMenuSelect = (index) => {
  router.push(index)
}
</script>

<style scoped>
.left-container {
  padding: 18px 14px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #f7fbff 0%, #eef6ff 100%);
  border-right: 1px solid var(--his-border);
  overflow-y: auto;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 18px;
  padding: 0 6px 16px;
  border-bottom: 1px solid var(--his-border);
}

.logo-icon {
  font-size: 24px;
  color: var(--his-primary);
}

.system-title {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
  color: var(--his-primary-dark);
  letter-spacing: 0.01em;
}

.menu-container {
  background: transparent !important;
  border: none;
  flex: 1;
  padding: 0;
}

.menu-item {
  height: 46px !important;
  line-height: 46px !important;
  font-size: 14px;
  margin: 5px 2px !important;
  border-radius: 9px !important;
  padding: 0 14px !important;
  color: #51677d;
  background: transparent !important;
  transition: background 0.18s ease, color 0.18s ease;
}

.menu-item:hover {
  background: #eaf3ff !important;
  color: var(--his-primary-dark) !important;
}

.menu-item.is-active {
  background: #eaf3ff !important;
  color: var(--his-primary-dark) !important;
  font-weight: 600;
  box-shadow: inset 3px 0 0 var(--his-primary);
}

.el-icon {
  margin-right: 10px;
  font-size: 17px;
  color: #6b8db3;
}

.menu-item.is-active .el-icon {
  color: var(--his-primary-dark) !important;
}

.menu-item:hover .el-icon {
  color: var(--his-primary) !important;
}
</style>