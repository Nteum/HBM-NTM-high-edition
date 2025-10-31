package com.hbm.entity.mob;

import net.minecraft.world.damagesource.DamageSource;

public enum GlyphidStatus {
    GRUND(20D,0.3D,2D,1F,0.1F),
    BOMBARDIER(20D,1D,2D,1F,0.1F),
    BRAWLER(35D,1D,10D,2F,0.15F),
    DIGGER(50D,1D,10D,3F,0.20F),
    BLASTER(35D,1D,10D,2F,0.15F),
    BEHEMOTH(125D,0.8D,25D,5F,0.35F),
    BRENDA(250D,1.2D,50D,10F,0.5F),
    NUCLEAR(100D,0.8D,50D,10F,0.5F),
    SCOUNT(20D,1.5D,5D,0.5F,0.5F)
    ;
    public double health;
    public double speed;
    public double damage;
    /** Base threshold is calculated using this number * the glyphid's armor */
    public float thresholdMultForArmor;
    public float resistanceMult;

    GlyphidStatus(double health, double speed, double damage, float thresholdMultArmor, float resistanceMult){
        this.health = health;
        this.speed = speed;
        this.damage = damage;
        this.thresholdMultForArmor = thresholdMultArmor;
        this.resistanceMult = resistanceMult;
    }

//    public boolean handleAttack(EntityGlyphid glyphid, DamageSource source, float amount) {
//        // Completely immune to acid from other glyphids
//        if((source == ModDamageSource.acid || ModDamageSource.s_acid.equals(source.getDamageType())) && source.getSourceOfDamage() instanceof EntityGlyphid) return false;
//        return glyphid.attackSuperclass(source, amount);
//    }
}
