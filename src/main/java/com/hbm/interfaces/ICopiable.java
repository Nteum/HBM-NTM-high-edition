package com.hbm.interfaces;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ICopiable {

    CompoundTag getSettings(Level world, int x, int y, int z);

    void pasteSettings(CompoundTag nbt, int index, Level world, PlayerEntry player, int x, int y, int z);

    default String getSettingsSourceID(Either<BlockEntity, Block> self) {
        Block block1 = self.right().get();
        Block block =  self.right().isEmpty() ? self.left().get().getBlockState().getBlock() : block1;
        return block.getDescriptionId();
    }

//    default String getSettingsSourceDisplay(Either<BlockEntity, Block> self) {
//        Block block1 = self.right().get();
//        Block block =  self.right().isEmpty() ? self.left().get().getBlockState().getBlock() : block1;
//        return block.getName();
//    }

    default String[] infoForDisplay(Level world, int x, int y, int z){
        return null;
    }
}
