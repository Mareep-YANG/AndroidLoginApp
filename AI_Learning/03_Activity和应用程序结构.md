# Activity和应用程序结构

理解Activity的作用是掌握Android应用架构的基础。本卡片讲解单Activity vs 多Activity的设计思想和实际应用。

## 学习目标

- 理解Activity的本质和作用
- 明确单Activity和多Activity架构的区别
- 掌握多Activity的设计场景和优缺点
- 了解现代Android开发的趋势

## 核心概念

### Activity的定义

**Activity（活动）** 是Android应用的基本组成单元，代表一个**单独的用户交互屏幕**。

简单来说：
- 一个Activity = 一个屏幕 + 该屏幕的业务逻辑
- Activity负责展示UI和处理用户交互
- 每个Activity独立管理自己的生命周期

### 应用程序的本质

你的理解**部分正确**，但需要细化：

```
简单应用 (如计算器):
┌─────────────────┐
│   一个Activity   │  ←─ 唯一的屏幕
└─────────────────┘

复杂应用 (如社交APP):
┌─────────────────┐
│  Activity 1     │  ←─ 登录屏幕
└─────────────────┘
         ↓
┌─────────────────┐
│  Activity 2     │  ←─ 首页/动态屏幕
└─────────────────┘
         ↓
┌─────────────────┐
│  Activity 3     │  ←─ 个人中心屏幕
└─────────────────┘
```

**应用程序** = 一个或多个Activity的集合 + 其他组件（Service、BroadcastReceiver等）

## 实现细节

### 单Activity vs 多Activity的对比

| 特性 | 单Activity架构 | 多Activity架构 |
|-----|---------------|---------------|
| **屏幕切换** | Fragment + Navigation | Activity跳转 |
| **用例** | 现代APP开发（推荐） | 传统APP或特殊场景 |
| **复杂度** | 中等（需要Fragment知识） | 相对简单（直观） |
| **内存占用** | 较低 | 较高 |
| **页面间通信** | ViewModel + LiveData | Intent + Bundle |
| **返回栈管理** | 更灵活 | 自动管理 |

### 多Activity的应用场景

#### 场景1：登录/认证流程

```
启动应用
    ↓
┌──────────────────┐
│ LoginActivity    │  ← 用户未登录，显示登录屏幕
│  (登录/注册)      │
└──────────────────┘
    ↓ 登录成功
┌──────────────────┐
│ MainActivity     │  ← 用户已登录，显示主屏幕
│  (主界面)        │
└──────────────────┘
```

**为什么需要多个Activity？**
- 登录前后的界面完全不同，业务逻辑独立
- 登录屏幕和主屏幕没有导航关系，不需要"返回"
- 用户登出后需要清除返回栈

#### 场景2：社交应用的完整流程

```
┌──────────────────┐
│ SplashActivity   │  ← 启动屏幕（显示LOGO）
│  (2秒加载)       │
└──────────────────┘
    ↓
┌──────────────────┐
│ LoginActivity    │  ← 登录/注册（如果未登录）
└──────────────────┘
    ↓
┌──────────────────┐
│ MainActivity     │  ← 主界面（底部导航）
│  - HomeFragment  │      可以包含多个Fragment
│  - FindFragment  │      通过BottomNavigationView切换
│  - MessageFragment
│  - ProfileFragment
└──────────────────┘
    ↓ 点击头像进入个人页面
┌──────────────────┐
│ ProfileActivity  │  ← 个人中心（可以是单独Activity）
│  (编辑资料等)    │      也可以是Fragment
└──────────────────┘
    ↓ 点击返回
回到 MainActivity
```

### Activity间的通信

多个Activity通过 **Intent** 进行通信：

```kotlin
// 从LoginActivity跳转到MainActivity
val intent = Intent(LoginActivity.this, MainActivity::class.java)
intent.putExtra("userId", user.id)  // 传递数据
startActivity(intent)

// 在MainActivity中接收数据
val userId = intent.getIntExtra("userId", -1)
```

### 返回栈的概念

Android维护一个**返回栈（Back Stack）**，管理Activity的跳转历史：

```
用户操作序列：
1. 启动 → [LoginActivity]
2. 登录 → [LoginActivity, MainActivity]
3. 点进用户资料 → [LoginActivity, MainActivity, ProfileActivity]
4. 按返回键 → [LoginActivity, MainActivity]  ← 返回到MainActivity
5. 按返回键 → [LoginActivity]  ← 返回到LoginActivity
6. 按返回键 → []  ← 应用退出

栈的特性: LIFO（后进先出）
```

## 代码示例

### 示例1：LoginActivity → MainActivity 的跳转

```kotlin
// LoginActivity.kt
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(onLoginSuccess = { user ->
                // 登录成功，跳转到MainActivity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra("user_id", user.id)
                intent.putExtra("user_name", user.name)

                // 清除返回栈，防止返回到LoginActivity
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                finish()  // 销毁LoginActivity
            })
        }
    }
}

// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 接收从LoginActivity传来的数据
        val userId = intent.getIntExtra("user_id", -1)
        val userName = intent.getStringExtra("user_name") ?: ""

        setContent {
            MainScreen(userId = userId, userName = userName)
        }
    }
}
```

