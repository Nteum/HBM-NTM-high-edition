package com.hbm.block.weapon;

import com.hbm.blockentity.base2.TileProxyCombo;
import com.hbm.blockentity.machine.ChemplantEntity;
import com.hbm.blockentity.weapon.NukeBombBoyEntity;
import com.hbm.blockentity.weapon.NukeBombFatEntity;
import com.hbm.utils.MultipartUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NukeBoy extends NukeBomb{
//    public static final VoxelShape this.shape = Block.box(-30,0,0,24,16,16);
    public NukeBoy(Properties pProperties, int range) {
        super(pProperties, range);
        this.shape = Block.box(-24,0,0,16,16,16);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new NukeBombBoyEntity(pPos,pState);
    }

    @Override
    protected ExplosionVisual getExplosionVisual() {
        return ExplosionVisual.THERMOBARIC;
    }
}
