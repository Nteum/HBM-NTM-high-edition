package com.hbm.item.weapon;

import com.hbm.entity.weapon.missile.EntityMissile;
import com.hbm.entity.weapon.missile.EntityMissileGenetic;
import com.hbm.main.ClientSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.obj.ObjLoader;

import java.util.function.Consumer;

public class ItemMissle extends Item{

    public ItemMissle(Properties pProperties) {
        super(pProperties);
    }

    public EntityMissile createEntity(Level level, BlockPos pos, BlockPos target){
//        return null;
        Vec3 center = pos.getCenter();
        return new EntityMissileGenetic(level,center.x,center.y,center.z,target);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ClientSetup.specialItemRender;
            }
        });
    }
}
