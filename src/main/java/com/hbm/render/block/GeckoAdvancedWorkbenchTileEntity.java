package net.mcreator.nuclearcraft.block.entity;

import io.netty.buffer.Unpooled;
import java.util.Iterator;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.mcreator.nuclearcraft.block.GeckoAdvancedWorkbenchBlock;
import net.mcreator.nuclearcraft.init.BigExplosivesModBlockEntities;
import net.mcreator.nuclearcraft.world.inventory.AdvancedWorkBechGuiMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/block/entity/GeckoAdvancedWorkbenchTileEntity.class */
public class GeckoAdvancedWorkbenchTileEntity extends RandomizableContainerBlockEntity implements GeoBlockEntity, WorldlyContainer {
    private final AnimatableInstanceCache cache;
    private NonNullList<ItemStack> stacks;
    private final LazyOptional<? extends IItemHandler>[] handlers;
    String prevAnim;

    public GeckoAdvancedWorkbenchTileEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType) BigExplosivesModBlockEntities.GECKO_ADVANCED_WORKBENCH.get(), pos, state);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.stacks = NonNullList.m_122780_(17, ItemStack.f_41583_);
        this.handlers = SidedInvWrapper.create(this, Direction.values());
        this.prevAnim = "0";
    }

    private PlayState predicate(AnimationState event) {
        String animationprocedure = m_58900_().m_61143_(GeckoAdvancedWorkbenchBlock.ANIMATION);
        if (animationprocedure.equals("0")) {
            return event.setAndContinue(RawAnimation.begin().thenLoop(animationprocedure));
        }
        return PlayState.STOP;
    }

    private PlayState procedurePredicate(AnimationState event) {
        String animationprocedure = m_58900_().m_61143_(GeckoAdvancedWorkbenchBlock.ANIMATION);
        if ((!animationprocedure.equals("0") && event.getController().getAnimationState() == AnimationController.State.STOPPED) || (!animationprocedure.equals(this.prevAnim) && !animationprocedure.equals("0"))) {
            if (!animationprocedure.equals(this.prevAnim)) {
                event.getController().forceAnimationReset();
            }
            event.getController().setAnimation(RawAnimation.begin().thenPlay(animationprocedure));
            if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                IntegerProperty integerPropertyM_61081_ = m_58900_().m_60734_().m_49965_().m_61081_("animation");
                if (integerPropertyM_61081_ instanceof IntegerProperty) {
                    IntegerProperty _integerProp = integerPropertyM_61081_;
                    this.f_58857_.m_7731_(m_58899_(), (BlockState) m_58900_().m_61124_(_integerProp, 0), 3);
                }
                event.getController().forceAnimationReset();
            }
        } else if (animationprocedure.equals("0")) {
            this.prevAnim = "0";
            return PlayState.STOP;
        }
        this.prevAnim = animationprocedure;
        return PlayState.CONTINUE;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController[]{new AnimationController(this, "controller", 0, this::predicate)});
        data.add(new AnimationController[]{new AnimationController(this, "procedurecontroller", 0, this::procedurePredicate)});
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void m_142466_(CompoundTag compound) {
        super.m_142466_(compound);
        if (!m_59631_(compound)) {
            this.stacks = NonNullList.m_122780_(m_6643_(), ItemStack.f_41583_);
        }
        ContainerHelper.m_18980_(compound, this.stacks);
    }

    public void m_183515_(CompoundTag compound) {
        super.m_183515_(compound);
        if (!m_59634_(compound)) {
            ContainerHelper.m_18973_(compound, this.stacks);
        }
    }

    /* renamed from: getUpdatePacket, reason: merged with bridge method [inline-methods] */
    public ClientboundBlockEntityDataPacket m_58483_() {
        return ClientboundBlockEntityDataPacket.m_195640_(this);
    }

    public CompoundTag m_5995_() {
        return m_187480_();
    }

    public int m_6643_() {
        return this.stacks.size();
    }

    public boolean m_7983_() {
        Iterator it = this.stacks.iterator();
        while (it.hasNext()) {
            ItemStack itemstack = (ItemStack) it.next();
            if (!itemstack.m_41619_()) {
                return false;
            }
        }
        return true;
    }

    public Component m_6820_() {
        return Component.m_237113_("gecko_advanced_workbench");
    }

    public int m_6893_() {
        return 64;
    }

    public AbstractContainerMenu m_6555_(int id, Inventory inventory) {
        return new AdvancedWorkBechGuiMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).m_130064_(this.f_58858_));
    }

    public Component m_5446_() {
        return Component.m_237113_("Gecko Advanced Workbench");
    }

    protected NonNullList<ItemStack> m_7086_() {
        return this.stacks;
    }

    protected void m_6520_(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public boolean m_7013_(int index, ItemStack stack) {
        if (index == 16) {
            return false;
        }
        return true;
    }

    public int[] m_7071_(Direction side) {
        return IntStream.range(0, m_6643_()).toArray();
    }

    public boolean m_7155_(int index, ItemStack stack, @Nullable Direction direction) {
        return m_7013_(index, stack);
    }

    public boolean m_7157_(int index, ItemStack stack, Direction direction) {
        return true;
    }

    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (!this.f_58859_ && facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
            return this.handlers[facing.ordinal()].cast();
        }
        return super.getCapability(capability, facing);
    }

    public void m_7651_() {
        super.m_7651_();
        for (LazyOptional<? extends IItemHandler> handler : this.handlers) {
            handler.invalidate();
        }
    }
}
