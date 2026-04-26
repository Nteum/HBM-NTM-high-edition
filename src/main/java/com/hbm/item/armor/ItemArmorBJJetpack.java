package com.hbm.item.armor;

import com.hbm.addational_data.entity.player.PlayerDataUtil;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toclient.S2CParticlePacket;
import com.hbm.registries.ModSounds;
import com.hbm.render.model.Models;
import com.hbm.render.model.armor.ModelArmorBJ;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemArmorBJJetpack extends ItemArmorBJ{
    public ItemArmorBJJetpack(ArmorMaterial pMaterial, Type pType, Properties pProperties, long capacity, long in, long consum, long drain) {
        super(pMaterial, pType, pProperties, capacity, in, consum, drain);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                return ((ModelArmorBJ) Models.getEntityModel(Models.BJ)).adjustWithOrigin(original, equipmentSlot);
            }
        });
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
        if (level.isClientSide()){
            if (hasFSBArmor(player)){
                ArmorUtils.resetFlightTime(player);
                if (PlayerDataUtil.isJetpackActive(player)){
                    if (player.getDeltaMovement().y < 0.4d) player.push(0, 0.1d, 0);
                    player.fallDistance = 0;

                    level.playSound(null, player.getOnPos(), ModSounds.WEAPON_IMMOLATOR_SHOOT.get(), SoundSource.PLAYERS, 0.125F, 1.5F);

                    CompoundTag data = new CompoundTag();
                    data.putString("type", "jetpack_bj");
                    data.putInt("player", player.getId());
                    ModMessages.sendToAllAround(new S2CParticlePacket(data, player.getX(), player.getY(), player.getZ()), player, 100);
                } else if (player.hasPose(Pose.CROUCHING)){
                    double yMotion = player.getDeltaMovement().y;
                    if (yMotion < -0.08){
                        double mo = yMotion * -0.4;
                        player.getDeltaMovement().add(0,mo,0).add(player.getLookAngle().scale(mo));
                    }
                }
            }
        }
    }
}
