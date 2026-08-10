package com.hbm.core.contents.obb;

import net.minecraft.world.entity.Entity;

/**
 * 用于在投射物命中时追踪具体命中了 OBB 的哪个部件的接口。
 *
 * <p>通过 Mixin 注入到 {@link Entity} 中，使得任何实体都可以记录
 * "上一次被投射物命中的 OBB 部件"，从而在伤害处理中区分
 * 炮塔、车轮、引擎等不同部件的伤害。
 *
 * <p>典型使用流程：
 * <ol>
 *   <li>投射物碰撞检测时，通过 OBB 射线检测确定命中了哪个 OBB</li>
 *   <li>调用 {@code OBBHitter.getInstance(projectile).obb$setCurrentHitPart(part)} 记录</li>
 *   <li>在 {@code hurt()} 方法中读取 {@code obb$getCurrentHitPart()} 来分配伤害</li>
 * </ol>
 */
public interface OBBHitter {

    /**
     * 获取当前命中的 OBB 部件。
     *
     * @return 命中的部件，如果未被设置则返回 {@code null}
     */
    OBB.Part obb$getCurrentHitPart();

    /**
     * 设置当前命中的 OBB 部件。
     *
     * @param part 命中的部件
     */
    void obb$setCurrentHitPart(OBB.Part part);

    /**
     * 从任意实体获取 {@link OBBHitter} 实例。
     *
     * <p>通过 Mixin 机制，所有 {@link Entity} 实例都实现了此接口，
     * 因此此方法只是做一次安全的类型转换。
     *
     * @param entity 目标实体
     * @return 该实体的 OBBHitter 接口
     */
    static OBBHitter getInstance(Entity entity) {
        return (OBBHitter) entity;
    }
}
