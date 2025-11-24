# AndroidLoginApp

一个Android原生开发学习项目，实现登录和个人中心功能。

## 项目概述

**AndroidLoginApp** 是基于Jetpack Compose和Kotlin构建的Android应用示例，展示登录认证和用户个人中心的实现。本项目作为Android原生开发学习的Demo项目。

- **包名**: `cn.mareep.androidloginapp`
- **最低API**: 24 (Android 7.0)
- **目标API**: 36 (Android 15)
- **UI框架**: Jetpack Compose + Material 3
- **语言**: Kotlin (JVM Target 11)
- **构建系统**: Gradle 8.13.1 (Kotlin DSL)

## 功能需求

### 1. 登录页面 (Login Screen)
- **启动流程**: App启动首先进入登录页面
- **UI组件**: 使用EditText输入用户信息和密码
- **验证逻辑**: 实现账号和密码验证
- **数据存储**: 使用数据库存储用户凭证，首次启动预埋测试账户
- **社交登录UI**: 集成微信和Apple登录按钮（UI + Toast提示）
- **导航**: 验证成功后跳转至个人中心页面

### 2. 个人中心页面 (Profile Screen)
- **用户头像**: 显示圆形头像
- **用户信息**: 用户名称和签名（使用SharedPreference存储）
- **交互功能**: 每个信息条目支持点击，触发Toast提示
- **数据持久化**: 支持信息修改和保存

## 项目结构

```
app/
├── src/
│   ├── main/
│   │   ├── java/cn/mareep/androidloginapp/
│   │   │   ├── MainActivity.kt                    # App入口
│   │   │   ├── ui/
│   │   │   │   ├── screens/
│   │   │   │   │   ├── LoginScreen.kt            # 登录页面
│   │   │   │   │   └── ProfileScreen.kt          # 个人中心页面
│   │   │   │   └── theme/
│   │   │   │       ├── Theme.kt
│   │   │   │       ├── Color.kt
│   │   │   │       └── Type.kt
│   │   │   ├── data/
│   │   │   │   ├── database/                     # Room数据库
│   │   │   │   └── preferences/                  # SharedPreference工具
│   │   │   └── viewmodel/                        # ViewModel层
│   │   ├── res/                                  # 资源文件
│   │   └── AndroidManifest.xml
│   ├── test/                                     # 单元测试
│   └── androidTest/                              # 集成测试
└── build.gradle.kts
```

## 技术栈

### UI框架
- **Jetpack Compose**: 声明式UI框架
- **Material 3**: Material Design 3组件库
- **Navigation Compose**: 页面导航

### 数据层
- **Room**: SQLite数据库ORM框架
- **SharedPreferences**: 轻量级键值存储
- **DataStore**: (可选) 新一代数据存储

### 其他关键库
- **ViewModel & LiveData**: MVVM架构支持
- **Kotlin Coroutines**: 异步编程
- **AndroidX**: 现代化Android库

## 快速开始

### 环境要求
- Android Studio Flamingo或更新版本
- Java 11或更高版本
- Android SDK (minSdk: 24, targetSdk: 36)

### 构建和运行

```bash
# 清理项目
./gradlew clean

# 构建Debug APK
./gradlew assembleDebug

# 在设备或模拟器上安装并运行
./gradlew installDebug

# 运行单元测试
./gradlew testDebug

# 运行集成测试
./gradlew connectedAndroidTest
```

## 开发规范

- **代码风格**: 遵循Kotlin官方代码风格 (`kotlin.code.style=official`)
- **包命名**: `cn.mareep.androidloginapp`
- **UI层**: 使用Compose Composable函数定义UI
- **数据层**: 通过Room和SharedPreferences持久化数据
- **架构**: MVVM模式，使用ViewModel分离UI和业务逻辑

## AI_Learning目录

`AI_Learning/` 目录存放学习过程中的知识卡片（Knowledge Cards），记录每个技术点的深入理解和实现细节。

## AI使用说明

本部分记录项目中使用AI Coding或Vibe Coding技术的情况，标明具体的应用场景和技术点。

### 初始化阶段 (2025-11-24)

| 序号 | 项目部分 | 使用技术 | 描述 |
|------|--------|--------|------|
| 1 | README.md | AI Coding | 生成项目文档、功能需求说明、项目结构和技术栈梳理 |
| 2 | CLAUDE.md | AI Coding | 更新项目指导文档，包括架构设计、实现清单和注意事项 |
| 3 | AI_Learning系统 | AI Coding | 创建知识卡片库框架、模板和索引 |
| 4 | 知识卡片 - Activity和应用程序结构 | Vibe Coding | 讲解单Activity vs 多Activity架构、返回栈概念、现代Android设计趋势 |

## 许可证

本项目为学习项目，参考课程要求。