### 示例2：处理返回键（防止误操作）

```kotlin
// ProfileActivity.kt - 编辑资料页面
class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileScreen(
                onSaveClick = {
                    // 保存后返回
                    setResult(Activity.RESULT_OK)
                    finish()
                },
                onCancelClick = {
                    // 取消返回
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            )
        }
    }

    // 重写返回键处理
    override fun onBackPressed() {
        if (hasUnsavedChanges()) {
            // 显示确认对话框
            showConfirmDialog("放弃修改？")
        } else {
            super.onBackPressed()
        }
    }
}
```

### 示例3：Activity间传递复杂数据

```kotlin
// 定义一个数据类
data class User(
    val id: Int,
    val name: String,
    val email: String
)

// 使用Bundle传递
fun startProfileActivity(user: User) {
    val intent = Intent(this, ProfileActivity::class.java)

    // 方法1: 分别put
    intent.putExtra("user_id", user.id)
    intent.putExtra("user_name", user.name)
    intent.putExtra("user_email", user.email)

    startActivity(intent)
}

// 在ProfileActivity接收
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val user = User(
        id = intent.getIntExtra("user_id", -1),
        name = intent.getStringExtra("user_name") ?: "",
        email = intent.getStringExtra("user_email") ?: ""
    )
}
```

## 常见问题

### Q: 我的应用需要多少个Activity？
A: 取决于应用的复杂度：
- **简单应用（计算器、闹钟）**: 1-2个Activity
- **中等应用（购物、社交）**: 3-5个Activity（+ Fragment）
- **复杂应用（抖音、微信）**: 10+个Activity（+ 大量Fragment）

一般规则：**如果两个屏幕有完全不同的业务逻辑和生命周期，就需要分为不同的Activity。**

### Q: 为什么现代APP倾向于单Activity + Fragment？
A:
1. **内存效率高**: Fragment比Activity轻量
2. **共享资源容易**: 多个Fragment可以共享ViewModel数据
3. **转换动画流畅**: Fragment转换比Activity转换更灵活
4. **返回栈更好控制**: Navigation库提供更强大的管理

这就是 **单Activity架构** 的核心思想，也是我们的AndroidLoginApp采用的方式！

### Q: Activity一定要在AndroidManifest.xml中注册吗？
A: 是的！**所有Activity都必须在AndroidManifest.xml中声明**，否则应用会崩溃：

```xml
<activity
    android:name=".LoginActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<activity
    android:name=".MainActivity"
    android:exported="true" />

<activity
    android:name=".ProfileActivity"
    android:exported="false" />
```

### Q: 如何判断应该用Activity还是Fragment？
A:
- **用Activity**: 完全独立的业务流程、需要启动特殊权限、屏幕间逻辑关系复杂
- **用Fragment**: 屏幕共享数据、需要转换动画、属于同一业务流程的不同视图

## 现代Android开发的趋势

### 单Activity + Navigation架构（推荐）

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidLoginAppTheme {
                // 使用Navigation Compose管理屏幕
                NavHost(navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("profile")
                            }
                        )
                    }
                    composable("profile") {
                        ProfileScreen(
                            onLogout = {
                                navController.navigate("login")
                            }
                        )
                    }
                }
            }
        }
    }
}
```

**这种方式的优势**：
- ✅ 一个Activity，所有屏幕切换通过Navigation管理
- ✅ 状态共享方便（使用ViewModel）
- ✅ 内存占用低
- ✅ 代码结构清晰

---

**这正是AndroidLoginApp的架构！** 我们使用单Activity + Compose Navigation来实现登录和个人中心的页面切换。

## 参考资源

- [Android Activity官方文档](https://developer.android.com/guide/components/activities)
- [Activity生命周期](https://developer.android.com/guide/components/activities/activity-lifecycle)
- [Activity间通信 - Intent](https://developer.android.com/guide/components/intents-filters)
- [单Activity架构最佳实践](https://developer.android.com/guide/navigation)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)

## 相关卡片

- [Jetpack Compose基础](01_Jetpack_Compose基础.md) - UI编程基础
- [Compose导航实现](08_Compose导航实现.md) - Navigation Compose详细讲解
- [ViewModel生命周期](13_ViewModel生命周期.md) - Activity和ViewModel的关系

---

**关键要点总结**：

1. **你的理解基本正确** - 简单应用确实可以用一个Activity
2. **但架构会演变** - 复杂应用需要多个Activity或单Activity + Fragment
3. **现代趋势是单Activity** - 配合Navigation和Fragment管理屏幕
4. **AndroidLoginApp采用单Activity** - 用Navigation Compose在LoginScreen和ProfileScreen间切换

希望这解答了你的困惑！还有其他问题吗？
