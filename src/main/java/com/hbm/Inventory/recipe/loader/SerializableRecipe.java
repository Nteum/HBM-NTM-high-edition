package com.hbm.Inventory.recipe.loader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个类用于将所有hbm自定义配方生成到json文件中
 * 在1.7.10的hbm原版中也有这个类
 * */
public abstract class SerializableRecipe {
    public static final Gson gson = new Gson();
    public static List<SerializableRecipe> recipeHandlers = new ArrayList();

    public static void registerAllHandlers() {
//        recipeHandlers.add(new BlastFurnaceRecipe());
    }

    public static void initialize() {
//        try {
//            registerAllHandlers();
//
//            String recipePath = "E:\\game\\MineCraftModDevelop\\HBM-forge\\HBM-forge\\src\\main\\resources" + "\\data\\" + HBMxx.MODID + "\\recipes";
////            String recipePath = SerializableRecipe.class.getClassLoader().getResource("data/" + HBMxx.MODID + "/recipes").toString();
//            File recipeFolder = new File(recipePath);
//            if (!recipeFolder.exists())recipeFolder.mkdir();
//            JsonWriter writer = null;
//
//            for(SerializableRecipe recipe : recipeHandlers) {
////                List<SerializableRecipe> recipeList = (List)recipe.getRecipeObject();
//                for (int i = 0; i < recipeList.size(); i++) {
//                    /** 创建配方文件 */
//                    File recipeFile = new File(recipeFolder,recipe.getFileName() + "_" + i + ".json");
//                    if (recipeFile.exists())recipeFile.delete();
//                    recipeFile.createNewFile();
//
//                    writer = new JsonWriter(new FileWriter(recipeFile));
//                    recipeList.get(i).writeRecipe(recipeList.get(i),writer);
//                    writer.close();
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }

    /** The machine's (or process') name used for the recipe file */
//    public abstract String getFileName();
    /** Is given a single recipe from the recipe list object (a wrapper, Tuple, array, HashMap Entry, etc) and writes it to the current ongoing GSON stream
     * @throws IOException */
//    public abstract void writeRecipe(Object recipe, JsonWriter writer) throws IOException;
    /** Return the list object holding all the recipes, usually an ArrayList or HashMap */
//    public abstract Object getRecipeObject();

    public void writeItemStack(ItemStack stack, JsonWriter writer) throws IOException {
        writer.beginArray();
        writer.setIndent("");
        writer.value(ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath());						//item name
        if(stack.getCount() != 1 || stack.getDamageValue() != 0) writer.value(stack.getCount());	//stack size
        if(stack.getDamageValue() != 0) writer.value(stack.getDamageValue());						//metadata
        writer.endArray();
        writer.setIndent("  ");
    }
}
