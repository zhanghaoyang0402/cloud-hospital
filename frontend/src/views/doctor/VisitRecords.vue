<template>
  <el-card>
    <el-form :inline="true" :model="searchForm">
      <el-form-item label="患者姓名">
        <el-input
          v-model="searchForm.patientName"
          placeholder="请输入姓名"
          clearable
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item label="病历号">
        <el-input
          v-model="searchForm.patientNo"
          placeholder="请输入病历号"
          clearable
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" stripe border v-loading="loading" style="width: 100%;">
      <el-table-column prop="id" label="挂号ID" width="80" />
      <el-table-column prop="patientName" label="患者姓名" width="120" />
      <el-table-column prop="patientNo" label="病历号" width="180" />
      <el-table-column prop="deptName" label="科室" width="100" />
      <el-table-column prop="doctorName" label="医生" width="100" />
      <el-table-column prop="visitDate" label="挂号日期" width="120" />
      <el-table-column prop="chiefComplaint" label="主诉" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="goToDoctorView(row.id)">
            诊疗
          </el-button>
        </template>
      </el-table-column>
    </el-table>

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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { searchRegistrations } from '@/api/modules/registration'

const router = useRouter()

const searchForm = reactive({
  patientName: '',
  patientNo: ''
})

const pageInfo = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const loading = ref(false)

// ========== 状态映射 ==========
const getStatusType = (status) => {
  if (status === 1) return 'success'
  if (status === 2) return 'info'
  return 'warning'
}

const getStatusText = (status) => {
  if (status === 1) return '诊毕'
  if (status === 2) return '已退号'
  return '未诊断'
}

// ========== 核心查询方法 ==========
const handleSearch = async () => {
  loading.value = true
  try {
    const params = {
      current: pageInfo.current,
      size: pageInfo.size
    }
    // ⚠️ 关键：只传有值的参数，空字符串一律不发送
    const name = searchForm.patientName?.trim()
    const no = searchForm.patientNo?.trim()
    if (name) params.patientName = name
    if (no) params.patientNo = no

    const res = await searchRegistrations(params)

    // 兼容后端返回格式
    if (res && res.records) {
      tableData.value = res.records
      pageInfo.total = res.total || 0
    } else if (Array.isArray(res)) {
      tableData.value = res
      pageInfo.total = res.length
    } else {
      tableData.value = []
      pageInfo.total = 0
    }
  } catch (e) {
    console.error('查询失败:', e)
    ElMessage.error(e.message || '查询失败')
    tableData.value = []
    pageInfo.total = 0
  } finally {
    loading.value = false
  }
}

// ========== 重置搜索 ==========
const resetSearch = () => {
  searchForm.patientName = ''
  searchForm.patientNo = ''
  pageInfo.current = 1
  handleSearch()
}

// ========== 跳转医生诊疗 ==========
const goToDoctorView = (regId) => {
  router.push(`/doctorView/${regId}`)
}

// ========== 页面加载时自动查询全部 ==========
onMounted(() => {
  handleSearch()
})
</script>