package com.hbm.core.contents.obb;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;
import java.util.Optional;

/**
 * Oriented Bounding Box (OBB) —— 用于精确的碰撞检测与射线命中判定。
 *
 * <p>一个 OBB 可以在 3D 空间中任意旋转，由以下要素定义：
 * <ul>
 *   <li><b>center</b> —— 世界空间中的旋转中心</li>
 *   <li><b>extents</b> —— 沿三个<b>本地</b>轴 (X, Y, Z) 的半长</li>
 *   <li><b>rotation</b> —— 描述盒子相对于世界坐标轴朝向的四元数</li>
 *   <li><b>part</b> —— 逻辑部件标识，用于伤害路由（如炮塔、车轮、车身等）</li>
 * </ul>
 *
 * <p>本实现参考了 SuperbWarfare / AnECanSaiTin 的 HitboxAPI，进行了以下性能优化：
 * <ul>
 *   <li>使用 ThreadLocal 轴缓冲区消除 SAT 碰撞检测中的逐次分配</li>
 *   <li>标量叉积展开避免分配中间 Vector3d 实例</li>
 *   <li>缓存的世界空间轴可复用于迭代碰撞求解循环</li>
 * </ul>
 *
 * <p><b>依赖：</b>JOML (org.joml) 用于向量/四元数运算，Minecraft Forge 1.20.1 的
 * net.minecraft.world.phys 包中的 AABB 和 Vec3。
 *
 * @author Generated based on SuperbWarfare OBB system
 * @since 1.0
 */
public class OBB {

    /** 世界空间中的旋转中心 */
    public final Vector3d center;

    /** 沿本地 X/Y/Z 轴的半长 */
    public final Vector3d extents;

    /** 朝向四元数 */
    public final Quaterniond rotation;

    /** 逻辑部件标识 */
    public final Part part;

    /**
     * 构造一个 Oriented Bounding Box。
     *
     * @param center   世界空间中的中心位置
     * @param extents  半长 (本地 X, 本地 Y, 本地 Z)
     * @param rotation 朝向四元数
     * @param part     部件标识
     */
    public OBB(Vector3d center, Vector3d extents, Quaterniond rotation, Part part) {
        this.center = new Vector3d(center);
        this.extents = new Vector3d(extents);
        this.rotation = new Quaterniond(rotation);
        this.part = part;
    }

    // ========================================================================
    // Mutators (in-place 修改)
    // ========================================================================

    /**
     * 就地更新 OBB 的中心位置。
     *
     * @param center 新的世界空间中心
     */
    public void setCenter(Vector3d center) {
        this.center.set(center);
    }

    /**
     * 就地更新 OBB 的半长。
     *
     * @param extents 新的半长
     */
    public void setExtents(Vector3d extents) {
        this.extents.set(extents);
    }

    /**
     * 就地更新 OBB 的朝向。
     *
     * @param rotation 新的旋转四元数
     */
    public void updateRotation(Quaterniond rotation) {
        this.rotation.set(rotation);
    }

    // ========================================================================
    // Axis helpers —— 世界空间轴计算
    // ========================================================================

    /**
     * 将 OBB 的三个正交归一世界空间轴填入预分配的数组 {@code out}。
     *
     * <p>这是<b>零分配</b>的变体，用于性能关键路径。调用者必须提供一个长度为 3 的
     * 预分配 Vector3d 数组。该方法通过以下方式覆写每个元素：
     * <ol>
     *   <li>设为对应的本地基向量 (X=(1,0,0), Y=(0,1,0), Z=(0,0,1))</li>
     *   <li>用 {@link #rotation} 旋转得到世界空间轴方向</li>
     * </ol>
     *
     * <p><b>使用约束：</b>
     * <ul>
     *   <li>数组 {@code out} 长度必须为 3</li>
     *   <li>调用者必须在同线程的下一次 getAxesInto/getAxes 调用之前消费结果</li>
     * </ul>
     *
     * @param out 目标数组，三个元素会被就地覆写
     */
    public void getAxesInto(Vector3d[] out) {
        out[0].set(1.0, 0.0, 0.0);
        rotation.transform(out[0]);

        out[1].set(0.0, 1.0, 0.0);
        rotation.transform(out[1]);

        out[2].set(0.0, 0.0, 1.0);
        rotation.transform(out[2]);
    }

