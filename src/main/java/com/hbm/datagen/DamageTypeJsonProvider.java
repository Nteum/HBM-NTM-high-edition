package com.hbm.datagen;

import com.google.gson.JsonObject;
import com.hbm.registries.ModDmgSrc;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DamageTypeJsonProvider implements DataProvider {
    private final PackOutput output;
    private final String modid;

    public DamageTypeJsonProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        List<DamageType> damageTypes = new ArrayList<>();

//        damageTypes.add(new DamageType(ModDmgSrc.nuclearBlast.location().getPath(), DamageScaling.ALWAYS, 0.1F));

//        damageTypes.put("nuclearBlast", new DamageType("nuclearBlast", ));

        return CompletableFuture.allOf(damageTypes.stream().map(damageType -> {
            JsonObject json = new JsonObject();
            json.addProperty("exhaustion", damageType.exhaustion());
            json.addProperty("message_id", damageType.msgId());
            json.addProperty("scaling", damageType.scaling().getSerializedName());
            if (damageType.effects() != DamageEffects.HURT)
                json.addProperty("effects", damageType.effects().getSerializedName());  // 空效果
            if (damageType.deathMessageType() != DeathMessageType.DEFAULT)
                json.addProperty("death_message_type", damageType.deathMessageType().getSerializedName());
            Path path = output.getOutputFolder()
                    .resolve("data/" + modid + "/damage_type/" + damageType.msgId() + ".json");
            return DataProvider.saveStable(pOutput, json, path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Damage Type JSONs";
    }
}
