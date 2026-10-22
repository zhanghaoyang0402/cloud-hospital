<template>
  <el-form :model="form" label-width="100px">
    <el-form-item label="主诉">
      <el-input v-model="form.chiefComplaint" type="textarea" :rows="2" />
    </el-form-item>
    <el-form-item label="现病史">
      <el-input v-model="form.presentIllness" type="textarea" :rows="3" />
    </el-form-item>
    <el-form-item label="既往史">
      <el-input v-model="form.pastHistory" type="textarea" :rows="2" />
    </el-form-item>
    <el-form-item label="过敏史">
      <el-input v-model="form.allergyHistory" type="textarea" :rows="2" />
    </el-form-item>
    <el-form-item label="体格检查">
      <el-input v-model="form.physicalExam" type="textarea" :rows="3" />
    </el-form-item>
    <el-form-item label="现病治疗情况">
      <el-input v-model="form.treatmentStatus" type="textarea" :rows="2" />
    </el-form-item>
    <el-form-item label="检查建议">
      <el-input v-model="form.examAdvice" type="textarea" :rows="2" />
    </el-form-item>
    <el-form-item label="注意事项">
      <el-input v-model="form.precautions" type="textarea" :rows="2" />
    </el-form-item>
    <el-form-item label="诊断ICD">
      <el-input v-model="form.diagnosisIcd" />
    </el-form-item>
    <el-form-item label="诊断名称">
      <el-input v-model="form.diagnosisName" />
    </el-form-item>
    <el-form-item label="发病时间">
      <el-date-picker v-model="form.diagnosisDate" type="date" value-format="YYYY-MM-DD" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="save">保存</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, onMounted, defineProps } from 'vue'
import { ElMessage } from 'element-plus'
import { saveMedicalRecord, getMedicalRecord } from '@/api/modules/medicalRecord'

const props = defineProps({ regId: { type: [String, Number], required: true } })
const form = ref({
  registrationId: props.regId,
  chiefComplaint: '',
  presentIllness: '',
  pastHistory: '',
  allergyHistory: '',
  physicalExam: '',
  treatmentStatus: '',
  examAdvice: '',
  precautions: '',
  diagnosisIcd: '',
  diagnosisName: '',
  diagnosisDate: ''
})

const load = async () => {
  const res = await getMedicalRecord(props.regId)
  if (res) form.value = { ...form.value, ...res }
}

const save = async () => {
  await saveMedicalRecord(form.value)
  ElMessage.success('保存成功')
  load()
}

onMounted(load)
</script>