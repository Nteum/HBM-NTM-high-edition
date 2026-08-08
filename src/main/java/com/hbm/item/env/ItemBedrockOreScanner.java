package com.hbm.item.env;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.item.interfaces.IUpdateInHand;
import com.hbm.core.network.HBMNetwork;
import com.hbm.network.packet.toclient.S2CHUDPacket;
import com.hbm.registries.HBMDimensions;
import com.hbm.world.feature.BedrockOreDefinition;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class ItemBedrockOreScanner extends Item implements IUpdateInHand {
    public static final ResourceLocation id = HBM.rl("bedrock_ore_scanner");
    public ItemBedrockOreScanner(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
    }

    public static MutableComponent translateDensity(double density) {
        if(density <= 0.1) return HBMLang.ITEM_ORE_DENSITY_SCANNER_VERYPOOR.translate();
        if(density <= 0.35) return HBMLang.ITEM_ORE_DENSITY_SCANNER_POOR.translate();
        if(density <= 0.75) return HBMLang.ITEM_ORE_DENSITY_SCANNER_LOW.translate();
        if(density >= 1.9) return HBMLang.ITEM_ORE_DENSITY_SCANNER_EXCELLENT.translate();
        if(density >= 1.65) return HBMLang.ITEM_ORE_DENSITY_SCANNER_VERYHEIGH.translate();
        if(density >= 1.25) return HBMLang.ITEM_ORE_DENSITY_SCANNER_HIGH.translate();
        return HBMLang.ITEM_ORE_DENSITY_SCANNER_MODERATE.translate();
    }

    public static ChatFormatting getColor(double density) {
        if(density <= 0.1) return ChatFormatting.DARK_RED;
        if(density <= 0.35) return ChatFormatting.RED;
        if(density <= 0.75) return ChatFormatting.GOLD;
        if(density > 2) return ChatFormatting.LIGHT_PURPLE; // only for BO items that got mined with fortune
        if(density >= 1.9) return ChatFormatting.AQUA;
        if(density >= 1.65) return ChatFormatting.BLUE;
        if(density >= 1.25) return ChatFormatting.GREEN;
        return ChatFormatting.YELLOW;
    }

    @Override
    public void onUpdate(ItemStack stack, Level level, Entity player) {
        if (!(player instanceof Player) || level.getGameTime() % 5 != 0 || !HBMDimensions.LEVELS.contains(level.dimension())) return;
        double totalLevel = 0D;
        ItemBedrockOreCombine.CelestialBedrockOre celestialBedrockOre = ItemBedrockOreCombine.CelestialBedrockOre.get(level.dimension());
        CompoundTag tag = new CompoundTag();
        for (ItemBedrockOreCombine.CelestialBedrockOreType type : celestialBedrockOre.types) {
            double oreLevel = ItemBedrockOreRaw.getOreLevel((ServerLevel) level, player.blockPosition().getX(), player.blockPosition().getZ(), type);
            tag.putInt(type.suffix, (int) ((oreLevel * 100) / 100D));
            totalLevel += oreLevel;
        }
        totalLevel /= celestialBedrockOre.types.length;
        int tier = BedrockOreDefinition.getTier(totalLevel);
        FluidStack boreFluid1 = BedrockOreDefinition.getBoreFluid(totalLevel);
        tag.putInt(HBMKey.TIER, tier);
        tag.put(HBMKey.FLUIDS, boreFluid1.writeToNBT(new CompoundTag()));
        HBMNetwork.sendToPlayer(new S2CHUDPacket(id, 4000, tag), (ServerPlayer) player);
    }
}
