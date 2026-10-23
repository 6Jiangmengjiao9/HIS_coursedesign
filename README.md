# 轻量化 HIS 门诊系统

面向课程设计验收的轻量化医院门诊系统，覆盖门诊核心业务闭环：

> 挂号 → 医生开处方 → 划价 → 收费 → 扣库存 → 发药

目标：保证业务逻辑正确、权限边界清晰、关键操作具备事务与审计能力。

## 技术栈

| 层次 | 技术 |
| --- | --- |
| 前端 | Vue 3 + Vite + Element Plus + Axios |
| 后端 | Spring Boot 3.5 + Spring Security + MyBatis |
| 数据库 | MySQL 8 |
| 认证 | JWT + BCrypt 密码哈希 |
| 测试 | JUnit 5 + MockMvc + H2 |
| 部署 | Docker + docker-compose |

## 角色与权限

| 角色 | 权限范围 |
| --- | --- |
| `admin` | 科室、医生、患者、药品、排班等基础数据维护 |
| `doctor` | 查看自己的挂号记录、开具处方、维护病历 |
| `cashier` | 查看待收费处方、执行收费 |
| `pharmacist` | 查看已收费处方、执行发药、查询审计日志 |
| `patient` | 预约挂号、查看自己的处方与费用、AI 问诊 |

所有业务接口校验 JWT，越权访问返回 `403`，未登录返回 `401`。

## 核心业务规则

1. **价格以后端为准**：处方金额由后端读取 `drug.unit_price × quantity` 计算，前端提交的 `totalFee` 直接忽略
2. **挂号事务**：锁定排班行并扣减 `remaining_slots`，号源不足返回 `400`
3. **开方事务**：校验挂号单归属与状态，写入处方主表和 `prescription_item`，挂号单标记为已开方
4. **收费事务**：收费单、处方状态、库存扣减、审计日志在同一事务内完成；库存不足返回 `400` 并整体回滚
5. **库存兜底**：数据库 `CHECK (stock_quantity >= 0)` + 条件更新 `stock_quantity >= quantity`
6. **发药**：仅药师可对已收费处方发药，发药后写入审计日志并将挂号单标记为已完成
7. **敏感数据**：密码使用 BCrypt 哈希；身份证示例数据脱敏展示

## 目录结构

```text
.
├── 202301020417/          # Spring Boot 后端
│   ├── src/main/java/     # controller / service / mapper / security
│   ├── src/main/resources/mapper/
│   └── src/test/java/     # JUnit + MockMvc 测试
├── Coursedesign/          # Vue 3 前端
│   └── src/components/    # 管理端、医生端、患者端、收费端、药房端
├── database/
│   ├── schema.sql         # 全新建库脚本
│   └── migration.sql      # 已有数据库升级脚本
├── docker-compose.yml
└── .env.example
```

## 本地运行

### 1. 初始化数据库

全新数据库：

```bash
mysql -u root -p < database/schema.sql
```

已有数据库执行一次升级脚本：

```bash
mysql -u root -p his < database/migration.sql
```

### 2. 启动后端

```bash
cd 202301020417
# 设置数据库连接（Windows PowerShell 示例）
$env:DB_URL="jdbc:mysql://127.0.0.1:3306/his?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="你的密码"
$env:JWT_SECRET="至少32位的随机字符串"
.\mvnw.cmd spring-boot:run
```

后端默认地址：`http://127.0.0.1:8081`

首次启动会创建演示账号（可用 `SEED_USERS=false` 关闭）：

| 用户名 | 密码 | 角色 |
| --- | --- | --- |
| admin | 123456 | admin |
| doctor | 123456 | doctor |
| cashier | 123456 | cashier |
| pharmacist | 123456 | pharmacist |
| patient | 123456 | patient |

### 3. 启动前端

```bash
cd Coursedesign
npm install
npm run dev
```

前端默认地址：`http://127.0.0.1:5173`

## Docker 一键启动

```bash
cp .env.example .env
docker compose up --build
```

启动后：

- 前端：`http://localhost`
- 后端：`http://localhost:8081`
- MySQL：`localhost:3306`

## 关键接口

| 方法 | 路径 | 角色 |
| --- | --- | --- |
| GET | `/login` | 公开 |
| POST | `/api/register-records` | patient / admin / cashier |
| GET | `/api/register-records/mine` | doctor |
| POST | `/addPrescription` | doctor / admin |
| GET | `/api/prescriptions/pending` | cashier |
| POST | `/api/charges/{prescriptionId}` | cashier |
| GET | `/api/prescriptions/paid` | pharmacist |
| POST | `/api/dispenses/{prescriptionId}` | pharmacist |
| GET | `/api/audit-logs` | pharmacist / admin |

## 测试

```bash
cd 202301020417
.\mvnw.cmd test
```

覆盖场景：

- 登录成功返回 JWT，响应中不包含密码
- 未登录访问返回 401
- 医生调用收费接口返回 403
- 收费员调用药品维护接口返回 403
- 后端划价忽略前端价格
- 正常收费扣减库存并写审计日志
- 库存不足返回 400，库存和处方状态回滚
- 挂号扣减号源，号源不足返回 400
- 发药仅限已收费处方，并写审计日志

## 验收清单

- [x] 四种业务角色 + 患者角色，JWT 鉴权与 403 拦截
- [x] BCrypt 密码哈希
- [x] 挂号扣号源事务
- [x] 处方明细与后端划价
- [x] 收费 + 扣库存事务，库存不足 400
- [x] 数据库库存非负约束
- [x] 发药与审计日志
- [x] JUnit + MockMvc 测试
- [x] Docker + docker-compose
- [x] AI 问诊保留为扩展功能
