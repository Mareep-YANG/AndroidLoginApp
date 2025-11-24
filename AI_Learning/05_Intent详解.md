# Intent详解

Intent是Android应用中最重要的通信机制。理解Intent能帮助你掌握如何让不同的组件（Activity、Service、BroadcastReceiver）相互通信，以及如何与系统交互。

## 学习目标

- 理解Intent的本质和作用
- 掌握显式Intent和隐式Intent的区别
- 理解Intent的数据传递机制
- 学会在实际项目中使用Intent

## 核心概念

### Intent是什么？

**Intent（意图）** 是Android中的一种**通信协议**。它用于在应用内或应用间传递操作请求和数据。

类比理解：
- **现实中的信件**: 你要告诉邮递员"我要寄一封信到北京"
- **Android中的Intent**: 你告诉系统"我要启动一个Activity或执行某个操作"

```
你的代码
   ↓
发送Intent给系统
   ↓
系统寻找合适的组件来处理
   ↓
启动那个组件
```

### Intent的核心职责

Intent主要有4个职责：

#### 1. **启动Activity** - 屏幕间导航
```
当前Activity
   ↓ (发送Intent)
系统
   ↓ (找到目标Activity)
目标Activity启动
```

例：用户点击"登录"按钮 → 从LoginActivity跳转到ProfileActivity

#### 2. **启动Service** - 后台执行任务
```
App前台
   ↓ (发送Intent)
系统
   ↓ (找到Service)
Service在后台运行
```

例：播放音乐（即使关闭App也继续播放）

#### 3. **发送Broadcast** - 系统级通知
```
你的代码
   ↓ (发送广播Intent)
系统广播机制
   ↓
所有注册监听的BroadcastReceiver收到
```

例：系统电量低、系统启动完成、网络连接状态改变

#### 4. **隐式启动系统功能** - 调用系统应用
```
你的代码
   ↓ (发送Intent)
系统
   ↓ (查询能处理的应用)
系统App/第三方App处理
```

例：打开浏览器、发送短信、拨打电话

## 显式Intent vs 隐式Intent

这是Intent最关键的两个概念。

### 显式Intent - 明确指定目标

**含义**: 你**明确告诉系统要启动哪个Activity**

```
特征：指定了确切的目标组件
适用场景：应用内部屏幕跳转
```

**类比**: 发信时写明具体地址——"寄给北京市朝阳区小王"

```
Intent的组成：
└─ 包名 (package): cn.mareep.androidloginapp
└─ 类名 (class): LoginActivity
```

### 隐式Intent - 描述要做什么

**含义**: 你**只说要做什么操作，让系统去找能处理的应用**

```
特征：不指定具体的目标，只说想做什么
适用场景：调用系统功能、启动第三方应用
```

**类比**: 发信时不写具体地址——"寄给北京市所有收快递的地方"

```
Intent的组成：
└─ Action (动作): android.intent.action.SEND
└─ Data (数据): 要发送的内容
└─ Category (分类): CATEGORY_DEFAULT
└─ Type (类型): image/*
```

### 对比表

| 特性 | 显式Intent | 隐式Intent |
|-----|-----------|----------|
| **目标明确性** | ✅ 明确指定 | ❌ 模糊描述 |
| **安全性** | 更安全，只启动自己的应用 | 可能启动第三方应用 |
| **灵活性** | 灵活性低，只能启动已知的组件 | 灵活性高，可发现新应用 |
| **应用场景** | 应用内导航 | 系统功能、第三方集成 |
| **例子** | 登录→个人中心 | 打开浏览器、分享文件 |

## Intent的组成部分

一个Intent包含以下信息：

### 1. **Component（组件）** - 显式Intent的关键
- **包名**: 应用的包名
- **类名**: 要启动的Activity/Service的类名
- 作用：指定具体要启动谁

