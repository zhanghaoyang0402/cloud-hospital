<template>
  <el-card>
    <div style="margin-bottom: 20px;">
      <el-input v-model="keyword" placeholder="输入姓名或病历号搜索" style="width: 300px; margin-right: 10px;" />
      <el-button type="primary" @click="search">搜索</el-button>
    </div>
    <el-table :data="patients" stripe>
      <el-table-column prop="patientNo" label="病历号" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="gender" label="性别" :formatter="(row) => row.gender === '1' ? '男' : '女'" />
      <el-table-column prop="birthDate" label="出生日期" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="address" label="地址" />
    </el-table>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { searchPatients } from '@/api/modules/patient'

const keyword = ref('')
const patients = ref([])

const search = async () => {
  if (!keyword.value) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  try {
    patients.value = await searchPatients(keyword.value) || []
  } catch (e) {
    ElMessage.error(e.message)
  }
}
</script>