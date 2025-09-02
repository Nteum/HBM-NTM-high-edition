package com.hbm.item.weapon;

import com.hbm.entity.weapon.missile.EntityMissile;
import com.hbm.main.ClientSetup;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class ItemMissile extends Item{
    public final MissileTier tier;
    public ItemMissile(Properties pProperties, MissileTier tier) {
        super(pProperties);
        this.tier = tier;
    }

    public EntityMissile createEntity(Level level, BlockPos pos, BlockPos target){
//        return null;
        Vec3 center = pos.getCenter();
//        return new EntityMissileGenetic(level,center.x,center.y,center.z,target);
        return null;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ClientSetup.specialItemRender;
            }
        });
    }

    public enum MissileTier {
        TIER0("Tier 0"),
        TIER1("Tier 1"),
        TIER2("Tier 2"),
        TIER3("Tier 3"),
        TIER4("Tier 4");

        public String display;

        private MissileTier(String display) {
            this.display = display;
        }
    }
}
