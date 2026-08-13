package com.hbm.Inventory.recipe.base;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hbm.HBM;
import com.hbm.Inventory.recipe.RecipeHelper;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import com.hbm.datagen.recipe.ingredient.FluidStackIngredient;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class RecipeSerializerBuilder {
    // 💡 只保存“字段类型与名称”的声明（Schema），不保存具体的 value，保证单例安全！
    private final List<FieldSchema<?>> fieldSchemas = new ArrayList<>();
    public static final int TYPE_STACK = 1;
    public static final int TYPE_INGREDIENT = 2;
    public static final int TYPE_COUNTABLE_INGREDIENT = 3;
    public static final int TYPE_FLUID_STACK = 4;
    public static final int TYPE_FLUID_INGREDIENT = 5;


    public RecipeSerializerBuilder integer(String name) {
        fieldSchemas.add(FieldSchemas.INT(name));
        return this;
    }
    public RecipeSerializerBuilder stack(String name) {
        fieldSchemas.add(FieldSchemas.ITEM(name));
        return this;
    }

    public RecipeSerializerBuilder ingredient(String name){
        fieldSchemas.add(FieldSchemas.INGREDIENT(name));
        return this;
    }

    public RecipeSerializerBuilder countableIngredient(String name){
        fieldSchemas.add(FieldSchemas.COUNTABLE_INGREDIENT(name));
        return this;
    }
    public RecipeSerializerBuilder fluid(String name){
        fieldSchemas.add(FieldSchemas.FLUID(name));
        return this;
    }
    public RecipeSerializerBuilder fluidIngredient(String name){
        fieldSchemas.add(FieldSchemas.FLUID_INGREDIENT(name));
        return this;
    }

    public RecipeSerializerBuilder list(String name, int typeKey) {
        // 1. 先用通配符拿到单元素的 Schema，此时编译器不会报错
        FieldSchema<?> elementSchema = switch (typeKey) {
            case TYPE_STACK -> FieldSchemas.ITEM_STACK(name);
            case TYPE_COUNTABLE_INGREDIENT -> FieldSchemas.COUNTABLE_INGREDIENT(name);
            case TYPE_FLUID_STACK -> FieldSchemas.FLUID(name);
            default -> FieldSchemas.INGREDIENT(name);
        };

        // 2. 调用下面的私有辅助方法，强行帮编译器完成类型握手
        addListSchemaHelper(name, elementSchema);
        return this;
    }

    // 💡 辅助方法：利用泛型捕获（Generic Capture）重新规整类型
    @SuppressWarnings("unchecked")
    private <E> void addListSchemaHelper(String name, FieldSchema<E> elementSchema) {
        this.fieldSchemas.add(FieldSchemas.LIST(name, elementSchema));
    }

    public <T extends AutoRecipe> RecipeSerializer<T> build(RecipeType<T> recipeType, AutoRecipeFactory<T> factory) {
        return new AutoRecipeSerializer<T>() {
            @Override
            public AutoRecipeBuilder getBuilder(ResourceLocation id) {
                return new AutoRecipeBuilder(id, this, fieldSchemas);
            }

            @Override
            public T fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
                Map<String, Object> values = new HashMap<>();
                for (FieldSchema<?> schema : fieldSchemas) {
                    values.put(schema.name, schema.jsonReader.read(pSerializedRecipe, schema.name));
                }
                T recipe = factory.create(pRecipeId, recipeType, this);
                recipe.initData(values);
                return recipe;
            }

            @Override
            public @Nullable T fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
                Map<String, Object> values = new HashMap<>();
                for (FieldSchema<?> schema : fieldSchemas) {
                    values.put(schema.name, schema.bufReader.read(pBuffer));
                }
                T recipe = factory.create(pRecipeId, recipeType, this);
                recipe.initData(values);
                return recipe; // 💡 修正：不再返回 null
            }

            @Override
            public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
                for (FieldSchema<?> schema : fieldSchemas) {
                    Object val = pRecipe.getValue(schema.name);
                    schema.writeBuf(pBuffer, val);
                }
            }
        };
    }

    // --- 内部静态架构类 ---

    // 💡 字段元数据声明 (纯逻辑，无状态，100% 线程安全)
    @SuppressWarnings("unchecked")
    private record FieldSchema<T>(
            String name,
            JsonReader<T> jsonReader,
            BufReader<T> bufReader,
            BufWriter<T> bufWriter,
            JsonWriter<T> jsonWriter
    ) {
        void writeBuf(FriendlyByteBuf buf, Object val) { bufWriter.write(buf, (T) val); }
        void writeJson(JsonObject json, Object val) { jsonWriter.write(json, name, (T) val); }
    }

    @FunctionalInterface interface JsonReader<T> { T read(JsonObject json, String name); }
    @FunctionalInterface interface BufReader<T> { T read(FriendlyByteBuf buf); }
    @FunctionalInterface interface BufWriter<T> { void write(FriendlyByteBuf buf, T val); }
    @FunctionalInterface interface JsonWriter<T> { void write(JsonObject json, String name, T val); }

    public static class FieldSchemas {

        // 1. 单个 Integer 的 FieldSchema
        public static FieldSchema<Integer> INT(String name) {
            return new FieldSchema<>(
                    name,
                    GsonHelper::getAsInt,
                    FriendlyByteBuf::readInt,
                    FriendlyByteBuf::writeInt,
                    JsonObject::addProperty
            );
        }

        public static FieldSchema<Float> FLOAT(String name) {
            return new FieldSchema<>(
                    name,
                    GsonHelper::getAsFloat,
                    FriendlyByteBuf::readFloat,
                    FriendlyByteBuf::writeFloat,
                    JsonObject::addProperty
            );
        }

        // 2. 单个 Item 的 FieldSchema
        public static FieldSchema<Item> ITEM(String name) {
            return new FieldSchema<>(
                    name,
                    GsonHelper::getAsItem,
                    buf -> ForgeRegistries.ITEMS.getValue(buf.readResourceLocation()),
                    (buf, item) -> buf.writeResourceLocation(ForgeRegistries.ITEMS.getKey(item)),
                    (json, key, item) -> json.addProperty(key, ForgeRegistries.ITEMS.getKey(item).toString())
            );
        }

        // 3. 单个 ItemStack 的 FieldSchema
        public static FieldSchema<ItemStack> ITEM_STACK(String name) {
            return new FieldSchema<>(
                    name,
                    (json, key) -> ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, key)),
                    FriendlyByteBuf::readItem,
                    FriendlyByteBuf::writeItem,
                    (json, key, stack) -> {
                        JsonObject stackObj = new JsonObject();
                        stackObj.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
                        if (stack.getCount() > 1) stackObj.addProperty("count", stack.getCount());
                        if (stack.hasTag()) stackObj.addProperty("nbt", stack.getTag().toString());
                        json.add(key, stackObj);
                    }
            );
        }

        // 4. 单个 Ingredient 的 FieldSchema
        public static FieldSchema<Ingredient> INGREDIENT(String name) {
            return new FieldSchema<>(
                    name,
                    (json, key) -> Ingredient.fromJson(json.get(key)),
                    Ingredient::fromNetwork,
                    (buf, ing) -> ing.toNetwork(buf),
                    (json, key, ing) -> json.add(key, ing.toJson())
            );
        }

        public static FieldSchema<CountableIngredient> COUNTABLE_INGREDIENT(String name) {
            return new FieldSchema<>(
                    name,
                    // JSON 读 (使用原版 ShapedRecipe 的标准解析)
                    (json, key) -> CountableIngredient.Serializer.INSTANCE.parse(GsonHelper.getAsJsonObject(json, key)),
                    // Buffer 读
                    CountableIngredient.Serializer.INSTANCE::parse,
                    // Buffer 写
                    CountableIngredient.Serializer.INSTANCE::write,
                    // JSON 写
                    (json, key, ingrdeient) -> json.add(key, ingrdeient.toJson())
            );
        }

        public static FieldSchema<FluidStack> FLUID(String name) {
            return new FieldSchema<FluidStack>(
                    name,
                    // JSON 读 (使用原版 ShapedRecipe 的标准解析)
                    (json, key) -> RecipeHelper.fluidStackFromJson(GsonHelper.getAsJsonObject(json, key)),
                    // Buffer 读
                    FriendlyByteBuf::readFluidStack,
                    // Buffer 写
                    FriendlyByteBuf::writeFluidStack,
                    // JSON 写
                    (json, key, fluid) -> json.add(key, RecipeHelper.fluidStackToJson(fluid))
            );
        }

        public static FieldSchema<FluidStackIngredient> FLUID_INGREDIENT(String name) {
            return new FieldSchema<FluidStackIngredient>(
                    name,
                    // JSON 读 (使用原版 ShapedRecipe 的标准解析)
                    (json, key) -> FluidStackIngredient.fromJson(GsonHelper.getAsJsonObject(json, key)),
                    // Buffer 读
                    FluidStackIngredient::fromNetwork,
                    // Buffer 写
                    FluidStackIngredient::toNetwork,
                    // JSON 写
                    (json, key, ingrdeient) -> json.add(key, ingrdeient.toJson())
            );
        }

        public static <T>FieldSchema<List<T>> LIST(String name, FieldSchema<T> elementSchema) {
            return new FieldSchema<List<T>>(
                    name,

                    // 1. JSON 读取逻辑：解析 JsonArray，循环调用单元素的 jsonReader
                    (json, key) -> {
                        JsonArray array = GsonHelper.getAsJsonArray(json, key);
                        List<T> list = new ArrayList<>();
                        for (JsonElement element : array) {
                            JsonObject elemObj = element.getAsJsonObject();

                            // 💡 关键修复：判断 elementSchema 的读取器需要的是外层包裹还是直接解析
                            // 如果传入的 key 是空字符串，我们需要构造一个“虚拟包裹”或者直接传本身
                            if (elementSchema.name() == null || elementSchema.name().isEmpty()) {
                                // 内层 Schema 没有 Key（如 ITEM_STACK("")），需要给它包裹一层假的 key 传进去，
                                // 或者直接调用不用 GsonHelper.getAsJsonObject 的直接解析逻辑
                                JsonObject dummyWrapper = new JsonObject();
                                dummyWrapper.add("val", elemObj);
                                list.add(elementSchema.jsonReader.read(dummyWrapper, "val"));
                            } else {
                                // 如果内层 Schema 有名字（如 ITEM_STACK("item")），去 elemObj 里去找该名字
                                if (elemObj.has(elementSchema.name())) {
                                    list.add(elementSchema.jsonReader.read(elemObj, elementSchema.name()));
                                } else {
                                    // 容错：如果找不到，把 elemObj 包装一下传进去
                                    JsonObject dummyWrapper = new JsonObject();
                                    dummyWrapper.add(elementSchema.name(), elemObj);
                                    list.add(elementSchema.jsonReader.read(dummyWrapper, elementSchema.name()));
                                }
                            }
                        }
                        return list;
                    },

                    // 2. Buffer 读取逻辑：先读 List 长度，再循环读出单元素
                    buf -> {
                        int size = buf.readVarInt(); // 用 VarInt 存长度，更省网络带宽
                        List<T> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            list.add(elementSchema.bufReader.read(buf));
                        }
                        return list;
                    },

                    // 3. Buffer 写入逻辑：先写入 List 长度，再循环写入单元素
                    (buf, list) -> {
                        buf.writeVarInt(list.size());
                        for (T item : list) {
                            elementSchema.writeBuf(buf, item);
                        }
                    },

                    // 4. JSON 写入逻辑 (用于 DataGen): 循环写入生成 JsonArray
                    (json, key, list) -> {
                        JsonArray array = new JsonArray();
                        for (T item : list) {
                            JsonObject dummyObj = new JsonObject();
                            elementSchema.writeJson(dummyObj, item);
                            // 提取写好的值放进 Array 里
                            if (dummyObj.has(elementSchema.name())) {
                                array.add(dummyObj.get(elementSchema.name()));
                            } else if (dummyObj.entrySet().size() == 1) {
                                array.add(dummyObj.entrySet().iterator().next().getValue());
                            } else {
                                array.add(dummyObj);
                            }
                        }
                        json.add(key, array);
                    }
            );
        }
    }

    // --- 基类配方定义 ---

    public static abstract class AutoRecipe implements Recipe<Container> {
        private final ResourceLocation id;
        private final RecipeType<?> type;
        private final RecipeSerializer<?> serializer;
        private final Map<String, Object> data = new HashMap<>();

        public <T extends AutoRecipe> AutoRecipe(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
            this.id = id;
            this.type = type;
            this.serializer = serializer;
        }

        void initData(Map<String, Object> parsedValues) {
            this.data.putAll(parsedValues);
            this.onDataLoaded(); // 提供给子类的钩子，比如从 data 提取数据赋值给成员变量
        }

        protected void onDataLoaded() {}

        public Object getValue(String name) { return data.get(name); }

        @Override public ResourceLocation getId() { return this.id; }
        @Override public RecipeSerializer<?> getSerializer() { return this.serializer; }
        @Override public RecipeType<?> getType() { return this.type; }

        @Override public boolean matches(Container pContainer, Level pLevel) { return true; }
        @Override public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) { return ItemStack.EMPTY; }
        @Override public boolean canCraftInDimensions(int pWidth, int pHeight) { return true; }
        @Override public ItemStack getResultItem(RegistryAccess pRegistryAccess) { return ItemStack.EMPTY; }

        public <T> T assemble(Container pContainer, Level pLevel){return null;}
    }

    @FunctionalInterface
    public interface AutoRecipeFactory<T extends AutoRecipe> {
        T create(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer);
    }

    // ---- recipebuilder ---
    public interface AutoRecipeSerializer<T extends Recipe<?>> extends RecipeSerializer<T> {
        AutoRecipeBuilder getBuilder(ResourceLocation id);
    }

    public static class AutoRecipeBuilder implements FinishedRecipe {
        private final ResourceLocation id;
        private final RecipeSerializer<?> serializer;
        private final List<FieldSchema<?>> fieldSchemas;
        private final Map<String, Object> values = new HashMap<>();

        public AutoRecipeBuilder(ResourceLocation id, RecipeSerializer<?> serializer, List<FieldSchema<?>> fieldSchemas) {
            this.id = id;
            this.serializer = serializer;
            this.fieldSchemas = fieldSchemas;
        }

        // 链式设置数据：通用方法
        public AutoRecipeBuilder setValue(String name, Object value) {
            this.values.put(name, value);
            return this;
        }

        public AutoRecipeBuilder setValue(Object ... objects){
            if (objects.length > fieldSchemas.size()) {
                HBM.LOGGER.error("Recipe [{}] 传入参数过多！预期最多 {} 个，实际传入 {} 个", id, fieldSchemas.size(), objects.length);
            }

            for (int i = 0; i < Math.min(objects.length, fieldSchemas.size()); i++) {
                FieldSchema<?> fieldSchema = fieldSchemas.get(i);
                values.put(fieldSchema.name(), objects[i]);
            }
            return this;
        }

        // 快捷方法封装
        public AutoRecipeBuilder setInt(String name, int value) { return setValue(name, value); }
        public AutoRecipeBuilder setList(String name, List<?> list) { return setValue(name, list); }

        // 💡 核心：利用 Schema 把存储的数据全部自动化写进 JsonObject
        @Override
        public void serializeRecipeData(JsonObject json) {
            for (FieldSchema<?> schema : fieldSchemas) {
                Object val = values.get(schema.name());
                if (val == null) {
                    HBM.LOGGER.warn("Recipe [{}] 的字段 [{}] 缺少数据，跳过序列化！", this.id, schema.name());
                    continue;
                }
                try {
                    schema.writeJson(json, val);
                } catch (Exception e) {
                    HBM.LOGGER.error("Recipe [{}] 序列化字段 [{}] 失败！传入的值为: [{}]",
                            this.id, schema.name(), String.valueOf(val), e);
                }
            }
        }

        public void save(Consumer<FinishedRecipe> consumer) {
            consumer.accept(this);
        }

        @Override public ResourceLocation getId() { return this.id; }
        @Override public RecipeSerializer<?> getType() { return this.serializer; }
        @Nullable @Override public JsonObject serializeAdvancement() { return null; }
        @Nullable @Override public ResourceLocation getAdvancementId() { return null; }
    }
}
