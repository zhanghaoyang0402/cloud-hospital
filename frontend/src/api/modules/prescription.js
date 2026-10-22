import request from '@/api/index'

export const createPrescription = (registrationId) => request.post('/prescription/create', null, { params: { registrationId } })
export const addPrescriptionItem = (prescriptionId, data) => request.post(`/prescription/addItem?prescriptionId=${prescriptionId}`, data)
export const removePrescriptionItem = (itemId) => request.delete(`/prescription/removeItem/${itemId}`)
export const submitPrescription = (prescriptionId) => request.put(`/prescription/submit/${prescriptionId}`)
export const listPrescriptions = (regId) => request.get(`/prescription/list/${regId}`)
export const getPrescriptionItems = (prescriptionId) => request.get(`/prescription/items/${prescriptionId}`)