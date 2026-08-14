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
