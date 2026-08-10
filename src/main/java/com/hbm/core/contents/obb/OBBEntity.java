package com.hbm.core.contents.obb;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 使用 OBB (Oriented Bounding Box) 碰撞箱的实体必须实现的接口。
 *
 * <p>实现此接口的实体应该通过 {@link #getOBBs()} 返回所有 OBB 碰撞箱的列表。
 * 当列表为空时（默认行为），实体退化为使用原版 AABB 碰撞。
 *
 * <p>典型用法：
 * <pre>{@code
 * public class MyVehicleEntity extends Entity implements OBBEntity {
 *     private final List<OBB> obbs = new ArrayList<>();
 *
 *     // 在 tick() 中更新 OBB 位置和朝向
 *     public void tick() {
 *         super.tick();
 *         for (OBB obb : obbs) {
 *             obb.setCenter(new Vector3d(getX(), getY(), getZ()));
 *         }
 *     }
 *
 *     public List<OBB> getOBBs() { return obbs; }
 * }
 * }</pre>
 */
public interface OBBEntity {

    /**
     * 返回此实体的所有 OBB 碰撞箱列表。
     *
     * <p>该列表是可变的 —— 实现者可以直接修改列表内容来更新 OBB 状态
     * （如位置、朝向、半长）。
     *
     * @return OBB 碰撞箱列表（可能为空）
     */
    List<OBB> getOBBs();

    /**
     * 此实体是否使用原版 AABB 碰撞模式。
     *
     * <p>默认实现：当 OBB 列表为空时返回 {@code true}。
     * 子类可以覆盖此方法强制启用 AABB 模式（例如在特定条件下）。
     *
     * @return {@code true} 表示使用 AABB 模式
     */
    default boolean enableAABB() {
        return getOBBs().isEmpty();
    }

    /**
     * 检查给定方块位置是否位于此实体的任何 OBB 内部。
     *
     * <p>创建一个以 pos 为中心的小型 AABB，然后检查它是否与任何 OBB 碰撞。
     * 用于方块破坏判定等场景。
     *
     * @param pos  待检查的方块位置
     * @param vec3 应用于所有 OBB 的平移偏移（通常是实体自身的运动向量）
     * @return 如果 pos 处的方块在某个 OBB 内返回 {@code true}
     */
    default boolean isInObb(BlockPos pos, Vec3 vec3) {
        List<OBB> obbList = getOBBs();
        // 创建一个以方块位置为中心、略小于方块的小型 AABB
        AABB aabb = new AABB(pos).inflate(0.3, 0.6, 0.3);
        for (OBB obb : obbList) {
            OBB moved = obb.move(vec3);
            if (OBB.isColliding(moved, aabb)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查给定实体是否与此实体的任何 OBB 碰撞。
     *
     * <p>如果目标实体也是 OBBEntity，执行 OBB-vs-OBB 碰撞检测；
     * 否则使用 OBB-vs-AABB 检测。
     *
     * @param entity 待检查的实体
     * @param vec3   应用于此实体所有 OBB 的平移偏移
     * @return 如果两个实体碰撞返回 {@code true}
     */
    default boolean isInObb(Entity entity, Vec3 vec3) {
        List<OBB> obbList = getOBBs();
        for (OBB obb : obbList) {
            OBB moved = obb.move(vec3);
            if (entity instanceof OBBEntity obbEntity && !obbEntity.enableAABB()) {
                // OBB vs OBB
                List<OBB> obbList2 = obbEntity.getOBBs();
                for (OBB obb2 : obbList2) {
                    if (OBB.isColliding(moved, obb2)) {
                        return true;
                    }
                }
            } else {
                // OBB vs AABB
                if (OBB.isColliding(moved, entity.getBoundingBox())) {
                    return true;
                }
            }
        }
        return false;
    }
}
