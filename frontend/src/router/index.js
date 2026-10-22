import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/register' },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/registration/Register.vue')
  },
  {
    path: '/cancel',
    name: 'CancelRegister',
    component: () => import('@/views/registration/CancelRegister.vue')
  },
  {
    path: '/fee',
    name: 'FeeManage',
    component: () => import('@/views/registration/FeeManage.vue')
  },
  {
    path: '/patients',
    name: 'PatientList',
    component: () => import('@/views/patient/PatientList.vue')
  },
  {
    path: '/visitRecords',
    name: 'VisitRecords',
    component: () => import('@/views/doctor/VisitRecords.vue')
  },
  {
    path: '/doctorView/:regId',
    name: 'DoctorView',
    component: () => import('@/views/doctor/DoctorView.vue'),
    props: true
  },
  {
    path: '/feeRecords',
    name: 'FeeRecords',
    component: () => import('@/views/fee/FeeRecords.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router