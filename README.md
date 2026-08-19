# E2E 自动化测试管理平台 — 后端服务

基于 **Spring Boot 4.1 + Java 21** 的 E2E 自动化测试管理平台后端服务，负责测试用例管理、任务调度、进程执行、报告存储和文件上传。

## 技术栈

| 组件 | 技术 |
|---|---|
| 框架 | Spring Boot 4.1 + Java 21 |
| ORM | MyBatis-Plus 3.5.17 |
| 认证 | Sa-Token 1.45.0 |
| 缓存 | Redisson 4.6.1 |
| 文件存储 | MinIO (S3 协议) |
| 进程调度 | Java ProcessBuilder |
| 实时推送 | SSE (SseEmitter) |

## 项目结构

```
RuoYi-Vue-Plus/
├── ruoyi-admin/              # Web 服务入口
├── ruoyi-common/             # 公共模块 (26个子模块)
│   ├── ruoyi-common-oss/     #   └─ MinIO/S3 文件存储
│   ├── ruoyi-common-mybatis/ #   └─ 数据库 ORM
│   ├── ruoyi-common-security/#   └─ Sa-Token 认证
│   └── ...
├── ruoyi-modules/            # 业务模块
│   ├── ruoyi-system/         #   └─ 系统管理
│   ├── ruoyi-e2e/            #   └─ E2E测试管理 (本项目核心)
│   └── ...
├── ruoyi-api/                # 跨模块 API 接口
└── script/sql/               # 数据库脚本
```

## E2E 模块 (`ruoyi-e2e`)

### 功能模块

| 模块 | Controller | 说明 |
|---|---|---|
| 测试用例管理 | `E2eTestCaseController` | 用例 CRUD、代码内容管理、版本历史、分组执行 |
| 测试任务管理 | `E2eTestTaskController` | 任务创建执行、进程管理、实时日志、分组执行 |
| 测试报告管理 | `E2eTestReportController` | 报告查询、产物查看、文件服务 |

### 核心能力

- **用例内容数据库存储** — 用例代码存储在 `e2e_test_case.content` 字段，不依赖文件系统
- **版本历史管理** — `e2e_test_case_history` 表记录每次修改，支持查看历史和版本回退
- **分组执行** — 按 `caseGroup` 分组执行，合并为一个任务、一份报告
- **进程调度** — 通过 `ProcessBuilder` 调用 `npx playwright test`，支持指定浏览器和有头/无头模式
- **实时日志** — SSE 推送执行日志，同时持久化到数据库
- **MinIO 文件上传** — HTML 报告和产物（截图/视频/Trace）上传到 MinIO，返回公开 HTTP URL
- **进程回收** — 任务停止时自动 kill Node 和浏览器子进程，杜绝僵尸进程

### 数据库表

| 表名 | 说明 |
|---|---|
| `e2e_test_case` | 测试用例（含代码内容） |
| `e2e_test_case_history` | 用例版本历史 |
| `e2e_test_task` | 执行任务 |
| `e2e_test_task_case` | 任务用例关联 |
| `e2e_test_artifact` | 测试产物 |

### 配置

在 `application-dev.yml` 中配置：

```yaml
e2e:
  base-path: ../e2e-test   # e2e-test 项目路径（相对路径，相对于应用工作目录）
  max-concurrency: 3        # 最大并发任务数
```

MinIO 配置通过数据库 `sys_oss_config` 表管理（`config_key = 'minio'`）。

## 快速开始

```bash
# 1. 执行数据库脚本
mysql -u root -p ry-vue < script/sql/e2e_test.sql

# 2. 确保 MinIO 服务已启动，配置 sys_oss_config 表

# 3. 启动后端
cd ruoyi-admin
mvn spring-boot:run
```

## API 接口

| 接口 | 方法 | 说明 |
|---|---|---|
| `/e2e/case/list` | GET | 分页查询用例 |
| `/e2e/case` | POST/PUT | 新增/修改用例 |
| `/e2e/case/content` | PUT | 保存用例代码 |
| `/e2e/case/history/{caseId}` | GET | 查询版本历史 |
| `/e2e/case/revert` | POST | 回退版本 |
| `/e2e/case/sync` | POST | 同步磁盘文件到数据库 |
| `/e2e/task` | POST | 创建并执行任务 |
| `/e2e/task/executeGroup` | POST | 分组执行 |
| `/e2e/task/stop/{taskId}` | POST | 停止任务 |
| `/e2e/task/logs/{taskId}` | GET | 获取任务日志 |
| `/e2e/report/list` | GET | 查询报告列表 |
| `/e2e/report/{taskId}` | GET | 查询报告详情 |
