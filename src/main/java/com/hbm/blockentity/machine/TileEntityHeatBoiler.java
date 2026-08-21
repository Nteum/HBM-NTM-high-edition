package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.block.HBMBlockProperties;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.heat.BasicHeatHandler;
import com.hbm.core.client.sounds.AudioWrapper;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.core.contents.fluid.HbmFluidType;
import com.hbm.core.contents.fluid.TraitData;
import com.hbm.explosion.temp.ExplosionOneOff;
import com.hbm.registries.HBMCaps;
import com.hbm.registries.ModSounds;
import com.hbm.saveddata.TomSaveData;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class TileEntityHeatBoiler extends BEDummyable {
    /* CONFIGURABLE */
    public static int maxHeat = 3_200_000;
    public static double diffusion = 0.1D;
    public static boolean canExplode = true;
    public static final int TANK_FLUID = 0;
    public static final int TANK_STEAM = 1;

    public boolean isOn;

    private AudioWrapper audio;
    private int audioTime;
    public TileEntityHeatBoiler(BlockPos pos, BlockState state) {
        super(pos, state);
        this.fluidHandler = new BasicFluidHandler(2, 16_000 * 100);
        ((BasicFluidHandler) this.fluidHandler).getFluidTank(TANK_FLUID).setCapacity(16_000);
        addCapability(ForgeCapabilities.FLUID_HANDLER, this::getFluidHandler);
        this.heatHandler = createHeatHandler(maxHeat);
        addCapability(HBMCaps.HEAT, this::getHeatHandler);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (!isHasExploded()){
            int light = this.level.getBrightness(LightLayer.SKY, this.worldPosition);
            if (light > 7 && TomSaveData.forWorld(this.level).fire > 1e-5){
                this.heatHandler.receiveHeat((int) ((maxHeat - this.heatHandler.getHeat()) * 0.000005D), false);
            }
            // 加热流体
            FluidStack inputFluid = this.fluidHandler.getFluidInTank(0);
            FluidType fluidType = inputFluid.getFluid().getFluidType();
            if (inputFluid.getFluid().is(FluidTags.WATER)) fluidType = HBMFluids.WATER.type().get();
            if (fluidType instanceof HbmFluidType hbmFluidType && hbmFluidType.hasTrait(TraitData.Heatable.class)){
                TraitData.Heatable heatable = hbmFluidType.getTrait(TraitData.Heatable.class);
                if (heatable.getEfficiency(TraitData.HeatingType.BOILER) > 0){
                    TraitData.Heatable.HeatingStep entry = heatable.getFirstStep();
                    int heatReq = (int) Math.max(entry.heatRequired() / heatable.getEfficiency(TraitData.HeatingType.BOILER), 1);
                    int inputOps = inputFluid.getAmount() / entry.inputMb();
                    int outputOps = (this.fluidHandler.getTankCapacity(1) - this.fluidHandler.getFluidInTank(1).getAmount()) / entry.outputMb();
                    int heatOps = this.heatHandler.getHeat() / heatReq;
                    int ops = Math.min(inputOps, Math.min(outputOps, heatOps));
                    this.fluidHandler.fill(new FluidStack(this.fluidHandler.getFluidInTank(0).getFluid(), this.fluidHandler.getFluidInTank(0).getAmount() - entry.inputMb() * ops), IFluidHandler.FluidAction.EXECUTE );
                    this.fluidHandler.fill(new FluidStack(this.fluidHandler.getFluidInTank(1).getFluid(), this.fluidHandler.getFluidInTank(1).getAmount() - entry.outputMb() * ops), IFluidHandler.FluidAction.EXECUTE );
                    this.heatHandler.extractHeat(heatReq * ops, false);
                    if (ops > 0 && level.random.nextInt(400) == 0)
                        level.playSound(null, this.worldPosition.above(2), ModSounds.BLOCK_BOILER_GROAN.get(), SoundSource.RECORDS, 0.5f, 1);
                    if (ops > 0) this.isOn = true;
                    if (outputOps == 0 && canExplode){
                        int xCoord = this.worldPosition.getX();
                        int yCoord = this.worldPosition.getY();
                        int zCoord = this.worldPosition.getZ();
                        for(int x = xCoord - 1; x <= xCoord + 1; x++) {
                            for(int y = yCoord + 2; y <= yCoord + 3; y++) {
                                for(int z = zCoord - 1; z <= zCoord + 1; z++) {
                                    level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                                }
                            }
                        }
                        // 爆炸少部分没做完。
                        level.setBlock(this.worldPosition.above(), Blocks.AIR.defaultBlockState(), 3);
                        ExplosionOneOff explosionOneOff = new ExplosionOneOff(level, null, xCoord + 0.5, yCoord + 2, zCoord + 0.5, 5, false, Explosion.BlockInteraction.KEEP);
                        explosionOneOff.setEntityProcessor(new ExplosionOneOff.EntityProcessorVanilla());
                        explosionOneOff.setBlockProcessor(new ExplosionOneOff.BlockProcessorStandard());
                        explosionOneOff.explode();
                        level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(HBMBlockProperties.BROKEN, true));
                    }
                }
            }
        }
        sendUpdatePacket();
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        if(this.isOn) audioTime = 20;

        if(audioTime > 0) {

            audioTime--;

            if(audio == null) {
                audio = createAudioLoop();
                audio.startSound();
            } else if(!audio.isPlaying()) {
                audio = rebootAudio(audio);
            }

            audio.updateVolume(1);
            audio.keepAlive();

        } else {

            if(audio != null) {
                audio.stopSound();
                audio = null;
            }
        }
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.put(HBMKey.FLUIDS, ((BasicHeatHandler) fluidHandler).serializeNBT());
        tag.put(HBMKey.HEAT, this.heatHandler.serializeNBT());
        return super.getReducedUpdateTag();
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        ((BasicHeatHandler) this.fluidHandler).deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        this.heatHandler.deserializeNBT(tag.getCompound(HBMKey.HEAT));
    }

    private boolean isHasExploded(){
        if (this.getBlockState().hasProperty(HBMBlockProperties.BROKEN)) {
            return this.getBlockState().getValue(HBMBlockProperties.BROKEN);
        }
        return false;
    }

    public BasicFluidHandler getFluids(){
        return (BasicFluidHandler) this.fluidHandler;
    }

    public BasicHeatHandler getHeats(){
        return (BasicHeatHandler) this.heatHandler;
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;    // 无UI
    }
}