    /**
     * 返回 OBB 的三个正交归一世界空间轴，每次调用都会分配新数组。
     *
     * <p>在性能关键路径（tick 循环、迭代碰撞求解）中优先使用 {@link #getAxesInto(Vector3d[])}
     * 以避免 GC 压力。此方法作为一次性查询的便利方法提供。
     *
     * @return 新分配的数组 [axisX, axisY, axisZ]
     */
    public Vector3d[] getAxes() {
        Vector3d[] out = new Vector3d[]{new Vector3d(), new Vector3d(), new Vector3d()};
        getAxesInto(out);
        return out;
    }

    // ========================================================================
    // Geometry queries —— 几何查询
    // ========================================================================

    /**
     * 计算给定世界空间点 {@code vec3} 从内部最接近 OBB 的哪个面。
     *
     * <p>返回值编码了<b>轴</b>（1=X, 2=Y, 3=Z）和<b>方向</b>（正=+轴, 负=-轴）。
     * 例如：
     * <ul>
     *   <li>{@code +1} → 点最接近 +X 面</li>
     *   <li>{@code -2} → 点最接近 -Y 面（底部）</li>
     *   <li>{@code +3} → 点最接近 +Z 面</li>
     * </ul>
     *
     * @param vec3 世界空间查询点（应在 OBB 内部或表面上才有意义）
     * @return 带符号的面索引：±1=±X, ±2=±Y, ±3=±Z
     */
    public int getEmbeddingFace(Vec3 vec3) {
        Vector3d[] axes = AXES_A.get();
        getAxesInto(axes);
        Vector3d rel = vec3ToVector3d(vec3).sub(center);

        double projX = Math.abs(rel.dot(axes[0]));
        double projY = Math.abs(rel.dot(axes[1]));
        double projZ = Math.abs(rel.dot(axes[2]));

        double min = Double.MAX_VALUE;
        int index = 0;

        double dx = extents.x - projX;
        double dy = extents.y - projY;
        double dz = extents.z - projZ;

        if (dx < min) { min = dx; index = 1; }
        if (dy < min) { min = dy; index = 2; }
        if (dz < min) { index = 3; }

        return index * (rel.dot(axes[index - 1]) < 0 ? -1 : 1);
    }

    /**
     * 返回世界空间点 {@code vec3} 在 OBB 内部的<b>最小穿透深度</b>。
     *
     * @param vec3 世界空间查询点
     * @return 穿透深度（内部为正，表面附近为零）
     */
    public double getEmbeddingDepth(Vec3 vec3) {
        Vector3d[] axes = AXES_A.get();
        getAxesInto(axes);
        Vector3d rel = vec3ToVector3d(vec3).sub(center);

        double projX = Math.abs(rel.dot(axes[0]));
        double projY = Math.abs(rel.dot(axes[1]));
        double projZ = Math.abs(rel.dot(axes[2]));

        double dx = extents.x - projX;
        double dy = extents.y - projY;
        double dz = extents.z - projZ;

        double minDepth = Double.MAX_VALUE;
        if (Math.abs(dx) < Math.abs(minDepth)) minDepth = dx;
        if (Math.abs(dy) < Math.abs(minDepth)) minDepth = dy;
        if (Math.abs(dz) < Math.abs(minDepth)) minDepth = dz;

        return minDepth;
    }

    /**
     * 返回 OBB 在世界空间中的 8 个角顶点。
     *
     * <p>顶点排列顺序：-X-Y-Z, +X-Y-Z, +X+Y-Z, -X+Y-Z, -X-Y+Z, +X-Y+Z, +X+Y+Z, -X+Y+Z
     * （与标准立方体网格相同）。
     *
     * <p><b>每次调用分配 8 个 Vector3d 实例。</b>避免在紧密循环中调用。
     *
     * @return 8 个世界空间角点位置的数组
     */
    public Vector3d[] getVertices() {
        Vector3d[] vertices = new Vector3d[8];
        Vector3d[] localVertices = new Vector3d[]{
            new Vector3d(-extents.x, -extents.y, -extents.z),
            new Vector3d( extents.x, -extents.y, -extents.z),
            new Vector3d( extents.x,  extents.y, -extents.z),
            new Vector3d(-extents.x,  extents.y, -extents.z),
            new Vector3d(-extents.x, -extents.y,  extents.z),
            new Vector3d( extents.x, -extents.y,  extents.z),
            new Vector3d( extents.x,  extents.y,  extents.z),
            new Vector3d(-extents.x,  extents.y,  extents.z)
        };
        for (int i = 0; i < 8; i++) {
            Vector3d vertex = localVertices[i];
            vertex.rotate(rotation);
            vertex.add(center);
            vertices[i] = vertex;
        }
        return vertices;
    }

