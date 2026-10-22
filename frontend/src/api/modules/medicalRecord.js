import request from '@/api/index'

export const saveMedicalRecord = (data) => request.post('/medicalRecord/save', data)
export const getMedicalRecord = (regId) => request.get(`/medicalRecord/get/${regId}`)