package com.hbm.addational_data;

import com.hbm.HBMKey;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.config.MobConfig;
import com.hbm.config.RadiationConfig;
import com.hbm.entity.ModEntityType;
import com.hbm.entity.mob.EntityGlyphid;
import com.hbm.utils.DataTypeHelper;
import com.hbm.utils.EnumUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//@Mod.EventBusSubscriber(modid = HBM.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Pollution implements INBTSerializable<CompoundTag> {
    public static Pollution BLANK = new Pollution();
    public static float MAX_VALUE = 10_000f;
    public static int UPDATE_FREQUENCY = 10;
    protected static final float DESTRUCTION_THRESHOLD = 15F;
    protected static final int DESTRUCTION_COUNT = 5;
    /** Baserate of soot generation for a furnace-equivalent machine per second */
    public static final float SOOT_PER_SECOND = 1F / 25F;
    /** Baserate of heavy metal generation, balanced around the soot values of combustion engines */
    public static final float HEAVY_METAL_PER_SECOND = 1F / 50F;
    /** Baserate for poison when spilled */
    public static final float POISON_PER_SECOND = 1F / 50F;
    float[] pollution;

    public Pollution(){
        pollution = new float[Type.values().length];
        Arrays.fill(pollution, 0);
    }

    public static Pollution getPollution(Level level, ChunkPos pos){
        LevelChunk chunk = getLoadedChunk(level, pos);
        if (chunk != null){
            return AdditionalDataManager.getChunkData(chunk, DataEntry.POLLUTION).map(o -> o instanceof Pollution ? (Pollution) o : null).orElse(new Pollution());
        }
        // 我认为，如果一个区块本身没加载，那它不应当被视为0污染，而是暂时返回null
        return null;
    }

    public static Pollution getPollution(Level level, BlockPos pos){
        return getPollution(level, new ChunkPos(pos));
//        if (level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4) && (level.getChunk(pos) instanceof LevelChunk chunk)){
//            return AdditionalDataManager.getChunkData(chunk, DataEntry.POLLUTION).map(o -> o instanceof Pollution ? (Pollution) o : null).orElse(new Pollution());
//        }
//        // 我认为，如果一个区块本身没加载，那它不应当被视为0污染，而是暂时返回null
//        return null;
    }

    public static float getPollution(Level level, BlockPos pos, Type type) {
        Pollution pollution = getPollution(level, pos);
        return RadiationConfig.enablePollution && pollution != null ? pollution.pollution[type.ordinal()] : 0;
    }

    public static void setPollution(Level level, ChunkPos pos, Pollution pollution){
        LevelChunk chunk = getLoadedChunk(level, pos);
        if (RadiationConfig.enablePollution && chunk != null){
            AdditionalDataManager.setChunkData(chunk, DataEntry.POLLUTION, pollution);
        }
    }

    public static void setPollution(Level level, BlockPos pos, Pollution pollution){
        setPollution(level, new ChunkPos(pos), pollution);
//        if (RadiationConfig.enablePollution && level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4) && (level.getChunk(pos) instanceof LevelChunk chunk)){
//            AdditionalDataManager.setChunkData(chunk, DataEntry.POLLUTION, pollution);
//        }
    }

    public static void setPollution(Level level, BlockPos pos, Type type, float value){
        Pollution pollution = getPollution(level, pos);
        if (RadiationConfig.enablePollution && pollution != null){
            pollution.pollution[type.ordinal()] = Mth.clamp(value, 0, MAX_VALUE);
            setPollution(level, pos, pollution);
        }
    }

    public static void increPollution(Level level, BlockPos pos, Type type, float value){
        Pollution pollution = getPollution(level, pos);
        if (RadiationConfig.enablePollution && pollution != null){
            pollution.pollution[type.ordinal()] = (float) Mth.clamp(pollution.pollution[type.ordinal()] + value * MobConfig.pollutionMult, 0, MAX_VALUE);
            setPollution(level, pos, pollution);
        }
    }

    public static void decrePollution(Level level, BlockPos pos, Type type, float value){
        increPollution(level, pos, type, -value);
    }

    public static void tick(ServerLevel level){
        boolean flag = level.random.nextInt(UPDATE_FREQUENCY) == 0;
        final Map<ChunkPos, Pollution> data = new HashMap<>();
        level.getChunkSource().chunkMap.getChunks().forEach(chunkHolder -> {
            LevelChunk fullChunk = chunkHolder.getFullChunk();
            if (fullChunk != null){
                AdditionalDataManager.getChunkData(fullChunk).ifPresent(chunkData -> {
                    ChunkPos pos = fullChunk.getPos();
                    Pollution pollution1 = getPollution(level, pos);
                    // collect
                    if (flag){
                        Pollution pollution2 = data.get(pos);
                        pollution2 = pollution2 == null ? new Pollution() : pollution2;

                        float S = pollution1.pollution[Type.SOOT.ordinal()];
                        float P = pollution1.pollution[Type.POISON.ordinal()];
                        float H = pollution1.pollution[Type.HEAVYMETAL.ordinal()];

                        pollution2.pollution[Type.SOOT.ordinal()] = S * (S > 10 ? 0.8f : 0.99f);
                        pollution2.pollution[Type.HEAVYMETAL.ordinal()] = H * 0.9995f;
                        pollution2.pollution[Type.POISON.ordinal()] = P * (P > 10 ? 0.9f : 0.995f);
                        data.put(pos, pollution2);

                        Pollution spreadPollution = new Pollution();
                        spreadPollution.pollution[Type.SOOT.ordinal()] = S > 10 ? S * 0.05f : 0;
                        spreadPollution.pollution[Type.POISON.ordinal()] = P > 10 ? P * 0.025f : 0;

                        for (int[] offset : EnumUtils.offsets) {
                            ChunkPos newPos = new ChunkPos(pos.x + offset[0], pos.z + offset[1]);
                            Pollution pollution3 = data.get(newPos);
                            pollution3 = pollution3 == null ? getPollution(level, newPos) : pollution3;
                            if (pollution3 != null) {
                                pollution3.pollution[Type.SOOT.ordinal()] += spreadPollution.pollution[Type.SOOT.ordinal()];
                                pollution3.pollution[Type.HEAVYMETAL.ordinal()] += spreadPollution.pollution[Type.HEAVYMETAL.ordinal()];
                                pollution3.pollution[Type.POISON.ordinal()] += spreadPollution.pollution[Type.POISON.ordinal()];
                                data.put(newPos, pollution3);
                            }
                        }
                    }
                    // execute
                    float P = pollution1.pollution[Type.POISON.ordinal()];
                    if (P > DESTRUCTION_THRESHOLD){
                        for (int i = 0; i < DESTRUCTION_COUNT; i++) {
                            int x = pos.getMiddleBlockX() + level.random.nextInt(64);
                            int z = pos.getMiddleBlockZ() + level.random.nextInt(64);

                            if (level.hasChunk(x >> 4, z >> 4)) {
                                int height = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - level.random.nextInt(3) + 1;
                                BlockPos blockPos = new BlockPos(x, height, z);
                                BlockState state = level.getBlockState(blockPos);
                                if (state.is(Blocks.GRASS) || state.is(Blocks.GRASS_BLOCK)){
                                    level.setBlock(blockPos, Blocks.DIRT.defaultBlockState(), 3);
                                } else if (state.is(Blocks.TALL_GRASS) || state.is(BlockTags.LEAVES)) {
                                    level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                });
            }
        });
        if (flag) {
            data.forEach((pos, pollution) -> setPollution(level, pos.getWorldPosition(), pollution));
            data.clear();
        }
    }

    public static final UUID maxHealth = UUID.fromString("25462f6c-2cb2-4ca8-9b47-3a011cc61207");
    public static final UUID attackDamage = UUID.fromString("8f442d7c-d03f-49f6-a040-249ae742eed9");

    public static void enforceMob(MobSpawnEvent.FinalizeSpawn event){
        if(!RadiationConfig.enablePollution) return;
        Mob mob = event.getEntity();
        BlockPos onPos = mob.getOnPos();
        Pollution pollution = getPollution(mob.level(), onPos);
        if (pollution == null) {
            return;
        }
        if (!(mob instanceof EntityGlyphid)){
            float S = pollution.pollution[Type.SOOT.ordinal()];
            if (S > RadiationConfig.buffMobThreshold) {
                if (mob.getAttribute(Attributes.MAX_HEALTH) != null && mob.getAttribute(Attributes.MAX_HEALTH).getModifier(maxHealth) == null) {
                    mob.getAttribute(Attributes.MAX_HEALTH).addTransientModifier(new AttributeModifier(maxHealth, "Soot Anger Health Increase", 1D, AttributeModifier.Operation.ADDITION));
                }
                if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null && mob.getAttribute(Attributes.ATTACK_DAMAGE).getModifier(attackDamage) == null) {
                    mob.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(new AttributeModifier(attackDamage, "Soot Anger Damage Increase", 1.5D, AttributeModifier.Operation.ADDITION));
                }
                mob.heal(mob.getMaxHealth());
            }
        }
    }

    private static LevelChunk getLoadedChunk(Level level, ChunkPos pos) {
        if (!level.hasChunk(pos.x, pos.z)) {
            return null;
        }
        if (level instanceof ServerLevel server) {
            return server.getChunkSource().getChunkNow(pos.x, pos.z);
        }
        return level.getChunk(pos.x, pos.z);
    }

//    @SubscribeEvent
//    public void rampantTargetSetter(PlayerSleepInBedEvent event){
//        if (MobConfig.rampantGlyphidGuidance) targetCoords = Vec3.createVectorHelper(event.x, event.y, event.z);
//    }

    // 污染导致哨兵异虫的出现
    public static void rampantScoutPopulator(SpawnPlacementRegisterEvent event){

        if(MobConfig.rampantNaturalScoutSpawn) {

            event.register(ModEntityType.GLYPHID_SCOUT.get(), (pEntityType, pServerLevel, pSpawnType, pPos, pRandom) -> {
                Pollution pollution = getPollution(pServerLevel.getLevel(), pPos);
                return pollution != null && pollution.pollution[Type.SOOT.ordinal()] > MobConfig.rampantScoutSpawnThresh;
            });

//            if (event.rand.nextInt(MobConfig.rampantScoutSpawnChance) == 0) {
//
//                float soot = PollutionHandler.getPollution(event.world, event.x, event.y, event.z, Pollution.Type.SOOT);
//
//                if (soot >= MobConfig.rampantScoutSpawnThresh) {
//                    EntityGlyphidScout scout = new EntityGlyphidScout(event.world);
//                    scout.setLocationAndAngles(event.x, event.y, event.z, event.world.rand.nextFloat() * 360.0F, 0.0F);
//                    if(scout.isValidLightLevel()) {
//                        //escort for the scout, which can also deal with obstacles
//                        EntityGlyphidDigger digger = new EntityGlyphidDigger(event.world);
//                        scout.setLocationAndAngles(event.x, event.y, event.z, event.world.rand.nextFloat() * 360.0F, 0.0F);
//                        digger.setLocationAndAngles(event.x, event.y, event.z, event.world.rand.nextFloat() * 360.0F, 0.0F);
//                        if(scout.getCanSpawnHere()) event.world.spawnEntityInWorld(scout);
//                        if(digger.getCanSpawnHere()) event.world.spawnEntityInWorld(digger);
//                    }
//                }
//            }
        }

    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray(HBMKey.POLLUTION, DataTypeHelper.floatArr2IntArr(pollution));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        pollution = DataTypeHelper.IntArr2FloatArr(nbt.getIntArray(HBMKey.POLLUTION));
    }

    public static void showPollution(Level level, Player player){
        if (player == null) return;
        Pollution pollution1 = getPollution(level, player.getOnPos());
        player.sendSystemMessage(Component.literal("Position: " + player.getOnPos().toShortString() +
                "\tSOOT: " + pollution1.pollution[Type.SOOT.ordinal()] + "\tPOISON: " + pollution1.pollution[Type.POISON.ordinal()] +
                "\tHEAVYMETAL: " + pollution1.pollution[Type.HEAVYMETAL.ordinal()] + "\tFALLOUT: " + pollution1.pollution[Type.FALLOUT.ordinal()]));
    }

    public static void clearPollution(Level level, Player player){
        if (player == null) return;
        setPollution(level, player.getOnPos(), new Pollution());
    }

    public static Fluid getPollutingFluid(Type type){
        return switch (type){
            case SOOT -> ModFluids.SMOKE.source().get();
            case HEAVYMETAL -> ModFluids.SMOKE_LEADED.source().get();
            case POISON, FALLOUT -> ModFluids.SMOKE_POISON.source().get();
            default -> null;
        };
    }

    public enum Type{
        NONE(0),
        SOOT(1 / 25f),
        POISON(1 / 50f),
        HEAVYMETAL(1 / 50f),
        FALLOUT(1 / 10f);
        // 基础的生成速率
        final float baseRate;
        Type(float baseRate){
            this.baseRate = baseRate;
        }
    }
}
