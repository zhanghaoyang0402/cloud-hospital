import request from '@/api/index'

export const searchPatients = (keyword) => request.get('/patient/search', { params: { keyword } })