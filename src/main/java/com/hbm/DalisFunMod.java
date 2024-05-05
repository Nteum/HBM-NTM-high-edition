package com.hbm;

import com.hbm.block.ModBlocks;
import com.hbm.client.gui.ModScreenHandlers;
import com.hbm.entity.blockentity.ModBlockEntities;
import com.hbm.item.ModItemGroups;
import com.hbm.item.ModItems;
import com.hbm.tag.ModTagKeys;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DalisFunMod implements ModInitializer {
	public static final String MOD_ID = "hbm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		//注意：先注册物品，再注册物品组，否则物品组找不到物品
		ModItems.registerModItems();
		ModTagKeys.registerTag();
		ModScreenHandlers.registerScreenHandler();
		ModBlockEntities.register();
		ModBlocks.registerBlocks();
		ModItemGroups.registerModItemGroups();
	}
}