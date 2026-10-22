<template>
  <div>
    <el-alert
      v-if="!regId"
      title="未获取到挂号ID，请返回重新进入"
      type="warning"
      :closable="false"
      style="margin-bottom: 10px;"
    />
    <el-table :data="records" stripe border v-loading="loading">
      <el-table-column prop="operateTime" label="时间" width="180" />
      <el-table-column label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.feeType === 1 ? 'success' : 'danger'">
            {{ row.feeType === 1 ? '收费' : '退费' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="金额(元)" width="120" />
      <el-table-column prop="payMethod" label="支付方式" width="120" />
      <el-table-column prop="operator" label="操作员" width="120" />
      <el-table-column label="明细ID" width="100">
        <template #default="{ row }">
          <span>{{ row.detailId }}</span>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="!loading && records.length === 0" style="text-align: center; color: #909399; padding: 20px;">
      暂无费用记录
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, defineProps } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeeRecords } from '@/api/modules/registration'

const props = defineProps({
  regId: { type: [String, Number], required: true }
})

const records = ref([])
const loading = ref(false)

const load = async () => {
  if (!props.regId) {
    records.value = []
    return
  }
  loading.value = true
  try {
    const res = await getFeeRecords(props.regId)
    records.value = Array.isArray(res) ? res : []
  } catch (e) {
    console.error('加载费用记录失败:', e)
    ElMessage.error(e.message || '加载费用记录失败')
    records.value = []
  } finally {
    loading.value = false
  }
}

// 监听 regId 变化，保证 tab 切换后也能加载
watch(() => props.regId, load, { immediate: true })

onMounted(load)
</script>