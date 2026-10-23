<template>
  <div class="common-layout">
        <!-- 数据概览 -->
        <el-card class="overview-card">
          <template #header>
            <div class="card-header">
              <span>数据概览</span>
              <el-button @click="fetchStatistics" :loading="loading">刷新数据</el-button>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-value">{{ statistics.totalPatients }}</div>
                  <div class="stat-label">总患者数</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-value">{{ statistics.totalDoctors }}</div>
                  <div class="stat-label">总医生数</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-value">{{ statistics.totalDepartments }}</div>
                  <div class="stat-label">总科室数</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-value">{{ statistics.totalReservations }}</div>
                  <div class="stat-label">总预约数</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-row :gutter="20" style="margin-top: 12px;">
            <el-col :span="6">
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-value">{{ statistics.totalDrugs }}</div>
                  <div class="stat-label">在售药品数</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-value">{{ statistics.totalDrugStock }}</div>
                  <div class="stat-label">药品总库存</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>

        <!-- 图表区域 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>各科室医生分布</span>
                </div>
              </template>
              <div ref="doctorChart" class="chart-container"></div>
            </el-card>
          </el-col>
          
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>预约状态统计</span>
                </div>
              </template>
              <div ref="reservationChart" class="chart-container"></div>
            </el-card>
          </el-col>
        </el-row>
        
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>就诊状态统计</span>
                </div>
              </template>
              <div ref="visitChart" class="chart-container"></div>
            </el-card>
          </el-col>
          
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>患者性别分布</span>
                </div>
              </template>
              <div ref="patientGenderChart" class="chart-container"></div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="24">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>药品库存与分类分析</span>
                </div>
              </template>
              <div ref="drugChart" class="chart-container"></div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 详细统计数据表格 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>详细统计数据</span>
            </div>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="科室统计" name="department">
              <el-table :data="departmentStats" style="width: 100%">
                <el-table-column prop="departmentName" label="科室名称" width="150" />
                <el-table-column prop="doctorCount" label="医生数量" width="100" />
                <el-table-column prop="reservationCount" label="预约数量" width="100" />
                <el-table-column prop="completedVisits" label="已完成就诊" width="120" />
                <el-table-column label="预约完成率">
                  <template #default="scope">
                    {{ calculatePercentage(scope.row.completedVisits, scope.row.reservationCount) }}
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            
            <el-tab-pane label="医生统计" name="doctor">
              <el-table :data="doctorStats" style="width: 100%">
                <el-table-column prop="doctorName" label="医生姓名" width="120" />
                <el-table-column prop="departmentName" label="所属科室" width="150" />
                <el-table-column prop="reservationCount" label="预约数量" width="100" />
                <el-table-column prop="completedVisits" label="已完成就诊" width="120" />
                <el-table-column label="预约完成率">
                  <template #default="scope">
                    {{ calculatePercentage(scope.row.completedVisits, scope.row.reservationCount) }}
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="药品统计" name="drug">
              <el-table :data="drugStats" style="width: 100%">
                <el-table-column prop="drugName" label="药品名称" min-width="160" />
                <el-table-column prop="drugType" label="分类" width="120" />
                <el-table-column prop="dosageForm" label="剂型" width="100" />
                <el-table-column prop="specification" label="规格" min-width="160" show-overflow-tooltip />
                <el-table-column prop="unitPrice" label="单价(元)" width="120">
                  <template #default="scope">
                    ¥{{ parseFloat(scope.row.unitPrice || 0).toFixed(2) }}
                  </template>
                </el-table-column>
                <el-table-column prop="stockQuantity" label="库存" width="100" />
                <el-table-column prop="statusText" label="状态" width="100" />
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

// 加载状态
const loading = ref(false)

// 数据统计
const statistics = ref({
  totalPatients: 0,
  totalDoctors: 0,
  totalDepartments: 0,
  totalReservations: 0,
  totalDrugs: 0,
  totalDrugStock: 0
})

// 图表引用
const doctorChart = ref(null)
const reservationChart = ref(null)
const visitChart = ref(null)
const patientGenderChart = ref(null)
const drugChart = ref(null)

// 详细统计数据
const departmentStats = ref([])
const doctorStats = ref([])
const drugStats = ref([])
const activeTab = ref('department')

