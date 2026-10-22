# cloud-hospital-frontend

云医院门诊管理系统的前端，Vue 3 + Vite + Element Plus。
配套后端：[cloud-hospital/backend](../backend)，接口说明见[项目根 README](../README.md)。

## 启动

```bash
npm install
npm run dev      # http://localhost:5173
```

启动前请确认后端已在 `http://localhost:8080` 运行，并且数据库已导入 `sql/schema.sql`。

```bash
npm run build    # 产物输出到 dist/
npm run preview  # 本地预览构建结果
```

## 跨域是怎么解决的

开发环境不写死后端地址，`vite.config.js` 里配了代理：

```js
server: {
  port: 5173,
  proxy: { '/api': { target: 'http://localhost:8080', changeOrigin: true } }
}
```

axios 实例的 `baseURL` 固定为 `/api`，于是浏览器请求 `localhost:5173/api/xxx`，
Vite 开发服务器转发到 `localhost:8080/api/xxx`，对外只有一个源，不存在跨域。

**注意：这个代理只在 `npm run dev` 下生效**，`npm run build` 出来的静态文件必须由
Nginx 之类的服务器提供同样的 `/api` 反向代理，或者和后端部署在同一个域名下。
后端 `WebConfig` 里另外配了 CORS 白名单（只放 `http://localhost:5173`），
是为了绕过代理直接调 8080 的调试场景。

## 目录结构

```
src/
├── api/
│   ├── index.js                # axios 实例：baseURL=/api，timeout 10s
│   └── modules/                # 按业务拆的 6 个模块，共 25 个接口函数
│       ├── common.js           #   科室、医生、收费项目字典
│       ├── patient.js          #   患者检索
│       ├── registration.js     #   挂号、退号、收费、退费、流水
│       ├── medicalRecord.js    #   病历保存与回显
│       ├── prescription.js     #   处方创建、加明细、删明细、提交
│       └── application.js      #   检查/检验/处置申请单
├── views/
│   ├── registration/           # 挂号台：挂号、退号、收费
│   ├── patient/                # 患者列表
│   ├── doctor/                 # 医生工作台（主页面 + 4 个子组件）
│   │   └── components/         #   病案首页、检查申请、诊断、处方
│   └── fee/                    # 费用流水查询
├── router/index.js             # 7 条路由，全部 () => import() 懒加载
└── main.js                     # 注册 Element Plus（中文包）与全部图标
```

## 约定

**响应拦截器统一拆包。** 后端返回 `{code, msg, data}`，`code !== 200` 直接 `reject`，
调用方只拿 `data`，业务代码里不需要每次判断 code：

```js
request.interceptors.response.use(response => {
  const res = response.data
  if (res.code !== 200) return Promise.reject(new Error(res.msg || '请求失败'))
  return res.data || res
})
```

**路由懒加载。** 7 个页面都用 `() => import(...)`，构建时自动分包，首屏只加载挂号台需要的代码。
挂号、收费、医生工作台三个页面依赖的 Element Plus 组件差异较大，懒加载对首屏体积有实际收益。

**用 `@` 指代 `src`。** `vite.config.js` 里配了别名，`import request from '@/api'` 不用写 `../../`。

## 已知的优化点

`main.js` 里用 `Object.entries(ElementPlusIconsVue)` 全量注册了 Element Plus 图标，
`npm run build` 时主包约 865 KB（gzip 后 274 KB），构建工具会给出 chunk 超过 500 KB 的提示。
实际用到的图标只有十几个，改成按需 `import { Search, Delete } from '@element-plus/icons-vue'`
可以显著减小首屏体积，这是本项目明确留着的优化项。
