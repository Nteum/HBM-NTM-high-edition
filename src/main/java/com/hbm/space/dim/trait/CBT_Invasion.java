package com.hbm.space.dim.trait;

import java.util.Random;

import com.hbm.registries.RegistryHelper;
import com.hbm.space.dim.CelestialBody;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Invasion trait — manages alien invasion events on a celestial body.
 * This is a scripted multi-wave event with:
 * - Wave 1: Basic glyphid spawns, 20 kills to advance
 * - Wave 2: Mixed glyphid types + UFOs, 100 kills
 * - Wave 3: Stronger enemies, 150 kills
 * - Wave 4: Boss UFO fight
 *
 * Drop pods deliver glyphid ground troops while siege UFOs attack from above.
 */
public class CBT_Invasion extends CelestialBodyTrait {

    public int wave;
    public int kills;
    public int killreq;
    public double waveTime;
    public boolean isInvading;
    public int lastSpawns;
    public int spawndelay;
    public int podBurstCounter = 0;
    public int podCooldown = 0;
    public boolean bossSpawned = false;
    public boolean warningPlayed;

    private final Random rand = new Random();

    public CBT_Invasion() {}

    public CBT_Invasion(int wave, double waveTime, boolean isInvading) {
        this.wave = wave;
        this.waveTime = waveTime;
        this.isInvading = isInvading;
    }

    public void prepare() {
        if (!isInvading && waveTime >= 0) {
            waveTime--;
            warningPlayed = true;
            if (waveTime <= 5) {
                warningPlayed = false;
            }
            if (waveTime <= 0) {
                isInvading = true;
            }
        }
    }

    @Override
    public void update(boolean isRemote, CelestialBody body) {
        if (!isRemote) {
            prepare();

            if (isInvading) {
                var server = ServerLifecycleHooks.getCurrentServer();
                if (server == null) return;
                ServerLevel world = server.getLevel(body.dimension);
                if (world == null || world.players().isEmpty()) return;

                logicTick(world);
                handleBurstSpawning(world);
                spawnAttempt(world);
            }
        } else {
            if (!isInvading && !warningPlayed) {
                warningPlayed = true;
                // Client-side: play alarm and show warning
                // Requires client proxy — add your own sound/chat handling here:
                // MainRegistry.proxy.playSound("hbm:alarm.ping", 10F, 1F);
                // Minecraft.getInstance().player.displayClientMessage(
                //     Component.literal("Incoming Invasion!").withStyle(ChatFormatting.RED), false);
            }

            if (rand.nextInt(Math.max(1, 5 - wave)) == 0 && isInvading) {
                // Client-side meteor visual:
                // WorldProviderCelestial.Meteor.addMeteor();
            }
        }
    }

    /** Spawn a drop pod with glyphid payload near a random player */
    public void spawnCattle(Level world) {
        if (world.players().isEmpty()) return;
        Player player = world.players().get(world.random.nextInt(world.players().size()));

        if (!(player instanceof ServerPlayer)) return;
        if (player.getY() < 50 && !RegistryHelper.worldIsSuperFlat((ServerLevel) world)) return; // non-flat surface check

        ServerPlayer playerMP = (ServerPlayer) player;

//        Random rand = world.random;
        // EntityCombatDropPod pod = new EntityCombatDropPod(world);
        // pod.setPos(playerMP.getX() + (rand.nextGaussian() * 15), 250, playerMP.getZ() + (rand.nextGaussian() * 15));
        // pod.setDeltaMovement(0, -1.5, 0);
        //
        // EntityGlyphid glyph;
        // int amount = 1;
        // ... (entity creation depends on wave number)
        //
        // NBTTagCompound nbt = new NBTTagCompound();
        // nbt.setString("id", EntityList.getEntityString(glyph));
        // glyph.writeToNBT(nbt);
        //
        // pod.setPayload(nbt, amount, 2);
        // world.addFreshEntity(pod);

        // NOTE: Port the EntityCombatDropPod and EntityGlyphid classes for full functionality.
        // For now, this method is a skeleton.
    }

    private void handleBurstSpawning(Level world) {
        if (wave > 3) return;

        if (podCooldown > 0) {
            podCooldown--;
            return;
        }

        if (world.getGameTime() % 10 + world.random.nextInt(3) == 0) {
            spawnCattle(world);
            podBurstCounter++;
            if (podBurstCounter >= 3 + (wave - 1)) {
                podBurstCounter = 0;
                podCooldown = 500;
            }
        }
    }

    private void logicTick(Level world) {
        if (!isInvading) return;

        switch (wave) {
            case 0: advanceWave(world); break;
            case 1:
                killreq = 20;
                if (kills >= killreq) advanceWave(world);
                break;
            case 2:
                killreq = 100;
                if (kills >= killreq) advanceWave(world);
                break;
            case 3:
                killreq = 150;
                if (kills >= killreq) advanceWave(world);
                break;
            case 4:
                killreq = 1;
                if (!bossSpawned) {
                    spawnBoss(world);
                    bossSpawned = true;
                }
                break;
        }
    }

