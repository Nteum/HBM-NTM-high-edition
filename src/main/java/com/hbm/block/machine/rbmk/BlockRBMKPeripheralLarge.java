package com.hbm.block.machine.rbmk;

import com.hbm.block.base.BlockDummyable;
import com.hbm.block.interfaces.ILookOverlay;
import com.hbm.blockentity.machine.rbmk.RBMKPeripheralEntity;
import com.hbm.reactor.rbmk.RBMKDoddOverlay;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import com.hbm.utils.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * Multi-block RBMK peripheral for large OBJ models (console, crane console, autoloader).
 */
public class BlockRBMKPeripheralLarge extends BlockDummyable implements ILookOverlay {
    private static final double[][] CONSOLE_BOXES = new double[][]{
            {-1.0D, 0.0D, 0.0D, 2.0D, 4.0D, 0.875D},
            {-1.0D, 0.0D, 0.875D, 2.0D, 0.5D, 3.0D}
    };
    private static final double[][] CRANE_BOXES = new double[][]{
            {-1.0D, 0.0D, 0.0D, 2.0D, 1.5D, 0.875D},
            {-1.0D, 0.0D, 0.875D, 2.0D, 0.5D, 2.0D}
    };
    private static final double[][] AUTOLOADER_BOXES = new double[][]{
            {0.375D, 0.0D, 0.375D, 0.625D, 4.0D, 0.625D},
            {0.0D, 4.0D, 0.0D, 1.0D, 9.0D, 1.0D}
    };

    private final RBMKPeripheralType type;
    private final int placementOffset;

    public BlockRBMKPeripheralLarge(Properties properties, RBMKPeripheralType type, VoxelShape shape, boolean rotateShape, int placementOffset) {
        super(properties);
        this.type = type;
        this.shape = shape;
        this.shapeRotates = rotateShape;
        this.placementOffset = placementOffset;
    }

    public RBMKPeripheralType getPeripheralType() {
        return type;
    }

    @Override
    protected int placementOffset() {
        return placementOffset;
    }

    @Nullable
    @Override
    protected BlockEntity mainBlockEntity(BlockPos pos, BlockState state) {
        return new RBMKPeripheralEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(IS_CORE) ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getCoreShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
        return orientShape(cellShape(new Vec3i(0, 0, 0)), state.getValue(FACING));
    }

    @Override
    protected VoxelShape getProxyShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!(level instanceof LevelReader levelReader) || !state.hasProperty(FACING)) {
            return Shapes.empty();
        }
        BlockPos core = getCore(state, levelReader, pos);
        if (core.equals(pos)) {
            return Shapes.block();
        }
        Direction facing = state.getValue(FACING);
        Vec3i worldOffset = pos.subtract(core);
        Vec3i localOffset = DirectionUtils.offsetRot(worldOffset, facing, Direction.SOUTH);
        return orientShape(cellShape(localOffset), facing);
    }

    private VoxelShape orientShape(VoxelShape shape, Direction facing) {
        return shape.isEmpty() ? shape : DirectionUtils.voxelShapeRot(shape, facing);
    }

    private VoxelShape cellShape(Vec3i localOffset) {
        return switch (type) {
            case CONSOLE -> clippedShape(localOffset, CONSOLE_BOXES);
            case CRANE_CONSOLE -> clippedShape(localOffset, CRANE_BOXES);
            case AUTOLOADER -> clippedShape(localOffset, AUTOLOADER_BOXES);
            default -> Shapes.block();
        };
    }

    private static VoxelShape clippedShape(Vec3i localOffset, double[][] boxes) {
        double cellMinX = localOffset.getX();
        double cellMinY = localOffset.getY();
        double cellMinZ = localOffset.getZ();
        double cellMaxX = cellMinX + 1.0D;
        double cellMaxY = cellMinY + 1.0D;
        double cellMaxZ = cellMinZ + 1.0D;

        VoxelShape result = Shapes.empty();
        for (double[] box : boxes) {
            double minX = Math.max(box[0], cellMinX);
            double minY = Math.max(box[1], cellMinY);
            double minZ = Math.max(box[2], cellMinZ);
            double maxX = Math.min(box[3], cellMaxX);
            double maxY = Math.min(box[4], cellMaxY);
            double maxZ = Math.min(box[5], cellMaxZ);
            if (maxX <= minX || maxY <= minY || maxZ <= minZ) {
                continue;
            }
            result = Shapes.or(result, Block.box(
                    (minX - cellMinX) * 16.0D,
                    (minY - cellMinY) * 16.0D,
                    (minZ - cellMinZ) * 16.0D,
                    (maxX - cellMinX) * 16.0D,
                    (maxY - cellMinY) * 16.0D,
                    (maxZ - cellMinZ) * 16.0D
            ));
        }
        return result;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockPos core = getCore(state, level, pos);
        BlockEntity blockEntity = level.getBlockEntity(core);
        if (blockEntity instanceof MenuProvider provider) {
            NetworkHooks.openScreen((net.minecraft.server.level.ServerPlayer) player, provider, buf -> buf.writeBlockPos(core));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public List<Component> getDesc(Level level, BlockPos pos) {
        return RBMKDoddOverlay.describe(level, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (type == RBMKPeripheralType.CONSOLE || type == RBMKPeripheralType.CRANE_CONSOLE) {
            return Shapes.block();
        }
        return super.getShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (type == RBMKPeripheralType.CONSOLE || type == RBMKPeripheralType.CRANE_CONSOLE) {
            return Shapes.block();
        }
        return super.getInteractionShape(state, level, pos);
    }
}
