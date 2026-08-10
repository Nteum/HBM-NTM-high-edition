package com.hbm.core.contents.obb;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;

/**
 * OBB 配置信息 —— 用于从 JSON 配置文件或其他数据源反序列化 OBB 定义。
 *
 * <p>在实体初始化阶段，载具等复杂实体从数据文件中读取 OBB 配置，
 * 然后在运行时基于实体的实际位置和朝向动态更新 OBB 的 center 和 rotation。
 *
 * <p>典型用法：
 * <pre>{@code
 * OBBInfo info = loadFromJson(json);
 * // 在实体 tick 中：
 * OBB obb = info.createOBB();
 * obb.setCenter(new Vector3d(entity.getX(), entity.getY(), entity.getZ()));
 * obb.updateRotation(/* entity yaw * /);
 * }</pre>
 */
public class OBBInfo {

    /** OBB 的半长尺寸 (本地 X, Y, Z) */
    private Vec3 size = Vec3.ZERO;

    /** OBB 相对于实体根部的偏移位置（本地坐标） */
    private Vec3 position = Vec3.ZERO;

    /** 变换类型标识（如 "Vehicle", "Turret", "Default"） */
    private String transform = "Default";

    /** 旋转类型标识 */
    private String rotation = "Default";

    /** 自定义旋转角度 (pitch, yaw, roll) 度数 */
    private Vec3 customRotate = Vec3.ZERO;

    /** OBB 所属的逻辑部件类型 */
    private OBB.Part part = OBB.Part.BODY;

    // ---- 缓存 ---- //
    private transient OBB cachedObb = null;

    // ========================================================================
    // 构造器
    // ========================================================================

    public OBBInfo() {}

    /**
     * 使用完整参数构造 OBBInfo。
     */
    public OBBInfo(Vec3 size, Vec3 position, OBB.Part part) {
        this.size = size;
        this.position = position;
        this.part = part;
    }

    // ========================================================================
    // 核心方法
    // ========================================================================

    /**
     * 获取或创建缓存的 OBB 实例。
     *
     * <p>首次调用时创建一个位于原点、无旋转的 OBB。
     * 调用者需要在每 tick 中根据实体状态更新 OBB 的 center 和 rotation。
     *
     * @return 缓存的 OBB 实例
     */
    public OBB getOBB() {
        if (cachedObb == null) {
            cachedObb = new OBB(
                OBB.vec3ToVector3d(Vec3.ZERO),
                OBB.vec3ToVector3d(size),
                new Quaterniond(),
                part
            );
        }
        return cachedObb;
    }

    /**
     * 使缓存的 OBB 失效，下次调用 {@link #getOBB()} 时将重新创建。
     */
    public void invalidateCache() {
        cachedObb = null;
    }

    /**
     * 对配置进行合法性限制。
     * 如果 transform 为空白，则设置默认值。
     */
    public void limit() {
        if (transform == null || transform.isBlank()) {
            transform = "Vehicle";
        }
    }

    // ========================================================================
    // Getters / Setters
    // ========================================================================

    public Vec3 getSize() {
        return size;
    }

    public void setSize(Vec3 size) {
        this.size = size;
        invalidateCache();
    }

    public Vec3 getPosition() {
        return position;
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public String getTransform() {
        return transform;
    }

    public void setTransform(String transform) {
        this.transform = transform;
    }

    public String getRotation() {
        return rotation;
    }

    public void setRotation(String rotation) {
        this.rotation = rotation;
    }

    public Vec3 getCustomRotate() {
        return customRotate;
    }

    public void setCustomRotate(Vec3 customRotate) {
        this.customRotate = customRotate;
    }

    public OBB.Part getPart() {
        return part;
    }

    public void setPart(OBB.Part part) {
        this.part = part;
        invalidateCache();
    }

    @Override
    public String toString() {
        return String.format("OBBInfo[size=%s, pos=%s, part=%s]", size, position, part);
    }
}
