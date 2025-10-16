package com.hbm.registries;

import com.hbm.blockentity.tools.TileEntityGeiger;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toserver.C2SKeyMessage;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class ModKeyMapping {
    public static List<KeyMapping> keys = new ArrayList<>();
    public static Map<KeyMapping, String> translateMap = new HashMap<>();

    public static final String category = "key.category.hbm";
    public static final String prefix = "key.hbm";
    
    public static KeyMapping calculatorKey = add(new KeyMapping(prefix + ".calculator", InputConstants.KEY_N, category), "Calculator");
    public static KeyMapping jetpackKey = add(new KeyMapping(prefix + ".toggle_back", InputConstants.KEY_C, category), "Toggle Jetpack");
    public static KeyMapping magnetKey = add(new KeyMapping(prefix + ".toggle_magnet", InputConstants.KEY_Z, category), "Toggle Magnet");
    public static KeyMapping hudKey = add(new KeyMapping(prefix + ".toggle_hud", InputConstants.KEY_V, category), "Toggle HUD");
    public static KeyMapping dashKey = add(new KeyMapping(prefix + ".dash", InputConstants.KEY_LSHIFT, category), "Dash");
    public static KeyMapping trainKey = add(new KeyMapping(prefix + ".train_inv", InputConstants.KEY_R, category), "Train Inventory");
    public static KeyMapping qmaw = add(new KeyMapping(prefix + ".qmaw", InputConstants.KEY_F1, category), "[ Press %s for help ]");
    public static KeyMapping abilityCycle = add(new KeyMapping(prefix + ".ability", InputConstants.UNKNOWN.getValue(), category), "Cycle Tool Abilities");
    public static KeyMapping abilityAlt = add(new KeyMapping(prefix + ".ability_alt", InputConstants.KEY_LALT, category), "Configure Tool Abilities");
    public static KeyMapping copyToolAlt = add(new KeyMapping(prefix + ".copy_tool_alt", InputConstants.KEY_LALT, category), "Copy Tool: Switch Paste");
    public static KeyMapping copyToolCtrl = add(new KeyMapping(prefix + ".copy_tool_ctrl", InputConstants.KEY_LCONTROL, category), "Copy Tool: Paste to Pipes");
    public static KeyMapping reloadKey = add(new KeyMapping(prefix + ".reload", InputConstants.KEY_R, category), "Reload");
    public static KeyMapping gunPrimaryKey = add(new KeyMapping(prefix + ".gun_primary", InputConstants.UNKNOWN.getValue(), category), "Primary Fire");
    public static KeyMapping gunSecondaryKey = add(new KeyMapping(prefix + ".gun_secondary", InputConstants.UNKNOWN.getValue(), category), "Secondary Fire");
    public static KeyMapping gunTertiaryKey = add(new KeyMapping(prefix + ".gun_tertitary", InputConstants.UNKNOWN.getValue(), category), "Gun Sights");
    public static KeyMapping craneUpKey = add(new KeyMapping(prefix + ".crane_move_up", InputConstants.KEY_UP, category), "Move Crane Forward");
    public static KeyMapping craneDownKey = add(new KeyMapping(prefix + ".crane_move_down", InputConstants.KEY_DOWN, category), "Move Crane Backward");
    public static KeyMapping craneLeftKey = add(new KeyMapping(prefix + ".crane_move_left", InputConstants.KEY_LEFT, category), "Move Crane Left");
    public static KeyMapping craneRightKey = add(new KeyMapping(prefix + ".crane_move_right", InputConstants.KEY_RIGHT, category), "Move Crane Right");
    public static KeyMapping craneLoadKey = add(new KeyMapping(prefix + ".craneload", InputConstants.KEY_RETURN, category), "Load/Unload Crane");
    public static KeyMapping add(KeyMapping keyMapping, String translateName){
        keys.add(keyMapping);
        translateMap.put(keyMapping, translateName);
        return keyMapping;
    }
    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event){
        keys.forEach(event::register);
    }
    public static void localName(LanguageProvider provider){
        provider.add(category, "NTM Hotkeys");
        translateMap.forEach((k, n) -> provider.add(k.getName(),n));
        translateMap = null;
    }
    public static void preCheck(InputEvent.Key event){
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.isPaused() || minecraft.player == null) return;
        // 检查你的特定按键绑定
        List<Integer> keyNums = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).consumeClick()){
                keyNums.add(i);
            }
        }
        // 有按键按下则发送数据包
        if (!keyNums.isEmpty()){
            ModMessages.sendToServer(new C2SKeyMessage(keyNums.stream().mapToInt(Integer::intValue).toArray()));
        }
    }
}
