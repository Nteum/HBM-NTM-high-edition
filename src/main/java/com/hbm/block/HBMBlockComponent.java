package com.hbm.block;

import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import com.hbm.utils.debug.BlockDebug;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * 各种原材料
 * */
public class HBMBlockComponent {
    private static final List<RegistryObject<Block>> matherialList = new ArrayList<>();
    public static RegistryObject<Block> STEEL_BLOCK;
    public static RegistryObject<Block> PLASTIC_BARREL;
    public static RegistryObject<Block> SELLAFIELD_SLAKED;

    public static void register(DeferredRegister<Block> BLOCKS){
        STEEL_BLOCK = registerWithItem("block_steel", ()->new Block(BlockBehaviour.Properties.of()));
        SELLAFIELD_SLAKED = registerWithItem("sellafield_slaked", ()->new Block(BlockBehaviour.Properties.of().explosionResistance(5.0f)));
    }
    private static RegistryObject<Block> registerWithItem(final String name, final Supplier<? extends Block> blocksup){
        RegistryObject<Block> blockRegistryObject = HBMMachine.registerBlockWithItem(ModBlocks.BLOCKS, name, blocksup);
        matherialList.add(blockRegistryObject);
        return blockRegistryObject;
    }

    public static void creativeTab(CreativeModeTab.Output pOutput){
        matherialList.forEach(blockRegistryObject -> pOutput.accept(blockRegistryObject.get()));
    }

    public static void genModel(BlockStateProvider provider){
        // 默认直接用simpleblock，所以名称和位置一定要放对
        matherialList.forEach(blockRegistryObject -> provider.simpleBlock(blockRegistryObject.get()));
    }

    public static void languageSupport(LanguageProvider provider){
        matherialList.forEach(blockRegistryObject -> provider.add(blockRegistryObject.get(), Arrays.stream(blockRegistryObject.getId().getPath().split("_")).map(s -> s.substring(0,1).toUpperCase() + s.substring(1)).reduce("",(r, id) -> r + " " + id)));
    }
    public static void lootable(BlockLootGen provider){
        provider.dropSelf(STEEL_BLOCK.get());
    }
}
