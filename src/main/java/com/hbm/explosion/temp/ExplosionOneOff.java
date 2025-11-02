package com.hbm.explosion.temp;

import com.google.common.collect.Sets;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toclient.S2CExplosionPacket;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModSounds;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 一次性爆炸，主要模仿原版Explosion，它能在单tick内完成射线检测和物品摧毁，所以不需要tick
 * 用作小规模爆炸
 * - 由于原版一些爆炸抗性之类的东西获得需要Explosion实例，因此这里直接继承Explosion
 * - 爆炸半径超过10格就会出现明显的延迟，因此很不建议把爆炸半径设的太大，如果你需要更大的爆炸，请使用ExplosionTickable
 * */
public class ExplosionOneOff extends Explosion {
    int resolution = 16;
    BlockAllocate blockAllocator;
    EntityProcessor entityProcessor;
    BlockProcessor blockProcessor;
    ExplosionEffect[] explosionEffects;
    public ExplosionOneOff(Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, List<BlockPos> pPositions) {
        this(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY, pPositions);
    }

    public ExplosionOneOff(Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, BlockInteraction pBlockInteraction, List<BlockPos> pPositions) {
        this(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
        this.toBlow.addAll(pPositions);
    }

    public ExplosionOneOff(Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, BlockInteraction pBlockInteraction) {
        this(pLevel, pSource, (DamageSource)null, (ExplosionDamageCalculator)null, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
    }

    public ExplosionOneOff(Level pLevel, @Nullable Entity pSource, @Nullable DamageSource pDamageSource, @Nullable ExplosionDamageCalculator pDamageCalculator, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, BlockInteraction pBlockInteraction) {
        super(pLevel, pSource, pDamageSource, pDamageCalculator, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
        if (pRadius < 8) setResolution(16);
        else if (pRadius < 16) setResolution(32);
        else if (pRadius < 32) setResolution(48);
        else if (pRadius < 64) setResolution(64);
    }

    private ExplosionDamageCalculator makeDamageCalculator(@Nullable Entity pEntity) {
        return pEntity == null ? EXPLOSION_DAMAGE_CALCULATOR : new EntityBasedExplosionDamageCalculator(pEntity);
    }

    public ExplosionOneOff setResolution(int resolution){
        this.resolution = resolution < 0 ? 0 : Math.min(resolution, 64);
        return this;
    }

    public ExplosionOneOff setBlockAllocator(BlockAllocate blockAllocator){
        this.blockAllocator = blockAllocator;
        return this;
    }
    public ExplosionOneOff setEntityProcessor(EntityProcessor entityProcessor){
        this.entityProcessor = entityProcessor;
        return this;
    }
    public ExplosionOneOff setBlockProcessor (BlockProcessor blockProcessor){
        this.blockProcessor = blockProcessor;
        return this;
    }

    @Override
    public void explode() {
        this.level.gameEvent(this.source, GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
        // 1. 搜索需要破坏的方块实体
        Set<BlockPos> set = Sets.newHashSet();
        // 射线检测中预估的射线长度
        float rayLenFloating = 0.3f;
        float stepSize = 0.5f;

        for(int j = 0; j < resolution; ++j) {
            for(int k = 0; k < resolution; ++k) {
                for(int l = 0; l < resolution; ++l) {
                    if (j == 0 || j == resolution - 1 || k == 0 || k == resolution - 1 || l == 0 || l == resolution - 1) {
                        double xDir = (float)j / (resolution - 1) * 2.0F - 1.0F;
                        double yDir = (float)k / (resolution - 1) * 2.0F - 1.0F;
                        double zDir = (float)l / (resolution - 1) * 2.0F - 1.0F;
                        double d3 = Math.sqrt(xDir * xDir + yDir * yDir + zDir * zDir);
                        xDir /= d3;
                        yDir /= d3;
                        zDir /= d3;
                        double targetX = this.x;
                        double targetY = this.y;
                        double targetZ = this.z;

                        this.blockAllocator.blockAllocate(this, set, rayLenFloating, stepSize, new Vec3(targetX, targetY, targetZ), new Vec3(xDir, yDir, zDir));
                    }
                }
            }
        }

        // 2. 记录需要伤害的生物
        this.toBlow.addAll(set);
        float f2 = this.radius * 2.0F;
        int k1 = Mth.floor(this.x - (double)f2 - 1.0D);
        int l1 = Mth.floor(this.x + (double)f2 + 1.0D);
        int i2 = Mth.floor(this.y - (double)f2 - 1.0D);
        int i1 = Mth.floor(this.y + (double)f2 + 1.0D);
        int j2 = Mth.floor(this.z - (double)f2 - 1.0D);
        int j1 = Mth.floor(this.z + (double)f2 + 1.0D);
        List<Entity> list = this.level.getEntities(this.source, new AABB(k1, i2, j2, l1, i1, j1));
        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.level, this, list, f2);
        Vec3 vec3 = new Vec3(this.x, this.y, this.z);

        for (Entity entity : list) {
            this.entityProcessor.processEntity(this, entity, vec3, f2);
        }

        boolean flag = this.interactsWithBlocks();

        if (flag) {
            this.blockProcessor.process(this);
        }

        if (this.fire) {
            for(BlockPos blockpos2 : this.toBlow) {
                if (this.random.nextInt(3) == 0 && this.level.getBlockState(blockpos2).isAir() && this.level.getBlockState(blockpos2.below()).isSolidRender(this.level, blockpos2.below())) {
                    this.level.setBlockAndUpdate(blockpos2, BaseFireBlock.getState(this.level, blockpos2));
                }
            }
        }

        // 这段是原版Level中explode的代码，将爆炸的信息传到相应的地方
        if (!this.interactsWithBlocks()) {
            this.clearToBlow();
        }
//        for(ServerPlayer serverplayer : ((ServerLevel)level).players()) {
//            if (serverplayer.distanceToSqr(x, y, z) < 4096.0D) {
//                ModMessages.sendToPlayer(new S2CExplosionPacket(x, y, z, radius, this.getToBlow(), this.getHitPlayers().get(serverplayer)), serverplayer);
//            }
//        }
        for (ExplosionEffect explosionEffect : explosionEffects) {
            explosionEffect.doEffect(this, level, getPosition(), this.radius);
        }
    }
    // 客户端需要处理的效果
    public void clientEffect(){
        if (this.level.isClientSide) {
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
        }

        boolean flag = this.interactsWithBlocks();
        if (!(this.radius < 2.0F) && flag) {
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
        } else {
            this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
        }
    }

    private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> pDropPositionArray, ItemStack pStack, BlockPos pPos) {
        int i = pDropPositionArray.size();

        for(int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = pDropPositionArray.get(j);
            ItemStack itemstack = pair.getFirst();
            if (ItemEntity.areMergable(itemstack, pStack)) {
                ItemStack itemstack1 = ItemEntity.merge(itemstack, pStack, 16);
                pDropPositionArray.set(j, Pair.of(itemstack1, pair.getSecond()));
                if (pStack.isEmpty()) {
                    return;
                }
            }
        }

        pDropPositionArray.add(Pair.of(pStack, pPos));
    }
    // 触发爆炸的函数，直接copy Level里的代码了。
    public static ExplosionOneOff explode(Level level, @javax.annotation.Nullable Entity pSource, @javax.annotation.Nullable DamageSource pDamageSource, @javax.annotation.Nullable ExplosionDamageCalculator pDamageCalculator, double pX, double pY, double pZ, float pRadius, boolean pFire, Level.ExplosionInteraction pExplosionInteraction, boolean pSpawnParticles){
        if (level.isClientSide) return null;
        Explosion.BlockInteraction explosion$blockinteraction = switch (pExplosionInteraction) {
            case NONE -> BlockInteraction.KEEP;
            case BLOCK -> getDestroyType(level, GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
            case MOB ->
                    net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level, pSource) ? getDestroyType(level, GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) : BlockInteraction.KEEP;
            case TNT -> getDestroyType(level, GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
            default -> throw new IncompatibleClassChangeError();
        };
        ExplosionOneOff explosion = new ExplosionOneOff(level, pSource, pDamageSource, pDamageCalculator, pX, pY, pZ, pRadius, pFire, explosion$blockinteraction);
        if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(level, explosion)) return explosion;

        return explosion;
    }

    private static Explosion.BlockInteraction getDestroyType(Level level, GameRules.Key<GameRules.BooleanValue> pGameRule) {
        return level.getGameRules().getBoolean(pGameRule) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
    }

    @FunctionalInterface
    interface BlockAllocate{
        void blockAllocate(ExplosionOneOff explosion, Set<BlockPos> set, float rayLen, float stepSize, Vec3 target, Vec3 rayDir);
    }
    // 原版方式
    public static class BlockAllocateVanilla implements BlockAllocate{
        @Override
        public void blockAllocate(ExplosionOneOff explosion, Set<BlockPos> set, float rayLen, float stepSize, Vec3 target, Vec3 rayDir) {
            for(float estimateRayLen = explosion.radius * ((1-rayLen) + explosion.level.random.nextFloat() * (2 * rayLen)); estimateRayLen > 0.0F; estimateRayLen -= 0.22500001F) {
                BlockPos blockpos = BlockPos.containing(target);
                if (!explosion.level.isInWorldBounds(blockpos)) {
                    break;
                }

                BlockState blockstate = explosion.level.getBlockState(blockpos);
                FluidState fluidstate = explosion.level.getFluidState(blockpos);
                Optional<Float> optional = explosion.damageCalculator.getBlockExplosionResistance(explosion, explosion.level, blockpos, blockstate, fluidstate);
                if (optional.isPresent()) {
                    estimateRayLen -= (optional.get() + 0.3F) * stepSize;
                }

                if (estimateRayLen > 0.0F && explosion.damageCalculator.shouldBlockExplode(explosion, explosion.level, blockpos, blockstate, estimateRayLen)) {
                    set.add(blockpos);
                }

                target.add(rayDir.scale(stepSize));
            }
        }
    }
    // 水下炸弹，冲击波不会受到液体的阻碍，也不会消除液体
    public static class BlockAllocateWater implements BlockAllocate{
        @Override
        public void blockAllocate(ExplosionOneOff explosion, Set<BlockPos> set, float rayLen, float stepSize, Vec3 target, Vec3 rayDir) {
            for(float estimateRayLen = explosion.radius * ((1-rayLen) + explosion.level.random.nextFloat() * (2 * rayLen)); estimateRayLen > 0.0F; estimateRayLen -= 0.22500001F) {
                BlockPos blockpos = BlockPos.containing(target);
                if (!explosion.level.isInWorldBounds(blockpos)) {
                    break;
                }

                BlockState blockstate = explosion.level.getBlockState(blockpos);
                FluidState fluidstate = explosion.level.getFluidState(blockpos);
                Optional<Float> optional = explosion.damageCalculator.getBlockExplosionResistance(explosion, explosion.level, blockpos, blockstate, fluidstate);
                boolean solid = fluidstate.isEmpty() || blockstate.isSolid();
                if (optional.isPresent() && solid) {
                    estimateRayLen -= (optional.get() + 0.3F) * stepSize;
                }

                if (estimateRayLen > 0.0F && explosion.damageCalculator.shouldBlockExplode(explosion, explosion.level, blockpos, blockstate, estimateRayLen) && solid) {
                    set.add(blockpos);
                }

                target = target.add(rayDir.scale(stepSize));
            }
        }
    }
    // 大范围爆炸，似乎是bob根据自己的理解修改了爆炸循环的条件
    public static class BlockAllocateBulkie implements BlockAllocate{
        double maximum;
        Predicate<BlockState> immuneBlock = blockState -> false;
        public BlockAllocateBulkie(double maximum){
            this.maximum = maximum;
        }
        public BlockAllocateBulkie(double maximum, Predicate<BlockState> predicate){
            this.maximum = maximum;
            this.immuneBlock = predicate;
        }
        public static BlockAllocateBulkie glyphid(double maximum){
            return new BlockAllocateBulkie(maximum, blockState -> blockState.is(ModBlocks.GLYPHID_SPAWNER.get()));
        }
        @Override
        public void blockAllocate(ExplosionOneOff explosion, Set<BlockPos> set, float rayLen, float stepSize, Vec3 target, Vec3 rayDir) {
            for(double dist = 0; dist <= (double) explosion.radius;) {
                double deltaX = target.x - explosion.x;
                double deltaY = target.y - explosion.y;
                double deltaZ = target.z - explosion.z;
                dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                BlockPos blockpos = BlockPos.containing(target);
                if (!explosion.level.isInWorldBounds(blockpos)) {
                    break;
                }

                BlockState blockstate = explosion.level.getBlockState(blockpos);
                FluidState fluidstate = explosion.level.getFluidState(blockpos);
                Optional<Float> optional = explosion.damageCalculator.getBlockExplosionResistance(explosion, explosion.level, blockpos, blockstate, fluidstate);
                if (optional.isPresent()) {
                    if(this.maximum < optional.get() || immuneBlock.test(blockstate)) {
                        break;
                    }
                }

                if (explosion.source == null || explosion.damageCalculator.shouldBlockExplode(explosion, explosion.level, blockpos, blockstate, explosion.radius)) {
                    set.add(blockpos);
                }

                target = target.add(rayDir.scale(stepSize));
            }
        }
    }

    @FunctionalInterface
    interface EntityProcessor{
        void processEntity(ExplosionOneOff explosion, Entity entity, Vec3 vec, float diameter);
    }

    public static class EntityProcessorVanilla implements EntityProcessor{

        @Override
        public void processEntity(ExplosionOneOff explosion, Entity entity, Vec3 vec, float diameter) {
            if (!entity.ignoreExplosion()) {
                double distanceScaled = Math.sqrt(entity.distanceToSqr(vec)) / (double) diameter;
                if (distanceScaled <= 1.0D) {
                    double deltaX = entity.getX() - explosion.x;
                    double deltaY = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - explosion.y;
                    double deltaZ = entity.getZ() - explosion.z;
                    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                    if (distance != 0.0D) {
                        deltaX /= distance;
                        deltaY /= distance;
                        deltaZ /= distance;
                        double knockback = (1.0D - distanceScaled) * getSeenPercent(vec, entity);
                        entity.hurt(explosion.getDamageSource(), (float) ((int) ((knockback * knockback + knockback) / 2.0D * 7.0D * (double) diameter + 1.0D)));
                        double enchKnockback;
                        if (entity instanceof LivingEntity livingentity) {
                            enchKnockback = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingentity, knockback);
                        } else {
                            enchKnockback = knockback;
                        }

                        deltaX *= enchKnockback;
                        deltaY *= enchKnockback;
                        deltaZ *= enchKnockback;
                        Vec3 vec31 = new Vec3(deltaX, deltaY, deltaZ);
                        entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
                        if (entity instanceof Player player) {
                            if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                                explosion.hitPlayers.put(player, vec31);
                            }
                        }
                    }
                }
            }
        }
    }

