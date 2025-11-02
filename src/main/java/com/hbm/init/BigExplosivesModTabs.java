package net.mcreator.nuclearcraft.init;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/init/BigExplosivesModTabs.class */
public class BigExplosivesModTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.f_279569_, BigExplosivesMod.MODID);
    public static final RegistryObject<CreativeModeTab> EXPLOSIVES = REGISTRY.register("explosives", () -> {
        return CreativeModeTab.builder().m_257941_(Component.m_237115_("item_group.big_explosives.explosives")).m_257737_(() -> {
            return new ItemStack((ItemLike) BigExplosivesModItems.TWO_HUNDRED_AND_FIFTY_KG_BOMB.get());
        }).m_257501_((parameters, tabData) -> {
            tabData.m_246326_((ItemLike) BigExplosivesModItems.TWO_FIDDY_BUTTON.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.TWO_HUNDRED_AND_FIFTY_KG_BOMB.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.EMPTY_AIRSTRIKE_DESIGNATOR.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.FIVE_HUNDRED_KILOGRAM_BOMB.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.TEN_KG_BOMB.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.NAPALM_BUTTON.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.NAPALM_BOMBB.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.TEN_KG_NAPALM_BOMB.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.BUNKER_BUSTER_BOMB.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.BUNKER_BUSTER_DESIGNATOR.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.ATOM_BOMB.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.TEST.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.FIVE_HUNDRED_KG_BOMB_DESIGNATOR.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.TEN_KG_BOMB_BARRAGE_DESIGNATOR.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.NAPALM_BARAGE_DESIGNATORR.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.IRON_PLATE.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.ATOMIC_CORE.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.FIRE_CORE.get());
            tabData.m_246326_(((Block) BigExplosivesModBlocks.BASIC_WORK_BENCH_GECKOLIB.get()).m_5456_());
            tabData.m_246326_(((Block) BigExplosivesModBlocks.GECKO_ADVANCED_WORKBENCH.get()).m_5456_());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.EXPLOSIVE_CORE.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.GOLDEN_BEETROOT.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.GOLDEN_BEETROOT_SOUP.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.CAKE_BOMB_DESIGNATOR.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.FOURTH_OF_JULY_DESIGNATOR.get());
            tabData.m_246326_((ItemLike) BigExplosivesModItems.FOURTH_OF_JULY_BOMB.get());
        }).m_257652_();
    });
}
