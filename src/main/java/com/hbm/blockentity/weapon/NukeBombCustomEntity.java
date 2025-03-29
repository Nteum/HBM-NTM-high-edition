package com.hbm.blockentity.weapon;

import com.hbm.blockentity.ModBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class NukeBombCustomEntity extends NukeBombEntity{
    public static final AABB BOX = AABB.of(new BoundingBox(-1,0,-1,2,1,1));
    public NukeBombCustomEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.NUKE_BOMB_CUSTOM_ENTITY.get(),pPos, pBlockState);
    }

//    @Override
//    public AABB getRenderBoundingBox() {
//        return BOX;
//    }
}
