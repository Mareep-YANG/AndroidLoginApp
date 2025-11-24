# Android常见布局

布局是构建Android UI的基础。掌握各种布局类型能帮助你快速高效地开发用户界面。本卡片讲解传统布局系统和现代Compose的关系。

## 学习目标

- 理解Android布局的本质和分类
- 掌握常见的5种传统布局
- 理解Compose如何改变了布局方式
- 学会在不同场景选择合适的布局

## 核心概念

### 布局是什么？

**布局（Layout）** 是用来**管理和组织UI组件位置和大小的容器**。

类比理解：
- **现实中的布局**: 商店的货架布置——怎样放商品能看起来整齐美观
- **Android中的布局**: 如何在屏幕上放置按钮、文本框等组件

```
布局的职责：
1. 定位 - 确定每个组件在屏幕上的位置
2. 尺寸 - 确定每个组件的宽度和高度
3. 对齐 - 确定组件相对于其他组件的位置关系
4. 响应式 - 适应不同屏幕大小
```

### 布局的层级概念

```
屏幕(最外层)
   ↓
根布局(一个Layout)
   ├─ 子组件1(Button)
   ├─ 子组件2(TextView)
   └─ 子布局(嵌套的Layout)
        ├─ 孙组件1
        └─ 孙组件2
```

**嵌套布局**是常见的做法，但过度嵌套会影响性能。

---

## 传统Android布局体系

### 布局体系概览

在使用Compose之前，Android用XML布局文件定义UI：

```
LinearLayout
   用途：一行或一列排列组件
   场景：简单的列表、表单

FrameLayout
   用途：重叠放置组件
   场景：卡片、浮动按钮、背景图

RelativeLayout
   用途：相对位置排列
   场景：复杂的位置关系

GridLayout
   用途：网格排列
   场景：相册、应用网格

ConstraintLayout
   用途：约束布局（最强大）
   场景：复杂UI、响应式设计
```

---

### 布局1: LinearLayout（线性布局）

**特点**: 按照**线性方向**（横向或纵向）排列子组件

```
纵向排列(orientation=vertical):
┌─────────────┐
│  按钮1      │
├─────────────┤
│  按钮2      │
├─────────────┤
│  按钮3      │
└─────────────┘

横向排列(orientation=horizontal):
┌────┬────┬────┐
│按钮│按钮│按钮│
│ 1  │ 2  │ 3  │
└────┴────┴────┘
```

**常用属性**:
- `android:orientation` - 排列方向（vertical/horizontal）
- `android:gravity` - 内容对齐方式
- `android:layout_weight` - 权重（占用剩余空间的比例）

**场景**:
- 简单的垂直或水平列表
- 表单输入（从上到下排列输入框）
- 你的项目中就用了LinearLayout排列登录表单

---

### 布局2: FrameLayout（帧布局）

**特点**: 后面的组件会**覆盖**前面的组件，默认所有子组件都在左上角

```
初始状态：
[ImageView(背景图)]

添加TextView：
[ImageView(背景图)]
   ↓ (覆盖在上面)
[TextView(文本)]

添加Button：
[ImageView(背景图)]
   ↓
[TextView(文本)]
   ↓ (在最上层)
[Button]
```

**类比**: 一叠纸片，最后放上去的纸片在最上面

**常用属性**:
- `android:layout_gravity` - 子组件在容器中的位置

**场景**:
- 卡片UI（背景+内容重叠）
- 浮动按钮(FAB)覆盖在列表上
- 图片加滤镜（图片+半透明遮罩层）
- 加载动画覆盖内容

---

### 布局3: RelativeLayout（相对布局）

**特点**: 每个子组件相对于**其他组件或容器的位置**来定位

```
相对定位方式：

相对于容器：
├─ layout_alignParentTop - 对齐容器顶部
├─ layout_alignParentBottom - 对齐容器底部
├─ layout_centerInParent - 居中
└─ layout_alignParentRight - 对齐容器右边

相对于其他组件：
├─ layout_above - 在某组件上方
├─ layout_below - 在某组件下方
├─ layout_toLeftOf - 在某组件左边
└─ layout_toRightOf - 在某组件右边

例子：
┌──────────────────┐
│ 标题(顶部居中)    │
├──────────────────┤
│                  │
│  内容区域        │
│                  │
├──────────────────┤
│[取消] [确定]     │
└──────────────────┘

标题：layout_alignParentTop + layout_centerHorizontal
按钮：layout_alignParentBottom + layout_alignParentRight
```

**使用复杂度**: 中等（相比ConstraintLayout较老）

**场景**:
- 对话框布局（标题+内容+按钮）
- 简单的位置关系定位

---

### 布局4: GridLayout（网格布局）

**特点**: 将子组件排列成**行列网格**

