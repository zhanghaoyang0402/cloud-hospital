<template>
  <el-card>
    <div style="margin-bottom: 20px;">
      <el-button @click="$router.back()">返回</el-button>
      <span style="margin-left: 20px; font-weight: bold;">挂号ID：{{ regId }}</span>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="病案首页" name="home">
        <MedicalRecord :regId="regId" />
      </el-tab-pane>
      <el-tab-pane label="检查申请" name="exam">
        <ApplicationList type="1" :regId="regId" />
      </el-tab-pane>
      <el-tab-pane label="检验申请" name="lab">
        <ApplicationList type="2" :regId="regId" />
      </el-tab-pane>
      <el-tab-pane label="处置申请" name="treatment">
        <ApplicationList type="3" :regId="regId" />
      </el-tab-pane>
      <el-tab-pane label="门诊确诊" name="diagnosis">
        <Diagnosis :regId="regId" />
      </el-tab-pane>
      <el-tab-pane label="开设处方" name="prescription">
        <PrescriptionView :regId="regId" />
      </el-tab-pane>
      <el-tab-pane label="费用查询" name="fee">
        <FeeRecords :regId="regId" />
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import MedicalRecord from './components/MedicalRecord.vue'
import ApplicationList from './components/ApplicationList.vue'
import Diagnosis from './components/Diagnosis.vue'
import PrescriptionView from './components/PrescriptionView.vue'
import FeeRecords from '@/views/fee/FeeRecords.vue'

const route = useRoute()
// 兼容 route.params 和 props 传入
const regId = computed(() => route.params.regId || route.query.regId)
const activeTab = ref('home')
</script>