package com.hbm.item.tool;

import com.hbm.blockentity.machine.TileEntitySolarMirror;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.SlotItemHandler;

import javax.swing.plaf.PanelUI;

public class ItemMirrorTool extends Item {
    public ItemMirrorTool(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        ItemStack itemInHand = pContext.getItemInHand();
        BlockPos clickedPos = pContext.getClickedPos();
        BlockState blockState = level.getBlockState(clickedPos);
        if (!level.isClientSide && itemInHand.getItem() instanceof ItemMirrorTool){
            if (blockState.is(ModBlocks.MACHINE_SOLAR_BOILER.get())){
                itemInHand.getOrCreateTag().putIntArray("xyz", new int[]{clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()});
            }else if (blockState.is(ModBlocks.SOLAR_MIRROR.get())){
                if (level.getBlockEntity(clickedPos) instanceof TileEntitySolarMirror solarMirror){
                    int[] intArray = itemInHand.getOrCreateTag().getIntArray("xyz");
                    if (intArray != null && intArray.length >= 3) solarMirror.setTarget(intArray[0], intArray[1], intArray[2]);
                }
            }
        }
        return super.useOn(pContext);
    }

}