// 固定的科室信息
const fixedDepartments = [
  {
    departmentId: 'KS001',
    departmentName: '心血管内科',
    introduction: '擅长冠心病、心肌梗死、心律失常等疾病的诊治，拥有先进的心脏介入治疗设备',
    location: '住院部4楼东侧',
    phone: '010-88881001'
  },
  {
    departmentId: 'KS002',
    departmentName: '骨科',
    introduction: '专注骨折、关节炎、脊柱疾病等诊治，开展微创手术和人工关节置换术',
    location: '住院部2楼西侧',
    phone: '010-88881002'
  },
  {
    departmentId: 'KS003',
    departmentName: '儿科',
    introduction: '针对0-14岁儿童常见疾病（如感冒、肺炎、腹泻）及疑难病症的诊治',
    location: '门诊楼2楼东侧',
    phone: '010-88881003'
  },
  {
    departmentId: 'KS004',
    departmentName: '神经内科',
    introduction: '诊治脑血管病、癫痫、帕金森等神经系统疾病，配备神经电生理检查设备',
    location: '住院部4楼东侧',
    phone: '010-88881004'
  },
  {
    departmentId: 'KS005',
    departmentName: '妇产科',
    introduction: '涵盖妇科疾病治疗、产前检查、产科分娩等业务，设VIP产房',
    location: '住院部5楼西侧',
    phone: '010-88881005'
  }
]

// 固定的医生信息（与科室对应）
const fixedDoctors = [
  { doctorId: 1, name: '张伟', departmentId: 'KS001', departmentName: '心血管内科', title: '主任医师' },
  { doctorId: 2, name: '李娜', departmentId: 'KS005', departmentName: '妇产科', title: '副主任医师' },
  { doctorId: 3, name: '王强', departmentId: 'KS002', departmentName: '骨科', title: '主任医师' },
  { doctorId: 4, name: '赵敏', departmentId: 'KS002', departmentName: '骨科', title: '主治医师' },
  { doctorId: 5, name: '刘洋', departmentId: 'KS003', departmentName: '儿科', title: '副主任医师' },
  { doctorId: 6, name: '孙俪', departmentId: 'KS003', departmentName: '儿科', title: '住院医师' },
  { doctorId: 7, name: '陈明', departmentId: 'KS004', departmentName: '神经内科', title: '主任医师' }
]

// 计算百分比
const calculatePercentage = (part, total) => {
  if (total === 0) return '0%'
  return `${Math.round((part / total) * 100)}%`
}

// 获取统计数据
const fetchStatistics = async () => {
  loading.value = true
  
  try {
    console.log('开始获取统计数据...')
    
    // 使用固定的科室和医生信息
    statistics.value.totalDepartments = fixedDepartments.length
    statistics.value.totalDoctors = fixedDoctors.length
    
    // 尝试获取患者、预约和药品数据
    let patientsData = []
    let reservationsData = []
    let drugsData = []
    
    try {
      const patientsRes = await axios.get('/getAllPatient')
      console.log('患者API响应:', patientsRes)
      patientsData = patientsRes.data && patientsRes.data.code === 0 ? patientsRes.data.data : []
    } catch (error) {
      console.error('获取患者数据失败:', error)
      // 使用模拟患者数据
      patientsData = [
        { id: 1, name: '张三', gender: '男', age: 35 },
        { id: 2, name: '李四', gender: '女', age: 28 },
        { id: 3, name: '王五', gender: '男', age: 45 },
        { id: 4, name: '赵六', gender: '女', age: 32 },
        { id: 5, name: '钱七', gender: '男', age: 50 }
      ]
    }
    
    try {
      const reservationsRes = await axios.get('/getAllReservation')
      console.log('预约API响应:', reservationsRes)
      reservationsData = reservationsRes.data && reservationsRes.data.code === 0 ? reservationsRes.data.data : []
    } catch (error) {
      console.error('获取预约数据失败:', error)
      // 使用模拟预约数据
      reservationsData = [
        { id: 1, departmentId: 'KS001', doctorId: 1, reservationStatus: 1, visitStatus: 1 },
        { id: 2, departmentId: 'KS002', doctorId: 3, reservationStatus: 1, visitStatus: 0 },
        { id: 3, departmentId: 'KS003', doctorId: 5, reservationStatus: 0, visitStatus: 0 },
        { id: 4, departmentId: 'KS004', doctorId: 7, reservationStatus: 1, visitStatus: 1 },
        { id: 5, departmentId: 'KS005', doctorId: 2, reservationStatus: 1, visitStatus: 0 },
        { id: 6, departmentId: 'KS001', doctorId: 1, reservationStatus: 1, visitStatus: 1 },
        { id: 7, departmentId: 'KS002', doctorId: 4, reservationStatus: 0, visitStatus: 0 }
      ]
    }

    // 获取药品数据
    try {
      const drugRes = await axios.get('/getAllDrug')
      console.log('药品API响应:', drugRes)
      drugsData = drugRes.data && drugRes.data.code === 0 ? drugRes.data.data : []
    } catch (error) {
      console.error('获取药品数据失败:', error)
      drugsData = []
    }
    
    // 更新统计数据
    statistics.value.totalPatients = patientsData.length
    statistics.value.totalReservations = reservationsData.length
    statistics.value.totalDrugs = Array.isArray(drugsData) ? drugsData.filter(d => d.status === 1).length : 0
    statistics.value.totalDrugStock = Array.isArray(drugsData)
      ? drugsData.reduce((sum, d) => sum + (d.stockQuantity || 0), 0)
      : 0
    
    // 处理统计表格数据
    processStatsData(fixedDepartments, fixedDoctors, reservationsData)

    // 处理药品统计数据
    drugStats.value = Array.isArray(drugsData)
      ? drugsData.map(d => ({
          drugName: d.drugName,
          drugType: d.drugType,
          dosageForm: d.dosageForm,
          specification: d.specification,
          unitPrice: d.unitPrice,
          stockQuantity: d.stockQuantity,
          status: d.status,
          statusText: d.status === 1 ? '在售' : '下架'
        }))
      : []
    
    // 等待DOM更新后绘制图表
    await nextTick()
    drawCharts(reservationsData, patientsData, drugsData)
    
    console.log('数据获取完成')
    
  } catch (error) {
    console.error('获取统计数据失败:', error)
    // 即使出错也使用固定数据展示
    useFixedData()
  } finally {
    loading.value = false
  }
}

