package com.hbm.block;

import com.hbm.HBMLang;
import com.hbm.block.states.BlockFluidPipe;
import com.hbm.block.machine.BlockChemplant;
import com.hbm.block.machine.BlockFluidBarrel;
import com.hbm.datagen.LanguageProvider;
import com.hbm.registries.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class HBMMachine {
    public static RegistryObject<Block> CHEMPLANT;
    public static RegistryObject<Block> PLASTIC_BARREL;
    public static RegistryObject<Block> CORRODED_BARREL;
    public static RegistryObject<Block> IRON_BARREL;
    public static RegistryObject<Block> STEEL_BARREL;
    public static RegistryObject<Block> TCALLOY_BARREL;
    public static RegistryObject<Block> ANTIMATTER_BARREL;
    public static RegistryObject<Block> FLUID_PIPE;
    public static void register(DeferredRegister<Block> BLOCKS){
        CHEMPLANT = registerBlockWithItem(BLOCKS, "chemplant", ()->new BlockChemplant(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(30.0F)));
        PLASTIC_BARREL = registerBlockWithItem(BLOCKS, "barrel_plastic", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.STONE), BlockFluidBarrel.BarrelProperties.of().capacity(12000)));
        CORRODED_BARREL = registerBlockWithItem(BLOCKS, "barrel_corroded", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(6000).hotResist().corrosiveResistance().highCorroResist().leaky()));
        IRON_BARREL = registerBlockWithItem(BLOCKS, "barrel_iron", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(8000).hotResist()));
        STEEL_BARREL = registerBlockWithItem(BLOCKS, "barrel_steel", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(16000).hotResist().corrosiveResistance()));
        TCALLOY_BARREL = registerBlockWithItem(BLOCKS, "barrel_tcalloy", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(24000).hotResist().highCorroResist()));
        ANTIMATTER_BARREL = registerBlockWithItem(BLOCKS, "barrel_antimatter", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(16000).hotResist().highCorroResist().antimatter()));
        FLUID_PIPE = registerBlockWithItem(BLOCKS, "fluid_pipe", ()->new BlockFluidPipe(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(10.0F)));
    }
    public static RegistryObject<Block> registerBlockWithItem(DeferredRegister<Block> BLOCKS, final String name, final Supplier<? extends Block> blocksup){
        RegistryObject<Block> block = BLOCKS.register(name,blocksup);
        ModItems.ITEMS.register(name,()->new BlockItem(block.get(),new Item.Properties()));
        return block;
    }

    public static void creativeTab(CreativeModeTab.Output pOutput){
        pOutput.accept(CHEMPLANT.get());
        pOutput.accept(PLASTIC_BARREL.get());
        pOutput.accept(CORRODED_BARREL.get());
        pOutput.accept(IRON_BARREL.get());
        pOutput.accept(STEEL_BARREL.get());
        pOutput.accept(TCALLOY_BARREL.get());
        pOutput.accept(ANTIMATTER_BARREL.get());
        pOutput.accept(FLUID_PIPE.get());
    }

    public static void languageSupport(LanguageProvider provider){
        provider.add(CHEMPLANT.get(), "Chemical Plant");
        provider.add(PLASTIC_BARREL.get(), "Safe Barrel™");
        provider.add(CORRODED_BARREL.get(), "Corroded Barrel");
        provider.add(IRON_BARREL.get(), "Iron Barrel");
        provider.add(STEEL_BARREL.get(), "Steel Barrel");
        provider.add(TCALLOY_BARREL.get(), "Technetium Steel Barrel");
        provider.add(ANTIMATTER_BARREL.get(), "Magnetic Antimatter Container");
        provider.add(HBMLang.FLUID_CAPACITY.getTranslationKey(), "Capacity: %1$s mB");
        provider.add(HBMLang.BARREL.getTranslationKey(), "");
    }

}