    private void advanceWave(Level world) {
        wave++;
        kills = 0;
        broadcast(world, Component.literal("Wave " + (wave == 4 ? "FINAL" : wave) + " is starting!")
            .withStyle(ChatFormatting.GOLD));
    }

    public void spawnAttempt(Level world) {
        if (wave > 3) return;
        int timer = 200;
        if (wave == 2) timer = 100;
        if (wave == 3) timer = 80;

        if (world.getGameTime() % timer == 0) {
            if (world.players().isEmpty()) return;
            Player player = world.players().get(world.random.nextInt(world.players().size()));

            if (player.getY() < 50 && !RegistryHelper.worldIsSuperFlat((ServerLevel) world)) return;

            double spawnX = player.getX() + world.random.nextGaussian() * 30;
            double spawnZ = player.getZ() + world.random.nextGaussian() * 30;
            double spawnY = player.getY() + 30 + world.random.nextInt(20);

            // UFO spawn logic — port EntitySiegeCraft and EntitySiegeUFO for full functionality
            // See reference code for the complete spawn logic
            lastSpawns++;
        }
    }

    /**
     * Called when an entity is killed during an invasion.
     * Advances the kill counter and wave progression.
     */
    public void onKill(LivingEntity entity, CelestialBody body) {
        // EntitySiegeUFO kills worth 1-2, EntitySiegeCraft kills worth 10
        // Port the entity type checks when those entities are ported
        //
        // if (entity instanceof EntitySiegeUFO) {
        //     int value = entity.getMaxHealth() >= 100 * 0.25 ? 2 : 1;
        //     kills += value;
        //     body.modifyTraits(this);
        // } else if (entity instanceof EntitySiegeCraft) {
        //     kills += 10;
        //     body.modifyTraits(this);
        // }
        //
        // if (wave >= 4 && entity instanceof EntityUFO) {
        //     // Invasion over — remove trait
        //     HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> currentTraits = body.getTraits();
        //     currentTraits.remove(CBT_Invasion.class);
        //     broadcast(entity.level(), Component.literal("The Invasion Is Over!").withStyle(ChatFormatting.YELLOW));
        //     body.setTraits(currentTraits);
        // }
    }

    private void spawnBoss(Level world) {
        if (world.players().isEmpty()) return;
        Player player = world.players().get(world.random.nextInt(world.players().size()));

        // EntityUFO entity = new EntityUFO(world);
        // entity.scanCooldown = 100;
        // entity.setPos(player.getX(), player.getY() + 50, player.getZ());
        // entity.setYRot(Mth.wrapDegrees(world.random.nextFloat() * 360.0F));
        // entity.yBodyRot = entity.getYRot();
        // entity.yHeadRot = entity.getYRot();
        // entity.finalizeSpawn(world, world.getCurrentDifficultyAt(entity.blockPosition()),
        //     MobSpawnType.EVENT, null, null);
        // world.addFreshEntity(entity);

        // NOTE: Port EntityUFO for full boss functionality.
    }

    private void broadcast(Level world, Component text) {
        for (Player p : world.players()) {
            p.sendSystemMessage(text);
        }
    }

    // --- Boss display info (for boss bar integration) ---

    public float getMaxHealth() { return killreq; }
    public float getHealth() { return killreq - kills; }
    public Component getDisplayName() { return Component.literal("Wave " + wave); }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        nbt.putInt("wave", wave);
        nbt.putInt("kills", kills);
        nbt.putInt("killreq", killreq);
        nbt.putDouble("waveTime", waveTime);
        nbt.putBoolean("isInvading", isInvading);
        nbt.putBoolean("warningPlayed", warningPlayed);
        nbt.putInt("podBurst", podBurstCounter);
        nbt.putInt("podCooldown", podCooldown);
        nbt.putBoolean("bossSpawned", bossSpawned);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        wave = nbt.getInt("wave");
        kills = nbt.getInt("kills");
        killreq = nbt.getInt("killreq");
        waveTime = nbt.getDouble("waveTime");
        isInvading = nbt.getBoolean("isInvading");
        warningPlayed = nbt.getBoolean("warningPlayed");
        podBurstCounter = nbt.getInt("podBurst");
        podCooldown = nbt.getInt("podCooldown");
        bossSpawned = nbt.getBoolean("bossSpawned");
    }

    @Override
    public void writeToBytes(FriendlyByteBuf buf) {
        buf.writeInt(wave);
        buf.writeInt(kills);
        buf.writeInt(killreq);
        buf.writeDouble(waveTime);
        buf.writeBoolean(isInvading);
        buf.writeBoolean(warningPlayed);
        buf.writeInt(podBurstCounter);
        buf.writeInt(podCooldown);
        buf.writeBoolean(bossSpawned);
    }

    @Override
    public void readFromBytes(FriendlyByteBuf buf) {
        wave = buf.readInt();
        kills = buf.readInt();
        killreq = buf.readInt();
        waveTime = buf.readDouble();
        isInvading = buf.readBoolean();
        warningPlayed = buf.readBoolean();
        podBurstCounter = buf.readInt();
        podCooldown = buf.readInt();
        bossSpawned = buf.readBoolean();
    }
}
