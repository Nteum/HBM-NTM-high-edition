package com.hbm.api.badthing;

import com.hbm.config.RadiationConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
/**
 * 给所有生物额外添加的属性
 * 本来是继承IExtendedEntityProperties接口的
 * 但高版本没有这个接口了，暂时保留这个类。
 * */
public class HbmLivingProps {
	
	public static final String key = "NTM_EXT_LIVING";
	public static final UUID digamma_UUID = UUID.fromString("2a3d8aec-5ab9-4218-9b8b-ca812bdf378b");
	public LivingEntity entity;
	
	/// VALS ///
	private float radiation;
	private float digamma;
	private int asbestos;
	public static final int maxAsbestos = 60 * 60 * 20;
	private int blacklung;
	public static final int maxBlacklung = 2 * 60 * 60 * 20;
	private float radEnv;
	private float radBuf;
	private int bombTimer;
	private int contagion;
	private int oil;
	public int fire;
	public int balefire;
	private List<ContaminationEffect> contamination = new ArrayList();
	
	public HbmLivingProps(LivingEntity entity) {
		this.entity = entity;
	}
	
	/// DATA ///
	/** 向一个实体注册这些数据 */
	public static HbmLivingProps registerData(LivingEntity entity) {
		return null;
	}
	/** 从这个实体中获取相关数据 */
	public static HbmLivingProps getData(LivingEntity entity) {
		return loadNBTData(entity);
	}

	public static CompoundTag toNBTData(HbmLivingProps hbmLivingProps) {

		CompoundTag props = new CompoundTag();

		props.putFloat("hfr_radiation", hbmLivingProps.radiation);
		props.putFloat("hfr_digamma", hbmLivingProps.digamma);
		props.putInt("hfr_asbestos", hbmLivingProps.asbestos);
		props.putInt("hfr_bomb", hbmLivingProps.bombTimer);
		props.putInt("hfr_contagion", hbmLivingProps.contagion);
		props.putInt("hfr_blacklung", hbmLivingProps.blacklung);
		props.putInt("hfr_oil", hbmLivingProps.oil);
		props.putInt("hfr_fire", hbmLivingProps.fire);
		props.putInt("hfr_balefire", hbmLivingProps.balefire);

		props.putInt("hfr_cont_count", hbmLivingProps.contamination.size());

		for(int i = 0; i < hbmLivingProps.contamination.size(); i++) {
			hbmLivingProps.contamination.get(i).save(props, i);
		}

		return props;
	}


	public static HbmLivingProps loadNBTData(LivingEntity entity) {
		CompoundTag props = (CompoundTag)entity.getPersistentData().get(key);
		HbmLivingProps hbmLivingProps = new HbmLivingProps(entity);
		if (props == null){
			entity.getPersistentData().put(key,toNBTData(hbmLivingProps));
			return hbmLivingProps;
		}
//		CompoundTag props = (CompoundTag) nbt.get("HbmLivingProps");

		if(props != null) {
			hbmLivingProps.radiation = props.getFloat("hfr_radiation");
			hbmLivingProps.digamma = props.getFloat("hfr_digamma");
			hbmLivingProps.asbestos = props.getInt("hfr_asbestos");
			hbmLivingProps.bombTimer = props.getInt("hfr_bomb");
			hbmLivingProps.contagion = props.getInt("hfr_contagion");
			hbmLivingProps.blacklung = props.getInt("hfr_blacklung");
			hbmLivingProps.oil = props.getInt("hfr_oil");
			hbmLivingProps.fire = props.getInt("hfr_fire");
			hbmLivingProps.balefire = props.getInt("hfr_balefire");

			int cont = props.getInt("hfr_cont_count");

			for(int i = 0; i < cont; i++) {
				hbmLivingProps.contamination.add(ContaminationEffect.load(props, i));
			}
		}
		return hbmLivingProps;
	}
	
	/// RADIATION ///
	public static float getRadiation(LivingEntity entity) {
		if(!RadiationConfig.enableContamination)
			return 0;
		return getData(entity).radiation;
	}
	
