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

//	@Override
//	public void initializeParts() {
//		this.chead = this.accRenderable.components.get("Head");
//		this.cbody = this.accRenderable.components.get("Body");
//		this.cleftArm = this.accRenderable.components.get("LeftArm");
//		this.crightArm = this.accRenderable.components.get("RightArm");
//		this.cleftLeg = this.accRenderable.components.get("LeftLeg").setRotPoint(0, 12, 0);
//		this.crightLeg = this.accRenderable.components.get("RightLeg").setRotPoint(0, 12, 0);
//		this.cleftFoot = this.accRenderable.components.get("LeftFoot").setRotPoint(0, 12, 0);
//		this.crightFoot = this.accRenderable.components.get("RightFoot").setRotPoint(0, 12, 0);
//	}
}