### 2. **Action（动作）** - 隐式Intent的关键
- **含义**: 你想执行什么操作
- **格式**: 通常是 `android.intent.action.XXX` 的形式
- **常见例子**:
  - `ACTION_VIEW` - 查看某个内容
  - `ACTION_SEND` - 分享内容
  - `ACTION_DIAL` - 拨号
  - `ACTION_CALL` - 拨电话

### 3. **Category（分类）** - 对Action的补充说明
- **含义**: 对Action的进一步说明
- **常见例子**:
  - `CATEGORY_DEFAULT` - 默认类别
  - `CATEGORY_LAUNCHER` - 可以在启动器中启动
  - `CATEGORY_BROWSABLE` - 可以从网页打开

### 4. **Data（数据）** - Intent携带的数据
- **含义**: Intent要传递的数据
- **URI格式**: `scheme://host:port/path?query=value`
- **例子**: `https://www.google.com`、`tel:10086`

### 5. **Type（类型）** - 数据的MIME类型
- **含义**: 数据是什么类型
- **例子**: `text/plain`、`image/jpeg`、`video/mp4`

### 6. **Extras（额外数据）** - 键值对数据
- **含义**: 传递应用特定的数据
- **Bundle形式**: 类似于Map的数据结构
- **例子**: 用户ID、用户名等

## 显式Intent的实际工作流程

当你发送显式Intent启动Activity时：

```
你的代码
   ↓
创建Intent(包名, 类名)
   ↓
startActivity(intent)
   ↓
系统找到对应的Activity
   ↓
调用该Activity的onCreate()
   ↓
Activity显示在屏幕上
```

**数据流**:
```
Intent → Activity的intent属性
         ↓
         通过intent.getXxxExtra()获取数据
```

## 隐式Intent的实际工作流程

当你发送隐式Intent时：

```
你的代码
   ↓
创建Intent(action, data)
   ↓
startActivity(intent)
   ↓
系统查询所有Activity的intent-filter
   ↓
匹配符合条件的Activity
   ↓
如果只有1个 → 直接启动
如果有多个 → 弹出选择对话框（Chooser）
如果0个 → 应用崩溃（ActivityNotFoundException）
```

**匹配规则**：
```
应用的AndroidManifest.xml中：
<activity>
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="https" />
    </intent-filter>
</activity>

这个Activity就能处理：
- action = VIEW
- 有DEFAULT分类
- scheme = https的链接
```

## Intent常见应用场景

### 场景1：应用内导航（显式Intent）
**目标**: 从LoginActivity跳转到ProfileActivity
**方式**: 显式Intent（明确指定目标）
```
谁发: LoginActivity
发送内容: "我要启动ProfileActivity"
系统做什么: 查找ProfileActivity，启动它
```

### 场景2：打开浏览器（隐式Intent）
**目标**: 打开某个网址
**方式**: 隐式Intent（描述要做什么）
```
谁发: 你的应用
发送内容: "我要查看一个网页，URL是..."
系统做什么: 查找能打开网页的应用（通常是浏览器），启动它
```

### 场景3：分享内容（隐式Intent）
**目标**: 分享一张图片到微信、QQ等
**方式**: 隐式Intent + Chooser
```
谁发: 你的应用
发送内容: "我要分享一张图片"
系统做什么: 查找所有能接收图片的应用，弹出选择框让用户选择
```

### 场景4：拨打电话（隐式Intent）
**目标**: 点击一个电话号码，启动拨号应用
**方式**: 隐式Intent
```
谁发: 你的应用
发送内容: "我要拨打某个电话号码"
系统做什么: 启动拨号应用，填入号码
```

### 场景5：后台任务（显式Intent）
**目标**: 播放音乐，即使关闭应用也继续播放
**方式**: 显式Intent启动Service
```
谁发: 你的应用
发送内容: "我要启动MusicService来后台播放"
系统做什么: 启动Service，Service在后台运行
```

## Intent与其他概念的关系

### Intent vs Navigation（导航库）

在现代Android开发中，特别是使用Compose时：

