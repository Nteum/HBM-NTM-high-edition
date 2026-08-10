package com.hbm.core.contents.obb;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * OBB 射线检测工具类 —— 在 OBB 和 AABB 混合实体上执行精确的射线命中判定。
 *
 * <p>原版 Minecraft 使用 {@link ProjectileUtil#getEntityHitResult} 进行射线检测，
 * 但那只支持 AABB。当实体使用 OBB 碰撞箱时，需要基于 OBB 的射线相交测试
 * 来确定精确命中位置。
 *
 * <p>核心流程：
 * <ol>
 *   <li>用扩大的 AABB 做粗筛收集候选实体</li>
 *   <li>对每个候选实体，如果它是 OBBEntity，遍历其 OBB 做精确 clip 测试</li>
 *   <li>找到最近的命中位置和对应的 OBB 部件</li>
 * </ol>
 */
public final class OBBRayTrace {

    private OBBRayTrace() {} // 工具类禁止实例化

    /**
     * 在投射物的路径上执行 OBB + AABB 混合射线检测。
     *
     * <p>用扩大的包围盒收集候选实体后，对每个 OBBEntity 遍历其 OBB 做精确 clip 测试。
     * 优先使用非 COLLISION OBB 的命中，其次回退到 COLLISION OBB（覆盖大型载具间隙）。
     *
     * @param level       当前世界
     * @param projectile  投射物实体
     * @param startVec    射线起点
     * @param endVec      射线终点
     * @param boundingBox 原版用于收集实体的 AABB（会被扩大以覆盖 OBB 实体）
     * @param filter      实体过滤器
     * @param inflationAmount AABB 膨胀量
     * @return 命中结果，如果无命中返回 {@code null}
     */
    public static EntityHitResult getEntityHitResult(Level level, Entity projectile,
                                                      Vec3 startVec, Vec3 endVec,
                                                      AABB boundingBox,
                                                      Predicate<Entity> filter,
                                                      float inflationAmount) {
        // 先执行原版的 AABB 检测获取候选结果
        EntityHitResult vanillaResult = ProjectileUtil.getEntityHitResult(
            level, projectile, startVec, endVec, boundingBox, filter, inflationAmount);

        double pDistance = startVec.distanceToSqr(endVec);
        Vector3d startVecD = OBB.vec3ToVector3d(startVec);
        Vector3d endVecD = OBB.vec3ToVector3d(endVec);

        EntityHitResult bestHit = null;
        double bestDistanceSqr = Double.MAX_VALUE;
        OBB.Part bestPart = null;

        // 扩大搜索范围以包含 OBB 实体
        List<Entity> entities = level.getEntities(projectile,
            boundingBox.inflate(8.0), filter);

        for (Entity entity : entities) {
            if (!(entity instanceof OBBEntity obbEntity) || obbEntity.enableAABB()) {
                continue;
            }

            // 跳过投射物自身和其骑乘者
            if (projectile instanceof Projectile proj) {
                if (proj.getOwner() == entity || entity.getPassengers().contains(proj.getOwner())) {
                    continue;
                }
            }

            List<OBB> obbList = obbEntity.getOBBs();
            for (OBB obb : obbList) {
                // COLLISION 类型的 OBB 不参与射线命中判定
                if (obb.part == OBB.Part.COLLISION) continue;

                // 按实体拾取半径膨胀 OBB
                OBB inflatedObb = obb.inflate(entity.getPickRadius() * 2);
                Optional<Vector3d> optional = inflatedObb.clip(startVecD, endVecD);

                if (obb.contains(startVec)) {
                    // 射线起点在 OBB 内部 —— 立即返回
                    if (pDistance >= 0) {
                        Vec3 hitPos = OBB.vector3dToVec3(optional.orElse(startVecD));
                        EntityHitResult hitResult = new EntityHitResult(entity, hitPos);
                        OBBHitter hitter = OBBHitter.getInstance(projectile);
                        hitter.obb$setCurrentHitPart(obb.part);
                        return hitResult;
                    }
                } else if (optional.isPresent()) {
                    Vector3d hitVec = optional.get();
                    double d1 = startVec.distanceToSqr(OBB.vector3dToVec3(hitVec));
                    if ((d1 < pDistance || pDistance == 0) && d1 < bestDistanceSqr) {
                        bestDistanceSqr = d1;
                        bestHit = new EntityHitResult(entity, OBB.vector3dToVec3(hitVec));
                        bestPart = obb.part;
                    }
                }
            }
        }

        if (bestHit != null) {
            OBBHitter hitter = OBBHitter.getInstance(projectile);
            hitter.obb$setCurrentHitPart(bestPart);
            return bestHit;
        }

        // 无 OBB 命中时返回原版结果
        return vanillaResult;
    }

    /**
     * 对单个实体执行 OBB + AABB 混合碰撞检测，返回世界空间的命中位置。
     *
     * <p>优先使用非 COLLISION OBB 的命中，其次回退到 COLLISION OBB。
     * 适用于自定义投射物的命中判定。
     *
     * @param entity   目标实体
     * @param startVec 射线起点（世界空间）
     * @param endVec   射线终点（世界空间）
     * @return 世界空间的命中位置，如果无命中返回 {@code null}
     */
    public static Vec3 clipObb(Entity entity, Vec3 startVec, Vec3 endVec) {
        if (entity instanceof OBBEntity obbEntity && !obbEntity.enableAABB()) {
            double closestDistSqr = Double.MAX_VALUE;
            Vec3 closestHitPos = null;
            OBB.Part closestHitPart = null;
            Vec3 collisionHitPos = null;
            double collisionDistSqr = Double.MAX_VALUE;

            for (OBB obb : obbEntity.getOBBs()) {
                Optional<Vector3d> obbVecOpt = obb.clip(
                    OBB.vec3ToVector3d(startVec),
                    OBB.vec3ToVector3d(endVec));
                if (obbVecOpt.isEmpty()) continue;

                Vector3d obbVec = obbVecOpt.get();
                Vec3 hitPos = OBB.vector3dToVec3(obbVec);
                double distSqr = startVec.distanceToSqr(hitPos);

                if (obb.part == OBB.Part.COLLISION) {
                    if (distSqr < collisionDistSqr) {
                        collisionDistSqr = distSqr;
                        collisionHitPos = hitPos;
                    }
                } else {
                    if (distSqr < closestDistSqr) {
                        closestDistSqr = distSqr;
                        closestHitPos = hitPos;
                        closestHitPart = obb.part;
                    }
                }
            }

            // 优先使用非 COLLISION 命中，否则回退到 COLLISION
            if (closestHitPos != null) {
                return closestHitPos;
            }
            return collisionHitPos;
        }
        return null;
    }

    /**
     * 获取玩家视线范围内最近的 OBB。
     *
     * <p>先通过原版实体查找找到玩家正在看的实体，如果是 OBBEntity，
     * 则对其所有非 COLLISION OBB 做精确射线测试，返回最近的命中 OBB。
     *
     * @param player 执行视线检测的玩家
     * @param range  最大射线距离（方块数）
     * @return 最近的命中 OBB，如果无命中或实体使用 AABB 模式返回 {@code null}
     */
    public static OBB getLookingObb(Player player, double range) {
        // 使用原版方法找到玩家正在看的实体
        Vec3 eyePos = player.getEyePosition(1.0f);
        Vec3 viewVec = player.getViewVector(1.0f);
        Vec3 lookEnd = eyePos.add(viewVec.scale(range));

        // 用原版 getEntityHitResult 获取候选实体
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(
            player.level(), player, eyePos, lookEnd,
            player.getBoundingBox().expandTowards(viewVec.scale(range)).inflate(1.0),
            e -> !e.isSpectator() && e.isPickable(),
            0.0f);

        if (hit == null) return null;

        Entity lookingEntity = hit.getEntity();
        if (!(lookingEntity instanceof OBBEntity obbEntity) || obbEntity.enableAABB()) {
            return null;
        }

        // 精确 OBB 射线测试
        OBB closestOBB = null;
        double minDistanceSq = Double.MAX_VALUE;

        for (OBB obb : obbEntity.getOBBs()) {
            if (obb.part == OBB.Part.COLLISION) continue;
            Vec3 hitPos = OBB.rayIntersect(obb, eyePos, lookEnd);
            if (hitPos == null) continue;

            double distanceSq = eyePos.distanceToSqr(hitPos);
            if (distanceSq < minDistanceSq) {
                minDistanceSq = distanceSq;
                closestOBB = obb;
            }
        }
        return closestOBB;
    }

    /**
     * 在多个实体中查找射线上最近的 OBB 命中。
     *
     * <p>用于自定义投射物在路径上命中多个 OBB 实体的场景。
     *
     * @param entities 候选实体列表
     * @param startVec 射线起点
     * @param endVec   射线终点
     * @return 最近的命中结果，包含实体和命中位置；如果无命中返回 {@code null}
     */
    public static HitResult findClosestObbHit(List<Entity> entities, Vec3 startVec, Vec3 endVec) {
        double closestDistSqr = Double.MAX_VALUE;
        Entity closestEntity = null;
        Vec3 closestHitPos = null;
        OBB.Part closestPart = null;

        Vector3d startD = OBB.vec3ToVector3d(startVec);
        Vector3d endD = OBB.vec3ToVector3d(endVec);

        for (Entity entity : entities) {
            if (!(entity instanceof OBBEntity obbEntity) || obbEntity.enableAABB()) {
                continue;
            }

            for (OBB obb : obbEntity.getOBBs()) {
                if (obb.part == OBB.Part.COLLISION) continue;

                Optional<Vector3d> hitOpt = obb.clip(startD, endD);
                if (hitOpt.isPresent()) {
                    Vec3 hitPos = OBB.vector3dToVec3(hitOpt.get());
                    double distSqr = startVec.distanceToSqr(hitPos);
                    if (distSqr < closestDistSqr) {
                        closestDistSqr = distSqr;
                        closestEntity = entity;
                        closestHitPos = hitPos;
                        closestPart = obb.part;
                    }
                }
            }
        }

        if (closestEntity != null) {
            return new HitResult(closestEntity, closestHitPos, closestPart);
        }
        return null;
    }

    /**
     * OBB 射线命中结果。
     */
    public static class HitResult {
        public final Entity entity;
        public final Vec3 hitPos;
        public final OBB.Part part;

        public HitResult(Entity entity, Vec3 hitPos, OBB.Part part) {
            this.entity = entity;
            this.hitPos = hitPos;
            this.part = part;
        }
    }
}
