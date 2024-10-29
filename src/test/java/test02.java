import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hbm.HBMxx;
import com.hbm.recipe.BlastFurnaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test02 {
    public static void main(String[] args) {
        Gson gson = new Gson();
//        ItemStack item1 = new ItemStack(Items.BAMBOO_PLANKS,1);
//        ItemStack item2 = new ItemStack(Items.APPLE,2);
//        ItemStack item3 = new ItemStack(Items.ENCHANTED_GOLDEN_APPLE);
        ResourceLocation id = new ResourceLocation(HBMxx.MODID, BlastFurnaceRecipe.TYPE);

        Map<Object,Object> map = new HashMap<>();
        map.put("type",id.toString());
        List<Map<String,Integer>> recipe = new ArrayList<>();

        recipe.add((Map<String, Integer>) new HashMap<>().put("def",2));
        recipe.add((Map<String, Integer>) new HashMap<>().put("ijk",3));
        map.put("recipe",recipe);
        String json = gson.toJson(map);
        System.out.println(json);
    }
}
