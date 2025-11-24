# Activity启动模式与任务栈

这是深入理解Android系统架构的关键概念。了解Activity的启动模式和任务栈能帮助你构建更符合用户预期的应用行为。

## 学习目标

- 理解Activity是系统层面还是进程内的概念
- 掌握任务栈的概念和工作原理
- 理解4种不同的启动模式
- 学会在实际场景中选择合适的启动模式

## 核心概念

### Activity是系统层面的概念

**关键认识**: Activity是**系统层面的组件**，而不是应用内部的概念。

```
系统视角（WindowManagerService）:
┌────────────────────────────────┐
│        Android系统              │
│  ┌──────────────────────────┐  │
│  │   窗口管理服务(WMS)       │  │
│  │  管理所有Activity的显示    │  │
│  └──────────────────────────┘  │
│                                 │
│  ┌──────────────────────────┐  │
│  │  ActivityManager Service  │  │
│  │  管理Activity的生命周期    │  │
│  └──────────────────────────┘  │
└────────────────────────────────┘
         ↑
         │ 通过Binder IPC
         │
┌────────────────────────────────┐
│      你的应用进程                 │
│  (独立的JVM虚拟机实例)           │
│                                 │
│  ├─ MainActivity                │
│  ├─ LoginActivity               │
│  └─ ProfileActivity             │
└────────────────────────────────┘
```

**深层原理**:

1. **Activity属于应用进程** - Activity的代码运行在你应用的进程内
2. **但管理权属于系统** - Activity的生命周期、显示、顺序由Android系统的ActivityManagerService(AMS)管理
3. **多应用共享系统服务** - 所有应用的Activity都通过Binder与系统通信

**类比理解**:
- 你的应用 = 工厂
- Activity = 工厂生产的产品
- Android系统 = 政府相关部门
- ActivityManager = 负责管理所有工厂生产的产品

系统不仅知道哪个应用有哪些Activity，还负责调度它们的显示顺序、销毁时机、栈的管理等。

---

### 任务栈(Back Stack)是什么？

**定义**: 任务栈是系统用来**管理Activity启动顺序和返回流程的数据结构**。

```
任务栈 = LIFO栈（后进先出）

假设你有一个社交应用：
启动应用
    ↓
[SplashActivity]                栈顶

用户点击"进入主页"
    ↓
[SplashActivity, MainActivity]   栈顶
                ↑

用户点击"查看某人资料"
    ↓
[SplashActivity, MainActivity, ProfileActivity]  栈顶
                                    ↑

用户点击"返回键"
    ↓
ProfileActivity被弹出并销毁
[SplashActivity, MainActivity]   栈顶
                ↑

再点"返回键"
    ↓
MainActivity被弹出并销毁
[SplashActivity]                栈顶

再点"返回键"
    ↓
SplashActivity被弹出并销毁，应用退出
[]                              栈为空
```

**重要特性**:

```
LIFO（后进先出）:
- 最后进入的Activity最先被移除
- 按返回键 = 弹出栈顶Activity

唯一性:
- 默认情况下，每次启动Activity都会在栈中创建新实例
- 即使启动同一个Activity多次，栈中也会有多个实例

进程与栈:
- 一个应用进程可以有多个任务栈
- 但大多数情况下，一个应用只有一个任务栈
```

---

### 任务栈与应用进程的关系

**重点**: 任务栈和应用进程是**两个不同的管理维度**

```
┌─────────────────────────────────────┐
│         Android系统                  │
│  TaskStack1         TaskStack2       │
│  ┌──────────┐      ┌──────────┐     │
│  │Activity1 │      │Activity3 │     │
│  │Activity2 │      │Activity4 │     │
│  └──────────┘      └──────────┘     │
└─────────────────────────────────────┘
       ↑                     ↑
       │                     │
  属于进程A              属于进程B

如果Activity3属于应用B:
- Activity3在自己的进程中运行
- 但在应用A的任务栈中管理
```

**现实场景**:

假设你的应用启动了来自其他应用的Activity：

