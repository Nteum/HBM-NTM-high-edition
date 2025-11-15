package com.hbm.addational_data.entity.player;

import com.hbm.HBM;
import com.hbm.addational_data.AdditionalDataManager;
import com.hbm.addational_data.DataEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HBM.MODID)
public class RightClickCount implements INBTSerializable<CompoundTag> {
    int count;
    public RightClickCount(){
        count = 0;
    }

    public Component getMessage(){
        count++;
        return Component.translatable("msg.hbm.right_click_time", count);
    }
//    @SubscribeEvent
//    public static void onPlayerBorn(PlayerEvent.PlayerLoggedInEvent event){
//        if (!event.getEntity().level().isClientSide())
//            AdditionalDataManager.getEntityData(event.getEntity()).ifPresent(data -> {
//                if (data.getData(DataEntry.RIGHT_CLICK_COUNT).isEmpty()){
//                    data.setData(DataEntry.RIGHT_CLICK_COUNT, new RightClickCount());
//                    event.getEntity().sendSystemMessage(Component.literal("Add Right click ability."));
//                }
//            });
//    }
//    @SubscribeEvent
//    public static void onRightClick(PlayerInteractEvent.RightClickBlock event){
//        if (!event.getEntity().level().isClientSide())
//            AdditionalDataManager.getEntityData(event.getEntity()).ifPresent(data -> {
//                data.getData(DataEntry.RIGHT_CLICK_COUNT).ifPresent(right_click -> {
//                    event.getEntity().sendSystemMessage(((RightClickCount) right_click).getMessage());
//                });
//            });
//    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("count", count);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        count = nbt.getInt("count");
    }
}
