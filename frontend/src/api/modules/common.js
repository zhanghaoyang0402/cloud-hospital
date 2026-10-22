import request from '@/api/index'

export const getDeptList = () => request.get('/dept/list')
export const getDoctorList = (deptId) => request.get('/doctor/list', { params: { deptId } })
export const getChargeItems = (itemType) => request.get('/chargeItem/list', { params: { itemType } })