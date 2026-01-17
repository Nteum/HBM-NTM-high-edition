package com.hbm.reactor.pile;

import com.hbm.api.block.IPileNeutronReceiver;
import com.hbm.block.machine.pile.ChicagoGraphiteDetectorBlock;
import com.hbm.block.machine.pile.ChicagoGraphiteRodBlock;
import com.hbm.block.machine.pile.ChicagoPileStateProperties;
import com.hbm.blockentity.machine.pile.ChicagoPileBlockEntity;
import com.hbm.utils.ContaminationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Legacy-style neutron ray casting used by the Chicago Pile components.
 */
public final class PileNeutronHandler {

    private static final int RANGE = 5;

    private PileNeutronHandler() {
    }

    public static PileNeutronNode makeNode(NeutronNodeWorld.StreamWorld world, ChicagoPileBlockEntity tile) {
        NeutronNode node = world.getNode(tile.getBlockPos());
        if (node instanceof PileNeutronNode pileNode) {
            return pileNode;
        }
        PileNeutronNode created = new PileNeutronNode(tile);
        world.addNode(created);
        return created;
    }

    public static final class PileNeutronNode extends NeutronNode {

        public PileNeutronNode(ChicagoPileBlockEntity tile) {
            super(tile);
        }

        public ChicagoPileBlockEntity pile() {
            return (ChicagoPileBlockEntity) blockEntity();
        }
    }

    public static final class PileNeutronStream extends NeutronStream {

        public PileNeutronStream(NeutronNode origin, Vec3 direction, double fluxQuantity) {
            super(origin, direction.normalize(), fluxQuantity);
        }

        @Override
        public void run(Level level, NeutronNodeWorld.StreamWorld streamWorld) {
            if (isDead()) {
                return;
            }
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            BlockPos start = originPos();

            for (double i = 0.5D; i <= RANGE; i += 0.5D) {
                int x = Mth.floor(start.getX() + 0.5D + direction.x * i);
                int y = Mth.floor(start.getY() + 0.5D + direction.y * i);
                int z = Mth.floor(start.getZ() + 0.5D + direction.z * i);
                cursor.set(x, y, z);

                if (cursor.equals(start)) {
                    continue;
                }

                BlockState state = level.getBlockState(cursor);
                BlockEntity tile = level.getBlockEntity(cursor);
                if (tile instanceof ChicagoPileBlockEntity pileTile) {
                    streamWorld.addNode(new PileNeutronNode(pileTile));
                }

                if (tile instanceof IPileNeutronReceiver receiver) {
                    receiver.receiveNeutrons((int) Math.floor(fluxQuantity));
                    if (!allowsThrough(state)) {
                        return;
                    }
                } else if (blocksFlux(state)) {
                    return;
                }

                contaminateEntities(level, cursor);
            }
        }

        private static boolean blocksFlux(BlockState state) {
            Block block = state.getBlock();
            if (block instanceof ChicagoGraphiteRodBlock rodBlock) {
                return rodBlock.isInserted(state);
            }
            return false;
        }

        private static boolean allowsThrough(BlockState state) {
            return state.getBlock() instanceof ChicagoGraphiteDetectorBlock detector && detector.isPowered(state);
        }

        private void contaminateEntities(Level level, BlockPos pos) {
            if (fluxQuantity <= 0.0D) {
                return;
            }
            AABB box = new AABB(pos).inflate(0.5D);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box);
            if (entities.isEmpty()) {
                return;
            }
            float dose = (float) (fluxQuantity / 4.0D);
            for (LivingEntity entity : entities) {
                ContaminationUtil.contaminate(entity,
                        ContaminationUtil.HazardType.RADIATION,
                        ContaminationUtil.ContaminationType.CREATIVE,
                        dose);
            }
        }
    }
}
