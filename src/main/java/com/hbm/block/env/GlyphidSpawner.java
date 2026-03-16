package com.hbm.block.env;

import com.hbm.addational_data.Pollution;
import com.hbm.block.HBMBlockProperties;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.config.MobConfig;
import com.hbm.entity.mob.EntityGlyphid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class GlyphidSpawner extends BaseEntityBlock {
    /*
     * 0 - base
     * 1 - infected
     * 2 - radiation
     * */
    public static final IntegerProperty VARIANT = HBMBlockProperties.VARIANT3;

    private static final ArrayList<Pair<Function<Level, EntityGlyphid>, int[]>> spawnMap = new ArrayList<>();

    static {
        // big thanks to martin for the suggestion of using functions
        spawnMap.add(new Pair<>(EntityGlyphid::new,				MobConfig.glyphidChance));
//        spawnMap.add(new Pair<>(EntityGlyphidBombardier::new,	MobConfig.bombardierChance));
//        spawnMap.add(new Pair<>(EntityGlyphidBrawler::new,		MobConfig.brawlerChance));
//        spawnMap.add(new Pair<>(EntityGlyphidDigger::new,		MobConfig.diggerChance));
//        spawnMap.add(new Pair<>(EntityGlyphidBlaster::new,		MobConfig.blasterChance));
//        spawnMap.add(new Pair<>(EntityGlyphidBehemoth::new,		MobConfig.behemothChance));
//        spawnMap.add(new Pair<>(EntityGlyphidBrenda::new,		MobConfig.brendaChance));
//        spawnMap.add(new Pair<>(EntityGlyphidNuclear::new,		MobConfig.johnsonChance));
    }
    public GlyphidSpawner(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(VARIANT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(VARIANT);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return GlyphidSpawnerEntity::tick;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new GlyphidSpawnerEntity(pPos, pState);
    }

    public static class GlyphidSpawnerEntity extends BlockEntity{

        boolean initialSpawn = true;

        public GlyphidSpawnerEntity(BlockPos pPos, BlockState pBlockState) {
            super(ModBlockEntityType.GLYPHID_SPAWNER.get(), pPos, pBlockState);
        }

        public static <T extends Entity> void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity){
            if (!level.isClientSide() && level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && pBlockEntity instanceof GlyphidSpawnerEntity spawner){
                if (spawner.initialSpawn || level.random.nextInt(MobConfig.swarmCooldown) == 0){
                    spawner.initialSpawn = false;
                    int count = 0;

                    for (EntityGlyphid entity : level.getEntitiesOfClass(EntityGlyphid.class, spawner.getRenderBoundingBox().inflate(64, 64, 64), (entity) -> true)) {
                        if (++count >= MobConfig.spawnMax) return;
                    }
                    float soot = Pollution.getPollution(level, spawner.getBlockPos(), Pollution.Type.SOOT);
                    int subtype = spawner.getBlockState().getValue(VARIANT);

                    if (count <= 3 || subtype == 2){
                        ArrayList<EntityGlyphid> currentSwarm = spawner.createSwarm(soot, subtype);

                        for(EntityGlyphid glyphid : currentSwarm) {
                            spawner.trySpawnEntity(glyphid);
                        }

                        if(!spawner.initialSpawn && level.random.nextInt(MobConfig.scoutSwarmSpawnChance + 1) == 0 && soot >= MobConfig.scoutThreshold && subtype != EntityGlyphid.TYPE_RADIOACTIVE) {
//                            EntityGlyphidScout scout = new EntityGlyphidScout(worldObj);
                            EntityGlyphid scout = new EntityGlyphid(level);
                            if(spawner.getBlockState().getValue(VARIANT) == 1) scout.getEntityData().set(EntityGlyphid.DATA_SUBTYPE, (byte) EntityGlyphid.TYPE_INFECTED);
                            spawner.trySpawnEntity(scout);
                        }
                    }
                }
            }
        }

        public void trySpawnEntity(EntityGlyphid glyphid) {
            double offsetX = glyphid.getRandom().nextGaussian() * 3;
            double offsetZ = glyphid.getRandom().nextGaussian() * 3;
            Vec3 center = getBlockPos().getCenter();

            for(int i = 0; i < 7; i++) {
                glyphid.setPos(center.x + 0.5 + offsetX, center.y - 2 + i, center.z + 0.5 + offsetZ);
                glyphid.setYRot(level.random.nextFloat() * 360.0F);
//                glyphid.setPos(xCoord + 0.5 + offsetX, yCoord - 2 + i, zCoord + 0.5 + offsetZ, worldObj.rand.nextFloat() * 360.0F, 0.0F);
                if(glyphid.getCanSpawnHere()) {
                    level.addFreshEntity(glyphid);
                    return;
                }
            }
        }

        public ArrayList<EntityGlyphid> createSwarm(float soot, int meta) {

            Random rand = new Random();
            ArrayList<EntityGlyphid> currentSpawns = new ArrayList<>();
            int swarmAmount = (int) Math.min(MobConfig.baseSwarmSize * Math.max(MobConfig.swarmScalingMult * (soot / MobConfig.sootStep), 1), 10);
            int cap = 100;

            while(currentSpawns.size() <= swarmAmount && cap >= 0) {
                // (dys)functional programing
                for(Pair<Function<Level, EntityGlyphid>, int[]> glyphid : spawnMap) {
                    int[] chance = glyphid.getSecond();
                    int adjustedChance = (int) (chance[0] + (chance[1] - chance[1] / Math.max(((soot + 1) / 3), 1)));
                    if(soot >= chance[2] && rand.nextInt(100) <= adjustedChance) {
                        EntityGlyphid entity = glyphid.getFirst().apply(level);
                        if(meta == 1) entity.getEntityData().set(EntityGlyphid.DATA_SUBTYPE, (byte) EntityGlyphid.TYPE_INFECTED);
                        if(meta == 2) entity.getEntityData().set(EntityGlyphid.DATA_SUBTYPE, (byte) EntityGlyphid.TYPE_RADIOACTIVE);
                        currentSpawns.add(entity);
                    }
                }

                cap--;
            }
            return currentSpawns;
        }

        @Override
        protected void saveAdditional(CompoundTag pTag) {
            super.saveAdditional(pTag);
            pTag.putBoolean("initialSpawn", initialSpawn);
        }

        @Override
        public void load(CompoundTag pTag) {
            super.load(pTag);
            initialSpawn = pTag.getBoolean("initialSpawn");
        }
    }
}
