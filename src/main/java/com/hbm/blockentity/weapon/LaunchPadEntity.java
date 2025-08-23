package com.hbm.blockentity.weapon;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BedLikeBlockEntity;
import com.hbm.entity.weapon.missile.EntityMissile;
import com.hbm.item.weapon.ItemDesignator;
import com.hbm.item.weapon.ItemMissile;
import com.hbm.registries.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LaunchPadEntity extends BedLikeBlockEntity {
    public ItemStack toRender;
    public boolean flagImpulse = false;
    public int countdown = MAX_COUNTDOWN;
    static int MAX_COUNTDOWN = 100;
    protected State state = State.MISSING;
    public LaunchPadEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.LAUNCHPAD_ENTITY.get(),pPos, pBlockState);
        items = NonNullList.withSize(7,ItemStack.EMPTY);
    }

    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (!level.isClientSide() && pBlockEntity instanceof LaunchPadEntity entity){
            if (!entity.checkMissile()){
                entity.state = State.MISSING;
                entity.countdown = MAX_COUNTDOWN;
            }else {
                if (entity.state == State.LOADING && entity.flagImpulse){
                    entity.flagImpulse = false;
                    entity.countdown = MAX_COUNTDOWN;
                    entity.state = State.READY;
                }
                if (entity.state == State.READY){
                    if (entity.countdown > 0)entity.countdown--;
                    else entity.launch();
                }
            }
        }
    }

    public boolean checkMissile(){
        return items.get(0).is(ModTags.Items.MISSILE) && hasFuel();
    }

    public boolean hasFuel(){
        return true;
    }
    //发射导弹
    public void launch(){
        int targetX = 0;
        int targetZ = 0;
        ItemStack itemStack = items.get(1);
        if (itemStack.getItem() instanceof ItemDesignator){
            if (itemStack.hasTag() && itemStack.getTag().contains("pos")){
                int[] pos = itemStack.getTag().getIntArray("pos");
                if (pos.length == 3){
                    EntityMissile entityMissile = ((ItemMissile) items.get(0).getItem()).createEntity(level, worldPosition, new BlockPos(pos[0], pos[1], pos[2]));
                    assert level != null;
                    level.addFreshEntity(entityMissile);
                    this.level.playLocalSound(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),
                            SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 6.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
                    items.set(0,ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("container.launchpad");
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }

    public void updateRedstonePower(BlockPos pos) {
        if (state == State.LOADING)
            flagImpulse = true;
    }

    public static enum State{
        MISSING, LOADING, READY;
    }
}
