<template>
  <div class="common-layout">
    <el-card class="page-card">
          <div class="page-header">
            <div class="page-title">预约管理</div>
            <div class="header-actions">
              <el-button type="primary" @click="fetchReservations">刷新数据</el-button>
              <el-button type="success" @click="openAddReservationDialog">新增预约</el-button>
            </div>
          </div>

          <el-row :gutter="16" class="filter-row" style="margin-bottom: 8px;">
            <el-col :span="8" :xs="24" :sm="12" :md="10">
              <el-input
                v-model="searchKeyword"
                class="responsive-input"
                placeholder="请输入患者姓名或医生姓名"
                clearable
              />
            </el-col>
            <el-col :span="6" :xs="24" :sm="6" :md="5">
              <el-select v-model="statusFilter" placeholder="预约状态" clearable style="width: 100%;">
                <el-option label="全部" value=""></el-option>
                <el-option label="未预约" value="0"></el-option>
                <el-option label="已预约" value="1"></el-option>
              </el-select>
            </el-col>
            <el-col :span="6" :xs="24" :sm="6" :md="5">
              <el-select v-model="visitFilter" placeholder="就诊状态" clearable style="width: 100%;">
                <el-option label="全部" value=""></el-option>
                <el-option label="未就诊" value="0"></el-option>
                <el-option label="已就诊" value="1"></el-option>
              </el-select>
            </el-col>
          </el-row>

        <el-card class="reservation-card">
          <el-table 
            :data="filteredReservations" 
            style="width: 100%" 
            v-loading="loading"
            element-loading-text="数据加载中..."
          >
            <el-table-column prop="patientName" label="患者姓名" width="120" align="center" />
            <el-table-column prop="patientAge" label="年龄" width="80" align="center" />
            <el-table-column prop="patientPhone" label="联系电话" width="130" align="center" />
            <el-table-column prop="reservationTime" label="预约时间" width="200" align="center">
              <template #default="scope">
                {{ formatReservationTime(scope.row.reservationTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="departmentName" label="科室" width="150" align="center" />
            <el-table-column prop="doctorName" label="医生" width="120" align="center" />
            <el-table-column label="预约状态" width="120" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.reservationStatus === 0 ? 'warning' : 'success'">
                  {{ scope.row.reservationStatus === 0 ? '未预约' : '已预约' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="就诊状态" width="120" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.visitStatus === 0 ? 'info' : 'success'">
                  {{ scope.row.visitStatus === 0 ? '未就诊' : '已就诊' }}
                </el-tag>
              </template>
            </el-table-column>
            <!-- 操作列 -->
            <el-table-column label="操作" width="180" align="center" fixed="right">
              <template #default="scope">
                <el-button 
                  type="primary" 
                  size="small" 
                  style="margin-right: 5px;"
                  @click="openEditReservationDialog(scope.row)"
                >
                  编辑
                </el-button>
                <el-button 
                  type="danger" 
                  size="small" 
                  @click="handleDelete(scope.row)"
                  :loading="deleteLoading === scope.row.id"
                >
                  删除
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
              :total="totalReservations"
              @size-change="handlePageSizeChange"
              @current-change="handleCurrentPageChange"
            />
          </div>
        </el-card>
    </el-card>
  </div>
  
  <!-- 新增预约对话框 -->
  <el-dialog v-model="addReservationDialogVisible" :title="isEditMode ? '编辑预约' : '新增预约'" width="600px" @close="resetAddReservationForm">
    <el-form :model="addReservationForm" label-width="100px" ref="addReservationFormRef">
      <el-form-item label="科室" prop="departmentId" required>
        <el-select v-model="addReservationForm.departmentId" placeholder="请选择科室" style="width: 100%;" @change="handleDepartmentChange">
          <el-option
            v-for="dept in departmentOptions"
            :key="dept.value"
            :label="dept.label"
            :value="dept.value"
          ></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="医生" prop="doctorId" required>
        <el-select 
          v-model="addReservationForm.doctorId" 
          placeholder="请选择医生" 
          style="width: 100%;"
          :disabled="!addReservationForm.departmentId"
        >
          <el-option
            v-for="doctor in filteredDoctors"
            :key="doctor.doctorId"
            :label="doctor.departmentName ? `${doctor.departmentName}-${doctor.name}` : doctor.name"
            :value="doctor.doctorId"
          ></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="患者姓名" prop="patientName" required>
        <el-input v-model="addReservationForm.patientName" placeholder="请输入患者姓名" />
      </el-form-item>
      
      <el-form-item label="患者年龄" prop="patientAge" required>
        <el-input-number v-model="addReservationForm.patientAge" :min="0" :max="150" style="width: 100%;" />
      </el-form-item>
      
      <el-form-item label="联系电话" prop="patientPhone" required>
        <el-input v-model="addReservationForm.patientPhone" placeholder="请输入联系电话" />
      </el-form-item>
      
      <el-form-item label="预约状态" prop="reservationStatus" required>
        <el-select v-model="addReservationForm.reservationStatus" placeholder="请选择预约状态" style="width: 100%;">
          <el-option label="未预约" :value="0"></el-option>
          <el-option label="已预约" :value="1"></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="就诊状态" prop="visitStatus" required>
        <el-select v-model="addReservationForm.visitStatus" placeholder="请选择就诊状态" style="width: 100%;">
          <el-option label="未就诊" :value="0"></el-option>
          <el-option label="已就诊" :value="1"></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="预约时间" prop="reservationTime" required>
        <el-date-picker
          v-model="addReservationForm.reservationTime"
          type="datetime"
          placeholder="请选择预约时间"
          style="width: 100%;"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="addReservationDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReservation">{{ isEditMode ? '更新' : '确定' }}</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import { API_BASE } from '../apiBase'

export default {
  name: 'ReservationList',
  data() {
    return {
      reservations: [],
      loading: false,
      deleteLoading: null, // 用于跟踪正在删除的行
      searchKeyword: '',
      statusFilter: '',
      visitFilter: '',
      // 分页相关
      currentPage: 1,
      pageSize: 5,
      totalReservations: 0,
      // 新增预约对话框相关数据
      addReservationDialogVisible: false,
      isEditMode: false, // 是否为编辑模式
      currentEditId: null, // 当前编辑的记录ID
      addReservationForm: {
        departmentId: '',
        doctorId: '',
        patientName: '',
        patientAge: null,
        patientPhone: '',
        reservationStatus: 1, // 默认已预约
        visitStatus: 0,       // 默认未就诊
        reservationTime: ''
      },
      // 科室选项
      departmentOptions: [
        { value: 'KS001', label: '心血管内科' },
        { value: 'KS002', label: '骨科' },
        { value: 'KS003', label: '儿科' },
        { value: 'KS004', label: '神经内科' },
        { value: 'KS005', label: '妇产科' }
      ],
      // 所有医生数据
      allDoctors: []
    }
  },
  computed: {
    filteredReservations() {
      let filtered = this.reservations;
      
      // 搜索过滤
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase();
        filtered = filtered.filter(item => 
          item.patientName.toLowerCase().includes(keyword) ||
          item.doctorName.toLowerCase().includes(keyword)
        );
      }
      
      // 预约状态过滤
      if (this.statusFilter !== '') {
        filtered = filtered.filter(item => 
          item.reservationStatus === parseInt(this.statusFilter)
        );
      }
      
      // 就诊状态过滤
      if (this.visitFilter !== '') {
        filtered = filtered.filter(item => 
          item.visitStatus === parseInt(this.visitFilter)
        );
      }
      
      return filtered;
    },
    // 根据选择的科室过滤医生
    filteredDoctors() {
      if (!this.addReservationForm.departmentId) {
        return [];
      }
      return this.allDoctors.filter(doctor => 
        doctor.departmentId === this.addReservationForm.departmentId
      );
    }
  },
  methods: {
    async fetchReservations(page = this.currentPage, size = this.pageSize) {
      // 确保参数是数字类型
      const pageNum = typeof page === 'number' ? page : parseInt(page) || this.currentPage
      const pageSizeNum = typeof size === 'number' ? size : parseInt(size) || this.pageSize
      
      this.loading = true
      try {
        const response = await fetch(`${API_BASE}/getReservationPage?pageNum=${pageNum}&pageSize=${pageSizeNum}`, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token') || ''}` }
        })
        const result = await response.json()
        if (result.code === 0) {
          this.reservations = result.data || []
          this.totalReservations = result.total || 0
          this.currentPage = result.pageNum || page
          this.pageSize = result.pageSize || size
        }
      } catch (error) {
        console.error('获取预约数据失败:', error)
        this.$message({
          message: '数据加载失败，请稍后重试',
          type: 'error'
        })
      } finally {
        this.loading = false
      }
    },
    // 处理每页数量变化
    handlePageSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.fetchReservations()
    },
    // 处理当前页变化
    handleCurrentPageChange(val) {
      this.currentPage = val
      this.fetchReservations()
    },
    formatReservationTime(time) {
      if (!time) return '无预约时间'
      const date = new Date(time)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    // 获取所有医生信息用于下拉选择
    async fetchAllDoctors() {
      try {
        const response = await fetch(`${API_BASE}/getAllDoctor`, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token') || ''}` }
        })
        const result = await response.json()
        if (result.code === 0) {
          this.allDoctors = result.data.map(item => ({
            doctorId: item.doctorId,
            name: item.name,
            departmentId: item.departmentId,
            departmentName: item.department?.departmentName || ''
          }))
        }
      } catch (error) {
        console.error('获取医生数据失败:', error)
        this.$message({
          message: '医生数据加载失败',
          type: 'error'
        })
      }
    },
    // 打开新增预约对话框
    openAddReservationDialog() {
      this.isEditMode = false
      this.currentEditId = null
      this.addReservationDialogVisible = true
      // 重置表单
      this.resetAddReservationForm()
    },
    // 打开编辑预约对话框
    openEditReservationDialog(row) {
      this.isEditMode = true
      this.currentEditId = row.id
      this.addReservationDialogVisible = true
      
      // 填充表单数据
      this.addReservationForm = {
        departmentId: row.departmentId,
        doctorId: row.doctorId,
        patientName: row.patientName,
        patientAge: row.patientAge,
        patientPhone: row.patientPhone,
        reservationStatus: row.reservationStatus,
        visitStatus: row.visitStatus,
        reservationTime: row.reservationTime
      }
    },
    // 重置新增预约表单
    resetAddReservationForm() {
      this.addReservationForm = {
        departmentId: '',
        doctorId: '',
        patientName: '',
        patientAge: null,
        patientPhone: '',
        reservationStatus: 1,
        visitStatus: 0,
        reservationTime: ''
      }
    },
    // 科室选择变化时清空医生选择
    handleDepartmentChange() {
      this.addReservationForm.doctorId = ''
    },
    // 提交预约信息
async submitReservation() {
  // 表单验证
  if (!this.addReservationForm.departmentId || 
      !this.addReservationForm.doctorId || 
      !this.addReservationForm.patientName || 
      this.addReservationForm.patientAge === null || 
      !this.addReservationForm.patientPhone || 
      !this.addReservationForm.reservationTime) {
    this.$message({
      message: '请填写所有必填项',
      type: 'warning'
    })
    return
  }
  
  try {
    // 确保所有字段都有有效值
    const requestData = {
      departmentId: this.addReservationForm.departmentId,
      doctorId: parseInt(this.addReservationForm.doctorId),
      patientName: this.addReservationForm.patientName,
      patientAge: parseInt(this.addReservationForm.patientAge),
      patientPhone: this.addReservationForm.patientPhone,
      reservationStatus: parseInt(this.addReservationForm.reservationStatus),
      visitStatus: parseInt(this.addReservationForm.visitStatus),
      reservationTime: this.addReservationForm.reservationTime
    };
    
    let url, method;
    if (this.isEditMode) {
      // 编辑模式 - 使用 PUT 方法，并将 ID 包含在请求体中而不是 URL 参数
      url = '/updateReservation';
      method = 'POST'; // 或者根据后端要求改为 PUT
      // 在编辑模式下添加 ID 到请求数据中
      requestData.id = this.currentEditId;
    } else {
      // 新增模式
      url = '/addReservation';
      method = 'POST';
    }
    
    const response = await fetch(`${API_BASE}${url}`, {
      method: method,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('token') || ''}`
      },
      body: JSON.stringify(requestData)
    })
    
    const result = await response.json()
    if (result.code === 0) {
      this.$message({
        message: this.isEditMode ? '预约更新成功' : '预约添加成功',
        type: 'success'
      })
      this.addReservationDialogVisible = false
      this.fetchReservations() // 刷新预约列表
    } else {
      this.$message({
        message: (this.isEditMode ? '预约更新失败: ' : '预约添加失败: ') + result.msg,
        type: 'error'
      })
    }
  } catch (error) {
    console.error(this.isEditMode ? '更新预约失败:' : '提交预约失败:', error)
    this.$message({
      message: this.isEditMode ? '预约更新失败，请稍后重试' : '预约提交失败，请稍后重试',
      type: 'error'
    })
  }
},
    // 删除预约记录
    async handleDelete(row) {
      try {
        // 确认删除
        await this.$confirm('确定要删除这条预约记录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        // 设置删除加载状态
        this.deleteLoading = row.id
        
        // 发送删除请求 - 使用查询参数传递 id
        const response = await fetch(`${API_BASE}/deleteReservation?id=${row.id}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${localStorage.getItem('token') || ''}`
          }
        })
        
        const result = await response.json()
        if (result.code === 0) {
          this.$message({
            message: '删除成功',
            type: 'success'
          })
          // 刷新数据
          this.fetchReservations()
        } else {
          this.$message({
            message: '删除失败: ' + result.msg,
            type: 'error'
          })
        }
      } catch (error) {
        // 如果是用户取消删除，不显示错误信息
        if (error !== 'cancel') {
          console.error('删除预约失败:', error)
          this.$message({
            message: '删除失败，请稍后重试',
            type: 'error'
          })
        }
      } finally {
        // 清除删除加载状态
        this.deleteLoading = null
      }
    }
  },
  mounted() {
    this.fetchReservations()
    this.fetchAllDoctors()
  }
}
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

.el-header {
  background-color: #ffffff;
  color: var(--el-text-color-primary);
  text-align: center;
  line-height: 60px;
  border-bottom: 1px solid #e6f0fa;
}

.el-main {
  background-color: #f5faff;
  padding: 20px;
  min-height: calc(100vh - 60px);
}

.reservation-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.header-actions {
  display: flex;
  gap: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.responsive-input {
  width: 100%;
}

/* 为操作按钮添加一些间距 */
.el-button + .el-button {
  margin-left: 8px;
}

.common-layout {
  background: transparent;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>