```
旧时代（多Activity）:
显式Intent → Activity跳转
startActivity(Intent)

现代时代（单Activity + Compose）:
Navigation Compose → Composable屏幕切换
navController.navigate("profile")
```

**不同点**:
- Intent是Android底层的通信机制
- Navigation是Jetpack提供的高级导航库
- Navigation简化了屏幕间的数据传递和返回栈管理

**你的项目使用的是Navigation Compose**，所以大多数场景下不需要显式Intent。

### Intent vs Bundle

```
Intent = 邮差 + 信件
Bundle = 信件的内容

Intent.putExtra() 其实是把数据放入内部的Bundle
```

## 常见问题

### Q: 显式Intent一定比隐式Intent安全吗？
A: 是的，因为：
- 显式Intent只启动你明确指定的应用，无法被其他应用拦截
- 隐式Intent可能被恶意应用注册intent-filter来拦截

### Q: 我如何知道某个隐式Intent是否有应用能处理？
A: 可以查询PackageManager：
```
val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
val apps = packageManager.queryIntentActivities(intent, 0)
if (apps.isEmpty()) {
    Toast.makeText(this, "没有应用能打开此链接", Toast.LENGTH_SHORT).show()
}
```

### Q: 为什么我的隐式Intent启动失败了？
A: 常见原因：
1. 没有应用注册能处理该Intent的intent-filter
2. intent-filter的action/data/category不匹配
3. 忘记在AndroidManifest.xml中声明intent-filter

### Q: startActivity() 和 startActivityForResult() 有什么区别？
A:
- `startActivity()`: 单向启动，无返回结果
- `startActivityForResult()`: 启动后等待结果返回（如选择图片后返回选中的路径）
- 现代做法：使用Activity Result API代替startActivityForResult()

### Q: Intent能传递哪些类型的数据？
A: 只能传递：
- 基本类型（Int、String、Boolean等）
- 实现了Serializable或Parcelable接口的对象
- Bundle对象

不能传递：
- 太大的对象（Intent有大小限制，通常~1MB）
- 数据库连接、网络连接等系统资源

## Intent的设计哲学

Intent体现了Android的一个重要设计理念：**隔离和解耦**

```
你的应用不需要知道具体是哪个应用处理你的请求，
只需要描述你的需求，让系统去匹配最合适的处理者。

这样做的好处：
✅ 应用之间互相独立
✅ 新应用可以轻松注册处理某个类型的Intent
✅ 用户可以选择用哪个应用处理
✅ 系统维护变得灵活
```

## 小结

| 概念 | 用途 |
|-----|-----|
| **Intent本质** | Android的通信协议，传递操作和数据 |
| **显式Intent** | 应用内导航，明确指定目标 |
| **隐式Intent** | 系统功能调用，让系统找合适的处理者 |
| **Component** | 显式Intent的关键，指定包名和类名 |
| **Action** | 隐式Intent的关键，描述要做什么 |
| **Data** | 传递给目标组件的数据 |
| **Extras** | 键值对形式的额外数据 |

## 参考资源

- [Android Intent官方文档](https://developer.android.com/guide/components/intents-filters)
- [Intent和Intent Filter](https://developer.android.com/guide/components/intents-filters)
- [隐式Intent的安全问题](https://developer.android.com/guide/topics/intents/intent-filters)

## 相关卡片

- [03_Activity和应用程序结构.md](03_Activity和应用程序结构.md) - Activity基本概念
- [04_Activity生命周期.md](04_Activity生命周期.md) - Activity的生命周期回调

---

**关键要点总结**:

1. **Intent是通信协议** - 连接Android各个组件
2. **两种主要类型** - 显式（明确目标）vs 隐式（描述需求）
3. **应用内导航用显式** - LoginActivity → ProfileActivity
4. **系统功能用隐式** - 调用浏览器、分享、拨号等
5. **现代项目多用Navigation** - 但Intent的理解依然重要
