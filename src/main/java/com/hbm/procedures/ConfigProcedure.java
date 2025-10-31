package net.mcreator.nuclearcraft.procedures;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/ConfigProcedure.class */
public class ConfigProcedure {
    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) throws IOException {
        execute();
    }

    public static void execute() throws IOException {
        execute(null);
    }

    private static void execute(@Nullable Event event) throws IOException {
        new File("");
        JsonObject mainjsonobject = new JsonObject();
        File bigexplosives = new File(FMLPaths.GAMEDIR.get().toString() + "/config/", File.separator + "bigexplosivesconfig.json");
        if (!bigexplosives.exists()) {
            try {
                bigexplosives.getParentFile().mkdirs();
                bigexplosives.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            mainjsonobject.addProperty("#Test", 0);
            mainjsonobject.addProperty("250kgRandomBombAmount", 2);
            mainjsonobject.addProperty("250kgAccurateBomb", true);
            mainjsonobject.addProperty("10KgBarrageAmount", 12);
            mainjsonobject.addProperty("NapalmBarrageAmount", 16);
            mainjsonobject.addProperty("BunkerBusterTicks", 190);
            mainjsonobject.addProperty("AtomicBombDamage", 120);
            mainjsonobject.addProperty("AmountOfExplosions500kgSpawns", 30);
            mainjsonobject.addProperty("WidthOfThe500kgExplosionSpawnRadius", 13);
            mainjsonobject.addProperty("4thOfJulyBombAmount", 8);
            Gson mainGSONBuilderVariable = new GsonBuilder().setPrettyPrinting().create();
            try {
                FileWriter fileWriter = new FileWriter(bigexplosives);
                fileWriter.write(mainGSONBuilderVariable.toJson(mainjsonobject));
                fileWriter.close();
            } catch (IOException exception2) {
                exception2.printStackTrace();
            }
        }
    }
}
