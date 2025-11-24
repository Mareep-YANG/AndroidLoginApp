# SharedPreferences详解

## 学习目标
- 理解 SharedPreferences 的用途和适用场景
- 掌握 SharedPreferences 的读写操作
- 理解 apply() 与 commit() 的区别
- 了解 SharedPreferences 的最佳实践

## 核心概念

### 什么是 SharedPreferences
SharedPreferences 是 Android 提供的轻量级数据存储方案，以键值对（Key-Value）的形式将数据保存在 XML 文件中。

**特点：**
- 轻量级：适合存储少量简单数据
- 持久化：应用关闭后数据依然保留
- 简单易用：API 设计简洁直观
- 文件存储：数据以 XML 格式存储在 `/data/data/<package_name>/shared_prefs/` 目录

### 适用场景
| 场景 | 示例 |
|------|------|
| 用户偏好设置 | 主题、语言、通知开关 |
| 简单状态记录 | 是否首次启动、上次登录时间 |
| 轻量配置信息 | 用户名、签名等显示信息 |
| 登录状态 | 记住密码、自动登录标记 |

### 不适用场景
- **大量数据**：应使用数据库（Room/SQLite）
- **复杂结构**：嵌套对象应使用数据库或文件
- **敏感信息**：密码等应使用 EncryptedSharedPreferences
- **频繁读写**：高频操作影响性能

## 支持的数据类型

SharedPreferences 支持以下基本类型：

| 类型 | 方法 |
|------|------|
| String | getString() / putString() |
| Int | getInt() / putInt() |
| Long | getLong() / putLong() |
| Float | getFloat() / putFloat() |
| Boolean | getBoolean() / putBoolean() |
| Set<String> | getStringSet() / putStringSet() |

## 核心 API

### 获取 SharedPreferences 实例

```kotlin
// 方式1：指定文件名（推荐）
val prefs = getSharedPreferences("file_name", Context.MODE_PRIVATE)

// 方式2：使用默认文件（Activity专用）
val prefs = getPreferences(Context.MODE_PRIVATE)
```

**模式说明：**
- `MODE_PRIVATE`：私有模式，只有本应用可访问（推荐使用）
- `MODE_WORLD_READABLE`/`MODE_WORLD_WRITEABLE`：已废弃，不安全

### 读取数据

```kotlin
val prefs = getSharedPreferences("user_profile", Context.MODE_PRIVATE)

// 读取时提供默认值，防止空指针
val username = prefs.getString("username", "默认用户")
val age = prefs.getInt("age", 0)
val isVip = prefs.getBoolean("is_vip", false)
```

### 写入数据

```kotlin
val prefs = getSharedPreferences("user_profile", Context.MODE_PRIVATE)

// 通过 Editor 写入数据
prefs.edit()
    .putString("username", "张三")
    .putInt("age", 25)
    .putBoolean("is_vip", true)
    .apply()  // 异步提交
```

## apply() vs commit()

| 特性 | apply() | commit() |
|------|---------|----------|
| 返回值 | 无（void） | boolean（成功/失败） |
| 执行方式 | 异步写入磁盘 | 同步写入磁盘 |
| 阻塞主线程 | 否 | 是 |
| 推荐场景 | 大多数场景 | 需要确认写入成功时 |

**最佳实践：** 优先使用 `apply()`，除非需要确认写入结果。

## 实现模式

### 封装工具类模式

```kotlin
object PreferenceManager {
    private const val PREF_NAME = "app_preferences"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getUsername(context: Context): String {
        return getPrefs(context).getString("username", "") ?: ""
    }

    fun setUsername(context: Context, username: String) {
        getPrefs(context).edit().putString("username", username).apply()
    }
}
```

### Kotlin 属性委托模式（进阶）

```kotlin
class UserPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    var username: String
        get() = prefs.getString("username", "") ?: ""
        set(value) = prefs.edit().putString("username", value).apply()

    var signature: String
        get() = prefs.getString("signature", "这个人很懒") ?: "这个人很懒"
        set(value) = prefs.edit().putString("signature", value).apply()
}
```

## SharedPreferences vs Room 数据库

| 特性 | SharedPreferences | Room 数据库 |
|------|-------------------|-------------|
| 数据结构 | 键值对 | 表结构 |
| 数据量 | 少量（KB级） | 大量（MB级） |
| 查询能力 | 无 | SQL 查询 |
| 关系数据 | 不支持 | 支持 |
| 使用复杂度 | 简单 | 中等 |
| 典型用途 | 配置、偏好 | 用户数据、业务数据 |

**项目中的实践：**
- **Room**：存储用户账户（邮箱/密码）
- **SharedPreferences**：存储用户资料（用户名/签名）

## 常见问题

### Q: SharedPreferences 是线程安全的吗？
A: 读取操作是线程安全的，但写入操作需要注意。使用 `apply()` 可以安全地从任何线程调用，它会在后台线程执行实际写入。

### Q: 如何监听数据变化？
A: 使用 `registerOnSharedPreferenceChangeListener()`：

```kotlin
prefs.registerOnSharedPreferenceChangeListener { sharedPrefs, key ->
    when (key) {
        "username" -> updateUsernameUI(sharedPrefs.getString(key, ""))
    }
}
```

### Q: 如何存储复杂对象？
A: 可以将对象序列化为 JSON 字符串存储，但建议使用 Room 数据库处理复杂数据。

## 安全注意事项

1. **不要存储敏感信息**：密码、Token 等应使用 EncryptedSharedPreferences
2. **使用 MODE_PRIVATE**：确保数据不被其他应用访问
3. **避免存储过多数据**：大数据量会影响读取性能

## 参考资源
- [官方文档 - Save key-value data](https://developer.android.com/training/data-storage/shared-preferences)
- [EncryptedSharedPreferences](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences)

## 相关卡片
- [Room数据库使用](04_Room数据库使用.md)
- [Activity和应用程序结构](03_Activity和应用程序结构.md)
