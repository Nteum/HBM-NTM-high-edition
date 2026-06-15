package com.hbm.block.env;

import com.hbm.block.interfaces.IToolable;
import com.hbm.block.interfaces.ToolType;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

// 石墨方块
public class BlockGraphite extends BlockFireCustom implements IToolable {
    public BlockGraphite(Properties pProperties, int encouragement, int flammability) {
        super(pProperties, encouragement, flammability);
    }

    @Override
    public boolean onScrew(UseOnContext context, ToolType tool) {
        if(tool != ToolType.HAND_DRILL)
            return false;
        Level world = context.getLevel();
        if(!world.isClientSide) {
//            world.setBlock(x, y, z, ModBlocks.block_graphite_drilled, side / 2, 3);
//            PacketDispatcher.wrapper.sendToAllAround(new ParticleBurstPacket(x, y, z, Block.getIdFromBlock(this), 0), new TargetPoint(world.provider.dimensionId, x, y, z, 50));
//            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, this.stepSound.func_150496_b(), (this.stepSound.getVolume() + 1.0F) / 2.0F, this.stepSound.getPitch() * 0.8F);
//
//            BlockGraphiteRod.ejectItem(world, x, y, z, ForgeDirection.getOrientation(side), new ItemStack(ModItems.ingot_graphite));
        }

        return true;
    }
}
