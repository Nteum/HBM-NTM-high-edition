package com.hbm.render.model.armor;

import com.hbm.main.ResourceManager;
import com.hbm.render.model.AccessableRenderable;
import com.hbm.render.model.IObjModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ModelArmorBismuth extends ModelArmorBase {

	public ModelArmorBismuth() {
		super("Head","Body","LeftArm","RightArm","LeftLeg","RightLeg","LeftFoot","RightFoot");
	}
}
