import request from '@/api/index'

export const apply = (data) => request.post('/application/apply', data)
export const listApplications = (regId) => request.get(`/application/list/${regId}`)
export const cancelApplication = (appId) => request.put(`/application/cancel/${appId}`)
export const executeApplication = (appId, result) => request.put(`/application/execute/${appId}`, null, { params: { result } })