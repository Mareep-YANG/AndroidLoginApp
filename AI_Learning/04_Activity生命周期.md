# Activity 生命周期

Activity生命周期是Android开发中最重要的概念之一。理解它能帮助你正确管理资源、处理状态变化和避免常见的应用崩溃问题。

## 学习目标

- 理解Activity的6个核心生命周期回调方法
- 掌握Activity的三种主要状态
- 学会在不同生命周期阶段做合适的处理
- 理解常见的生命周期场景

## 核心概念

### Activity的三种主要状态

```
┌─────────────────────────────────────────────────────┐
│                  Activity 状态图                      │
└─────────────────────────────────────────────────────┘

                    ┌──────────────┐
                    │   Resumed    │  ← Activity可见且在前景
                    │   (运行中)    │     用户可以与之交互
                    └──────────────┘
                         ↑    ↓
                         │    │
        ┌─────────────────┤    └─────────────────┐
        │                 │                      │
        ↓                 ↓                       ↓
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│  Paused      │   │   Created    │   │  Destroyed   │
│ (暂停中)      │   │  (创建中)     │   │  (销毁中)     │
│ 可见但无法    │   │ 初始化资源    │   │ 释放资源     │
│ 交互         │   │              │   │              │
└──────────────┘   └──────────────┘   └──────────────┘
```

### 详细的生命周期流程

```
应用启动/重新打开
       ↓
   onCreate()          ← 创建Activity，初始化界面布局
       ↓
   onStart()           ← Activity变得可见（但不在前景）
       ↓
   onResume()          ← Activity获得焦点，进入前景，用户可交互
       ↓
  ━━━━━━━━━━━━━━━  用户与App交互的阶段  ━━━━━━━━━━━━━━━

  当另一个Activity启动或用户按Home键：
       ↓
   onPause()           ← Activity失去焦点，但仍可见
                       （可能被透明Activity覆盖）
       ↓
   onStop()            ← Activity完全不可见
       ↓
   onDestroy()         ← Activity被销毁，释放资源
       ↓
    销毁完成
```

## 实现细节

### 6个核心生命周期方法

#### 1. onCreate(savedInstanceState: Bundle?)
**时机**: Activity首次创建时调用
**作用**: 初始化Activity，设置UI布局，初始化必要的全局状态

```kotlin
class LoginActivity : ComponentActivity() {
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 恢复之前保存的状态（如屏幕旋转）
        if (savedInstanceState != null) {
            val savedEmail = savedInstanceState.getString("email")
            // 恢复UI状态
        }

        // 初始化ViewModel
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // 设置UI内容
        setContent {
            AndroidLoginAppTheme {
                LoginScreen(viewModel = viewModel)
            }
        }

        Log.d("LoginActivity", "onCreate called")
    }
}
```

**重要特点**:
- 只调用一次（重新启动时需要重新创建）
- `savedInstanceState` 包含之前保存的状态（屏幕旋转时恢复）
- 这是初始化重量级资源的好地方

#### 2. onStart()
**时机**: Activity变得对用户可见时调用
**作用**: 准备Activity即将进入前景

```kotlin
override fun onStart() {
    super.onStart()
    Log.d("LoginActivity", "onStart called - Activity now visible")

    // 启动定位服务、注册广播接收器等
    // 但不要立即更新UI或开始动画
}
```

**重要特点**:
- 可能调用多次（onStop → onStart）
- Activity此时不在前景，用户看不到交互反应
- 用于轻量级操作

#### 3. onResume()
**时机**: Activity获得焦点，进入前景，用户可以交互时调用
**作用**: 启动与Activity相关的操作

```kotlin
override fun onResume() {
    super.onResume()
    Log.d("LoginActivity", "onResume called - Activity in foreground")

    // 启动动画、动画帧更新
    // 更新传感器数据（如GPS、摄像头）
    // 注册生命周期感知的观察者
    viewModel.loadUserData()  // 重新加载数据
}
```

**重要特点**:
- 可能调用多次（onPause → onResume）
- **这是Activity最"活跃"的状态**
- 启动高消耗资源的操作的最佳地点

#### 4. onPause()
**时机**: Activity失去焦点但仍可见时调用
**作用**: 暂停与Activity相关的操作

```kotlin
override fun onPause() {
    super.onPause()
    Log.d("LoginActivity", "onPause called - Activity losing foreground")

    // 暂停动画
    // 暂停音频播放
    // 释放摄像头/GPS等独占资源
    // 提交未保存的数据

    // 这个方法必须执行快速！不要做耗时操作
    // 如果太慢，可能影响下一个Activity启动
}
```

