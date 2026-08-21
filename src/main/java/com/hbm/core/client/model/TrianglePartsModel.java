package com.hbm.core.client.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.hbm.HBM;
import com.hbm.render.RenderUtils;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import joptsimple.internal.Strings;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.client.model.obj.ObjTokenizer;
import net.minecraftforge.client.textures.UnitTextureAtlasSprite;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.util.ConcatenatedListView;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
public class TrianglePartsModel implements IUnbakedGeometry<TrianglePartsModel> {

    public static class Loader implements IGeometryLoader<TrianglePartsModel>, ResourceManagerReloadListener {
        public static final String LOADER_NAME = "triangle_parts_obj";
        public static final Loader INSTANCE = new Loader();

        private ResourceManager manager = Minecraft.getInstance().getResourceManager();

        private final Map<ModelSettings, TrianglePartsModel> modelCache = Maps.newConcurrentMap();
        private final Map<ResourceLocation, ObjMaterialLibrary> materialCache = Maps.newConcurrentMap();
        // 注册client作为模组加载器
        public static void register(ModelEvent.RegisterGeometryLoaders event){
            event.register(TrianglePartsModel.Loader.LOADER_NAME, TrianglePartsModel.Loader.INSTANCE);
        }
        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            modelCache.clear();
            materialCache.clear();
            manager = resourceManager;
        }

        @Override
        public TrianglePartsModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
            if (!jsonObject.has("model"))
                throw new JsonParseException("OBJ Loader requires a 'model' key that points to a valid .OBJ model.");

            String modelLocation = jsonObject.get("model").getAsString();

            boolean automaticCulling = GsonHelper.getAsBoolean(jsonObject, "automatic_culling", true);
            boolean shadeQuads = GsonHelper.getAsBoolean(jsonObject, "shade_quads", true);
            boolean flipV = GsonHelper.getAsBoolean(jsonObject, "flip_v", false);
            boolean emissiveAmbient = GsonHelper.getAsBoolean(jsonObject, "emissive_ambient", true);
            String mtlOverride = GsonHelper.getAsString(jsonObject, "mtl_override", null);

