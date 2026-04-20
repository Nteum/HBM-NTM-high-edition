package com.hbm.item.armor;

import com.hbm.HBM;
import com.hbm.HBMLang;

import com.hbm.blockentity.tools.TileEntityGeiger;
import com.hbm.item.HBMCombat;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import com.hbm.render.model.armor.ModelArmorBase;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

//Armor with full set bonus
// 全套盔甲会有加成
// 盔甲模型的主要接口在HumanoidArmorLayer中
public class ItemArmorFSB extends ArmorItem implements IArmorDisableModel {
    private String texture = "";
    private ResourceLocation overlay = null;
    public List<MobEffectInstance> effects = new ArrayList<>();
    public boolean noHelmet = false;
    public boolean vats = false;
    public boolean thermal = false;
    public boolean geigerSound = false;
    public boolean customGeiger = false;
    public boolean hardLanding = false;
    public int dashCount = 0;
    public int stepSize = 0;
    public SoundEvent step;
    public SoundEvent jump;
    public SoundEvent fall;
    public Supplier<HBMCombat.Suit> suit;

    public ItemArmorFSB(ArmorMaterial pMaterial, Type pType, Properties pProperties, Supplier<HBMCombat.Suit> suit) {
        super(pMaterial, pType, pProperties);
        this.suit = suit;
    }
    public ItemArmorFSB(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        this(pMaterial, pType, pProperties, ()->new HBMCombat.Suit(null, null, null, null));
    }
    public ItemArmorFSB addEffect(MobEffectInstance ... effect) {
        effects.addAll(List.of(effect));
        return this;
    }
    public ItemArmorFSB enableVATS(boolean vats) {
        this.vats = vats;
        return this;
    }

    public ItemArmorFSB enableThermalSight(boolean thermal) {
        this.thermal = thermal;
        return this;
    }

    public ItemArmorFSB setHasGeigerSound(boolean geiger) {
        this.geigerSound = geiger;
        return this;
    }

    public ItemArmorFSB setHasCustomGeiger(boolean geiger) {
        this.customGeiger = geiger;
        return this;
    }

    public ItemArmorFSB setHasHardLanding(boolean hardLanding) {
        this.hardLanding = hardLanding;
        return this;
    }

    public ItemArmorFSB setDashCount(int dashCount) {
        this.dashCount = dashCount;
        return this;
    }

    public ItemArmorFSB setStepSize(int stepSize) {
        this.stepSize = stepSize;
        return this;
    }

    public ItemArmorFSB setStep(SoundEvent step) {
        this.step = step;
        return this;
    }

    public ItemArmorFSB setJump(SoundEvent jump) {
        this.jump = jump;
        return this;
    }

    public ItemArmorFSB setFall(SoundEvent fall) {
        this.fall = fall;
        return this;
    }

    public ItemArmorFSB setOverlay(String path) {
        this.overlay = new ResourceLocation(path);
        return this;
    }

    public ItemArmorFSB suit(Supplier<HBMCombat.Suit> suit){
        this.suit = suit;
        return this;
    }

    public ItemArmorFSB cloneStats(ItemArmorFSB original) {

        //lists aren't being modified after instantiation, so there's no need to dereference
        this.effects = original.effects;
        this.noHelmet = original.noHelmet;
        this.vats = original.vats;
        this.thermal = original.thermal;
        this.geigerSound = original.geigerSound;
        this.customGeiger = original.customGeiger;
        this.hardLanding = original.hardLanding;
        this.dashCount = original.dashCount;
        this.stepSize = original.stepSize;
        this.step = original.step;
        this.jump = original.jump;
        this.fall = original.fall;
        //overlay doesn't need to be copied because it's helmet exclusive
        return this;
    }

    public String getTexture() {
        return texture;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, list, pIsAdvanced);
        List<Component> toAdd = new ArrayList<>();
        if(!effects.isEmpty()) {
            MutableComponent tipeffect = Component.empty();
            for (int i = 0; i < effects.size(); i++) {
                tipeffect.append(effects.get(i).getEffect().getDisplayName());
                if (i < effects.size()-1) tipeffect.append(", ");
            }
            toAdd.add(tipeffect.withStyle(ChatFormatting.AQUA));
        }

        if(geigerSound) toAdd.add(Component.translatable(HBMLang.ARMOR_GEIGERSOUND.key()).withStyle(ChatFormatting.GOLD));
        if(customGeiger) toAdd.add(Component.translatable(HBMLang.ARMOR_GEIGERHUD.key()).withStyle(ChatFormatting.GOLD));
        if(vats) toAdd.add(Component.translatable(HBMLang.ARMOR_VATS.key()).withStyle(ChatFormatting.RED));
        if(thermal) toAdd.add(Component.translatable(HBMLang.ARMOR_THERMAL.key()).withStyle(ChatFormatting.RED));
        if(hardLanding) toAdd.add(Component.translatable(HBMLang.ARMOR_HARDLANDING.key()).withStyle(ChatFormatting.RED));
        if(stepSize != 0) toAdd.add(Component.translatable(HBMLang.ARMOR_STEPSIZE.key()).withStyle(ChatFormatting.BLUE));
        if(dashCount > 0) toAdd.add(Component.translatable(HBMLang.ARMOR_DASH.key()).withStyle(ChatFormatting.AQUA));

