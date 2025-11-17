package com.hbm.dev;

import com.hbm.HBM;
import com.hbm.render.model.BaseObjModel;
import com.hbm.render.model.entity.ModelGlyphid;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Development helper that validates OBJ group references against the actual groups
 * baked out of the source .obj files. Enable via -Dhbm.modelValidate=true.
 */
public final class ModelValidator {

    private static final Logger LOGGER = LogManager.getLogger("HBM-ModelValidator");
    private static final String PROPERTY = "hbm.modelValidate";

    private ModelValidator() {
    }

    public static void runIfRequested() {
        if (!Boolean.parseBoolean(System.getProperty(PROPERTY, "false"))) {
            return;
        }
        if (Minecraft.getInstance() == null || Minecraft.getInstance().getResourceManager() == null) {
            LOGGER.warn("Minecraft resources unavailable; skipping model validation.");
            return;
        }
        LOGGER.info("HBM model validator enabled via -D{}=true", PROPERTY);
        validateGlyphid();
    }

    private static void validateGlyphid() {
        try {
            ModelGlyphid<?> glyphid = new ModelGlyphid<>();
            ResourceLocation json = HBM.modelRl("entity/glyphid");
            glyphid.parseJson(json);
            BaseObjModel root = glyphid.getRootModel();
            if (root == null) {
                LOGGER.warn("Glyphid model '{}' failed to load.", json);
                return;
            }
            Set<String> available = new HashSet<>();
            collectGroups(root, available);
            List<String> required = glyphid.trackedGroups();
            for (String group : required) {
                if (group == null || group.isEmpty()) {
                    continue;
                }
                if (!available.contains(group)) {
                    LOGGER.warn("Glyphid model '{}' missing group '{}'. OBJ / JSON mismatch detected.", root.getModelIdentifier(), group);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Glyphid model validation failed", ex);
        }
    }

    private static void collectGroups(BaseObjModel model, Set<String> groups) {
        if (model == null) {
            return;
        }
        if (model.name != null && !model.name.isEmpty()) {
            groups.add(model.name);
        }
        for (BaseObjModel child : model.children.values()) {
            collectGroups(child, groups);
        }
    }
}