```
3列网格：
┌────┬────┬────┐
│ 1  │ 2  │ 3  │
├────┼────┼────┤
│ 4  │ 5  │ 6  │
├────┼────┼────┤
│ 7  │ 8  │ 9  │
└────┴────┴────┘

常见场景 - 计算器：
┌────┬────┬────┬────┐
│ 7  │ 8  │ 9  │ /  │
├────┼────┼────┼────┤
│ 4  │ 5  │ 6  │ *  │
├────┼────┼────┼────┤
│ 1  │ 2  │ 3  │ -  │
├────┼────┼────┼────┤
│ 0  │ .  │ =  │ +  │
└────┴────┴────┴────┘
```

**常用属性**:
- `android:columnCount` - 列数
- `android:rowCount` - 行数
- `android:layout_column` - 子组件所在列
- `android:layout_row` - 子组件所在行

**场景**:
- 应用菜单网格
- 相册展示（多列）
- 计算器按钮布局

---

### 布局5: ConstraintLayout（约束布局）

**特点**: 通过**约束关系**精确控制组件位置，最灵活强大

```
约束的概念：

组件可以通过约束与：
├─ 容器边界相连
├─ 其他组件相连
└─ 参考线相连

例子：
┌──────────────────┐
│ A                │  A的约束：
│                  │  ├─ top与容器顶部距离10dp
│ B        C       │
│                  │  B的约束：
│        D         │  ├─ left与A的left对齐
│                  │  ├─ top与A的bottom距离10dp
└──────────────────┘
                    C的约束：
                    ├─ right与容器right距离10dp
                    ├─ top与A的bottom对齐

                    D的约束：
                    ├─ centerX与容器centerX对齐
                    ├─ top与B的bottom距离20dp
```

**约束类型**:
```
1. 连接约束 - 与其他元素连接
2. 对齐约束 - 与其他元素对齐
3. 分布约束 - 均匀分布
4. 比例约束 - 按比例定位
5. 链约束 - 多个元素形成链
```

**为什么ConstraintLayout最强大**:
- 不需要嵌套多个布局
- 一个ConstraintLayout可以搞定复杂UI
- 性能好（减少嵌套）
- Android Studio有可视化设计工具

**场景**:
- 复杂UI界面
- 响应式设计
- 任何需要精确控制位置的页面

---

## 布局的权重分配

在LinearLayout中，常用`layout_weight`来分配剩余空间：

```
例：三个按钮分别占用1/2, 1/4, 1/4的宽度

┌────────────────────────────────┐
│       按钮1(1/2)  │ 按钮2 │按钮3│
│                   │ (1/4) │(1/4)│
└────────────────────────────────┘

实现方式：
按钮1：layout_weight="2"
按钮2：layout_weight="1"
按钮3：layout_weight="1"
```

---

## 传统布局 vs Jetpack Compose

### 传统布局（XML）

```xml
<!-- res/layout/login_screen.xml -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="邮箱" />

    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="密码" />

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="登录" />

</LinearLayout>
```

**特点**:
- XML声明式
- 需要单独的布局文件
- 需要findViewById来引用组件
- 类型不安全

---

### Compose（现代方式）

```kotlin
@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("邮箱") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { /* 登录 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("登录")
        }
    }
}
```

**特点**:
- Kotlin代码直接定义UI
- 在一个文件中
- 类型安全
- 更易维护和重构

---

## Compose中的常见布局

### 1. Column - 垂直排列
```kotlin
Column {
    Text("项目1")
    Text("项目2")
    Text("项目3")
}
```

等同于传统的 LinearLayout(vertical)

### 2. Row - 水平排列
```kotlin
Row {
    Button(onClick = {}) { Text("按钮1") }
    Button(onClick = {}) { Text("按钮2") }
    Button(onClick = {}) { Text("按钮3") }
}
```

等同于传统的 LinearLayout(horizontal)

### 3. Box - 重叠布局
```kotlin
Box {
    Image(painter = painterResource(R.drawable.bg), contentDescription = null)
    Text("文本覆盖在图片上", color = Color.White)
}
```

等同于传统的 FrameLayout

### 4. LazyColumn - 垂直滚动列表
```kotlin
LazyColumn {
    items(items.size) { index ->
        Text(items[index])
    }
}
```

类似于传统的 RecyclerView + LinearLayout

### 5. LazyRow - 水平滚动列表
```kotlin
LazyRow {
    items(items.size) { index ->
        Card {
            Text(items[index])
        }
    }
}
```

### 6. LazyGrid - 网格布局
```kotlin
LazyVerticalGrid(columns = GridCells.Fixed(3)) {
    items(items.size) { index ->
        Text(items[index])
    }
}
```

等同于传统的 GridLayout

---

## 响应式布局设计

现代Android需要适应不同的屏幕尺寸（手机、平板、折叠屏）

