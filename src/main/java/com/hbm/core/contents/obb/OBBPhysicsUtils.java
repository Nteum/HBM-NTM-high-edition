package com.hbm.core.contents.obb;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

/**
 * OBB 物理工具类 —— 处理 OBB-vs-世界、OBB-vs-实体碰撞的求解。
 *
 * <p>包含以下核心功能：
 * <ul>
 *   <li>OBB 与世界的碰撞求解（Y→X→Z 轴分离 SAT 裁剪）</li>
 *   <li>实体与 OBB 碰撞箱载具的交互（站顶/推出/动量传递）</li>
 *   <li>OBB 着地检测 / 底面支撑比例检测</li>
 *   <li>多个 OBB 的组合包围 AABB 计算</li>
 * </ul>
 */
public final class OBBPhysicsUtils {

    private OBBPhysicsUtils() {} // 工具类禁止实例化

    /** 忽略小于此值的穿透量，抑制浮点噪声 */
    private static final double MIN_PENETRATION = 0.005;

    /** 沿切向（平行于表面）的基础允许穿透量 */
    private static final double MAX_PENETRATION = 0.1;

    /**
     * 将 OBB 与世界的碰撞沿 Y → X → Z 轴使用 SAT 裁剪进行求解。
     *
     * <p>这是 OBB 物理的核心方法，在每 tick 的 {@code travel()} 或 {@code tick()} 中调用。
     * 思路是将 OBB 沿 Y 轴方向平移后与候选方块/实体的 AABB 做 SAT 碰撞测试，
     * 根据 MTV 修正位移，然后依次处理 X 轴和 Z 轴。
     *
     * <p>性能优化：
     * <ul>
     *   <li>方块碰撞数据在一 tick 内缓存为平铺的 double 数组</li>
     *   <li>OBB 的朝向轴一次推导，三个轴通道复用</li>
     *   <li>在高度图保证无碰撞的列上跳过方块碰撞形状分解</li>
     * </ul>
     *
     * @param level    当前世界
     * @param obb      用于碰撞检测的 OBB（通常只有 {@link OBB.Part#COLLISION} 类型）
     * @param movement 预期运动向量（已包含重力）
     * @return 裁剪后的运动向量
     */
    public static Vec3 resolveObbWorldCollision(Level level, OBB obb, Vec3 movement) {
        if (movement.lengthSqr() < 1e-7) return movement;

        // 推导 OBB 的世界空间轴（朝向不变，平移不影响轴方向）
        Vector3d[] axes = obb.getAxes();

        // 构建搜索 AABB：OBB 的世界包围盒 + 运动向量扩展 + 步高容差
        AABB searchBox = OBB.getWorldAABB(obb)
            .expandTowards(movement)
            .inflate(0.5)
            .expandTowards(0.0, 0.6, 0.0); // stepHeight + 少量容差

        // 收集候选碰撞 AABB（方块 + 实体）
        List<AABB> allAabbs = new ArrayList<>();

        // 方块碰撞（VoxelShape → AABB 列表）
        for (var shape : level.getBlockCollisions(null, searchBox)) {
            shape.forAllBoxes((x0, y0, z0, x1, y1, z1) -> {
                allAabbs.add(new AABB(x0, y0, z0, x1, y1, z1));
            });
        }

        // 实体碰撞
        for (var shape : level.getEntityCollisions(null, searchBox)) {
            shape.forAllBoxes((x0, y0, z0, x1, y1, z1) -> {
                allAabbs.add(new AABB(x0, y0, z0, x1, y1, z1));
            });
        }

        if (allAabbs.isEmpty()) return movement;

        double rx = movement.x;
        double ry = movement.y;
        double rz = movement.z;

        // ============ Y 轴通道 ============
        // 将 OBB 平移到 (0, ry, 0) 并做 SAT 测试
        {
            AABB obbAabb = OBB.getTranslatedWorldAABB(obb, axes, new Vec3(0.0, ry, 0.0));
            Vector3d testCenter = new Vector3d(obb.center.x, obb.center.y + ry, obb.center.z);
            for (AABB aabb : allAabbs) {
                // 粗筛：跳过 XZ 无重叠的 AABB（非求解轴）
                if (obbAabb.maxX <= aabb.minX || obbAabb.minX >= aabb.maxX) continue;
                if (obbAabb.maxZ <= aabb.minZ || obbAabb.minZ >= aabb.maxZ) continue;

                Vector3d mtv = OBB.computeObbAabbMtv(testCenter, axes, obb.extents, aabb);
                if (mtv == null) continue;
                if (Math.abs(mtv.y) < MIN_PENETRATION) continue;

                double mtvLen = Math.sqrt(mtv.x * mtv.x + mtv.y * mtv.y + mtv.z * mtv.z);
                // nY: MTV 在 Y 方向的分量 → 1=平地接触（严格容差），→ 0=墙壁接触（宽松容差）
                double nY = Math.abs(mtv.y) / mtvLen;
                double effectiveMaxPen = MAX_PENETRATION * (1.0 - nY * 0.85);

                if (ry > 0 && mtv.y < 0) {
                    double excess = -mtv.y - effectiveMaxPen;
                    if (excess > 0) ry = Math.max(0.0, ry - excess * nY);
                } else if (ry < 0 && mtv.y > 0) {
                    double excess = mtv.y - effectiveMaxPen;
                    if (excess > 0) ry = Math.min(0.0, ry + excess * nY);
                } else if (ry <= 0 && mtv.y > 0) {
                    if (mtv.y > effectiveMaxPen) {
                        ry = Math.min(0.0, ry + (mtv.y - effectiveMaxPen) * 0.5 * nY);
                    }
                }
            }
        }

        // ============ X 轴通道 ============
        // 将 OBB 平移到 (rx, ry, 0) 并做 SAT 测试
        {
            AABB obbAabb = OBB.getTranslatedWorldAABB(obb, axes, new Vec3(rx, ry, 0.0));
            Vector3d testCenter = new Vector3d(obb.center.x + rx, obb.center.y + ry, obb.center.z);
            for (AABB aabb : allAabbs) {
                if (obbAabb.maxY <= aabb.minY || obbAabb.minY >= aabb.maxY) continue;
                if (obbAabb.maxZ <= aabb.minZ || obbAabb.minZ >= aabb.maxZ) continue;

                Vector3d mtv = OBB.computeObbAabbMtv(testCenter, axes, obb.extents, aabb);
                if (mtv == null) continue;
                if (Math.abs(mtv.x) < MIN_PENETRATION) continue;

                double mtvLen = Math.sqrt(mtv.x * mtv.x + mtv.y * mtv.y + mtv.z * mtv.z);
                double nX = Math.abs(mtv.x) / mtvLen;
                double effectiveMaxPen = MAX_PENETRATION * (1.0 - nX);

                if (rx > 0 && mtv.x < 0) {
                    double excess = -mtv.x - effectiveMaxPen;
                    if (excess > 0) rx = Math.max(0.0, rx - excess * nX);
                } else if (rx < 0 && mtv.x > 0) {
                    double excess = mtv.x - effectiveMaxPen;
                    if (excess > 0) rx = Math.min(0.0, rx + excess * nX);
                }
            }
        }

        // ============ Z 轴通道 ============
        // 将 OBB 平移到 (rx, ry, rz) 并做 SAT 测试
        {
            AABB obbAabb = OBB.getTranslatedWorldAABB(obb, axes, new Vec3(rx, ry, rz));
            Vector3d testCenter = new Vector3d(obb.center.x + rx, obb.center.y + ry, obb.center.z + rz);
            for (AABB aabb : allAabbs) {
                if (obbAabb.maxX <= aabb.minX || obbAabb.minX >= aabb.maxX) continue;
                if (obbAabb.maxY <= aabb.minY || obbAabb.minY >= aabb.maxY) continue;

                Vector3d mtv = OBB.computeObbAabbMtv(testCenter, axes, obb.extents, aabb);
                if (mtv == null) continue;
                if (Math.abs(mtv.z) < MIN_PENETRATION) continue;

                double mtvLen = Math.sqrt(mtv.x * mtv.x + mtv.y * mtv.y + mtv.z * mtv.z);
                double nZ = Math.abs(mtv.z) / mtvLen;
                double effectiveMaxPen = MAX_PENETRATION * (1.0 - nZ);

                if (rz > 0 && mtv.z < 0) {
                    double excess = -mtv.z - effectiveMaxPen;
                    if (excess > 0) rz = Math.max(0.0, rz - excess * nZ);
                } else if (rz < 0 && mtv.z > 0) {
                    double excess = mtv.z - effectiveMaxPen;
                    if (excess > 0) rz = Math.min(0.0, rz + excess * nZ);
                }
            }
        }

        return new Vec3(rx, ry, rz);
    }

