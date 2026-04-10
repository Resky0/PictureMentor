# PhotoMentor - 摄影专家评系统

一个面向摄影小白的AI辅助摄影应用，提供智能图像评分、改进建议和实时拍摄指导。

## 技术栈

### 后端
- Spring Boot 3.x
- Java 17
- MyBatis Plus
- MySQL 8.0

### 前端
- Vue 3 + TypeScript
- Vite
- Vue Router
- Pinia
- Tailwind CSS
- Axios

## 功能特性

- 📸 **照片评分**：AI智能分析，给你的照片专业评分
- 🎯 **拍摄指导**：实时取景，获得专业拍摄建议
- 📊 **历史记录**：查看进步轨迹，对比学习成长
- 👤 **个人中心**：用户信息管理

## 项目结构

```
.
├── backend/          # Spring Boot后端
│   ├── src/
│   │   └── main/
│   │       ├── java/com/photomentor/
│   │       │   ├── controller/
│   │       │   ├── service/
│   │       │   ├── mapper/
│   │       │   ├── entity/
│   │       │   ├── dto/
│   │       │   ├── config/
│   │       │   └── common/
│   │       └── resources/
│   └── pom.xml
├── frontend/         # Vue前端
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── router/
│   │   ├── stores/
│   │   ├── api/
│   │   └── utils/
│   └── package.json
└── README.md
```

## 快速开始

### 后端启动

1. 创建数据库并执行schema.sql
2. 配置application.yml中的数据库连接信息
3. 运行Spring Boot应用

```bash
cd backend
mvn spring-boot:run
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

## 设计理念

- 温暖友好：使用柔和的色彩和友好的文案，让用户感到轻松
- 直观易用：简化操作流程，减少学习成本
- 鼓励式反馈：提供积极的建议，避免打击用户信心
- 精致细节：微交互动画，精致的阴影和渐变效果

## 许可证

MIT License