```
你的应用(进程A) 的任务栈:
┌──────────────────────────────┐
│  YourAppActivity1            │  ← 你的应用
├──────────────────────────────┤
│  OtherAppActivity1           │  ← 其他应用的Activity
│  (运行在其他应用的进程中)     │
├──────────────────────────────┤
│  YourAppActivity2            │  ← 你的应用
└──────────────────────────────┘

这个混合栈是一个任务！
当按返回键时，会按栈的顺序返回。
```

---

## Activity的4种启动模式

启动模式决定了当启动Activity时，系统如何处理栈中的实例。

### 模式1: standard（标准模式）

**特点**: 每次启动都创建新实例

```
启动流程示意:

初始状态:
[Activity A]

点击按钮启动B:
[Activity A, Activity B]  ← B的新实例

再启动B:
[Activity A, Activity B, Activity B]  ← B的新实例2

再启动B:
[Activity A, Activity B, Activity B, Activity B]  ← B的新实例3

点返回键:
[Activity A, Activity B, Activity B]  ← B实例3被销毁

点返回键:
[Activity A, Activity B]  ← B实例2被销毁
```

**场景**:
- 普通页面跳转（最常用）
- 不需要复用实例的页面

**缺点**:
- 内存占用高（如果重复启动同一个Activity）
- 可能出现栈中有多个同类Activity的情况

---

### 模式2: singleTop（栈顶单例模式）

**特点**: 如果Activity已在栈顶，就复用，否则创建新实例

```
初始状态:
[Activity A, Activity B]  ← B在栈顶

启动B:
[Activity A, Activity B]  ← 复用B，不创建新实例
                          ← B的onNewIntent()被调用

启动C:
[Activity A, Activity B, Activity C]  ← 创建C新实例

启动B:
[Activity A, Activity B, Activity C, Activity B]  ← B创建新实例（因为B不在栈顶）
```

**关键**: onNewIntent()回调
```
当Activity已在栈顶，再次启动它时:
onNewIntent(intent) 被调用
                ↓
你可以在这里处理新的Intent数据
而不用重新走onCreate → onStart → onResume
```

**场景**:
- 推送通知点击（需要更新显示，但不创建新实例）
- 搜索结果页面（新的搜索词应该更新当前页，而不是新建页面）

---

### 模式3: singleTask（栈内单例模式）

**特点**: 同一个任务栈中只有一个实例，启动时会清除该Activity上方的所有Activity

```
初始状态:
[Activity A, Activity B]

启动C（singleTask）:
[Activity A, Activity B, Activity C]  ← 创建C

启动D:
[Activity A, Activity B, Activity C, Activity D]  ← 创建D

再启动C（singleTask）:
[Activity A, Activity B, Activity C]  ← D被销毁，C被复用
                                       ← C的onNewIntent()被调用
```

**栈的变化**:
```
启动前: [A, B, C, D]
启动C:  D被弹出并销毁
结果:  [A, B, C]  ← C复用，D消失
```

**场景**:
- App主页面（无论如何启动都只有一个实例）
- 从任何地方返回主页
- 登录成功后跳转到主界面

**真实场景**: 微信的聊天列表页面
```
用户可能的操作路径:
1. 进入聊天列表(ConversationList)
2. 点进某个聊天(ChatActivity)
3. 点击用户头像看资料(ProfileActivity)
4. 点击关注用户，返回资料页
5. 点返回，返回聊天页
6. 点返回，返回聊天列表

此时聊天列表应该用singleTask，确保栈中只有一个。
如果用standard，重复进出会创建多个聊天列表实例。
```

---

### 模式4: singleInstance（全局单例模式）

**特点**: 这个Activity必须单独占用一个任务栈，是全局唯一的

```
系统中有多个任务栈:

任务栈1（你的应用A）:
[MainActivity, Activity B]

任务栈2（Activity C - singleInstance）:
[Activity C]  ← C单独占用一个栈

任务栈3（应用B）:
[ApplicationB Activity1]

启动C时，如果已存在:
[MainActivity, Activity B] 不变
[Activity C] 被复用，onNewIntent()被调用
```

