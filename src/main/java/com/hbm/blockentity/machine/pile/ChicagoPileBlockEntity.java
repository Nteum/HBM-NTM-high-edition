package com.hbm.blockentity.machine.pile;

import com.hbm.api.block.IPileNeutronReceiver;
import com.hbm.reactor.pile.NeutronNodeWorld;
import com.hbm.reactor.pile.PileNeutronHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class ChicagoPileBlockEntity extends BlockEntity implements IPileNeutronReceiver {

    protected ChicagoPileBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public interface ServerTicker<T extends ChicagoPileBlockEntity> {
        void tick(Level level, BlockPos pos, BlockState state, T blockEntity);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ChicagoPileBlockEntity be) {
        if (!level.isClientSide) {
            be.serverTick();
        }
    }

    protected abstract void serverTick();

    protected void emitNeutrons(int fluxPerStream, int streams) {
        if (level == null || fluxPerStream <= 0 || streams <= 0) {
            return;
        }
        NeutronNodeWorld.StreamWorld streamWorld = NeutronNodeWorld.getOrCreate(level);
        PileNeutronHandler.PileNeutronNode node = PileNeutronHandler.makeNode(streamWorld, this);
        RandomSource random = level.getRandom();
        for (int i = 0; i < streams; i++) {
            Vec3 dir = randomDirection(random);
            streamWorld.queue(new PileNeutronHandler.PileNeutronStream(node, dir, fluxPerStream));
        }
    }

    private Vec3 randomDirection(RandomSource random) {
        double theta = random.nextDouble() * Math.PI * 2.0D;
        double phi = Math.acos(2.0D * random.nextDouble() - 1.0D);
        double x = Math.sin(phi) * Math.cos(theta);
        double y = Math.sin(phi) * Math.sin(theta);
        double z = Math.cos(phi);
        return new Vec3(x, y, z);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level != null) {
            NeutronNodeWorld.StreamWorld world = NeutronNodeWorld.getOrCreate(level);
            world.removeNode(worldPosition);
        }
    }
}
