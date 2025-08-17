package com.hbm.Inventory.fluid.trait;


import com.hbm.HBMLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public class FluidTraitSimple {

	public static class FT_Gaseous extends FluidTrait {
		@Override public void addInfoHidden(List<Component> info) {
			info.add(Component.translatable(HBMLang.FT_GASEOUS.key()).withStyle(ChatFormatting.BLUE));
		}
	}

	/** gaseous at room temperature, for cryogenic hydrogen for example */
	public static class FT_Gaseous_ART extends FluidTrait {
		@Override public void addInfoHidden(List<Component> info) {
			info.add(Component.translatable(HBMLang.FT_GASEOUS_ART.key()).withStyle(ChatFormatting.BLUE))
			;
		}
	}

	public static class FT_Liquid extends FluidTrait {
		@Override public void addInfoHidden(List<Component> info) {
			info.add(Component.translatable(HBMLang.FT_LIQUID.key()).withStyle(ChatFormatting.BLUE));
		}
	}

	/** to viscous to be sprayed/turned into a mist */
	public static class FT_Viscous extends FluidTrait {
		@Override public void addInfoHidden(List<Component> info) {
			info.add(Component.translatable(HBMLang.FT_VISCOUS.key()).withStyle(ChatFormatting.BLUE))
			;
		}
	}

	public static class FT_Plasma extends FluidTrait {
		@Override public void addInfoHidden(List<Component> info) {
			info.add(Component.translatable(HBMLang.FT_PLASMA.key()).withStyle(ChatFormatting.LIGHT_PURPLE))
			;
		}
	}

	public static class FT_Amat extends FluidTrait {
		@Override public void addInfo(List<Component> info) {
			info.add(Component.translatable(HBMLang.FT_AMAT.key()).withStyle(ChatFormatting.DARK_RED));
		}
	}

	public static class FT_LeadContainer extends FluidTrait {
		@Override public void addInfo(List<Component> info) {
			info.add(Component.translatable(HBMLang.FT_LEAD_CONTAINER.key()).withStyle(ChatFormatting.DARK_RED));
		}
	}

	public static class FT_Delicious extends FluidTrait {
		@Override public void addInfoHidden(List<Component> info) {
			info.add(Component.translatable(HBMLang.FT_DELICIOUS.key()).withStyle(ChatFormatting.DARK_GREEN));
		}
	}

	public static class FT_Unsiphonable extends FluidTrait {
		@Override public void addInfoHidden(List<Component> info) {
			info.add(Component.translatable(HBMLang.FT_UNSIPHONABLE.key()).withStyle(ChatFormatting.BLUE));
		}
	}

	public static class FT_NoID extends FluidTrait { }
	public static class FT_NoContainer extends FluidTrait { }
}
