import axios from 'axios'

const request = axios.create({
  baseURL: '/api',   // 根据环境变量可调整
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.request.use(
  config => config,
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res.data || res
  },
  error => Promise.reject(error)
)

export default request