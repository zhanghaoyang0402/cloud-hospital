<template>
  <div>
    <el-button type="primary" @click="createNew">新建处方</el-button>
    <el-table :data="prescriptions" stripe style="margin-top: 10px;">
      <el-table-column prop="prescriptionNo" label="处方编号" width="180" />
      <el-table-column prop="createTime" label="开立时间" width="180" />
      <el-table-column label="总金额">
        <template #default="{ row }">{{ row.totalAmount }} 元</template>
      </el-table-column>
      <el-table-column label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'warning' : 'success'">
            {{ row.status === 0 ? '草稿' : '已提交' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row)">查看</el-button>
          <el-button v-if="row.status === 0" size="small" type="primary" @click="editPrescription(row)">编辑</el-button>
          <el-button v-if="row.status === 0" size="small" type="success" @click="submit(row.id)">提交</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="`编辑处方 ${currentPrescription?.prescriptionNo || ''}`" width="700px">
      <div style="margin-bottom: 10px;">
        <el-select v-model="newItem.itemId" placeholder="选择药品" style="width: 220px;">
          <el-option
            v-for="item in drugs"
            :key="item.id"
            :label="`${item.itemName}（${item.price}元）`"
            :value="item.id"
          />
        </el-select>
        <el-input-number v-model="newItem.quantity" :min="1" style="width: 100px; margin-left: 10px;" />
        <el-input v-model="newItem.dosage" placeholder="用法用量" style="width: 200px; margin-left: 10px;" />
        <el-button type="primary" style="margin-left: 10px;" @click="addItem">添加</el-button>
      </div>
      <el-table :data="currentPrescription?.items || []" stripe>
        <el-table-column prop="itemName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="price" label="单价" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="dosage" label="用法用量" />
        <el-table-column prop="amount" label="金额" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="removeItem(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, defineProps } from 'vue'
import { ElMessage } from 'element-plus'
import {
  createPrescription,
  addPrescriptionItem,
  removePrescriptionItem,
  submitPrescription,
  listPrescriptions
} from '@/api/modules/prescription'
import { getChargeItems } from '@/api/modules/common'

const props = defineProps({ regId: { type: [String, Number], required: true } })

const prescriptions = ref([])
const dialogVisible = ref(false)
const currentPrescription = ref(null)
const newItem = ref({ itemId: '', quantity: 1, dosage: '' })
const drugs = ref([])

const load = async () => {
  prescriptions.value = await listPrescriptions(props.regId) || []
}

const createNew = async () => {
  const pres = await createPrescription(props.regId)
  ElMessage.success('处方已创建')
  await load()
  // 打开刚创建的处方进行编辑
  const target = prescriptions.value.find(p => p.id === pres.id)
  if (target) {
    currentPrescription.value = target
    dialogVisible.value = true
  }
}

const editPrescription = (row) => {
  currentPrescription.value = row
  dialogVisible.value = true
}

const addItem = async () => {
  if (!newItem.value.itemId) return ElMessage.warning('请选择药品')
  await addPrescriptionItem(currentPrescription.value.id, newItem.value)
  ElMessage.success('已添加')
  newItem.value = { itemId: '', quantity: 1, dosage: '' }
  await refreshCurrent()
}

const removeItem = async (itemId) => {
  await removePrescriptionItem(itemId)
  ElMessage.success('已删除')
  await refreshCurrent()
}

const submit = async (id) => {
  await submitPrescription(id)
  ElMessage.success('处方已提交')
  load()
}

const refreshCurrent = async () => {
  // 重新查询列表，并更新当前编辑的处方
  await load()
  if (currentPrescription.value) {
    const updated = prescriptions.value.find(p => p.id === currentPrescription.value.id)
    if (updated) currentPrescription.value = updated
  }
}

onMounted(async () => {
  drugs.value = await getChargeItems('drug') || []
  load()
})
</script>