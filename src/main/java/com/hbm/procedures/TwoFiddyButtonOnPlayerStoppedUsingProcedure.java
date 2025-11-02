package net.mcreator.nuclearcraft.procedures;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.init.BigExplosivesModEntities;
import net.mcreator.nuclearcraft.init.BigExplosivesModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/TwoFiddyButtonOnPlayerStoppedUsingProcedure.class */
public class TwoFiddyButtonOnPlayerStoppedUsingProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) throws IOException {
        if (entity == null) {
            return;
        }
        double Scaling2 = 0.0d;
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
                    _level.m_5594_((Player) null, BlockPos.m_274561_(x, y, z), (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:designatorbeep")), SoundSource.NEUTRAL, 1.0f, 1.0f);
                } else {
                    _level.m_7785_(x, y, z, (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:designatorbeep")), SoundSource.NEUTRAL, 1.0f, 1.0f, false);
                }
            }
            if (!world.m_8055_(new BlockPos(entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(0.0d)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123341_(), entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(0.0d)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123342_(), entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(0.0d)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123343_())).m_60815_() && mainjsonobject.get("250kgAccurateBomb").getAsBoolean()) {
                Scaling2 = 0.0d + 250.0d;
                if (world instanceof ServerLevel) {
                    ServerLevel _level2 = (ServerLevel) world;
                    Entity entityToSpawn = ((EntityType) BigExplosivesModEntities.TWO_FIDDY.get()).m_262496_(_level2, BlockPos.m_274561_(entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling2)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123341_(), entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling2)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123342_() + 400, entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling2)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123343_()), MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.m_20334_(0.0d, 0.0d, 0.0d);
                    }
                }
            }
            for (int index0 = 0; index0 < ((int) mainjsonobject.get("250kgRandomBombAmount").getAsDouble()); index0++) {
                if (world instanceof ServerLevel) {
                    ServerLevel _level3 = (ServerLevel) world;
                    Entity entityToSpawn2 = ((EntityType) BigExplosivesModEntities.TWO_FIDDY.get()).m_262496_(_level3, BlockPos.m_274561_(entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling2)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123341_() + Mth.m_216263_(RandomSource.m_216327_(), -20.0d, 20.0d), entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling2)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123342_() + Mth.m_216263_(RandomSource.m_216327_(), 400.0d, 500.0d), entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling2)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123343_() + Mth.m_216263_(RandomSource.m_216327_(), -20.0d, 20.0d)), MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn2 != null) {
                        entityToSpawn2.m_20334_(0.0d, 0.0d, 0.0d);
                    }
                }
            }
            BigExplosivesMod.queueServerWork(1, () -> {
                if (itemstack.m_220157_(1, RandomSource.m_216327_(), (ServerPlayer) null)) {
                    itemstack.m_41774_(1);
                    itemstack.m_41721_(0);
                }
                if (entity instanceof Player) {
                    Player _player = (Player) entity;
                    _player.m_36335_().m_41524_((Item) BigExplosivesModItems.TWO_FIDDY_BUTTON.get(), 250);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
