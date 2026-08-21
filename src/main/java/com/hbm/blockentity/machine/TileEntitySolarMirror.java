package com.hbm.blockentity.machine;

import com.google.errorprone.annotations.Var;
import com.hbm.core.blockentity.BEUpdateable;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntitySolarMirror extends BEUpdateable {
    public int tX;
    public int tY;
    public int tZ;
    public boolean isOn;
    public TileEntitySolarMirror(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (level.getGameTime() % 20 == 0) sendUpdatePacket();
        int brightness;
        if (tY < this.worldPosition.getY()
                || (brightness = level.getBrightness(LightLayer.SKY, this.worldPosition.above()) - 11) <= 0
                || !level.canSeeSky(this.worldPosition.above())){
            isOn = false;
            return;
        }
        isOn = true;
        SolarBoilerEntity tileEntity = WorldUtils.getTileEntity(SolarBoilerEntity.class, level, new BlockPos(tX, tY - 1, tZ));
        if (tileEntity != null) tileEntity.heat += brightness;
    }

    public void setTarget(int x, int y, int z) {
        tX = x;
        tY = y;
        tZ = z;
        this.setChanged();
        sendUpdatePacket();
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        BlockPos pos = new BlockPos(tX, tY - 1, tZ);
        if (WorldUtils.getTileEntity(level, pos) instanceof SolarBoilerEntity solarBoiler){
            solarBoiler.primary.add(pos);
        }
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("xyz", new int[]{tX, tY, tZ});
        tag.putBoolean("on", this.isOn);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        this.isOn = tag.getBoolean("on");
        int[] intArray = tag.getIntArray("xyz");
        tX = intArray[0];
        tY = intArray[1];
        tZ = intArray[2];
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putIntArray("xyz", new int[]{tX, tY, tZ});
        pTag.putBoolean("on", this.isOn);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.isOn = pTag.getBoolean("on");
        int[] intArray = pTag.getIntArray("xyz");
        tX = intArray[0];
        tY = intArray[1];
        tZ = intArray[2];
    }
}
