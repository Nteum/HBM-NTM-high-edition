package com.hbm.api.badthing;

import com.hbm.handler.radiation.ChunkRadiationManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;

import java.util.HashSet;

public class ContaminationUtil {
	
	/**
	 * 根据辐射抗性计算此生物可以获得多少辐射
	 * @param entity
	 * @return
	 */
	public static float calculateRadiationMod(LivingEntity entity) {
		
		if(entity instanceof Player player) {

			float koeff = 10.0F;
			return (float) Math.pow(koeff, -HazmatRegistry.getResistance(player));
		}
		
		return 1;
	}
	//获取实体辐射
	public static float getRads(Entity e) {

		if(!(e instanceof LivingEntity))
			return 0.0F;

		if(isRadImmune(e))
			return 0.0F;
		
		LivingEntity entity = (LivingEntity)e;
		
		return HbmLivingProps.getRadiation(entity);
	}
	
	public static HashSet<Class> immuneEntities = new HashSet();
	//是否免疫辐射
	public static boolean isRadImmune(Entity e) {

//		if(e instanceof LivingEntity && isPotionUseful((LivingEntity)e, HbmPotion.mutation))
//			return true;
		
		if(immuneEntities.isEmpty()) {
//			immuneEntities.add(EntityCreeperNuclear.class);
//			immuneEntities.add(EntityMooshroom.class);
//			immuneEntities.add(EntityZombie.class);
//			immuneEntities.add(EntitySkeleton.class);
//			immuneEntities.add(EntityQuackos.class);
//			immuneEntities.add(EntityOcelot.class);
//			immuneEntities.add(IRadiationImmune.class);
		}
		
		Class entityClass = e.getClass();
		
		for(Class clazz : immuneEntities) {
			if(clazz.isAssignableFrom(entityClass)) return true;
		}
		
		if("cyano.lootable.entities.EntityLootableBody".equals(entityClass.getName())) return true;
		
		return false;
	}
	
	/// ASBESTOS ///
	public static void applyAsbestos(Entity e, int i) {

		if(!(e instanceof LivingEntity entity))
			return;
		
		if(e instanceof Player && ((Player)e).isCreative())
			return;
		
		if(e instanceof Player && e.tickCount < 200)
			return;

//		if(ArmorRegistry.hasAllProtection(entity, 3, HazardClass.PARTICLE_FINE))
//			ArmorUtil.damageGasMaskFilter(entity, i);
//		else
		HbmLivingProps.incrementAsbestos(entity, i);
	}
	
	/// DIGAMMA ///
	public static void applyDigammaData(Entity e, float f) {

		if(!(e instanceof LivingEntity entity))
			return;
//		entity.canBeAffected()

//		if(e instanceof EntityDuck || e instanceof EntityOcelot)
//			return;
		
		if(e instanceof Player && ((Player)e).isCreative())
			return;
		
		if(e instanceof Player && e.tickCount < 200)
			return;

//		if(isPotionUseful(entity, HbmPotion.stability.id))
//			return;
		
//		if(!(entity instanceof Player && ArmorUtil.checkForDigamma((Player) entity)))
//			HbmLivingProps.incrementDigamma(entity, f);
	}
	
