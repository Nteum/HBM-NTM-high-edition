package com.hbm.item.armor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorItem.*;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * 盔甲组装机相关功能
 * */
public class ArmorModHandler {

	public static final int helmet_only = 0;
	public static final int plate_only = 1;
	public static final int legs_only = 2;
	public static final int boots_only = 3;
	public static final int servos = 4;
	public static final int cladding = 5;
	public static final int kevlar = 6;
	public static final int extra = 7;
	public static final int battery = 8;

	public static final int MOD_SLOTS = 9;

	public static final UUID[] UUIDs = new UUID[] {
			UUID.fromString("8d6e5c77-133e-4056-9c80-a9e42a1a0b65"),
			UUID.fromString("b1b7ee0e-1d14-4400-8037-f7f2e02f21ca"),
			UUID.fromString("30b50d2a-4858-4e5b-88d4-3e3612224238"),
			UUID.fromString("426ee0d0-7587-4697-aaef-4772ab202e78")
	};

	public static final UUID[] fixedUUIDs = new UUID[] {
			UUID.fromString("e572caf4-3e65-4152-bc79-c4d4048cbd29"),
			UUID.fromString("bed30902-8a6a-4769-9f65-2a9b67469fff"),
			UUID.fromString("baebf7b3-1eda-4a14-b233-068e2493e9a2"),
			UUID.fromString("28016c1b-d992-4324-9409-a9f9f0ffb85c")
	};

	//The key for the NBTTagCompound that holds the armor mods
	public static final String MOD_COMPOUND_KEY = "ntm_armor_mods";
	//The key for the specific slot inside the armor mod NBT Tag
	public static final String MOD_SLOT_KEY = "mod_slot_";

	/**
	 * Checks if a mod can be applied to an armor piece
	 * Needs to be used to prevent people from inserting invalid items into the armor table
	 * @param armor
	 * @param mod
	 * @return
	 */
	public static boolean isApplicable(ItemStack armor, ItemStack mod) {
		if(armor == null || mod == null || !(armor.getItem() instanceof ArmorItem armorItem)
				|| !(mod.getItem() instanceof ItemArmorMod aMod)) return false;
		Type type = armorItem.getType();

		return (type == Type.HELMET && aMod.helmet) || (type == Type.CHESTPLATE && aMod.chestplate)
				|| (type == Type.LEGGINGS && aMod.leggings) || (type == Type.BOOTS && aMod.boots);
	}

	/**
	 * Applies an mod to the given armor piece
	 * Make sure to check for applicability first
	 * Will override present mods so make sure to only use unmodded armor pieces
	 * @param armor
	 * @param mod
	 */
	public static void applyMod(ItemStack armor, ItemStack mod) {
		CompoundTag tag = armor.getOrCreateTagElement(MOD_COMPOUND_KEY);

		ItemArmorMod aMod = (ItemArmorMod)mod.getItem();
		if (mod.getItem() instanceof ItemArmorMod armorMod){
			int slot = armorMod.type.ordinal();
			tag.put(MOD_SLOT_KEY + slot, mod.serializeNBT());
		}
	}

	/**
	 * Removes the mod from the given slot
	 * @param armor
	 * @param slot
	 */
	public static void removeMod(ItemStack armor, int slot) {
		CompoundTag tag = armor.getOrCreateTagElement(MOD_COMPOUND_KEY);
		tag.remove(MOD_SLOT_KEY + slot);
		if(tag.isEmpty()) armor.getOrCreateTag().remove(MOD_COMPOUND_KEY);
	}

	/**
	 * Removes ALL mods
	 * Should be used when the armor piece is put in the armor table slot AFTER the armor pieces have been separated
	 * @param armor
	 */
	public static void clearMods(ItemStack armor) {
		if (armor.hasTag() && armor.getTag() != null) armor.getTag().remove(MOD_COMPOUND_KEY);
	}

	/**
	 * Does what the name implies. Returns true if the stack has NBT and that NBT has the MOD_COMPOUND_KEY tag.
	 * @param armor
	 * @return
	 */
	public static boolean hasMods(ItemStack armor) {
		return armor.getTagElement(MOD_COMPOUND_KEY) == null;
	}

	/**
	 * Gets all the modifications in the provided armor
	 * @param armor
	 * @return
	 */
	public static ItemStack[] pryMods(ItemStack armor) {

		ItemStack[] slots = new ItemStack[MOD_SLOTS];

		CompoundTag mods = armor.getTagElement(MOD_COMPOUND_KEY);
		if (mods == null) return slots;

		for(int i = 0; i < MOD_SLOTS; i++) {
			ItemStack stack = ItemStack.of(mods.getCompound(MOD_SLOT_KEY + i));
            slots[i] = stack;
		}

		return slots;
	}

	public static ItemStack pryMod(ItemStack armor, int slot) {
		CompoundTag tag = armor.getTagElement(MOD_COMPOUND_KEY);
		if (tag == null) return ItemStack.EMPTY;
		ItemStack stack = ItemStack.of((CompoundTag) tag.get(MOD_SLOT_KEY + slot));
		if (!stack.isEmpty()) return stack;
		removeMod(armor, slot);
		return ItemStack.EMPTY;
	}
}