    /**
     * 使用 slab 方法在 OBB 本地坐标中执行射线-OBB 相交测试。
     *
     * <p>从 {@code pFrom} 到 {@code pTo} 的线段被变换到 OBB 的本地坐标系，
     * 然后与轴对齐的平板 {@code [-extents, +extents]} 进行测试。
     * 如果线段相交，返回世界空间中的第一个交点。
     *
     * @param pFrom 世界空间中的射线起点
     * @param pTo   世界空间中的射线终点
     * @return 如果线段命中 OBB，返回包含交点的 Optional；否则返回 Optional.empty()
     */
    public Optional<Vector3d> clip(Vector3d pFrom, Vector3d pTo) {
        Vector3d[] axes = AXES_A.get();
        getAxesInto(axes);

        Vector3d localFrom = worldToLocal(pFrom, axes);
        Vector3d localTo = worldToLocal(pTo, axes);
        Vector3d dir = new Vector3d(localTo).sub(localFrom);

        double tEnter = 0.0;
        double tExit = 1.0;

        for (int i = 0; i < 3; i++) {
            double min = -extents.get(i);
            double max = extents.get(i);
            double origin = localFrom.get(i);
            double direction = dir.get(i);

            if (Math.abs(direction) < 1e-7) {
                // 射线在该轴上近乎平行于 slab
                if (origin < min || origin > max) return Optional.empty();
                continue;
            }

            double t1 = (min - origin) / direction;
            double t2 = (max - origin) / direction;
            double tNear = Math.min(t1, t2);
            double tFar = Math.max(t1, t2);

            if (tNear > tEnter) tEnter = tNear;
            if (tFar < tExit) tExit = tFar;
            if (tEnter > tExit) return Optional.empty();
        }

        Vector3d localHit = new Vector3d(dir).mul(tEnter).add(localFrom);
        return Optional.of(localToWorld(localHit, axes));
    }

    // ========================================================================
    // 坐标变换辅助方法 (private)
    // ========================================================================

    /**
     * 将世界空间点变换到 OBB 本地坐标系。
     */
    private Vector3d worldToLocal(Vector3d worldPoint, Vector3d[] axes) {
        Vector3d rel = new Vector3d(worldPoint).sub(center);
        return new Vector3d(rel.dot(axes[0]), rel.dot(axes[1]), rel.dot(axes[2]));
    }

    /**
     * 将 OBB 本地点变换回世界空间。
     */
    private Vector3d localToWorld(Vector3d localPoint, Vector3d[] axes) {
        Vector3d result = new Vector3d(center);
        result.add(axes[0].mul(localPoint.x, new Vector3d()));
        result.add(axes[1].mul(localPoint.y, new Vector3d()));
        result.add(axes[2].mul(localPoint.z, new Vector3d()));
        return result;
    }

    // ========================================================================
    // Geometric operations —— 不可变变换
    // ========================================================================

    /**
     * 返回 OBB 的<b>副本</b>，每个半长均匀增加 {@code amount}。
     * 原 OBB 不会被修改。负值会收缩盒子。
     *
     * @param amount 沿所有三个轴的均匀膨胀量
     * @return 新的膨胀后 OBB
     */
    public OBB inflate(double amount) {
        return new OBB(center,
            new Vector3d(extents).add(amount, amount, amount),
            rotation, part);
    }

    /**
     * 返回 OBB 的<b>副本</b>，半长按每轴分别增加。
     *
     * @param x 本地 X 轴膨胀量
     * @param y 本地 Y 轴膨胀量
     * @param z 本地 Z 轴膨胀量
     * @return 新的膨胀后 OBB
     */
    public OBB inflate(double x, double y, double z) {
        return new OBB(center,
            new Vector3d(extents).add(x, y, z),
            rotation, part);
    }

    /**
     * 返回 OBB 的<b>副本</b>，按 {@code vec3} 平移。
     * 旋转和半长保持不变，仅偏移中心。
     *
     * @param vec3 世界空间中的平移向量
     * @return 新的平移后 OBB
     */
    public OBB move(Vec3 vec3) {
        return new OBB(
            new Vector3d(center.x + vec3.x, center.y + vec3.y, center.z + vec3.z),
            extents, rotation, part);
    }

