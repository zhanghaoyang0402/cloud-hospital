<template>
  <el-card>
    <el-form :model="form" label-width="100px" ref="formRef">
      <el-divider content-position="left">患者信息</el-divider>
      <el-form-item label="病历号">
        <el-input v-model="form.patientNo" placeholder="已有病历号可输入" />
      </el-form-item>
      <el-form-item label="姓名" required>
        <el-input v-model="form.patientName" />
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.gender">
          <el-radio label="1">男</el-radio>
          <el-radio label="2">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="出生日期">
        <el-date-picker v-model="form.birthDate" type="date" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="身份证号" required>
        <el-input v-model="form.idCard" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="家庭住址">
        <el-input v-model="form.address" />
      </el-form-item>
      <el-divider content-position="left">挂号信息</el-divider>
      <el-form-item label="科室" required>
        <el-select v-model="form.deptId" placeholder="请选择" @change="onDeptChange">
          <el-option v-for="d in depts" :key="d.id" :label="d.deptName" :value="d.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="医生" required>
        <el-select v-model="form.doctorId" placeholder="请选择">
          <el-option v-for="d in doctors" :key="d.id" :label="d.name" :value="d.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="号别">
        <el-select v-model="form.regLevel">
          <el-option label="普通" value="普通" />
          <el-option label="专家" value="专家" />
        </el-select>
      </el-form-item>
      <el-form-item label="就诊日期" required>
        <el-date-picker v-model="form.visitDate" type="date" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="午别" required>
        <el-radio-group v-model="form.noon">
          <el-radio label="1">上午</el-radio>
          <el-radio label="2">下午</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">挂号</el-button>
        <el-button @click="resetForm">清空</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { register } from '@/api/modules/registration'
import { getDeptList, getDoctorList } from '@/api/modules/common'

const form = ref({
  patientNo: '',
  patientName: '',
  gender: '1',
  birthDate: '',
  idCard: '',
  phone: '',
  address: '',
  deptId: '',
  doctorId: '',
  regLevel: '普通',
  visitDate: '',
  noon: '1'
})
const depts = ref([])
const doctors = ref([])

const onDeptChange = async (val) => {
  doctors.value = val ? await getDoctorList(val) : []
}
const submit = async () => {
  if (!form.value.patientName || !form.value.idCard|| !form.value.deptId || !form.value.doctorId || !form.value.visitDate || !form.value.noon) {
    return ElMessage.warning('请填写完整信息')
  }
  try {
    const res = await register(form.value)
    ElMessage.success(`挂号成功，挂号ID：${res.id}`)
    resetForm()
  } catch (e) {
    ElMessage.error(e.message)
  }
}
const resetForm = () => {
  form.value = { patientNo: '', patientName: '', gender: '1', birthDate: '', idCard: '', phone: '', address: '', deptId: '', doctorId: '', regLevel: '普通', visitDate: '', noon: '1' }
}
onMounted(async () => {
  depts.value = await getDeptList() || []
})
</script>