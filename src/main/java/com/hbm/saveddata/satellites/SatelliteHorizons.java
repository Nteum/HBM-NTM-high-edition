package com.hbm.saveddata.satellites;

import com.hbm.saveddata.SatelliteSavedData;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

public class SatelliteHorizons extends Satellite {

    boolean used = false;

    public SatelliteHorizons() {
        this.satIface = Interfaces.SAT_COORD;
    }

    @Override
    public void onOrbit(Level world, double x, double y, double z) {
        // TODO(port): MainRegistry.horizonsStart achievement not yet ported
        // for (Player p : world.players())
        //     p.awardStat(MainRegistry.horizonsStart);
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        super.writeToNBT(nbt);
        nbt.putBoolean("used", used);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        super.readFromNBT(nbt);
        used = nbt.getBoolean("used");
    }

    @Override
    public void onCoordAction(Level world, Player player, int x, int y, int z) {
        if (used) return;

        used = true;
        SatelliteSavedData.getData(world, x, z).setDirty();

        // TODO(port): EntityTom not yet ported
        // EntityTom tom = new EntityTom(world);
        // tom.setPos(x + 0.5, 600, z + 0.5);
        // world.getChunkSource().getChunk(x >> 4, z >> 4, ChunkStatus.FULL, true);
        // world.addFreshEntity(tom);

        // TODO(port): MainRegistry.horizonsEnd achievement not yet ported
        // for (Player p : world.players())
        //     p.awardStat(MainRegistry.horizonsEnd);

        if (!world.isClientSide()) {
            var server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                server.getPlayerList().broadcastSystemMessage(
                    Component.literal("Horizons has been activated.")
                        .withStyle(ChatFormatting.RED), false);
            }
        }
    }
}
