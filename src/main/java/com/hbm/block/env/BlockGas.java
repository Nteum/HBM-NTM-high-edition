package com.hbm.block.env;

import com.hbm.HBMKey;
import com.hbm.particle.ParticleSystem;
import com.hbm.registries.ModItems;
import com.hbm.utils.math.BitUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockGas extends Block {
    float red;
    float green;
    float blue;
    // 使用一个标准的满额方块体积作为检测区域
    protected static final VoxelShape BOX = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public BlockGas(Properties properties, float r, float g, float b) {
        // 实例化时，属性建议基于可穿过、无阻挡的材料（如空气或结构空位）进行微调
        super(properties
                .noCollission()           // 再次确保物理上绝对不产生碰撞阻挡
                .noLootTable()            // 气体被生存模式空手左键打破时，不掉落任何物品
                .replaceable()            // 允许其他方块直接右键覆盖放置在它的位置上（像草和空气一样）
                .pushReaction(PushReaction.DESTROY) // 活塞推过来时直接将其销毁，而不是把气块推走
        );
        this.red = r;
        this.green = g;
        this.blue = b;
    }

    /**
     * 1. 🔥 核心：允许生物直接穿过
     * 返回空形状，意味着这个方块没有任何物理撞击体积，子弹、玩家、生物都会直接穿透它。
     */
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    /**
     * 2. 🔥 核心：保留逻辑检测体积
     * 虽然没有物理碰撞，但我们需要保留它的点击/检测体积，这样下面的 entityInside() 才能被正确触发。
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BOX;
    }

    /**
     * 3. 🔥 核心：彻底隐形
     * 告诉游戏引擎不要渲染这个方块的任何方块网格（防止出现紫黑格或石头质感）。
     */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    /**
     * 4. 气体不阻挡视线，也不产生阴影
     */
    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource rand) {
        super.tick(pState, pLevel, pPos, rand);
        Direction dir = Direction.from3DDataValue(rand.nextInt(6));
        if (rand.nextInt(2) == 0 && pLevel.getBlockState(pPos.relative(dir)).isAir()){
            pLevel.setBlock(pPos.relative(dir), this.defaultBlockState(), 3);
        }
    }

    /**
     * 6. ✨ 视觉效果：在客户端源源不断地产生毒气粒子
     */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        // 如果玩家有监视眼镜就能看到雾气
        LocalPlayer player = Minecraft.getInstance().player;
        if (player.getInventory().getArmor(0).is(ModItems.ASH_GLASS.get())){
            if(rand.nextInt(9)==1) {
                Vec3 center = pos.getCenter();
                CompoundTag tag = new CompoundTag();
                tag.putIntArray(HBMKey.COLOR, new int[]{Float.floatToIntBits(red), Float.floatToIntBits(green),Float.floatToIntBits(blue)});
                ParticleSystem.vanillaCustom(ParticleTypes.CLOUD, center.x, center.y, center.z, 0, 0, 0, tag);
            }
        }
    }
}