**重要特点**:
- 执行必须快速（通常< 500ms）
- Activity仍然可见（可能被透明Activity覆盖）
- 保存重要数据的好地点
- **千万不要在这里做网络请求等耗时操作**

#### 5. onStop()
**时机**: Activity完全不可见时调用
**作用**: 做一些不需要用户看到的操作

```kotlin
override fun onStop() {
    super.onStop()
    Log.d("LoginActivity", "onStop called - Activity no longer visible")

    // 保存持久化数据到数据库/SharedPreferences
    // 取消注册广播接收器
    // 停止定位服务
    // 暂停相对消耗资源的操作

    // 这里可以做稍微耗时的操作，因为用户看不到
}
```

**重要特点**:
- Activity完全不可见
- 可以做相对耗时的操作（数据保存、数据库操作）
- 在此之后系统可能会销毁Activity（内存不足时）

#### 6. onDestroy()
**时机**: Activity被销毁时调用
**作用**: 释放所有资源

```kotlin
override fun onDestroy() {
    super.onDestroy()
    Log.d("LoginActivity", "onDestroy called - Activity being destroyed")

    // 取消所有任务和回调
    // 关闭数据库连接
    // 释放未被垃圾回收的资源
    // 停止线程和定时器

    // 在这里处理：
    // - ViewModel的清理
    // - 监听器的注销
    // - 线程的停止
}
```

**重要特点**:
- Activity即将被完全销毁
- 可能是用户按返回键或系统杀死进程
- 最后的清理机会

## 常见生命周期场景

### 场景1：正常启动应用
```
事件序列：
1. 用户点击应用图标
2. Activity创建

调用顺序：
onCreate() → onStart() → onResume()

状态: 应用正常运行，用户可交互
```

### 场景2：用户按Home键
```
事件序列：
1. 应用在前景运行
2. 用户按下Home键
3. 应用进入后台

调用顺序：
onPause() → onStop()

状态: 应用后台保活，数据保留在内存中
```

### 场景3：从后台返回应用
```
事件序列：
1. 应用在后台
2. 用户点击应用重新进入

调用顺序：
onStart() → onResume()

说明: onCreate 不会再次调用！Activity仍在内存中
```

### 场景4：用户按返回键退出
```
事件序列：
1. 应用正常运行
2. 用户按返回键
3. Activity栈顶被弹出

调用顺序：
onPause() → onStop() → onDestroy()

状态: Activity被完全销毁，内存被释放
```

### 场景5：屏幕旋转
```
事件序列：
1. 用户旋转设备
2. Activity需要重新适配屏幕

调用顺序（默认行为）：
onPause() → onStop() → onDestroy()
    ↓ (Activity重建)
onCreate(savedInstanceState) → onStart() → onResume()

使用savedInstanceState恢复数据：
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (savedInstanceState != null) {
        val savedValue = savedInstanceState.getString("key")
    }
}

// 在onPause或onStop时保存状态
override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString("key", value)
}
```

### 场景6：在两个Activity间切换
```
假设从LoginActivity切换到MainActivity：

LoginActivity 栈变化:
onCreate() → onStart() → onResume()  [用户在登录界面]
            ↓
        onPause()  [点击登录，MainActivity启动]
            ↓
        onStop()   [MainActivity已完全覆盖]
            ↓
        onDestroy() [用户不再返回登录界面，Activity被销毁]

MainActivity 栈变化:
onCreate() → onStart() → onResume()  [MainActivity启动]
```

## 实战应用：完整的Activity实现

```kotlin
class LoginActivity : ComponentActivity() {
    private lateinit var viewModel: LoginViewModel
    private var dataObserver: Observer<UserData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LoginActivity", "onCreate: initializing Activity")

        // 初始化ViewModel
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // 恢复保存的状态
        if (savedInstanceState != null) {
            val email = savedInstanceState.getString("email", "")
            // 恢复输入框内容等
        }

        // 设置Compose UI
        setContent {
            AndroidLoginAppTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { userId ->
                        navigateToProfile(userId)
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("LoginActivity", "onStart: Activity becoming visible")

        // 注册广播接收器
        // 启动位置更新（如果需要）
    }

    override fun onResume() {
        super.onResume()
        Log.d("LoginActivity", "onResume: Activity in foreground")

        // 启动UI相关的动画
        // 重新加载数据
        viewModel.checkAutoLogin()
    }

    override fun onPause() {
        super.onPause()
        Log.d("LoginActivity", "onPause: Activity losing focus")

        // 暂停动画
        // 释放摄像头等资源
        // 保存草稿内容
    }

    override fun onStop() {
        super.onStop()
        Log.d("LoginActivity", "onStop: Activity no longer visible")

        // 保存数据到数据库
        // 取消网络请求（如果可能）
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LoginActivity", "onDestroy: Activity being destroyed")

        // 停止所有后台任务
        // 取消所有注册的监听器
        viewModel.cleanup()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 保存临时状态（用于屏幕旋转恢复）
        outState.putString("email", "user@example.com")
    }

    private fun navigateToProfile(userId: Int) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("user_id", userId)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
```

