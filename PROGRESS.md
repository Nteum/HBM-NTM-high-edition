# HBM 1.7.10 → 1.20.1 移植进度

> 项目: `E:\game\MineCraftModDevelop\HBM-forge`
> 参考: `E:\LEARNING\code\Minecraft\reference\Hbm-s-Nuclear-Tech-GIT-space`

---

## 第 1 批：天体 & 太阳系 & Trait 系统（2026-08-11）

### 已移植文件：36 个

#### 天体力学核心 (`com.hbm.space.dim`)

| 文件 | 行数 | 状态 | 说明 |
|---|---|---|---|
| `CelestialBody.java` | ~440 | ✅ | 天体轨道力学：卫星树结构、轨道参数、潮汐锁定、Terraforming静态方法、重力/自转/公转计算、trait读写、大气化学(emitGas/consumeGas/reactAtmosphere)。`int dimensionId` → `ResourceKey<Level> dimension` |
| `SolarSystem.java` | ~1054 | ✅ | Kerbol太阳系全部天体定义(28个)、Body枚举(11个星球)、AstroMetric/OrreryMetric天体度量、Hohmann转移ΔV计算、天体位置/相位角/视大小递归计算。Vec3/Mth已全部1.20.1化 |
| `SolarSystemWorldSavedData.java` | ~240 | ✅ | 天体trait持久化 (`WorldSavedData` → `SavedData`)、轨道站数据管理、客户端trait同步缓存。`ChunkCoordIntPair` → `ChunkPos` |
| `SpaceConfig.java` | ~30 | ✅ | 维度Key常量 (`SpaceConfig.orbitDimension` 等，引用 `HBMDimensions`) |

#### 天体特性 (`com.hbm.space.dim.trait`)

| 文件 | 行数 | 状态 | 说明 |
|---|---|---|---|
| `CelestialBodyTrait.java` | ~65 | ✅ | Trait基类 + 注册表 (BiMap) + 13种trait注册。`NBTTagCompound` → `CompoundTag`，`ByteBuf` → `FriendlyByteBuf` |
| `CBT_Atmosphere.java` | ~195 | ✅ | 大气层成分(多流体分压)、气体消耗/排放、分压排序、流体颜色、NBT/网络序列化 |
| `CBT_Water.java` | ~50 | ✅ | 水体特性(可非水流体) |
| `CBT_Temperature.java` | ~40 | ✅ | 表面温度(Celsius) |
| `CBT_Bees.java` | ~40 | ✅ | 蜜蜂数量 |
| `CBT_Destroyed.java` | ~60 | ✅ | 行星毁灭特效(客户端插值动画) |
| `CBT_Dyson.java` | ~140 | ✅ | 戴森球蜂群管理(多蜂群ID、成员衰减、消费计数) |
| `CBT_Impact.java` | ~40 | ✅ | 撞击时间戳 |
| `CBT_Lights.java` | ~65 | ✅ | 光污染/文明等级 (`Block.getLightValue()` → `BlockState.getLightEmission()`) |
| `CBT_War.java` | ~340 | ✅ | 星际战争(生命/护盾/弹道)、Projectile更新循环、NBT+网络序列化 |
| `CBT_Weather.java` | ~345 | ✅ | 天气系统(雨/雷/闪电分级)、`MathHelper` → `Mth`、`DimensionManager.getWorld` → `server.getLevel` |
| `CBT_Invasion.java` | ~400 | ⚠️ | 外星入侵波次系统(4波+Boss)。Trait逻辑完整，实体生成代码注释(TODO-entity) |
| `CBT_Compromised.java` | ~8 | ✅ | 纯标记Trait |

#### 轨道/航天器 (`com.hbm.space.dim.orbit`)

| 文件 | 行数 | 状态 | 说明 |
|---|---|---|---|
| `OrbitalStation.java` | ~100 | ⚠️ | 轨道站STUB：数据类定义(StationState枚举/字段)达标，`getStationFromPosition`返回null待完整移植 |

#### 工具 (`com.hbm.space.util`)

| 文件 | 行数 | 状态 | 说明 |
|---|---|---|---|
| `AstronomyUtil.java` | ~35 | ✅ | 天体物理常数 (G、KM_IN_AU、MB_PER_ATM等) |

### 本批次待完成 TODO 清单

#### 零依赖（可直接实现的TODO）

| # | 文件 | TODO | 说明 |
|---|---|---|---|
| - | (无) | | 本批次没有零依赖TODO |

#### 依赖已移植类（跨本批次的TODO）

| # | 文件 | TODO | 所需依赖 | 说明 |
|---|---|---|---|---|
| T1 | `SolarSystem.java:1054` | `TileEntityDysonReceiver.runTests()` | TileEntityDysonReceiver(后置) | Dyson球接收器的ΔV测试验证 |

#### 依赖未移植类（等待后续批次的TODO）

| # | 文件:行号 | TODO | 所需依赖 |
|---|---|---|---|
| — | `CBT_Invasion.java` | 实体生成 (`spawnCattle/spawnAttempt/spawnBoss`) | `EntityUFO`, `EntitySiegeCraft/UFO`, `EntityCombatDropPod`, `EntityGlyphid*` 系列, `WorldProviderCelestial` |
| — | `CBT_Invasion.java` | `onKill`实体类型判断 | `EntitySiegeUFO/Craft`, `EntityUFO` |
| — | `CBT_Invasion.java` | 客户端告警音效/文本 | `MainRegistry.proxy`(Proxy系统已废弃→需网络包替代) |

---

## 第 2 批：SavedData 数据存储（2026-08-11）

### 已移植文件：15 个

#### 核心SavedData (`com.hbm.saveddata`)

| 文件 | 行数 | 状态 | 说明 |
|---|---|---|---|
| `TomSaveData.java` | ~90 | ✅ | 撞击事件持久化(dust/fire/impact/time/坐标)。`WorldSavedData` → `SavedData` + `computeIfAbsent(load, factory, name)` |
| `AnnihilatorSavedData.java` | ~220 | ⚠️ | 湮灭器合成池。核心BigInteger累加逻辑完整，`Item.itemRegistry` → `BuiltInRegistries.ITEM`。Payout计算注释(TODO-recipe) |
| `SatelliteSavedData.java` | ~178 | ✅ | 卫星频率注册表持久化、位置感知查找(轨道站重定向)、跨天体搜索、客户端缓存。`@SideOnly(Side.CLIENT)` → `@OnlyIn(Dist.CLIENT)`、`dimensionId(int)` → `ResourceKey<Level>` |

#### 卫星类 (`com.hbm.saveddata.satellites`)

| 文件 | 行数 | 状态 | 说明 |
|---|---|---|---|
| `Satellite.java` | ~370 | ⚠️ | 卫星基类：注册系统、ItemStack NBT配置(`stackTagCompound` → `getTag()/setTag()`)、轨道投放、NBT序列化、`ByteBuf` → `FriendlyByteBuf`。渲染方法注释(TODO-render)。`BufferUtil` → `buf.writeUtf/readUtf` |
| `SatelliteMapper.java` | ~10 | ✅ | 测绘卫星 (HAS_MAP + SAT_PANEL) |
| `SatelliteScanner.java` | ~10 | ✅ | 矿物扫描卫星 (HAS_ORES + SAT_PANEL) |
| `SatelliteRadar.java` | ~12 | ✅ | 雷达卫星 (HAS_MAP + HAS_RADAR + SAT_PANEL) |
| `SatelliteLaser.java` | ~42 | ⚠️ | 激光炮卫星。`onClick`框架完整，`world.getHeightValue(x,z)` → `getHeight(Heightmap.Types.WORLD_SURFACE, x, z)`。EntityDeathBlast生成注释(TODO-entity) |
| `SatelliteResonator.java` | ~30 | ✅ | 共振传送卫星。`EntityPlayerMP.setPlayerLocation` → `connection.teleport()`，`mountEntity(null)` → `stopRiding()`，`playSoundEffect` → `playSound(SoundEvents.ENDERMAN_TELEPORT)` |
| `SatelliteFoeq.java` | ~20 | ⚠️ | FOEQ卫星。成就触发注释(TODO-achievement) |
| `SatelliteHorizons.java` | ~63 | ⚠️ | Horizons卫星。成就触发注释(TODO-achievement)，EntityTom生成注释(TODO-entity)，`sendChatMsg` → `broadcastSystemMessage(Component.literal(...))` |
| `SatelliteMiner.java` | ~68 | ⚠️ | 采矿卫星基类。Cargo池注册框架完整，`ItemPoolsSatellite`引用注释(TODO-itempool) |
| `SatelliteLunarMiner.java` | ~9 | ⚠️ | 月球采矿卫星。Cargo注册注释(TODO-itempool) |
| `SatelliteDysonRelay.java` | ~8 | ✅ | 戴森球中继卫星(纯标记类) |
| `SatelliteWar.java` | ~67 | ⚠️ | 战争卫星基类。`ByteBuf` → `FriendlyByteBuf`，客户端渲染+sound注释(TODO-render) |
| `SatelliteRailgun.java` | ~121 | ⚠️ | 轨道炮卫星。核心开火/目标/弹道逻辑完整，客户端渲染注释(TODO-render) |

### 本批次待完成 TODO 清单

#### 零依赖（可直接实现的TODO）

| # | 文件 | TODO | 说明 |
|---|---|---|---|
| - | (无) | | |

#### 依赖未移植类（等待后续批次的TODO）

