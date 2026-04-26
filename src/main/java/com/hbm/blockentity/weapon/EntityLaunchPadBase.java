package com.hbm.blockentity.weapon;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.api.Mode;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.block.weapon.IBomb.*;
import com.hbm.blockentity.IRadarCommandReceiver;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.entity.weapon.missile.EntityMissile;
import com.hbm.entity.weapon.missile.EntityMissileAntiBallistic;
import com.hbm.item.weapon.ItemDesignator;
import com.hbm.item.weapon.ItemMissile;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public abstract class EntityLaunchPadBase extends DummyableBlockEntity implements IRadarCommandReceiver {
	public ItemStack toRender;
//
//	public long power;
	public static final long maxPower = 100_000;
//
//	public int prevRedstonePower;
//	public int redstonePower;
//	public Set<BlockPos> activatedBlocks = new HashSet<>(4);
//
	public int state = 0;
	public static final int STATE_MISSING = 0;
	public static final int STATE_LOADING = 1;
	public static final int STATE_READY = 2;
//
//	public FluidTank[] tanks;

	protected BasicEnergyContainer energyContainer;
	protected BasicFluidHandler fluidHandler;

	/**
	 * 物品槽至少有三个
	 * - 0：导弹
	 * - 1：控制器
	 * - 2：电池
	 * */
	public EntityLaunchPadBase(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int slotNum) {
		super(pType, pPos, pBlockState);
		items = NonNullList.withSize(slotNum, ItemStack.EMPTY);
		this.fluidHandler = new BasicFluidHandler().addTanks(2, 24_000, Mode.INPUT);
	}

	@Override
	public Component getDefaultName() {
		return Component.translatable(HBMLang.CONTAINER_LAUNCHPAD.key());
	}


	@Override
	protected void onUpdateServer() {
		super.onUpdateServer();

		TransmitUtils.dischargeItem(this, this.getItem(2));

		this.toRender = getItem(0);

//		if(this.isMissileValid()) {
//			if(this.getItem(0).getItem() instanceof ItemMissile missile) {
//				setFuel(missile);
//			}
//		}

		sendUpdatePacket();
	}

	@Override
	public @NotNull CompoundTag getReducedUpdateTag() {
		CompoundTag tag = new CompoundTag();
		if (this.toRender != null) tag.put(HBMKey.ITEM, this.toRender.serializeNBT());
		return tag;
	}

	@Override
	public void handleUpdatePacket(@NotNull CompoundTag tag) {
		CompoundTag tag1 = tag.getCompound(HBMKey.ITEM);
		if (!tag1.isEmpty()) this.toRender = ItemStack.of(tag1);
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		if(level.getBlockEntity(this.getTilePos()) != this) {
			return false;
		} else {
			return pPlayer.distanceToSqr(this.getTilePos().getCenter()) <= 128 && super.stillValid(pPlayer);
		}
	}

	@Override
	public void load(@NotNull CompoundTag nbt) {
		super.load(nbt);
		this.energyContainer.deserializeNBT(nbt.getCompound(HBMKey.ENERGY));
		this.fluidHandler.deserializeNBT(nbt.getCompound(HBMKey.FLUIDS));
	}

	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		pTag.put(HBMKey.ENERGY, this.energyContainer.serializeNBT());
		pTag.put(HBMKey.FLUIDS, this.fluidHandler.serializeNBT());
	}

	@Override
	public BasicEnergyContainer getEnergyContainer() {
		return energyContainer;
	}

	public BasicFluidHandler getFluidHandler() {
		return fluidHandler;
	}
	//	@Override