**多栈示意**:
```
任务栈1: [A, B, D]  ← 返回键弹出顺序是D, B, A
任务栈2: [C]        ← C的singleInstance栈

用户操作:
1. 在D中点返回 → B显示
2. 在B中点返回 → A显示
3. 在A中点返回 → 应用退出

此时C栈一直存在，除非C被销毁或用户点返回时C还在栈顶
```

**场景**:
- 来电显示界面（来电时显示，无论用户在做什么）
- 紧急提示界面（系统级的全局通知）
- 极少使用

---

## 启动模式的声明方式

在AndroidManifest.xml中声明：

```xml
<activity
    android:name=".MainActivity"
    android:launchMode="singleTask" />

<activity
    android:name=".LoginActivity"
    android:launchMode="singleTop" />

<activity
    android:name=".NotificationActivity"
    android:launchMode="singleInstance" />
```

---

## 真实场景案例分析

### 案例1: 电商应用的购物流程

```
场景: 用户在浏览商品，点击商品进入详情，选择购买，结算

页面栈:

初始状态 - MainActivity(首页):
[MainActivity]

点击搜索商品 - SearchActivity:
[MainActivity, SearchActivity]

搜索结果点击商品 - ProductDetailActivity:
[MainActivity, SearchActivity, ProductDetailActivity]

点击"购买" - CheckoutActivity:
[MainActivity, SearchActivity, ProductDetailActivity, CheckoutActivity]

支付成功弹出成功页 - OrderSuccessActivity:
[MainActivity, SearchActivity, ProductDetailActivity, CheckoutActivity, OrderSuccessActivity]

现在点"继续购物" - 应该返回首页(MainActivity)

如果没有启动模式管理:
点返回会经过: OrderSuccess → Checkout → ProductDetail → Search → MainActivity
用户需要按5次返回键才能到首页

解决方案:
在OrderSuccessActivity跳转回首页时:
Intent intent = new Intent(this, MainActivity.class);
intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
startActivity(intent);
```

**涉及的概念**:
- `FLAG_ACTIVITY_CLEAR_TOP`: 清除目标Activity上方的所有Activity
- `FLAG_ACTIVITY_SINGLE_TOP`: 如果目标Activity在栈顶，复用而不创建新实例

---

### 案例2: 社交应用的消息通知

```
场景: 用户在应用内，突然收到多条来自微信的消息推送

初始状态:
[SplashActivity, MainActivity, FriendsListActivity]  ← 当前显示

用户没有点击通知，继续使用应用:
[SplashActivity, MainActivity, FriendsListActivity, ChatDetailActivity]
                                                      ↑ 打开某个聊天

收到微信推送(点击通知):
推送会启动微信的NotificationActivity

此时栈变成:
[SplashActivity, MainActivity, FriendsListActivity, ChatDetailActivity, 微信NotificationActivity]

问题: 用户返回时会回到自己的应用，这不符合预期
用户期望: 点通知进微信，返回时也回到微信

解决方案:
微信的NotificationActivity应该使用singleInstance
这样它会在单独的栈中:

任务栈1(你的应用):
[SplashActivity, MainActivity, FriendsListActivity, ChatDetailActivity]

任务栈2(微信):
[微信NotificationActivity]

用户在微信页面点返回:
[微信NotificationActivity] 栈被清空
应用显示: [SplashActivity, MainActivity, FriendsListActivity, ChatDetailActivity]

这样用户体验更自然
```

---

### 案例3: 登录应用(你的项目)

```
场景: AndroidLoginApp的启动流程

初始状态:
[MainActivity(显示LoginScreen)]

用户成功登录:
需要显示ProfileScreen，但不希望用户能返回登录页

当前栈:
[MainActivity]

导航到ProfileScreen:
[MainActivity] 栈中仍然只有一个Activity
              ↓
            ProfileScreen通过Navigation Compose显示
            (不创建新的Activity)

用户在ProfileScreen点返回:
返回到LoginScreen
[MainActivity]

如果用多Activity实现:
[LoginActivity] → 登录成功 → [ProfileActivity]

此时用户点返回键会回到LoginActivity
解决方案: 在跳转时清除返回栈

Intent intent = new Intent(this, ProfileActivity.class);
intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;
startActivity(intent);

或者重写onBackPressed():
override fun onBackPressed() {
    super.onBackPressed()
    finish()  // 销毁当前Activity
}

你的项目使用Navigation Compose，这样的问题自动解决了！
因为只有一个MainActivity，屏幕切换不涉及Activity栈管理。
```

