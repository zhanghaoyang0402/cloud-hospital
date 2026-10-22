<template>
  <div>
    <el-button type="primary" @click="dialogVisible = true">新增申请</el-button>
    <el-table :data="list" stripe style="margin-top: 10px;">
      <el-table-column prop="itemName" label="项目名称" />
      <el-table-column prop="price" label="单价" />
      <el-table-column prop="execDept" label="执行科室" />
      <el-table-column prop="urgency" label="加急">
        <template #default="{ row }">
          <el-tag :type="row.urgency ? 'danger' : 'info'">{{ row.urgency ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态">
        <template #default="{ row }">
          <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" size="small" type="danger" @click="cancel(row.id)">取消</el-button>
          <el-button v-if="row.status === 1" size="small" type="success" @click="execute(row.id)">执行</el-button>
          <el-button size="small" @click="viewResult(row)">查看结果</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增申请" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="项目">
          <el-select v-model="form.itemId" placeholder="请选择" style="width: 100%;">
            <el-option
              v-for="item in chargeItems"
              :key="item.id"
              :label="`${item.itemName}（${item.price}元）`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="执行科室">
          <el-input v-model="form.execDept" />
        </el-form-item>
        <el-form-item label="是否加急">
          <el-switch v-model="form.urgency" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="目的">
          <el-input v-model="form.purpose" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, defineProps } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { apply, listApplications, cancelApplication, executeApplication } from '@/api/modules/application'
import { getChargeItems } from '@/api/modules/common'

const props = defineProps({
  regId: { type: [String, Number], required: true },
  type: { type: String, required: true }
})

const list = ref([])
const dialogVisible = ref(false)
const form = ref({
  registrationId: props.regId,
  applicationType: Number(props.type),
  itemId: '',
  execDept: '',
  urgency: 0,
  purpose: ''
})
const chargeItems = ref([])
const statusMap = { 0: '未收费', 1: '已收费', 2: '已执行', 3: '已取消' }
const statusType = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }

// 根据类型映射到 charge_item 的 itemType
const getItemType = () => {
  if (props.type === '1') return 'check'
  if (props.type === '2') return 'test'
  if (props.type === '3') return 'treatment'
  return ''
}

const load = async () => {
  list.value = await listApplications(props.regId)
}

const submit = async () => {
  if (!form.value.itemId) return ElMessage.warning('请选择项目')
  await apply(form.value)
  ElMessage.success('申请成功')
  dialogVisible.value = false
  // 重置表单
  form.value.itemId = ''
  form.value.execDept = ''
  form.value.urgency = 0
  form.value.purpose = ''
  load()
}

const cancel = async (id) => {
  await ElMessageBox.confirm('确认取消该申请？', '提示', { type: 'warning' })
  await cancelApplication(id)
  ElMessage.success('已取消')
  load()
}

const execute = async (id) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入执行结果：', '执行', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await executeApplication(id, value)
    ElMessage.success('执行完成')
    load()
  } catch (e) {
    // 用户取消
  }
}

const viewResult = (row) => {
  if (!row.result) return ElMessage.info('暂无结果')
  ElMessageBox.alert(row.result, '执行结果', { confirmButtonText: '关闭' })
}

onMounted(async () => {
  chargeItems.value = await getChargeItems(getItemType()) || []
  load()
})
</script>