// 处理统计表格数据
const processStatsData = (departmentsData, doctorsData, reservationsData) => {
  console.log('处理统计表格数据...')
  
  // 处理科室统计
  departmentStats.value = departmentsData.map(dept => {
    // 计算该科室的医生数量
    const doctorCount = doctorsData.filter(doctor => 
      doctor.departmentId === dept.departmentId
    ).length
    
    // 计算该科室的预约数量
    const deptReservations = reservationsData.filter(res => 
      res.departmentId === dept.departmentId
    )
    const reservationCount = deptReservations.length
    const completedVisits = deptReservations.filter(res => 
      res.visitStatus === 1
    ).length
    
    return {
      departmentName: dept.departmentName,
      doctorCount,
      reservationCount,
      completedVisits
    }
  })
  
  console.log('科室统计数据:', departmentStats.value)
  
  // 处理医生统计
  doctorStats.value = doctorsData.map(doctor => {
    const doctorReservations = reservationsData.filter(res => 
      res.doctorId === doctor.doctorId
    )
    
    const reservationCount = doctorReservations.length
    const completedVisits = doctorReservations.filter(res => 
      res.visitStatus === 1
    ).length
    
    return {
      doctorName: doctor.name,
      departmentName: doctor.departmentName,
      reservationCount,
      completedVisits
    }
  })
  
  console.log('医生统计数据:', doctorStats.value)
}

