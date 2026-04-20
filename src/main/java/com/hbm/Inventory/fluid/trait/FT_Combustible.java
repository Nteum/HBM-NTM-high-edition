package com.hbm.Inventory.fluid.trait;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.HBMLang;
import com.hbm.utils.math.BobMth;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.util.List;

public class FT_Combustible extends FluidTrait {
	
	protected FuelGrade fuelGrade;
	protected long combustionEnergy;
	
	public FT_Combustible() { }
	
	public FT_Combustible(FuelGrade grade, long energy) {
		this.fuelGrade = grade;
		this.combustionEnergy = energy;
	}
	
	@Override
	public void addInfo(List<Component> info) {
		super.addInfo(info);
		info.add(Component.translatable(HBMLang.FT_COMBUSTIBLE1.key()).withStyle(ChatFormatting.GOLD));
		info.add(Component.translatable(HBMLang.FT_COMBUSTIBLE2.key(), BobMth.getShortNumber(combustionEnergy)).withStyle(ChatFormatting.GOLD));
		info.add(Component.translatable(HBMLang.FT_COMBUSTIBLE3.key(), fuelGrade.getGrade()).withStyle(ChatFormatting.GOLD));
	}
	
	public long getCombustionEnergy() {
		return this.combustionEnergy;
	}
	
	public FuelGrade getGrade() {
		return this.fuelGrade;
	}
	
	public static enum FuelGrade {
		LOW("Low"),			//heating and industrial oil				< star engine, iGen
		MEDIUM("Medium"),	//petroil									< diesel generator
		HIGH("High"),		//diesel, gasoline							< HP engine
		AERO("Aviation"),	//kerosene and other light aviation fuels	< turbofan
		GAS("Gaseous");		//fuel gasses like NG, PG and syngas		< gas turbine
		
		private String grade;
		
		private FuelGrade(String grade) {
			this.grade = grade;
		}
		
		public String getGrade() {
			return this.grade;
		}
	}

	@Override
	public void serializeJSON(JsonWriter writer) throws IOException {
		writer.name("energy").value(combustionEnergy);
		writer.name("grade").value(fuelGrade.name());
	}
	
	@Override
	public void deserializeJSON(JsonObject obj) {
		this.combustionEnergy = obj.get("energy").getAsLong();
		this.fuelGrade = FuelGrade.valueOf(obj.get("grade").getAsString());
	}
}
