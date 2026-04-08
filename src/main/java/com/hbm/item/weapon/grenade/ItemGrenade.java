package com.hbm.item.weapon.grenade;

//import com.hbm.entity.logic.EntityGrenadeBouncyBase;
//import com.hbm.entity.logic.GrenadeGeneticEntity;
import com.hbm.entity.weapon.grenade.*;
import com.hbm.registries.ModSounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

/** 手榴弹 */
public class ItemGrenade extends Item {
    Type type;

    public ItemGrenade(Properties pProperties, Type type) {
        super(pProperties.stacksTo(16));
        this.type = type;
    }

    /** 右键手榴弹的效果
     * （参考的ItemSnowball）
     * */
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        ThrownGrenade grenade = getGrenadeEntity(type,pLevel,pPlayer);
        if (grenade == null) {
            return InteractionResultHolder.fail(itemStack);
        }

        SoundEvent throwSound = getThrowSound(type);
        float throwVolume = throwSound == SoundEvents.SNOWBALL_THROW ? 0.5F : 0.85F;
        pLevel.playSound((Player)null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                throwSound, SoundSource.PLAYERS, throwVolume, 0.92F + pLevel.getRandom().nextFloat() * 0.2F);
        if (!pLevel.isClientSide) {
            grenade.setItem(itemStack);
            grenade.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
            pLevel.addFreshEntity(grenade);
        }

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        if (!pPlayer.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return super.getRarity(pStack);
    }

    public enum Type{
        GENERIC, STRONG, FIRE, FRAG, BLACK_HOLE, TAU,
        ASCHRAB, BREACH, BURST, CLOUD, CLUSTER, ELECTRIC, FLARE, GAS, GASCAN,
        IF_BOUNCY, IF_BRIMSTONE, IF_CONCUSSION, IF_GENERIC, IF_HE, IF_HOPWIRE, IF_IMPACT,
        IF_INCENDIARY, IF_MYSTERY, IF_NULL, IF_SPARK, IF_STICKY, IF_TOXIC,
        KIT, KYIV, LEMON, MIRV, MK2, NUCLEAR, NUKE, PINK_CLOUD, PLASMA, POISON, PULSE,
        SCHRABIDIUM, SHRAPNEL, SMART, ZOMG;

        public int getLegacyFuseTicks() {
            return switch (this) {
                case FLARE -> 140;
                case NUCLEAR, NUKE, SCHRABIDIUM, ASCHRAB, ZOMG -> 120;
                case IF_IMPACT -> 160;
                default -> 80;
            };
        }

        public double getLegacyBounceMod() {
            return switch (this) {
                case IF_BOUNCY -> 0.9D;
                case IF_STICKY -> 0.05D;
                case IF_HOPWIRE -> 0.15D;
                default -> 0.25D;
            };
        }

        public boolean isImpactFuse() {
            return this == IF_IMPACT;
        }
    }

    protected static ThrownGrenade getGrenadeEntity(Type type, Level pLevel, Player pPlayer){
        return switch (type){
            case GENERIC -> new EntityGrenadeGenetic(pPlayer,pLevel);
            case STRONG -> new EntityGrenadeStrong(pPlayer,pLevel);
            case FIRE -> new EntityGrenadeFire(pPlayer, pLevel);
            case FRAG -> new EntityGrenadeFrag(pPlayer,pLevel);
            case BLACK_HOLE -> new EntityGrenadeBlackHole(pPlayer,pLevel);
            case TAU, ASCHRAB, BREACH, BURST, CLOUD, CLUSTER, ELECTRIC, FLARE, GAS, GASCAN,
                    IF_BOUNCY, IF_BRIMSTONE, IF_CONCUSSION, IF_GENERIC, IF_HE, IF_HOPWIRE, IF_IMPACT,
                    IF_INCENDIARY, IF_MYSTERY, IF_NULL, IF_SPARK, IF_STICKY, IF_TOXIC,
                    KIT, KYIV, LEMON, MIRV, MK2, NUCLEAR, NUKE, PINK_CLOUD, PLASMA, POISON, PULSE,
                    SCHRABIDIUM, SHRAPNEL, SMART, ZOMG -> new EntityGrenadeLegacy(pPlayer, pLevel, type);
        };
    }

    private static SoundEvent getThrowSound(Type type) {
        return switch (type) {
            case BLACK_HOLE -> ModSounds.WEAPON_SING_FLYBY.get();
            case GAS, GASCAN, IF_TOXIC, POISON, PINK_CLOUD, CLOUD -> ModSounds.ITEM_SPRAY.get();
            case ELECTRIC, IF_SPARK, PULSE -> ModSounds.WEAPON_TESLA_SHOOT.get();
            case FIRE, FLARE, IF_INCENDIARY, IF_BRIMSTONE, PLASMA, LEMON -> ModSounds.WEAPON_FLAMETHROWER_IGNITE.get();
            case NUCLEAR, NUKE, SCHRABIDIUM, ASCHRAB, ZOMG -> ModSounds.WEAPON_GLAUNCHER.get();
            case TAU -> ModSounds.WEAPON_TAU_SHOOT.get();
            default -> SoundEvents.SNOWBALL_THROW;
        };
    }
}
