package com.hbm.block.machine;

import com.hbm.block.base.BaseMachineBlock;
import com.hbm.block.base.BedLikeBlock;
import com.hbm.block.base.BlockDummyable;
import com.hbm.block.interfaces.ICustomBlockItemModel;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.TileProxyCombo;
import com.hbm.blockentity.machine.AssemblerEntity;
import com.hbm.blockentity.machine.ChemplantEntity;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.hbm.block.base.BedLikeBlock.square;
import static com.hbm.render.RenderUtils.renderBlockModel;

public class BlockAssembler extends BlockDummyable implements ICustomBlockItemModel {
//    public static final VoxelShape SHAPE = Block.box(-32.0,0.0D,-32.0D,32.0D,32.0D,32.0D);
    public BlockAssembler(Properties pProperties) {
        super(pProperties);
        SHAPE = Block.box(-32.0,0.0D,-32.0D,32.0D,32.0D,32.0D);
    }
    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new AssemblerEntity(pPos,pState);
    }

    @Override
    public void renderStatic(ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        poseStack.pushPose();

        BakedModel body = Models.get(Models.ASSEMBLER_BODY);
        BakedModel arm = Models.get(Models.ASSEMBLER_ARM);
        BakedModel cog = Models.get(Models.ASSEMBLER_COG);
        BakedModel slider = Models.get(Models.ASSEMBLER_SLIDER);

        RenderUtils.renderModel(arm, poseStack, buffer, light, overlay, RenderType.cutout());
        RenderUtils.renderModel(slider, poseStack, buffer, light, overlay, RenderType.cutout());
        RenderUtils.renderModel(body, poseStack, buffer, light, overlay, RenderType.cutout());

        poseStack.pushPose();
        poseStack.translate(-0.6, 0.75, 1.0625);
        RenderUtils.renderModel(cog, poseStack, buffer, light, overlay, RenderType.cutout());
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-0.6, 0.75, -1.0625);
        RenderUtils.renderModel(cog, poseStack, buffer, light, overlay, RenderType.cutout());
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.6, 0.75, -1.0625);
        RenderUtils.renderModel(cog, poseStack, buffer, light, overlay, RenderType.cutout());
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.6, 0.75, 1.0625);
        RenderUtils.renderModel(cog, poseStack, buffer, light, overlay, RenderType.cutout());
        poseStack.popPose();

        poseStack.popPose();
    }
}