// 绘制图表
const drawCharts = (reservationsData, patientsData, drugsData) => {
  console.log('开始绘制图表...')
  
  // 1. 科室医生分布图
  if (doctorChart.value && departmentStats.value.length > 0) {
    try {
      const doctorChartInstance = echarts.init(doctorChart.value)
      const option1 = {
        color: ['#2f80ed', '#56ccf2', '#7fb3f5', '#b5d7ff', '#1b64c7'],
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          top: '5%',
          left: 'center'
        },
        series: [
          {
            name: '医生数量',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: departmentStats.value.map(stat => ({
              value: stat.doctorCount,
              name: stat.departmentName
            }))
          }
        ]
      }
      doctorChartInstance.setOption(option1)
      
      window.addEventListener('resize', () => {
        doctorChartInstance.resize()
      })
    } catch (error) {
      console.error('绘制科室医生分布图失败:', error)
    }
  }

  // 2. 预约状态统计图
  if (reservationChart.value && Array.isArray(reservationsData)) {
    try {
      const reservedCount = reservationsData.filter(res => res.reservationStatus === 1).length
      const notReservedCount = reservationsData.filter(res => res.reservationStatus === 0).length
      
      console.log('预约状态统计:', { reservedCount, notReservedCount })
      
      const reservationChartInstance = echarts.init(reservationChart.value)
      const option2 = {
        color: ['#2f80ed', '#b5d7ff', '#56ccf2', '#7fb3f5'],
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          top: '5%',
          left: 'center'
        },
        series: [
          {
            name: '预约状态',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: reservedCount, name: '已预约' },
              { value: notReservedCount, name: '未预约' }
            ]
          }
        ]
      }
      reservationChartInstance.setOption(option2)
      
      window.addEventListener('resize', () => {
        reservationChartInstance.resize()
      })
    } catch (error) {
      console.error('绘制预约状态统计图失败:', error)
    }
  }

  // 3. 就诊状态统计图
  if (visitChart.value && Array.isArray(reservationsData)) {
    try {
      const visitedCount = reservationsData.filter(res => res.visitStatus === 1).length
      const notVisitedCount = reservationsData.filter(res => res.visitStatus === 0).length
      
      console.log('就诊状态统计:', { visitedCount, notVisitedCount })
      
      const visitChartInstance = echarts.init(visitChart.value)
      const option3 = {
        color: ['#1b64c7', '#7fb3f5', '#56ccf2', '#b5d7ff'],
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          top: '5%',
          left: 'center'
        },
        series: [
          {
            name: '就诊状态',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: visitedCount, name: '已就诊' },
              { value: notVisitedCount, name: '未就诊' }
            ]
          }
        ]
      }
      visitChartInstance.setOption(option3)
      
      window.addEventListener('resize', () => {
        visitChartInstance.resize()
      })
    } catch (error) {
      console.error('绘制就诊状态统计图失败:', error)
    }
  }

  // 4. 患者性别分布图
  if (patientGenderChart.value && Array.isArray(patientsData)) {
    try {
      const maleCount = patientsData.filter(patient => patient.gender === '男').length
      const femaleCount = patientsData.filter(patient => patient.gender === '女').length
      const unknownCount = patientsData.filter(patient => 
        !patient.gender || (patient.gender !== '男' && patient.gender !== '女')
      ).length
      
      console.log('患者性别统计:', { maleCount, femaleCount, unknownCount })
      
      const patientGenderChartInstance = echarts.init(patientGenderChart.value)
      const option4 = {
        color: ['#2f80ed', '#56ccf2', '#7fb3f5', '#b5d7ff'],
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          top: '5%',
          left: 'center'
        },
        series: [
          {
            name: '患者性别',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: maleCount, name: '男性' },
              { value: femaleCount, name: '女性' },
              { value: unknownCount, name: '未知' }
            ]
          }
        ]
      }
      patientGenderChartInstance.setOption(option4)
      
      window.addEventListener('resize', () => {
        patientGenderChartInstance.resize()
      })
    } catch (error) {
      console.error('绘制患者性别分布图失败:', error)
    }
  }

  // 5. 药品库存与分类分析
  if (drugChart.value && Array.isArray(drugsData) && drugsData.length > 0) {
    try {
      // 按药品分类汇总库存
      const typeStockMap = {}
      drugsData.forEach(d => {
        const type = d.drugType || '其他'
        const stock = d.stockQuantity || 0
        typeStockMap[type] = (typeStockMap[type] || 0) + stock
      })

      const types = Object.keys(typeStockMap)
      const stocks = types.map(t => typeStockMap[t])

      const drugChartInstance = echarts.init(drugChart.value)
      const option5 = {
        color: ['#2f80ed'],
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: types,
          axisLabel: {
            interval: 0,
            rotate: 30
          }
        },
        yAxis: {
          type: 'value',
          name: '库存数量'
        },
        series: [
          {
            name: '库存数量',
            type: 'bar',
            data: stocks,
            itemStyle: {
              color: '#2f80ed'
            },
            barMaxWidth: 40
          }
        ]
      }
      drugChartInstance.setOption(option5)

      window.addEventListener('resize', () => {
        drugChartInstance.resize()
      })
    } catch (error) {
      console.error('绘制药品库存与分类分析图失败:', error)
    }
  }
}

// 使用固定数据
const useFixedData = () => {
  console.log('使用固定数据')
  
  // 统计数据
  statistics.value = {
    totalPatients: 150,
    totalDoctors: fixedDoctors.length,
    totalDepartments: fixedDepartments.length,
    totalReservations: 320,
    totalDrugs: 0,
    totalDrugStock: 0
  }
  
  // 处理统计表格数据
  processStatsData(fixedDepartments, fixedDoctors, [
    { departmentId: 'KS001', doctorId: 1, reservationStatus: 1, visitStatus: 1 },
    { departmentId: 'KS002', doctorId: 3, reservationStatus: 1, visitStatus: 0 },
    { departmentId: 'KS003', doctorId: 5, reservationStatus: 0, visitStatus: 0 },
    { departmentId: 'KS004', doctorId: 7, reservationStatus: 1, visitStatus: 1 },
    { departmentId: 'KS005', doctorId: 2, reservationStatus: 1, visitStatus: 0 }
  ])
  
  // 绘制图表
  nextTick(() => {
    drawCharts([], [], [])
  })
}

// 页面加载时获取数据
onMounted(() => {
  fetchStatistics()
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

.overview-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-card {
  text-align: center;
  height: 120px;
}

.stat-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #2f80ed;
}

.stat-label {
  font-size: 14px;
  color: #6b7f95;
  margin-top: 8px;
}

.chart-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.chart-container {
  width: 100%;
  height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>