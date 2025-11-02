package com.hbm.Inventory.fluid.trait;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.ExtendedFluidType;
import com.hbm.addational_data.Pollution;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class FT_Polluting extends FluidTrait {

	//original draft had both of them inside a hashmap for the release type but honestly handling hash maps in hash maps adds more complexity than it removes
	public HashMap<Pollution.Type, Float> releaseMap = new HashMap();
	public HashMap<Pollution.Type, Float> burnMap = new HashMap();

	public FT_Polluting release(Pollution.Type type, float amount) {
		releaseMap.put(type, amount);
		return this;
	}

	public FT_Polluting burn(Pollution.Type type, float amount) {
		burnMap.put(type, amount);
		return this;
	}

	@Override
	public void addInfo(List<Component> info) {
		info.add(Component.translatable(HBMLang.FT_POLLUTION1.key()).withStyle(ChatFormatting.GOLD));
	}

	@Override
	public void addInfoHidden(List<Component> info) {
		if(!this.releaseMap.isEmpty()) {
			info.add(Component.translatable(HBMLang.FT_POLLUTION2.key()).withStyle(ChatFormatting.GREEN));
			for(Entry<Pollution.Type, Float> entry : releaseMap.entrySet())
				info.add(Component.translatable(HBMLang.FT_PER_MB.key(),entry.getValue(),entry.getKey()).withStyle(ChatFormatting.GREEN));
		}
		if(!this.burnMap.isEmpty()) {
			info.add(Component.translatable(HBMLang.FT_POLLUTION3.key()).withStyle(ChatFormatting.RED));
			for(Entry<Pollution.Type, Float> entry : burnMap.entrySet())
				info.add(Component.translatable(HBMLang.FT_PER_MB.key(),entry.getValue(),entry.getKey()).withStyle(ChatFormatting.RED));
		}
	}

	@Override
	public void onFluidRelease(Level world, BlockPos pos, FluidTank tank, int overflowAmount, FluidReleaseType type) {
//		if(type == FluidReleaseType.SPILL) for(Entry<Pollution.Type, Float> entry : releaseMap.entrySet()) PollutionHandler.incrementPollution(world, x, y, z, entry.getKey(), entry.getValue());
//		if(type == FluidReleaseType.BURN) for(Entry<Pollution.Type, Float> entry : burnMap.entrySet()) PollutionHandler.incrementPollution(world, x, y, z, entry.getKey(), entry.getValue());
	}

	@Override
	public void serializeJSON(JsonWriter writer) throws IOException {
		writer.name("release").beginObject();
		for(Entry<Pollution.Type, Float> entry : releaseMap.entrySet()) {
			writer.name(entry.getKey().name()).value(entry.getValue());
		}
		writer.endObject();
		writer.name("burn").beginObject();
		for(Entry<Pollution.Type, Float> entry : burnMap.entrySet()) {
			writer.name(entry.getKey().name()).value(entry.getValue());
		}
		writer.endObject();
	}

	@Override
	public void deserializeJSON(JsonObject obj) {
		if(obj.has("release")) {
			JsonObject release = obj.get("release").getAsJsonObject();
			for(Pollution.Type type : Pollution.Type.values()) {
				if(release.has(type.name())) {
					releaseMap.put(type, release.get(type.name()).getAsFloat());
				}
			}
		}
		if(obj.has("burn")) {
			JsonObject release = obj.get("burn").getAsJsonObject();
			for(Pollution.Type type : Pollution.Type.values()) {
				if(release.has(type.name())) {
					burnMap.put(type, release.get(type.name()).getAsFloat());
				}
			}
		}
	}

	public static void pollute(Level world, int x, int y, int z, ExtendedFluidType type, FluidReleaseType release, float mB) {
		FT_Polluting trait = type.getTrait(FT_Polluting.class);
		if(trait == null) return;
		if(release == FluidReleaseType.VOID) return;

		HashMap<Pollution.Type, Float> map = release == FluidReleaseType.BURN ? trait.burnMap : trait.releaseMap;

		for(Entry<Pollution.Type, Float> entry : map.entrySet()) {
//			PollutionHandler.incrementPollution(world, x, y, z, entry.getKey(), entry.getValue() * mB);
		}
	}
}