    @FunctionalInterface
    interface BlockProcessor{
        void process(ExplosionOneOff explosion);
    }

    public static class BlockProcessorStandard implements BlockProcessor{
        boolean noDrops = false;
        protected IBlockMutator mutator;

        public BlockProcessorStandard setMutator(IBlockMutator blockMutator){
            this.mutator = blockMutator;
            return this;
        }
        @Override
        public void process(ExplosionOneOff explosion) {
            ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
            boolean isPlayer = explosion.getIndirectSourceEntity() instanceof Player;
            Util.shuffle(explosion.toBlow, explosion.level.random);

            // 处理方块效果
            for(BlockPos blockpos : explosion.toBlow) {
                BlockState blockstate = explosion.level.getBlockState(blockpos);
                if (!blockstate.isAir()) {
                    BlockPos blockpos1 = blockpos.immutable();
                    // 处理凋落物信息
                    if (blockstate.canDropFromExplosion(explosion.level, blockpos, explosion)) {
                        if (explosion.level instanceof ServerLevel serverlevel) {
                            List<ItemStack> drops = new ArrayList<>();
                            if (!noDrops){
                                BlockEntity blockentity = blockstate.hasBlockEntity() ? explosion.level.getBlockEntity(blockpos) : null;
                                LootParams.Builder lootparams$builder = (new LootParams.Builder(serverlevel))
                                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos))
                                        .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                                        .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity)
                                        .withOptionalParameter(LootContextParams.THIS_ENTITY, explosion.source);
                                if (explosion.blockInteraction == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
                                    lootparams$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, explosion.radius);
                                }
                                // 掉落经验
                                blockstate.spawnAfterBreak(serverlevel, blockpos, ItemStack.EMPTY, isPlayer);
                                drops = blockstate.getDrops(lootparams$builder);
                            }

                            drops.forEach((itemStack) -> addBlockDrops(objectarraylist, itemStack, blockpos1));
                        }
                    }
                    // 这个函数让方块消失的
                    blockstate.onBlockExploded(explosion.level, blockpos, explosion);
                    // 修改爆炸后方块结果
                    if(this.mutator != null) this.mutator.mutatePre(explosion, blockstate, blockpos);
                }
            }
            // 生成掉落物实体
            for(Pair<ItemStack, BlockPos> pair : objectarraylist) {
                Block.popResource(explosion.level, pair.getSecond(), pair.getFirst());
            }

            if(this.mutator != null) {
                for (BlockPos blockPos : explosion.toBlow) {
                    if (explosion.level.getBlockState(blockPos).is(Blocks.AIR)){
                        this.mutator.mutatePost(explosion, blockPos);
                    }
                }
            }
        }

        public BlockProcessorStandard setNoDrop(){
            this.noDrops = true;
            return this;
        }
    }

    interface IBlockMutator {
        void mutatePre(ExplosionOneOff explosion, BlockState state, BlockPos pos);
        void mutatePost(ExplosionOneOff explosion, BlockPos pos);
    }
    public static class BlockMutatorBulkie implements IBlockMutator{
        BlockState blockState = Blocks.AIR.defaultBlockState();
        public BlockMutatorBulkie(BlockState blockState){
            this.blockState = blockState;
        }
        @Override
        public void mutatePre(ExplosionOneOff explosion, BlockState state, BlockPos pos) {
            if (!state.canOcclude() || !state.getRenderShape().equals(RenderShape.MODEL) || state.isSignalSource()) return;
            if (pos.getCenter().distanceTo(new Vec3(explosion.x, explosion.y, explosion.z)) >= explosion.radius - 0.7) explosion.level.setBlock(pos, blockState, 3);
        }

        @Override
        public void mutatePost(ExplosionOneOff explosion, BlockPos pos) {
        }
    }
    /**
     * 爆炸特效
     * 这个特效还是从服务端发起，发包被客户端接收处理
     * */
    public interface ExplosionEffect {
        void doEffect(ExplosionOneOff explosion, Level level, Vec3 position, float size);
    }
    // 原版特效
    public static class ExplosionEffectVanilla implements ExplosionEffect{
        @Override
        public void doEffect(ExplosionOneOff explosion, Level level, Vec3 position, float size) {
            for(ServerPlayer serverplayer : ((ServerLevel)level).players()) {
                // 我猜是由于服务端无法预计客户端渲染距离，所以直接用4096这个最大的渲染距离计算了。
                if (serverplayer.distanceToSqr(position) < 4096.0D) {
                    ModMessages.sendToPlayer(new S2CExplosionPacket(position.x, position.y, position.z, size, explosion.getToBlow(), explosion.getHitPlayers().get(serverplayer)), serverplayer);
                }
            }
        }
    }

    public static class ExplosionEffectTiny implements ExplosionEffect{

        @Override
        public void doEffect(ExplosionOneOff explosion, Level level, Vec3 position, float size) {
            if(level.isClientSide) return;

            level.playSound(null, BlockPos.containing(position), ModSounds.WEAPON_EXPLOSION_SMALL_FAR.get(), SoundSource.RECORDS, 15.0f, 1.0f);

//            NBTTagCompound data = new NBTTagCompound();
//            data.setString("type", "vanillaExt");
//            data.setString("mode", "largeexplode");
//            data.setFloat("size", 1.5F);
//            data.setByte("count", (byte)1);
//            PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, x, y, z), new TargetPoint(world.provider.dimensionId, x, y, z, 100));
        }
    }
}
