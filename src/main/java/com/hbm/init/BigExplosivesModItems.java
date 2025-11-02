package net.mcreator.nuclearcraft.init;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.item.AtomBombItem;
import net.mcreator.nuclearcraft.item.FiveHundredKilogramBombItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public final class BigExplosivesModItems {

    public static final DeferredRegister<Item> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ITEMS, BigExplosivesMod.MODID);

    public static final RegistryObject<Item> ATOM_BOMB = REGISTRY.register(
            "atom_bomb", () -> new AtomBombItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> FIVE_HUNDRED_KILOGRAM_BOMB = REGISTRY.register(
            "five_hundred_kilogram_bomb", () -> new FiveHundredKilogramBombItem(new Item.Properties().stacksTo(1)));

    private BigExplosivesModItems() {
    }
}
