package com.hbm.core.contents.fluid;

public enum EnumSymbol {
	NONE(0, 0),
	RADIATION(195, 2),		// 辐射
	NOWATER(195, 63),		// 非水
	ACID(195, 124),			// 酸液
	ASPHYXIANT(195, 185),	// 毒气
	CROYGENIC(134, 185),		// 低温
	ANTIMATTER(73, 185),		// 反物质
	OXIDIZER(12, 185);		// 氧化剂
	
	public int x;
	public int y;
	
	private EnumSymbol(int x, int y) {
		this.x = x;
		this.y = y;
	}
}