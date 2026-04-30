package com.hbm.explosion;

import com.hbm.config.ConfigBomb;
import com.hbm.utils.ConcurrentBitSet;
import com.hbm.utils.SubChunkKey;
import com.hbm.utils.SubChunkSnapshot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

import static com.hbm.HBM.LOGGER;

/**
 * Fast raytracing explosion for mk5
 *
 * @author mlbv
 */
public class ExplosionNukeRayParallelized implements IExplosionRay {

    private static final float NUKE_RESISTANCE_CUTOFF = 2_000_000F;
    private static final float INITIAL_ENERGY_FACTOR = 0.3F; // Scales the initial energy of the explosion, affects crater size
    private static final double RESOLUTION_FACTOR = 1.0; // Scales ray density, affects performance and fidelity
    private static final int BLOCK_TIME_CHECK_INTERVAL = 256;
    private static final int CLIENT_CHUNK_UPDATES_PER_TICK = 8;

    private final int minY;
    private final int worldHeight;
    private final int bitsetSize;
    private final int sectionsCount;

    private final ServerLevel level;
    private final int originX, originY, originZ;
    private final int strength;
    private final int radius;

    private final ConcurrentMap<ChunkPos, ConcurrentBitSet> destructionMap;
    private final ConcurrentMap<ChunkPos, ConcurrentMap<Integer, DoubleAdder>> damageMap;
    private final ConcurrentMap<SubChunkKey, SubChunkSnapshot> snapshots;
    private final ConcurrentMap<SubChunkKey, ConcurrentLinkedQueue<RayTask>> waitingRoom;
    private final BlockingQueue<RayTask> rayQueue;
    private final ExecutorService pool;
    private final CountDownLatch latch;
    private final Thread latchWatcherThread;
    private final List<ChunkPos> orderedChunks;
    private final Map<ChunkPos, Integer> destructionBitCursors;
    private final Map<ChunkPos, Set<Integer>> changedChunkSections;
    private final Map<ChunkPos, Set<Integer>> pendingClientChunkSections;
    private final int rayCount;
    private final AtomicInteger nextRayIndex;
    private final BlockingQueue<SubChunkKey> highPriorityReactiveQueue;
    private final Iterator<SubChunkKey> lowPriorityProactiveIterator;
    private int orderedChunkCursor = 0;
    private volatile boolean collectFinished = false;
    private volatile boolean consolidationFinished = false;
    private volatile boolean destroyFinished = false;