	public static void setRadiation(LivingEntity entity, float rad) {
		if(RadiationConfig.enableContamination)
			getData(entity).radiation = rad;
	}
	
	public static void incrementRadiation(LivingEntity entity, float rad) {
		if(!RadiationConfig.enableContamination)
			return;
		
		HbmLivingProps data = getData(entity);
		float radiation = getData(entity).radiation + rad;
		
		if(radiation > 2500)
			radiation = 2500;
		if(radiation < 0)
			radiation = 0;
		
		setRadiation(entity, radiation);
	}
	
	/// RAD ENV ///
	public static float getRadEnv(LivingEntity entity) {
		return getData(entity).radEnv;
	}
	
	public static void setRadEnv(LivingEntity entity, float rad) {
		getData(entity).radEnv = rad;
	}
	
	/// RAD BUF ///
	public static float getRadBuf(LivingEntity entity) {
		return getData(entity).radBuf;
	}
	
	public static void setRadBuf(LivingEntity entity, float rad) {
		getData(entity).radBuf = rad;
	}
	
	/// CONTAMINATION ///
	public static List<ContaminationEffect> getCont(LivingEntity entity) {
		return getData(entity).contamination;
	}
	
	public static void addCont(LivingEntity entity, ContaminationEffect cont) {
		getData(entity).contamination.add(cont);
	}
	
	/// DIGAMA ///
	public static float getDigamma(LivingEntity entity) {
		return getData(entity).digamma;
	}
	//部分内容和当前不相关，注释掉了，以后再实现
	public static void setDigamma(LivingEntity entity, float digamma) {
		if(entity.level().isClientSide)
			return;
		
		getData(entity).digamma = digamma;
		
		float healthMod = (float)Math.pow(0.5, digamma) - 1F;
		
//		AttributeInstance attributeinstance = entity.getAttribute();
//
//		try {
//			attributeinstance.removeModifier(attributeinstance.getModifier(digamma_UUID));
//		} catch(Exception ex) { }
//
//		attributeinstance.applyModifier(new AttributeModifier(digamma_UUID, "digamma", healthMod, 2));
		
		if(entity.getHealth() > entity.getMaxHealth() && entity.getMaxHealth() > 0) {
			entity.setHealth(entity.getMaxHealth());
		}
		
//		if((entity.getMaxHealth() <= 0 || digamma >= 10.0F) && entity.isAlive()) {
//			entity.setAbsorptionAmount(0);
//			entity.attackEntityFrom(ModDamageSource.digamma, 500F);
//			entity.setHealth(0);
//			entity.onDeath(ModDamageSource.digamma);
//
//			CompoundTag data = new CompoundTag();
//			data.setString("type", "sweat");
//			data.setInteger("count", 50);
//			data.setInteger("block", Block.getIdFromBlock(Blocks.soul_sand));
//			data.setInteger("entity", entity.getEntityId());
//			PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, 0, 0, 0),  new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 50));
//		}
//
//		if(entity instanceof Player) {
//
//			float di = getData(entity).digamma;
//
//			if(di > 0F)
//				((EntityPlayer) entity).triggerAchievement(MainRegistry.digammaSee);
//			if(di >= 2F)
//				((EntityPlayer) entity).triggerAchievement(MainRegistry.digammaFeel);
//			if(di >= 10F)
//				((EntityPlayer) entity).triggerAchievement(MainRegistry.digammaKnow);
//		}
	}
	
	public static void incrementDigamma(LivingEntity entity, float digamma) {
		
		HbmLivingProps data = getData(entity);
		float dRad = getDigamma(entity) + digamma;
		
		if(dRad > 10)
			dRad = 10;
		if(dRad < 0)
			dRad = 0;
		
		setDigamma(entity, dRad);
	}
	
	
	/// ASBESTOS ///
	public static int getAsbestos(LivingEntity entity) {
		if(RadiationConfig.disableAsbestos) return 0;
		return getData(entity).asbestos;
	}
	
