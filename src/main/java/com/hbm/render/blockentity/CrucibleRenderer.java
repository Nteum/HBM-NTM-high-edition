package com.hbm.render.blockentity;

import com.hbm.HBM;
import com.hbm.Inventory.fluid.CrucibleFluidHandler;
import com.hbm.Inventory.material.HBMMatter;
import com.hbm.blockentity.machine.CrucibleEntity;
import com.hbm.registries.HBMMatters;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;

import static com.hbm.render.RenderUtils.renderBlockModel;

public class CrucibleRenderer implements BlockEntityRenderer<CrucibleEntity> {
    public static final ResourceLocation FLUID_TEXTURE = HBM.rl("textures/block/fluid/lava_gray.png");
    public static BakedModel crucible_model;

    public CrucibleRenderer(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        crucible_model = modelManager.getModel(Models.CRUCIBLE);
    }
    @Override
    public void render(CrucibleEntity crucible, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer modelRenderer = blockRenderer.getModelRenderer();
        BlockState blockState = crucible.getBlockState();

        pPoseStack.pushPose();
        DirectionUtils.generalMachineRotate(pPoseStack, blockState);
        renderBlockModel(crucible_model,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);

        BlockPos pos = crucible.getBlockPos();
        float flowWide = 0.15f;
        float tilt = Mth.PI / 4;
        Direction direction = DirectionUtils.horizRot(Direction.SOUTH, crucible.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING), Direction.EAST);
        VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(FLUID_TEXTURE));
        Vec3 origin = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        if (crucible.isStoreStackPouring()){
            CrucibleFluidHandler fluidHandler = crucible.getStoreStack();
            HBMMatter matter = HBMMatters.getMatterFromFluid(fluidHandler.getFluidInTank(0));
            int color = matter == null ? 0xffffffff : matter.moltenColor;
            Vec3 pourStart = pos.relative(direction).getCenter().relative(direction, 0.125).relative(Direction.UP, 0.375).subtract(origin);
            Vec3 flowDir = getFlowDir(direction, tilt);
            Vec3 pourEnd = pourStart.add(flowDir.scale(-pourStart.y / flowDir.y));
            RenderUtils.renderRectPillar(pPoseStack, consumer, color, pourStart, pourEnd, flowWide, pPackedLight, pPackedOverlay);
            RenderUtils.renderRectPillar(pPoseStack, consumer, color, pourEnd.relative(Direction.UP, flowWide / 3), pourEnd.relative(Direction.DOWN, 0.875), flowWide, pPackedLight, pPackedOverlay);
        }
        if (crucible.isAlloyStackPouring()){
            direction = direction.getOpposite();
            CrucibleFluidHandler fluidHandler = crucible.getAlloyStack();
            HBMMatter matter = HBMMatters.getMatterFromFluid(fluidHandler.getFluidInTank(0));
            int color = matter == null ? 0xffffffff : matter.moltenColor;
            Vec3 pourStart = pos.relative(direction).getCenter().relative(direction, 0.125).relative(Direction.UP, 0.375).subtract(origin);
            Vec3 flowDir = getFlowDir(direction, tilt);
            Vec3 pourEnd = pourStart.add(flowDir.scale(-pourStart.y / flowDir.y));
            RenderUtils.renderRectPillar(pPoseStack, consumer, color, pourStart, pourEnd, flowWide, pPackedLight, pPackedOverlay);
            RenderUtils.renderRectPillar(pPoseStack, consumer, color, pourEnd.relative(Direction.UP, flowWide / 3), pourEnd.relative(Direction.DOWN, 0.875), flowWide, pPackedLight, pPackedOverlay);
        }
        pPoseStack.popPose();
    }
    // tilt斜率，单位是弧度
    private Vec3 getFlowDir(Direction direction, float tilt){
        Vec3i dirNormal = direction.getNormal();
        Vec3 flowDir = new Vec3(dirNormal.getX(), dirNormal.getY(), dirNormal.getZ());
        switch (direction){
            case EAST -> flowDir = flowDir.zRot(tilt);
            case WEST -> flowDir = flowDir.zRot(-tilt);
            case SOUTH -> flowDir = flowDir.xRot(tilt);
            case NORTH -> flowDir = flowDir.xRot(-tilt);
        }
        return flowDir.normalize();
    }
}