            return loadModel(new ModelSettings(ResourceLocation.parse(modelLocation), automaticCulling, shadeQuads, flipV, emissiveAmbient, mtlOverride));
        }

        public TrianglePartsModel loadModel(ModelSettings settings)
        {
            return modelCache.computeIfAbsent(settings, (data) -> {
                Resource resource = manager.getResource(settings.modelLocation()).orElseThrow();
                try (ObjTokenizer tokenizer = new ObjTokenizer(resource.open()))
                {
                    return TrianglePartsModel.parse(tokenizer, settings);
                } catch (FileNotFoundException e)
                {
                    throw new RuntimeException("Could not find OBJ model", e);
                } catch (Exception e)
                {
                    throw new RuntimeException("Could not read OBJ model", e);
                }
            });
        }
        // 加载mtl的方法，我认为forge的方式没有问题，所以直接复制。
        public ObjMaterialLibrary loadMaterialLibrary(ResourceLocation materialLocation)
        {
            return materialCache.computeIfAbsent(materialLocation, (location) -> {
                Resource resource = manager.getResource(location).orElseThrow();
                try (ObjTokenizer rdr = new ObjTokenizer(resource.open()))
                {
                    return new ObjMaterialLibrary(rdr);
                } catch (FileNotFoundException e)
                {
                    throw new RuntimeException("Could not find OBJ material library", e);
                } catch (IOException e)
                {
                    HBM.LOGGER.error("Could not read OBJ material library", e);
                    e.printStackTrace();
                    throw new RuntimeException("Could not read OBJ material library", e);
                } catch (Exception e){
                    HBM.LOGGER.error("Could not read OBJ material library", e);
                    e.printStackTrace();
                    throw new RuntimeException("Exception unknown", e);
                }
            });
        }
    }
    // 用于数据生成
    public static class LoaderBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
        ResourceLocation model;
        boolean automatic_culling = false;
        boolean flip_v = false;
        boolean shadeQuads = true;
        boolean emissiveAmbient = true;
        String mtlOverride = null;
        // 💡 核心：这个构造器的参数顺序，完美对应了你要传给 customLoader 的那个 BiFunction！
        public LoaderBuilder(T modelBuilder, ExistingFileHelper existingFileHelper) {
            // 必须通过显式指定你自定义 Loader 的“注册ID”（比如 "your_mod:my_obj_loader"）
            super(HBM.rl(Loader.LOADER_NAME), modelBuilder, existingFileHelper);
        }

        public LoaderBuilder<T> setModel(ResourceLocation resourceLocation){
            this.model = resourceLocation;
            if (!this.model.getPath().endsWith(".obj")) {
                this.model = this.model.withSuffix(".obj");
            }
            if (!this.model.getPath().startsWith("models/")) {
                this.model = this.model.withPrefix("models/");
            }
            return this;
        }

        public LoaderBuilder<T> autoCull(boolean automatic_culling){
            this.automatic_culling = automatic_culling;
            return this;
        }

        public LoaderBuilder<T> flipV(boolean flip_v){
            this.flip_v = flip_v;
            return this;
        }

        // 💡 当 DataGen 最终写盘时，这个方法会被自动调用，把你的 Java 变量变成 JSON
        @Override
        public JsonObject toJson(JsonObject json) {
            json = super.toJson(json); // 这一步会自动把 "loader": "your_mod:my_obj_loader" 塞进去
            json.addProperty("model", this.model.toString());
            if (!automatic_culling) json.addProperty("automatic_culling", automatic_culling);
            if (flip_v) json.addProperty("flip_v", flip_v);
            if (!shadeQuads) json.addProperty("shade_quads", shadeQuads);
            if (!emissiveAmbient) json.addProperty("emissive_ambient", emissiveAmbient);
            if (mtlOverride != null && !mtlOverride.isEmpty()) json.addProperty("mtl_override", mtlOverride);
            // 如果你有其他操控子模型的参数，在这里塞进 JsonObject 即可
            return json;
        }
    }

    /**
     * 模型加载部分
     */
    private static final Vector4f COLOR_WHITE = new Vector4f(1, 1, 1, 1);
    private static final Vec2[] DEFAULT_COORDS = {
            new Vec2(0, 0),
            new Vec2(0, 1),
            new Vec2(1, 1),
            new Vec2(1, 0),
    };

    private final Map<String, ModelGroup> parts = Maps.newLinkedHashMap();

    private final List<Vector3f> positions = Lists.newArrayList();  // 模型位置
    private final List<Vec2> texCoords = Lists.newArrayList();      // 贴图位置
    private final List<Vector3f> normals = Lists.newArrayList();    // 法线
    private final List<Vector4f> colors = Lists.newArrayList();     // 颜色

    public final boolean automaticCulling;
    public final boolean shadeQuads;
    public final boolean flipV;
    public final boolean emissiveAmbient;
    @Nullable
    public final String mtlOverride;
    public final ResourceLocation modelLocation;

    private TrianglePartsModel(ModelSettings settings)
    {
        this.modelLocation = settings.modelLocation;
//        this.automaticCulling = settings.automaticCulling;
        this.automaticCulling = false;      // 先关掉看看能不能绕过检查
        this.shadeQuads = settings.shadeQuads;
        this.flipV = settings.flipV;
        this.emissiveAmbient = settings.emissiveAmbient;
        this.mtlOverride = settings.mtlOverride;
    }

    /**
     * forge原本的obj加载流程
     */
    public static TrianglePartsModel parse(ObjTokenizer tokenizer, ModelSettings settings) throws IOException
    {
        var model = new TrianglePartsModel(settings);                 // 待生成的模型
        var modelLocation = model.modelLocation;                 // 模型的位置
        var materialLibraryOverrideLocation = model.mtlOverride; // 模型mtl的路径

        // for relative references to material libraries
        String modelDomain = modelLocation.getNamespace();
        String modelPath = modelLocation.getPath();
        int lastSlash = modelPath.lastIndexOf('/');
        if (lastSlash >= 0)
            modelPath = modelPath.substring(0, lastSlash + 1); // include the '/'
        else
            modelPath = "";

        ObjMaterialLibrary mtllib = ObjMaterialLibrary.EMPTY;       // mtl文件的内容，其中包含多个材质Material
        ObjMaterialLibrary.Material currentMat = null;              // 当前使用的材质Material
        String currentSmoothingGroup = null;
        ModelGroup currentGroup = null;                             //
        ModelObject currentObject = null;
        ModelMesh currentMesh = null;

        boolean objAboveGroup = false;
        // 根据mtlOverride读取对应的mtl库
        if (materialLibraryOverrideLocation != null)
        {
            String lib = materialLibraryOverrideLocation;
            if (lib.contains(":"))
                mtllib = Loader.INSTANCE.loadMaterialLibrary(ResourceLocation.parse(lib));
            else
                mtllib = Loader.INSTANCE.loadMaterialLibrary(ResourceLocation.fromNamespaceAndPath(modelDomain, modelPath + lib));
        }

        String[] line;
        while ((line = tokenizer.readAndSplitLine(true)) != null)
        {
            switch (line[0])
            {
                case "mtllib": // 加载obj文件中的mtl库，它的优先级低于json中定义的mtl库
                {
                    if (materialLibraryOverrideLocation != null)
                        break;

                    String lib = line[1];
                    if (lib.contains(":"))
                        mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(ResourceLocation.parse(lib));
                    else mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(ResourceLocation.fromNamespaceAndPath(modelDomain, modelPath + lib));
                    break;
                }

                case "usemtl": // Sets the current material (starts new mesh)
                {
                    String mat = Strings.join(Arrays.copyOfRange(line, 1, line.length), " ");
                    ObjMaterialLibrary.Material newMat = mtllib.getMaterial(mat);
                    if (!Objects.equals(newMat, currentMat))
                    {
                        currentMat = newMat;
                        if (currentMesh != null && currentMesh.mat == null && currentMesh.faces.size() == 0)
                        {
                            currentMesh.mat = currentMat;
                        }
                        else
                        {
                            // Start new mesh
                            currentMesh = null;
                        }
                    }
                    break;
                }

                case "v": // Vertex
                    model.positions.add(parseVector4To3(line));
                    break;
                case "vt": // Vertex texcoord
                    model.texCoords.add(parseVector2(line));
                    break;
                case "vn": // Vertex normal
                    model.normals.add(parseVector3(line));
                    break;
                case "vc": // Vertex color (non-standard)
                    model.colors.add(parseVector4(line));
                    break;

                case "f": // Face
                {
                    if (currentMesh == null)
                    {
                        // 默认mat
                        if (currentMat == null) {
                            currentMat = new ObjMaterialLibrary.Material("Texture");
                            currentMat.diffuseColorMap = "#texture0";
                        }
                        currentMesh = model.new ModelMesh(currentMat, currentSmoothingGroup);
                        if (currentObject != null)
                        {
                            currentObject.meshes.add(currentMesh);
                        }
                        else
                        {
                            if (currentGroup == null)
                            {
                                currentGroup = model.new ModelGroup("");
                                model.parts.put("", currentGroup);
                            }
                            currentGroup.meshes.add(currentMesh);
                        }
                    }

                    int[][] vertices = new int[line.length - 1][];
                    for (int i = 0; i < vertices.length; i++)
                    {
                        String vertexData = line[i + 1];
                        String[] vertexParts = vertexData.split("/");
                        int[] vertex = Arrays.stream(vertexParts).mapToInt(num -> Strings.isNullOrEmpty(num) ? 0 : Integer.parseInt(num)).toArray();
                        if (vertex[0] < 0) vertex[0] = model.positions.size() + vertex[0];
                        else vertex[0]--;
                        if (vertex.length > 1)
                        {
                            if (vertex[1] < 0) vertex[1] = model.texCoords.size() + vertex[1];
                            else vertex[1]--;
                            if (vertex.length > 2)
                            {
                                if (vertex[2] < 0) vertex[2] = model.normals.size() + vertex[2];
                                else vertex[2]--;
                                if (vertex.length > 3)
                                {
                                    if (vertex[3] < 0) vertex[3] = model.colors.size() + vertex[3];
                                    else vertex[3]--;
                                }
                            }
                        }
                        vertices[i] = vertex;
                    }

                    currentMesh.faces.add(vertices);

                    break;
                }

                case "s": // Smoothing group (starts new mesh)
                {
                    String smoothingGroup = "off".equals(line[1]) ? null : line[1];
                    if (!Objects.equals(currentSmoothingGroup, smoothingGroup))
                    {
                        currentSmoothingGroup = smoothingGroup;
                        if (currentMesh != null && currentMesh.smoothingGroup == null && currentMesh.faces.size() == 0)
                        {
                            currentMesh.smoothingGroup = currentSmoothingGroup;
                        }
                        else
                        {
                            // Start new mesh
                            currentMesh = null;
                        }
                    }
                    break;
                }

                case "g":
                {
                    String name = line[1];
                    if (objAboveGroup)
                    {
                        currentObject = model.new ModelObject(currentGroup.name() + "/" + name);
                        currentGroup.parts.put(name, currentObject);
                    }
                    else
                    {
                        currentGroup = model.new ModelGroup(name);
                        model.parts.put(name, currentGroup);
                        currentObject = null;
                    }
                    // Start new mesh
                    currentMesh = null;
                    break;
                }

                case "o":
                {
                    String name = line[1];
                    if (objAboveGroup || currentGroup == null)
                    {
                        objAboveGroup = true;

                        currentGroup = model.new ModelGroup(name);
                        model.parts.put(name, currentGroup);
                        currentObject = null;
                    }
                    else
                    {
                        currentObject = model.new ModelObject(currentGroup.name() + "/" + name);
                        currentGroup.parts.put(name, currentObject);
                    }
                    // Start new mesh
                    currentMesh = null;
                    break;
                }
            }
        }

        return model;
    }

    private static Vector3f parseVector4To3(String[] line)
    {
        Vector4f vec4 = parseVector4(line);
        return new Vector3f(vec4.x() / vec4.w(), vec4.y() / vec4.w(), vec4.z() / vec4.w());
    }

    private static Vec2 parseVector2(String[] line)
    {
        return switch (line.length)
        {
            case 1 -> new Vec2(0, 0);
            case 2 -> new Vec2(Float.parseFloat(line[1]), 0);
            default -> new Vec2(Float.parseFloat(line[1]), Float.parseFloat(line[2]));
        };
    }

    private static Vector3f parseVector3(String[] line)
    {
        return switch (line.length)
        {
            case 1 -> new Vector3f();
            case 2 -> new Vector3f(Float.parseFloat(line[1]), 0, 0);
            case 3 -> new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0);
            default -> new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]));
        };
    }

    static Vector4f parseVector4(String[] line)
    {
        return switch (line.length)
        {
            case 1 -> new Vector4f();
            case 2 -> new Vector4f(Float.parseFloat(line[1]), 0, 0, 1);
            case 3 -> new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0, 1);
            case 4 -> new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]), 1);
            default -> new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]), Float.parseFloat(line[4]));
        };
    }
    /**
     * 继承自IUnbakedGeometry。bake函数将模型转换为BakedModel
     * bake 需要什么：
     *
     * */
    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));

        var renderTypeHint = context.getRenderTypeHint();
        var renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;

        var bakedPartsBuilder = ImmutableMap.<String, BakedModel>builder();
        for (var entry : parts.entrySet())
        {
            var name = entry.getKey();
            if (!context.isComponentVisible(name, true))
                continue;
            var model = entry.getValue();
            model.addQuads(bakedPartsBuilder, context, spriteGetter, modelState);
        }
        var bakedParts = bakedPartsBuilder.build();

        return new Baked(context.isGui3d(), context.useBlockLight(), context.useAmbientOcclusion(), particle, context.getTransforms(), overrides, bakedParts);
    }



    /**
     * 仿自 CompositeModel.Baked
     * */
    public static class Baked implements IDynamicBakedModel
    {
        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final boolean isSideLit;
        private final TextureAtlasSprite particle;
        private final ItemOverrides overrides;
        private final ItemTransforms transforms;
        private final ImmutableMap<String, BakedModel> children;

        public Baked(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, ItemTransforms transforms, ItemOverrides overrides, ImmutableMap<String, BakedModel> children)
        {
            this.children = children;
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.isSideLit = isSideLit;
            this.particle = particle;
            this.overrides = overrides;
            this.transforms = transforms;
        }

        @NotNull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
        {
            List<List<BakedQuad>> quadLists = new ArrayList<>();
            for (Map.Entry<String, BakedModel> entry : children.entrySet())
            {
                if (renderType == null || (state != null && entry.getValue().getRenderTypes(state, rand, data).contains(renderType)))
                {
                    quadLists.add(entry.getValue().getQuads(state, side, rand, CompositeModel.Data.resolve(data, entry.getKey()), renderType));
                }
            }
            return ConcatenatedListView.of(quadLists);
        }

        @Override
        public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData)
        {
            var builder = CompositeModel.Data.builder();
            for (var entry : children.entrySet())
                builder.with(entry.getKey(), entry.getValue().getModelData(level, pos, state, CompositeModel.Data.resolve(modelData, entry.getKey())));
            return modelData.derive().with(CompositeModel.Data.PROPERTY, builder.build()).build();
        }

        @Override
        public boolean useAmbientOcclusion()
        {
            return isAmbientOcclusion;
        }

        @Override
        public boolean isGui3d()
        {
            return isGui3d;
        }

        @Override
        public boolean usesBlockLight()
        {
            return isSideLit;
        }

        /**
         * 和普通物品不同的独立渲染路径
         */
        @Override
        public boolean isCustomRenderer()
        {
            return true;
        }

        @Override
        public TextureAtlasSprite getParticleIcon()
        {
            return particle;
        }

        @Override
        public ItemOverrides getOverrides()
        {
            return overrides;
        }

        @Override
        public ItemTransforms getTransforms()
        {
            return transforms;
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
        {
            var sets = new ArrayList<ChunkRenderTypeSet>();
            for (Map.Entry<String, BakedModel> entry : children.entrySet())
                sets.add(entry.getValue().getRenderTypes(state, rand, CompositeModel.Data.resolve(data, entry.getKey())));
            return ChunkRenderTypeSet.union(sets);
        }

        @Nullable
        public BakedModel getPart(String name)
        {
            return children.get(name);
        }
    }

    public class ModelGroup extends ModelObject {
        final Map<String, ModelObject> parts = Maps.newLinkedHashMap();

        ModelGroup(String name) {
            super(name);
        }

        @Override
        public void addQuads(ImmutableMap.Builder<String, BakedModel> builder, IGeometryBakingContext owner, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform) {
            super.addQuads(builder, owner, spriteGetter, modelTransform);

            parts.values().stream().filter(part -> owner.isComponentVisible(part.name(), true)).forEach(part -> part.addQuads(builder, owner, spriteGetter, modelTransform));
        }
    }

    public class ModelObject {
        public final String name;

        List<ModelMesh> meshes = Lists.newArrayList();

        ModelObject(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }

        public void addQuads(ImmutableMap.Builder<String, BakedModel> builder, IGeometryBakingContext owner, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform)
        {
            TextureAtlasSprite particle = spriteGetter.apply(owner.getMaterial("particle"));
            var renderTypeHint = owner.getRenderTypeHint();
            var renderTypes = renderTypeHint != null ? owner.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;
            IModelBuilder<?> modelBuilder = IModelBuilder.of(owner.useAmbientOcclusion(), owner.useBlockLight(), owner.isGui3d(), owner.getTransforms(), ItemOverrides.EMPTY, particle, renderTypes);
            for (ModelMesh mesh : meshes)
            {
                mesh.addQuads(owner, modelBuilder, spriteGetter, modelTransform);
            }
            BakedModel bakedModel = modelBuilder.build();
            builder.put(name(), bakedModel);
        }
    }

    private class ModelMesh {
        @Nullable
        public ObjMaterialLibrary.Material mat;
        @Nullable
        public String smoothingGroup;
        public final List<int[][]> faces = Lists.newArrayList();

        public ModelMesh(ObjMaterialLibrary.Material currentMat, String currentSmoothingGroup) {
            this.mat = currentMat;
            this.smoothingGroup = currentSmoothingGroup;
        }

        public void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform)
        {
            if (mat == null) return;
            TextureAtlasSprite texture = spriteGetter.apply(UnbakedGeometryHelper.resolveDirtyMaterial(mat.diffuseColorMap, owner));
            int tintIndex = mat.diffuseTintIndex;
            Vector4f colorTint = mat.diffuseColor;

            var rootTransform = owner.getRootTransform();
            var transform = rootTransform.isIdentity() ? modelTransform.getRotation() : modelTransform.getRotation().compose(rootTransform);
            for (int[][] face : faces)
            {
                Pair<BakedQuad, Direction> quad = makeQuad(face, tintIndex, colorTint, mat.ambientColor, texture, transform);
                if (quad.getRight() == null)
                    modelBuilder.addUnculledFace(quad.getLeft());
                else
                    modelBuilder.addCulledFace(quad.getRight(), quad.getLeft());
            }
        }
    }

    private Pair<BakedQuad, Direction> makeQuad(int[][] indices, int tintIndex, Vector4f colorTint, Vector4f ambientColor, TextureAtlasSprite texture, Transformation transform)
    {
        boolean needsNormalRecalculation = false;
        for (int[] ints : indices)
        {
            needsNormalRecalculation |= ints.length < 3;
        }
        Vector3f faceNormal = new Vector3f();
        if (needsNormalRecalculation)
        {
            Vector3f a = positions.get(indices[0][0]);
            Vector3f ab = positions.get(indices[1][0]);
            Vector3f ac = positions.get(indices[2][0]);
            Vector3f abs = new Vector3f(ab);
            abs.sub(a);
            Vector3f acs = new Vector3f(ac);
            acs.sub(a);
            abs.cross(acs);
            // 零向量检查
            if (abs.lengthSquared() > 1e-8f) {
                abs.normalize();
                faceNormal = abs;
            } else {
                faceNormal = new Vector3f(0, 1, 0); // fallback for degenerate face
            }
        }

        // ===== start =====
        var quadBaker = new TriangleQuadBakingVertexConsumer.Buffered();    // 采用另一种转换器

        quadBaker.setSprite(texture);
        quadBaker.setTintIndex(tintIndex);

        int uv2 = 0;
        if (emissiveAmbient)
        {
            int fakeLight = (int) ((ambientColor.x() + ambientColor.y() + ambientColor.z()) * 15 / 3.0f);
            uv2 = LightTexture.pack(fakeLight, fakeLight);
            quadBaker.setShade(fakeLight == 0 && shadeQuads);
        }
        else
        {
            quadBaker.setShade(shadeQuads);
        }

        boolean hasTransform = !transform.isIdentity();
        // The incoming transform is referenced on the center of the block, but our coords are referenced on the corner
        Transformation transformation = hasTransform ? transform.blockCenterToCorner() : transform;

        Vector4f[] pos = new Vector4f[4];
        Vector3f[] norm = new Vector3f[4];

        for (int i = 0; i < 3; i++)     // 循环最多到3
        {
            int[] index = indices[Math.min(i, indices.length - 1)];
            Vector4f position = new Vector4f(positions.get(index[0]), 1);
            Vec2 texCoord = index.length >= 2 && texCoords.size() > 0 ? texCoords.get(index[1]) : DEFAULT_COORDS[i];
            Vector3f norm0 = !needsNormalRecalculation && index.length >= 3 && normals.size() > 0 ? normals.get(index[2]) : faceNormal;
            Vector3f normal = norm0;
            Vector4f color = index.length >= 4 && colors.size() > 0 ? colors.get(index[3]) : COLOR_WHITE;
            if (hasTransform)
            {
                normal = new Vector3f(norm0);
                transformation.transformPosition(position);
                transformation.transformNormal(normal);
            }
            Vector4f tintedColor = new Vector4f(
                    color.x() * colorTint.x(),
                    color.y() * colorTint.y(),
                    color.z() * colorTint.z(),
                    color.w() * colorTint.w());
            quadBaker.vertex(position.x(), position.y(), position.z());
            quadBaker.color(tintedColor.x(), tintedColor.y(), tintedColor.z(), tintedColor.w());
            // 潜在问题：位于 [0,1] 范围之外的 UV 坐标会越界采样地图集
            quadBaker.uv(
                    texture.getU(texCoord.x * 16),
                    texture.getV((flipV ? 1 - texCoord.y : texCoord.y) * 16)
            );
            quadBaker.uv2(uv2);
            quadBaker.normal(normal.x(), normal.y(), normal.z());
            if (i == 0)
            {
                quadBaker.setDirection(Direction.getNearest(normal.x(), normal.y(), normal.z()));
            }
            quadBaker.endVertex();
            pos[i] = position;
            norm[i] = normal;
        }

        Direction cull = null;
        if (automaticCulling)
        {
            if (Mth.equal(pos[0].x(), 0) && // vertex.position.x
                    Mth.equal(pos[1].x(), 0) &&
                    Mth.equal(pos[2].x(), 0) &&
                    Mth.equal(pos[3].x(), 0) &&
                    norm[0].x() < 0) // vertex.normal.x
            {
                cull = Direction.WEST;
            }
            else if (Mth.equal(pos[0].x(), 1) && // vertex.position.x
                    Mth.equal(pos[1].x(), 1) &&
                    Mth.equal(pos[2].x(), 1) &&
                    Mth.equal(pos[3].x(), 1) &&
                    norm[0].x() > 0) // vertex.normal.x
            {
                cull = Direction.EAST;
            }
            else if (Mth.equal(pos[0].z(), 0) && // vertex.position.z
                    Mth.equal(pos[1].z(), 0) &&
                    Mth.equal(pos[2].z(), 0) &&
                    Mth.equal(pos[3].z(), 0) &&
                    norm[0].z() < 0) // vertex.normal.z
            {
                cull = Direction.NORTH; // can never remember
            }
            else if (Mth.equal(pos[0].z(), 1) && // vertex.position.z
                    Mth.equal(pos[1].z(), 1) &&
                    Mth.equal(pos[2].z(), 1) &&
                    Mth.equal(pos[3].z(), 1) &&
                    norm[0].z() > 0) // vertex.normal.z
            {
                cull = Direction.SOUTH;
            }
            else if (Mth.equal(pos[0].y(), 0) && // vertex.position.y
                    Mth.equal(pos[1].y(), 0) &&
                    Mth.equal(pos[2].y(), 0) &&
                    Mth.equal(pos[3].y(), 0) &&
                    norm[0].y() < 0) // vertex.normal.z
            {
                cull = Direction.DOWN; // can never remember
            }
            else if (Mth.equal(pos[0].y(), 1) && // vertex.position.y
                    Mth.equal(pos[1].y(), 1) &&
                    Mth.equal(pos[2].y(), 1) &&
                    Mth.equal(pos[3].y(), 1) &&
                    norm[0].y() > 0) // vertex.normal.y
            {
                cull = Direction.UP;
            }
        }

        return Pair.of(quadBaker.getQuad(), cull);
    }

    private record ModelSettings(@NotNull ResourceLocation modelLocation, boolean automaticCulling, boolean shadeQuads, boolean flipV, boolean emissiveAmbient, @Nullable String mtlOverride)
    { }

    /** 生成三角形边的工具，代码基本复制自QuadBakingVertexConsumer */
    private static class TriangleQuadBakingVertexConsumer implements VertexConsumer
    {
        private final Map<VertexFormatElement, Integer> ELEMENT_OFFSETS = Util.make(new IdentityHashMap<>(), map -> {
            int i = 0;
            for (var element : DefaultVertexFormat.BLOCK.getElements())
                map.put(element, DefaultVertexFormat.BLOCK.getOffset(i++) / 3); // Int offset
        });
        private static final int QUAD_DATA_SIZE = IQuadTransformer.STRIDE *3;

        private final Consumer<BakedQuad> quadConsumer;

        int vertexIndex = 0;
        private int[] quadData = new int[QUAD_DATA_SIZE];

        private int tintIndex;
        private Direction direction = Direction.DOWN;
        private TextureAtlasSprite sprite = UnitTextureAtlasSprite.INSTANCE;
        private boolean shade;
        private boolean hasAmbientOcclusion;

        public TriangleQuadBakingVertexConsumer(Consumer<BakedQuad> quadConsumer)
        {
            this.quadConsumer = quadConsumer;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z)
        {
            int offset = vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
            quadData[offset] = Float.floatToRawIntBits((float) x);
            quadData[offset + 1] = Float.floatToRawIntBits((float) y);
            quadData[offset + 2] = Float.floatToRawIntBits((float) z);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z)
        {
            int offset = vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.NORMAL;
            quadData[offset] = ((int) (x * 127.0f) & 0xFF) |
                    (((int) (y * 127.0f) & 0xFF) << 8) |
                    (((int) (z * 127.0f) & 0xFF) << 16);
            return this;
        }

        @Override
        public VertexConsumer color(int r, int g, int b, int a)
        {
            int offset = vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.COLOR;
            quadData[offset] = ((a & 0xFF) << 24) |
                    ((b & 0xFF) << 16) |
                    ((g & 0xFF) << 8) |
                    (r & 0xFF);
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v)
        {
            int offset = vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
            quadData[offset] = Float.floatToRawIntBits(u);
            quadData[offset + 1] = Float.floatToRawIntBits(v);
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v)
        {
            if (IQuadTransformer.UV1 >= 0) // Vanilla doesn't support this, but it may be added by a 3rd party
            {
                int offset = vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.UV1;
                quadData[offset] = (u & 0xFFFF) | ((v & 0xFFFF) << 16);
            }
            return this;
        }

        @Override
        public VertexConsumer uv2(int u, int v)
        {
            int offset = vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.UV2;
            quadData[offset] = (u & 0xFFFF) | ((v & 0xFFFF) << 16);
            return this;
        }

        @Override
        public VertexConsumer misc(VertexFormatElement element, int... rawData)
        {
            Integer baseOffset = ELEMENT_OFFSETS.get(element);
            if (baseOffset != null)
            {
                int offset = vertexIndex * IQuadTransformer.STRIDE + baseOffset;
                System.arraycopy(rawData, 0, quadData, offset, rawData.length);
            }
            return this;
        }

        @Override
        public void endVertex()
        {
            if (++vertexIndex != 3)     // 终止条件由4改为3
                return;
            // We have a full quad, pass it to the consumer and reset
            quadConsumer.accept(new BakedQuad(quadData, tintIndex, direction, sprite, shade, hasAmbientOcclusion));
            vertexIndex = 0;
            quadData = new int[QUAD_DATA_SIZE];
        }

        @Override
        public void defaultColor(int r, int g, int b, int a)
        {
        }

        @Override
        public void unsetDefaultColor()
        {
        }

        public void setTintIndex(int tintIndex)
        {
            this.tintIndex = tintIndex;
        }

        public void setDirection(Direction direction)
        {
            this.direction = direction;
        }

        public void setSprite(TextureAtlasSprite sprite)
        {
            this.sprite = sprite;
        }

        public void setShade(boolean shade)
        {
            this.shade = shade;
        }

        public void setHasAmbientOcclusion(boolean hasAmbientOcclusion)
        {
            this.hasAmbientOcclusion = hasAmbientOcclusion;
        }

        public static class Buffered extends TriangleQuadBakingVertexConsumer
        {
            private final BakedQuad[] output;

            public Buffered()
            {
                this(new BakedQuad[1]);
            }

            private Buffered(BakedQuad[] output)
            {
                super(q -> output[0] = q);
                this.output = output;
            }

            public BakedQuad getQuad()
            {
                var quad = Preconditions.checkNotNull(output[0], "No quad has been emitted. Vertices in buffer: " + vertexIndex);
                output[0] = null;
                return quad;
            }
        }
    }

    /**
     * 渲染模型
     * */
    private static class RenderTypes extends RenderType {

        // 构造函数保持私有，仅用于继承 RenderType 的内部 Shard 状态
        private RenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
            super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
        }

        // 💡 1. 专门用于渲染三角形 (Mode.TRIANGLES) 的自定义 RenderType
        public static final RenderType TRIANGLE_CUTOUT = create(
                "triangle_cutout", // 自定义名称
                DefaultVertexFormat.BLOCK, // 顶点格式 (包含 Position, Color, UV, Overlay, Light, Normal)
                VertexFormat.Mode.TRIANGLES, // 👈 关键点：设置为三角形绘制模式！
                98304,                      // 32 * 3 * 1024，缓存大小
                false,
                true,
                CompositeState.builder()
                        .setShaderState(RENDERTYPE_SOLID_SHADER) // 使用原版 Block 的 Shader
                        .setTextureState(BLOCK_SHEET)            // 使用原版的方块贴图图集 (Atlas)
                        .setTransparencyState(NO_TRANSPARENCY)
                        .setLightmapState(LIGHTMAP)             // 启用光照贴图
                        .setOverlayState(OVERLAY)               // 启用 Overlay (如受伤红闪)
                        .createCompositeState(true)
        );
    }

    public static void renderCutout(BakedModel model, PoseStack pPose, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay){
        RenderUtils.renderModel(model, pPose, pBuffer, pPackedLight, pPackedOverlay, RenderTypes.TRIANGLE_CUTOUT);
    }

    public static void renderItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, int size){
        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(pStack, Minecraft.getInstance().level, Minecraft.getInstance().player, 42);
        if (model instanceof Baked baked){
            pPoseStack.pushPose();
            pPoseStack.translate(0.5F, 0.5f, 0.5F);
            float baseScale = 1.0f / size;
            float offsetX = size / 1f;
            float offsetY = size / 1f;
            float offsetZ = 0;
            float scale = baseScale;
            switch (pDisplayContext){
                case GUI -> {
                    scale = 0.625f * baseScale;
                    pPoseStack.mulPose(Axis.XP.rotationDegrees(30));
                    pPoseStack.mulPose(Axis.YP.rotationDegrees(135));
                    pPoseStack.translate(offsetX, - offsetY, offsetZ);
                    pPoseStack.scale(scale, scale, scale);
                }case THIRD_PERSON_RIGHT_HAND -> {
                    scale = 0.375f * baseScale;
                    pPoseStack.mulPose(Axis.XP.rotationDegrees(75));
                    pPoseStack.mulPose(Axis.YP.rotationDegrees(45));
                    pPoseStack.translate(offsetX, 2.5f, offsetZ);
                    pPoseStack.scale(scale, scale, scale);
                }case FIRST_PERSON_RIGHT_HAND -> {
                    scale = 0.4f * baseScale;
                    pPoseStack.mulPose(Axis.XP.rotationDegrees(0));
                    pPoseStack.mulPose(Axis.YP.rotationDegrees(45));
                    pPoseStack.translate(offsetX, -offsetY, offsetZ);
                    pPoseStack.scale(scale, scale, scale);
                }case GROUND -> {
                    scale = 0.25f * baseScale;
                    pPoseStack.translate(offsetX, -offsetY, offsetZ);
                    pPoseStack.scale(scale, scale, scale);
                }case FIXED -> {
                    scale = 0.5f * baseScale;
                    pPoseStack.translate(offsetX, -offsetY, offsetZ);
                    pPoseStack.scale(scale, scale, scale);
                }
            }
            renderCutout(baked, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
            pPoseStack.popPose();
        }else { // 如果判别错误就用默认渲染方式
            Minecraft.getInstance().getItemRenderer().renderStatic(pStack, pDisplayContext, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, Minecraft.getInstance().level, 42);
        }
    }
}
