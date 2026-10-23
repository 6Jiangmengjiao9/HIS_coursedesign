<template>
  <div class="left-container">
    <div class="logo">
      <el-icon class="logo-icon"><OfficeBuilding /></el-icon>
      <h1 class="system-title">门诊管理</h1>
    </div>
    <el-menu
      :default-active="activeMenu"
      class="menu-container"
      background-color="transparent"
      text-color="#333"
      active-text-color="#2f80ed"
      @select="handleMenuSelect"
    >
      <el-menu-item index="/admin/patient" class="menu-item">
        <el-icon><User /></el-icon>
        <span>患者信息管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/doctor" class="menu-item">
        <el-icon><UserFilled /></el-icon>
        <span>医生信息管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/department" class="menu-item">
        <el-icon><OfficeBuilding /></el-icon>
        <span>科室信息管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/reservation" class="menu-item">
        <el-icon><AlarmClock /></el-icon>
        <span>患者预约管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/drug" class="menu-item">
        <el-icon><Goods /></el-icon>
        <span>药品信息管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/schedule" class="menu-item">
        <el-icon><Calendar /></el-icon>
        <span>医生排班管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/statistics" class="menu-item">
        <el-icon><DataAnalysis /></el-icon>
        <span>统计分析</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  User,
  UserFilled,
  OfficeBuilding,
  AlarmClock,
  DataAnalysis,
  Goods,
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