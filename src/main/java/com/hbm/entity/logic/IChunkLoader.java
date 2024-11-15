package com.hbm.entity.logic;

import net.minecraft.server.level.Ticket;
import net.minecraftforge.common.world.ForgeChunkManager;

public interface IChunkLoader {
    public void init(Ticket ticket);
}