---

## 启动模式对比表

| 模式 | 特点 | 栈内实例数 | 场景 |
|-----|-----|----------|------|
| **standard** | 每次创建新实例 | 多个 | 普通页面跳转 |
| **singleTop** | 栈顶复用 | 1+多个 | 推送通知、搜索结果 |
| **singleTask** | 栈内复用 | 1 | 应用首页、主页面 |
| **singleInstance** | 全局唯一 | 1 | 全局通知、来电显示 |

---

## 常见问题

### Q: 为什么说Activity是系统层面的概念？
A:
- Activity代码运行在你的应用进程中
- 但Activity的生命周期、显示顺序、返回栈管理完全由系统的ActivityManagerService控制
- 系统知道全局所有应用有哪些Activity
- 系统可以在任何时候销毁你的Activity（内存不足时）

### Q: 一个应用可以有多个任务栈吗？
A: 可以。虽然不常见，但以下情况会创建多个栈：
- 设置android:taskAffinity不同的值
- 使用不同的taskAffinity启动Activity
- 启动其他应用的Activity时

但99%的应用只用一个栈。

### Q: singleTask和singleTop的区别是什么？
A:
- **singleTop**: 只检查栈顶，如果Activity在栈顶就复用，否则创建新实例
- **singleTask**: 检查整个栈，如果Activity已存在就复用，并清除其上的所有Activity

### Q: 我应该怎么选择启动模式？
A: 默认使用standard，除非你有特殊需求：
- 需要全局唯一 → singleTask
- 需要防止重复创建 → singleTop
- 非常罕见情况 → singleInstance

### Q: Intent.FLAG_ACTIVITY_NEW_TASK是什么？
A: 这是通过Intent flags设置临时的启动模式行为：
```
FLAG_ACTIVITY_NEW_TASK: 在新任务栈中启动(临时singleTask行为)
FLAG_ACTIVITY_CLEAR_TOP: 启动目标Activity，清除其上的所有Activity
FLAG_ACTIVITY_SINGLE_TOP: 临时singleTop行为
FLAG_ACTIVITY_CLEAR_TASK: 清除目标任务栈中的所有Activity
```

---

## 小结

| 概念 | 说明 |
|-----|------|
| **Activity层级** | 系统层面管理，进程内运行 |
| **任务栈** | LIFO栈，存放应用启动的Activity实例 |
| **standard** | 每次创建新实例（最常用） |
| **singleTop** | 栈顶复用，防止重复创建 |
| **singleTask** | 栈内唯一，清除上层实例 |
| **singleInstance** | 全局唯一，单独任务栈 |

## 参考资源

- [Android任务栈官方文档](https://developer.android.com/guide/components/activities/tasks-and-back-stack)
- [Activity启动模式详解](https://developer.android.com/guide/components/activities)

## 相关卡片

- [03_Activity和应用程序结构.md](03_Activity和应用程序结构.md) - Activity基本概念
- [04_Activity生命周期.md](04_Activity生命周期.md) - Activity生命周期回调
- [05_Intent详解.md](05_Intent详解.md) - Intent与Activity的通信

---

**关键要点总结**:

1. **Activity是系统层面的组件** - 代码在进程内，但由系统管理生命周期和栈
2. **任务栈是LIFO结构** - 最后进的Activity最先出栈
3. **4种启动模式** - standard最常用，singleTask用于主页，singleTop防止重复
4. **现代项目用Navigation** - 如果用单Activity + Navigation Compose就不需要关心这些
5. **但理解原理很重要** - 有助于深入掌握Android系统架构
