package com.hbm.Inventory.fluid.trait;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.HBMLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.util.List;

public class FT_PWRModerator extends FluidTrait {

	private double multiplier;
	public FT_PWRModerator(){}
	public FT_PWRModerator(double mulitplier) {
		this.multiplier = mulitplier;
	}

	public double getMultiplier() {
		return multiplier;
	}

	@Override
	public void addInfo(List<Component> info) {
		info.add(Component.translatable(HBMLang.FT_PWRMODERATOR.key()).withStyle(ChatFormatting.BLUE));
	}

	@Override
	public void addInfoHidden(List<Component> info) {
		int mult = (int) (multiplier * 100 - 100);
		info.add(Component.translatable(HBMLang.FT_CORE_FLUX.key(), mult).withStyle(ChatFormatting.BLUE));
	}

	@Override
	public void serializeJSON(JsonWriter writer) throws IOException {
		writer.name("multiplier").value(multiplier);
	}

	@Override
	public void deserializeJSON(JsonObject obj) {
		this.multiplier = obj.get("multiplier").getAsDouble();
	}
}
