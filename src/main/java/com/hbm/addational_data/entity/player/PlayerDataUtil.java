package com.hbm.addational_data.entity.player;

import com.hbm.addational_data.AdditionalDataManager;
import com.hbm.addational_data.DataEntry;
import com.hbm.network.ServerMsgHandler;
import com.hbm.registries.ModKeyMapping;
import net.minecraft.world.entity.player.Player;

public class PlayerDataUtil {
    public static boolean isJetpackActive(Player player){
        return AdditionalDataManager.checkEntityData(player, DataEntry.JETPACK_ENABLE) && ServerMsgHandler.keyPressed(player, ModKeyMapping.jetpackKey);
    }
}
