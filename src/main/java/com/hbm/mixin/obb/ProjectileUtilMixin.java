package com.hbm.mixin.obb;

import com.hbm.core.contents.obb.OBB;
import com.hbm.core.contents.obb.OBBEntity;
import com.hbm.core.contents.obb.OBBHitter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * 覆写 {@link ProjectileUtil#getEntityHitResult} 使其支持 OBB 精确射线检测。
 *
 * <p>原版方法仅基于 AABB 做射线-实体碰撞检测。本 Mixin 在原版检测结果的基础上：
 * <ol>
 *   <li>遍历搜索范围内的所有 OBBEntity</li>
 *   <li>对每个 OBB 做精确的 clip（射线-OBB 相交）测试</li>
 *   <li>找到最近的 OBB 命中位置</li>
 *   <li>通过 {@link OBBHitter} 记录命中的 OBB 部件</li>
 * </ol>
 *
 * <p>如果 OBB 命中比原版 AABB 命中更近，则覆写返回值为 OBB 命中的结果。
 *
 * <p><b>注意：</b>所有 COLLISION 类型的 OBB 不参与射线命中判定，
 * 仅 BODY、TURRET、WHEEL_LEFT 等可命中部件参与。
 */
@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {

    /**
     * 在投射物实体搜索的 {@code getEntityHitResult} 尾部注入 OBB 射线检测。
     *
     * <p>此为主要的投射物命中入口，例如箭矢、子弹等使用。
     */
    @Inject(method = "getEntityHitResult(Lnet/minecraft/world/level/Level;"
            + "Lnet/minecraft/world/entity/Entity;"
            + "Lnet/minecraft/world/phys/Vec3;"
            + "Lnet/minecraft/world/phys/Vec3;"
            + "Lnet/minecraft/world/phys/AABB;"
            + "Ljava/util/function/Predicate;"
            + "F)"
            + "Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At("TAIL"), cancellable = true)
    private static void onGetEntityHitResult(Level pLevel, Entity pProjectile,
                                              Vec3 pStartVec, Vec3 pEndVec,
                                              AABB pBoundingBox,
                                              Predicate<Entity> pFilter,
                                              float pInflationAmount,
                                              CallbackInfoReturnable<EntityHitResult> cir) {
        EntityHitResult vanillaResult = cir.getReturnValue();
        double pDistance = pStartVec.distanceToSqr(pEndVec);

        Vector3d startVec = OBB.vec3ToVector3d(pStartVec);
        Vector3d endVec = OBB.vec3ToVector3d(pEndVec);

        EntityHitResult bestHit = null;
        double bestDistanceSqr = Double.MAX_VALUE;
        OBB.Part bestPart = null;

        // 扩大搜索范围以包含 OBB 实体
        for (Entity entity : pLevel.getEntities(pProjectile,
                pBoundingBox.inflate(8.0), pFilter)) {

            if (!(entity instanceof OBBEntity obbEntity) || obbEntity.enableAABB()) {
                continue;
            }

            // 跳过投射物的主人和其骑乘者
            if (entity.getPassengers().contains(
                    pProjectile.getRootVehicle() != null
                        ? pProjectile.getRootVehicle().getFirstPassenger()
                        : null)) {
                continue;
            }

            List<OBB> obbList = obbEntity.getOBBs();
            for (OBB obb : obbList) {
                // COLLISION 类型不参与射线命中判定
                if (obb.part == OBB.Part.COLLISION) continue;

                // 按实体拾取半径膨胀 OBB
                OBB inflatedObb = obb.inflate(entity.getPickRadius() * 2);
                Optional<Vector3d> optional = inflatedObb.clip(startVec, endVec);

                if (obb.contains(pStartVec)) {
                    // 射线起点在 OBB 内部 —— 立即返回此实体
                    if (pDistance >= 0) {
                        Vec3 hitPos = OBB.vector3dToVec3(optional.orElse(startVec));
                        EntityHitResult hitResult = new EntityHitResult(entity, hitPos);
                        OBBHitter hitter = OBBHitter.getInstance(pProjectile);
                        hitter.obb$setCurrentHitPart(obb.part);
                        cir.setReturnValue(hitResult);
                        return;
                    }
                } else if (optional.isPresent()) {
                    Vector3d hitVec = optional.get();
                    Vec3 hitPos = OBB.vector3dToVec3(hitVec);
                    double d1 = pStartVec.distanceToSqr(hitPos);

                    if ((d1 < pDistance || pDistance == 0) && d1 < bestDistanceSqr) {
                        bestDistanceSqr = d1;
                        bestHit = new EntityHitResult(entity, hitPos);
                        bestPart = obb.part;
                    }
                }
            }
        }

        // 如果有 OBB 命中，覆写返回值
        if (bestHit != null) {
            OBBHitter hitter = OBBHitter.getInstance(pProjectile);
            hitter.obb$setCurrentHitPart(bestPart);
            cir.setReturnValue(bestHit);
        }
    }

    /**
     * 在玩家视线搜索的 {@code getEntityHitResult} 尾部注入 OBB 检测。
     *
     * <p>此重载用于玩家用十字准星选取实体（如右键交互）。
     */
    @Inject(method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;"
            + "Lnet/minecraft/world/phys/Vec3;"
            + "Lnet/minecraft/world/phys/Vec3;"
            + "Lnet/minecraft/world/phys/AABB;"
            + "Ljava/util/function/Predicate;"
            + "D)"
            + "Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At("TAIL"), cancellable = true)
    private static void onGetEntityHitResult(Entity pShooter, Vec3 pStartVec, Vec3 pEndVec,
                                              AABB pBoundingBox,
                                              Predicate<Entity> pFilter, double pDistance,
                                              CallbackInfoReturnable<EntityHitResult> cir) {
        // 获取原版结果
        EntityHitResult vanillaResult = cir.getReturnValue();
        Level level = pShooter.level();

        Vector3d startVec = OBB.vec3ToVector3d(pStartVec);
        Vector3d endVec = OBB.vec3ToVector3d(pEndVec);

        EntityHitResult bestHit = null;
        double bestDistanceSqr = Double.MAX_VALUE;

        List<Entity> entities = level.getEntities(pShooter,
            pBoundingBox.inflate(8.0), pFilter);

        for (Entity entity : entities) {
            if (!(entity instanceof OBBEntity obbEntity) || obbEntity.enableAABB()) {
                continue;
            }

            // 跳过自身及其骑乘者
            if (entity.getPassengers().contains(pShooter)) {
                continue;
            }

            for (OBB obb : obbEntity.getOBBs()) {
                if (obb.part == OBB.Part.COLLISION) continue;

                OBB inflatedObb = obb.inflate(entity.getPickRadius() * 2);
                Optional<Vector3d> optional = inflatedObb.clip(startVec, endVec);

                if (obb.contains(pStartVec)) {
                    if (pDistance >= 0) {
                        Vec3 hitPos = OBB.vector3dToVec3(optional.orElse(startVec));
                        cir.setReturnValue(new EntityHitResult(entity, hitPos));
                        return;
                    }
                } else if (optional.isPresent()) {
                    Vector3d hitVec = optional.get();
                    Vec3 hitPos = OBB.vector3dToVec3(hitVec);
                    double d1 = pStartVec.distanceToSqr(hitPos);

                    if ((d1 < pDistance || pDistance == 0) && d1 < bestDistanceSqr) {
                        // 跳过自身骑乘的载具（除非 pDistance == 0）
                        if (entity.getRootVehicle() == pShooter.getRootVehicle()
                                && !entity.canRiderInteract()) {
                            if (pDistance == 0) {
                                bestDistanceSqr = d1;
                                bestHit = new EntityHitResult(entity, hitPos);
                            }
                        } else {
                            bestDistanceSqr = d1;
                            bestHit = new EntityHitResult(entity, hitPos);
                        }
                    }
                }
            }
        }

        if (bestHit != null) {
            cir.setReturnValue(bestHit);
        }
    }
}
