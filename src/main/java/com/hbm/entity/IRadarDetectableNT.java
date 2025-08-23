package com.hbm.entity;

public interface IRadarDetectableNT {

	public static final int TIER0 =		0;
	public static final int TIER1 =		1;
	public static final int TIER2 =		2;
	public static final int TIER3 =		3;
	public static final int TIER4 =		4;
	public static final int TIER10 =	5;
	public static final int TIER10_15 =	6;
	public static final int TIER15 =	7;
	public static final int TIER15_20 =	8;
	public static final int TIER20 =	9;
	public static final int TIER_AB =	10;
	public static final int PLAYER =	11;
	public static final int ARTY =		12;
	/** Reserved type that shows a unique purple blip. Used for when nothing else applies. */
	public static final int SPECIAL =	13;

	/** 雷达上显示的导弹名称 */
	public String getUnlocalizedName();
	/** 雷达图上导弹的威胁等级 */
	public int getBlipLevel();
	/** 是否可以被雷达发现 */
	public boolean canBeSeenBy(Object radar);
	/** 当前雷达参数是否足以发现导弹 */
	public boolean paramsApplicable(RadarScanParams params);
	/** 被雷达发现是否可以转化为红石信号输出 */
	public boolean suppliesRedstone(RadarScanParams params);
	
	public static class RadarScanParams {
		public boolean scanMissiles = true;
		public boolean scanShells = true;
		public boolean scanPlayers = true;
		public boolean smartMode = true;
		
		public RadarScanParams(boolean m, boolean s, boolean p, boolean smart) {
			this.scanMissiles = m;
			this.scanShells = s;
			this.scanPlayers = p;
			this.smartMode = smart;
		}
	}
}
