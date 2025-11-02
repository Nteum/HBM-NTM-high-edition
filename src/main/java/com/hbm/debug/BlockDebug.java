package com.hbm.debug;

import com.hbm.entity.effect.EntityMeteor;
import com.hbm.explosion.temp.ExplosionOneOff;
import com.hbm.registries.ModItems;
import com.hbm.particle.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BlockDebug extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("debug_active");
    public BlockDebug(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(ACTIVE);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack itemInHand = pPlayer.getItemInHand(pHand);
        if (!pLevel.isClientSide()){
            if (itemInHand.is(ModItems.DEBUG_WAND.get())) {
                dropParticle(ModParticleTypes.DEAD_LEAF.get(), pLevel, pPos, pPlayer);
            } else if (itemInHand.is(ModItems.METEOR_REMOTE.get())) {
                testMeteorite(pState, pLevel, pPos, pPlayer);
            } else if (itemInHand.is(Items.FLINT_AND_STEEL)){
                return explode(pState, pLevel, pPos, pPlayer);
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
//        if (pLevel.getBlockState(pPos.below()).isAir()){
//            addParticle(ModParticleTypes.DEAD_LEAF.get(), pLevel, pPos, Minecraft.getInstance().player);
//        }
//        if (pState.getValue(ACTIVE)){
//            if (pLevel.getRandom().nextFloat() > 0){
//                Vec3 center = pPos.getCenter().add(0, 0.5, 0);
//                ParticleSystem.addRocketFlame(center.x, center.y, center.z, 0, 0.1, 0, null, 60 + pRandom.nextInt(20));
//            }
//        }
    }

    public void dropParticle(ParticleOptions type, Level pLevel, BlockPos pPos, Player pPlayer){
        if (pLevel instanceof ServerLevel serverLevel){
            serverLevel.sendParticles(type,pPos.getX() + pLevel.random.nextFloat(), pPos.getY(), pPos.getZ() + pLevel.random.nextFloat(), 1, 0, 0, 0, 0);
        }else {
            pLevel.addParticle(type, pPos.getX() + pLevel.random.nextFloat(), pPos.getY(), pPos.getZ() + pLevel.random.nextFloat(), 0, 0, 0);
        }
    }
    public void addParticle(ParticleOptions type, Level pLevel, BlockPos pPos, Player pPlayer){
        Vec3 center = pPos.above().getCenter();
        pPlayer.sendSystemMessage(Component.translatable("msg.hbm.particle", type.getType().toString()));
        if (pLevel instanceof ServerLevel serverLevel){
            serverLevel.sendParticles(type, center.x, center.y, center.z, 1, 0, 0, 0, 0);
        }else {
            pLevel.addParticle(type, center.x, center.y, center.z, 0, 0, 0);
        }
    }
    // 测试陨石实体
    public void testMeteorite(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer){
        if (pState.getValue(ACTIVE)) return;
        EntityMeteor meteor = new EntityMeteor(pLevel);
        meteor.setPos(pPos.getCenter().x, pPos.getCenter().y + 2, pPos.getCenter().z);
        pLevel.addFreshEntity(meteor);
    }

    public InteractionResult explode(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer){
        if (!pLevel.isClientSide) {
            pLevel.removeBlock(pPos, false);
            ExplosionOneOff explode = ExplosionOneOff.explode(pLevel, pPlayer, null, null, pPos.getCenter().x, pPos.getCenter().y, pPos.getCenter().z, 32, false, Level.ExplosionInteraction.TNT, true);
//            explode.setResolution(32);
            explode.setBlockAllocator(new ExplosionOneOff.BlockAllocateBulkie(60));
            explode.setEntityProcessor(new ExplosionOneOff.EntityProcessorVanilla());
            explode.setBlockProcessor(new ExplosionOneOff.BlockProcessorStandard().setMutator(new ExplosionOneOff.BlockMutatorBulkie(Blocks.GOLD_ORE.defaultBlockState())).setNoDrop());
            explode.explode();
        }
        return InteractionResult.CONSUME;
    }
}
