# 云医院门诊管理系统

前后端分离的门诊业务系统，覆盖 **挂号 → 接诊 → 写病历 → 开处方/检查申请 → 收费 → 退费** 的完整闭环。
后端 Spring Boot 3 + MyBatis-Plus，前端 Vue 3 + Element Plus。

<!-- 建议补 3 张截图到 docs/images/，然后取消下面的注释
![挂号台](docs/images/register.png)
![医生工作台](docs/images/doctor.png)
![收费](docs/images/fee.png)
-->

## 技术栈

| 层 | 选型 |
| --- | --- |
| 后端 | Spring Boot 3.2.0、MyBatis-Plus 3.5.5、MySQL 8.0、Hutool、Lombok、JDK 17 |
| 前端 | Vue 3.5、Vite、Element Plus、axios、vue-router 4 |
| 交互 | RESTful JSON，统一响应体 `{code, msg, data}` |

## 业务流程

```
患者到院
  └─ 挂号台：按身份证号/病历号查患者（查不到则建档）→ 选科室、医生、午别 → 生成挂号单（状态 0 未就诊）
       └─ 医生工作台：接诊 → 写病历（主诉/现病史/既往史/过敏史/体格检查/诊断）
            ├─ 开检查、检验、处置申请单 → 提交
            └─ 开处方（可增删明细）→ 提交处方（同时把挂号单置为 1 已就诊）
                 └─ 收费台：按挂号单列出待收费明细（处方 + 申请单）→ 逐条收费 → 写费用流水
                      └─ 需要撤销时：退费（明细状态回滚 + 写反向流水）；退号前会校验是否还有已收费项目
```

## 目录结构

```
cloud-hospital/
├── backend/                        # Spring Boot 后端
│   ├── src/main/java/com/neuedu/
│   │   ├── controller/             # 8 个 Controller，24 个接口
│   │   ├── service/impl/           # 业务逻辑，关键方法带 @Transactional
│   │   ├── mapper/                 # MyBatis-Plus BaseMapper + 少量注解 SQL
│   │   ├── entity/ dto/ enums/     # 实体、出入参、状态枚举
│   │   └── config/                 # 分页插件、CORS
│   ├── src/main/resources/
│   │   ├── application.yml.example # 配置模板（复制为 application.yml 后填自己的密码）
│   │   └── mapper/                 # 自定义 XML（列表联表查询、收费明细聚合）
│   └── pom.xml
├── frontend/                       # Vue 3 前端
│   ├── src/
│   │   ├── views/                  # 7 个页面 + 医生工作台的 4 个子组件
│   │   ├── api/                    # axios 实例 + 6 个业务模块，25 个接口函数
│   │   └── router/                 # createWebHistory + 路由懒加载
│   └── vite.config.js              # @ 别名、/api 代理到 8080
└── sql/schema.sql                  # 11 张表结构 + 科室/医生/收费项目字典数据
```

## 快速开始

### 1. 建库

```bash
mysql -uroot -p -e "CREATE DATABASE cloud_hospital DEFAULT CHARSET utf8mb4;"
mysql -uroot -p cloud_hospital < sql/schema.sql
```

脚本包含 11 张表结构和科室、医生、收费项目三类字典数据，**不含任何患者与业务数据**。

### 2. 启动后端（JDK 17）

```bash
cd backend
cp src/main/resources/application.yml.example src/main/resources/application.yml
# 打开 application.yml，把 password 改成你的数据库密码
mvn spring-boot:run
```

服务跑在 `http://localhost:8080`。

### 3. 启动前端（Node 18+）

```bash
cd frontend
npm install
npm run dev
```

打开 `http://localhost:5173`。Vite 已把 `/api` 代理到 `localhost:8080`，无需额外配置跨域。

## 接口清单

| 模块 | 接口 |
| --- | --- |
| 基础字典 | `GET /api/dept/list`、`GET /api/doctor/list`、`GET /api/chargeItem/list` |
| 患者 | `GET /api/patient/search` |
| 挂号 | `POST /api/registration/register`、`PUT /api/registration/cancel/{regId}`、`GET /api/registration/list`、`GET /api/registration/{regId}` |
| 病历 | `POST /api/medicalRecord/save`、`GET /api/medicalRecord/get/{regId}` |
| 申请单 | `POST /api/application/apply`、`GET /api/application/list/{regId}`、`PUT /api/application/cancel/{appId}`、`PUT /api/application/execute/{appId}` |
| 处方 | `POST /api/prescription/create`、`POST /api/prescription/addItem`、`DELETE /api/prescription/removeItem/{itemId}`、`PUT /api/prescription/submit/{prescriptionId}`、`GET /api/prescription/list/{regId}`、`GET /api/prescription/detail/{prescriptionId}` |
| 收费 | `GET /api/registration/chargeDetails/{regId}`、`POST /api/registration/charge`、`POST /api/registration/refund`、`GET /api/registration/feeRecords/{regId}` |

## 几处实现细节

**1. 挂号时先查患者再建档，避免同一个人反复新建**

先按身份证号查，查不到再按病历号查；两个都没命中才新建患者，病历号用 `P + yyyyMMdd + 6 位随机数`。
命中已有患者时，只在手机号/住址真的变化时才 `update`，不无脑覆盖。

**2. 空字符串一律转成 null 再入库**

身份证号、手机号、住址这三个字段在表上带唯一索引（身份证）或参与查询，空串入库会导致第二条"空身份证"记录直接触发唯一键冲突。所以入参统一走 `toNullIfBlank()`。

**3. 收费/退费是"改状态 + 写流水"两步，用事务保证一致**

收费要同时做两件事：把明细状态从 0 改成 1、往 `fee_record` 插一条正向流水。只改状态不记流水，账就对不上；只记流水不改状态，就会重复收费。两个动作放在同一个 `@Transactional` 里。
退费同理，明细状态回退、插一条 `fee_type=2` 的反向流水，金额取原明细金额而不是前端传来，避免被篡改。

**4. 退号前做前置校验，防止"号退了钱没退"**

`cancelRegistration` 会遍历该挂号下的处方明细和申请单，只要还有一条已收费（status=1）就直接抛异常。先退费才能退号，顺序不能反。

**5. 金额统一 BigDecimal，处方总价在后端算**

单价 × 数量用 `BigDecimal.multiply`，处方总价由 `calcTotal` 在服务端累加，前端只做展示，不参与金额计算。

## 已知不足

这些是明确的短板，写在这里而不是藏着：

- **没有登录与鉴权**。当前所有接口匿名可访问，适合本地演示，不能直接上线。鉴权（JWT + 接口级 RBAC）在另一个项目 `pro-emos-oa` 里完整做过一遍，这个项目刻意只做业务闭环。
- **挂号费写死 10 元**，没有按号别（普通/专家）和科室分级定价。
- **号源未做并发控制**。没有"剩余号源"字段，两个人同时挂同一个医生的同一个午别不会互相排斥。
- **`prescription_detail` 表是早期设计遗留**，实际业务用的是 `prescription_item`，该表与对应实体、Mapper 已无业务引用。
- **没有单元测试**，只有 Spring Boot 生成的 `contextLoads()`。

## 作者

张浩阳 · 华北水利水电大学 计算机科学与技术 · 2027 届
邮箱：zhanghaoyang0402@163.com
