package com.hbm.mixin.obb;

import com.hbm.core.contents.obb.OBB;
import com.hbm.core.contents.obb.OBBEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

/**
 * 修补 {@link Level#getEntities} 使其在 AABB 搜索中包含 OBB 实体。
 *
 * <p>原版 {@code getEntities} 仅基于 AABB 收集实体。当实体（如载具）使用
 * OBB 碰撞箱时，其实际碰撞体积可能与 AABB 有较大差异（尤其是旋转后的长条形状），
 * 导致投射物的 AABB 搜索遗漏这些实体。
 *
 * <p>此 Mixin 在原有返回结果的基础上，额外检查所有 OBBEntity：
 * 如果其 OBB 与搜索 AABB 有碰撞，且尚未在返回列表中，则将其加入。
 *
 * <p><b>注意：</b>此修补仅在调用者（pEntity）是投射物时生效，
 * 以最小化对所有 getEntities 调用的性能影响。
 */
@Mixin(Level.class)
public abstract class LevelMixin {

    @Shadow
    protected abstract LevelEntityGetter<Entity> getEntities();

    /**
     * 在 {@code getEntities} 返回后，补充因为使用 OBB 而被 AABB 搜索遗漏的实体。
     *
     * @param pEntity      搜索的中心实体
     * @param pBoundingBox 搜索 AABB
     * @param pPredicate   实体过滤条件
     * @param cir          回调信息，用于修改返回值
     */
    @Inject(method = "getEntities(Lnet/minecraft/world/entity/Entity;"
            + "Lnet/minecraft/world/phys/AABB;"
            + "Ljava/util/function/Predicate;)Ljava/util/List;",
            at = @At("RETURN"))
    public void onGetEntities(Entity pEntity, AABB pBoundingBox,
                               Predicate<? super Entity> pPredicate,
                               CallbackInfoReturnable<List<Entity>> cir) {
        // 仅在调用者是投射物时生效，限制性能影响
        if (!(pEntity instanceof Projectile)) return;

        List<Entity> result = cir.getReturnValue();

        // 遍历所有已追踪实体，检查 OBBEntity 的 OBB 是否与搜索 AABB 碰撞
        StreamSupport.stream(getEntities().getAll().spliterator(), false)
            .filter(e -> pPredicate.test(e) && e != pEntity)
            .forEach(entity -> {
                if (entity instanceof OBBEntity obbEntity && !obbEntity.enableAABB()) {
                    for (OBB obb : obbEntity.getOBBs()) {
                        if (OBB.isColliding(obb, pBoundingBox) && !result.contains(entity)) {
                            result.add(entity);
                            break; // 已加入，无需继续检查该实体的其他 OBB
                        }
                    }
                }
            });
    }
}
