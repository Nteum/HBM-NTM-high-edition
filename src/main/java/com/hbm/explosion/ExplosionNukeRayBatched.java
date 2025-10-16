package com.hbm.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.*;
/** 处理核爆过程的工具类 */
public class ExplosionNukeRayBatched implements IExplosionRay {
    //所有需要处理的区块，key是区块的位置，value是区块中待删方块的列表
    private final HashMap<ChunkPos, List<Vec3>> perChunk = new HashMap<>(); //for future: optimize blockmap further by using sub-chunks instead of chunks
    private final List<ChunkPos> orderedChunks = new ArrayList<>();
    private final CoordComparator comparator = new CoordComparator();
    private final BlockPos pos;       //爆炸中心所在的位置（实体）
    private final ChunkPos chunkPos;  //爆炸中心所在的区块

    private final Level level;

    private final int strength;
    private final int radius;
    private final int speed;
    private final int gspNumMax;
    private int gspNum;
    //球坐标系的方位角
    private double theta;
    private double phi;

    private boolean isAusf3Complete = false;

    int collectTipCnt = 0;
    int processChunkCnt = 0;
    public ExplosionNukeRayBatched(Level level, BlockPos pos, int strength, int speed, int radius) {
        this.level = level;
        this.pos = pos;
        this.chunkPos = new ChunkPos(pos);
        this.strength = strength;
        this.radius = radius;
        this.speed = speed;
        // Total number of points
        this.gspNumMax = (int)(2.5 * Math.PI * Math.pow(this.strength,2));
        this.gspNum = 1;

        // The beginning of the generalized spiral points
        this.theta = Math.PI;
        this.phi = 0.0;
    }

    public void collectTip(int count) {
        int amountProcessed = 0;
        while(this.gspNumMax >= this.gspNum){
            //将球坐标方位角转换成对应直角坐标方向向量
            Vec3 vec = spherical2cartesian(new Vec2((float) theta, (float) phi));

            int length = (int)Math.ceil(strength);
            //res这里应该是类似冲击强度的概念
            float res = strength;
            //沿着一条从中心发出的射线，冲击波可以到达的最远距离。
            Vec3 endPoint = null;
            HashSet<ChunkPos> chunkCoords = new HashSet();
            //从原点开始，沿着特定方向，考察length格数
            //i相当于延申的距离
            for(int i = 0; i < length && i < this.radius && res > 0; i ++) {
                //从原点pos沿vec延申i格的位置
                Vec3 dirVec = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(vec.scale(i));
                BlockPos dirPos = new BlockPos((int) Math.floor(dirVec.x), (int) Math.floor(dirVec.y), (int) Math.floor(dirVec.z));
                BlockState blockState = level.getBlockState(dirPos);
                //fac应该是冲击波的衰减程度
                double fac = 100 - ((double) i) / ((double) length) * 100;
                fac *= 0.07D;

                //爆炸抗性这里先使用block的默认抗性
                // 我也搞不明白怎么回事，只能用一种简单粗暴的方式删除所有草方块之类的东西。
                if (blockState.canBeReplaced()) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2,0);
                if (!blockState.liquid())res -= (float) Math.pow(blockState.getBlock().getExplosionResistance(), 7.5D - fac);

                if(res > 0 && !blockState.isAir()) {
                    //更新lastPos
                    endPoint = dirVec;
                    //添加区块
                    chunkCoords.add(new ChunkPos(dirPos));
                }
            }

            for(ChunkPos pos : chunkCoords) {
                List<Vec3> endList = perChunk.computeIfAbsent(pos, k -> new ArrayList<>());
                //不同区块重用端点，端点不一定在区块内，删除的时候在下一阶段做判断
                endList.add(endPoint);
            }

            // Raise one generalized spiral points
            this.generateGspUp();

            if (++amountProcessed >= count)
                return;
        }

        orderedChunks.addAll(perChunk.keySet());
        orderedChunks.sort(comparator);

