package com.hbm.block;

import com.hbm.HBMLang;
import com.hbm.block.logistic.BlockFluidPipe;
import com.hbm.block.machine.BlockChemplant;
import com.hbm.block.machine.BlockFluidBarrel;
import com.hbm.block.tools.GeigerCounter;
import com.hbm.block.weapon.LaunchPad;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.item.HBMItems;
import com.hbm.utils.debug.BlockDebug;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.awt.desktop.OpenURIEvent;
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
    public static RegistryObject<Block> DEBUG_BLOCK;
    public static RegistryObject<Block> GEIGER_COUNTER;
    public static RegistryObject<Block> LAUNCH_PAD;
    public static void register(DeferredRegister<Block> BLOCKS){
        CHEMPLANT = registerBlockWithItem(BLOCKS, "chemplant", ()->new BlockChemplant(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(30.0F)));
        PLASTIC_BARREL = registerBlockWithItem(BLOCKS, "barrel_plastic", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.STONE), BlockFluidBarrel.BarrelProperties.of().capacity(12000)));
        CORRODED_BARREL = registerBlockWithItem(BLOCKS, "barrel_corroded", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(6000).hotResist().corrosiveResistance().highCorroResist().leaky()));
        IRON_BARREL = registerBlockWithItem(BLOCKS, "barrel_iron", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(8000).hotResist()));
        STEEL_BARREL = registerBlockWithItem(BLOCKS, "barrel_steel", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(16000).hotResist().corrosiveResistance()));
        TCALLOY_BARREL = registerBlockWithItem(BLOCKS, "barrel_tcalloy", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(24000).hotResist().highCorroResist()));
        ANTIMATTER_BARREL = registerBlockWithItem(BLOCKS, "barrel_antimatter", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(16000).hotResist().highCorroResist().antimatter()));
        FLUID_PIPE = registerBlockWithItem(BLOCKS, "fluid_pipe", ()->new BlockFluidPipe(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(10.0F)));
        GEIGER_COUNTER = registerBlockWithItem(BLOCKS, "geiger", ()->new GeigerCounter(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(10.0F)));
        LAUNCH_PAD = registerBlockWithItem(BLOCKS, "launch_pad", ()->new LaunchPad(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(10.0F)));

        DEBUG_BLOCK = registerBlockWithItem(BLOCKS, "debug_block", ()->new BlockDebug(BlockBehaviour.Properties.copy(Blocks.STONE)));
    }
    public static RegistryObject<Block> registerBlockWithItem(DeferredRegister<Block> BLOCKS, final String name, final Supplier<? extends Block> blocksup){
        RegistryObject<Block> block = BLOCKS.register(name,blocksup);
        HBMItems.ITEMS.register(name,()->new BlockItem(block.get(),new Item.Properties()));
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
        pOutput.accept(GEIGER_COUNTER.get());


        pOutput.accept(DEBUG_BLOCK.get());
    }

    public static void languageSupport(LanguageProvider provider){
        provider.add(CHEMPLANT.get(), "Chemical Plant");
        provider.add(PLASTIC_BARREL.get(), "Safe Barrel™");
        provider.add(CORRODED_BARREL.get(), "Corroded Barrel");
        provider.add(IRON_BARREL.get(), "Iron Barrel");
        provider.add(STEEL_BARREL.get(), "Steel Barrel");
        provider.add(TCALLOY_BARREL.get(), "Technetium Steel Barrel");
        provider.add(ANTIMATTER_BARREL.get(), "Magnetic Antimatter Container");
        provider.add(HBMLang.FLUID_CAPACITY.key(), "Capacity: %1$s mB");
        provider.add(HBMLang.BARREL.key(), "HBM Barrel");
        provider.add(GEIGER_COUNTER.get(), "Geiger Counter");
        provider.add(LAUNCH_PAD.get(), "Launch Pad");

        provider.add(DEBUG_BLOCK.get(), "Debug Block");
    }
    
    public static void lootable(BlockLootGen provider){
        provider.dropSelf(HBMMachine.CHEMPLANT.get());
        provider.dropSelf(HBMMachine.PLASTIC_BARREL.get());
        provider.dropSelf(HBMMachine.CORRODED_BARREL.get());
        provider.dropSelf(HBMMachine.IRON_BARREL.get());
        provider.dropSelf(HBMMachine.STEEL_BARREL.get());
        provider.dropSelf(HBMMachine.TCALLOY_BARREL.get());
        provider.dropSelf(HBMMachine.ANTIMATTER_BARREL.get());
        provider.dropSelf(HBMMachine.FLUID_PIPE.get());
        provider.dropSelf(HBMMachine.GEIGER_COUNTER.get());
        provider.dropSelf(HBMMachine.LAUNCH_PAD.get());

        provider.dropSelf(DEBUG_BLOCK.get());
    }

    public static void model(BlockStateGen provider){
        provider.horizontalBlockWithItem(HBMMachine.GEIGER_COUNTER.get());
        provider.horizontalBlockWithItem(HBMMachine.LAUNCH_PAD.get());
    }
}
