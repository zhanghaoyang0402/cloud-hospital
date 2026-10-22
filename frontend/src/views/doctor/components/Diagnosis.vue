<template>
  <el-form :model="form" label-width="100px">
    <el-form-item label="ICD编码">
      <el-input v-model="form.diagnosisIcd" />
    </el-form-item>
    <el-form-item label="诊断名称">
      <el-input v-model="form.diagnosisName" />
    </el-form-item>
    <el-form-item label="发病时间">
      <el-date-picker v-model="form.diagnosisDate" type="date" value-format="YYYY-MM-DD" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="save">保存诊断</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, onMounted, defineProps } from 'vue'
import { ElMessage } from 'element-plus'
import { getMedicalRecord, saveMedicalRecord } from '@/api/modules/medicalRecord'

const props = defineProps({ regId: { type: [String, Number], required: true } })

const form = ref({
  registrationId: props.regId,
  diagnosisIcd: '',
  diagnosisName: '',
  diagnosisDate: ''
})

const load = async () => {
  const res = await getMedicalRecord(props.regId)
  if (res) {
    form.value.diagnosisIcd = res.diagnosisIcd || ''
    form.value.diagnosisName = res.diagnosisName || ''
    form.value.diagnosisDate = res.diagnosisDate || ''
  }
}

const save = async () => {
  // 需要先把病历整体加载出来，避免覆盖其他字段
  const existing = await getMedicalRecord(props.regId) || {}
  const data = { ...existing, ...form.value }
  await saveMedicalRecord(data)
  ElMessage.success('诊断已保存')
  load()
}

onMounted(load)
</script>