    public ExplosionNukeRayParallelized(ServerLevel level, BlockPos origin, int strength, int radius) {
        this.level = level;
        this.originX = origin.getX();
        this.originY = origin.getY();
        this.originZ = origin.getZ();
        this.strength = strength;
        this.radius = radius;

        this.minY = level.getMinBuildHeight();
        this.worldHeight = level.getHeight();
        this.bitsetSize = 16 * this.worldHeight * 16;
        this.sectionsCount = level.getSectionsCount();

        this.rayCount = Math.max(0, (int) (2.5 * Math.PI * strength * strength * RESOLUTION_FACTOR));
        this.nextRayIndex = new AtomicInteger();
        this.latch = new CountDownLatch(this.rayCount);
        List<SubChunkKey> sortedSubChunks = getAllSubChunks();
        this.lowPriorityProactiveIterator = sortedSubChunks.iterator();
        this.highPriorityReactiveQueue = new LinkedBlockingQueue<>();

        int initialChunkCapacity = (int) sortedSubChunks.stream().map(SubChunkKey::getPos).distinct().count();
        this.destructionMap = new ConcurrentHashMap<>(initialChunkCapacity);
        this.damageMap = new ConcurrentHashMap<>(initialChunkCapacity);

        int subChunkCount = sortedSubChunks.size();
        this.snapshots = new ConcurrentHashMap<>(subChunkCount);
        this.waitingRoom = new ConcurrentHashMap<>(subChunkCount);
        this.orderedChunks = new ArrayList<>();
        this.destructionBitCursors = new HashMap<>(initialChunkCapacity);
        this.changedChunkSections = new HashMap<>(initialChunkCapacity);
        this.pendingClientChunkSections = new HashMap<>(initialChunkCapacity);

        this.rayQueue = new LinkedBlockingQueue<>();

        int workers = Math.max(1, Math.min(6, Runtime.getRuntime().availableProcessors() - 2));
        this.pool = Executors.newWorkStealingPool(workers);

        for (int i = 0; i < workers; i++) pool.submit(new Worker());

        this.latchWatcherThread = new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                collectFinished = true;
                if (ConfigBomb.explosionAlgorithm == 2) pool.submit(this::runConsolidation);
                else consolidationFinished = true;
            }
        }, "ExplosionNuke-LatchWatcher-" + System.nanoTime());
        this.latchWatcherThread.setDaemon(true);
        this.latchWatcherThread.start();
    }

    @SuppressWarnings("deprecation")
    private static float getNukeResistance(Block b) {
        if (b.defaultBlockState().liquid()) return 0.1F;
        if (b == Blocks.SANDSTONE) return Blocks.STONE.getExplosionResistance();
        if (b == Blocks.OBSIDIAN) return Blocks.STONE.getExplosionResistance() * 3.0F;
        return b.getExplosionResistance();
    }

    private List<SubChunkKey> getAllSubChunks() {
        List<SubChunkKey> keys = new ArrayList<>();
        int cr = (radius + 15) >> 4;
        int originCX = SectionPos.blockToSectionCoord(originX);
        int originCZ = SectionPos.blockToSectionCoord(originZ);
        int minCX = originCX - cr;
        int maxCX = originCX + cr;
        int minCZ = originCZ - cr;
        int maxCZ = originCZ + cr;

        int minExplosionY = Math.max(this.minY, originY - radius);
        int maxExplosionY = Math.min(this.minY + this.worldHeight - 1, originY + radius);
        int minSectionY = level.getSectionIndex(minExplosionY);
        int maxSectionY = level.getSectionIndex(maxExplosionY);
        int originSectionY = level.getSectionIndex(originY);

        for (int cx = minCX; cx <= maxCX; cx++) {
            for (int cz = minCZ; cz <= maxCZ; cz++) {
                for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
                    int sectionYBlock = level.getSectionYFromSectionIndex(sectionY) * 16;
                    int chunkCenterX = (cx << 4) + 8;
                    int chunkCenterY = sectionYBlock + 8;
                    int chunkCenterZ = (cz << 4) + 8;
                    double dx = chunkCenterX - originX;
                    double dy = chunkCenterY - originY;
                    double dz = chunkCenterZ - originZ;
                    if (dx * dx + dy * dy + dz * dz <= (radius + 14) * (radius + 14)) {
                        keys.add(new SubChunkKey(cx, cz, sectionY));
                    }
                }
            }
        }
        keys.sort(Comparator.comparingInt(key -> {
            int distCX = key.getChunkX() - originCX;
            int distCZ = key.getChunkZ() - originCZ;
            int distSubY = key.getSectionY() - originSectionY;
            return distCX * distCX + distCZ * distCZ + distSubY * distSubY;
        }));
        return keys;
    }

    @Override
    public void cacheChunksTick(int timeBudgetMs) {
        if (collectFinished) return;
        final long deadline = System.nanoTime() + (timeBudgetMs * 1_000_000L);
        while (System.nanoTime() < deadline) {
            SubChunkKey ck = highPriorityReactiveQueue.poll();
            if (ck == null) break;
            processCacheKey(ck);
        }
        while (System.nanoTime() < deadline && lowPriorityProactiveIterator.hasNext()) {
            SubChunkKey ck = lowPriorityProactiveIterator.next();
            processCacheKey(ck);
        }
    }

    private void processCacheKey(SubChunkKey ck) {
        if (snapshots.containsKey(ck)) return;
        snapshots.put(ck, SubChunkSnapshot.getSnapshot(level, ck, ConfigBomb.chunkloading));
        ConcurrentLinkedQueue<RayTask> waiters = waitingRoom.remove(ck);
        if (waiters != null) rayQueue.addAll(waiters);
    }

    @Override
    public void destructionTick(int timeBudgetMs) {
        if (!collectFinished || !consolidationFinished || destroyFinished) return;

        final long deadline = System.nanoTime() + timeBudgetMs * 1_000_000L;
        int remainingBlocks = Math.max(1, ConfigBomb.blastSpeed);
        int blocksSinceTimeCheck = 0;

        if (orderedChunks.isEmpty() && !destructionMap.isEmpty()) {
            orderedChunks.addAll(destructionMap.keySet());
            int originCX = SectionPos.blockToSectionCoord(originX);
            int originCZ = SectionPos.blockToSectionCoord(originZ);
            orderedChunks.sort(Comparator.comparingInt(c -> Math.abs(originCX - c.x) + Math.abs(originCZ - c.z)));
        }

        while (orderedChunkCursor < orderedChunks.size() && remainingBlocks > 0) {
            ChunkPos cp = orderedChunks.get(orderedChunkCursor);
            ConcurrentBitSet bs = destructionMap.get(cp);
            if (bs == null) {
                orderedChunkCursor++;
                continue;
            }

            LevelChunk chunk = level.getChunk(cp.x, cp.z);
            LevelChunkSection[] sections = chunk.getSections();
            boolean chunkModified = false;

            int bitIndex = bs.nextSetBit(destructionBitCursors.getOrDefault(cp, 0));
            while (bitIndex >= 0 && remainingBlocks > 0) {
                if ((blocksSinceTimeCheck++ & (BLOCK_TIME_CHECK_INTERVAL - 1)) == 0 && System.nanoTime() >= deadline) {
                    destructionBitCursors.put(cp, bitIndex);
                    if (chunkModified) chunk.setUnsaved(true);
                    flushClientChunkUpdates(CLIENT_CHUNK_UPDATES_PER_TICK);
                    return;
                }

                int xLocal = bitIndex & 0xF;
                int zLocal = (bitIndex >> 4) & 0xF;
                int yNorm = bitIndex >> 8;
                int yGlobal = yNorm + this.minY;
                int nextBitIndex = bs.nextSetBit(bitIndex + 1);

                int sectionY = level.getSectionIndex(yGlobal);
                if (sectionY >= 0 && sectionY < sections.length) {
                    LevelChunkSection section = sections[sectionY];
                    if (section != null) {
                        int yLocal = SectionPos.sectionRelative(yGlobal);

                        if (!section.getBlockState(xLocal, yLocal, zLocal).isAir()) {
                            BlockPos pos = new BlockPos((cp.x << 4) | xLocal, yGlobal, (cp.z << 4) | zLocal);

                            if (level.getBlockEntity(pos) != null) level.removeBlockEntity(pos);
                            section.setBlockState(xLocal, yLocal, zLocal, Blocks.AIR.defaultBlockState(), false);
                            level.getLightEngine().checkBlock(pos);
                            level.getChunkSource().blockChanged(pos);
                            trackDirtySection(cp, sectionY);
                            chunkModified = true;
                        }
                    }
                }
                bs.clear(bitIndex);
                remainingBlocks--;
                bitIndex = nextBitIndex;
            }

            if (chunkModified) {
                chunk.setUnsaved(true);
            }
            if (bitIndex >= 0) {
                destructionBitCursors.put(cp, bitIndex);
                break;
            } else {
                destructionMap.remove(cp);
                for (int sectionY = 0; sectionY < this.sectionsCount; sectionY++) {
                    snapshots.remove(new SubChunkKey(cp, sectionY));
                }
                destructionBitCursors.remove(cp);
                markChunkReadyForClientUpdate(cp);
                orderedChunkCursor++;
            }
        }

        flushClientChunkUpdates(CLIENT_CHUNK_UPDATES_PER_TICK);

        if (orderedChunkCursor >= orderedChunks.size()
                && destructionMap.isEmpty()
                && changedChunkSections.isEmpty()
                && pendingClientChunkSections.isEmpty()) {
            destroyFinished = true;
            if (pool != null) pool.shutdown();
        }
    }

    private void trackDirtySection(ChunkPos chunkPos, int sectionIndex) {
        changedChunkSections.computeIfAbsent(chunkPos, ignored -> new HashSet<>()).add(sectionIndex);
    }

    private void markChunkReadyForClientUpdate(ChunkPos chunkPos) {
        Set<Integer> dirtySections = changedChunkSections.remove(chunkPos);
        if (dirtySections != null && !dirtySections.isEmpty()) {
            pendingClientChunkSections.put(chunkPos, dirtySections);
        }
    }

    private void flushClientChunkUpdates(int maxChunks) {
        if (pendingClientChunkSections.isEmpty() || maxChunks <= 0) {
            return;
        }

        Iterator<Map.Entry<ChunkPos, Set<Integer>>> iterator = pendingClientChunkSections.entrySet().iterator();
        int sentChunks = 0;

        while (iterator.hasNext() && sentChunks < maxChunks) {
            Map.Entry<ChunkPos, Set<Integer>> entry = iterator.next();
            ChunkPos chunkPos = entry.getKey();
            LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
            Heightmap.primeHeightmaps(chunk, EnumSet.of(
                    Heightmap.Types.MOTION_BLOCKING,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Heightmap.Types.OCEAN_FLOOR,
                    Heightmap.Types.WORLD_SURFACE
            ));
            chunk.initializeLightSources();
            level.getChunkSource().getLightEngine().lightChunk(chunk, false);

            for (int sectionIndex : entry.getValue()) {
                int sectionY = level.getSectionYFromSectionIndex(sectionIndex);
                SectionPos sectionPos = SectionPos.of(chunkPos, sectionY);
                level.getChunkSource().onLightUpdate(LightLayer.SKY, sectionPos);
                level.getChunkSource().onLightUpdate(LightLayer.BLOCK, sectionPos);
            }

            iterator.remove();
            sentChunks++;
        }
    }

    @Override
    public boolean isComplete() {
        return collectFinished && consolidationFinished && destroyFinished;
    }

    @Override
    public void cancel() {
        this.collectFinished = true;
        this.consolidationFinished = true;
        this.destroyFinished = true;

        if (this.rayQueue != null) this.rayQueue.clear();
        if (this.waitingRoom != null) this.waitingRoom.clear();

        if (this.latch != null) while (this.latch.getCount() > 0) this.latch.countDown();
        if (this.latchWatcherThread != null && this.latchWatcherThread.isAlive()) this.latchWatcherThread.interrupt();

        if (this.pool != null && !this.pool.isShutdown()) {
            this.pool.shutdownNow();
            try {
                if (!this.pool.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                    LOGGER.error("ExplosionNukeRayParallelized thread pool did not terminate promptly on cancel.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (!this.pool.isShutdown()) this.pool.shutdownNow();
            }
        }
        if (this.destructionMap != null) this.destructionMap.clear();
        if (this.damageMap != null) this.damageMap.clear();
        if (this.snapshots != null) this.snapshots.clear();
        if (this.orderedChunks != null) this.orderedChunks.clear();
        if (this.destructionBitCursors != null) this.destructionBitCursors.clear();
        if (this.changedChunkSections != null) this.changedChunkSections.clear();
        if (this.pendingClientChunkSections != null) this.pendingClientChunkSections.clear();
    }

    private Vec3 generateSphereRay(int index, int count) {
        if (count <= 1) {
            return new Vec3(1, 0, 0);
        }
        double phi = Math.PI * (3.0 - Math.sqrt(5.0));
        double y = 1.0 - (index / (double) (count - 1)) * 2.0;
        double r = Math.sqrt(1.0 - y * y);
        double t = phi * index;
        return new Vec3(Math.cos(t) * r, y, Math.sin(t) * r).normalize();
    }

    private void runConsolidation() {
        damageMap.forEach((cp, innerDamageMap) -> {
            if (innerDamageMap.isEmpty()) {
                damageMap.remove(cp);
                return;
            }
            ConcurrentBitSet chunkDestructionBitSet = destructionMap.computeIfAbsent(cp, k -> new ConcurrentBitSet(this.bitsetSize));
            innerDamageMap.forEach((bitIndex, accumulatedDamageAdder) -> {
                float accumulatedDamage = (float) accumulatedDamageAdder.sum();
                if (accumulatedDamage <= 0.0f) {
                    innerDamageMap.remove(bitIndex);
                    return;
                }
                int yNorm = bitIndex >> 8;
                int yGlobal = yNorm + this.minY;
                int sectionY = level.getSectionIndex(yGlobal);
                if (sectionY < 0 || sectionY >= this.sectionsCount) {
                    innerDamageMap.remove(bitIndex);
                    return;
                }

                SubChunkKey snapshotKey = new SubChunkKey(cp, sectionY);
                SubChunkSnapshot snap = snapshots.get(snapshotKey);
                if (snap == null || snap == SubChunkSnapshot.EMPTY) {
                    innerDamageMap.remove(bitIndex);
                    return;
                }

                int xLocal = bitIndex & 0xF;
                int zLocal = (bitIndex >> 4) & 0xF;
                Block originalBlock = snap.getBlock(xLocal, SectionPos.sectionRelative(yGlobal), zLocal);
                if (originalBlock == Blocks.AIR) {
                    innerDamageMap.remove(bitIndex);
                    return;
                }

                float resistance = getNukeResistance(originalBlock);
                if (accumulatedDamage >= resistance * RESOLUTION_FACTOR) {
                    chunkDestructionBitSet.set(bitIndex);
                }
                innerDamageMap.remove(bitIndex);
            });
            if (innerDamageMap.isEmpty()) damageMap.remove(cp);
        });
        damageMap.clear();
        consolidationFinished = true;
    }

    private class Worker implements Runnable {
        @Override
        public void run() {
            try {
                while (!collectFinished && !Thread.currentThread().isInterrupted()) {
                    RayTask task = rayQueue.poll();
                    if (task == null) {
                        int nextIndex = nextRayIndex.getAndIncrement();
                        if (nextIndex < rayCount) {
                            task = new RayTask(nextIndex);
                        } else {
                            task = rayQueue.poll(100, TimeUnit.MILLISECONDS);
                        }
                    }
                    if (task != null) task.trace();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private class RayTask {
        private static final double RAY_DIRECTION_EPSILON = 1e-6;
        private static final double PROCESSING_EPSILON = 1e-9;
        private static final float MIN_EFFECTIVE_DIST_FOR_ENERGY_CALC = 0.01f;

        final int dirIndex;
        int x, y, z;
        float energy;
        double tMaxX, tMaxY, tMaxZ, tDeltaX, tDeltaY, tDeltaZ;
        double dirX, dirY, dirZ;
        int stepX, stepY, stepZ;
        boolean initialised = false;
        double currentRayPosition;

        private int lastCX = Integer.MIN_VALUE, lastCZ = Integer.MIN_VALUE, lastSectionY = Integer.MIN_VALUE;
        private SubChunkKey currentSubChunkKey = null;

        RayTask(int dirIdx) {
            this.dirIndex = dirIdx;
        }

        void init() {
            Vec3 dir = generateSphereRay(this.dirIndex, rayCount);
            this.energy = strength * INITIAL_ENERGY_FACTOR;
            this.x = originX;
            this.y = originY;
            this.z = originZ;

            this.currentRayPosition = 0.0;

            this.dirX = dir.x();
            this.dirY = dir.y();
            this.dirZ = dir.z();

            this.stepX = (Math.abs(dirX) < RAY_DIRECTION_EPSILON) ? 0 : (dirX > 0 ? 1 : -1);
            this.tDeltaX = (stepX == 0) ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(dirX);
            this.tMaxX = (stepX == 0) ? Double.POSITIVE_INFINITY : tDeltaX * (stepX > 0 ? 1 : 0);

            this.stepY = (Math.abs(dirY) < RAY_DIRECTION_EPSILON) ? 0 : (dirY > 0 ? 1 : -1);
            this.tDeltaY = (stepY == 0) ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(dirY);
            this.tMaxY = (stepY == 0) ? Double.POSITIVE_INFINITY : tDeltaY * (stepY > 0 ? 1 : 0);

            this.stepZ = (Math.abs(dirZ) < RAY_DIRECTION_EPSILON) ? 0 : (dirZ > 0 ? 1 : -1);
            this.tDeltaZ = (stepZ == 0) ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(dirZ);
            this.tMaxZ = (stepZ == 0) ? Double.POSITIVE_INFINITY : tDeltaZ * (stepZ > 0 ? 1 : 0);

            this.initialised = true;
        }

        void trace() {
            boolean isPaused = false;
            try {
                if (!initialised) init();
                if (energy <= 0) return;
                while (energy > 0) {
                    if (y < minY || y >= (minY + worldHeight) || Thread.currentThread().isInterrupted()) break;
                    if (currentRayPosition >= radius - PROCESSING_EPSILON) break;

                    int cx = SectionPos.blockToSectionCoord(x);
                    int cz = SectionPos.blockToSectionCoord(z);
                    int sectionY = level.getSectionIndex(y);
                    if (cx != lastCX || cz != lastCZ || sectionY != lastSectionY) {
                        currentSubChunkKey = new SubChunkKey(cx, cz, sectionY);
                        lastCX = cx;
                        lastCZ = cz;
                        lastSectionY = sectionY;
                    }

                    SubChunkSnapshot snap = snapshots.get(currentSubChunkKey);
                    if (snap == null) {
                        isPaused = true;
                        final boolean[] amFirst = {false};
                        ConcurrentLinkedQueue<RayTask> waiters = waitingRoom.computeIfAbsent(currentSubChunkKey, k -> {
                            amFirst[0] = true;
                            return new ConcurrentLinkedQueue<>();
                        });
                        if (amFirst[0]) highPriorityReactiveQueue.add(currentSubChunkKey);
                        waiters.add(this);
                        return;
                    }
                    if (snap == SubChunkSnapshot.EMPTY) {
                        if (skipEmptySubChunk()) {
                            break;
                        }
                        continue;
                    }
                    double t_exit_voxel = Math.min(tMaxX, Math.min(tMaxY, tMaxZ));
                    double segmentLenInVoxel = t_exit_voxel - this.currentRayPosition;
                    double segmentLenForProcessing;
                    boolean stopAfterThisSegment = false;

                    if (this.currentRayPosition + segmentLenInVoxel > radius - PROCESSING_EPSILON) {
                        segmentLenForProcessing = Math.max(0.0, radius - this.currentRayPosition);
                        stopAfterThisSegment = true;
                    } else segmentLenForProcessing = segmentLenInVoxel;

                    if (segmentLenForProcessing > PROCESSING_EPSILON) {
                        Block block = snap.getBlock(SectionPos.sectionRelative(x), SectionPos.sectionRelative(y), SectionPos.sectionRelative(z));
                        if (block != Blocks.AIR) {
                            float resistance = getNukeResistance(block);
                            if (resistance >= NUKE_RESISTANCE_CUTOFF) {
                                energy = 0;
                            } else {
                                double energyLossFactor = getEnergyLossFactor(resistance);
                                float damageDealt = (float) (energyLossFactor * segmentLenForProcessing);
                                energy -= damageDealt;
                                if (damageDealt > 0) {
                                    int yNorm = y - minY;
                                    int xLocal = SectionPos.sectionRelative(x);
                                    int zLocal = SectionPos.sectionRelative(z);
                                    int bitIndex = (yNorm << 8) | (zLocal << 4) | xLocal;

                                    ChunkPos chunkPos = currentSubChunkKey.getPos();
                                    if (ConfigBomb.explosionAlgorithm == 2) {
                                        damageMap.computeIfAbsent(chunkPos, cp -> new ConcurrentHashMap<>(256)).computeIfAbsent(bitIndex, k -> new DoubleAdder()).add(damageDealt);
                                    } else if (energy > 0) {
                                        destructionMap.computeIfAbsent(chunkPos, posKey -> new ConcurrentBitSet(bitsetSize)).set(bitIndex);
                                    }
                                }
                            }
                        }
                    }
                    this.currentRayPosition = t_exit_voxel;
                    if (energy <= 0 || stopAfterThisSegment) break;
                    if (tMaxX < tMaxY) {
                        if (tMaxX < tMaxZ) {
                            x += stepX;
                            tMaxX += tDeltaX;
                        } else {
                            z += stepZ;
                            tMaxZ += tDeltaZ;
                        }
                    } else {
                        if (tMaxY < tMaxZ) {
                            y += stepY;
                            tMaxY += tDeltaY;
                        } else {
                            z += stepZ;
                            tMaxZ += tDeltaZ;
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Ray {} at distance {} finished exceptionally due to: ", dirIndex, currentRayPosition, e);
            } finally {
                if (!isPaused) latch.countDown();
            }
        }

        private boolean skipEmptySubChunk() {
            double tExit = radius;

            if (stepX > 0) {
                int sectionMaxX = ((x >> 4) << 4) + 16;
                tExit = Math.min(tExit, (sectionMaxX - originX) * tDeltaX);
            } else if (stepX < 0) {
                int sectionMinX = (x >> 4) << 4;
                tExit = Math.min(tExit, (originX - sectionMinX) * tDeltaX);
            }

            if (stepY > 0) {
                int sectionMaxY = ((y >> 4) << 4) + 16;
                tExit = Math.min(tExit, (sectionMaxY - originY) * tDeltaY);
            } else if (stepY < 0) {
                int sectionMinY = (y >> 4) << 4;
                tExit = Math.min(tExit, (originY - sectionMinY) * tDeltaY);
            }

            if (stepZ > 0) {
                int sectionMaxZ = ((z >> 4) << 4) + 16;
                tExit = Math.min(tExit, (sectionMaxZ - originZ) * tDeltaZ);
            } else if (stepZ < 0) {
                int sectionMinZ = (z >> 4) << 4;
                tExit = Math.min(tExit, (originZ - sectionMinZ) * tDeltaZ);
            }

            if (!Double.isFinite(tExit) || tExit <= currentRayPosition + PROCESSING_EPSILON) {
                double nextVoxelExit = Math.min(tMaxX, Math.min(tMaxY, tMaxZ));
                if (!Double.isFinite(nextVoxelExit) || nextVoxelExit >= radius - PROCESSING_EPSILON) {
                    currentRayPosition = radius;
                    return true;
                }
                currentRayPosition = nextVoxelExit;
                advanceToNextVoxel();
                return false;
            }

            currentRayPosition = Math.min(tExit, radius);
            if (currentRayPosition >= radius - PROCESSING_EPSILON) {
                return true;
            }

            double advancedPosition = currentRayPosition + PROCESSING_EPSILON;
            x = (int) Math.floor(originX + dirX * advancedPosition);
            y = (int) Math.floor(originY + dirY * advancedPosition);
            z = (int) Math.floor(originZ + dirZ * advancedPosition);

            tMaxX = stepX == 0 ? Double.POSITIVE_INFINITY : (stepX > 0 ? (x + 1 - originX) : (originX - x)) * tDeltaX;
            tMaxY = stepY == 0 ? Double.POSITIVE_INFINITY : (stepY > 0 ? (y + 1 - originY) : (originY - y)) * tDeltaY;
            tMaxZ = stepZ == 0 ? Double.POSITIVE_INFINITY : (stepZ > 0 ? (z + 1 - originZ) : (originZ - z)) * tDeltaZ;

            lastCX = Integer.MIN_VALUE;
            lastCZ = Integer.MIN_VALUE;
            lastSectionY = Integer.MIN_VALUE;
            currentSubChunkKey = null;
            return false;
        }

        private void advanceToNextVoxel() {
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    x += stepX;
                    tMaxX += tDeltaX;
                } else {
                    z += stepZ;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    y += stepY;
                    tMaxY += tDeltaY;
                } else {
                    z += stepZ;
                    tMaxZ += tDeltaZ;
                }
            }
            lastCX = Integer.MIN_VALUE;
            lastCZ = Integer.MIN_VALUE;
            lastSectionY = Integer.MIN_VALUE;
            currentSubChunkKey = null;
        }

        private double getEnergyLossFactor(float resistance) {
            double effectiveDist = Math.max(this.currentRayPosition, MIN_EFFECTIVE_DIST_FOR_ENERGY_CALC);
            return (Math.pow(resistance + 1.0, 3.0 * (effectiveDist / radius)) - 1.0);
        }
    }
}
