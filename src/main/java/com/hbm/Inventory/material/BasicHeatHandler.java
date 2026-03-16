package com.hbm.Inventory.material;

import com.hbm.HBMKey;
import com.hbm.api.heat.IHeatHandler;
import com.hbm.registries.HBMCaps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;

public class BasicHeatHandler implements IHeatHandler, INBTSerializable<CompoundTag> {
    private static int MAX = Integer.MAX_VALUE;
    private int heat;
    private final int maxHeat;
    private final int extractLimit;
    private final int receiveLimit;
    private boolean canDiffusion = false;   // 是否存在导热衰减
    private double diffusion = 1.0;         // 导热衰减的数值
    public BasicHeatHandler(int capacity, int maxReceive, int maxExtract, int heat)
    {
        this.maxHeat = capacity;
        this.receiveLimit = maxReceive;
        this.extractLimit = maxExtract;
        this.heat = Math.max(0 , Math.min(capacity, heat));
    }
    public static BasicHeatHandler of(int maxHeat){
        return new BasicHeatHandler(maxHeat, maxHeat, maxHeat, 0);
    }
    public static BasicHeatHandler onlyIn(int maxHeat){
        return new BasicHeatHandler(maxHeat, maxHeat, 0, 0);
    }
    public static BasicHeatHandler onlyOut(int maxHeat){
        return new BasicHeatHandler(maxHeat, 0, maxHeat, 0);
    }
    public BasicHeatHandler diffusion(double diffusion){
        this.canDiffusion = true;
        this.diffusion = diffusion;
        return this;
    }
    @Override
    public int getHeat() {
        return heat;
    }

    @Override
    public int getMaxHeat() {
        return maxHeat;
    }

    @Override
    public boolean canExtract() {
        return extractLimit > 0;
    }

    @Override
    public boolean canReceive() {
        return receiveLimit > 0;
    }

    @Override
    public int extractHeat(int maxExtract, boolean simulate) {
        if (!canExtract() || maxExtract <= 0) return 0;
        int extract = Math.min(heat, Math.min(maxExtract, extractLimit));
        if (!simulate) this.heat -= extract;
        return extract;
    }

    @Override
    public int receiveHeat(int maxReceive, boolean simulate) {
        if (!canReceive() || maxReceive <= 0) return 0;
        int receive = Math.min(maxHeat - heat, Math.min(maxReceive, receiveLimit));
        if (!simulate) this.heat += receive;
        return receive;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(HBMKey.HEAT, heat);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.heat = nbt.contains(HBMKey.HEAT, Tag.TAG_INT) ? nbt.getInt(HBMKey.HEAT) : this.heat;
    }

    public void receiveFromOther(IHeatHandler other){
        if (!this.canReceive() || !other.canExtract()) return;
        // 热量需要靠温度差来传导
        int shouldReceive = Math.min(this.receiveLimit, Math.max(0, other.getHeat() - this.getHeat()));
        int canGiveFull = other.extractHeat(shouldReceive, true); // 对方能给的满额
        int canTake = this.receiveHeat((int)(canGiveFull * diffusion), true); // 我能收的（带损耗）

        if (canTake > 0) {
            // 换算回对方需要付出的原始值
            int needToExtract = (int)(canTake / diffusion);
            int actuallyExtracted = other.extractHeat(needToExtract, false);
            this.receiveHeat((int)(actuallyExtracted * diffusion), false);
        }
    }

    public void receiveFromOther(Level level, BlockPos pos){
        BlockEntity blockEntity = level.getBlockEntity(pos);
        IHeatHandler other;
        if (blockEntity != null && (other = blockEntity.getCapability(HBMCaps.HEAT).orElse(null)) != null){
            this.receiveFromOther(other);
        }
    }

    public void extractToOther(IHeatHandler other){
        if (!this.canExtract() || !other.canReceive()) return;
        // 热量需要靠温度差来传导
        int shouldExtract = Math.min(this.extractLimit, Math.max(0, this.getHeat() - other.getHeat()));
        int canGiveFull = this.extractHeat(shouldExtract, true); // 对方能给的满额
        int canTake = other.receiveHeat((int)(canGiveFull * diffusion), true); // 我能收的（带损耗）

        if (canTake > 0) {
            // 换算回对方需要付出的原始值
            int needToExtract = (int)(canTake / diffusion);
            int actuallyExtracted = this.extractHeat(needToExtract, false);
            other.receiveHeat((int)(actuallyExtracted * diffusion), false);
        }
    }

    public void extractToOther(Level level, BlockPos pos){
        BlockEntity blockEntity = level.getBlockEntity(pos);
        IHeatHandler other;
        if (blockEntity != null && (other = blockEntity.getCapability(HBMCaps.HEAT).orElse(null)) != null){
            this.extractToOther(other);
        }
    }
    /**
     * 热量的衰减
     * 机器按需使用
     * */
    public void decay(){
        this.heat = Math.max(this.heat - Math.max(this.heat / 1000, 1), 0);
    }
}
