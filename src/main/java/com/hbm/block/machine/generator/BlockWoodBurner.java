package com.hbm.block.machine.generator;

import com.hbm.block.base.BlockDummyable;
import com.hbm.block.interfaces.ITooltipProvider;
import com.hbm.blockentity.generator.TileWoodBurner;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
/**
 * GUI - 0.625; ground - 0.25; fixed - 0.5; third-right - 0.275; first - 0.4
 * */
public class BlockWoodBurner extends BlockDummyable implements ITooltipProvider {
    public BlockWoodBurner(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileWoodBurner(pPos, pState);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    @Override
    public void addInformation(ItemStack stack, Player player, List list, boolean ext) {
        this.addStandardInfo(stack, player, list, ext);
    }
}