| # | 文件:行号 | TODO | 所需依赖 | 类别 |
|---|---|---|---|---|
| T-entity-1 | `SatelliteLaser.java:36` | `EntityDeathBlast` 死亡射线实体 | `com.hbm.entity.logic.EntityDeathBlast` | entity |
| T-entity-2 | `SatelliteHorizons.java:46` | `EntityTom` TOM导弹实体 | `com.hbm.entity.projectile.EntityTom` | entity |
| T-achievement-1 | `SatelliteFoeq.java:14` | `MainRegistry.achFOEQ` 成就 | `com.hbm.main.MainRegistry` | main |
| T-achievement-2 | `SatelliteHorizons.java:22` | `MainRegistry.horizonsStart` 成就 | `com.hbm.main.MainRegistry` | main |
| T-achievement-3 | `SatelliteHorizons.java:52` | `MainRegistry.horizonsEnd` 成就 | `com.hbm.main.MainRegistry` | main |
| T-itempool-1 | `SatelliteMiner.java:60` | `ItemPoolsSatellite.POOL_SAT_MINER` | `com.hbm.itempool.ItemPoolsSatellite` | itempool |
| T-itempool-2 | `SatelliteLunarMiner.java:6` | `ItemPoolsSatellite.POOL_SAT_LUNAR` | `com.hbm.itempool.ItemPoolsSatellite` | itempool |
| T-satitem-1 | `Satellite.java:95` | `ModItems.sat_mapper/scanner/radar/...` 卫星物品注册 | `com.hbm.items.ModItems` | item |
| T-recipe-1 | `AnnihilatorSavedData.java:144,149` | `AnnihilatorRecipes.getHighestPayoutFromKey` | `com.hbm.inventory.recipes.AnnihilatorRecipes` | recipe |
| T-recipe-2 | `AnnihilatorSavedData.java:112` | `ItemStackUtil.getOreDictNames` → tag迁移 | `com.hbm.util.ItemStackUtil`(需tag改造) | recipe |
| T-fluid-1 | `AnnihilatorSavedData.java` | `FluidType`, `Fluids.fromName()` | `com.hbm.inventory.fluid.FluidType/Fluids` | fluid |
| T-render-1 | `Satellite.java:358` | `Satellite.render()` + `renderDefault()` GL11渲染 | 整个render管线 | render |
| T-render-2 | `SatelliteWar.java:49` | `playsound()` + `getModel()` | `Minecraft.getInstance()`, IModelCustom | render |
| T-render-3 | `SatelliteRailgun.java:100` | `render()` GL11/BeamPronter/ResourceManager | 整个render管线 | render |
| T-main-1 | `CBT_Invasion.java` 多处 | 实体生成(UFO/Glyphid/DropPod) | 见上表 | entity |
| T-main-2 | `CBT_Invasion.java` 多处 | 客户端告警(`MainRegistry.proxy`) | 需网络包替代旧Proxy模式 | network |
| T-dyson-1 | `SolarSystem.java:1054` | `TileEntityDysonReceiver.runTests()` | TileEntityDysonReceiver | tile |

### 按依赖类别汇总

| 依赖类别 | TODO 数量 | 说明 |
|---|---|---|
| **entity** (实体) | 5+ | EntityDeathBlast, EntityTom, EntityUFO, EntitySiegeCraft/UFO, EntityGlyphid*, EntityCombatDropPod |
| **render** (渲染) | 3 | Satellite渲染管线 (GL11 → RenderSystem/PoseStack) |
| **recipe** (配方) | 3 | AnnihilatorRecipes, ComparableStack, ItemStackUtil(tag迁移) |
| **fluid** (流体) | 1 | FluidType + Fluids 常量类 |
| **item** (物品) | 2 | ModItems 卫星物品, ItemPoolsSatellite |
| **main** (主类) | 3 | MainRegistry 成就 |
| **tile** (方块实体) | 1 | TileEntityDysonReceiver |
| **network** (网络) | 1 | CBT_Invasion客户端告警(旧Proxy模式 → 网络包) |

---

## 全局未移植依赖（阻塞多个批次）

| 依赖 | 被哪些文件引用 | 优先级 |
|---|---|---|
| `com.hbm.inventory.fluid.FluidType` / `Fluids` | AnnihilatorSavedData, CBT_*, CelestialBody | 🔴 高 |
| `com.hbm.inventory.FluidStack` | CelestialBody | 🔴 高 |
| `com.hbm.items.ModItems` | Satellite.register() | 🟡 中 |
| `com.hbm.main.MainRegistry` | SatelliteFoeq, SatelliteHorizons | 🟡 中 |
| `com.hbm.entity.logic.EntityDeathBlast` | SatelliteLaser | 🟢 低 |
| `com.hbm.entity.projectile.EntityTom` | SatelliteHorizons | 🟢 低 |
| `com.hbm.render.*` (Shader, BeamPronter, ResourceManager) | Satellite*, SatelliteRailgun | 🟢 低 |
| `com.hbm.inventory.recipes.AnnihilatorRecipes` | AnnihilatorSavedData | 🟡 中 |
| `com.hbm.tileentity.machine.TileEntityDysonReceiver` | SolarSystem.runTests() | 🟢 低 |

---

## 统计

| 类别 | 数量 |
|---|---|
| 已移植文件总数 | **51** |
| 完全可用 (✅) | **24** |
| 部分可用 (⚠️, 有 TODO) | **27** |
| TODO 总数 | **21** |
| 其中：零依赖可立即实现 | **0** |
| 其中：等待其他模块移植 | **21** |

---

## 第 3 批：维度渲染 Effects + 时间 + 陨石 + 天空渲染器（2026-08-12）

### 已移植文件：5 个

| 文件 | 行数 | 状态 | 说明 |
|---|---|---|---|
| `CelestialBodyWorldSavedData.java` | ~65 | ✅ | 维度本地时间持久化 (`WorldSavedData` → `SavedData`) |
| `CelestialTimeProvider.java` | ~180 | ✅ | 时间计算静态工具（日行角度/日长/月相/日食/亮度） |
| `CelestialMeteor.java` | ~100 | ✅ | 客户端陨石动画（`Meteor` 内部类 + 列表） |
| `CelestialDimensionEffects.java` | ~250 | ✅ | `extends DimensionSpecialEffects`：雾色/日落/云高/地平线 |
| `CelestialSkyRenderer.java` | ~675 | ⚠️ | 天空渲染主循环。基本渲染完成，9个shader方法待shader JSON |

### 架构决策

| 1.7.10 原始 | 1.20.1 目标 |
|---|---|
| `WorldProviderCelestial` (1025行) | → `CelestialDimensionEffects` + `CelestialTimeProvider` + `CelestialMeteor` |
| `SkyProviderCelestial.render()` (~500行) | → `CelestialSkyRenderer.renderSky()` |
| `Meteor` 内部类 | → `CelestialMeteor` 独立类 |

### 新增 TODO（10个）

| # | 文件 | TODO | 类别 |
|---|---|---|---|
| T-shader-1 | CelestialSkyRenderer | 9个 shader JSON + ShaderInstance（crescent/atmosphere/swarm等） | render |
| T-shader-2 | CelestialSkyRenderer | CBT_Destroyed 黑洞着色器 | render |
| T-satrender-1 | CelestialSkyRenderer | Satellite.render()渲染 | render |
| T-dust-1 | CelestialDimensionEffects | ImpactWorldHandler 尘埃 | render |
| T-digamma-1 | CelestialSkyRenderer | HbmLivingProps digamma | render |
| T-fluids-1 | CelestialDimensionEffects | ~~Fluids 常量~~ → 已改为 HBMFluids/HbmFluidType (2026-08-12) | fluid |
| T-model-1 | CelestialSkyRenderer | ResourceManager 模型资源 | render |
| T-lode-1 | CelestialSkyRenderer | ModEventHandlerClient | render |
| T-fogfix-1 | CelestialDimensionEffects | 正确skyColor来源 | render |
| T-metrics-1 | CelestialSkyRenderer | updateSky() 预计算优化 | render |

### Fluid 类型重构（2026-08-12）

将 trait/sky 系统中的流体标识从 `HbmFluidType` 改为 vanilla `Fluid`：
- `CBT_Atmosphere.FluidEntry.fluid`: `HbmFluidType` → `Fluid`
- `CBT_Water.fluid`: `HbmFluidType` → `Fluid`
- `CelestialBody.gas`: `Forge FluidType` → `Fluid`
- 需要 trait 数据时通过 `((HbmFluidType) fluid.getFluidType()).xxx()` 获取
- NBT 序列化使用 `BuiltInRegistries.FLUID.getKey(fluid)` 替代名称字符串
- SolarSystem 中流体常量引用从 `.type().get()` 改为 `.source().get()`

### 统计更新

| 类别 | 旧值 | 新值 |
|---|---|---|
| 已移植文件总数 | 51 | **56** |
| 完全可用 (✅) | 24 | **28** |
| 部分可用 (⚠️) | 27 | **28** |
| TODO 总数 | 21 | **31** |

---

## 第 4 批：Shader 着色器移植（2026-08-12）

### 分类：全部为核心着色器（core shaders）

这些 shader 在几何渲染期间作用于单个天体 quad，非全屏后处理。

### 改动

| 步骤 | 详情 |
|---|---|
| GLSL 现代化 | `#version 120/130` → `#version 150`；`varying` → `in`/`out`；`gl_FragColor` → `out vec4 fragColor`；`texture2D` → `texture`；`gl_TexCoord[0]` → `texCoord0` |
| 文件重命名 | `.frag` → `.fsh`，`.vert` → `.vsh` |
| JSON 定义 | 12 个核心 shader JSON，指向对应 `.vsh`/`.fsh` |
| 注册 | `ClientEvents.ClientModEvents.handleRegisterShaders`，用 `event.registerShader()` |

### 已移植文件

| 类型 | 数量 | 文件 |
|---|---|---|
| Fragment shader (.fsh) | 11 | blackhole, blackholed, crescent, atmosphere, atmosphere_emissive, lightning, nightlights, nuke, fle, supernovae, swarm |
| Vertex shader (.vsh) | 2 | default (POSITION_TEX), swarm (POSITION_COLOR + displacement) |
| JSON 定义 | 12 | 每个 shader 一个 |
| 噪声纹理 | 1 | iChannel1.png |
| 注册代码 | 1 | ClientEvents.java (+60行) |

### CelestialSkyRenderer 中 shader 调用方式

```java
// 旧 (1.7.10)
crescentShader.use();
crescentShader.setUniform1f("phase", phase);
drawPlanetShaderQuad(tessellator, size);
crescentShader.stop();

// 新 (1.20.1) — 现在可以直接用 ClientEvents.crescentShader
RenderSystem.setShader(() -> ClientEvents.crescentShader);
ClientEvents.crescentShader.safeGetUniform("phase").set(phase);
drawPlanetShaderQuad(...); // 用 Tesselator
// 无需 stop() — RenderSystem.setShader 切换时自动解绑
```

### 统计更新

| 类别 | 旧值 | 新值 |
|---|---|---|
| 已移植文件总数 | 56 | **72** |
| TODO 总数 | 31 | **24** (T-shader-1 和 T-shader-2 的 shader JSON/GLSL 部分已解决) |

---

## 第 5 批：天空渲染依赖 + Cloud/Weather 渲染器（2026-08-12）

### 架构决策 — CloudProviderCelestial 拆分

原 1.7.10 `CloudProviderCelestial` (417行) 包含两层：
- **静态数据方法** (`getCloudTintStrength`, `getTintedCloudColor`, `isNeutralCloudFluid`) — 纯计算
- **渲染方法** (`render`, `renderFlatLayer`, `renderFancyLayer`) — GL11 绘制