    /**
     * 测试世界空间点 {@code vec3} 是否在 OBB 内部（或表面上）。
     *
     * <p>将 OBB 中心到 {@code vec3} 的向量投影到 OBB 的三个世界空间轴上，
     * 比较投影大小与半长。
     *
     * @param vec3 世界空间中的待测点
     * @return 如果点在 OBB 内部或边界上返回 {@code true}；否则返回 {@code false}
     */
    public boolean contains(Vec3 vec3) {
        Vector3d[] axes = AXES_A.get();
        getAxesInto(axes);
        Vector3d rel = vec3ToVector3d(vec3).sub(center);
        double projX = Math.abs(rel.dot(axes[0]));
        double projY = Math.abs(rel.dot(axes[1]));
        double projZ = Math.abs(rel.dot(axes[2]));
        return projX <= extents.x && projY <= extents.y && projZ <= extents.z;
    }

    // ========================================================================
    // Part enum —— 部件标识
    // ========================================================================

    /**
     * 逻辑部件标识，用于将碰撞/伤害事件路由到载具的特定子组件。
     */
    public enum Part {
        /** 空/未定义 */
        EMPTY,
        /** 左轮 */
        WHEEL_LEFT,
        /** 右轮 */
        WHEEL_RIGHT,
        /** 炮塔 */
        TURRET,
        /** 主引擎 */
        MAIN_ENGINE,
        /** 副引擎 */
        SUB_ENGINE,
        /** 车身 */
        BODY,
        /** 可交互区域（如车门） */
        INTERACTIVE,
        /** 碰撞专用 OBB（不参与射线命中判定） */
        COLLISION
    }

    // ========================================================================
    // Thread-local 轴缓冲区
    // ========================================================================

    /**
     * 主 OBB 的线程本地轴缓冲区。
     *
     * <p>预分配以消除 {@code getAxesInto} 中每次调用的数组分配。
     * 每个线程拥有独立的隔离实例，无需同步。
     *
     * <p><b>必须在同线程的下一次 {@code getAxesInto} 调用之前消费。</b>
     */
    public static final ThreadLocal<Vector3d[]> AXES_A =
        ThreadLocal.withInitial(() -> new Vector3d[]{
            new Vector3d(), new Vector3d(), new Vector3d()
        });

    /**
     * 辅助 OBB 的线程本地轴缓冲区。
     *
     * <p>某些方法（如 OBB×OBB 碰撞检测）需要同时持有<b>两个</b>不同 OBB 的世界空间轴，
     * 因此需要两个独立的缓冲区池。
     */
    public static final ThreadLocal<Vector3d[]> AXES_B =
        ThreadLocal.withInitial(() -> new Vector3d[]{
            new Vector3d(), new Vector3d(), new Vector3d()
        });

    // ========================================================================
    // 碰撞测试 —— OBB vs OBB / OBB vs AABB
    // ========================================================================

    /**
     * 使用分离轴定理 (SAT) 测试两个 OBB 是否重叠。
     *
     * <p>两个有向盒碰撞当且仅当它们在所有 15 个候选分离轴上的投影都重叠：
     * 3 个来自 obb 的面法线 + 3 个来自 other 的面法线 + 9 个边叉积轴。
     * 实际 SAT 逻辑委托给 JOML 的优化实现 {@code Intersectiond.testObOb}。
     *
     * @param obb   第一个有向包围盒
     * @param other 第二个有向包围盒
     * @return 如果两个 OBB 相交返回 {@code true}
     */
    public static boolean isColliding(OBB obb, OBB other) {
        Vector3d[] axes1 = AXES_A.get();
        obb.getAxesInto(axes1);

        Vector3d[] axes2 = AXES_B.get();
        other.getAxesInto(axes2);

        return Intersectiond.testObOb(
            obb.center, axes1[0], axes1[1], axes1[2], obb.extents,
            other.center, axes2[0], axes2[1], axes2[2], other.extents
        );
    }

