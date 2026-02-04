package com.hbm.render.model.engine;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mojang.math.Transformation;
import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.LightTexture;
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
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.client.model.obj.ObjTokenizer;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.minecraftforge.common.util.ConcatenatedListView;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
/**
 * forge 的 OjbLoader的问题：
 * 1. 默认的流程生成SimpleBakedModel，这种模型取消了obj中模型分体的细节，无法被分块渲染
 * 2. 特殊流程生成CompositeRenderable，可以分体渲染，用于实体，但是所有mesh都只能套一个贴图，无法处理多个贴图的物品。
 * 3. 有读mtl文件的能力，但只能从mtl中读入贴图位置，mtl的其他功能没有被使用
 * */
public class CustomPartsModel implements IUnbakedGeometry<CustomPartsModel> {
    private static final Vector4f COLOR_WHITE = new Vector4f(1, 1, 1, 1);
    private static final Vec2[] DEFAULT_COORDS = {
            new Vec2(0, 0),
            new Vec2(0, 1),
            new Vec2(1, 1),
            new Vec2(1, 0),
    };

    private final Map<String, ModelGroup> parts = Maps.newLinkedHashMap();
    private final Set<String> rootComponentNames = Collections.unmodifiableSet(parts.keySet());
    private Set<String> allComponentNames;

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

    private CustomPartsModel(ModelSettings settings)
    {
        this.modelLocation = settings.modelLocation;
        this.automaticCulling = settings.automaticCulling;
        this.shadeQuads = settings.shadeQuads;
        this.flipV = settings.flipV;
        this.emissiveAmbient = settings.emissiveAmbient;
        this.mtlOverride = settings.mtlOverride;
    }
    public static CustomPartsModel parse(ObjTokenizer tokenizer, ModelSettings settings) throws IOException
    {
        var model = new CustomPartsModel(settings);                 // 待生成的模型
        var modelLocation = settings.modelLocation;                 // 模型的位置
        var materialLibraryOverrideLocation = settings.mtlOverride; // 模型mtl的路径

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
                mtllib = Loader.INSTANCE.loadMaterialLibrary(new ResourceLocation(lib));
            else
                mtllib = Loader.INSTANCE.loadMaterialLibrary(new ResourceLocation(modelDomain, modelPath + lib));
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
                        mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(lib));
                    else
                        mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(modelDomain, modelPath + lib));
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
//            bakedPartsBuilder.put(name, model.bake(bakedPartsBuilder));
            model.addQuads(bakedPartsBuilder, context, spriteGetter, modelState);
        }
        var bakedParts = bakedPartsBuilder.build();

        return new Baked(context.isGui3d(), context.useBlockLight(), context.useAmbientOcclusion(), particle, context.getTransforms(), overrides, bakedParts);
    }
    /**
     * 生成可用于实体的模型
     * */
    public Model bakeModel(){
        return null;
    }

    public static class Loader implements IGeometryLoader<CustomPartsModel>, ResourceManagerReloadListener {
        public static final Loader INSTANCE = new Loader();
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        private static final Logger LOGGER = LogManager.getLogger("HBM-CustomModelLoader");

        private ResourceManager manager = Minecraft.getInstance().getResourceManager();

        private final Map<ModelSettings, CustomPartsModel> modelCache = Maps.newConcurrentMap();
        private final Map<ResourceLocation, ObjMaterialLibrary> materialCache = Maps.newConcurrentMap();
        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            modelCache.clear();
            materialCache.clear();
            manager = resourceManager;
        }

        @Override
        public CustomPartsModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
            if (!jsonObject.has("model"))
                throw new JsonParseException("OBJ Loader requires a 'model' key that points to a valid .OBJ model.");

            String modelLocation = jsonObject.get("model").getAsString();

            boolean automaticCulling = GsonHelper.getAsBoolean(jsonObject, "automatic_culling", true);
            boolean shadeQuads = GsonHelper.getAsBoolean(jsonObject, "shade_quads", true);
            boolean flipV = GsonHelper.getAsBoolean(jsonObject, "flip_v", false);
            boolean emissiveAmbient = GsonHelper.getAsBoolean(jsonObject, "emissive_ambient", true);
            String mtlOverride = GsonHelper.getAsString(jsonObject, "mtl_override", null);

            return loadModel(new ModelSettings(new ResourceLocation(modelLocation), automaticCulling, shadeQuads, flipV, emissiveAmbient, mtlOverride));
        }

        public CustomPartsModel loadModel(ModelSettings settings)
        {
            return modelCache.computeIfAbsent(settings, (data) -> {
                Resource resource = manager.getResource(settings.modelLocation()).orElseThrow();
                try (ObjTokenizer tokenizer = new ObjTokenizer(resource.open()))
                {
                    return CustomPartsModel.parse(tokenizer, settings);
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
                    e.printStackTrace();
                    throw new RuntimeException("Could not read OBJ material library", e);
                } catch (Exception e){
                    e.printStackTrace();
                    throw new RuntimeException("Exception unknown", e);
                }
            });
        }
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

        @Override
        public boolean isCustomRenderer()
        {
            return false;
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
            if (mat == null)
                return;
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
            abs.normalize();
            faceNormal = abs;
        }

        var quadBaker = new QuadBakingVertexConsumer.Buffered();

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

        for (int i = 0; i < 4; i++)
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

    public record ModelSettings(@NotNull ResourceLocation modelLocation, boolean automaticCulling, boolean shadeQuads, boolean flipV, boolean emissiveAmbient, @Nullable String mtlOverride)
    { }
}
