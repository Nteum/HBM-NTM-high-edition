package com.hbm.core.contents.upgrade;

import com.hbm.Inventory.UpgradeManagerNT;
import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import com.hbm.item.machine.ItemMachineUpgrade.UpgradeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.Map;

/**
 * 升级效果抽象层。
 *
 * 三步职责分离：
 * 1. UpgradeManagerNT —— 扫描物品槽，把 ItemMachineUpgrade 汇总为各类型等级（checkSlots）
 * 2. IUpgradeInfoProvider —— 机器声明可接受哪些升级及上限（getValidUpgrades），
 *    并描述每个升级的数值效果（canProvideInfo / provideInfo，供 GUI tooltip 使用）
 * 3. MachineUpgradeHandler —— 机器每 tick 调用它，得到按等级折算的 speed/power 倍率，
 *    再套用到自身的消耗/进度上
 *
 * 相比旧版每台机器手写：
 *     upgradeManager.checkSlots(...);
 *     int speed = upgradeManager.getLevel(SPEED);
 *     this.consumption = 50 + speed * 50 - power * 15;
 *     this.maxProgress = 100 - speed * 25 + power * 10;
 * 本类把"等级 → 倍率"的换算统一起来，机器只需声明每级影响系数。
 */
public class MachineUpgradeHandler {
    private final UpgradeManagerNT manager;

    // 每级升级对参数的影响系数（默认值）
    private double speedPerLevel = 0.25;    // 每级速度 +25%（缩短加工时间）
    private double powerPerLevel = 0.15;    // 每级功耗 -15%
    private double effectPerLevel = 0.0;    // 每级效果加成
    private double overdrivePerLevel = 0.0; // 每级过载倍率
    private double fortunePerLevel = 0.0;   // 每级时运加成

    public MachineUpgradeHandler(BlockEntity owner){
        this.manager = new UpgradeManagerNT(owner);
    }
    public MachineUpgradeHandler speed(double perLevel){ this.speedPerLevel = perLevel; return this; }
    public MachineUpgradeHandler power(double perLevel){ this.powerPerLevel = perLevel; return this; }
    public MachineUpgradeHandler effect(double perLevel){ this.effectPerLevel = perLevel; return this; }
    public MachineUpgradeHandler overdrive(double perLevel){ this.overdrivePerLevel = perLevel; return this; }
    public MachineUpgradeHandler fortune(double perLevel){ this.fortunePerLevel = perLevel; return this; }

    /** 每 tick 扫描升级槽并更新等级缓存 */
    public void check(List<ItemStack> slots, int start, int end){
        manager.checkSlots(manager.owner, slots, start, end);
    }

    /** 速度倍率（>1 表示更快，加工时间 = 基础时间 / 该值） */
    public double getSpeedMultiplier(){
        return 1.0 + manager.getSpeedLevel() * speedPerLevel;
    }
    /** 功耗倍率（<1 表示更省电） */
    public double getPowerMultiplier(){
        return 1.0 - manager.getPowerLevel() * powerPerLevel;
    }
    public double getEffectMultiplier(){
        return 1.0 + manager.getEffectLevel() * effectPerLevel;
    }
    public double getOverdriveMultiplier(){
        return 1.0 + manager.getOverdriveLevel() * overdrivePerLevel;
    }
    public double getFortuneBonus(){
        return manager.getFortuneLevel() * fortunePerLevel;
    }

    public int getLevel(UpgradeType type){
        return manager.getLevel(type);
    }
    public UpgradeManagerNT getManager(){
        return manager;
    }

    /** 便捷：把最终功耗与加工时间应用到机器字段 */
    public void applyTo(UpgradeParams params){
        params.apply(this);
    }

    /**
     * 机器用本接口描述自身的可升级参数。机器只需实现 apply 方法把倍率套用进去。
     * 示例：
     *   handler.applyTo(new UpgradeParams(){
     *       @Override public void apply(MachineUpgradeHandler h){
     *           consumption = (int)(baseConsumption * h.getPowerMultiplier());
     *           maxProgress = (int)(baseProgress / h.getSpeedMultiplier());
     *       }
     *   });
     */
    @FunctionalInterface
    public interface UpgradeParams {
        void apply(MachineUpgradeHandler handler);
    }
}
