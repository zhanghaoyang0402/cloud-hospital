<template>
  <el-card>
    <!-- 搜索条件 -->
    <el-form :model="searchForm" inline>
      <el-form-item label="病历号">
        <el-input v-model="searchForm.patientNo" placeholder="请输入病历号" clearable />
      </el-form-item>
      <el-form-item label="患者姓名">
        <el-input v-model="searchForm.patientName" placeholder="请输入姓名" clearable />
      </el-form-item>
      <el-form-item label="挂号日期">
        <el-date-picker
          v-model="searchForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table :data="tableData" stripe border style="width: 100%">
      <el-table-column prop="id" label="挂号ID" width="80" />
      <el-table-column prop="patientName" label="患者姓名" />
      <el-table-column prop="patientNo" label="病历号" />
      <el-table-column prop="deptName" label="科室" />
      <el-table-column prop="doctorName" label="医生" />
      <el-table-column prop="visitDate" label="挂号日期" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'info'">
            {{ row.status === 0 ? '未就诊' : row.status === 1 ? '已就诊' : '已退号' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button
            type="danger"
            size="small"
            :disabled="row.status !== 0"
            @click="handleCancel(row.id)"
          >
            退号
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
      <el-pagination
        v-model:current-page="pageInfo.current"
        v-model:page-size="pageInfo.size"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pageInfo.total"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { searchRegistrations, cancelRegister } from '@/api/modules/registration'

// 搜索条件
const searchForm = reactive({
  patientNo: '',
  patientName: '',
  dateRange: []
})

// 分页信息
const pageInfo = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref([])

// 查询挂号列表
const handleSearch = async () => {
  try {
    const params = {}
    if (searchForm.patientNo) params.patientNo = searchForm.patientNo
    if (searchForm.patientName) params.patientName = searchForm.patientName
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    // 分页参数始终传递
    params.current = pageInfo.current
    params.size = pageInfo.size

    const res = await searchRegistrations(params)
    tableData.value = res.records || []
    pageInfo.total = res.total || 0
  } catch (error) {
    ElMessage.error(error.message || '查询失败')
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.patientNo = ''
  searchForm.patientName = ''
  searchForm.dateRange = []
  pageInfo.current = 1
  handleSearch()
}

// 退号操作
const handleCancel = async (regId) => {
  try {
    await ElMessageBox.confirm('确认要退掉该挂号吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelRegister(regId)
    ElMessage.success('退号成功')
    handleSearch() // 刷新列表
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '退号失败')
    }
  }
}

// 页面加载时自动查询
handleSearch()
</script>