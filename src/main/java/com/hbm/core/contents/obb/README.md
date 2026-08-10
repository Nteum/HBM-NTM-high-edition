# OBB Collision Module for Minecraft Forge 1.20.1

基于 SuperbWarfare（卓越前线）模组 OBB 碰撞系统的纯 Java 独立模块。

## 概述

本模块实现了 Minecraft 中的 **OBB (Oriented Bounding Box，有向包围盒)** 碰撞检测系统，
允许实体拥有任意旋转的非轴对齐碰撞箱，解决原版只支持 AABB 的局限性。

适用于需要精确碰撞检测的复杂形状实体，如飞机、坦克、轮船等载具。

## 系统架构

```
com.example.obb/
├── OBB.java                    # 核心 OBB 数据类 + 碰撞检测 + 射线相交
├── OBBEntity.java              # OBB 实体接口
├── OBBHitter.java              # 命中部件追踪接口
├── OBBInfo.java                # OBB 配置数据（JSON 反序列化）
├── OBBPhysicsUtils.java        # 物理求解（世界碰撞、实体碰撞）
├── OBBRayTrace.java            # 射线检测工具（OBB 精确命中）
├── renderer/
│   └── OBBRenderer.java        # F3+B 调试碰撞箱渲染
└── mixin/
    ├── EntityMixin.java               # 为 Entity 注入 OBBHitter
    ├── LevelMixin.java                # getEntities 补丁（包含 OBB 实体）
    ├── ProjectileUtilMixin.java       # OBB 射线检测覆写
    └── EntityRenderDispatcherMixin.java # OBB 调试渲染注入
```

## 核心算法

### 1. 分离轴定理 (SAT) 碰撞检测

- **OBB vs OBB**: 15 轴 SAT 测试（3+3 面法线 + 9 边叉积），委托给 JOML `Intersectiond.testObOb`
- **OBB vs AABB**: 等价于 OBB vs OBB（AABB 旋转=单位矩阵）
- **MTV (最小平移向量)**: 完整 15 轴 SAT + 标量叉积展开实现，免分配

### 2. 射线-OBB 相交 (Slab Method)

将射线变换到 OBB 本地坐标，与轴对齐平板 `[-extents, +extents]` 求交，再变换回世界空间。

### 3. 物理碰撞求解

Y → X → Z 分轴 SAT 裁剪：
- 每轴独立将 OBB 平移后与候选方块的 AABB 做 SAT 测试
- 根据 MTV 法线方向区分地面接触 vs 墙壁碰撞
- 地面接触：严格容差；墙壁碰撞：宽松容差

### 4. 实体-OBB 交互

实体陷入 OBB → 沿 MTV 推出（4 次迭代选最深穿透）
实体速度会导致穿入 → 截断速度分量

## 性能优化

| 优化项 | 说明 |
|--------|------|
| ThreadLocal 轴缓冲区 | `AXES_A` / `AXES_B` 消除 SAT 每帧数组分配 |
| 标量叉积展开 | MTV 计算中避免 JOML `Vector3d.cross()` 堆分配 |
| 免分配 API | `getAxesInto(out[])` 就地覆写预分配缓冲 |
| 轴缓存复用 | 推导一次，Y/X/Z 三通道复用 |
| 粗筛跳过 | XZ/YZ/XY 无重叠的 AABB 直接跳过 SAT |
| 高度图快速路径 | `isSearchBoxProvablyAirborne` 提前排除悬空区域 |

## 依赖

- **Minecraft Forge 1.20.1**
- **JOML** (org.joml) — 向量/四元数/矩阵运算
- **Mixin** (org.spongepowered.asm.mixin) — 字节码注入

## 集成方式

### 1. 添加 Mixin 配置

在 `mods.toml` 或主 mod 类中注册：

```java
@Mod("your_mod_id")
public class YourMod {
    public YourMod() {
        // Mixin 自动通过 mixins.json 加载
    }
}
```

在 `META-INF/mods.toml` 中添加 Mixin 配置引用。

### 2. 实现 OBBEntity 接口

```java
public class MyVehicleEntity extends Entity implements OBBEntity {
    private final List<OBB> obbs = new ArrayList<>();

    public MyVehicleEntity(EntityType<?> type, Level level) {
        super(type, level);
        // 从配置初始化 OBB
        obbs.add(new OBB(
            new Vector3d(0, 0, 0),
            new Vector3d(2, 1, 5),
            new Quaterniond(),
            OBB.Part.BODY
        ));
    }

    @Override
    public List<OBB> getOBBs() { return obbs; }

    @Override
    public void tick() {
        super.tick();
        // 每 tick 更新 OBB 位置和朝向
        for (OBB obb : obbs) {
            obb.setCenter(new Vector3d(getX(), getY(), getZ()));
            obb.updateRotation(/* 实体旋转四元数 */);
        }
    }
}
```

### 3. 实现碰撞检测

```java
// 在实体的 travel() 或 tick() 中
public void travel() {
    // OBB vs 世界碰撞求解
    Vec3 movement = getDeltaMovement();
    OBB collisionObb = /* 获取碰撞 OBB */;
    Vec3 corrected = OBBPhysicsUtils.resolveObbWorldCollision(level(), collisionObb, movement);
    setDeltaMovement(corrected);

    // 实体支撑
    OBBPhysicsUtils.handleEntityObbCollision(getOBBs(), otherEntity);
}
```

### 4. 使用 OBB 部件伤害

```java
// 在 hurt() 方法中读取命中部件
if (source.getDirectEntity() instanceof Projectile projectile) {
    OBBHitter hitter = OBBHitter.getInstance(projectile);
    OBB.Part part = hitter.obb$getCurrentHitPart();

    switch (part) {
        case TURRET -> turretHealth -= amount;
        case WHEEL_LEFT -> leftWheelHealth -= amount;
        // ...
    }
}
```

## 文件清单

| 文件 | 行数 | 说明 |
|------|------|------|
| OBB.java | ~500 | 核心类：OBB 数据、SAT 碰撞、MTV、射线相交、AABB 辅助 |
| OBBEntity.java | ~85 | OBB 实体接口 |
| OBBHitter.java | ~45 | 命中部件追踪接口 |
| OBBInfo.java | ~125 | 配置数据类 |
| OBBPhysicsUtils.java | ~340 | 物理碰撞求解 |
| OBBRayTrace.java | ~225 | 射线检测工具 |
| OBBRenderer.java | ~105 | F3+B 渲染 |
| EntityMixin.java | ~45 | Entity OBBHitter 注入 |
| LevelMixin.java | ~60 | getEntities 补丁 |
| ProjectileUtilMixin.java | ~175 | 投射物 OBB 射线检测 |
| EntityRenderDispatcherMixin.java | ~55 | 调试渲染注入 |

## OBB.Part 枚举说明

| 值 | 用途 | 参与射线命中? |
|----|------|:---:|
| EMPTY | 空/未定义 | ❌ |
| WHEEL_LEFT | 左轮 | ✅ |
| WHEEL_RIGHT | 右轮 | ✅ |
| TURRET | 炮塔 | ✅ |
| MAIN_ENGINE | 主引擎 | ✅ |
| SUB_ENGINE | 副引擎 | ✅ |
| BODY | 车身 | ✅ |
| INTERACTIVE | 可交互区域（车门等） | ✅ |
| COLLISION | 碰撞专用（仅参与物理，不参与射线） | ❌ |

## 参考来源

- SuperbWarfare (卓越前线) by atsuishio — OBB 原始 Kotlin 实现
- HitboxAPI by AnECanSaiTin — OBB 渲染参考
- JOML — 高效 Java 线性代数库