→ 静态方法吸收进 `AtmosphereRenderUtil`（数据层），渲染方法独立为 `CelestialCloudRenderer`。

### 已移植文件：5 个

| 文件 | 行数 | 说明 |
|---|---|---|
| `ImpactWorldHandler.java` | ~30 | `getDustForClient/getFireForClient` → 从 TomSaveData 读取 |
| `CelestialNukeShockHandler.java` | ~90 | 核爆闪光追踪：server trigger + client query，220tick 自动清理 |
| `AtmosphereRenderUtil.java` | ~280 | 大气渲染参数 + 吸收 CloudProviderCelestial 静态云层方法 |
| `CelestialCloudRenderer.java` | ~170 | 天体云层渲染（flat模式完整；fancy骨架就位） |
| `CelestialWeatherRenderer.java` | ~170 | 天体雨雪渲染（自定义颜色，基于地表液体） |

### 已更新文件：3 个

| 文件 | 更新 |
|---|---|
| `DataEntry.java` | + `DIGAMMA(Float.class)` + `GRAVITY(Boolean.class, true, true, true)` |
| `ClientEvents.java` | + `renderLodeStar` 开关 |
| `CelestialDimensionEffects.java` | fog/sunset 接入 ImpactWorldHandler dust/fire |
| `CelestialSkyRenderer.java` | shader TODO → ClientEvents引用，digamma → AdditionalDataManager，大气参数 → AtmosphereRenderUtil |

### DataEntry 系统评价

**优点**：Capability 正确，DataEntry 类型安全，syncToClient 自动同步
**改进点**：
1. 序列化用 `ordinal()` 脆弱 → 建议改用 `name()` 字符串
2. 单枚举膨胀 → 建议按领域分拆 (PlayerDataEntry/EntityDataEntry)
3. `type.newInstance()` JDK 弃用 → 需 `getDeclaredConstructor().newInstance()`
4. Capability 只挂 Player → 建议扩展到 LivingEntity
5. GRAVITY 建议 Boolean→Float (重力系数 0~1)

### 统计更新

| 类别 | 旧值 | 新值 |
|---|---|---|
| 已移植文件总数 | 72 | **77** |
| TODO 总数 | 24 | **18** (T-dust-1/T-digamma-1/T-fluids-1/T-model-1/T-lode-1 已解决) |

---

## 第 6 批：TODO 清理（2026-08-12）

### 已解决（依赖齐全的 TODO）

| TODO | 文件 | 解决方案 |
|---|---|---|
| 卫星物品注册 | Satellite.register() | 使用 `ModItems.SAT_HEAD_MAPPER/SCANNER/RADAR/LASER/RESONATOR` |
| 目标维度解析 | Satellite.getTargetDimensionId | 使用 `SolarSystem.Body.getDimensionId()` (已存在) |
| 卫星渲染 | Satellite.render/renderOrbitLine | PoseStack + Tesselator 迁移完成 |
| metrics 缓存 | CelestialSkyRenderer | tick级 memoization 替代 updateSky() |
| 卫星渲染调用 | CelestialSkyRenderer | `entry.getValue().render(...)` 已接线 |
| 日落渲染 | CelestialSkyRenderer | vanilla 日出曲线 + 带电尘埃变色 |
| 毁灭天体碎片 | CelestialSkyRenderer | 30随机碎片quad + 冲击波 + 闪光 |
| 撞击叠加层 | CelestialSkyRenderer | 熔岩纹理 + 冲击波 + 闪光 flare |
| 核弹闪光 | CelestialSkyRenderer | ClientEvents.nukeShader + AtmosphereRenderUtil.applyNukeShockUniforms |
| 战争弹道渲染 | CelestialSkyRenderer | 冲击波纹理 + 内圈flare quad |
| 烟雾billboard | CelestialSkyRenderer | 相机相对旋转 (getMainCamera rotation) |
| 太阳亮度曲线 | CelestialTimeProvider | 正确 cos 曲线 + dust + 大气吸收 |
| 星星亮度 | CelestialTimeProvider | rain/thunder/dust 因子 |
| 日出计算 | CelestialDimensionEffects | vanilla OverworldDimensionSpecialEffects 曲线 |
| 天空渲染委托 | CelestialDimensionEffects | `CelestialSkyRenderer.renderSky(...)` |
| fancy 云层 | CelestialCloudRenderer | 双pass (depth-only + color) 3D云盒 |
| 服务端撞击效果 | ImpactWorldHandler | LightLayer + BushBlock/LeavesBlock 判定 |

### 仍未解决（依赖确实缺失）

| TODO | 所需依赖 |
|---|---|
| SatelliteFoeq/Horizons 成就 | MainRegistry (整个主类未移植) |
| SatelliteLaser EntityDeathBlast | 实体系统 |
| SatelliteHorizons EntityTom | 实体系统 |
| SatelliteMiner ItemPoolsSatellite | 物品池系统 |
| SatelliteRailgun BeamPronter | 渲染工具 |
| AnnihilatorSavedData 配方 payout | AnnihilatorRecipes + ItemStackUtil + ComparableStack |
| CelestialNukeShockHandler 网络同步 | HBMNetwork 网络包 |
| renderHeldSatellitePreview | ISatChip |
| ModBlocks.burning_earth | 方块注册（fallback 已用 COARSE_DIRT） |
| TileEntityDysonReceiver | 方块实体系统 |

### 统计更新

| 类别 | 旧值 | 新值 |
|---|---|---|
| TODO 总数 | 18 | **10** |

---

## 第 7 批：Shader 编译错误修复（2026-08-13）

### 运行日志发现的 crash

`Invalid shaders/core/fle.json: Couldn't compile fragment program` → 游戏启动崩溃。

### 根因与修复

| 问题 | 根因 | 修复 |
|---|---|---|
| `fle.fsh` 无 `#version` 声明 | 原文件 `fle.frag` 根本没有 version 行（只有注释），sed 转换找不到替换目标，编译器按 GLSL 1.10 处理导致 `texture()` 报错 | 文件头插入 `#version 150` + `out vec4 fragColor;` |
| `fragColor` 未定义 | 同上——awk/sed 只在发现 `gl_FragColor` 时才插入 out 声明，fle.frag 直接用了 `fragColor` 名字所以被跳过 | 同上 |
| `default.vsh` 缺 `vPosition` 输出 | fle.fsh / supernovae.fsh / blackholed.fsh 需要 `in vec3 vPosition`，但 default.vsh 只输出了 texCoord0 | 添加 `out vec3 vPosition; vPosition = Position;` |
| `supernovae.fsh` 遗留 `texture2DLod` | 老 GLSL 1.20 函数名 | 替换为 `textureLod` |

### 验证结果

- 全部 12 个 fragment shader 均有 `#version 150` + `out vec4 fragColor;`
- default.vsh 输出 `texCoord0` + `vPosition` 与所有 fragment shader 的 `in` 声明完全匹配
- swarm.vsh 输出 `vColor` 与 swarm.fsh 匹配

---

## 第 8 批：运行时 NPE 修复（2026-08-13）

### 运行日志错误

```
java.lang.NullPointerException: Cannot invoke "com.hbm.space.dim.CelestialBody.getTrait(java.lang.Class)" because "body" is null
    at CelestialDimensionEffects.getBrightnessDependentFogColor(CelestialDimensionEffects.java:48)
```

### 根因

`SolarSystem.init()` **从未被调用** — 天体注册表（dimToBodyMap/nameToBodyMap）全程为空，`CelestialBody.getBody()` 连 overworld 兜底都返回 null。雾色计算第一个踩到。

### 修复

| 文件 | 修复 |
|---|---|
| `HBM.java` | `commonSetup` 中调用 `com.hbm.space.dim.SolarSystem.init()`（流体注册已完成，此时调用安全） |
| `CelestialBody.java` | `getBody(String)` 和 `getBody(ResourceKey)` 增加防御性初始化：`SolarSystem.kerbol == null` 时自动 `SolarSystem.init()`，防止初始化顺序问题 |

### 防御性初始化安全性

- 无递归：`init()` 内部只构造 CelestialBody（构造器不调 getBody），`runTests()` 调用 `getBody` 时 kerbol 已赋值
- commonSetup 时 `Minecraft.getInstance().level` 为 null，`withShader()` 提前返回，安全
- 流体 RegistryObject 在 commonSetup 前已完成注册填充，`HBMFluids.XXX.source().get()` 非 null

---

## 第 9 批：天空渲染补全（2026-08-13）

### 用户反馈：天空盒显示为默认彩色天空

### 根因（对比原版 SkyProviderCelestial.render() 全流程找出的遗漏）

| # | 遗漏 | 影响 |
|---|---|---|
| 1 | 天空渐变颜色硬编码黑色 | 🔴 天空背景全黑/缺失，露出原版天空盒 |
| 2 | 地平线以下的行星表面贴图 quad（原版 472-506 行） | 🔴 高海拔/轨道视角看不到脚下行星 |
| 3 | renderAtmosphereGlow 大气辉光带 | 行星周围无辉光 |
| 4 | renderHeldSatellitePreview | 手持卫星无预览 |
| 5 | Station 渲染（OrbitalStation.orbitingStations） | 轨道站不可见 |
| 6 | Flesh shader（CBT_COMPROMISED） | 感染效果缺失 |
| 7 | Crescent shader（月相明暗） | 行星无昼夜明暗分界 |
| 8 | Black hole shader（CBT_Destroyed 恒星） | 毁灭恒星无黑洞效果 |
| 9 | Swarm shader（戴森球虫群） | 虫群不可见 |
| 10 | planetTint 硬编码 Vec3.ZERO | 行星大气混合色缺失 |

### 修复内容

| 文件 | 修复 |
|---|---|
| `CelestialDimensionEffects` | 新增 `getSkyColorStatic()` — 完整移植原版 getSkyColor（多流体混合 + 战争闪光 + eclipse + dust/fire） |
| `CelestialSkyRenderer` | renderSkyGradient 使用真实天空色；新增 `renderPlanetSurface`（地平线下行星表面）、`renderAtmosphereGlow`（四带渐变辉光）、`renderFleshSky`（fle shader + 噪声纹理）、`renderHeldSatellitePreview`（ISatChip）、`renderStation`（种子旋转轨道站）、`renderBlackHoleSun`（blackhole shader）、`renderSwarm`（三环交错点云）、`renderCrescentShadow`（crescent shader） |
| `Satellite` | `renderDefault()` 从 stub 实现为真实渲染（渲染手持卫星预览） |
| `OrbitalStation` | 新增 `orbitingStations` 客户端静态列表 |