    // ========================================================================
    // 实体-OBB 碰撞交互
    // ========================================================================

    /**
     * 处理单个实体与 OBB 碰撞箱之间的交互。
     *
     * <p>分两个阶段：
     * <ul>
     *   <li><b>阶段 A</b>：当前帧已陷入 OBB → 沿 MTV 推出（纯位置修正）</li>
     *   <li><b>阶段 B</b>：deltaMovement 会导致穿入 → 截断速度分量</li>
     * </ul>
     *
     * <p>实体在 OBB 顶部 → 站在表面跟随载具移动；
     * 实体在 OBB 侧面/下方 → 推出 + 清零朝向该 OBB 的速度分量。
     *
     * @param obbList 碰撞 OBB 列表（不含 COLLISION 和 INTERACTIVE 类型）
     * @param entity  被碰撞的实体
     */
    public static void handleEntityObbCollision(List<OBB> obbList, Entity entity) {
        if (entity.noPhysics) return;

        Vec3 movement = entity.getDeltaMovement();
        double minPenetration = 0.01;

        // ---- 阶段 A：当前帧已陷入 → 迭代推出 ----
        for (int iter = 0; iter < 4; iter++) {
            double bestMtvX = 0, bestMtvY = 0, bestMtvZ = 0;
            double bestLenSq = 0;
            boolean bestOnTop = false;

            for (OBB obb : obbList) {
                if (obb.part == OBB.Part.COLLISION || obb.part == OBB.Part.INTERACTIVE) continue;
                Vector3d mtv = OBB.computeObbAabbMtv(obb, entity.getBoundingBox());
                if (mtv == null) continue;

                double curLenSq = mtv.x * mtv.x + mtv.y * mtv.y + mtv.z * mtv.z;
                if (curLenSq < minPenetration * minPenetration) continue;
                if (curLenSq > bestLenSq) {
                    bestLenSq = curLenSq;
                    bestMtvX = mtv.x; bestMtvY = mtv.y; bestMtvZ = mtv.z;
                    bestOnTop = -mtv.y / Math.sqrt(curLenSq) > 0.5;
                }
            }

            if (bestLenSq == 0) break; // 无碰撞

            double bestLen = Math.sqrt(bestLenSq);
            double pushNx = -bestMtvX / bestLen;
            double pushNy = -bestMtvY / bestLen;
            double pushNz = -bestMtvZ / bestLen;
            double extra = 0.02;

            double pushX = -bestMtvX + pushNx * extra;
            double pushY = -bestMtvY + pushNy * extra;
            double pushZ = -bestMtvZ + pushNz * extra;

            // 站在地面上时不允许向下推
            if (pushY < 0 && entity.onGround()) pushY = 0.0;

            if (bestOnTop) {
                // 实体站在 OBB 顶部 → 跟随 OBB 移动
                entity.setPos(entity.getX() + pushX, entity.getY() + pushY, entity.getZ() + pushZ);
                entity.setDeltaMovement(Vec3.ZERO);
                entity.setOnGround(true);
                entity.fallDistance = 0f;
                return; // 站顶后不需要再做后续推出
            }

            // 推出并清零朝向该 OBB 的速度分量
            entity.setPos(entity.getX() + pushX, entity.getY() + pushY, entity.getZ() + pushZ);
            double velToward = movement.x * pushNx + movement.y * pushNy + movement.z * pushNz;
            if (velToward > 0) {
                entity.setDeltaMovement(new Vec3(
                    movement.x - pushNx * velToward,
                    movement.y - pushNy * velToward,
                    movement.z - pushNz * velToward
                ));
            }
        }

        // ---- 阶段 B：deltaMovement 会导致穿入 → 截断速度 ----
        double clampedDx = movement.x;
        double clampedDy = movement.y;
        double clampedDz = movement.z;
        boolean standingOnObb = false;
        boolean hasCollision = false;

        for (int iter = 0; iter < 4; iter++) {
            boolean clippedAny = false;
            for (OBB obb : obbList) {
                if (obb.part == OBB.Part.COLLISION || obb.part == OBB.Part.INTERACTIVE) continue;

                AABB probeAabb = entity.getBoundingBox().move(clampedDx, clampedDy, clampedDz);
                Vector3d mtv = OBB.computeObbAabbMtv(obb, probeAabb);
                if (mtv == null) continue;

                double mtvLenSq = mtv.x * mtv.x + mtv.y * mtv.y + mtv.z * mtv.z;
                if (mtvLenSq < minPenetration * minPenetration) continue;

                double mtvLen = Math.sqrt(mtvLenSq);
                double nx = -mtv.x / mtvLen;
                double ny = -mtv.y / mtvLen;
                double nz = -mtv.z / mtvLen;

                if (mtvLen > 0.05) {
                    double velIntoObb = clampedDx * nx + clampedDy * ny + clampedDz * nz;
                    if (velIntoObb > 0) {
                        double oldDy = clampedDy;
                        clampedDx -= nx * velIntoObb;
                        clampedDy -= ny * velIntoObb;
                        clampedDz -= nz * velIntoObb;
                        // 不能因截断而增加下落速度
                        if (clampedDy < oldDy) clampedDy = oldDy;
                        clippedAny = true;
                    }
                }

                if (ny > 0.5) standingOnObb = true;
                hasCollision = true;
            }
            if (!clippedAny) break;
        }

        if (!hasCollision) return;

        if (standingOnObb) {
            entity.setDeltaMovement(Vec3.ZERO);
            entity.setOnGround(true);
            entity.fallDistance = 0f;
        } else {
            entity.setDeltaMovement(new Vec3(clampedDx, clampedDy, clampedDz));
        }
    }

