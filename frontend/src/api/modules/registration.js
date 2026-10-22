import request from '@/api/index'

export const register = (data) => request.post('/registration/register', data)
export const cancelRegister = (regId) => request.put(`/registration/cancel/${regId}`)   // ✅ 用 PUT
export const searchRegistrations = (params) => request.get('/registration/list', { params })
export const getRegistration = (regId) => request.get(`/registration/${regId}`)
export const getFeeRecords = (regId) => request.get(`/registration/feeRecords/${regId}`)
export const getChargeDetails = (regId) => request.get(`/registration/chargeDetails/${regId}`)
export const charge = (data) => request.post('/registration/charge', data)
export const refund = (data) => request.post('/registration/refund', data)