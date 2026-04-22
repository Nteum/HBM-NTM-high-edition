# HBM-NTM High Edition-CN

基于 **Minecraft Forge 1.20.1** 的 HBM / NTM 高版本移植与扩展工程。  
本仓库的目标不是做一个“名字像 HBM”的新模组，而是尽可能把 **旧版 HBM’s Nuclear Tech Mod / NTM 的内容、术语、玩法和视觉表现** 迁移到现代 Forge 环境中，并在保证可维护性的前提下逐步补齐缺失系统。

---

## 项目目标

本项目的核心目标有四个：

1. **旧版内容移植**
   - 将旧版 HBM / NTM 的方块、物品、机器、反应堆、武器、爆炸效果等移植到 `1.20.1`。
2. **玩法语义保持**
   - 尽量保持旧版术语、用途、交互方式、控制逻辑与 GUI 语义。
3. **资源表现对齐**
   - 模型、贴图、语言、配方、掉落表、创造标签页、JEI 展示尽量对齐旧版与 MC百科认知。
4. **现代工程化维护**
   - 使用 Forge 1.20.1、Java 17+、Gradle、数据生成、现代注册体系，保证后续继续扩展和修复时可维护。

---

## 当前状态

当前仓库处于 **持续移植中**，不是最终稳定版。  
已经具备以下基础：

- Forge `1.20.1`
- Java `17+`
- JEI 集成
- 大量 HBM 物品、方块、机器已完成初步注册与资源接入
- 旧版配方、模型、语言、资源正在分批迁移
- RBMK 体系、研究反应堆体系、部分大型机器正在重点修复中

当前版本号来自 `gradle.properties`：

- `mod_id`: `hbm`
- `mod_name`: `HBM-NTM High Edition-CN`
- `mod_version`: `1.5.0b0405-alpha-Build1001`

注意：  
这是开发仓库，不保证所有内容都已经“完全可玩”。如果你看到紫黑贴图、占位模型、错位 GUI、旧版占位菜单，说明对应部分仍在修复中。

---

## 技术栈

- **Minecraft**: `1.20.1`
- **Forge**: `47.2.32`
- **Java**: `17+`
- **Gradle**: `8.x`
- **ForgeGradle**: `6.0.25`
- **JEI**: 已接入开发依赖
- **Mixin**: 已启用

---

## 开发环境要求

### 必要环境
- JDK 17+
- Gradle Wrapper（仓库自带）
- 能正常下载 Forge / Mojang / Modrinth / Maven 依赖的网络环境

### 推荐环境
- IntelliJ IDEA、VS Code + Minecraft dev类插件
- Windows/macOS/Linux
- 至少 `4 GB` JVM 构建内存
- 本地单独的 Gradle 用户目录，避免污染全局缓存

---

## 快速开始

### 1. 克隆仓库
```bash
git clone https://github.com/Nteum/HBM-NTM-high-edition.git
cd HBM-NTM-high-edition
```

### 2. 编译
```bash
GRADLE_USER_HOME="$PWD/.gradle-home" ./gradlew --no-daemon compileJava
```

### 3. 启动开发客户端
```bash
GRADLE_USER_HOME="$PWD/.gradle-home" ./gradlew --no-daemon runClient
```

### 4. 运行测试
```bash
GRADLE_USER_HOME="$PWD/.gradle-home" ./gradlew --no-daemon test --offline
```

### 5. 完整构建
```bash
GRADLE_USER_HOME="$PWD/.gradle-home" ./gradlew --no-daemon build
```

### 6. 运行数据生成
```bash
GRADLE_USER_HOME="$PWD/.gradle-home" ./gradlew --no-daemon runData
```

---

## 常用命令

### 清理并构建
```bash
./gradlew --no-daemon clean assemble
```

### 只编译 Java
```bash
GRADLE_USER_HOME="$PWD/.gradle-home" ./gradlew --no-daemon compileJava
```

### 启动客户端
```bash
GRADLE_USER_HOME="$PWD/.gradle-home" ./gradlew --no-daemon runClient
```

### 执行测试
```bash
GRADLE_USER_HOME="$PWD/.gradle-home" ./gradlew --no-daemon test --offline
```

### 生成数据
```bash
GRADLE_USER_HOME="$PWD/.gradle-home" ./gradlew --no-daemon runData
```

---

## 目录结构

```text
HBM-NTM-high-edition/
├── build.gradle
├── gradle.properties
├── settings.gradle
├── README.md
├── AGENTS.md
├── TODO.md
├── docs/
├── reports/
├── tools/
├── run/
├── run-data/
├── src/
│   ├── main/
│   │   ├── java/com/hbm/
│   │   └── resources/
│   │       ├── assets/hbm/
│   │       └── META-INF/
│   ├── generated/
│   │   └── resources/
│   └── test/
│       └── java/
└── gradle/
```