//	public void updateEntity() {
//
//		if(!worldObj.isRemote) {
//
//			if(worldObj.getTotalWorldTime() % 20 == 0) {
//				for(DirPos pos : getConPos()) {
//					this.trySubscribe(worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
//					if(tanks[0].getTankType() != Fluids.NONE) this.trySubscribe(tanks[0].getTankType(), worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
//					if(tanks[1].getTankType() != Fluids.NONE) this.trySubscribe(tanks[1].getTankType(), worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
//				}
//			}
//
//			if(this.redstonePower > 0 && this.prevRedstonePower <= 0) {
//				this.launchFromDesignator();
//			}
//
//			this.prevRedstonePower = this.redstonePower;
//
//			this.power = Library.chargeTEFromItems(slots, 2, power, maxPower);
//			tanks[0].loadTank(3, 4, slots);
//			tanks[1].loadTank(5, 6, slots);
//
//			if(this.isMissileValid()) {
//				if(slots[0].getItem() instanceof ItemMissile) {
//					ItemMissile missile = (ItemMissile) slots[0].getItem();
//					setFuel(missile);
//				}
//			}
//
//			this.networkPackNT(250);
//		}
//	}
//
//	@Override
//	public void serialize(ByteBuf buf) {
//		super.serialize(buf);
//
//		buf.writeLong(this.power);
//		buf.writeInt(this.state);
//		tanks[0].serialize(buf);
//		tanks[1].serialize(buf);
//
//		if(slots[0] != null) {
//			buf.writeBoolean(true);
//			buf.writeInt(Item.getIdFromItem(slots[0].getItem()));
//			buf.writeShort((short) slots[0].getItemDamage());
//		} else {
//			buf.writeBoolean(false);
//		}
//	}
//
//	@Override
//	public void deserialize(ByteBuf buf) {
//		super.deserialize(buf);
//
//		this.power = buf.readLong();
//		this.state = buf.readInt();
//		tanks[0].deserialize(buf);
//		tanks[1].deserialize(buf);
//
//		if(buf.readBoolean()) {
//			this.toRender = new ItemStack(Item.getItemById(buf.readInt()), 1, buf.readShort());
//		} else {
//			this.toRender = null;
//		}
//	}

//	@Override public boolean canConnect(ForgeDirection dir) {
//		return dir != ForgeDirection.UP && dir != ForgeDirection.DOWN;
//	}
//
//	@Override
//	public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
//		return new ContainerLaunchPadLarge(player.inventory, this);
//	}
//
//	@Override
//	@SideOnly(Side.CLIENT)
//	public Object provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
//		return new GUILaunchPadLarge(player.inventory, this);
//	}
	