        if(!toAdd.isEmpty()) {
            list.add(Component.translatable(HBMLang.ARMOR_FSB.key()).withStyle(ChatFormatting.GOLD));
            list.addAll(toAdd);
        }
    }

    public static boolean hasFSBArmor(Player player) {
        ItemStack plateSlot = player.getInventory().armor.get(2);       // 根据胸甲判定
        if (plateSlot.getItem() instanceof ItemArmorFSB armorFSB){
            HBMCombat.Suit suit = armorFSB.suit.get();
            if (suit.HELMET() != null && !player.getInventory().getArmor(3).is(suit.HELMET().get())) return false;
            if (suit.LEGS() != null && !player.getInventory().getArmor(1).is(suit.LEGS().get())) return false;
            if (suit.BOOT() != null && !player.getInventory().getArmor(0).is(suit.BOOT().get())) return false;
            return true;
        }
        return false;
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
        boolean step = true;
        // 未知代码，暂时不用
//        if(player.getUniqueID().equals(ShadyUtil.the_NCR) || player.getUniqueID().equals(ShadyUtil.Barnaby99_x)) {
//            step = false;
//
//            if(player.worldObj.isRemote && player.onGround) {
//                steppy(player, "hbm:step.powered");
//            }
//        }

        if(hasFSBArmor(player)) {
            ItemArmorFSB chestplate = (ItemArmorFSB) player.getInventory().getArmor(2).getItem();
            if (!level.isClientSide()){
                for (MobEffectInstance effect : chestplate.effects) {
                    player.addEffect(effect);
                }
                if (chestplate.geigerSound && !player.getInventory().hasAnyOf(Set.of(ModItems.GEIGER_COUNTER.get(), ModBlocks.GEIGER_COUNTER.get().asItem()))){
                    TileEntityGeiger.show(level, null, player, player.tickCount, TileEntityGeiger.check(level, player.getOnPos()));
                }
            } else if (step && chestplate.step != null && player.onGround()){
                steppy(player, chestplate.step);
            }
        }
    }
    public static void steppy(Player player, SoundEvent sound) {
        try {

//            Field nextStepDistance = ReflectionHelper.findField(Entity.class, "nextStepDistance", "field_70150_b");
//            Field distanceWalkedOnStepModified = ReflectionHelper.findField(Entity.class, "distanceWalkedOnStepModified", "field_82151_R");
//
//            if(player.getEntityData().getFloat("hfr_nextStepDistance") == 0) {
//                player.getEntityData().setFloat("hfr_nextStepDistance", nextStepDistance.getFloat(player));
//            }
//
//            int px = MathHelper.floor_double(player.posX);
//            int py = MathHelper.floor_double(player.posY - 0.2D - (double) player.yOffset);
//            int pz = MathHelper.floor_double(player.posZ);
//            Block block = player.worldObj.getBlock(px, py, pz);
//
//            if(block.getMaterial() != Material.air && player.getEntityData().getFloat("hfr_nextStepDistance") <= distanceWalkedOnStepModified.getFloat(player))
//                player.playSound(sound, 1.0F, 1.0F);
//
//            player.getEntityData().setFloat("hfr_nextStepDistance", nextStepDistance.getFloat(player));

        } catch(Exception x) {
        }
    }

    public boolean isArmorEnabled(ItemStack stack) { return true; }

    private HashSet<EnumPlayerPart> hidden = new HashSet<EnumPlayerPart>();
    private boolean needsFullSet = false;

    public ItemArmorFSB hides(EnumPlayerPart... parts) {
        Collections.addAll(hidden, parts);
        return this;
    }

    public ItemArmorFSB setFullSetForHide() {
        needsFullSet = true;
        return this;
    }

    @Override
    public boolean disablesPart(Player player, ItemStack stack, EnumPlayerPart part) {
        return false;
    }
    // 获取盔甲纹理
    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return super.getArmorTexture(stack, entity, slot, type);
    }

    public void handleJump(Player player) {

//        if(ArmorFSB.hasFSBArmor(player)) {
//
//            ArmorFSB chestplate = (ArmorFSB) player.inventory.armorInventory[2].getItem();
//
//            if(chestplate.jump != null)
//                player.playSound(chestplate.jump, 1.0F, 1.0F);
//        }
    }

    public void handleFall(Player player) {

//        if(ArmorFSB.hasFSBArmor(player)) {
//
//            ArmorFSB chestplate = (ArmorFSB) player.inventory.armorInventory[2].getItem();
//
//            if(chestplate.hardLanding && player.fallDistance > 10) {
//
//                // player.playSound(Block.soundTypeAnvil.func_150496_b(), 2.0F,
//                // 0.5F);
//
//                List<Entity> entities = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(3, 0, 3));
//
//                for(Entity e : entities) {
//
//                    if(e instanceof EntityItem)
//                        continue;
//
//                    Vec3 vec = Vec3.createVectorHelper(player.posX - e.posX, 0, player.posZ - e.posZ);
//
//                    if(vec.lengthVector() < 3) {
//
//                        double intensity = 3 - vec.lengthVector();
//                        e.motionX += vec.xCoord * intensity * -2;
//                        e.motionY += 0.1D * intensity;
//                        e.motionZ += vec.zCoord * intensity * -2;
//
//                        e.attackEntityFrom(DamageSource.causePlayerDamage(player).setDamageBypassesArmor(), (float) (intensity * 10));
//                    }
//                }
//                // return;
//            }
//
//            if(chestplate.fall != null)
//                player.playSound(chestplate.fall, 1.0F, 1.0F);
//        }
    }

    public void handleAttack(LivingAttackEvent event) { }
    public void handleHurt(LivingHurtEvent event) { }

    public enum FSBTex{helmet, chest, arm, leg;}

    @OnlyIn(Dist.CLIENT)
    public void renderObjItem(ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay){}

    @OnlyIn(Dist.CLIENT)
    public static void renderStandard(ItemArmorFSB armorFSB, ModelArmorBase model, String helmet, String chest, String arm, String leg,
                               ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay){
        float scale = 0.08f;
        float xRot = -45;
        float yRot = 225;
        float xOffset = 0.5f;
        float yOffset = 0.35f;
        float zOffSet = 0.5f;
        if (pDisplayContext != ItemDisplayContext.GUI){
            xRot = 0; scale = 0.04f;
            yOffset = 0.5f;
            if (pDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND){
                yRot = 135;
            }else if (pDisplayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
                yRot = 45;
            }else if (pDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND){
                yRot = -45;
            }
        }

        ResourceLocation texture = HBM.rl("textures/models/armor/");
        if (armorFSB.getType() == ArmorItem.Type.HELMET){
            pPoseStack.translate(xOffset, yOffset+0.35, zOffSet);
            pPoseStack.mulPose(Axis.XN.rotationDegrees(xRot));
            pPoseStack.mulPose(Axis.YN.rotationDegrees(yRot));
            pPoseStack.scale(scale*0.8f, scale*0.8f, scale*0.8f);
            VertexConsumer buffer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(texture.withSuffix(helmet + ".png")));
            model.chead.renderStatic(pPoseStack, buffer, pPackedLight, pPackedOverlay);
        }else if (armorFSB.getType() == ArmorItem.Type.CHESTPLATE){
            pPoseStack.translate(xOffset, yOffset, zOffSet);
            pPoseStack.mulPose(Axis.XN.rotationDegrees(xRot));
            pPoseStack.mulPose(Axis.YN.rotationDegrees(yRot));
            pPoseStack.scale(scale*0.6f,scale*0.6f,scale*0.6f);
            VertexConsumer buffer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(texture.withSuffix(chest + ".png")));
            model.cbody.renderStatic(pPoseStack, buffer, pPackedLight, pPackedOverlay);
            VertexConsumer buffer1 = pBuffer.getBuffer(RenderType.entityTranslucentCull(texture.withSuffix(arm + ".png")));
            model.cleftArm.renderStatic(pPoseStack, buffer1, pPackedLight, pPackedOverlay);
            model.crightArm.renderStatic(pPoseStack, buffer1, pPackedLight, pPackedOverlay);
        }else if (armorFSB.getType() == ArmorItem.Type.LEGGINGS){
            if (pDisplayContext != ItemDisplayContext.GUI) yOffset += 0.2f;
            pPoseStack.translate(xOffset, yOffset-0.65, zOffSet);
            pPoseStack.mulPose(Axis.XN.rotationDegrees(xRot));
            pPoseStack.mulPose(Axis.YN.rotationDegrees(yRot));
            pPoseStack.scale(scale, scale, scale);
            VertexConsumer buffer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(texture.withSuffix(leg + ".png")));
            model.cleftLeg.renderStatic(pPoseStack, buffer, pPackedLight, pPackedOverlay);
            model.crightLeg.renderStatic(pPoseStack, buffer, pPackedLight, pPackedOverlay);
        }else if (armorFSB.getType() == ArmorItem.Type.BOOTS){
            if (pDisplayContext != ItemDisplayContext.GUI) yOffset += 0.2f;
            pPoseStack.translate(xOffset, yOffset-1, zOffSet);
            pPoseStack.mulPose(Axis.XN.rotationDegrees(xRot));
            pPoseStack.mulPose(Axis.YN.rotationDegrees(yRot));
            pPoseStack.scale(scale, scale, scale);
            VertexConsumer buffer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(texture.withSuffix(leg + ".png")));
            model.cleftFoot.renderStatic(pPoseStack, buffer, pPackedLight, pPackedOverlay);
            model.crightFoot.renderStatic(pPoseStack, buffer, pPackedLight, pPackedOverlay);
        }
    }
}