### 重点说明

#### `src/main/java/com/hbm`
主游戏逻辑代码：
- 方块
- 方块实体
- 菜单与 GUI
- 渲染器
- 网络包
- 注册表
- 兼容层
- 反应堆系统
- 爆炸与特效系统

#### `src/main/resources/assets/hbm`
手写资源：
- blockstates
- models
- textures
- lang
- shaders / GUI 贴图等

#### `src/generated/resources`
数据生成输出目录。  
**不要把手写资源长期放在这里。**  
这个目录适合：
- 配方
- 掉落表
- tags
- 自动生成的 blockstates / models / item models

#### `tools/`
项目辅助脚本目录。  
用于：
- 旧版内容审计
- 缺失资源检查
- 报告生成
- 迁移辅助脚本

#### `reports/`
脚本或审计结果输出目录。  
例如：
- 旧版配方导入报告
- 丢失模型 / 贴图检测报告
- RBMK 组件矩阵报告

---

## 旧版移植基线

本仓库默认以以下旧版仓库为主要移植基线：

```text
Hbm-s-Nuclear-Tech-GIT
```

也就是：

```text
bobcat的Hbm-s-Nuclear-Tech-GIT，1.7.10原版
```

迁移、修复、行为对齐时，优先参考这份旧版源码中的：

- 旧版方块类
- 旧版方块实体类
- 旧版 GUI
- 旧版渲染逻辑
- 旧版模型与贴图命名
- 旧版配方与物品体系

---

## 参考基线

### 1. 旧版源码
- `Hbm-s-Nuclear-Tech-GIT`

### 2. MC百科
本项目默认把 MC百科作为玩家可见层面的重要基线，用于统一：
- 名称
- 术语
- 用法说明
- 玩法认知
- 分类结构