    /**
     * 测试 OBB 是否与轴对齐包围盒 (AABB) 重叠。
     *
     * <p>等价于 OBB×OBB 碰撞检测，其中 AABB 的"旋转"为单位矩阵
     * （其轴始终与世界 X/Y/Z 对齐）。
     *
     * @param obb  有向包围盒
     * @param aabb 世界空间中的轴对齐包围盒
     * @return 如果形状相交返回 {@code true}
     */
    public static boolean isColliding(OBB obb, AABB aabb) {
        Vector3d[] axes = AXES_A.get();
        obb.getAxesInto(axes);
        Vector3d aabbCenter = new Vector3d(aabb.getCenter().x, aabb.getCenter().y, aabb.getCenter().z);
        Vector3d aabbHalfExtents = new Vector3d(
            (aabb.maxX - aabb.minX) / 2.0,
            (aabb.maxY - aabb.minY) / 2.0,
            (aabb.maxZ - aabb.minZ) / 2.0
        );
        return Intersectiond.testObOb(
            obb.center.x, obb.center.y, obb.center.z,
            axes[0].x, axes[0].y, axes[0].z,
            axes[1].x, axes[1].y, axes[1].z,
            axes[2].x, axes[2].y, axes[2].z,
            obb.extents.x, obb.extents.y, obb.extents.z,
            aabbCenter.x, aabbCenter.y, aabbCenter.z,
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0,
            0.0, 0.0, 1.0,
            aabbHalfExtents.x, aabbHalfExtents.y, aabbHalfExtents.z
        );
    }

    // ========================================================================
    // 最小平移向量 (MTV) 计算
    // ========================================================================

