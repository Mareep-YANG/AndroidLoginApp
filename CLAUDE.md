# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**AndroidLoginApp** is an Android learning project implementing login authentication and profile management features. Built with Kotlin and Jetpack Compose, it demonstrates core Android development concepts through practical implementation.

- **Package**: `cn.mareep.androidloginapp`
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 15)
- **UI Framework**: Jetpack Compose with Material 3
- **Language**: Kotlin (JVM target 11)
- **Build System**: Gradle with Kotlin DSL (Gradle 8.13.1)
- **Architecture**: MVVM pattern with ViewModel and Repository

## Development Setup

### Prerequisites
- Android Studio (latest stable recommended)
- Java 11 or higher
- Gradle wrapper (included in repository)

### Building and Running

```bash
# Build the debug APK
./gradlew assembleDebug

# Build the release APK
./gradlew assembleRelease

# Run the app on a connected device or emulator
./gradlew installDebug
```

### Testing

```bash
# Run unit tests (JUnit)
./gradlew testDebug

# Run instrumented tests (Espresso + Compose)
./gradlew connectedAndroidTest

# Run a specific test class
./gradlew testDebug --tests cn.mareep.androidloginapp.ExampleUnitTest
```

### Code Style and Quality

```bash
# Build and generate lint reports (if detekt is added)
./gradlew build
```