### 传统方式
```
res/layout/
  ├─ activity_main.xml (手机)
  └─ activity_main.xml (平板)

res/layout-w600dp/
  └─ activity_main.xml (平板专用)

在运行时根据屏幕宽度选择不同布局
```

### Compose方式
```kotlin
@Composable
fun ResponsiveScreen() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    if (screenWidth < 600.dp) {
        // 手机布局
        PhoneLayout()
    } else {
        // 平板布局
        TabletLayout()
    }
}
```

更灵活、代码化的做法

---

## 布局性能优化

### 避免过度嵌套

❌ 不好的做法：
```
LinearLayout
  └─ LinearLayout
      └─ LinearLayout
          └─ Button  (3层嵌套，不必要)
```

✅ 好的做法：
```
ConstraintLayout
  └─ Button  (直接放，只需1层)
```

### 使用合适的布局

- **简单线性布局** → LinearLayout（轻量）
- **复杂位置关系** → ConstraintLayout（更优化）
- **重叠效果** → FrameLayout（简单场景）
- **长列表** → RecyclerView/LazyColumn（复用视图）

### Compose的优势

Compose的layout系统天然避免了过度嵌套问题，自动优化了性能。

---

## 常见问题

### Q: 我应该选择哪个布局？
A: 决策树：
```
是否是长列表？
├─ 是 → RecyclerView/LazyColumn
└─ 否 → 继续

是否有复杂的位置关系？
├─ 是 → ConstraintLayout
└─ 否 → 继续

是否需要重叠效果？
├─ 是 → FrameLayout
└─ 否 → 继续

是否是线性排列？
├─ 是 → LinearLayout
└─ 否 → GridLayout
```

### Q: 为什么我的布局看起来变形了？
A: 常见原因：
1. 使用了固定尺寸（dp），没有考虑屏幕适配
2. 嵌套布局权重分配不当
3. 使用了match_parent，但父容器也是match_parent

### Q: Compose和XML布局能混用吗？
A: 可以，但不推荐：
- Compose可以包含XML布局（ComposeView）
- XML可以包含Compose UI（AndroidView）
- 混用会增加复杂度，不如全部使用Compose

### Q: 什么时候使用ConstraintLayout而不是RelativeLayout？
A:
- ConstraintLayout更强大、更灵活
- ConstraintLayout性能更好（减少嵌套）
- RelativeLayout已经被认为是过时的
- 现在推荐：ConstraintLayout或Compose

### Q: 如何在Compose中实现margin和padding？
A:
- Padding = 内边距（组件内部空间）
- Margin = 外边距（组件外部空间）

```kotlin
Column(
    modifier = Modifier.padding(16.dp)  // 内边距
) {
    Text(
        "Hello",
        modifier = Modifier.padding(8.dp)  // 内边距
    )
}
```

外边距通过父容器的spacing实现：
```kotlin
Column(
    verticalArrangement = Arrangement.spacedBy(8.dp)  // 元素间距
) {
    Text("项目1")
    Text("项目2")
}
```

---

## 小结

| 布局 | 特点 | 场景 |
|-----|-----|------|
| **LinearLayout** | 线性排列，简单轻量 | 简单列表、表单 |
| **FrameLayout** | 重叠放置 | 卡片、浮动按钮 |
| **RelativeLayout** | 相对定位（过时） | 不推荐使用 |
| **GridLayout** | 网格排列 | 相册、菜单网格 |
| **ConstraintLayout** | 约束定位，最强大 | 复杂UI、响应式 |
| **Column(Compose)** | 垂直排列（现代） | Compose项目 |
| **Row(Compose)** | 水平排列（现代） | Compose项目 |
| **Box(Compose)** | 重叠布局（现代） | Compose项目 |

## 参考资源

- [Android布局官方文档](https://developer.android.com/guide/topics/ui/declaring-layout)
- [ConstraintLayout完整指南](https://developer.android.com/training/constraint-layout)
- [Jetpack Compose布局](https://developer.android.com/jetpack/compose/layouts)
- [响应式设计最佳实践](https://developer.android.com/guide/topics/resources/providing-resources)

## 相关卡片

- [01_Jetpack_Compose基础.md](01_Jetpack_Compose基础.md) - Compose编程基础
- [03_Activity和应用程序结构.md](03_Activity和应用程序结构.md) - Activity与UI的关系

---

**关键要点总结**:

1. **布局是容器** - 用来组织和定位UI组件
2. **5大传统布局** - Linear/Frame/Relative/Grid/Constraint
3. **选择布局** - 根据场景选择，ConstraintLayout最强大
4. **Compose改变了方式** - 用Kotlin代码代替XML，更简洁高效
5. **你的项目用Compose** - Column/Row/Box就能搞定大多数场景
6. **响应式设计很重要** - 考虑不同屏幕尺寸