### TODO 清零

`CelestialSkyRenderer` 剩余 TODO：**0 个**。原版 render() 全流程现已覆盖。

---

## 第 10 批：天空盒/地面全黑修复（2026-08-13）

### 用户反馈：天空仍是缺省天空盒 + 地面全黑

### 根因（4 个独立 bug）

| # | Bug | 文件 | 影响 |
|---|---|---|---|
| 1 | `hasSkyLight=false` | WorldGenMun:81, WorldGenIke:65 | 🔴 LightEngine 完全不计算天光 → 地面全黑 |
| 2 | ike 维度用 `BuiltinDimensionTypes.END_EFFECTS` | WorldGenIke:76 | 🔴 用原版末地渲染效果 → 紫色末地天空盒 |
| 3 | 天空渐变只画 y=0 水平面 | CelestialSkyRenderer.renderSkyGradient | 🔴 只覆盖屏幕下半部，天空上半部暴露背景 |
| 4 | `SkyType.NONE` | CelestialDimensionEffects 构造 | 🟡 部分版本会跳过整个天空 pass（含自定义 renderSky） |

### 修复

| 修复 | 说明 |
|---|---|
| mun/ike `hasSkyLight` → true | 天光引擎恢复，地面白天正常亮（注释"黑暗天空"是误解——黑暗天空应由天空渲染实现，而非关闭光照） |
| ike `END_EFFECTS` → `HBM.rl("moon_effects")` | 挂载 CelestialDimensionEffects |
| renderSkyGradient 改为 6 面天空盒 | ±200 立方体全面覆盖视球，替代原版 glSkyList 穹顶 |
| `SkyType.NONE` → `SkyType.NORMAL` | 确保自定义 renderSky 被调用（返回 true 时 vanilla 不画日月星） |
| getStarBrightness 加入太阳角度曲线 | 1 - sunBrightness：白天星星隐藏，夜晚可见（原实现无视昼夜） |

### 第二轮修复（天空盒剔除 + 旧存档光照）

| # | Bug | 修复 |
|---|---|---|
| 1 | 6面天空盒顶点绕序反了且未 disableCull → 从盒内看全部被背面剔除，天空盒根本不显示 | renderSkyGradient/renderStars 添加 `RenderSystem.disableCull()` |
| 2 | 未 override `getSkyColor` → vanilla skyBuffer 背景用基类黑色 | override 为大气天空色 |
| 3 | **旧存档光照数据过期** — 世界在 has_skylight=false 时生成，所有 chunk 的天光烘焙为 0 | 需删存档重开或 F3+A 重载区块，新 chunk 才会正常 |

### ⚠️ 重要：修改维度数据后必须注意

1. `genDimensionType` 等是 datagen 生成的（`src/generated/resources/`）——改 Java 后必须 **runData 重新生成**，否则生效的是旧 JSON
2. 维度光照配置变更对**旧存档的已生成区块不生效**（光照数据已烘焙进 chunk）——测试前删存档或重载区块

---

## 第 11 批：结构文件批量移植（2026-08-15）

### 完成工作

1. **通用结构转换脚本** `tools/convert_structure.py`
   - 支持 gzip NBT 读写（zlib raw inflate 兼容 Java 写入的特殊 gzip）
   - 方块名映射：`hbm:tile.XXX` → `hbm:XXX`，vanilla 1.7.10 → 1.20.1 重命名表
   - meta→BlockState 转换：楼梯 facing/half/shape、半砖 type/waterlogged、柱 axis
   - wand_jigsaw → minecraft:jigsaw（orientation + final_state），wand_loot 保留 `hbm:wand_loot`
   - 未知方块 → minecraft:air（结构仍可放置）
   - 输出到 `src/main/resources/data/hbm/structures/`

2. **全部 130 个旧结构文件已转换**（含 dresbmk/meteor/mohobase/munbase 子目录），0 失败

3. **ModBlocks 注册新方块**（WrappedBlockRegistryBuilder 系统）：
   - 混凝土族：brick_concrete 系列、concrete_smooth/pillar/slab/double_slab/stairs、concrete_asbestos 等
   - 钢材族：steel_wall/corner/roof/beam/scaffold/grate/grate_wide/poles
   - 砖块族：brick_compound/asbestos/fire/light/obsidian + 各 stairs
   - deco 系列：deco_steel/rusty_steel/titanium/lead/beryllium/aluminium/rbmk/rbmk_smooth
   - barrel：red_barrel/pink_barrel/vitrified_barrel
   - crate：crate/crate_can/crate_lead/crate_metal/crate_red/crate_weapon
   - 其他：reinforced_glass/pane、reinforced_sand/stone/brick、ladder_steel/tungsten/aluminium、bobblehead、pedestal、fence_metal、hev_battery、spikes、radiorec、tape_recorder、filing_cabinet、deco_computer/crt/toaster、rail_narrow、ore_coal_oil、gravel_obsidian、red_wire_coated、red_connector、balefire、barbed_wire、det_charge、gas_asbestos、wand_air/loot/jigsaw/logic/tandem、deco_loot

### 待办（后续批次）
- deco_pipe 系列（deco_pipe/quad/framed/rim 各变体）需 BlockPipe 类
- door 系列（door_metal/bunker/office）需 DoorBlock 类
- 机器方块（machine_fluidtank/diesel/hephaestus 等）需对应 Block 类
- lightstone/dungeon_spawner/skeleton_holder/spotlight 等特殊方块
- concrete_colored/concrete_colored_ext 彩色方块
- plant_dead 植物、toxic_block/ntm_dirt 等缺纹理方块

### 补充注册（同批次追加）
- deco_pipe 全系列 24 变体（pipe/quad/rim/framed × 配色）→ 普通 Block + pipe_* 纹理
- door_metal/office/bunker → 普通 Block（先保证结构显示，DoorBlock 功能待实现）
- concrete_colored/concrete_colored_ext → 转换脚本临时映射为 hbm:concrete（16色/8色 Block 待实现）

### 全局统计
- 130/130 旧结构文件已转换（0 失败），含 gzip 兼容性修复（zlib raw inflate）
- 新增注册方块 ~80 个

---

## 第 12 批：核心基础设施架构 Review 与重构（2026-08-15）

### 1. transport_net 网络系统抽象
- 新增基类 `AbstractNetwork`（节点集合+合并/切分语义）与 `AbstractNetworkSystem`（拓扑存储/join/leave/link/cut/延迟队列/BFS 连通分量/网络合并切分），作为分配网络通用骨架
- `EnergyNetwork` / `EnergyNetworkSystem` 改为继承上述基类
- `FluidBackupSystem` / `FluidNetwork`（原 NetWork）改为继承上述基类，重命名 NetWork→FluidNetwork，更新 PipeEntity 引用
- 清理遗留的轻量级 `FluidNetworkSystem`（未被真正使用）：移除 BlockFluidPipe 中的死代码调用与 ServerEventHandler 中的 tick

### 2. BECapabilities 增强
- 新增 `SideAccessConfig`：每个面可配置物品槽/流体槽的 Mode（IN/BOTH/OUT/NONE）与能量模式（禁/入/出/双向）
- `getItemHandler/getEnergyHandler/getFluidHandler` 改为消费 SideAccessConfig
- **修复严重 bug**：`load()` 与 `saveAdditional()` 的函数体写反了（load 在写、save 在读），已纠正

### 3. 多方块系统
- 修复 `BEDummyable.onUpdateServer` 中 `distributed` 永不重置、`isFormed` 粘性为 false 的 bug：改为每 tick 复查代理完整性

### 4. 各子系统 review 结论（详见会话记录）
- addational_data：capability 直挂实体/chunk，需要注意 PersistentData 与 capability 的读写时机、玩家 respawn 保持
- hazard：建议继续用 addational_data 存玩家状态，hazard 判定逻辑保持独立（参考 mek 物品能力但不必照搬）
- recipe：SerializableRecipe 目前是模板，JSON 生成尚未启用
- OBJ 模型：CustomPartsModel/TrianglePartsModel 支持分体渲染+默认 mtl

---

## 第 13 批：机器模块抽象 + 渲染/音效基础设施（2026-08-15）

### 1. 机器模块抽象（core/contents/machine）
- `MachineModuleBase<R extends Recipe<Container>>`：参考旧版 com.hbm.module.machine.ModuleMachineBase，将配方处理通用逻辑从 TE 剥离为可复用模块
  - 槽位/罐位接线（input/outputSlots、inputTanks/outputTanks）配置一次
  - 统一 hasInput/canProcess/process/consumeInput/produceOutput
  - 整合 MachineItemHandler / IEnergyContainer / BasicFluidHandler
- `ModuleContainer`：模块物品 handler 的 Container 视图，供配方 matches/assemble 使用
- `AssemblerModule`：装配机示例，演示模式用法
- 自动 IO（物品/流体进出）由 TE 通过 ItemTransferUtils/FluidTransferUtils + getInputSlots/getOutputSlots 完成

### 2. 渲染 util（core/client/render/CoreRenderUtil）
- 封装旧版 GL11 立即模式调用 → Tesselator/BufferBuilder 现代写法（begin/vertex/end、drawBox、vertexUV）
- 状态机封装：depthTest/blend/cullFace/color
- 矩阵操作：pushMatrix/translate
- 每个方法附带对应旧 API 的注释说明；移植时拿不准的 GL 调用优先在此类封装

### 3. 音效现代化（core/client/sounds）
- `HBMUsableSound extends AbstractTickableSoundInstance`：现代循环音效，tick 内更新音量/位置
- `AudioWrapper` 重写：改用 HBMUsableSound，增加 keepAlive 过期机制（机器停止后声音自动停止，防残留）
- 保留旧 playLoopSound(Vec3, long) 签名兼容

---

## 第 14 批：GUI 泛化（2026-08-15）

### 新增 core/client/gui/GuiMachineBase
泛化旧版 com.hbm.inventory.gui.GuiInfoContainer 的公共逻辑（所有机器 GUI 都需要）：
- drawElectricityInfo / drawFluidInfo / drawProgressInfo / drawCustomInfoStat（悬浮信息）
- drawStackText（多行文本+物品的复杂悬浮框）
- drawInfoPanel（gui_utility.png 信息图标，含旧 5 参签名兼容）
- checkClick / isMouseInside（交互区域判定）
- renderItem / renderItemWithCount（GUI 内物品渲染）
- getUpgradeInfo（从 IUpgradeInfoProvider 收集升级信息）

