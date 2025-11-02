package com.hbm.datagen;

import com.google.gson.*;
import com.hbm.HBM;
import com.hbm.particle.ModParticleTypes;
import com.hbm.registries.HBMDamage;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.VisibleForTesting;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
/**
 * 用于生成一些模组自定义的json文件
 * */
public class HBMJsonProvider implements DataProvider {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    protected final PackOutput output;
    protected final String modid;
    public final boolean isClient;
    @VisibleForTesting
    public final Map<ResourceLocation, JsonElement> dataToGenerate = new HashMap<>();
    @VisibleForTesting
    public final ExistingFileHelper existingFileHelper;
    // 常用地址
    public final ResourceLocation particleRl = HBM.rl("particles/");
    public final ResourceLocation damageTypeRl = HBM.rl("damage_type/");

    public HBMJsonProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper, boolean isClient){
        this.output = output;
        this.modid = modid;
        this.existingFileHelper = existingFileHelper;
        this.isClient = isClient;
    }

    private void registerData(){
        particleFile();
        damageType();
    }

    private void particleFile(){
        ModParticleTypes.generateJson(this);
    }

    public void simpleParticle(String name){
        ResourceLocation rl = particleRl.withSuffix(name);
        JsonObject root = new JsonObject();
        JsonArray texture = new JsonArray();
        texture.add(HBM.rl(name).toString());
        root.add("textures", texture);

        this.dataToGenerate.put(rl, root);
    }

    public void simpleParticle2Name(String name, String tex){
        ResourceLocation rl = particleRl.withSuffix(name);
        JsonObject root = new JsonObject();
        JsonArray texture = new JsonArray();
        texture.add(HBM.rl(tex).toString());
        root.add("textures", texture);

        this.dataToGenerate.put(rl, root);
    }

    private void damageType(){
        HBMDamage.damageTypeJson(this);
    }

    public void damageType(DamageType damageType){
        ResourceLocation rl = damageTypeRl.withSuffix(damageType.msgId());

        JsonObject json = new JsonObject();
        json.addProperty("exhaustion", damageType.exhaustion());
        json.addProperty("message_id", damageType.msgId());
        json.addProperty("scaling", damageType.scaling().getSerializedName());
        if (damageType.effects() != DamageEffects.HURT)
            json.addProperty("effects", damageType.effects().getSerializedName());  // 空效果
        if (damageType.deathMessageType() != DeathMessageType.DEFAULT)
            json.addProperty("death_message_type", damageType.deathMessageType().getSerializedName());

        this.dataToGenerate.put(rl, json);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        // 输入注册数据
        registerData();
        // 输出生成文件
        CompletableFuture<?>[] futures = new CompletableFuture<?>[this.dataToGenerate.size()];
        int i = 0;

        for (Map.Entry<ResourceLocation, JsonElement> entry : this.dataToGenerate.entrySet()) {
            Path target = getPath(entry.getKey());
            futures[i++] = DataProvider.saveStable(pOutput, entry.getValue(), target);
        }

        return CompletableFuture.allOf(futures);
    }

    @Override
    public String getName() {
        return isClient ? "hbm_json" : "hbm_json_server";
    }

    protected Path getPath(ResourceLocation loc) {
        return this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(loc.getNamespace()).resolve(loc.getPath() + ".json");
    }
}