Currently, the project uses Kotlin's official code style (`kotlin.code.style=official`).

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/cn/mareep/androidloginapp/
│   │   │   ├── MainActivity.kt              # Main activity entry point with navigation
│   │   │   ├── ui/
│   │   │   │   ├── screens/
│   │   │   │   │   ├── LoginScreen.kt      # Login page with EditText, validation, social login
│   │   │   │   │   └── ProfileScreen.kt    # Profile page with user info and avatar
│   │   │   │   └── theme/
│   │   │   │       ├── Theme.kt            # Material 3 theme configuration
│   │   │   │       ├── Color.kt            # Color palette definitions
│   │   │   │       └── Type.kt             # Typography definitions
│   │   │   ├── data/
│   │   │   │   ├── database/
│   │   │   │   │   ├── AppDatabase.kt      # Room database instance
│   │   │   │   │   ├── UserDao.kt          # User data access object
│   │   │   │   │   └── User.kt             # User entity (Email, Password)
│   │   │   │   ├── preferences/
│   │   │   │   │   └── PreferenceManager.kt# SharedPreference helper for username, signature
│   │   │   │   └── repository/
│   │   │   │       └── UserRepository.kt   # Repository pattern for data access
│   │   │   ├── viewmodel/
│   │   │   │   ├── LoginViewModel.kt       # Login logic and validation
│   │   │   │   └── ProfileViewModel.kt     # Profile data and management
│   │   │   └── utils/
│   │   │       └── ValidationUtils.kt      # Email/password validation helpers
│   │   ├── res/                             # Resources (strings, colors, drawables)
│   │   └── AndroidManifest.xml
│   ├── test/                                # Unit tests (JUnit)
│   └── androidTest/                         # Instrumented tests (Espresso)
└── build.gradle.kts                         # App-level build configuration
```

## Functional Requirements

### Login Screen Features
- **App Entry Point**: App launches directly into login screen
- **UI Components**:
  - EditText for email/username input
  - EditText for password input (with password masking)
  - Login button with validation feedback
  - Social login buttons (WeChat, Apple) with UI and click handlers triggering Toast
- **Validation Logic**:
  - Email/username format validation
  - Password length and complexity validation
  - Real-time validation feedback
- **Data Storage**:
  - Use Room database to store user credentials
  - Pre-populate test accounts on first app launch for testing
  - Verify input against stored database records
- **Navigation**:
  - On successful login validation, navigate to ProfileScreen
  - Handle login failures with error messages

### Profile Screen Features
- **User Information Display**:
  - Circular avatar image
  - Username (stored and retrieved from SharedPreferences)
  - User signature/bio (stored and retrieved from SharedPreferences)
- **Interactivity**:
  - Each info item is fully clickable
  - Click triggers Toast notification with item details
  - Support for editing and saving changes
- **Data Persistence**:
  - Use SharedPreferences to store username and signature
  - Updates persist across app restarts
- **Navigation**:
  - Logout button to return to LoginScreen

## Architecture and Key Patterns

### MVVM Architecture
- **ViewModel Layer**: Manages UI state and business logic, survives configuration changes
- **Repository Pattern**: Abstracts data sources (Room database, SharedPreferences)
- **Separation of Concerns**: UI layer only handles presentation, data layer handles persistence

### UI Architecture
- **Jetpack Compose**: Declarative UI framework. All UI is defined in Kotlin with composable functions.
- **Material 3 Theme**: The app uses Material Design 3 with dynamic color support on Android 12+.
  - Light and dark color schemes defined in `Theme.kt`
  - Theme adapts to system preferences automatically via `isSystemInDarkTheme()`
  - Dynamic color on Android 12+ uses system wallpaper colors
- **Navigation Compose**: Handle screen transitions between LoginScreen and ProfileScreen

### Activity Structure
- **MainActivity**: Single activity with Compose-based navigation
  - Uses `ComponentActivity` with Compose integration
  - Calls `enableEdgeToEdge()` for edge-to-edge display
  - Sets up navigation graph for LoginScreen and ProfileScreen
  - Wrapped with `AndroidLoginAppTheme` for theming

### Data Layer
- **Room Database**:
  - User entity with email and hashed password
  - UserDao for database operations
  - Pre-populated with test accounts on first launch
- **SharedPreferences**:
  - Store username and signature for quick access
  - Use PreferenceManager wrapper for type-safe access
- **Repository Pattern**:
  - UserRepository abstracts Room and SharedPreferences
  - Provides unified interface for UI layer

### Dependencies
All dependencies are managed through version catalogs in `gradle/libs.versions.toml`:
- **Compose BOM**: Centralized version management for Compose libraries
- **Material 3**: `androidx.compose.material3` for Material Design components
- **Room**: `androidx.room` for database persistence
- **Lifecycle**: `androidx.lifecycle` for ViewModel and LiveData
- **Navigation Compose**: `androidx.navigation:navigation-compose` for screen navigation
- **Testing**: JUnit 4, Espresso, and Compose testing libraries
- **Core Libraries**: androidx.core, androidx.activity

## Important Configuration Notes

- **Android Namespace**: Set to `cn.mareep.androidloginapp` for R class generation
- **Non-Transitive R Class**: Enabled (`android.nonTransitiveRClass=true`) for reduced APK size
- **AndroidX**: Enforced via gradle.properties
- **ProGuard**: Not yet enabled in release builds
- **JVM Memory**: Set to 2048m in gradle.properties for build performance

## Debugging and Common Issues

### Building Issues
- If Gradle sync fails, ensure Java 11+ is set in Android Studio preferences
- Run `./gradlew clean` before rebuilding to clear stale artifacts

### Compose Preview
- Composable functions marked with `@Preview` can be viewed in Android Studio's design preview
- Use `@Preview` with `showBackground = true` to see background in previews

### Device Requirements
- Minimum SDK 24 supports Android 7.0+
- Full feature support on Android 12+ (for dynamic colors)

## 实现清单

### 核心功能
- [ ] **登录页面UI**: EditText组件、登录按钮、微信/Apple登录按钮
- [ ] **登录验证**: 邮箱格式验证、密码强度检查、错误提示
- [ ] **数据库设置**: Room数据库、User实体、UserDao、初始数据预埋
- [ ] **个人中心页面**: 圆形头像、用户名、签名显示
- [ ] **SharedPreferences集成**: 存储/获取用户名和签名
- [ ] **导航实现**: LoginScreen和ProfileScreen之间的导航
- [ ] **ViewModel实现**: LoginViewModel和ProfileViewModel
- [ ] **Toast提示**: 社交登录和个人中心项点击提示

### 增强功能
- [ ] 输入字段实时验证反馈
- [ ] 密码加密存储
- [ ] 个人信息编辑功能
- [ ] 登出功能
- [ ] 会话管理
- [ ] 单元测试
- [ ] UI集成测试

## 重要实现注意事项

### 密码安全
- **不应存储明文密码**，使用哈希算法（如BCrypt）
- 学习项目中可使用简单哈希方式演示

### SharedPreferences vs Room数据库
- **Room数据库**: 存储用户账户信息（邮箱/密码）
- **SharedPreferences**: 存储频繁变化的个人资料（用户名、签名）
- 使用Repository模式抽象数据源

### Compose状态管理
- 用ViewModel持有登录/个人中心状态
- 使用State Hoisting将状态提升到父级Composable
- 利用Compose重组实现响应式UI

## 知识卡片系统

`AI_Learning/` 目录存放学习过程中生成的知识卡片，记录关键概念和实现细节：
- 每张卡片遵循结构化模板（概述、详细说明、代码示例、参考资源）
- 所有卡片在目录索引中，便于导航和查阅
- 随着项目的推进不断增加新的卡片来记录学习成果

## AI使用记录要求

**重要**: 每次AI助手完成任务后，必须执行以下步骤：

### 1. 更新README.md的AI使用说明表格
在README.md中的"AI使用说明"部分添加一行新记录，包含：
- **序号**: 按时间顺序编号
- **项目部分**: 具体在项目的哪个部分工作（如：UI层、数据层、知识卡片等）
- **使用技术**: 明确标注是`AI Coding`（代码生成和架构设计）还是`Vibe Coding`（知识讲解和思维引导）
- **描述**: 简洁说明完成的工作内容

### 2. 生成或更新相关知识卡片
根据任务内容，在`AI_Learning/`目录中：
- 创建新的中文知识卡片（如果是新技术点）
- 或更新现有卡片（如果是相关主题）
- 所有卡片必须使用中文编写
- 更新AI_Learning/README.md的索引

### 3. 同步到CLAUDE.md
如果产生了新的架构决策或实现约定，应更新本文件相应部分

### AI Coding vs Vibe Coding的区分

**AI Coding**:
- 直接生成和编写代码
- 架构设计和项目组织
- 文件创建和代码结构
- 集成开发环境支持

**Vibe Coding**:
- 知识讲解和概念说明
- 最佳实践和常见陷阱指导
- 代码设计思想引导
- 学习过程中的思维启蒙

### 示例

完成"实现登录页面"任务后，应该：
1. 在README.md中添加: `| 序号 | LoginScreen | AI Coding | 实现登录UI、输入验证、数据库交互 |`
2. 生成知识卡片: `07_用户认证与验证.md`（如果不存在）
3. 在CLAUDE.md中确保项目结构和架构说明保持最新