### 接线
- com.hbm.gui.screen.BaseMachineGui 改为继承 GuiMachineBase，删除重复的 drawInfoPanel/drawCustomInfoStat/isMouseInside
- 现有 GuiCrystallizer 等自动获得基类能力

### 已有组件盘点
- widget/：BarProgress、BarEnergy、BarFluid（带 tooltip 的动态条）、MultiStateButton、BounceButton、Rect
- page/recipe/：RecipePage（现代配方选择页，替代旧 GUIScreenRecipeSelector）

---

## 第 15 批：升级系统抽象（2026-08-15）

### 问题
旧 UpgradeManagerNT 的 checkSlotsInternal 在移植时被整个注释掉，导致 getLevel 永远返回 0，
升级系统实际失效（ElectricFurnace/Chemplant 的升级槽不生效）。

### 改动
1. 修复 UpgradeManagerNT：
   - 恢复扫描逻辑：遍历升级槽，按 IUpgradeInfoProvider.getValidUpgrades() 过滤可接受类型
   - 同类型叠加 tier 并用机器上限封顶；mutex 互斥类型只保留优先级最高者
   - 槽位内容未变化时跳过重扫（缓存）
   - 启用 ItemMachineUpgrade.UpgradeType.mutex 字段
2. 新增 core/contents/upgrade/MachineUpgradeHandler（升级效果抽象层）：
   - 三步职责分离：UpgradeManagerNT（扫描汇总）→ IUpgradeInfoProvider（声明可接受类型与效果）→ MachineUpgradeHandler（等级→倍率）
   - 统一 speed/power/effect/overdrive/fortune 每级影响系数换算
   - 机器只需：new MachineUpgradeHandler(this).speed(0.25).power(0.15) + 每 tick check + 用倍率
3. 接线：ElectricFurnaceEntity / ChemplantEntity 改用 MachineUpgradeHandler，
   消除手写 checkSlots/getLevel/公式 的重复代码

---

## 第 16 批：升级整合进配方模块 + 富文本 + 说明文本集中化（2026-08-15）

### 1. MachineModuleBase 整合升级
- 新增 enableUpgrades(槽范围, speed系数, power系数) 开启升级支持
- canProcess/process 自动应用速度/功耗倍率，process 内部每 tick 调 updateUpgrades
- 构造可选传入 owner BlockEntity（升级系统需要）

### 2. 富文本解析器（api/text/RichText）
- lang 字符串中支持 <red>、<gold>、<#RRGGBB> 等颜色标签 + <b>/<i>/<u>/<s>/<obf> 样式标签（可嵌套）
- RichText.parse(str) → MutableComponent；RichText.parseLang(key) → 从 lang 取值并解析
- 目的：取代原版大量 EnumChatFormatting 字符串拼接，翻译时直接在 lang 里标注样式

### 3. 说明文本集中化（block/interfaces/ITooltipProvider 增强）
- 说明文本写在 lang 的 "<物品id>.desc" 键中，支持富文本标签
- addStandardInfo 按 SHIFT 显示说明（保留旧交互），否则显示提示
- 覆盖 descKey() 可自定义说明键
- 删除重复新建的 api/ITooltipProvider，统一使用 block/interfaces/ITooltipProvider

---

## 第 17 批：正式开始机器移植（2026-08-15）

### 移植策略（按用户指示）
- 以 ModBlocks 为索引，逐台移植，避免翻旧的不使用代码
- 从易到难：先打通管线，再移植复杂的
- 每台机器移植前确认 blockrender 位置（分散放置）
- 上下文管理：一次一台

### 第一台：machine_converter_he_rf（HE→RF 能量转换器）
- 理由：无 GUI/无流体/无配方/单方块/普通模型，纯能量转换，验证能量能力接入管线
- Block：com.hbm.block.machine.BlockConverterHeRf（继承 BlockMachineBase）
- BE：com.hbm.blockentity.machine.ConverterHeRfEntityBE（继承 BaseMachineBlockEntity）
  - HBMCaps.LONG_ENERGY 接收 HE（ProxyEnergyHandler）
  - ForgeCapabilities.ENERGY 输出 FE（HybridEnergyStorage）
  - 转换比例 5 HE → 1 RF（沿用旧版）
- 注册：ModBlocks（registerMachineBlockWithItem）+ HBMTiles（CONVERTER_HE_RF_ENTITY）
- 模型：cube_all 用 machine_converter_he_rf 纹理（已存在）
- 资源：模型/blockstate/物品模型 三文件已建；lang zh_cn 已有
- 编译通过

### 遗留
- 未加入 creative tab（保持最小改动）
- 后续可移植反向 machine_converter_rf_he（复用模式）

---

## 第 18 批：WrappedBlockRegistryBuilder 自动生成验证 + 两台转换器（2026-08-16）

### 关键里程碑：builder 自动生成资源管线打通
- 用 WrappedBlockRegistryBuilder 注册方块 → runData 自动生成 model/blockstate/item model/loot/lang/tag
- 验证：machine_converter_he_rf / machine_converter_rf_he 的 blockstate+模型+loot 全部自动生成成功
- 结论：后续所有机器移植无需手写资源文件，只写 Block/BE/Menu/Screen + builder 注册

### 已移植机器（本次 + 上次）
1. machine_converter_he_rf（HE→RF，第17批）
2. machine_converter_rf_he（RF→HE，本次）
   - Block: BlockConverterHeRf / BlockConverterRfHe
   - BE: ConverterHeRfEntity / ConverterRfHeEntity（双能量能力 LONG_ENERGY + Forge ENERGY）
   - builder 注册：.tab(MACHINE).mSmp(cube_all).loot(drop_self).loc(reverse_gen).tile(::new)

### 修复
- ModBlocks: 移除 deco_rbmk/deco_rbmk_smooth 的重复 registerLegacyBlockItemAlias（与 DECO_RBMK 方块注册冲突，导致 datagen 崩溃）
- 运行 runData 验证通过

### 配方移植策略（用户指示）
- 配方暂缓，用 datagen（src/main/java/com/hbm/datagen/recipe/provider）生成，先出示例，后续统一添加

---

## 第 19 批：machine_microwave 微波炉移植（2026-08-16）

### 选型
- 探索代理调查 10 台候选机器后选定：唯一"单方块+无流体+无自定义物品+无多方块"俱全
- 只依赖 vanilla 熔炉配方（SMELTING），有 GUI 验证 Menu+Screen 管线

### 移植内容
- Block: BlockMicrowave（BlockMachineBase）
- BE: MicrowaveEntity（BaseMachineBlockEntity）
  - 3 槽：0输入（可熔炼食物）、1输出、2电池
  - 能量：LONG_ENERGY + Forge ENERGY 双能力，覆盖 getEnergyContainer()
  - 逻辑：speed 0-5 调节，speed>=5 过热爆炸（简化版 ExplosionVNT）
  - 物品：自定义 ItemStackHandler 完全委托 items
- Menu: MicrowaveMenu（BaseMachineMenu，SlotItemHandler）
- GUI: MicrowaveGui（BaseMachineGui，能量/进度/速度条 + 速度按钮）
  - 按钮通过现有 C2SSyncTileMessage 发到 handleClientPacket
- 注册：builder（.tab/.mSmp/.loot/.loc/.tile）+ HBMMenus（IForgeMenuType）+ MenuScreens
- runData 自动生成 blockstate/模型，中英文 lang 已有

### 关键收获
- WrappedBlockRegistryBuilder 全自动资源管线已完全打通（第3台验证）
- MenuType 用 IForgeMenuType.create 配 (id,inv,buf) 构造
- 跨包访问 BE 用 Menu 提供 getEntity() getter
- 食物判断用 Item.isEdible()（1.20.1 无 ItemFood）

### 已移植机器累计：3 台
converter_he_rf / converter_rf_he / microwave

---

## 第 20 批：批量移植策略启动（2026-08-16）

### 策略
- 3-5台一批，同类型批量移植
- 用探索代理批量调查机器依赖，避免逐个试错
- 优先：无GUI/无渲染/单方块/无重依赖

### 本批完成：machine_detector（功率检测器）
- 收到电网能量→点亮→输出红石强信号15
- Block: BlockDetector（LIT blockstate + isSignalSource + getDirectSignal）
- BE: DetectorEntity（LONG_ENERGY 接收，每tick耗1能量）
- builder 注册 + runData 自动生成资源，lang 已有
- 编译通过

### 批量调查结论（探索代理）
- keyforge：单方块/无渲染/有GUI，依赖 ItemKeyPin+ItemKey（后者已移植），首推下一批
- autocrafter：单方块/无渲染/有GUI，需先移 ModulePatternMatcher
- 多数"看似简单"机器有隐藏依赖：大气系统(TileEntityMachineBase)、流体MK2、OBJ渲染、天体维度
- 纯被动/能量型机器稀少，GUI型机器为主要批量对象

### 已移植机器累计：4台
converter_he_rf / converter_rf_he / microwave / detector

---

## 第 21 批：detector + keyforge 批量移植（2026-08-16）

### 本批完成 2 台 + 2 物品（一次写完再一起 rundata）
1. machine_detector（功率检测器，第20批启动本次收尾）
   - LIT blockstate + 红石强信号，LONG_ENERGY 接收
2. machine_keyforge（锁匠桌）
   - 3槽：模板钥匙/复制目标/随机钥匙，复制 pin
   - 移植物品：key（ItemKey）、key_pin（ItemKeyPin，NBT pins）
   - Block/BE/Menu/GUI 全套，builder 注册
- 一次编译 + 一次 runData，全部通过
- keyforge 用 cube_all + machine_keyforge_side 纹理（避免 bottom_top 纹理命名问题）

### 已移植机器累计：5台
converter_he_rf / converter_rf_he / microwave / detector / keyforge
（另有 key/key_pin 物品）

### 批量移植工作流（已固定）
1. 用脚本/探索代理确认依赖
2. 一次写完全套（Block+BE+Menu+GUI+物品）
3. 集中注册（builder + HBMMenus + MenuScreens）
4. 攒几台后一次 compile + 一次 runData 排查

---

## 第 22 批：funnel 组合漏斗移植（2026-08-16）

