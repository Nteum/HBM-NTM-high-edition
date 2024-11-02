package com.hbm.api;

import com.hbm.main.HBMxx;
import com.hbm.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

//列出所有tag，实际添加tag的动作在itemtagprovider中
public class HBMTags {
    public static class HBMItemTags extends ItemTagsProvider{
        //原版
        public static final TagKey<Item> COAL = ItemTags.create(hbm("vanilla/coal"));
        public static final TagKey<Item> IRON = ItemTags.create(hbm("vanilla/iron"));
        public static final TagKey<Item> GOLD = ItemTags.create(hbm("vanilla/gold"));
        public static final TagKey<Item> LAPIS = ItemTags.create(hbm("vanilla/lapis"));
        public static final TagKey<Item> REDSTONE = ItemTags.create(hbm("vanilla/redstone"));
        public static final TagKey<Item> NETHERQUARTZ = ItemTags.create(hbm("vanilla/netherquartz"));
        public static final TagKey<Item> QUARTZ = ItemTags.create(hbm("vanilla/quartz"));
        public static final TagKey<Item> DIAMOND = ItemTags.create(hbm("vanilla/diamod"));
        public static final TagKey<Item> EMERALD = ItemTags.create(hbm("vanilla/emerald"));
        //放射性物质
        public static final TagKey<Item> U = ItemTags.create(hbm("rad/uranium"));
        public static final TagKey<Item> U233 = ItemTags.create(hbm("rad/uranium233"));
        public static final TagKey<Item> U235 = ItemTags.create(hbm("rad/uranium235"));
        public static final TagKey<Item> U238 = ItemTags.create(hbm("rad/uranium238"));
        public static final TagKey<Item> TH232 = ItemTags.create(hbm("rad/thorium"));
        public static final TagKey<Item> PU = ItemTags.create(hbm("rad/plutonium"));
        public static final TagKey<Item> PURG = ItemTags.create(hbm("rad/plutoniumrg"));
        public static final TagKey<Item> PU238 = ItemTags.create(hbm("rad/plutonium238"));
        public static final TagKey<Item> PU239 = ItemTags.create(hbm("rad/plutonium239"));
        public static final TagKey<Item> PU240 = ItemTags.create(hbm("rad/plutonium240"));
        public static final TagKey<Item> PU241 = ItemTags.create(hbm("rad/plutonium241"));
        public static final TagKey<Item> AM241 = ItemTags.create(hbm("rad/americium241"));
        public static final TagKey<Item> AM242 = ItemTags.create(hbm("rad/americium242"));
        public static final TagKey<Item> AMRG = ItemTags.create(hbm("rad/americiumrg"));
        public static final TagKey<Item> NP237 = ItemTags.create(hbm("rad/neptunium237"));
        public static final TagKey<Item> PO210 = ItemTags.create(hbm("rad/polonium210"));
        public static final TagKey<Item> TC99 = ItemTags.create(hbm("rad/technetium99"));
        public static final TagKey<Item> RA226 = ItemTags.create(hbm("rad/radium226"));
        public static final TagKey<Item> AC227 = ItemTags.create(hbm("rad/actinium227"));
        public static final TagKey<Item> CO60 = ItemTags.create(hbm("rad/cobalt60"));
        public static final TagKey<Item> AU198 = ItemTags.create(hbm("rad/gold198"));
        public static final TagKey<Item> PB209 = ItemTags.create(hbm("rad/lead209"));
        public static final TagKey<Item> SA326 = ItemTags.create(hbm("rad/schrabidium"));
        public static final TagKey<Item> SA327 = ItemTags.create(hbm("rad/solinium"));
        public static final TagKey<Item> SBD = ItemTags.create(hbm("rad/schrabidate"));
        public static final TagKey<Item> SRN = ItemTags.create(hbm("rad/schraranium"));
        public static final TagKey<Item> GH336 = ItemTags.create(hbm("rad/ghiorsium336"));
        public static final TagKey<Item> MUD = ItemTags.create(hbm("rad/watz_mud"));
        //稳定金属
        public static final TagKey<Item> TI = ItemTags.create(hbm("metal/titanium"));
        public static final TagKey<Item> MINGRADE = ItemTags.create(hbm("metal/mingrade"));
        public static final TagKey<Item> ALLOY = ItemTags.create(hbm("metal/advanced_alloy"));
        public static final TagKey<Item> W = ItemTags.create(hbm("metal/tungsten"));
        public static final TagKey<Item> AL = ItemTags.create(hbm("metal/aluminum"));
        public static final TagKey<Item> STEEL = ItemTags.create(hbm("metal/steel"));
        public static final TagKey<Item> TCALLOY = ItemTags.create(hbm("metal/tc_alloy"));
        public static final TagKey<Item> CDALLOY = ItemTags.create(hbm("metal/cd_alloy"));
        public static final TagKey<Item> BBRONZE = ItemTags.create(hbm("metal/bismuth_bronze"));
        public static final TagKey<Item> ABRONZE = ItemTags.create(hbm("metal/arsenic_bronze"));
        public static final TagKey<Item> PB = ItemTags.create(hbm("metal/lead"));
        public static final TagKey<Item> BI = ItemTags.create(hbm("metal/bismuth"));
        public static final TagKey<Item> AS = ItemTags.create(hbm("metal/arsenic"));
        public static final TagKey<Item> CA = ItemTags.create(hbm("metal/calcium"));
        public static final TagKey<Item> CD = ItemTags.create(hbm("metal/cadmium"));
        public static final TagKey<Item> TA = ItemTags.create(hbm("metal/tantalum"));
        public static final TagKey<Item> COLTAN = ItemTags.create(hbm("metal/coltan"));
        public static final TagKey<Item> NB = ItemTags.create(hbm("metal/niobium"));
        public static final TagKey<Item> BE = ItemTags.create(hbm("metal/beryllium"));
        public static final TagKey<Item> CO = ItemTags.create(hbm("metal/cobalt"));
        public static final TagKey<Item> B = ItemTags.create(hbm("metal/boron"));
        public static final TagKey<Item> SI = ItemTags.create(hbm("metal/silicon"));
        public static final TagKey<Item> GRAPHITE = ItemTags.create(hbm("metal/graphite"));
        public static final TagKey<Item> CARBON = ItemTags.create(hbm("metal/carbon"));
        public static final TagKey<Item> DURA = ItemTags.create(hbm("metal/dura_steel"));
        public static final TagKey<Item> POLYMER = ItemTags.create(hbm("metal/polymer"));
        public static final TagKey<Item> BAKELITE = ItemTags.create(hbm("metal/bakelite"));
        public static final TagKey<Item> PET = ItemTags.create(hbm("metal/pet"));
        public static final TagKey<Item> PC = ItemTags.create(hbm("metal/polycarbonate"));
        public static final TagKey<Item> PVC = ItemTags.create(hbm("metal/pvc"));
        public static final TagKey<Item> LATEX = ItemTags.create(hbm("metal/latex"));
        public static final TagKey<Item> RUBBER = ItemTags.create(hbm("metal/rubber"));
        public static final TagKey<Item> MAGTUNG = ItemTags.create(hbm("metal/magnetized_tungsten"));
        public static final TagKey<Item> CMB = ItemTags.create(hbm("metal/cmb_steel"));
        public static final TagKey<Item> DESH = ItemTags.create(hbm("metal/workers_alloy"));
        public static final TagKey<Item> STAR = ItemTags.create(hbm("metal/starmetal"));
        public static final TagKey<Item> BIGMT = ItemTags.create(hbm("metal/saturnite"));
        public static final TagKey<Item> FERRO = ItemTags.create(hbm("metal/ferrouranium"));
        public static final TagKey<Item> EUPH = ItemTags.create(hbm("metal/euphemium"));
        public static final TagKey<Item> DNT = ItemTags.create(hbm("metal/dineutronium"));
        public static final TagKey<Item> FIBER = ItemTags.create(hbm("metal/fiberglass"));
        public static final TagKey<Item> ASBESTOS = ItemTags.create(hbm("metal/asbestos"));
        public static final TagKey<Item> OSMIRIDIUM = ItemTags.create(hbm("metal/osmiridium"));


        //一般物品
        public static final TagKey<Item> COKE = net.minecraft.tags.ItemTags.create(new ResourceLocation(HBMxx.MODID,"coke"));
        public static final TagKey<Item> BRIQUETTE = net.minecraft.tags.ItemTags.create(new ResourceLocation(HBMxx.MODID,"briquette"));
        public HBMItemTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(pOutput, pLookupProvider, pBlockTags, modId, existingFileHelper);
        }
        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            tag(COKE).add(ModItems.coke_coal.get(),ModItems.coke_lignite.get(),ModItems.coke_petroleum.get());
            tag(BRIQUETTE).add(ModItems.briquette_coal.get(),ModItems.briquette_lignite.get(),ModItems.briquette_wood.get());
        }

        static ResourceLocation hbm(String s){return new ResourceLocation(HBMxx.MODID,s);}
    }
}
