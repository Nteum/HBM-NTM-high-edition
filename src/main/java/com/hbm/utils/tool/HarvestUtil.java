package com.hbm.utils.tool;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class HarvestUtil {
    public enum HarvestType{SINGLE, CHAIN, SQUARE}
    public static class HarvestContext{
        HarvestType type;
        int maxCount;
        int[] dimensions;
        public static HarvestContext single(){
            HarvestContext harvestContext = new HarvestContext();
            harvestContext.type = HarvestType.SINGLE;
            return harvestContext;
        }
        public static HarvestContext chain(int maxCount){
            HarvestContext harvestContext = new HarvestContext();
            harvestContext.maxCount = maxCount;
            return harvestContext;
        }
        public static HarvestContext square(int width, int height, int length){
            HarvestContext harvestContext = new HarvestContext();
            harvestContext.dimensions = new int[]{width, height, length};
            return harvestContext;
        }
    }

    /**
     * 电动机器专用的连锁挖矿（无物理工具消耗）
     *
     * @param fortuneLevel 机器升级模块提供的时运等级（0代表无时运，1~3代表时运I~III）
     * @return 实际成功连锁挖掘的方块数量（方便你乘以每块的耗电量，在机器里扣除电力）
     */
    public static int performMachineHarvest(ServerLevel level, BlockPos machinePos, BlockPos originPos, HarvestContext context, @Nullable IItemHandler itemHandler, int fortuneLevel, boolean silkTouch) {
        // 1. 获取通用的虚拟玩家
        FakePlayer fakePlayer = FakePlayerFactory.getMinecraft(level);
        fakePlayer.setPos(machinePos.getX() + 0.5, machinePos.getY(), machinePos.getZ() + 0.5);

        // 2. 【核心魔法】：电动机器不用工具，但我们可以凭空捏造一把临时的“隐形概念镐”给虚拟玩家
        // 这样不仅能完美触发原版的 destroyBlock，还能动态附加上机器插的“时运升级”！
        ItemStack virtualTool = ItemStack.EMPTY;
        virtualTool = new ItemStack(net.minecraft.world.item.Items.DIAMOND_PICKAXE); // 用钻石镐做载体保证什么矿都能挖
        if (fortuneLevel > 0) virtualTool.enchant(Enchantments.BLOCK_FORTUNE, fortuneLevel); // 拍上机器的时运等级
        else if (silkTouch) virtualTool.enchant(Enchantments.SILK_TOUCH, 1);
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, virtualTool);

        int mineCount = 0;
        if (context.type == HarvestType.SINGLE){
            mineCount = harvestSingleBlock(level, fakePlayer, originPos, true, itemHandler, itemHandler != null);
        }else if (context.type == HarvestType.CHAIN){
            mineCount = performChainMine(level, fakePlayer, originPos, context.maxCount, true, itemHandler, itemHandler != null);
        }

        // 4. 清理虚拟玩家的主手，防止残留
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        fakePlayer.getInventory().clearContent();

        return mineCount; // 返回总共挖了多少个，用于总扣电
    }

    public static int harvestSingleBlock(ServerLevel level, ServerPlayer player, BlockPos currentPos, boolean matchState, @Nullable IItemHandler itemHandler, boolean autoCollect){
        boolean success = player.gameMode.destroyBlock(currentPos);
        ItemStack tool = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (success) {
            // 自动搜集凋落物
            if (autoCollect){
                if (itemHandler == null) itemHandler = new InvWrapper(player.getInventory());
                collectDropsAtPos(level, currentPos, itemHandler);
            }
            // 工具扣除耐久（如果是生存模式且物品支持耐久）
            if (!(player instanceof FakePlayer) && !player.isCreative() && tool.isDamageableItem()) {
                tool.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }
            return 1;
        }
        return 0;
    }
    /**
     * 执行连锁挖矿的核心通用函数
     *
     * @param level       服务器世界
     * @param player      执行挖掘的玩家
     * @param originPos   起始方块（玩家物理破坏的那个方块）
     * @param maxCount    最大连锁方块数量（防止服务器卡死，比如 64 或 128）
     * @param matchState  是否严格匹配方块状态（true: 连矿石种类和NBT都匹配，false: 只匹配同类Block）
     */
    public static int performChainMine(ServerLevel level, ServerPlayer player, BlockPos originPos, int maxCount, boolean matchState, @Nullable IItemHandler itemHandler, boolean autoCollect) {
        ItemStack tool = player.getItemInHand(InteractionHand.MAIN_HAND);
        boolean isFakePlayer = player instanceof FakePlayer;
        if (tool.isEmpty()) return 0;

        BlockState targetState = level.getBlockState(originPos);
        Block targetBlock = targetState.getBlock();

        // 1. 初始化 BFS 队列和已访问集合（采用 FastUtil 优化性能）
        Queue<BlockPos> queue = new ArrayDeque<>();
        LongSet visited = new LongOpenHashSet();

        queue.add(originPos);
        visited.add(originPos.asLong());

        int minedCount = 0;

        // 2. 开始 BFS 蔓延算法
        while (!queue.isEmpty() && minedCount < maxCount) {
            BlockPos currentPos = queue.poll();
            // 跳过起始方块，因为玩家自己已经通过原版挖掘流程把它破坏掉了
            if (!currentPos.equals(originPos) ) {
                BlockState currentState = level.getBlockState(currentPos);
                // 检查方块是否匹配
                if (shouldMine(targetState, currentState, targetBlock, matchState)) {
                    // 核心安全破坏：调用 playerGameMode 的接口，保证完美触发掉落物、经验和进度
                    boolean success = player.gameMode.destroyBlock(currentPos);
                    if (success) {
                        minedCount++;
                        // 自动搜集凋落物
                        if (autoCollect){
                            if (itemHandler == null) itemHandler = new InvWrapper(player.getInventory());
                            collectDropsAtPos(level, currentPos, itemHandler);
                        }
                        // 工具扣除耐久（如果是生存模式且物品支持耐久）
                        if (!isFakePlayer && !player.isCreative() && tool.isDamageableItem()) {
                            tool.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                            // 如果工具在中途直接爆掉了，立刻终止连锁，防止空手拆迁
                            if (tool.isEmpty()) {
                                break;
                            }
                        }
                    }
                }
            }

            // 3. 往 26 个方向（上下左右前后 + 斜对角）全面蔓延
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;

                        BlockPos neighbor = currentPos.offset(x, y, z);
                        long neighborLong = neighbor.asLong();

                        if (!visited.contains(neighborLong)) {
                            visited.add(neighborLong);

                            // 剪枝优化：只有当邻居方块确实匹配时，才放入队列继续蔓延
                            BlockState neighborState = level.getBlockState(neighbor);
                            if (shouldMine(targetState, neighborState, targetBlock, matchState)) {
                                queue.add(neighbor);
                            }
                        }
                    }
                }
            }
        }
        return minedCount;
    }

    /**
     * 判断方块是否符合连锁挖掘条件的过滤断言
     */
    private static boolean shouldMine(BlockState originState, BlockState currentState, Block targetBlock, boolean matchState) {
        if (currentState.isAir()) return false;

        if (matchState) {
            // 严格匹配：必须是完全一样的状态（例如：如果是红石矿，亮着的和暗着的会区分开）
            return currentState == originState;
        } else {
            // 宽松匹配：只要是同一种方块就行（例如：亮着的红石矿和暗着的红石矿都会被一并连锁）
            return currentState.is(targetBlock);
        }
    }

    /**
     * 自动拦截并抓取指定坐标掉落物的通用私有方法
     */
    private static void collectDropsAtPos(ServerLevel level, BlockPos pos, IItemHandler inventory) {
        // 创建一个稍微比 1x1x1 方块大一点点的探测盒（上下左右各外扩 0.25 格，防止掉落物因为弹跳物理效果飞走）
        AABB searchBox = new AABB(pos).inflate(0.25);

        // 抓取该范围内所有的物品实体
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, searchBox);

        for (ItemEntity itemEntity : items) {
            // 安全检查：防止该物品已经被别人标记为死亡或正在被吸走
            if (!itemEntity.isAlive()) continue;

            ItemStack dropStack = itemEntity.getItem();
            if (dropStack.isEmpty()) continue;

            // 使用 Forge 官方自带的物品插入助手方法（自动寻找空格、自动处理堆叠上限）
            // 参数 3: simulate（false 代表真正插入物品，true 代表只模拟不改变容器）
            ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, dropStack, false);

            if (remainder.isEmpty()) {
                // 情况 A：机器背包很大，完全吃下了这一堆掉落物，直接把世界上的实体清除掉
                itemEntity.discard();
            } else {
                // 情况 B：机器背包满了或者快满了，只吃下了一部分。
                // 把吃剩的物品重新写回实体中，让它留在地上，防止吃不下导致物品凭空消失（严谨机制）
                itemEntity.setItem(remainder);
            }
        }
    }

}
