package com.hbm.Inventory.fluid.trait;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.ExtendedFluidType;
import com.hbm.Inventory.fluid.ModFluids;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class FT_Coolable extends FluidTrait {
	
	protected HashMap<CoolingType, Double> efficiency = new HashMap();
	
	public FluidType coolsTo;
	public int amountReq;
	public int amountProduced;
	public int heatEnergy;
	
	public FT_Coolable() { }
	
	public FT_Coolable(FluidType type, int req, int prod, int heat) {
		this.coolsTo = type;
		this.amountReq = req;
		this.amountProduced = prod;
		this.heatEnergy = heat;
	}
	
	public FT_Coolable setEff(CoolingType type, double eff) {
		efficiency.put(type, eff);
		return this;
	}
	
	public double getEfficiency(CoolingType type) {
		Double eff = this.efficiency.get(type);
		return eff != null ? eff : 0.0D;
	}
	
	@Override
	public void addInfoHidden(List<Component> info) {
		info.add(Component.translatable(HBMLang.FT_THERMAL_CAPACITY.key(), heatEnergy, amountReq).withStyle(ChatFormatting.RED));
		for(CoolingType type : CoolingType.values()) {
			double eff = getEfficiency(type);
			if(eff > 0) {
				info.add(Component.translatable(HBMLang.FT_EFFICIENCY.key(), type.name, ((int) (eff * 100D))).withStyle(ChatFormatting.YELLOW));
			}
		}
	}
	
	public static enum CoolingType {
		TURBINE("Turbine Steam"),
		HEATEXCHANGER("Coolable");
		
		public String name;
		
		private CoolingType(String name) {
			this.name = name;
		}
	}

	@Override
	public void serializeJSON(JsonWriter writer) throws IOException {
		writer.name("coolsTo").value(this.coolsTo.getDescriptionId());
		writer.name("amountReq").value(this.amountReq);
		writer.name("amountProd").value(this.amountProduced);
		writer.name("heatEnergy").value(this.heatEnergy);
		
		for(Entry<CoolingType, Double> entry : this.efficiency.entrySet()) {
			writer.name(entry.getKey().name()).value(entry.getValue());
		}
	}
	
	@Override
	public void deserializeJSON(JsonObject obj) {
		this.coolsTo = ExtendedFluidType.getFTFromJson(obj, "coolsTo");
		this.amountReq = obj.get("amountReq").getAsInt();
		this.amountProduced = obj.get("amountProd").getAsInt();
		this.heatEnergy = obj.get("heatEnergy").getAsInt();
		
		for(CoolingType type : CoolingType.values()) {
			if(obj.has(type.name())) efficiency.put(type, obj.get(type.name()).getAsDouble());
		}
	}
}