### 本批
- machine_funnel（组合漏斗）完成
  - 18槽（0-8输入、9-17输出），3种模式（3x3/2x2循环）
  - 用 1.20.1 TransientCraftingContainer + RecipeManager 查合成配方
  - mode 按钮走 C2SSyncTileMessage → handleClientPacket
  - OBJ 渲染简化为普通 cube 模型（machine_funnel_side 纹理）
  - Block/BE/Menu/GUI 全套，builder 注册，runData 通过

### 探索代理调查结论（本批）
- funnel：依赖基本齐备，最可行 ✅（已完成）
- autocrafter：缺 ModulePatternMatcher + 矿辞工具，高难度
- mixer：缺大量物品 + 流体 API 重写 + MixerRecipes，很高难度（暂缓）
- ashpit：依赖未移植的火炉系统（灰烬来源），放弃

### 已移植机器累计：6台
converter_he_rf / converter_rf_he / microwave / detector / keyforge / funnel

### 教训
- 看似简单的机器常依赖未移植的"上游"系统（如 ashpit 需要火炉产生灰烬）
- 探索代理批量调查比逐个试错省上下文

---

## 第 23 批：core 多方块体系统一 + solar/funnel 完整渲染（2026-08-16）

### core 多方块体系统一（重要架构变更）
- 用户指示：所有多方块机器统一继承 core/blockentity 的 BEDummyable（新体系），旧体系（DummyableBlockEntity + TileProxyCombo）不再用于新机器，后续 assembler 等也改
- 改动：
  - BEDummyable：加 setMultiblockData（从 MultiblockData 构建 MultiblockModule）、giveProxyCapabilities、distributeCapabilities 默认调 MultiblockData.distributeCaps；checkProxy 兼容 BEProxy/TileProxyBase
  - core BlockDummyable.newBlockEntity：非核心方块返回 BEProxy（替代 TileProxyCombo）
  - DummableHelper：fillSpace/resolveCorePos 同时识别 BEProxy 与 BEDummyable
  - MultiblockData.distributeCaps：同时支持 DummyableBlockEntity 与 BEDummyable

### machine_solar 完整移植（多方块 + TESR + OBJ）
- Block: BlockSolarPanel（core BlockDummyable，5x5 平台，multiblock 注册）
- BE: SolarPanelEntity（BEDummyable + MultiblockData，新能量体系 BasicEnergyHandler）
- 太阳功率用 CelestialBody.getSunPower()（按天体日距平方反比，非 stub）
- OBJ: 复制 solar_panel.obj + 纹理，Models.SOLAR_PANEL 注册
- TESR: SolarPanelRenderer（按朝向旋转渲染）
- MultiblockData 注册 machine_solar

### machine_funnel 完整渲染
- 复制 funnel.obj，用 forge:obj 方块模型（machine_funnel.json → funnel.obj）
- 已知优化项：原版分 Top/Bottom/Side 三件贴不同纹理，当前用单一 side 纹理（后续可补 TESR 分件纹理）

### 已移植机器累计：7台
converter_he_rf / converter_rf_he / microwave / detector / keyforge / funnel / solar

---

## 第 24 批：科技线盘点 + OBJ 注册方式统一（2026-08-16）

### 关键发现
- 新工程已有大量已注册机器：MACHINE_ARC_FURNACE/CENTRIFUGE/CHEMPLANT/CRYSTALLIZER/ORE_SLOPPER（用 genSimpleModel + 自定义 loader）
- block/machines/ 已有 OBJ：acidizer/arc_furnace/centrifuge/miner_large/ore_slopper/wood_burner
- 我差点重复注册 centrifuge/crystallizer/arc_furnace（已删除重复）

### builder 新增 .obj() 便捷方法
- 用自定义 loader（CustomPartsModel.LoaderBuilder / hbm:multi_parts_obj）生成 OBJ 方块模型
- 参数：model(obj路径)、texture(贴图)、size(缩放基数，不确定填1)
- 机器用 HORIZONTAL（带朝向），装饰才 SIMPLE
- genSimpleModel 已确认用自定义 loader（非 forge:obj），与 .obj() 等效

### OBJ 机器统一做法（后续遵循）
用 builder 的 .obj(model, texture, size) 或 .model(provider.genSimpleModel(...))，勿手写 forge:obj json

### 已移植机器累计：7台
converter_he_rf / converter_rf_he / microwave / detector / keyforge / funnel / solar
（另有已注册的 arc_furnace/centrifuge/chemplant/crystallizer/ore_slopper 等）

---

## 第 25 批：科技线深度盘点 + 探索结论（2026-08-16）

### 探索结论（diesel/liquefactor/sawmill 均未移植）
- machine_diesel：单方块+GUI+渲染+污染基类，依赖 canister_empty 等
- machine_liquefactor：多方块+GUI+渲染+配方系统（LiquefactionRecipes 缺失）
- machine_sawmill：多方块+渲染+投掷实体（EntitySawblade）+缺失物品/方块
- 共同缺口：旧基类 TileEntityMachinePolluting/TileEntityMachineBase 无新体系等价物；api.hbm.energymk2/fluidmk2 等已删需映射；IControlReceiver/IGUIProvider 等接口缺失

### 关键判断
- 大量机器移植卡在"通用基础设施缺失"（旧基类/接口/配方体系），非机器本身
- 一次性补全这些基础设施（新体系 TileEntityMachineBase + 通用接口）会让后续所有机器受益
- 但这是较大的架构工作，需要用户确认方向

### 当前状态
- 已移植（本轮）：solar（core多方块+TESR+OBJ）、funnel（OBJ）、detector、keyforge、microwave、converter×2
- 新工程已有：arc_furnace/centrifuge/chemplant/crystallizer/ore_slopper/wood_burner/miner_large 等科技线机器
- OBJ 注册方式已统一：builder.obj() 或 genSimpleModel（自定义 loader）

---

## 第 26 批：BEMachineBase 定位明确 + LiquefactionRecipe 配方（2026-08-16）

### BEMachineBase 功能定位（用户明确）
- 不另立旧版 TileEntityMachineBase 的 slots[]/ISidedInventory 功能
- 直接复用 BECapabilities 已有的：items(MachineItemHandler)/energyContainer(HBMEnergyHandler)/fluidHandler + getItemHandler/getEnergyHandler/getFluidHandler + addCapability + createItemHandler 等
- 机器子类移植时：items.getStackInSlot(i)、energyContainer.getEnergy()、capabilitiesContent.addCapability(...)
- 已移除误加的旧版功能方法

### 配方体系（用户指示）
- 新配方类型继承 RecipeSerializerBuilder.AutoRecipe
- 仿照 RecipeCentrifuge/RecipeCrystallizer：factory 静态字段 + (id,type,serializer) 构造 + onDataLoaded + matches/assemble
- 在 ModRecipes 用 register() 注册

### 新建 LiquefactionRecipe
- 液化配方：CountableIngredient input → FluidStack outputFluid + duration
- ModRecipes.LIQUEFACTOR 注册
- 编译通过

---

## 第 27 批：machine_liquefactor 工业液化机（2026-08-16）

### 移植内容（core 体系完整移植）
- Block: BlockLiquefactor（core BlockDummyable + MultiblockData 3x1x1 高4 + 流体cap）
- BE: LiquefactorEntity（BEDummyable + BECapabilities）
  - 4槽：0输入/1电池/2-3升级；能量 BasicEnergyHandler；流体 BasicFluidHandler 1罐
  - 配方：LiquefactionRecipe（AutoRecipe）用 CachedCheck 查询
  - 升级：SPEED/POWER 手写
- Menu/GUI: LiquefactorMenu/LiquefactorGui
- OBJ: 复制 liquefactor.obj + 纹理，builder.obj() 生成
- 配方: LiquefactionRecipeProvider（煤/褐煤→煤油示例）+ ModRecipes.LIQUEFACTOR 注册
- runData 通过，配方 JSON 正确生成

### 已移植机器累计：8台
converter_he_rf / converter_rf_he / microwave / detector / keyforge / funnel / solar / liquefactor

---

## 第 28 批：machine_solidifier 工业固化机（2026-08-16）

### 移植内容（core 体系，与 liquefactor 同构）
- Block: BlockSolidifier（BlockDummyable + getMultiblockData 覆写，未用 mapping 静态注册——用户新指示）
- BE: SolidifierEntity（BEDummyable，5槽：0输出/1电池/2-3升级/4罐标识 + 1流体罐）
- 配方: SolidificationRecipe（AutoRecipe：流体→物品，output 用 list(TYPE_STACK)）
- Menu/GUI: SolidifierMenu/SolidifierGui
- OBJ + datagen 配方（lava→obsidian、water→ice）

### 修复：AutoRecipe 的 .stack() 序列化 bug
- .stack() 生成 ITEM schema（期望 Item），但配方字段是 ItemStack → 序列化失败
- 改用 .list(OUTPUT, TYPE_STACK)（ITEM_STACK schema）解决
- 液化配方（.fluid() FLUID schema）本来正确

### 后续机器 MultiblockData 新做法（用户指示）
- 由 BlockDummyable 子类的 getMultiblockData() 提供，不再在 MultiblockData.mapping 静态注册
- 已注册的 liquefactor/solidifier 保持现状（在 mapping 注册了），后续机器用新法

### 已移植机器累计：9台
converter×2 / microwave / detector / keyforge / funnel / solar / liquefactor / solidifier

---

## 第 29 批：油系机器批量移植 5 台（2026-08-16）

### 本批完成 5 台（core 体系 + AutoRecipe 配方 + datagen）
1. machine_coker（焦化装置）— 热裂化重油→石油焦+油焦，22格多方块，热源驱动
   - CokerRecipe（流体→物品+流体），配方：heavyoil→COKE_PETROLEUM+OIL_COKER
2. machine_hydrotreater（加氢装置）— 油+氢→脱硫油+酸气，6格多方块，简化催化剂
   - HydrotreatingRecipe（流体→3流体）
3. machine_refinery（炼油厂）— 热油→重油/石脑油/轻油/石油气，8格多方块
   - RefineryRecipe（流体→4流体列表），配方：HOTOIL→4馏分
   - 简化：移除过压爆炸/火灾/污染
4. machine_diesel（柴油发电机）— 单方块，烧燃料→发电（FT_Combustible燃烧能），红石控制
   - 简化：移除污染/空气
5. machine_radiator（散热器）— 废蒸汽→水，复用 CondenserLogic，5x5 多方块

### 新增 HBMKey 常量
- HYDROGEN / SOURGAS（加氢配方用）

