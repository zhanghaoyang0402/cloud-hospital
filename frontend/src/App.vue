<template>
  <div id="app">
    <el-container>
      <el-header style="background-color: #409EFF; color: white; display: flex; align-items: center; justify-content: space-between;">
        <h2>东软云医院</h2>
        <div>
          <el-button text style="color: white;" @click="$router.push('/register')">挂号</el-button>
          <el-button text style="color: white;" @click="$router.push('/cancel')">退号</el-button>
          <el-button text style="color: white;" @click="$router.push('/fee')">收费</el-button>
          <el-button text style="color: white;" @click="$router.push('/patients')">患者列表</el-button>
          <el-button text style="color: white;" @click="$router.push('/visitRecords')">看诊记录</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { RouterView } from 'vue-router'
</script>