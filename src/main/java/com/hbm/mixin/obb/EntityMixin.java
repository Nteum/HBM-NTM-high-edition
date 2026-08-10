package com.hbm.mixin.obb;

import com.hbm.core.contents.obb.OBB;
import com.hbm.core.contents.obb.OBBHitter;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * 为所有 {@link Entity} 实例注入 {@link OBBHitter} 接口。
 *
 * <p>通过此 Mixin，每个实体都可以记录"上一次被投射物命中的 OBB 部件"，
 * 从而在伤害处理中区分炮塔、车轮、引擎等不同部件的伤害。
 *
 * <p><b>使用方式：</b>
 * <pre>{@code
 * // 在投射物命中检测中记录命中的部件
 * OBBHitter hitter = OBBHitter.getInstance(projectile);
 * hitter.obb$setCurrentHitPart(OBB.Part.TURRET);
 *
 * // 在 hurt() 方法中读取
 * OBBHitter hitter = OBBHitter.getInstance(projectile);
 * OBB.Part part = hitter.obb$getCurrentHitPart();
 * // 根据 part 分配伤害
 * }</pre>
 */
@Mixin(Entity.class)
public abstract class EntityMixin implements OBBHitter {

    /**
     * 当前命中的 OBB 部件，由投射物碰撞检测设置。
     */
    @Unique
    public OBB.Part obb$currentHitPart;

    @Override
    public OBB.Part obb$getCurrentHitPart() {
        return this.obb$currentHitPart;
    }

    @Override
    public void obb$setCurrentHitPart(OBB.Part part) {
        this.obb$currentHitPart = part;
    }
}