典型参考：
- [HBM 物品总表](https://www.mcmod.cn/item/list/513-1.html)
- [RBMK 控制台](https://www.mcmod.cn/item/453237.html)

### 3. 当前 1.20.1 代码结构
旧版行为不能直接照搬；需要适配现代：
- Forge 注册体系
- 能力系统
- 菜单同步
- 渲染管线
- 数据生成
- 资源组织方式

---

## 资源与数据约定

### 手写资源放这里
- `src/main/resources/assets/hbm/...`

### 自动生成资源放这里
- `src/generated/resources/...`

### 禁止混用
- 不要把长期维护的贴图、OBJ、GUI 资源只放在 `generated`
- 不要手工修改会被 datagen 覆盖的生成文件，除非你明确知道该文件不再由脚本覆盖

### 资源修复原则
对于移植内容，不能只检查“文件是否存在”，还要检查：

1. `blockstate` 属性是否和代码一致
2. `model` 路径是否真实存在
3. `texture` 路径是否真实存在
4. `item model` 是否使用了正确 parent
5. GUI 图标和世界模型是否语义一致
6. 粒子贴图 `particle` 是否存在
7. 多方块 / 大模型是否有正确显示变换与点击盒

---

## 代码风格

- 最好为Java 17
- 四空格缩进
- K&R 大括号风格
- 类名使用 `UpperCamelCase`
- 字段 / 方法使用 `lowerCamelCase`
- 常量使用 `SCREAMING_SNAKE_CASE`

### 包组织
- 方块：`com.hbm.block`
- 方块实体：`com.hbm.blockentity`
- 菜单：`com.hbm.gui.menu`
- 屏幕：`com.hbm.gui.screen`
- 注册表：`com.hbm.registries`
- 渲染器：`com.hbm.render`
- 反应堆：`com.hbm.reactor`
- 网络：`com.hbm.network`
- 兼容层：`com.hbm.compat`

---

## 测试约定

- 使用 `JUnit 4.13.2`
- 测试目录：
```text
src/test/java
```

### 要求
- 测试不应依赖 Forge 运行时
- 需要对 Minecraft 依赖做 mock / stub
- 修改配方导入、能力同步、注册逻辑、工具脚本时，应尽量补对应测试

---

## 当前重点开发方向

### 1. RBMK 体系
当前 RBMK 相关是重点修复区域，包括但不限于：

- 控制台 GUI
- 自动装填机 GUI
- 燃料通道 / 控制棒通道 GUI
- 多方块 footprint
- 点击盒 / 选择盒
- 显示面板 / 图表 / 数码管 / 键盘 / 仪表
- DODD 参数显示
- 模型、贴图、blockstate 对齐
- 面板安装与控制台联动
- 控制逻辑与 AZ-5
- 反应堆最小可玩闭环

### 2. 研究反应堆
- GUI 对齐
- 模型修复
- 交互与输入逻辑
- 缺失贴图、物品图标、配方补齐

### 3. 大型机器与外设
- 冷凝塔
- 冷凝器
- 机器 OBJ 模型缩放与 GUI 图标
- 多方块代理与命中逻辑

### 4. 旧版物品 / 方块搬运
- 模型
- 贴图
- 语言
- 合成配方
- 掉落表
- 创造栏归类
- JEI 展示

---

## 已有辅助脚本

仓库里已有 `tools/` 与 `reports/` 体系，用于做这类事情：

- 检查旧版未移植物品
- 审计缺失模型 / 贴图
- 输出 JSON 报告
- 生成对比清单

后续建议统一把“是否缺失”的定义扩展为：

- 文件不存在
- 属性不匹配导致运行时 missing model
- 图标 parent 错误
- 模型尺寸 / GUI 显示异常
- 世界模型与交互盒严重不一致
- 运行时 fallback 到占位 GUI

---

## 已知问题

以下问题在开发期间是正常现象，但不代表可以长期保留：

- 紫黑贴图
- 黑块占位
- 错误的 item model parent
- 多方块点击盒漂移
- GUI 打开到旧版占位菜单
- 面板显示面与点击面不一致
- 某些方块只完成注册，尚未完成玩法闭环
- 某些 legacy 配方仍引用旧 ID
- 一些脚本目前只能做到“发现问题”，还不能做到“完整自动修复”

---

## 开发注意事项

### 1. `run/` 不要提交
开发运行目录 `run/` 只用于本地 dev client。

### 2. `.gradle-home` 不要提交
建议本地单独使用：
```bash
GRADLE_USER_HOME="$PWD/.gradle-home"
```

### 3. 代理配置
`gradle.properties` 曾支持本地代理配置。  
如果你无法下载依赖，先检查：
- 代理是否开启
- 地址是否可用
- 是否需要关闭本地回环代理配置

### 4. 版本号会自动递增
当前 `build.gradle` 带有版本自增逻辑。  
运行以下任务时可能会自动更新 `gradle.properties` 中的 `mod_version`：

- `compileJava`
- `build`
- `runClient`
- `runData`

如果你不希望版本号变化，请先看清构建脚本逻辑再执行。

---

## 提交规范

建议提交信息使用短句、动作导向，可中英混合，例如：

```text
fix: 修复 RBMK 控制台点击盒
feat: 补充自动装填机独立 GUI
refactor: 重构 RBMK 列状态分类
chore: 清理 legacy 配方别名
```

PR / 提交说明中建议包含：

- 改动范围
- 是否影响玩法
- 是否影响资源
- 是否需要重新生成数据
- 是否经过 `compileJava` / `test` / `runClient` 验证
- 如果是视觉修复，附截图

---

## 许可证

项目当前在 `gradle.properties` 中声明：

```text
LGPL
```

请同时结合仓库中的 `LICENSE` 文件理解具体条款。

---

## 给开发者的直接建议

如果你要继续往这个仓库里做移植，优先遵循下面三条：

1. **先对照旧版源码，再写现代实现**
    - 不要只看 MC百科截图复刻外观；行为和 GUI 语义必须看旧版源码。
2. **先修闭环，再补细节**
    - 先保证“能放、能开、能用、能炸”，再处理装饰细节。
3. **不要把“文件存在”当成“已经移植完成”**
    - 运行时 blockstate、model、texture、GUI、shape、menu、renderer、NBT、network 只要有一环错了，就仍然算未完成。

---

## 维护者说明

如果你正在直接维护这个仓库，建议每次改完大型系统后至少做下面这组验证：

```bash
GRADLE_USER_HOME="$PWD/.gradle-home" ./gradlew --no-daemon compileJava
GRADLE_USER_HOME="$PWD/.gradle-home" ./gradlew --no-daemon test --offline
GRADLE_USER_HOME="$PWD/.gradle-home" ./gradlew --no-daemon runClient
```

进游戏后至少检查：

- 创造栏图标是否正确
- 方块世界模型是否正常
- 点击盒是否贴合模型
- GUI 是否打开到正确菜单
- DODD 是否显示正确对象类型
- 退出世界时是否正常保存

---

## 致用法说明的最终定义

这个项目的完成标准不是“能编译”，也不是“资源文件都在”。  
真正的完成标准是：

- 模型正确
- 贴图正确
- 图标正确
- GUI 正确
- 点击盒正确
- 配方正确
- 语言正确
- 玩法正确
- 创造可测试
- 生存可使用
- 运行可保存
- 爆炸可闭环

只满足其中一部分，不算完成。