        isAusf3Complete = true;
    }

    public void processChunk() {
        if(this.perChunk.isEmpty()) return;

        ChunkPos coord = orderedChunks.get(0);
        List<Vec3> list = perChunk.get(coord);
        HashSet<BlockPos> toRem = new HashSet<>();      //被移除的方块
        HashSet<BlockPos> toRemTips = new HashSet<>();  //被移除的处在边界点的方块

        int enter = (int) (Math.min(Math.abs(pos.getX() - (coord.x << 4)), Math.abs(pos.getZ() - (coord.z << 4)))) - 16; //jump ahead to cut back on NOPs

        enter = Math.max(enter, 0);

        for(Vec3 triplet : list) {
            Vec3 vec = triplet.subtract(pos.getCenter());
            double vecLen = vec.length();
            vec = vec.normalize();
            BlockPos tip = new BlockPos((int) Math.floor(triplet.x), (int) Math.floor(triplet.y), (int) Math.floor(triplet.z));

            boolean inChunk = false;
            for(int i = enter; i < vecLen; i++) {
                BlockPos pos1 = new BlockPos((int) Math.floor(pos.getX() + vec.x * i), (int) Math.floor(pos.getY() + vec.y * i), (int) Math.floor(pos.getZ() + vec.z * i));
                if(pos1.getX() >> 4 != coord.x || pos1.getZ() >> 4 != coord.z) {
                    if(inChunk) {
                        break;
                    } else {
                        continue;
                    }
                }
                inChunk = true;

                if(!level.getBlockState(pos1).isAir()) {
                    if(pos1.equals(tip)) {
                        toRemTips.add(pos1);
                    }
                    toRem.add(pos1);
                }
            }
        }

        for(BlockPos pos : toRem) {
            if(toRemTips.contains(pos)) {
                this.handleTip(pos);
            } else {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2,0);
            }
        }

//        List<T> nearbyEntities = level.getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat().range(20.0D), this, new AABB(new BlockPos(coord.x << 4, pos.getY() - 30, coord.z << 4), new BlockPos(coord.x << 4 + 15, pos.getY() + 30, coord.z << 4 + 15)));


        perChunk.remove(coord);
        orderedChunks.remove(0);
    }

    protected void handleTip(BlockPos blockPos) {
        level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
    }

    private static Vec3 spherical2cartesian(Vec2 sPos){
        return new Vec3(Math.sin(sPos.x) * Math.cos(sPos.y), Math.cos(sPos.x), Math.sin(sPos.x) * Math.sin(sPos.y));
    }
    //更新方位角
    private void generateGspUp(){
        if (this.gspNum < this.gspNumMax) {
            int k = this.gspNum + 1;
            double hk = -1.0 + 2.0 * (k - 1.0) / (this.gspNumMax - 1.0);
            this.theta = Math.acos(hk);

            double prev_lon = this.phi;
            double lon = prev_lon + 3.6 / Math.sqrt(this.gspNumMax) / Math.sqrt(1.0 - hk * hk);
            this.phi = lon % (Math.PI * 2);
        } else {
            this.theta = 0.0;
            this.phi = 0.0;
        }
        this.gspNum++;
    }

    @Override
    public void cacheChunksTick(int processTimeMs) {
        if (!isAusf3Complete) {
            // time ignored here since collectTip() did not implement a time limit
            collectTip(speed*10);
        }
    }

    @Override
    public void destructionTick(int processTimeMs) {
        if (!isAusf3Complete) return;
        long start = System.currentTimeMillis();
        while(!perChunk.isEmpty() && System.currentTimeMillis() < start + processTimeMs)
            processChunk();
    }

    @Override
    public void cancel() {
        isAusf3Complete = true;
        perChunk.clear();
        orderedChunks.clear();
    }

    @Override
    public boolean isComplete() {
        return isAusf3Complete && perChunk.isEmpty();
    }

    //比较器，用于根据到中心点的距离对区块排序
    public class CoordComparator implements Comparator<ChunkPos> {
        @Override
        public int compare(ChunkPos o1, ChunkPos o2) {
            int diff1 = Math.abs((chunkPos.x - o1.x)) + Math.abs((chunkPos.z - o1.z));
            int diff2 = Math.abs((chunkPos.x - o2.x)) + Math.abs((chunkPos.z - o2.z));
            return diff1 - diff2;
        }
    }
    @Deprecated
    private static BlockPos OutsideRound(BlockPos pos, Vec3 vec, int i){
        double x0 = vec.x<0?Math.ceil(pos.getX() + vec.x * i):Math.floor(pos.getX() + vec.x * i);
        double y0 = vec.y<0?Math.ceil(pos.getY() + vec.y * i):Math.floor(pos.getY() + vec.y * i);
        double z0 = vec.z<0?Math.ceil(pos.getZ() + vec.z * i):Math.floor(pos.getZ() + vec.z * i);
        return new BlockPos((int) x0, (int) y0, (int) z0);
    }
}
