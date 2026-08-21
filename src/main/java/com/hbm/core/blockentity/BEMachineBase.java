package com.hbm.core.blockentity;

import com.hbm.blockentity.HBMTiles;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.gui.HBMMenus;
import com.hbm.registries.RegistryHelper;
import com.hbm.space.dim.CelestialBody;
import com.hbm.space.dim.orbit.Space;
import com.hbm.space.dim.trait.CBT_Atmosphere;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.IntStream;

import static com.hbm.HBM.MODID;

/**
 * 有menu界面的机器的基类，如果不需要menu，可以直接不实现createMenu
 *
 * 并入旧版 TileEntityMachineBase 的功能（统一机器基类）：
 * - 物品槽访问：items（MachineItemHandler）支持 slots 数组风格
 * - 侧面物品访问：getAccessibleSlotsFromSide / canInsert / canExtract
 * - 流体比例：getGaugeScaled
 * - 按钮：handleButtonPacket
 */
public abstract class BEMachineBase extends BECapabilities implements Nameable, MenuProvider {
    protected ContainerData containerData;
    public BEMachineBase(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }


    @Override
    public Component getName() {
        return Component.translatable("block." + MODID + "." + getId(this.getBlockState()));
    }

    public ContainerData getContainerData(){
        return containerData == null ? containerData = createContainerData() : containerData;
    }

    protected ContainerData createContainerData(){
        return new ContainerData() {
            @Override
            public int get(int p_39284_) {
                return 0;
            }

            @Override
            public void set(int p_39285_, int p_39286_) {}

            @Override
            public int getCount() {
                return 0;
            }
        };
    }

    public MenuType<?> getMenuType(){
        return HBMMenus.getById(this.getId(this.getBlockState()));
    }

    //========================直接从BaseContainerBlockEntity复制的=====================
    protected LockCode lockKey = LockCode.NO_LOCK;
    public boolean canOpen(Player pPlayer) {
        return canUnlock(pPlayer, this.lockKey, this.getDisplayName());
    }

    public static boolean canUnlock(Player pPlayer, LockCode pCode, Component pDisplayName) {
        if (!pPlayer.isSpectator() && !pCode.unlocksWith(pPlayer.getMainHandItem())) {
            pPlayer.displayClientMessage(Component.translatable("container.isLocked", pDisplayName), true);
            pPlayer.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
            return false;
        } else {
            return true;
        }
    }

    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return this.canOpen(pPlayer) ? this.createMenu(pContainerId, pPlayerInventory) : null;
    }

    protected abstract AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory);

    //============space =====================
    public boolean breatheAir(int amount) {
        return breatheAir(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), amount);
    }

    public static boolean breatheAir(Level world, int x, int y, int z, int amount) {
        CBT_Atmosphere atmosphere = world.dimension().equals(Space.LEVEL_KEY) ? null : CelestialBody.getTrait(world, CBT_Atmosphere.class);
        if(atmosphere != null) {
            if(atmosphere.hasFluid(HBMFluids.EARTHAIR.source().get(), 0.19) || atmosphere.hasFluid(HBMFluids.OXYGEN.source().get(), 0.09)) {
                return true;
            }
        }

//        List<AtmosphereBlob> blobs = ChunkAtmosphereManager.proxy.getBlobs(world, x, y, z);
//        for(AtmosphereBlob blob : blobs) {
//            if(blob.hasFluid(Fluids.EARTHAIR, 0.19) || blob.hasFluid(Fluids.OXYGEN, 0.09)) {
//                blob.consume(amount);
//                return true;
//            }
//        }

        return false;
    }
}