    // ========================================================================
    // OBB 着地检测
    // ========================================================================

    /**
     * 检查 OBB 是否接触地面。
     *
     * <p>将 OBB 向下偏移微小距离（0.02），然后在世界 AABB 范围内
     * 检查与方块碰撞形状的 OBB-AABB 碰撞。
     *
     * @param level 当前世界
     * @param obb   要检测的 OBB
     * @return 如果 OBB 接触地面返回 {@code true}
     */
    public static boolean isObbOnGround(Level level, OBB obb) {
        // 向下偏移测试 OBB
        OBB testObb = obb.move(new Vec3(0.0, -0.02, 0.0));
        Vector3d[] axes = testObb.getAxes();
        Vector3d ext = testObb.extents;

        // 计算测试 OBB 的世界包围 AABB 并稍微扩大以包含方块碰撞形状
        double halfX = Math.abs(axes[0].x) * ext.x + Math.abs(axes[1].x) * ext.y + Math.abs(axes[2].x) * ext.z;
        double halfY = Math.abs(axes[0].y) * ext.x + Math.abs(axes[1].y) * ext.y + Math.abs(axes[2].y) * ext.z;
        double halfZ = Math.abs(axes[0].z) * ext.x + Math.abs(axes[1].z) * ext.y + Math.abs(axes[2].z) * ext.z;

        AABB searchAABB = new AABB(
            testObb.center.x - halfX - 0.15, testObb.center.y - halfY - 0.15, testObb.center.z - halfZ - 0.15,
            testObb.center.x + halfX + 0.15, testObb.center.y + halfY + 0.15, testObb.center.z + halfZ + 0.15
        );

        for (BlockPos pos : BlockPos.betweenClosed(
            (int) Math.floor(searchAABB.minX), (int) Math.floor(searchAABB.minY), (int) Math.floor(searchAABB.minZ),
            (int) Math.floor(searchAABB.maxX), (int) Math.floor(searchAABB.maxY), (int) Math.floor(searchAABB.maxZ))) {

            var state = level.getBlockState(pos);
            if (state.isAir()) continue;
            var shape = state.getCollisionShape(level, pos);
            if (shape.isEmpty()) continue;

            for (AABB aabb : shape.toAabbs()) {
                AABB worldAabb = aabb.move(pos.getX(), pos.getY(), pos.getZ());
                if (OBB.isColliding(testObb, worldAabb)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查 OBB 底面支撑比例 —— 用于防止载具步进下落时卡入小坑洞。
     *
     * <p>采样底面 5 个点（四角 + 中心），检查各点正下方是否有方块支撑。
     *
     * @param level 当前世界
     * @param obb   位于目标位置的 OBB
     * @return 长度为 2 的数组：[0]=支撑比例 (0.0~1.0), [1]=需要的向上修正量
     */
    public static double[] checkBottomSupportRatio(Level level, OBB obb) {
        Vector3d[] axes = obb.getAxes();
        Vector3d center = obb.center;
        double ex = obb.extents.x;
        double ey = obb.extents.y;
        double ez = obb.extents.z;

        // 底面 5 个采样点：四角 + 中心
        double[][] offsets = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}, {0, 0}};
        double closeThreshold = 1.5; // 方块表面 0.5 格内视为"接触地表"
        int onSurfaceCount = 0;
        double maxPenetration = 0.0; // 采样点低于方块表面的最大深度

        for (double[] offset : offsets) {
            double fx = offset[0], fz = offset[1];
            double lx = fx * ex, lz = fz * ez;

            // 计算底面采样点的世界坐标: center + lx*axis0 + (-ey)*axis1 + lz*axis2
            double wx = center.x + axes[0].x * lx + axes[1].x * (-ey) + axes[2].x * lz;
            double wy = center.y + axes[0].y * lx + axes[1].y * (-ey) + axes[2].y * lz;
            double wz = center.z + axes[0].z * lx + axes[1].z * (-ey) + axes[2].z * lz;

            BlockPos blockPos = BlockPos.containing(wx, wy, wz);
            BlockPos blockPosBelow = BlockPos.containing(wx, wy - 0.02, wz);

            var state = level.getBlockState(blockPosBelow);
            var shape = state.getCollisionShape(level, blockPosBelow);

            if (shape.isEmpty()) {
                state = level.getBlockState(blockPos);
                shape = state.getCollisionShape(level, blockPos);
            }

            if (!shape.isEmpty()) {
                BlockPos shapeBlockPos = !level.getBlockState(blockPosBelow)
                    .getCollisionShape(level, blockPosBelow).isEmpty() ? blockPosBelow : blockPos;
                double blockTopY = shapeBlockPos.getY() + shape.max(Direction.Axis.Y);
                double dist = wy - blockTopY; // >0=在地表上方, <0=陷入地表

                if (Math.abs(dist) <= closeThreshold) {
                    onSurfaceCount++;
                    if (dist < 0) {
                        maxPenetration = Math.max(maxPenetration, -dist);
                    }
                }
            }
        }

        return new double[] {
            onSurfaceCount / (double) offsets.length,  // 支撑比例
            maxPenetration                               // 需要的向上修正量
        };
    }

    // ========================================================================
    // 组合 AABB 计算
    // ========================================================================

    /**
     * 计算列表中所有 OBB 的最小外接 AABB（投影法，避免逐顶点计算）。
     *
     * <p>世界坐标轴上的半长 = Σ(|localAxis_i · worldAxis| * extent_i)。
     * 对于每个 OBB 只需计算一次轴投影即可得到 AABB 范围，无需遍历 8 个顶点。
     *
     * @param obbList    OBB 列表
     * @param fallbackAabb 当列表为空时返回的默认 AABB
     * @return 所有 OBB 的组合外接 AABB
     */
    public static AABB calculateCombinedAABB(List<OBB> obbList, AABB fallbackAabb) {
        if (obbList.isEmpty()) return fallbackAabb;

        Vector3d min = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        Vector3d max = new Vector3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);

        // 复用单个 ThreadLocal 轴缓冲区
        Vector3d[] axes = OBB.AXES_A.get();

        for (OBB obb : obbList) {
            obb.getAxesInto(axes);
            Vector3d c = obb.center;
            Vector3d e = obb.extents;

            double halfX = Math.abs(axes[0].x) * e.x + Math.abs(axes[1].x) * e.y + Math.abs(axes[2].x) * e.z;
            double halfY = Math.abs(axes[0].y) * e.x + Math.abs(axes[1].y) * e.y + Math.abs(axes[2].y) * e.z;
            double halfZ = Math.abs(axes[0].z) * e.x + Math.abs(axes[1].z) * e.y + Math.abs(axes[2].z) * e.z;

            if (c.x - halfX < min.x) min.x = c.x - halfX;
            if (c.y - halfY < min.y) min.y = c.y - halfY;
            if (c.z - halfZ < min.z) min.z = c.z - halfZ;
            if (c.x + halfX > max.x) max.x = c.x + halfX;
            if (c.y + halfY > max.y) max.y = c.y + halfY;
            if (c.z + halfZ > max.z) max.z = c.z + halfZ;
        }

        return new AABB(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    // ========================================================================
    // 碰撞弹跳
    // ========================================================================

    /**
     * 水平碰撞反弹 —— 在 X 或 Z 方向碰墙时削减对应方向的速度。
     *
     * @param entity    碰撞的实体
     * @param direction 碰撞方向
     */
    public static void bounceHorizontal(Entity entity, Direction direction) {
        Vec3 delta = entity.getDeltaMovement();
        switch (direction.getAxis()) {
            case X -> entity.setDeltaMovement(delta.multiply(0.8, 0.99, 0.99));
            case Z -> entity.setDeltaMovement(delta.multiply(0.99, 0.99, 0.8));
        }
    }

    /**
     * 垂直碰撞反弹 —— 在 Y 方向碰地/顶时反转 Y 速度。
     *
     * @param entity    碰撞的实体
     * @param direction 碰撞方向
     */
    public static void bounceVertical(Entity entity, Direction direction) {
        if (direction.getAxis() == Direction.Axis.Y) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.9, -0.8, 0.9));
        }
    }
}
