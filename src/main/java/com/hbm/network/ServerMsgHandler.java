package com.hbm.network;

import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerMsgHandler {
    public static ConcurrentMap<Integer, Set<KeyMapping>> pressedKey = new ConcurrentHashMap<>();

    public static void tick(TickEvent.ServerTickEvent event){
        if (event.phase == TickEvent.Phase.START){
            // 每tick开始清空所有内容，然后接收每tick新的按键请求
            pressedKey.clear();
        }
    }

    public static boolean keyPressed(Player player, KeyMapping keyMapping){
        return pressedKey.containsKey(player.getId()) && pressedKey.get(player.getId()).contains(keyMapping);
    }
}
