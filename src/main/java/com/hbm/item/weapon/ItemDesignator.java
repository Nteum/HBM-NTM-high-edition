package com.hbm.item.weapon;

import com.hbm.HBMKey;
import com.hbm.api.item.IDesignatorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDesignator extends Item implements IDesignatorItem {
    public ItemDesignator(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();
        if (!level.isClientSide() && player != null){
            ItemStack itemInHand = pContext.getItemInHand();
            if (player.isCrouching() && itemInHand.getItem() instanceof ItemDesignator){
                BlockPos clickedPos = pContext.getClickedPos();
                itemInHand.getOrCreateTag().put(HBMKey.POSITION, NbtUtils.writeBlockPos(clickedPos));
                return InteractionResult.sidedSuccess(false);
            }
        }
        return super.useOn(pContext);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        if (isReady(pLevel, pStack, new BlockPos(0,0,0))) {
            pTooltipComponents.add(Component.literal(NbtUtils.readBlockPos(pStack.getTagElement(HBMKey.POSITION)).toShortString()));
        }
    }

    @Override
    public boolean isReady(Level world, ItemStack stack, BlockPos pos) {
        if (!stack.hasTag()) {
            return false;
        }
        CompoundTag positionTag = stack.getTagElement(HBMKey.POSITION);
        return positionTag != null && !positionTag.isEmpty();
    }

    @Override
    public Vec3 getCoords(Level world, ItemStack stack, BlockPos pos) {
        if (!isReady(world, stack, pos)) {
            return pos.getCenter();
        }
        return NbtUtils.readBlockPos(stack.getTagElement(HBMKey.POSITION)).getCenter();
    }
}
