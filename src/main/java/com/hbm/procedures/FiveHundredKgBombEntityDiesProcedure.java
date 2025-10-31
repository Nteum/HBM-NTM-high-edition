package net.mcreator.nuclearcraft.procedures;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/FiveHundredKgBombEntityDiesProcedure.class */
public class FiveHundredKgBombEntityDiesProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) throws IOException {
        new File("");
        new JsonObject();
        File bigexplosives = new File(FMLPaths.GAMEDIR.get().toString() + "/config/", File.separator + "bigexplosivesconfig.json");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(bigexplosives));
            StringBuilder jsonstringbuilder = new StringBuilder();
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                } else {
                    jsonstringbuilder.append(line);
                }
            }
            bufferedReader.close();
            JsonObject mainjsonobject = (JsonObject) new Gson().fromJson(jsonstringbuilder.toString(), JsonObject.class);
            if (world instanceof Level) {
                Level _level = (Level) world;
                if (!_level.m_5776_()) {
                    _level.m_5594_((Player) null, BlockPos.m_274561_(x, y, z), (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:big_bomb")), SoundSource.MASTER, 200.0f, 1.0f);
                } else {
                    _level.m_7785_(x, y, z, (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:big_bomb")), SoundSource.MASTER, 200.0f, 1.0f, false);
                }
            }
            for (int index0 = 0; index0 < ((int) mainjsonobject.get("AmountOfExplosions500kgSpawns").getAsDouble()); index0++) {
                if (world instanceof Level) {
                    Level _level2 = (Level) world;
                    if (!_level2.m_5776_()) {
                        _level2.m_254849_((Entity) null, x + Mth.m_216263_(RandomSource.m_216327_(), mainjsonobject.get("WidthOfThe500kgExplosionSpawnRadius").getAsDouble() - (mainjsonobject.get("WidthOfThe500kgExplosionSpawnRadius").getAsDouble() * 2.0d), mainjsonobject.get("WidthOfThe500kgExplosionSpawnRadius").getAsDouble()), y + Mth.m_216263_(RandomSource.m_216327_(), -7.0d, 5.0d), z + Mth.m_216263_(RandomSource.m_216327_(), mainjsonobject.get("WidthOfThe500kgExplosionSpawnRadius").getAsDouble() - (mainjsonobject.get("WidthOfThe500kgExplosionSpawnRadius").getAsDouble() * 2.0d), mainjsonobject.get("WidthOfThe500kgExplosionSpawnRadius").getAsDouble()), 6.0f, Level.ExplosionInteraction.TNT);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
