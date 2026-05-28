# PhotoMentor - AI 摄影指导与照片评分系统

PhotoMentor 是一个面向摄影初学者的 AI 辅助摄影应用。用户可以上传照片获得构图、光线、色彩、焦点等维度的评分与改进建议，也可以通过拍摄指导页面获取实时取景建议，逐步形成自己的拍摄反馈闭环。

## 项目亮点

- **照片智能评分**：上传照片后，AI 从构图、光线、色彩、焦点四个维度生成评分。
- **改进建议生成**：结合照片内容输出整体分析和可执行的优化建议。
- **拍摄指导**：调用摄像头取景，提供构图、曝光、对焦和实用拍摄技巧。
- **历史记录管理**：按用户保存评分记录，支持查看和删除历史分析结果。
- **用户体系**：支持注册、登录、JWT 鉴权和个人资料维护。
- **移动端友好**：前端页面采用底部导航，适合手机浏览器使用。

## 页面预览

### 登录与注册

![登录页面截图待补充](docs/images/login.png)

### 首页

![image-20260528194221421](https://raw.githubusercontent.com/Resky0/MdPicture/img/img/image-20260528194221421.png)

### 照片评分

![image-20260528194306482](https://raw.githubusercontent.com/Resky0/MdPicture/img/img/image-20260528194306482.png)

### 历史记录

![image-20260528194332806](https://raw.githubusercontent.com/Resky0/MdPicture/img/img/image-20260528194332806.png)

## 技术栈

### 后端

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring AI
- MyBatis-Plus
- MySQL 8.0
- JWT
- Maven

### 前端

- Vue 3
- TypeScript
- Vite
- Vue Router
- Pinia
- Tailwind CSS
- Axios

## 项目结构

```text
.
├── backend/                         # Spring Boot 后端服务
│   ├── src/main/java/com/photomentor/
│   │   ├── common/                  # 通用响应结构
│   │   ├── config/                  # 鉴权、跨域、静态资源配置
│   │   ├── controller/              # REST API 控制器
│   │   ├── mapper/                  # MyBatis-Plus Mapper
│   │   ├── model/
│   │   │   ├── dto/                 # 请求 DTO
│   │   │   ├── entity/              # 数据库实体
│   │   │   └── vo/                  # 响应视图对象
│   │   ├── service/                 # 业务逻辑与 AI 调用
│   │   ├── util/                    # JWT、密码工具
│   │   └── PhotoMentorApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml          # 主配置文件
│   │   └── application-local.yml    # 本地开发配置（不提交）
│   ├── src/main/sql/
│   │   └── schema.sql               # 数据库初始化脚本
│   └── pom.xml
├── frontend/                        # Vue 前端应用
│   ├── src/
│   │   ├── api/                     # 接口封装
│   │   ├── pages/                   # 页面组件
│   │   ├── router/                  # 路由与登录拦截
│   │   ├── stores/                  # Pinia 状态管理
│   │   ├── utils/                   # Axios 请求实例
│   │   ├── App.vue
│   │   └── main.ts
│   ├── vite.config.ts               # 本地代理配置
│   └── package.json
├── uploads/                         # 本地上传文件目录
├── .gitignore
└── README.md
```

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- npm 9+
- MySQL 8.0+
- 可用的通义千问 / DashScope 兼容模式 API Key

## 快速开始

### 1. 初始化数据库

在 MySQL 中执行后端提供的初始化脚本：

```bash
mysql -u root -p < backend/src/main/sql/schema.sql
```

默认数据库名为 `photo_mentor`。如果需要使用其他数据库名，请同步修改 `backend/src/main/resources/application.yml` 中的连接地址。

### 2. 配置后端

复制配置模板：

```bash
cd backend/src/main/resources
cp application.yml application-local.yml
```

打开 `application-local.yml`，按本地环境调整数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/photo_mentor?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

AI 服务建议通过环境变量配置，避免把真实密钥提交到代码仓库：

```bash
set DASHSCOPE_API_KEY=your_api_key
```

PowerShell 用户可以使用：

```powershell
$env:DASHSCOPE_API_KEY="your_api_key"
```

后端默认配置：

- 服务地址：`http://localhost:8080`
- 上传目录：`./uploads`
- 单文件上传上限：`10MB`

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在：

```text
http://localhost:3000
```

Vite 已配置本地代理：

- `/api` -> `http://localhost:8080`
- `/uploads` -> `http://localhost:8080`

## 使用流程

1. 访问前端页面，先注册或登录账号。
2. 在「照片评分」页面上传照片。
3. 点击开始分析，等待 AI 返回评分、分析报告和改进建议。
4. 在「历史记录」页面查看过往评分结果。
5. 在「拍摄指导」页面授权浏览器摄像头，获取构图、曝光和对焦建议。

## 主要接口

| 模块 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 用户 | `POST` | `/api/user/register` | 注册账号 |
| 用户 | `POST` | `/api/user/login` | 登录并获取 Token |
| 用户 | `GET` | `/api/user/profile` | 获取个人资料 |
| 用户 | `PUT` | `/api/user/profile` | 更新个人资料 |
| 照片 | `POST` | `/api/photos/upload` | 上传照片 |
| 评分 | `POST` | `/api/scoring/analyze` | 对已上传照片进行 AI 分析 |
| 历史 | `GET` | `/api/history` | 分页获取评分历史 |
| 历史 | `DELETE` | `/api/history/{id}` | 删除评分记录 |
| 指导 | `POST` | `/api/guide/advice` | 获取拍摄建议 |

除注册和登录外，其他接口需要在请求头中携带：

```text
Authorization: Bearer <token>
```

## 构建与预览

前端生产构建：

```bash
cd frontend
npm run build
```

本地预览构建产物：

```bash
npm run preview
```

后端打包：

```bash
cd backend
mvn clean package
```

## 配置说明

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `server.port` | `8080` | 后端服务端口 |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/photo_mentor...` | MySQL 连接地址 |
| `spring.ai.openai.api-key` | `${DASHSCOPE_API_KEY:...}` | AI 服务密钥，建议只使用环境变量 |
| `spring.ai.openai.base-url` | `https://dashscope.aliyuncs.com/compatible-mode` | DashScope OpenAI 兼容模式地址 |
| `file.upload.path` | `./uploads` | 图片上传保存目录 |

## 常见问题

### 1. 前端请求接口失败

请确认后端已在 `8080` 端口启动，并检查 `frontend/vite.config.ts` 中的代理目标是否正确。

### 2. 上传图片后无法访问

请确认 `file.upload.path` 指向的目录存在，并且后端对 `/uploads/**` 的静态资源映射已生效。

### 3. AI 分析失败

请检查 `DASHSCOPE_API_KEY` 是否可用、模型名称是否仍可访问，以及后端是否可以连接 DashScope 服务。

### 4. 数据库初始化报错

如果导入 `schema.sql` 时提示测试用户缺少密码字段，可以为测试数据补充 `password` 值，或删除脚本末尾的测试数据插入语句后重新执行。

### 5. CORS 跨域错误

如果遇到跨域问题，请检查是否移除了 Controller 上的 `@CrossOrigin` 注解，CORS 策略已统一由 `WebConfig` 管理。

## 开发备注

- 当前图片分析模型在后端服务中指定为 `qwen-vl-max-latest`。
- 前端会把登录 Token 保存到 `localStorage`，路由守卫会阻止未登录用户访问业务页面。
- 拍摄指导依赖浏览器摄像头权限；移动端浏览器通常需要 HTTPS 或可信本地环境才能正常授权。
- 生产环境中请关闭过宽的跨域配置，并将密钥、数据库密码等敏感信息移入环境变量或配置中心。
- 本地开发时，敏感配置放在 `application-local.yml` 中，该文件已被 `.gitignore` 忽略。

## 许可证

MIT License
