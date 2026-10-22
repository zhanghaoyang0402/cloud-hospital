<template>
  <el-card>
    <div style="margin-bottom: 20px;">
      <el-input v-model="regId" placeholder="请输入挂号ID" style="width: 200px; margin-right: 10px;" />
      <el-button type="primary" @click="loadData">查询</el-button>
    </div>

    <el-tabs v-model="activeTab">
      <!-- 待收费 -->
      <el-tab-pane label="待收费项目" name="pending">
        <el-table :data="pendingList" stripe border>
          <el-table-column prop="itemName" label="项目名称" />
          <el-table-column prop="spec" label="规格" />
          <el-table-column prop="quantity" label="数量" />
          <el-table-column prop="amount" label="金额(元)" />
          <el-table-column prop="createTime" label="开立时间" width="180" />
          <el-table-column label="来源">
            <template #default="{ row }">
              <el-tag>{{ sourceName(row.sourceType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="handleCharge(row)">收费</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 15px;">
          <strong>合计：{{ pendingTotal }} 元</strong>
        </div>
      </el-tab-pane>

      <!-- 已收费项目 -->
      <el-tab-pane label="已收费项目" name="charged">
        <el-table :data="chargedList" stripe border>
          <el-table-column prop="itemName" label="项目名称" />
          <el-table-column prop="spec" label="规格" />
          <el-table-column prop="quantity" label="数量" />
          <el-table-column prop="amount" label="金额(元)" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" type="danger" @click="handleRefund(row)">退费</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 费用流水 -->
      <el-tab-pane label="费用流水" name="records">
        <el-table :data="feeRecords" stripe border>
          <el-table-column prop="operateTime" label="时间" width="180" />
          <el-table-column label="类型">
            <template #default="{ row }">
              <el-tag :type="row.feeType === 1 ? 'success' : 'danger'">
                {{ row.feeType === 1 ? '收费' : '退费' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额(元)" />
          <el-table-column prop="payMethod" label="支付方式" />
          <el-table-column prop="operator" label="操作员" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getChargeDetails, charge, refund, getFeeRecords } from '@/api/modules/registration'

const regId = ref('')
const activeTab = ref('pending')
const allDetails = ref([])
const feeRecords = ref([])

const pendingList = computed(() => allDetails.value.filter(d => d.status === 0))
const chargedList = computed(() => allDetails.value.filter(d => d.status === 1))
const pendingTotal = computed(() =>
  pendingList.value.reduce((sum, d) => sum + Number(d.amount || 0), 0).toFixed(2)
)

const sourceName = (t) => {
  if (t === 'prescription') return '处方'
  if (t === 'application') return '申请单'
  return t
}

const loadData = async () => {
  if (!regId.value) {
    ElMessage.warning('请输入挂号ID')
    return
  }
  try {
    allDetails.value = await getChargeDetails(regId.value) || []
    feeRecords.value = await getFeeRecords(regId.value) || []
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  }
}

const handleCharge = async (row) => {
  try {
    await charge({
      sourceType: row.sourceType,
      detailId: row.id,
      payMethod: '现金',
      operator: 'admin'
    })
    ElMessage.success('收费成功')
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '收费失败')
  }
}

const handleRefund = async (row) => {
  try {
    await ElMessageBox.confirm('确认退费？', '提示', { type: 'warning' })
    await refund({
      sourceType: row.sourceType,
      detailId: row.id,
      operator: 'admin'
    })
    ElMessage.success('退费成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '退费失败')
  }
}
</script>