	public static void setAsbestos(LivingEntity entity, int asbestos) {
		if(RadiationConfig.disableAsbestos) return;
		getData(entity).asbestos = asbestos;
		
		if(asbestos >= maxAsbestos) {
			getData(entity).asbestos = 0;
//			entity.hurt(ModDamageSource.asbestos,1000);
		}
	}
	
	public static void incrementAsbestos(LivingEntity entity, int asbestos) {
		if(RadiationConfig.disableAsbestos) return;
		setAsbestos(entity, getAsbestos(entity) + asbestos);
		
//		if(entity instanceof EntityPlayerMP) {
//			PacketDispatcher.wrapper.sendTo(new PlayerInformPacket(ChatBuilder.start("").nextTranslation("info.asbestos").color(EnumChatFormatting.RED).flush(), MainRegistry.proxy.ID_GAS_HAZARD, 3000), (EntityPlayerMP) entity);
//		}
	}
	
	/// BLACK LUNG DISEASE ///
	public static int getBlackLung(LivingEntity entity) {
		if(RadiationConfig.disableCoal) return 0;
		return getData(entity).blacklung;
	}
	
	public static void setBlackLung(LivingEntity entity, int blacklung) {
		if(RadiationConfig.disableCoal) return;
		getData(entity).blacklung = blacklung;
		
		if(blacklung >= maxBlacklung) {
			getData(entity).blacklung = 0;
//			entity.hurt(ModDamageSource.blacklung,1000);
		}
	}
	
	public static void incrementBlackLung(LivingEntity entity, int blacklung) {
		if(RadiationConfig.disableCoal) return;
		setBlackLung(entity, getBlackLung(entity) + blacklung);
		
//		if(entity instanceof EntityPlayerMP) {
//			PacketDispatcher.wrapper.sendTo(new PlayerInformPacket(ChatBuilder.start("").nextTranslation("info.coaldust").color(EnumChatFormatting.RED).flush(), MainRegistry.proxy.ID_GAS_HAZARD, 3000), (EntityPlayerMP) entity);
//		}
	}
	
	/// TIME BOMB ///
	public static int getTimer(LivingEntity entity) {
		return getData(entity).bombTimer;
	}
	
	public static void setTimer(LivingEntity entity, int bombTimer) {
		getData(entity).bombTimer = bombTimer;
	}
	
	/// CONTAGION ///
	public static int getContagion(LivingEntity entity) {
		return getData(entity).contagion;
	}
	
	public static void setContagion(LivingEntity entity, int contageon) {
		getData(entity).contagion = contageon;
	}
	
	/// OIL ///
	public static int getOil(LivingEntity entity) { return getData(entity).oil; }
	public static void setOil(LivingEntity entity, int oil) { getData(entity).oil = oil; }
	
	public static class ContaminationEffect {
		
		public float maxRad;
		public int maxTime;
		public int time;
		public boolean ignoreArmor;
		
		public ContaminationEffect(float rad, int time, boolean ignoreArmor) {
			this.maxRad = rad;
			this.maxTime = this.time = time;
			this.ignoreArmor = ignoreArmor;
		}
		
		public float getRad() {
			return maxRad * ((float)time / (float)maxTime);
		}
		
		public void save(CompoundTag nbt, int index) {
			CompoundTag me = new CompoundTag();
			me.putFloat("maxRad", this.maxRad);
			me.putInt("maxTime", this.maxTime);
			me.putInt("time", this.time);
			me.putBoolean("ignoreArmor", ignoreArmor);
			nbt.put("cont_" + index, me);
		}
		
		public static ContaminationEffect load(CompoundTag nbt, int index) {
			CompoundTag me = (CompoundTag) nbt.get("cont_" + index);
            assert me != null;
            float maxRad = me.getFloat("maxRad");
			int maxTime = nbt.getInt("maxTime");
			int time = nbt.getInt("time");
			boolean ignoreArmor = nbt.getBoolean("ignoreArmor");
			
			ContaminationEffect effect = new ContaminationEffect(maxRad, maxTime, ignoreArmor);
			effect.time = time;
			return effect;
		}
	}
}
