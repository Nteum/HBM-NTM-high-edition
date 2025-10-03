package com.hbm.item.armor;

import com.hbm.api.item.IGasMask;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

//
public class ArmorRegistry {
    public static HashMap<Item, ArrayList<HazardClass>> hazardClasses = new HashMap();

    public static void registerHazard(Item item, HazardClass... hazards) {
        hazardClasses.put(item, new ArrayList<HazardClass>(Arrays.asList(hazards)));
    }
    public static ItemStack getArmor(LivingEntity entity, int slot){
        Iterable<ItemStack> armorSlots = entity.getArmorSlots();
        if (armorSlots instanceof List<ItemStack> armorList){
            return armorList.get(slot);
        }else {
            for (int i = 0; i < 4; i++) {
                ItemStack next = entity.getArmorSlots().iterator().next();
                if (i == slot) return next;
            }
        }
        return ItemStack.EMPTY;
    }
    public static boolean checkArmorNull(LivingEntity entity, int slot) {
        return getArmor(entity,slot).isEmpty();
    }
    public static boolean hasAllProtection(LivingEntity entity, int slot, HazardClass... clazz) {
        if(checkArmorNull(entity, slot)) return false;

        Set<HazardClass> list = getProtectionFromItem(getArmor(entity, slot), entity);
        return list.containsAll(Arrays.asList(clazz));
    }
    public static boolean hasAnyProtection(LivingEntity entity, int slot, HazardClass... clazz) {
        if(checkArmorNull(entity, slot)) return false;

        Set<HazardClass> list = getProtectionFromItem(getArmor(entity, slot), entity);

        for(HazardClass haz : clazz) {
            if(list.contains(haz)) return true;
        }

        return false;
    }

    public static boolean hasProtection(LivingEntity entity, int slot, HazardClass clazz) {
        if(checkArmorNull(entity, slot)) return false;

        Set<HazardClass> list = getProtectionFromItem(getArmor(entity, slot), entity);

        return list.contains(clazz);
    }
    public static Set<HazardClass> getProtectionFromItem(ItemStack stack, LivingEntity entity) {
        Set<HazardClass> prot = new HashSet<>();
        Item item = stack.getItem();
        //if the item has HazardClasses assigned to it, add those
        if(hazardClasses.containsKey(item))
            prot.addAll(hazardClasses.get(item));

        if(item instanceof IGasMask mask) {
            ItemStack filter = mask.getFilter(stack, entity);

            if(filter != null) {
                //add the HazardClasses from the filter, then remove the ones blacklisted by the mask
                List<HazardClass> filProt = (List<HazardClass>) hazardClasses.get(filter.getItem()).clone();
                for(HazardClass c : mask.getBlacklist(stack, entity)) filProt.remove(c);
                prot.addAll(filProt);
            }
        }

        if(ArmorModHandler.hasMods(stack)) {
            ItemStack[] mods = ArmorModHandler.pryMods(stack);
            for(ItemStack mod : mods) {
                //recursion! run the exact same procedure on every mod, in case future mods will have filter support
                if(mod != null) prot.addAll(getProtectionFromItem(mod, entity));
            }
        }

        return prot;
    }

    public enum HazardClass {
        GAS_LUNG("hazard.gasChlorine"),				//also attacks eyes -> no half mask
        GAS_MONOXIDE("hazard.gasMonoxide"),				//only affects lungs
        GAS_INERT("hazard.gasInert"),					//SA
        PARTICLE_COARSE("hazard.particleCoarse"),		//only affects lungs
        PARTICLE_FINE("hazard.particleFine"),			//only affects lungs
        BACTERIA("hazard.bacteria"),					//no half masks
        //NERVE_AGENT("hazard.nerveAgent"),				//aggressive nerve agent, also attacks skin
        GAS_BLISTERING("hazard.corrosive"),				//corrosive substance, also attacks skin
        SAND("hazard.sand"),							//blinding sand particles
        LIGHT("hazard.light");							//blinding light

        public final String lang;

        private HazardClass(String lang) {
            this.lang = lang;
        }
    }
}