    /**
     * 使用分离轴定理 (SAT) 计算分离 OBB 和 AABB 所需的最小平移向量 (MTV)。
     *
     * <p>此重载接收前置计算好的 OBB 世界空间轴，方便在上层调用中复用
     * （例如在一次 tick 中将同一个 OBB 与多个候选 AABB 进行对比时）。
     *
     * <p><b>实现说明：</b>执行完整的 15 轴 SAT 测试（3 个 OBB 面法线、
     * 3 个 AABB 面法线、9 个边叉积轴），同时避免在热路径上分配堆内存：
     * <ul>
     *   <li>世界轴使用直接分量访问，而非构造 Vector3d 实例</li>
     *   <li>边叉积轴使用闭合标量表达式而非分配新的 Vector3d</li>
     *   <li>找到无重叠轴时立即返回 null（SAT 标准提前退出）</li>
     * </ul>
     *
     * <p>返回 MTV 的符号约定：向量指向 OBB 需要被推开以消除重叠的方向。
     *
     * @param obbCenter  OBB 的世界空间中心
     * @param obbAxes    OBB 的三个正交归一世界空间轴
     * @param obbExtents OBB 沿自身三个本地轴的半长
     * @param aabb       待测试的世界空间轴对齐包围盒
     * @return 如果形状重叠，返回 MTV；如果发现分离轴（无碰撞），返回 {@code null}
     */
    public static Vector3d computeObbAabbMtv(Vector3d obbCenter, Vector3d[] obbAxes,
                                              Vector3d obbExtents, AABB aabb) {
        Vector3d obbAxis0 = obbAxes[0];
        Vector3d obbAxis1 = obbAxes[1];
        Vector3d obbAxis2 = obbAxes[2];
        double extX = obbExtents.x;
        double extY = obbExtents.y;
        double extZ = obbExtents.z;

        double aabbCenterX = (aabb.minX + aabb.maxX) * 0.5;
        double aabbCenterY = (aabb.minY + aabb.maxY) * 0.5;
        double aabbCenterZ = (aabb.minZ + aabb.maxZ) * 0.5;
        double aabbHalfX = (aabb.maxX - aabb.minX) * 0.5;
        double aabbHalfY = (aabb.maxY - aabb.minY) * 0.5;
        double aabbHalfZ = (aabb.maxZ - aabb.minZ) * 0.5;

        // 两盒中心之间的向量 —— 计算一次，所有轴投影复用
        double dX = obbCenter.x - aabbCenterX;
        double dY = obbCenter.y - aabbCenterY;
        double dZ = obbCenter.z - aabbCenterZ;

        double minOverlap = Double.MAX_VALUE;
        double mtvAxisX = 0.0, mtvAxisY = 0.0, mtvAxisZ = 0.0;
        boolean pushNegative = false;

        Vector3d[] obbAxesArr = {obbAxis0, obbAxis1, obbAxis2};
        double[] obbHalfArr = {extX, extY, extZ};

        // ---- 轴 1-3: OBB 面法线 ----
        // 投影到其自身面法线上时，OBB 的半径恰好等于沿该轴的半长
        // （轴因为正交归一构造），因此 OBB 侧不需要求和点积 —— 只有 AABB 侧需要通用投影公式。
        for (int i = 0; i < 3; i++) {
            Vector3d axis = obbAxesArr[i];
            double axisX = axis.x, axisY = axis.y, axisZ = axis.z;
            double centerDist = dX * axisX + dY * axisY + dZ * axisZ;
            double obbRadius = obbHalfArr[i];
            double aabbRadius = Math.abs(axisX) * aabbHalfX
                              + Math.abs(axisY) * aabbHalfY
                              + Math.abs(axisZ) * aabbHalfZ;
            double overlap = obbRadius + aabbRadius - Math.abs(centerDist);
            if (overlap < 0.0) return null; // 发现分离轴，无碰撞
            if (overlap < minOverlap) {
                minOverlap = overlap;
                mtvAxisX = axisX; mtvAxisY = axisY; mtvAxisZ = axisZ;
                pushNegative = centerDist < 0.0;
            }
        }

        // ---- 轴 4-6: AABB 面法线（世界 X/Y/Z） ----
        // 投影到世界轴上时，AABB 的半径恰好等于其沿该轴的半长；
        // 只有 OBB 侧需要通用投影半长公式。
        // X-axis
        {
            double obbRadius = Math.abs(obbAxis0.x) * extX
                             + Math.abs(obbAxis1.x) * extY
                             + Math.abs(obbAxis2.x) * extZ;
            double overlap = obbRadius + aabbHalfX - Math.abs(dX);
            if (overlap < 0.0) return null;
            if (overlap < minOverlap) {
                minOverlap = overlap;
                mtvAxisX = 1.0; mtvAxisY = 0.0; mtvAxisZ = 0.0;
                pushNegative = dX < 0.0;
            }
        }
        // Y-axis
        {
            double obbRadius = Math.abs(obbAxis0.y) * extX
                             + Math.abs(obbAxis1.y) * extY
                             + Math.abs(obbAxis2.y) * extZ;
            double overlap = obbRadius + aabbHalfY - Math.abs(dY);
            if (overlap < 0.0) return null;
            if (overlap < minOverlap) {
                minOverlap = overlap;
                mtvAxisX = 0.0; mtvAxisY = 1.0; mtvAxisZ = 0.0;
                pushNegative = dY < 0.0;
            }
        }
        // Z-axis
        {
            double obbRadius = Math.abs(obbAxis0.z) * extX
                             + Math.abs(obbAxis1.z) * extY
                             + Math.abs(obbAxis2.z) * extZ;
            double overlap = obbRadius + aabbHalfZ - Math.abs(dZ);
            if (overlap < 0.0) return null;
            if (overlap < minOverlap) {
                minOverlap = overlap;
                mtvAxisX = 0.0; mtvAxisY = 0.0; mtvAxisZ = 1.0;
                pushNegative = dZ < 0.0;
            }
        }

        // ---- 轴 7-15: 边叉积 (3 OBB 轴 × 3 世界轴) ----
        // 对于世界基向量 e，(a × e) 有闭合形式、免分配表达式：
        //   a × (1,0,0) = ( 0,   a.z, -a.y)
        //   a × (0,1,0) = (-a.z,  0,   a.x)
        //   a × (0,0,1) = ( a.y, -a.x,  0)
        // 这避免了为每个候选 AABB 调用 Vector3d.cross 分配新向量 9 次。
        for (int i = 0; i < 3; i++) {
            Vector3d a = obbAxesArr[i];
            for (int j = 0; j < 3; j++) {
                double axisX, axisY, axisZ;
                switch (j) {
                    case 0:  axisX = 0.0;  axisY = a.z;  axisZ = -a.y; break;
                    case 1:  axisX = -a.z; axisY = 0.0;  axisZ = a.x;  break;
                    default: axisX = a.y;  axisY = -a.x; axisZ = 0.0;  break;
                }

                double lenSq = axisX * axisX + axisY * axisY + axisZ * axisZ;
                // 退化轴（OBB 轴与对应世界轴平行）：没有分离信息且归一化时会除以约 0。
                if (lenSq < 1.0e-9) continue;

                double invLen = 1.0 / Math.sqrt(lenSq);
                double nx = axisX * invLen;
                double ny = axisY * invLen;
                double nz = axisZ * invLen;

                double centerDist = dX * nx + dY * ny + dZ * nz;
                double obbRadius = 0.0;
                for (int k = 0; k < 3; k++) {
                    Vector3d ak = obbAxesArr[k];
                    obbRadius += Math.abs(ak.x * nx + ak.y * ny + ak.z * nz) * obbHalfArr[k];
                }
                double aabbRadius = Math.abs(nx) * aabbHalfX
                                  + Math.abs(ny) * aabbHalfY
                                  + Math.abs(nz) * aabbHalfZ;
                double overlap = obbRadius + aabbRadius - Math.abs(centerDist);
                if (overlap < 0.0) return null;
                if (overlap < minOverlap) {
                    minOverlap = overlap;
                    mtvAxisX = nx; mtvAxisY = ny; mtvAxisZ = nz;
                    pushNegative = centerDist < 0.0;
                }
            }
        }

        // MTV 必须将 OBB 推出 AABB：如果 OBB 的投影中心位于轴负侧
        // （相对于 AABB 的投影中心），则沿该轴的推出方向必须取反。
        double sign = pushNegative ? -1.0 : 1.0;
        return new Vector3d(
            mtvAxisX * sign * minOverlap,
            mtvAxisY * sign * minOverlap,
            mtvAxisZ * sign * minOverlap
        );
    }

