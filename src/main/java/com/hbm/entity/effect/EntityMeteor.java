package com.hbm.entity.effect;

import com.hbm.block.HBMBlockComponent;
import com.hbm.config.ConfigWorld;
import com.hbm.entity.ModEntityType;
import com.hbm.explosion.ExplosionUtils;
import com.hbm.item.HBMComponent;
import com.hbm.particle.ParticleSystem;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModSounds;
import com.hbm.world.feature.Meteorite;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

public class EntityMeteor extends Entity {
    public boolean safe = false;
    public EntityMeteor(Level pLevel) {
        this(ModEntityType.ENTITY_METEOR.get(), pLevel);
    }

    public EntityMeteor(EntityType<EntityMeteor> entityType, Level level) {
        super(entityType, level);
        this.fireImmune();
    }

    @Override
    public void tick() {
        if (!level().isClientSide && !ConfigWorld.enableMeteorStrikes.get()){
            this.discard();
            return;
        }
        super.tick();

        Vec3 movement = this.getDeltaMovement();
        if (!level().isClientSide){
            if (movement.y() >= -1) {
                movement = movement.subtract(0, 0.01, 0);
                this.setDeltaMovement(movement);
            }
            this.move(MoverType.SELF, movement);

            if (this.position().y() < level().getMaxBuildHeight()){

                if (this.onGround()){
                    level().explode(this, this.getX(), getY(), getZ(), 5 + random.nextFloat(), Level.ExplosionInteraction.BLOCK);
                    double x = this.getX(), y = this.getY(), z = this.getZ();
                    if (ConfigWorld.enableMeteorTails.get()){
                        ExplosionUtils.spawnRubble(level(), x, y, z, 15);
                        ExplosionUtils.spawnParticles(level(), x, y + 5, z, 75);
                        ExplosionUtils.spawnParticles(level(), x + 5, y, z, 75);
                        ExplosionUtils.spawnParticles(level(), x - 5, y, z, 75);
                        ExplosionUtils.spawnParticles(level(), x, y, z + 5, 75);
                        ExplosionUtils.spawnParticles(level(), x, y, z - 5, 75);
                    }

                    // Bury the meteor into the ground
                    int spawnPosX = (int) (Math.round(x - 0.5D) + (safe ? 0 : (this.getDeltaMovement().x() * 4)));
                    int spawnPosY = (int) Math.round(y - (safe ? 0 : 4));
                    int spawnPosZ = (int) (Math.round(z - 0.5D) + (safe ? 0 : (this.getDeltaMovement().z() * 4)));

                    clearMeteorPath(level(), spawnPosX, spawnPosY, spawnPosZ);
                    Meteorite.spawnMeteor((ServerLevel) level(), this.getOnPos(), false, false, false);
                    level().playSound(null, this.getOnPos(), ModSounds.ENTITY_OLD_EXPLOSION.get(), SoundSource.BLOCKS, 10000, 0.5f + random.nextFloat() * 0.1f);

                    this.discard();
                }
            }
        }
        if (level().isClientSide){
            tickCount ++;
            if (ConfigWorld.enableMeteorTails.get()){
                CompoundTag data = new CompoundTag();
                data.putString("type", "exhaust");
                data.putString("mode", "meteor");
                data.putInt("count", 10);
                data.putDouble("width", 1);
                data.putDouble("x", getX() - movement.x);
                data.putDouble("y", getY() - movement.y);
                data.putDouble("z", getZ() - movement.z);

                ParticleSystem.handleParticleCombo(data);
            }
        }
    }

    public void clearMeteorPath(Level world, int x, int y, int z) {
        if (safe) return;

        int radius = 5;
        BlockPos pos;
        for (int bx = -radius; bx <= radius; bx++) {
            for (int by = -radius; by <= radius; by++) {
                for (int bz = -radius; bz <= radius; bz++) {
                    pos = new BlockPos(x + bx, y + by, z + bz);
                    BlockState blockState = level().getBlockState(pos);
                    float resistance = blockState.getExplosionResistance(world, pos, null);
                    if (blockState.canBeReplaced() || resistance > 0 && resistance < 0.3f){
                        if (blockState.is(BlockTags.LEAVES)) world.setBlock(pos, ModBlocks.WASTE_LEAVES.get().defaultBlockState(), 3);
                        else world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }else {
                        if (resistance < 0 || resistance >= 5) return;
                        if (random.nextInt(6) == 0){
                            // Turn blocks into damaged variants
                            if(blockState.is(Blocks.DIRT)) {
                                world.setBlock(pos, ModBlocks.WASTE_LEAVES.get().defaultBlockState(), 3);
                            } else if(blockState.is(Blocks.SAND)) {
                                if(random.nextInt(2) == 1) {
                                    level().setBlock(pos, Blocks.SANDSTONE.defaultBlockState(), 3);
                                } else {
                                    level().setBlock(pos, Blocks.GLASS.defaultBlockState(), 3);
                                }
                            } else if(blockState.is(Blocks.STONE)) {
                                level().setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("safe", safe);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        safe = tag.getBoolean("safe");
    }
}
