package com.hbm.block.weapon;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.weapon.LaunchPadTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

public class LaunchPad extends BlockDummyable implements IBomb {
	public LaunchPad(Properties pProperties) {
		super(pProperties);
		this.shape = Block.box(-16, 0, -16, 32, 160, 32);
//		this.shape = Shapes.or(Shapes.box(-1.5D, 0D, -1.5D, -0.5D, 1D, -0.5D),
//					Shapes.box(0.5D, 0D, -1.5D, 1.5D, 1D, -0.5D),
//					Shapes.box(-1.5D, 0D, 0.5D, -0.5D, 1D, 1.5D),
//					Shapes.box(0.5D, 0D, 0.5D, 1.5D, 1D, 1.5D),
//					Shapes.box(-0.5D, 0.5D, -1.5D, 0.5D, 1D, 1.5D),
//					Shapes.box(-1.5D, 0.5D, -0.5D, 1.5D, 1D, 0.5D));
	}

	@Override
	protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
		return new LaunchPadTileEntity(pPos, pState);
	}
	// 作用不明
//	@Override
//	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
//		return this.standardOpenBehavior(world, x, y, z, player, 0);
//	}

	@Override
	public int[] getDimensions() {
		return new int[] {0, 0, 1, 1, 1, 1};
	}

	// 导弹发射
	@Override
	public BombReturnCode explode(Level pLevel, BlockPos pPos) {
		if(!pLevel.isClientSide()) {
			BlockState blockState = pLevel.getBlockState(pPos);
			BlockPos core = blockState.getValue(IS_CORE) ? pPos : getCore(blockState, pLevel, pPos);
			if(core != null){
				BlockEntity coreEntity = pLevel.getBlockEntity(core);
				if(coreEntity instanceof LaunchPadTileEntity launchPad){
					return launchPad.launchFromDesignator();
				}
			}
		}
		return BombReturnCode.UNDEFINED;
	}

	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
		if (!pLevel.isClientSide()){
			if (pState.getValue(IS_CORE)){
				if (pLevel.getBlockEntity(pPos) instanceof LaunchPadTileEntity launchPad){
//					launchPad.updateRedstonePower(pPos);
					if (pLevel.getBestNeighborSignal(pPos) > 0) launchPad.launchFromDesignator();
				}
			}else {
				super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
			}
		}
	}
}