## 常见问题

### Q: onCreate 和 onStart 的区别？
A:
- `onCreate`: 第一次创建时调用，可能伴随状态恢复
- `onStart`: 每次Activity变可见时调用，包括从后台返回

### Q: 为什么要在 onResume 而不是 onCreate 中启动相机？
A:
- `onCreate` 只调用一次，但从后台返回不会调用
- `onResume` 每次都会调用，确保相机在每次进入前景时都被初始化

### Q: onPause 为什么必须快速执行？
A:
- Android在调用 `onPause` 之后才会启动下一个Activity的 `onCreate`
- 如果 `onPause` 太慢，会阻塞整个应用，用户体验变差

### Q: 屏幕旋转时如何保存数据？
A:
- 使用 `onSaveInstanceState()` 保存临时UI状态
- 使用 ViewModel 保存应用数据（ViewModel在旋转时不会被销毁）
- 使用数据库/SharedPreferences保存持久化数据

### Q: 什么时候用 onStop，什么时候用 onPause 保存数据？
A:
- `onPause`: 保存**草稿**和**即时数据**（必须快速）
- `onStop`: 保存**最终数据**到数据库（可以做耗时操作）

### Q: 系统可能在什么时候销毁Activity?
A:
- 用户按返回键
- 调用 `finish()`
- 系统内存不足时（杀死后台Activity）
- 旋转屏幕时（默认行为）

## 生命周期最佳实践

### ✅ 应该做的事

| 生命周期 | 操作 |
|--------|------|
| onCreate | UI初始化、变量初始化、ViewModel创建 |
| onStart | 注册广播接收器 |
| onResume | 启动动画、启动传感器、更新UI |
| onPause | 暂停动画、释放独占资源、保存草稿 |
| onStop | 保存到数据库、取消网络请求 |
| onDestroy | 清理所有资源、停止线程 |

### ❌ 不应该做的事

| 生命周期 | 不应该做 |
|--------|--------|
| onCreate | 长网络请求、磁盘I/O |
| onStart | 不必要的广播注册 |
| onResume | 关闭相机（应该在onPause关闭） |
| onPause | **任何耗时操作**（会卡住UI） |
| onStop | 启动新的后台任务 |
| onDestroy | 启动新的Activity（内存不安全） |

## 与Compose的关系

在现代Android开发中（使用Compose），生命周期仍然存在，但多数情况下你不需要显式重写这些方法：

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Compose会自动处理大多数生命周期逻辑
        setContent {
            AndroidLoginAppTheme {
                // 在这里定义UI
                LoginScreen()
            }
        }
    }

    // 如果需要特殊处理，才显式重写其他方法
    override fun onResume() {
        super.onResume()
        // 特殊处理
    }
}
```

**Compose Lifecycle Libraries** 可以帮助你在Composable中响应生命周期事件：

```kotlin
@Composable
fun MyScreen() {
    val lifeCycleEvent = LocalLifecycleOwner.current.lifecycle.currentState

    DisposableEffect(lifeCycleEvent) {
        // 当进入Resumed状态时
        if (lifeCycleEvent == Lifecycle.State.RESUMED) {
            // 启动某个操作
        }

        onDispose {
            // 清理资源
        }
    }
}
```

## 参考资源

- [Android Activity生命周期官方文档](https://developer.android.com/guide/components/activities/activity-lifecycle)
- [Android生命周期感知的组件](https://developer.android.com/topic/libraries/architecture/lifecycle)
- [在Compose中使用生命周期](https://developer.android.com/jetpack/compose/lifecycle)

## 相关卡片

- [03_Activity和应用程序结构.md](03_Activity和应用程序结构.md) - Activity的基本概念
- [13_ViewModel生命周期.md](13_ViewModel生命周期.md) - ViewModel与Activity生命周期的关系

---

**关键要点总结**:

1. **6个核心方法**: onCreate → onStart → onResume → onPause → onStop → onDestroy
2. **3个主要状态**: Created、Started(Paused)、Resumed
3. **关键原则**:
   - `onResume`: 启动高消耗资源 ✅
   - `onPause`: 执行要快速，不要做耗时操作 ⚡
   - `onStop`: 保存数据到持久化存储 💾
4. **现代做法**: 使用ViewModel保存数据，让Activity负责UI生命周期管理