//	@SuppressWarnings("incomplete-switch") //shut up
//	public void setFuel(ItemMissile missile) {
//		switch(missile.fuel) {
//		case ETHANOL_PEROXIDE:
//			tanks[0].setTankType(Fluids.ETHANOL);
//			tanks[1].setTankType(Fluids.PEROXIDE);
//			break;
//		case KEROSENE_PEROXIDE:
//			tanks[0].setTankType(Fluids.KEROSENE);
//			tanks[1].setTankType(Fluids.PEROXIDE);
//			break;
//		case KEROSENE_LOXY:
//			tanks[0].setTankType(Fluids.KEROSENE);
//			tanks[1].setTankType(Fluids.OXYGEN);
//			break;
//		case JETFUEL_LOXY:
//			tanks[0].setTankType(Fluids.KEROSENE_REFORM);
//			tanks[1].setTankType(Fluids.OXYGEN);
//			break;
//		}
//	}
	
	/** Requires the missile slot to be non-null and he item to be compatible */
	public boolean isMissileValid() {
		return !this.getItem(0).isEmpty() && isMissileValid(this.getItem(0));
	}
	
	public boolean isMissileValid(ItemStack stack) {
		if (!(stack.getItem() instanceof ItemMissile missile) || !missile.launchable) {
			return false;
		}
		return missile.formFactor == ItemMissile.MissileFormFactor.ABM || missile.missileCreator != null;
	}
	
	public boolean hasFuel() {
		return true;
//		if (getStored() < 75_000) return false;
//		ItemStack itemStack = getItem(0);
//
//		return itemStack.getItem() instanceof ItemMissile missile
//				&& this.fluidHandler.getFluidInTank(0).getAmount() >= missile.fuelCap
//				&& this.fluidHandler.getFluidInTank(0).getAmount() >= missile.fuelCap;
	}

	public Entity instantiateMissile(int targetX, int targetZ) {
		if (this.getItem(0).isEmpty()) return null;

		ItemStack item = this.getItem(0);
		if (item.getItem() instanceof ItemMissile itemMissile){
			Vec3 posCenter = this.worldPosition.getCenter();
			if (itemMissile.formFactor == ItemMissile.MissileFormFactor.ABM) {
				EntityMissileAntiBallistic missile = new EntityMissileAntiBallistic(level);
				missile.setPos(posCenter.x, this.worldPosition.getY() + getLaunchOffset(), posCenter.z);
				return missile;
			}
			if (itemMissile.missileCreator == null) {
				return null;
			}
			EntityMissile entityMissile = itemMissile.missileCreator.create(level, (float) posCenter.x, (float) (this.worldPosition.getY() + getLaunchOffset()), (float) posCenter.z, new BlockPos(targetX, this.worldPosition.getY(), targetZ));
			return entityMissile;
		}

//		Class<? extends EntityMissileBaseNT> clazz = TileEntityLaunchPadBase.missiles.get(new ComparableStack(slots[0]).makeSingular());
//
//		if(clazz != null) {
//			try {
//				EntityMissileBaseNT missile = clazz.getConstructor(World.class, float.class, float.class, float.class, int.class, int.class).newInstance(worldObj, xCoord + 0.5F, yCoord + (float) getLaunchOffset() /* Position arguments need to be floats, jackass */, zCoord + 0.5F, targetX, targetZ);
//				if(GeneralConfig.enableExtendedLogging) MainRegistry.logger.log(Level.INFO, "[MISSILE] Tried to launch missile at " + xCoord + " / " + yCoord + " / " + zCoord + " to " + xCoord + " / " + zCoord + "!");
//				missile.getDataWatcher().updateObject(3, (byte) MathHelper.clamp_int(this.getBlockMetadata() - 10, 2, 5));
//				return missile;
//			} catch(Exception e) { }
//		}
//
//		if(slots[0].getItem() == ModItems.missile_anti_ballistic) {
//			EntityMissileAntiBallistic missile = new EntityMissileAntiBallistic(worldObj);
//			missile.posX = xCoord + 0.5D;
//			missile.posY = yCoord + getLaunchOffset();
//			missile.posZ = zCoord + 0.5D;
//			return missile;
//		}
		
		return null;
	}
	
	public void finalizeLaunch(Entity missile) {
		level.addFreshEntity(missile);
		level.playSound(null, this.getTilePos().getX() + 0.5, this.getTilePos().getY(), this.getTilePos().getZ() + 0.5, ModSounds.WEAPON_MISSILE_TAKE_OFF.get(), SoundSource.AMBIENT, 2.0f, 1.0f);
		this.energyContainer.extract(75_000, false);
		ItemStack itemStack = this.getItem(0);
		if (!itemStack.isEmpty() && itemStack.getItem() instanceof  ItemMissile itemMissile){
			this.fluidHandler.getFluidTanks().get(0).drain(itemMissile.fuelCap, IFluidHandler.FluidAction.EXECUTE);
			this.fluidHandler.getFluidTanks().get(1).drain(itemMissile.fuelCap, IFluidHandler.FluidAction.EXECUTE);
		}
		this.getItem(0).shrink(1);


//		worldObj.spawnEntityInWorld(missile);
//		TrackerUtil.setTrackingRange(worldObj, missile, 500);
//		worldObj.playSoundEffect(xCoord + 0.5, yCoord, zCoord + 0.5, "hbm:weapon.missileTakeOff", 2.0F, 1.0F);
//
//		this.power -= 75_000;
//
//		if(slots[0] != null && slots[0].getItem() instanceof ItemMissile) {
//			ItemMissile item = (ItemMissile) slots[0].getItem();
//			tanks[0].setFill(tanks[0].getFill() - item.fuelCap);
//			tanks[1].setFill(tanks[1].getFill() - item.fuelCap);
//		}
//
//		this.decrStackSize(0, 1);
	}
	
	public BombReturnCode launchFromDesignator() {
		if(!canLaunch()) return BombReturnCode.ERROR_MISSING_COMPONENT;
		
		boolean needsDesignator = needsDesignator(this.getItem(0).getItem());

		int targetX = this.worldPosition.getX();
		int targetZ = this.worldPosition.getZ();

		ItemStack itemStack = this.getItem(1);
		if (needsDesignator){
			if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemDesignator designator){
				if (!designator.isReady(level, itemStack, this.worldPosition)) return BombReturnCode.ERROR_MISSING_COMPONENT;
				Vec3 coords = designator.getCoords(level, itemStack, this.worldPosition);
				targetX = (int) Math.floor(coords.x);
				targetZ = (int) Math.floor(coords.z);
			} else {
				return BombReturnCode.ERROR_MISSING_COMPONENT;
			}
		}
		
		return this.launchToCoordinate(targetX, targetZ);
	}
	
	public BombReturnCode launchToEntity(Entity entity) {
		if(!canLaunch()) return BombReturnCode.ERROR_MISSING_COMPONENT;
		
		Entity e = instantiateMissile(entity.getBlockX(), entity.getBlockZ());
		if(e != null) {
			
//			if(e instanceof EntityMissileAntiBallistic) {
//				EntityMissileAntiBallistic abm = (EntityMissileAntiBallistic) e;
//				abm.tracking = entity;
//			}
			
			finalizeLaunch(e);
			return BombReturnCode.LAUNCHED;
		}
		return BombReturnCode.ERROR_MISSING_COMPONENT;
	}
	
	public BombReturnCode launchToCoordinate(int targetX, int targetZ) {
		if(!canLaunch()) return BombReturnCode.ERROR_MISSING_COMPONENT;
		
		Entity e = instantiateMissile(targetX, targetZ);
		if(e != null) {
			finalizeLaunch(e);
			return BombReturnCode.LAUNCHED;
		}
		return BombReturnCode.ERROR_MISSING_COMPONENT;
	}


	@Override
	public boolean sendCommandPosition(int x, int y, int z) {
		return this.launchToCoordinate(x, z) == BombReturnCode.LAUNCHED;
	}

	@Override
	public boolean sendCommandEntity(Entity target) {
		return this.launchToEntity(target) == BombReturnCode.LAUNCHED;
	}
	
	public boolean needsDesignator(Item item) {
		if (item instanceof ItemMissile missile) {
			return missile.formFactor != ItemMissile.MissileFormFactor.ABM;
		}
		return true;
	}
	
	/** Full launch condition, checks if the item is launchable, fuel and power are present and any additional checks based on launch pad type */
	public boolean canLaunch() {
//		return this.isMissileValid() && this.hasFuel() && this.isReadyForLaunch();
		return this.isMissileValid() && this.isReadyForLaunch();
	}
	
	public int getFuelState() {
		return getGaugeState(0);
	}
	
	public int getOxidizerState() {
		return getGaugeState(1);
	}
	
	public int getGaugeState(int tank) {
		ItemStack itemStack = this.getItem(0);

		if(!itemStack.isEmpty() && itemStack.getItem() instanceof ItemMissile missile) {
			ItemMissile.MissileFuel fuel = missile.fuel;
			
			if(fuel == ItemMissile.MissileFuel.SOLID) return 0;
			return fluidHandler.getFluidInTank(tank).getAmount() >= missile.fuelCap ? 1 : -1;
		}
		
		return 0;
	}

	public void updateRedstonePower(BlockPos pPos){

	}
	
	/** Any extra conditions for launching in addition to the missile being valid and fueled */
	public abstract boolean isReadyForLaunch();
	public abstract double getLaunchOffset();

}