    /**
     * {@link #computeObbAabbMtv(Vector3d, Vector3d[], Vector3d, AABB)} 的便利重载，
     * 内部通过 {@link #getAxes()} 推导 OBB 的世界空间轴。
     *
     * <p>在同一 tick 内将同一 OBB 与多个候选 AABB 进行对比时，
     * 优先使用显式传轴的重载以避免重复推导相同的朝向不变轴。
     *
     * @param obb  待测试的 OBB
     * @param aabb 世界空间中的轴对齐包围盒
     * @return 如果形状重叠返回 MTV；否则返回 {@code null}
     */
    public static Vector3d computeObbAabbMtv(OBB obb, AABB aabb) {
        return computeObbAabbMtv(obb.center, obb.getAxes(), obb.extents, aabb);
    }

    // ========================================================================
    // AABB 辅助方法
    // ========================================================================

    /**
     * 计算 OBB <b>在应用平移后</b>的世界空间 AABB。
     *
     * <p>因为平移不影响朝向，所得 AABB 的半长与未平移的 OBB 的投影半长相同；
     * 只有盒子中心需要偏移 {@code translation}。
     *
     * <p>这避免了分配平移后的 OBB 副本并重新通过四元数旋转推导 8 个角顶点。
     *
     * @param obb         源 OBB
     * @param axes        OBB 的三个正交归一世界空间轴
     * @param translation 应用于 OBB 中心的世界空间偏移
     * @return OBB 被 {@code translation} 平移后的世界空间 AABB
     */
    public static AABB getTranslatedWorldAABB(OBB obb, Vector3d[] axes, Vec3 translation) {
        Vector3d ext = obb.extents;
        double halfX = Math.abs(axes[0].x) * ext.x + Math.abs(axes[1].x) * ext.y + Math.abs(axes[2].x) * ext.z;
        double halfY = Math.abs(axes[0].y) * ext.x + Math.abs(axes[1].y) * ext.y + Math.abs(axes[2].y) * ext.z;
        double halfZ = Math.abs(axes[0].z) * ext.x + Math.abs(axes[1].z) * ext.y + Math.abs(axes[2].z) * ext.z;
        double cx = obb.center.x + translation.x;
        double cy = obb.center.y + translation.y;
        double cz = obb.center.z + translation.z;
        return new AABB(cx - halfX, cy - halfY, cz - halfZ,
                        cx + halfX, cy + halfY, cz + halfZ);
    }

    /**
     * 计算包围 {@code obb} 的世界空间轴对齐包围盒 (AABB)。
     *
     * <p>使用投影公式而非顶点枚举：
     * {@code halfX = |axis0.x|*ex + |axis1.x|*ey + |axis2.x|*ez}，以此类推。
     *
     * @param obb 源 OBB
     * @return 包围 obb 的世界空间 AABB
     */
    public static AABB getWorldAABB(OBB obb) {
        Vector3d[] axes = AXES_A.get();
        obb.getAxesInto(axes);

        Vector3d c = obb.center;
        Vector3d e = obb.extents;

        double halfX = Math.abs(axes[0].x) * e.x + Math.abs(axes[1].x) * e.y + Math.abs(axes[2].x) * e.z;
        double halfY = Math.abs(axes[0].y) * e.x + Math.abs(axes[1].y) * e.y + Math.abs(axes[2].y) * e.z;
        double halfZ = Math.abs(axes[0].z) * e.x + Math.abs(axes[1].z) * e.y + Math.abs(axes[2].z) * e.z;

        return new AABB(
            c.x - halfX, c.y - halfY, c.z - halfZ,
            c.x + halfX, c.y + halfY, c.z + halfZ
        );
    }

