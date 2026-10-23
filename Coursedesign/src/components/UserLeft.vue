<template>
  <div class="left-container">
    <div class="logo">
      <el-icon class="logo-icon"><OfficeBuilding /></el-icon>
      <h1 class="system-title">医生工作站</h1>
    </div>
    <el-menu
      :default-active="activeMenu"
      class="menu-container"
      background-color="transparent"
      text-color="#333"
      active-text-color="#2f80ed"
      @select="handleMenuSelect"
    >
      <el-menu-item index="/user/overview" class="menu-item">
        <el-icon><DataAnalysis /></el-icon>
        <span>系统概览</span>
      </el-menu-item>
      <el-menu-item index="/user/reservations" class="menu-item">
        <el-icon><AlarmClock /></el-icon>
        <span>患者预约管理</span>
      </el-menu-item>
      <el-menu-item index="/user/patient" class="menu-item">
        <el-icon><UserIcon /></el-icon>
        <span>患者信息管理</span>
      </el-menu-item>
      <el-menu-item index="/user/medical-record" class="menu-item">
        <el-icon><Document /></el-icon>
        <span>病历录入与查询</span>
      </el-menu-item>
      <el-menu-item index="/user/prescription" class="menu-item">
        <el-icon><Tickets /></el-icon>
        <span>处方开具与管理</span>
      </el-menu-item>
      <el-menu-item index="/user/schedule" class="menu-item">
        <el-icon><Calendar /></el-icon>
        <span>排班表</span>
      </el-menu-item>
       <el-menu-item index="/user/diagnosis" class="menu-item">
        <el-icon><EditPen /></el-icon>
        <span>医生诊断记录</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  OfficeBuilding,
  DataAnalysis,
  User as UserIcon,
  Document,
  EditPen,
  Tickets,
  AlarmClock,
  Calendar
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