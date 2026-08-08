package com.hbm.item;

/**
 * I'm not at all sure if bunching together all these enums in one long class is a good idea
 * but I don't want to make a new class for every multi item to hold the enum
 * since that's entirely against the point of ItemEnumMulti to begin with.
 * @author hbm
 */
public class ItemEnums {

	public enum EnumCokeType {
		COAL,
		LIGNITE,
		PETROLEUM
	}

	public enum EnumTarType {
		CRUDE,
		CRACK,
		COAL,
		WOOD,
		WAX,
		PARAFFIN
	}

	public enum EnumAshType {
		WOOD,
		COAL,
		MISC,
		FLY,
		SOOT,
		FULLERENE
	}

	public enum EnumBriquetteType {
		COAL,
		LIGNITE,
		WOOD
	}

	public enum EnumLegendaryType {
		TIER1,
		TIER2,
		TIER3
	}

	public enum EnumPlantType {
		TOBACCO,
		ROPE,
		MUSTARDWILLOW,
	}

	
	public enum EnumChunkType {
		RARE,
		MALACHITE,
		CRYOLITE,
		PENTLANDITE,
		MOONSTONE
	}

	public enum EnumAchievementType {
		GOFISH,
		ACID,
		BALLS,
		DIGAMMASEE,
		DIGAMMAFEEL,
		DIGAMMAKNOW,
		DIGAMMAKAUAIMOHO,
		DIGAMMAUPONTOP,
		DIGAMMAFOROURRIGHT,
		QUESTIONMARK
	}

	public enum EnumFuelAdditive {
		ANTIKNOCK,
		DEICER
	}

	public enum EnumPages {
		PAGE1, PAGE2, PAGE3, PAGE4, PAGE5, PAGE6, PAGE7, PAGE8
	}

	public enum EnumSecretType {
		CANISTER, CONTROLLER, SELENIUM_STEEL, ABERRATOR, FOLLY
	}

	public enum EnumCasingType {
		SMALL, LARGE, SMALL_STEEL, LARGE_STEEL, SHOTSHELL, BUCKSHOT, BUCKSHOT_ADVANCED
	}

	public enum EnumIngotMetal {
		SCRAP, INGOT, COUNTER, KEY, BEACON, CASING, CLOCKWORK, BAR, DETECTOR
	}

	public enum EnumExpensiveType {
		STEEL_PLATING, HEAVY_FRAME, CIRCUIT, LEAD_PLATING, FERRO_PLATING, COMPUTER, BRONZE_TUBES, PLASTIC, GOLD_DUST, DEGENERATE_MATTER, STAINLESS_BEAM, AVIONICS, ASTRO
	}

	public enum EnumElectrodeType {
		GRAPHITE(	10),
		LANTHANIUM(	100),
		DESH(		500),
		SATURNITE(	1500);

		public int durability;

		private EnumElectrodeType(int dura) {
			this.durability = dura;
		}
	}
}
