package com.hbm.blockentity.tools;

import com.hbm.addational_data.chunk.RadiationManager;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.CapabilityBlockEntity;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** 盖革计数器 */
public class TileEntityGeiger extends CapabilityBlockEntity {
    int timer = 0;
    float ticker = 0;
    public TileEntityGeiger(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.GEIGER_COUNTER.get(), pPos, pBlockState);
    }

    public static void ticker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (pBlockEntity instanceof TileEntityGeiger geiger && !level.isClientSide()){
            geiger.timer++;

            if(geiger.timer == 10) {
                geiger.timer = 0;
                geiger.ticker = check(level, pPos);

                // To update the adjacent comparators
                level.updateNeighborsAtExceptFromFacing(geiger.getTilePos(), geiger.getBlockState().getBlock(), null);
            }

            show(level, pPos, null, geiger.timer, geiger.ticker);
        }
    }
    
    public static void show(Level level, BlockPos blockPos, @Nullable Player player, int timer, float ticker){
        if(timer % 5 == 0) {

            if(ticker > 0) {
                List<Integer> list = new ArrayList<Integer>();

                if(ticker < 1) list.add(0);
                if(ticker < 5) list.add(0);
                if(ticker < 10) list.add(1);
                if(ticker > 5 && ticker < 15) list.add(2);
                if(ticker > 10 && ticker < 20) list.add(3);
                if(ticker > 15 && ticker < 25) list.add(4);
                if(ticker > 20 && ticker < 30) list.add(5);
                if(ticker > 25) list.add(6);

                int r = list.get(level.random.nextInt(list.size()));

                if (r > 0) {
                    if (player == null)
                        level.playSound(null, blockPos, geigerSounds[r], SoundSource.BLOCKS, 1.0f, 1.0f);
                    else player.playSound(geigerSounds[r], 1.0f, 1.0f);
                }
            } else if(level.random.nextInt(50) == 0) {
                if (player == null)
                    level.playSound(null, blockPos, geigerSounds[1 + level.random.nextInt(1)], SoundSource.BLOCKS, 1.0f, 1.0f);
                else player.playSound(geigerSounds[1 + level.random.nextInt(1)], 1.0f, 1.0f);
            }
        }
    }
    private static final SoundEvent[] geigerSounds = new SoundEvent[]{ModSounds.ITEM_GEIGER1.get(), ModSounds.ITEM_GEIGER2.get(),ModSounds.ITEM_GEIGER3.get(), ModSounds.ITEM_GEIGER4.get(),ModSounds.ITEM_GEIGER5.get(),ModSounds.ITEM_GEIGER6.get()};

    public static float check(Level level, BlockPos worldPosition) {
        return level == null ? 0f : RadiationManager.getRadiation(level, worldPosition);
    }
}
