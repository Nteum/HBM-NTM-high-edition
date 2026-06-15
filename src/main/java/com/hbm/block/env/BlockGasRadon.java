package com.hbm.block.env;

import com.hbm.item.armor.ArmorRegistry;
import com.hbm.item.armor.ArmorUtils;
import com.hbm.utils.ContaminationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockGasRadon extends BlockGas{
    public BlockGasRadon(Properties properties) {
        super(properties, 0.1F, 0.8F, 0.1F);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        super.entityInside(pState, pLevel, pPos, pEntity);
        if (pEntity instanceof LivingEntity livingEntity) {
            if (ArmorRegistry.hasAllProtection(livingEntity, 3, ArmorRegistry.HazardClass.PARTICLE_FINE)) {

            }else{

            }

//            if(ArmorRegistry.hasAllProtection(entityLiving, 3, HazardClass.PARTICLE_FINE)) {
//                ArmorUtil.damageGasMaskFilter(entityLiving, 1);
//            } else {
//                ContaminationUtil.contaminate((EntityLivingBase)entity, HazardType.RADIATION, ContaminationType.RAD_BYPASS, 0.05F);
//                HbmLivingProps.incrementAsbestos((EntityLivingBase)entity, 1);
//            }
        }
    }
}
