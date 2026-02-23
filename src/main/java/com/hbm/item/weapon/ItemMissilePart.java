package com.hbm.item.weapon;

import com.hbm.main.ClientEventHandler;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class ItemMissilePart extends Item{
    public final MissileTier tier;
    public ItemMissilePart(Properties pProperties, MissileTier tier) {
        super(pProperties);
        this.tier = tier;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ClientEventHandler.getLazyItemRender();
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
