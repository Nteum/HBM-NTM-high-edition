package com.hbm;

import com.google.errorprone.annotations.Var;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.resource.PathPackResources;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

//我试图通过直接在mod类的构造函数里直接调用这个函数来获得obj模型，然而失败了
//不过类确实获取了，或许只是我暂时没掌握渲染的技巧？
public class Procedure0 {
    private static HBMxx hbm;
    public static CompositeRenderable PRESS_BODY;

    public static InputStream loadAssets(String path){
        return Procedure0.class.getClassLoader().getResourceAsStream(path);
    }
    public static Resource getResource(String parent, String path){
        PathPackResources pSource = new PathPackResources(path, true, Path.of(parent));
        InputStream stream = Procedure0.class.getClassLoader().getResourceAsStream(parent + path);
        return new Resource(pSource,()-> Objects.requireNonNull(stream));
    }
    public static Resource getAssetsResource(String path){
        return getResource(modAssetsPrefix,path);
    }
    public static final String modAssetsPrefix = "assets/"+HBMxx.MODID+"/";
    public static final String modDataPrefix = "data/"+HBMxx.MODID+"/";
    public static final String modModelPrefix = modAssetsPrefix + "models/";
    public static final String modTexturesPrefix = modAssetsPrefix + "textures/";
    public static ObjModel.ModelSettings getDefaultObjModelSetting(String path){
        return new ObjModel.ModelSettings(hbm(path),true,true,true,false,null);
    }
    public static ResourceLocation hbm(String path){return new ResourceLocation(HBMxx.MODID,path);}
    public static void procedure0(HBMxx hbmxx) throws IOException {
        PRESS_BODY = Procedure0_0.CustomerObjModel.parse("models/block/press_body")
                .bakeRenderable(StandaloneGeometryBakingContext.create(hbm("block/press_body"), Map.of("#texture0", hbm("model/press_body"))));
//        ResourceLocation objLoc = hbm("models/block/press_body.obj");
//        ObjModel objModel = ObjLoader.INSTANCE.loadModel(getDefaultObjModelSetting("models/block/press_body.obj"));
//        PRESS_BODY = objModel.bakeRenderable(StandaloneGeometryBakingContext.create(objLoc, Map.of("#texture0", hbm("model/press_base"))));
//        try(InputStream inputStream = hbm.getClass().getClassLoader().getResourceAsStream("assets/hbmxx/data/something.txt")) {
//            assert inputStream != null;
//            Scanner scan = new Scanner(inputStream);
//            String s = scan.nextLine();
//            HBMxx.LOGGER.info(s);
//        } catch (IOException e) {
//            HBMxx.LOGGER.info("load file something.txt fail");
//            throw new RuntimeException(e);
//        }
    }
}