### 经验
- 多方块机器 getMultiblockData() 覆写（不再依赖 mapping 静态注册）
- 配方输出用 .list(TYPE_STACK / TYPE_FLUID_STACK)
- BaseMachineBlockEntity（旧体系）无 fluidHandler 字段，用 BaseMachineBlockEntity 时自行持有 FluidTank + 注册 ForgeCapabilities.FLUID_HANDLER

### 已移植机器累计：14台
converter×2 / microwave / detector / keyforge / funnel / solar / liquefactor / solidifier / coker / hydrotreater / refinery / diesel / radiator

---

## 第 30 批：修复闪退 + 三项意见落实（2026-08-16）

### 1. 关键修复：放置机器闪退（NPE）
- 根因：BEMachineBase 构造 `getTypeById(HBMTiles.getId(getId(state)))` 双重加 "tile_" 前缀 → 查 `tile_tile_xxx` 为 null → NPE
- 修复：改为 `getTypeById(getId(state))`（getTypeById 内部已加前缀）

### 2. 意见1：ModBlocks 大写注册名
- 新机器字段改为大写（MACHINE_LIQUEFACTOR/SOLIDIFIER/COKER/HYDROTREATER/REFINERY/DIESEL/RADIATOR/SOLAR/FUNNEL/DETECTOR/MICROWAVE/KEYFORGE）
- 填入用户预留的占位声明处（MACHINE_* 无初始化处），不再在文件末尾小写堆砌
- 同步更新 BE/MultiblockData 引用

### 3. 意见2：obj size + 碰撞箱
- obj size 按 MultiblockData 最大维度设置（coker 22/hydrotreater 6/refinery 8/liquefactor&solidifier 3/solar 2）
- 所有 BlockDummyable 子类构造函数补 shape（coker 352格高、hydrotreater 96、refinery 128、liquefactor&solidifier 64、radiator 5x5平台）

### 4. 意见3：测试
- 新增 src/main/java/com/hbm/test/MachineTestSuite.java：服务器进入世界后自动自检
  - 高空放置液化机/炼油厂 → 验证 BE 创建 → 注入流体 → 放物品 → 查配方 → 销毁验证无残留
  - 接入 ServerEventHandler.levelTick（单机 OVERWORLD 首次执行）
- runclient 进入存档即可看日志（HBM-MachineTest）
- GameTest 框架尝试但 @GameTestHolder 不在 common 编译 classpath，放弃改用自检

### 已移植机器累计：14台

---

## 第 31 批：测试移至 src/test + OBJ 解析修复（2026-08-16）

### 1. 测试移至 src/test（用户要求）
- 从 main 删除 MachineTestSuite（不再随模组打包）
- 新增 src/test/java/com/hbm/test/MachineGameTests.java（GameTest 框架）
  - 测试：放置液化机/炼油厂 → 验证 BE → 注入流体 → 放物品 → 查配方 → 销毁
- build.gradle 配置 test sourceSet（依赖 main）+ client/server run 加载 test classpath
- 生成 8x8x8 空结构 src/test/resources/data/hbm/gametest/structures/empty.nbt
- 验证：compileTestJava 通过；jar 不含测试类（不打包生产）
- runGameTestServer 因模组既有 client 类引用（ITooltipProvider 等）无法跑纯服务端，需用 runclient 的 /test 命令

### 2. OBJ 模型解析 bug 修复（放置闪退相关）
- 根因：Blender OBJ 混合 `f v/vt/vn` 和 `f v//vn` 格式，CustomPartsModel 对空 vt 索引执行 `--` 变 -1 → texCoords.get(-1) 越界
- 修复：parse 时空索引映射为 0（makeQuad 用默认坐标），正常索引才减 1
- 修复后液化机等 OBJ 模型可正常烘焙

### 3. 闪退修复回顾（上批）
- BEMachineBase 构造双重 "tile_" 前缀 → NPE，已修复

### 已移植机器累计：14台

---

## 第 32 批：修复 GUI 打开闪退（Slot container null）（2026-08-16）

### 根因
- 打开机器界面（如高炉 difurnace）时 Slot.getItem NPE：Slot 的 container 为 null
- MenuBase/BaseMachineMenu 的 BE 构造只设 be/containerData，未设 container
- 子类如 DifurnaceMenu 用 `new Slot(container, ...)` → container null → 崩溃

### 修复
- MenuBase BE 构造：`container = blockEntity instanceof Container c ? c : null`
- BaseMachineMenu BE 构造：同样处理
- 对 BE 实现 Container 的机器（旧体系/BEMachineBase）生效；BEDummyable 机器用 SlotItemHandler 不受影响

### 涉及
- core/menu/MenuBase.java
- gui/menu/BaseMachineMenu.java

---

## 第 33 批：机器 Menu/GUI 改用 builder 自动注册（2026-08-16）

### 改动（用户建议）
- 9 台新增机器的 Menu/GUI 注册从手动（HBMMenus + ClientEventHandler MenuScreens.register）改为 WrappedBlockRegistryBuilder 链式 `.menu(XxxMenu::new).gui(XxxGui::new)`
- Menu 内 MenuType 获取从 `HBMMenus.XXX_MENU.get()` 改为 `HBMMenus.getById("machine_xxx")`（builder 自动注册 menu_machine_xxx）
- 删除 HBMMenus 中 9 个手动 MenuType.register 和 ClientEventHandler 中 9 个手动 MenuScreens.register
- 涉及机器：microwave/keyforge/funnel/liquefactor/solidifier/coker/hydrotreater/refinery/diesel

### 顺带修复
- FLUID_DUCT_NEO 既有残缺注册（`new WrappedBlockRegistryBuilder(...).build();` 缺链式调用 + `sound(ModSounds.)` 未完成）→ 补全为有效注册

### skill 更新
- hbm-1710-to-1201-porting SKILL.md 新增"第 4A 章：机器移植实战"（完整模板/builder 用法/已移植清单/bug 修复/测试/探索建议）

### 已移植机器累计：14台

---

## 第 34 批：RTG + E-Press + Ashpit（2026-08-16）

### 已移植机器：3 台

| 文件 | 状态 | 说明 |
|---|---|---|
| `utils/RTGUtil.java` | ✅ | RTG 辅助工具（热量计算，简化掉衰变/配置） |
| `item/misc/ItemRTGPellet.java` | ✅ | 增加 heat 字段；`pellet_rtg` 改为 ItemRTGPellet(heat=10) |
| `block/machine/BlockRTG.java` | ✅ | RTG 方块（单方块） |
| `blockentity/machine/RTGEntityBE.java` | ✅ | 15 槽 RTG 燃料棒 → heat → power(×5/tick)，输出 HE 到相邻方块 |
| `gui/menu/RTGMenu.java` | ✅ | 15 槽位菜单 |
| `gui/screen/RTGGui.java` | ✅ | 热量条+能量条（gui_rtg.png，ySize 188） |
| `block/machine/BlockEPress.java` | ✅ | 电动锻压机方块（单方块） |
| `blockentity/machine/EPressEntityBE.java` | ✅ | 5 槽（电池/模板/输入/输出/升级），用电能锻压，复用 ModRecipes.PRESS |
| `gui/menu/EPressMenu.java` | ✅ | 5 槽位菜单 |
| `gui/screen/EPressGui.java` | ✅ | 能量条+锻压进度（gui_epress.png，ySize 186） |
| `block/machine/BlockAshpit.java` | ✅ | 灰烬收集器方块（单方块） |
| `blockentity/machine/AshpitEntityBE.java` | ✅ | 5 槽灰烬、接收灰烬等级并合成 ash 物品（addAsh 供火箱/烟囱调用） |
| `gui/menu/AshpitMenu.java` | ✅ | 5 槽位（只能取出）菜单 |
| `gui/screen/AshpitGui.java` | ✅ | 灰烬槽位显示（gui_ashpit.png，ySize 168） |

### 注册（ModBlocks，builder 链）
- `MACHINE_ASHPIT` → `machine_ashpit`：cube_all 用 `models/machines/ashpit` 贴图，`.tile().menu().gui()`
- `MACHINE_RTG_GREY` → `machine_rtg`：cube_all 用 `rtg` 贴图，`.tile().menu().gui()`（字段沿用参考名的 machine_rtg，注册名与 Menu/BE 一致）
- `MACHINE_EPRESS` → `machine_epress`：cube_all 用 `machine_epress` 贴图，`.tile().menu().gui()`

### 修复的预先存在 WIP bug（用户确认后修复）
1. **BEUpdateable 构造器断编译**：工作区 WIP 把 `BEUpdateable(BlockEntityType, BlockPos, BlockState)` 改为 `(BlockPos, BlockState)`，但 CapabilityBE/BEProxy/BEPipeBase/全部 RBMK BE 仍调 3 参 → 加回 3 参构造器重载（保留新 2 参 + 旧 3 参），BECapabilities 同样处理
2. **MACHINE_FURNACE_BRICK litEmission 崩溃**：`MachineBrickFurnace` 无 LIT 属性却用 `.lightLevel(litEmission(1))` → runData 静态初始化即崩（Block{minecraft:air}）。移除 litEmission
3. **Meteorite 静态引用注册顺序**：`static List meteorOres = List.of(ModBlocks.ORE_METEOR_*.get())` 在类加载即求值 → 注册前为 null。改为懒加载 `getMeteorOres()`
4. **TileEntityFurnaceBrick 孤立行**：`isItemValid` 内有孤立 `AbstractFurnaceBlockEntity` 语法错误 → 删除

### 测试
- MachineGameTests 新增 placeRTG/placeEPress/placeAshpit 三个 GameTest（验证 BE 创建、槽位数量、RTG 产热产电、Ashpit 灰烬转换）

### 已移植机器累计：17台

---

## 第 35 批：Radiolysis + ArcWelder + MilkReformer（2026-08-17）

### 已移植机器：3 台（全部改用用户 core 基类体系）