    // ========================================================================
    // 空间查询
    // ========================================================================

    /**
     * 返回 {@code obb} 表面（或内部）距离 {@code point} 最近的点。
     *
     * <p>算法：将 obb 中心到 point 的向量投影到 obb 的三个世界空间轴上，
     * 将每个投影夹到 {@code [-extent, +extent]} 范围内，
     * 然后从夹后的投影重建世界空间点。
     *
     * @param point 世界空间查询点
     * @param obb   目标 OBB
     * @return obb 上或内部的世界空间最近点
     */
    public static Vector3d getClosestPointOBB(Vector3d point, OBB obb) {
        Vector3d[] axes = AXES_A.get();
        obb.getAxesInto(axes);
        Vector3d nearP = new Vector3d(obb.center);
        Vector3d dist = point.sub(nearP, new Vector3d());
        double[] extArr = {obb.extents.x, obb.extents.y, obb.extents.z};

        for (int i = 0; i < 3; i++) {
            double distance = dist.dot(axes[i]);
            distance = Mth.clamp(distance, -extArr[i], extArr[i]);
            nearP.x += distance * axes[i].x;
            nearP.y += distance * axes[i].y;
            nearP.z += distance * axes[i].z;
        }
        return nearP;
    }

    // ========================================================================
    // 射线相交
    // ========================================================================

    /**
     * 测试从 {@code start} 到 {@code end} 的线段是否与 {@code obb} 相交。
     *
     * <p>将线段端点变换到 obb 的本地坐标系，在本地空间执行射线-AABB 相交测试。
     * 第一个交点（如果有）变换回世界空间。
     *
     * @param obb   目标 OBB
     * @param start 世界空间中的线段起点
     * @param end   世界空间中的线段终点
     * @return 世界空间中的命中位置，如果不相交返回 {@code null}
     */
    public static Vec3 rayIntersect(OBB obb, Vec3 start, Vec3 end) {
        Vec3 center = vector3dToVec3(obb.center);
        Vec3 extents = vector3dToVec3(obb.extents);

        Vector3d localStart = toLocal(obb, start);
        Vector3d localEnd = toLocal(obb, end);

        Vector2d result = new Vector2d();
        boolean intersects = Intersectiond.intersectRayAab(
            localStart.x, localStart.y, localStart.z,
            localEnd.x - localStart.x, localEnd.y - localStart.y, localEnd.z - localStart.z,
            -extents.x, -extents.y, -extents.z,
            extents.x, extents.y, extents.z,
            result
        );

        if (!intersects) return null;

        double clampedT = Math.max(0.0, Math.min(1.0, result.x));
        Vector3d localHit = new Vector3d(
            localStart.x + clampedT * (localEnd.x - localStart.x),
            localStart.y + clampedT * (localEnd.y - localStart.y),
            localStart.z + clampedT * (localEnd.z - localStart.z)
        );
        obb.rotation.transform(localHit);
        return new Vec3(localHit.x + center.x, localHit.y + center.y, localHit.z + center.z);
    }

    /**
     * 将世界空间点变换到 OBB 本地坐标系。
     */
    private static Vector3d toLocal(OBB obb, Vec3 worldPoint) {
        Vec3 center = vector3dToVec3(obb.center);
        Quaterniond inverse = new Quaterniond(obb.rotation).conjugate();
        Vector3d relative = new Vector3d(
            worldPoint.x - center.x,
            worldPoint.y - center.y,
            worldPoint.z - center.z
        );
        inverse.transform(relative);
        return relative;
    }

    // ========================================================================
    // 坐标转换辅助方法
    // ========================================================================

    /**
     * 将 Minecraft {@link Vec3} 转换为 JOML {@link Vector3d}。
     */
    public static Vector3d vec3ToVector3d(Vec3 vec3) {
        return new Vector3d(vec3.x, vec3.y, vec3.z);
    }

    /**
     * 将 JOML {@link Vector3d} 转换为 Minecraft {@link Vec3}。
     */
    public static Vec3 vector3dToVec3(Vector3d vector3d) {
        return new Vec3(vector3d.x, vector3d.y, vector3d.z);
    }

    @Override
    public String toString() {
        return String.format("OBB[center=(%.2f,%.2f,%.2f), extents=(%.2f,%.2f,%.2f), part=%s]",
            center.x, center.y, center.z, extents.x, extents.y, extents.z, part);
    }
}