	public static void applyDigammaDirect(Entity e, float f) {

		if(!(e instanceof LivingEntity entity))
			return;

//		if(e instanceof IRadiationImmune)
//			return;
		
		if(e instanceof Player && ((Player)e).isCreative())
			return;

		HbmLivingProps.incrementDigamma(entity, f);
	}
	//获取digamma数据
	public static float getDigamma(Entity e) {

		if(!(e instanceof LivingEntity))
			return 0.0F;
		
		LivingEntity entity = (LivingEntity)e;
		return HbmLivingProps.getDigamma(entity);
	}
	//打印盖格计数器的信息
	public static void printGeigerData(Player player) {

		Level world = player.level();

		double eRad = ((int)(HbmLivingProps.getRadiation(player) * 10)) / 10D;

		double rads = ((int)(ChunkRadiationManager.proxy.getRadiation(world, player.getOnPos())) * 10) / 10D;
		double env = ((int)(HbmLivingProps.getRadBuf(player) * 10D)) / 10D;
		
		double res = ((int)(10000D - ContaminationUtil.calculateRadiationMod(player) * 10000D)) / 100D;
		double resKoeff = ((int)(HazmatRegistry.getResistance(player) * 100D)) / 100D;

		String chunkPrefix = getPreffixFromRad(rads);
		String envPrefix = getPreffixFromRad(env);
		String radPrefix = "";
		String resPrefix = "" + ChatFormatting.WHITE;
		
		if(eRad < 200)
			radPrefix += ChatFormatting.GREEN;
		else if(eRad < 400)
			radPrefix += ChatFormatting.YELLOW;
		else if(eRad < 600)
			radPrefix += ChatFormatting.GOLD;
		else if(eRad < 800)
			radPrefix += ChatFormatting.RED;
		else if(eRad < 1000)
			radPrefix += ChatFormatting.DARK_RED;
		else
			radPrefix += ChatFormatting.DARK_GRAY;
		
		if(resKoeff > 0)
			resPrefix += ChatFormatting.GREEN;

		//localization and server-side restrictions have turned this into a painful mess
		//a *functioning* painful mess, nonetheless
		player.displayClientMessage(Component.literal("===== ☢ ").append(Component.translatable("geiger.title")).append(Component.literal(" ☢ =====")).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),true);
		player.displayClientMessage(Component.translatable("geiger.chunkRad").append(Component.literal(" " + chunkPrefix + rads + " RAD/s")).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)),true);
		player.displayClientMessage(Component.translatable("geiger.envRad").append(Component.literal(" " + envPrefix + env + " RAD/s")).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)),true);
		player.displayClientMessage(Component.translatable("geiger.playerRad").append(Component.literal(" " + radPrefix + eRad + " RAD")).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)),true);
		player.displayClientMessage(Component.translatable("geiger.playerRes").append(Component.literal(" " + resPrefix + res + "% (" + resKoeff + ")")).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)),true);
	}
	//打印辐射剂量信息
	public static void printDosimeterData(Player player) {

		double env = ((int)(HbmLivingProps.getRadBuf(player) * 10D)) / 10D;
		boolean limit = false;
		
		if(env > 3.6D) {
			env = 3.6D;
			limit = true;
		}
		
		String envPrefix = getPreffixFromRad(env);
		
		player.displayClientMessage(Component.literal("===== ☢ ").append(Component.translatable("geiger.title.dosimeter")).append(Component.literal(" ☢ =====")).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),true);
		player.displayClientMessage(Component.translatable("geiger.envRad").append(Component.literal(" " + envPrefix + (limit ? ">" : "") + env + " RAD/s")).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)),true);
	}
	//根据辐射强度返回字体颜色
	public static String getPreffixFromRad(double rads) {

		String chunkPrefix = "";
		
		if(rads == 0)
			chunkPrefix += ChatFormatting.GREEN;
		else if(rads < 1)
			chunkPrefix += ChatFormatting.YELLOW;
		else if(rads < 10)
			chunkPrefix += ChatFormatting.GOLD;
		else if(rads < 100)
			chunkPrefix += ChatFormatting.RED;
		else if(rads < 1000)
			chunkPrefix += ChatFormatting.DARK_RED;
		else
			chunkPrefix += ChatFormatting.DARK_GRAY;
		
		return chunkPrefix;
	}
	//打印玩家健康信息
	public static void printDiagnosticData(Player player) {

		double digamma = ((int)(HbmLivingProps.getDigamma(player) * 100)) / 100D;
		double halflife = ((int)((1D - Math.pow(0.5, digamma)) * 10000)) / 100D;
		
		player.displayClientMessage(Component.literal("===== Ϝ ").append(Component.translatable("digamma.title")).append(Component.literal(" Ϝ =====")).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE)),true);
		player.displayClientMessage(Component.translatable("digamma.playerDigamma").append(Component.literal(ChatFormatting.RED + " " + digamma + " DRX")).setStyle(Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)),true);
		player.displayClientMessage(Component.translatable("digamma.playerHealth").append(Component.literal(ChatFormatting.RED + " " + halflife + "%")).setStyle(Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)),true);
		player.displayClientMessage(Component.translatable("digamma.playerRes").append(Component.literal(ChatFormatting.BLUE + " " + "N/A")).setStyle(Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)),true);
	}
	//测试一下药水是否有用的工具类
	public static boolean isPotionUseful(LivingEntity entity, Potion potion){
		for (MobEffectInstance effect : potion.getEffects()) {
			if (entity.canBeAffected(effect))return true;
		}
		return false;
	}
	//灾害类型
	public static enum HazardType {
		RADIATION,
		DIGAMMA
	}
	//污染类型
	public static enum ContaminationType {
		FARADAY,			//preventable by metal armor
		HAZMAT,				//preventable by hazmat
		HAZMAT2,			//preventable by heavy hazmat
		DIGAMMA,			//preventable by fau armor or stability
		DIGAMMA2,			//preventable by robes
		CREATIVE,			//preventable by creative mode, for rad calculation armor piece bonuses still apply
		RAD_BYPASS,			//same as creative but will not apply radiation resistance calculation
		NONE				//not preventable
	}
	
	/**
	 * 判断一个实体是否可以被某种灾害和类型感染。
	 * This system is nice but the cont types are a bit confusing. Cont types should have much better names and multiple cont types should be applicable.
	 */
	@SuppressWarnings("incomplete-switch") //just shut up
	//instead of this does-everything-but-nothing-well solution, please use the ArmorRegistry to check for protection and the HBM Props for applying contamination. still good for regular radiation tho
	public static boolean contaminate(LivingEntity entity, HazardType hazard, ContaminationType cont, float amount) {
		
		if(hazard == HazardType.RADIATION) {
			float radEnv = HbmLivingProps.getRadEnv(entity);
			HbmLivingProps.setRadEnv(entity, radEnv + amount);
		}
		
		if(entity instanceof Player player) {
			//判断盔甲是防辐射
//			switch(cont) {
//			case FARADAY:			if(ArmorUtil.checkForFaraday(player))	return false; break;
//			case HAZMAT:			if(ArmorUtil.checkForHazmat(player))	return false; break;
//			case HAZMAT2:			if(ArmorUtil.checkForHaz2(player))		return false; break;
//			case DIGAMMA:			if(ArmorUtil.checkForDigamma(player))	return false; if(ArmorUtil.checkForDigamma2(player))	return false; break;
//			case DIGAMMA2:			if(ArmorUtil.checkForDigamma2(player))	return false; break;
//			}
			
			if(player.isCreative() && cont != ContaminationType.NONE && cont != ContaminationType.DIGAMMA2)
				return false;
			
			if(player.tickCount < 200)
				return false;
		}
		
		if(hazard == HazardType.RADIATION && isRadImmune(entity))
			return false;
		
		switch(hazard) {
		case RADIATION: HbmLivingProps.incrementRadiation(entity, amount * (cont == ContaminationType.RAD_BYPASS ? 1 : calculateRadiationMod(entity))); break;
		case DIGAMMA: HbmLivingProps.incrementDigamma(entity, amount); break;
		}
		
		return true;
	}
}