| 文件 | 状态 | 说明 |
|---|---|---|
| `block/machine/BlockRadiolysis.java` | ✅ | 辐射裂解装置（3 格高多方块，BlockDummyable） |
| `blockentity/machine/RadiolysisEntity.java` | ✅ | extends BEDummyable：10 RTG 槽产热→产 HE+裂解流体，3 罐（输入/输出1/输出2），15 槽 |
| `gui/menu/RadiolysisMenu.java` | ✅ | 15 槽位（RTG/流体桶/消毒/电池） |
| `gui/screen/RadiolysisGui.java` | ✅ | 能量条（gui_radiolysis.png，230x166） |
| `Inventory/recipe/RadiolysisRecipe.java` | ✅ | AutoRecipe：流体→2 流体（input/output1/output2） |
| `block/machine/BlockArcWelder.java` | ✅ | 电弧焊机（2 格高多方块） |
| `blockentity/machine/ArcWelderEntity.java` | ✅ | extends BEDummyable：8 槽（3 输入/输出/电池/罐ID/2升级），1 罐，复用 ArcWelderRecipe |
| `gui/menu/ArcWelderMenu.java` | ✅ | 8 槽位 |
| `gui/screen/ArcWelderGui.java` | ✅ | 能量条+进度（gui_arc_welder.png，176x204） |
| `Inventory/recipe/ArcWelderRecipe.java` | ✅ | AutoRecipe：多物品+可选流体→物品（inputs list + fluid + output） |
| `block/machine/BlockMilkReformer.java` | ✅ | 牛奶改质器（7 格高多方块，placementOffset 1） |
| `blockentity/machine/MilkReformerEntity.java` | ✅ | extends BEDummyable：11 槽，4 罐（牛奶/EMILK/CMILK/CREAM），硬编码 refine 逻辑 |
| `gui/menu/MilkReformerMenu.java` | ✅ | 9 槽位（电池/牛奶/3产物桶出入） |
| `gui/screen/MilkReformerGui.java` | ✅ | 能量条（gui_milk_reformer.png，176x238，纹理从参考复制） |

### 注册（ModBlocks，builder 链）
- `MACHINE_RADIOLYSIS` → `machine_radiolysis`：`.obj()` + `.tile(...,true)` + `.menu().gui()`
- `MACHINE_ARC_WELDER` → `machine_arc_welder`：同上
- `MACHINE_MILK_REFORMER` → `machine_milk_reformer`：同上
- OBJ 模型复制：`block/radiolysis/radiolysis.obj`、`block/arc_welder/arc_welder.obj`、`block/milk_reformer/milk_reformer.obj`；贴图 `models/radiolysis.png`（复制）、`models/machines/milker.png`、`arc_welder.png`（已有）

### 配方
- ModRecipes 新增 `RADIOLYSIS` / `ARC_WELDER`
- datagen providers：`RadiolysisRecipeProvider`（水→过氧化氢+氢气）、`ArcWelderRecipeProvider`（铁板/钢板→焊缝）
- 复现并确认 bug #4：AutoRecipe `.stack()` 期望 Item 而非 ItemStack，输出用 `.list(TYPE_STACK)` + provider 传 `List.of(ItemStack)`

### MultiblockData
- mapping 新增 3 条：radiolysis(2,0,1,1,1,1)、arc_welder(1,0,1,0,1,1)、milk_reformer(6,0,1,1,1,1)
- Block 内 getMultiblockData() 同名（供直接放置）

### 采用用户 core 基类体系（重要）
- BE 全部 extends `BEDummyable`（core），构造 `super(pos, state)`（type 由 blockstate 推导）
- 能力：`MachineItemHandler`(items) / `createEnergyHandler(cap, IO)`(energyContainer) / `BasicFluidHandler`(fluidHandler)
- `setMultiblockData(MultiblockData.mapping.get(ModBlocks.XXX.get()))`
- 电池充电用 `TransmitUtils.dischargeItem`；RTG 热量用 `RTGUtil.updateRTGs(IItemHandler, slots)`（用户已改为接收 IItemHandler）
- 升级扫描参考 LiquefactorEntity 手写（不用 MachineUpgradeHandler.check，因其需 List<ItemStack>）

### 测试
- MachineGameTests 新增 placeRadiolysis/placeArcWelder/placeMilkReformer（验证 BE 创建、槽位数、配方、RTG 产热产电、牛奶注入）

### 已移植机器累计：20台

---

## 第 36 批：BlastFurnace + Mixer + CatalyticReformer + VacuumDistill + GasCent + CryoDistill + Stirling + Sawmill + CombustionEngine + BreedingReactor（2026-08-17）

### 已移植机器：10 台（全部 core 基类体系）

| 机器 | Block | BE 基类 | GUI | 配方 |
|---|---|---|---|---|
| 高炉 BlastFurnace | BlockBlastFurnace (7格) | BEDummyable | ✅ | 复用 BlastFurnaceRecipe |
| 混合机 Mixer | BlockMixer (3格) | BEDummyable | ✅ | MixerRecipe |
| 催化重整器 CatalyticReformer | BlockCatalyticReformer (3格) | BEDummyable | ✅ | ReformingRecipe |
| 真空蒸馏塔 VacuumDistill | BlockVacuumDistill (9格) | BEDummyable | ✅ | VacuumRefineryRecipe |
| 气体离心机 GasCent | BlockGasCent (4格) | BEDummyable | ✅ | GasCentrifugeRecipe |
| 低温蒸馏器 CryoDistill | BlockCryoDistill (5格, offset 3) | BEDummyable | ✅ | CryoRecipe |
| 斯特林机 Stirling | BlockStirling (2格, offset 1) | BEDummyable | 无（右键装齿轮） | 无 |
| 锯木机 Sawmill | BlockSawmill (2格, offset 1) | BEDummyable | 无 | 无（硬编码原木→木板） |
| 内燃机 CombustionEngine | BlockCombustionEngine (2格) | BEDummyable | ✅ | 无（FT_Combustible 燃烧） |
| 增殖反应堆 BreedingReactor | BlockBreedingReactor (3格) | BEDummyable | ✅ | 复用 BreederRecipes |

### 新增配方类（5 个，均继承 AutoRecipe）
- `MixerRecipe`（2 流体+固体→流体）、`ReformingRecipe`（流体→3 流体）
- `VacuumRefineryRecipe`（流体→4 流体）、`CryoRecipe`（流体→4 流体）、`GasCentrifugeRecipe`（流体→物品列表，简化掉 PseudoFluidType）
- ModRecipes 注册 5 个类型 + datagen providers（Mixer/Reforming/VacuumRefinery/Cryo/GasCentrifuge）

### 新增物品
- `catalytic_converter`（催化重整器催化剂）

### 顺带修复
- **HBMFluids.SOURGAS 声明未初始化**（static 引用 NPE）→ 补初始化（sourgas，GASEOUS+腐蚀）

### 注册与命名
- `MACHINE_REACTOR_BREEDING` 注册名用 `machine_reactor`（参考名），避免与既有 `machine_reactor_breeding`（研究增殖堆）冲突
- OBJ 模型复制 10 个到 `models/block/*/`，贴图全部已有
- MultiblockData.mapping 新增 10 条

### 经验（沿用 core 基类）
- 发电机器（CombustionEngine/Stirling）energyContainer 用 `BasicEnergyHandler.OUTPUT` + `TransmitUtils.outputOnly`
- FT_Combustible 在**旧版** ExtendedFluidType（FluidTrait 体系），非新 HbmFluidType（IFluidTrait）——用 `stack.getFluid().getFluidType()` + `instanceof ExtendedFluidType`
- Forge 1.20.1 `FluidStack.getFluid()` 返回 `Fluid`，要 `.getFluidType()` 才是 `FluidType`
- 清空流体罐用 `FluidStack.EMPTY`（不是 FluidTank.EMPTY）
- ItemStack.is() 接受 Item/TagKey，不接受 Block（用 `Blocks.XXX.asItem()` 或 tag）

### 测试
- MachineGameTests 新增 10 个 GameTest（placeBlastFurnace/placeMixer/placeCatalyticReformer/placeVacuumDistill/placeGasCent/placeCryoDistill/placeCombustionEngine/placeStirling/placeSawmill/placeBreedingReactor）

### 已移植机器累计：30台

---

## 第 37 批：SteamEngine + DeuteriumExtractor + Decon + CatalyticCracker + HeatBoiler + FractionTower + Alkylation + BigAssTank + SolarBoiler + Siren（2026-08-17）

### 已移植机器：10 台（全部 core 基类体系，均无 GUI）

| 机器 | Block | BE 基类 | 配方/逻辑 |
|---|---|---|---|
| 蒸汽机 SteamEngine | BlockSteamEngine (2格) | BEDummyable | FT_Coolable 蒸汽→废蒸汽+HE |
| 氘提取器 DeuteriumExtractor | BlockDeuteriumExtractor (单方块) | BEMachineBase | 水→重水 |
| 净化装置 Decon | BlockDecon (单方块) | BEMachineBase | 清除生物辐射 |
| 催化裂化塔 CatalyticCracker | BlockCatalyticCracker (4格) | BEDummyable | 复用 CrackingRecipes（新工程已有） |
| 热锅炉 HeatBoiler | BlockHeatBoiler (4格) | BEDummyable | FT_Heatable 水→蒸汽 |
| 分馏塔 FractionTower | BlockFractionTower (3格) | BEDummyable | FractionRecipe（新移植） |
| 烷基化 Alkylation | BlockAlkylation (4格) | BEDummyable | AlkylationRecipe（新移植） |
| 大型储罐 BigAssTank | BlockBigAssTank (6格) | BEDummyable | 1600万 mB 流体储罐 |
| 太阳能锅炉 SolarBoiler | BlockSolarBoiler (3格) | BEDummyable | 太阳功率水→蒸汽 |
| 警报器 Siren | BlockSiren (单方块) | BEMachineBase | 红石触发声音（简化，无磁带） |

### 新增配方类（2 个 AutoRecipe）
- `FractionRecipe`（流体→2 流体）、`AlkylationRecipe`（流体+酸→2 流体）
- ModRecipes 注册 FRACTION/ALKYLATION + datagen providers

### 注册与命名
- 7 台多方块注册 + MultiblockData mapping（steam_engine/catalytic_cracker/heat_boiler/fraction_tower/alkylation/bigass_tank/solar_boiler）
- 3 台单方块（deuterium/decon/siren）用 BlockMachineBase + mSmp 模型
- OBJ 复制：steam_engine/fraction_tower/catalytic_cracker/alkylation_unit/bigasstank/solar_boiler/boiler
- 修复 MACHINE_STEAM_ENGINE 注册名（用直接字段，不用 _R 变体）

### 顺带修复
- **ModItems 去重脚本丢失 16 个 INGOT 注册**（INGOT_U235/U238/PU*/AM*/TH232/URANIUM 等变成 null stub）→ 用 HEAD 完整注册行恢复 stub
- 去重后 compileJava + runData 全部通过

### 测试
- MachineGameTests 新增 10 个 GameTest（placeSteamEngine/placeDeuteriumExtractor/placeDecon/placeCatalyticCracker/placeHeatBoiler/placeFractionTower/placeAlkylation/placeBigAssTank/placeSolarBoiler/placeSiren）

### 已移植机器累计：40台
