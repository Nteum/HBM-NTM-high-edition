package com.hbm.blockentity.weapon;


import com.hbm.HBMKey;
import com.hbm.api.Mode;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.HybridEnergyStorage;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.inventory.ModeBuilder;
import com.hbm.block.weapon.LaunchPad;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.registries.HBMCaps;
import com.hbm.entity.projectile.EntityThrowableNT;
import com.hbm.gui.menu.LaunchPadMenu;
import com.hbm.registries.ModBlocks;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LaunchPadTileEntity extends EntityLaunchPadBase {
	private final HybridEnergyStorage forgeEnergy;
	protected ContainerData containerData = new ContainerData() {
		@Override
		public int get(int pIndex) {
			return 0;
		}

		@Override
		public void set(int pIndex, int pValue) {

		}

		@Override
		public int getCount() {
			return 0;
		}
	};
	public LaunchPadTileEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityType.LAUNCHPAD_ENTITY.get(),pPos, pBlockState, 7);
		this.slotModes = new ModeBuilder().addModes(7, Mode.BOTH).get();
		this.energyContainer = new BasicEnergyContainer(maxPower, maxPower, 0, 0);
		this.forgeEnergy = new HybridEnergyStorage(this.energyContainer);
		this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(energyContainer));
		this.capabilitiesContent.addCapability(ForgeCapabilities.ENERGY, this.forgeEnergy);
		this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.fluidHandler);
		this.multiblockData = MultiblockData.mapping.get(ModBlocks.LAUNCH_PAD.get());
	}

	@Override public boolean isReadyForLaunch() { return delay <= 0; }
	@Override public double getLaunchOffset() { return 1D; }

	public int delay = 0;

	@Override
	protected void onUpdateServer() {
		if(this.delay > 0) delay--;

		if(!this.isMissileValid() || !this.hasFuel()) {
			this.delay = 100;
			this.toRender = this.getItem(0);
		}

		if(!this.hasFuel() || !this.isMissileValid()) {
			this.state = this.STATE_MISSING;
		} else {
			if(this.delay > 0) {
				this.state = this.STATE_LOADING;
			} else {
				this.state = this.STATE_READY;
			}
		}
		super.onUpdateServer();
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
		return new LaunchPadMenu(pContainerId, pInventory, this, containerData);
	}

	@Override
	protected void onUpdateClient() {
		Vec3 center = this.getTilePos().getCenter();
		List<EntityThrowableNT> missiles = level.getEntitiesOfClass(EntityThrowableNT.class, new AABB(center.x - 0.5, center.y, center.z - 0.5, center.x + 1.5, center.y, center.z + 1.5));
		for (EntityThrowableNT missile : missiles) {
			Direction facing = this.getBlockState().getValue(LaunchPad.FACING);
			Direction dir;
			for (int i = 0; i < 15; i++) {
				dir = level.random.nextBoolean() ? facing : facing.getOpposite();
				float moX = (float) (level.random.nextGaussian() * 0.15F + 0.75) * dir.getStepX();
				float moZ = (float) (level.random.nextGaussian() * 0.15F + 0.75) * dir.getStepZ();

//				NBTTagCompound data = new NBTTagCompound();
//				data.setDouble("posX", xCoord + 0.5);
//				data.setDouble("posY", yCoord + 0.25);
//				data.setDouble("posZ", zCoord + 0.5);
//				data.setString("type", "launchSmoke");
//				data.setDouble("moX", moX);
//				data.setDouble("moY", 0);
//				data.setDouble("moZ", moZ);
//				MainRegistry.proxy.effectNT(data);
			}
		}

	}

	@Override
	public void finalizeLaunch(Entity missile) {
		super.finalizeLaunch(missile);
		this.delay = 100;
	}

	@Override
	public void load(@NotNull CompoundTag nbt) {
		super.load(nbt);
		this.delay = nbt.getInt(HBMKey.DELAY);
	}

	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		pTag.putInt(HBMKey.DELAY, this.delay);
	}
}
