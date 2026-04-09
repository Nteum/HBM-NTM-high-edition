//在客户端启动时做一次离线质检，把常见问题直接打到日志.
package com.hbm.render.model; //没注意package默认的，导致无法PortQC

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import net.minecraftforge.client.model.obj.ObjLoader; //当加入OBJ时可用

import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import static com.hbm.HBM.MODID;
import static com.hbm.HBM.LOGGER;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)

public final class ModelQC {

    private ModelQC() {
    }

    public static void onClientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            ResourceManager rm = net.minecraft.client.Minecraft.getInstance().getResourceManager(); //应用相对路径
            // ResourceManager am = net.minecraftforge.client.model.obj.(/* 填空obj */);
            List<ResourceLocation> suspects = List.of(

                // 把要检查的一批 OBJ 加进来

                new ResourceLocation(MODID, "models/block/bomb/fat_man.obj"),
                new ResourceLocation(MODID, "models/block/crucible/crucible.obj")

            );

            for (var rl : suspects) {
                try {
                    List<Resource> res = rm.getResourceStack(rl);
                    if (res.isEmpty()) { LOGGER.warn("[QC] missing OBJ: {}", rl); continue; }
                    String text = IOUtils.toString(res.get(0).open(), StandardCharsets.UTF_8);
                    if (!text.contains("usemtl")) LOGGER.warn("[QC] OBJ has no material: {}", rl);
                    if (text.contains("vt ") && !text.contains("vt 0.0")) {
                        // 只是示例：可以解析更严格的规则（重复材质、负缩放等）
                    }
                } catch (Exception ex) {
                    LOGGER.error("[QC] fail load OBJ {} : {}", rl, ex.toString());
                }
            }
        });
    }

    public static ModelQC createModelQC() {
        return new ModelQC(); //新
    }
}