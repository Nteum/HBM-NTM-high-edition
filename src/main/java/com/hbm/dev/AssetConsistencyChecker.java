package com.hbm.dev;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.HBM;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Development helper that validates that each registered HBM item and block has a complete
 * blockstate/model/texture pipeline.
 *
 * Enable by starting the game/datagen run with -Dhbm.assetCheck=true. The checker will emit a
 * detailed report to both the log and {@code asset-report.txt}.
 */
public final class AssetConsistencyChecker {

    private static final Logger LOGGER = LogManager.getLogger("HBM-AssetChecker");
    private static final String PROPERTY = "hbm.assetCheck";

    private AssetConsistencyChecker() {
    }

    public static void runIfRequested() {
        if (!Boolean.parseBoolean(System.getProperty(PROPERTY, "false"))) {
            return;
        }
        LOGGER.info("HBM asset checker enabled via -D{}=true", PROPERTY);
        try {
            runCheck();
        } catch (Exception ex) {
            LOGGER.error("HBM asset checker failed", ex);
        }
    }

    private static void runCheck() throws IOException {
        List<Path> assetRoots = discoverAssetRoots();
        if (assetRoots.isEmpty()) {
            LOGGER.warn("No asset roots discovered; skipped asset consistency check.");
            return;
        }
        Report report = new Report();
        checkItems(assetRoots, report);
        checkBlocks(assetRoots, report);
        report.dump();
        report.writeToFile(FMLPaths.GAMEDIR.get().resolve("asset-report.txt"));
    }

    private static List<Path> discoverAssetRoots() {
        Path gameDir = FMLPaths.GAMEDIR.get();
        List<Path> roots = new ArrayList<>();
        Path mainAssets = gameDir.resolve("src/main/resources/assets/hbm");
        if (Files.isDirectory(mainAssets)) {
            roots.add(mainAssets);
        }
        Path generatedAssets = gameDir.resolve("src/generated/resources/assets/hbm");
        if (Files.isDirectory(generatedAssets)) {
            roots.add(generatedAssets);
        }
        Path runtimeAssets = gameDir.resolve("assets/hbm");
        if (Files.isDirectory(runtimeAssets)) {
            roots.add(runtimeAssets);
        }
        LOGGER.info("HBM asset checker roots: {}", roots);
        return roots;
    }

    private static void checkItems(List<Path> roots, Report report) {
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            if (id == null || !HBM.MODID.equals(id.getNamespace())) {
                continue;
            }
            String name = id.getPath();
            Path modelPath = findAsset(roots, Path.of("models", "item", name + ".json"));
            if (modelPath == null) {
                report.missingItemModels.add(name);
                continue;
            }
            JsonObject modelJson = readJson(modelPath, report);
            if (modelJson == null) {
                continue;
            }
            JsonObject texturesObj = modelJson.getAsJsonObject("textures");
            if (texturesObj == null || !texturesObj.has("layer0")) {
                report.itemModelMissingTexture.add(name + " -> layer0");
                continue;
            }
            String texture = texturesObj.get("layer0").getAsString();
            Path texturePath = resolveTexturePath(texture, "item");
            if (texturePath == null || findAsset(roots, texturePath) == null) {
                report.missingItemTextures.add(name + " -> " + texture);
            }
        }
    }

    private static void checkBlocks(List<Path> roots, Report report) {
        for (Block block : ForgeRegistries.BLOCKS) {
            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
            if (id == null || !HBM.MODID.equals(id.getNamespace())) {
                continue;
            }
            String name = id.getPath();
            Path blockstatePath = findAsset(roots, Path.of("blockstates", name + ".json"));
            if (blockstatePath == null) {
                report.missingBlockstates.add(name);
                continue;
            }
            JsonObject blockstateJson = readJson(blockstatePath, report);
            if (blockstateJson == null) {
                continue;
            }
            Set<String> models = collectModelReferences(blockstateJson);
            if (models.isEmpty()) {
                report.blockstateMissingModels.add(name);
                continue;
            }
            for (String model : models) {
                Path modelPath = resolveModelPath(model);
                if (modelPath == null) {
                    report.invalidBlockModelRefs.add(name + " -> " + model);
                    continue;
                }
                Path absoluteModel = findAsset(roots, modelPath);
                if (absoluteModel == null) {
                    report.missingBlockModels.add(model);
                    continue;
                }
                JsonObject modelJson = readJson(absoluteModel, report);
                if (modelJson == null) {
                    continue;
                }
                JsonObject texturesObj = modelJson.getAsJsonObject("textures");
                if (texturesObj == null) {
                    continue;
                }
                for (Map.Entry<String, JsonElement> entry : texturesObj.entrySet()) {
                    if (!entry.getValue().isJsonPrimitive()) {
                        continue;
                    }
                    String texture = entry.getValue().getAsString();
                    if (!texture.startsWith(HBM.MODID + ":")) {
                        continue;
                    }
                    Path texturePath = resolveTexturePath(texture, "block");
                    if (texturePath == null || findAsset(roots, texturePath) == null) {
                        report.missingBlockTextures.add(model + " -> " + texture);
                    }
                }
            }
        }
    }

    private static Set<String> collectModelReferences(JsonElement element) {
        Set<String> models = new HashSet<>();
        if (element == null) {
            return models;
        }
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("model") && obj.get("model").isJsonPrimitive()) {
                models.add(obj.get("model").getAsString());
            }
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                models.addAll(collectModelReferences(entry.getValue()));
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                models.addAll(collectModelReferences(child));
            }
        }
        return models;
    }

    private static JsonObject readJson(Path path, Report report) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception ex) {
            report.failedToReadFiles.add(path.toString() + " -> " + ex.getMessage());
            return null;
        }
    }

    private static Path resolveModelPath(String namespacePath) {
        if (!namespacePath.startsWith(HBM.MODID + ":")) {
            return null;
        }
        String value = namespacePath.substring(HBM.MODID.length() + 1);
        if (!value.startsWith("block/")) {
            return null;
        }
        return Path.of("models", value + ".json");
    }

    private static Path resolveTexturePath(String namespacePath, String expectedFolder) {
        if (!namespacePath.startsWith(HBM.MODID + ":")) {
            return null;
        }
        String value = namespacePath.substring(HBM.MODID.length() + 1);
        if (!value.startsWith(expectedFolder + "/")) {
            return null;
        }
        return Path.of("textures", value + ".png");
    }

    private static Path findAsset(List<Path> roots, Path relative) {
        for (Path root : roots) {
            Path candidate = root.resolve(relative);
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private static final class Report {
        private final List<String> missingItemModels = new ArrayList<>();
        private final List<String> itemModelMissingTexture = new ArrayList<>();
        private final List<String> missingItemTextures = new ArrayList<>();
        private final List<String> missingBlockstates = new ArrayList<>();
        private final List<String> blockstateMissingModels = new ArrayList<>();
        private final List<String> invalidBlockModelRefs = new ArrayList<>();
        private final List<String> missingBlockModels = new ArrayList<>();
        private final List<String> missingBlockTextures = new ArrayList<>();
        private final List<String> failedToReadFiles = new ArrayList<>();

        private void dump() {
            if (isClean()) {
                LOGGER.info("HBM asset checker: no inconsistencies found.");
                return;
            }
            logList("Missing item models", missingItemModels);
            logList("Item models missing textures", itemModelMissingTexture);
            logList("Missing item textures", missingItemTextures);
            logList("Missing blockstates", missingBlockstates);
            logList("Blockstates without model references", blockstateMissingModels);
            logList("Blockstates referencing invalid models", invalidBlockModelRefs);
            logList("Missing block models", missingBlockModels);
            logList("Missing block textures", missingBlockTextures);
            logList("Failed to read JSON files", failedToReadFiles);
        }

        private void logList(String title, List<String> entries) {
            if (entries.isEmpty()) {
                return;
            }
            LOGGER.warn("{} ({}):", title, entries.size());
            entries.stream().sorted().forEach(entry -> LOGGER.warn(" -> {}", entry));
        }

        private boolean isClean() {
            return missingItemModels.isEmpty()
                && itemModelMissingTexture.isEmpty()
                && missingItemTextures.isEmpty()
                && missingBlockstates.isEmpty()
                && blockstateMissingModels.isEmpty()
                && invalidBlockModelRefs.isEmpty()
                && missingBlockModels.isEmpty()
                && missingBlockTextures.isEmpty()
                && failedToReadFiles.isEmpty();
        }

        private void writeToFile(Path output) throws IOException {
            List<String> lines = new ArrayList<>();
            lines.add("HBM Asset Consistency Report");
            lines.add("----------------------------");
            lines.add("");
            appendSection(lines, "Missing item models", missingItemModels);
            appendSection(lines, "Item models missing textures", itemModelMissingTexture);
            appendSection(lines, "Missing item textures", missingItemTextures);
            appendSection(lines, "Missing blockstates", missingBlockstates);
            appendSection(lines, "Blockstates without model references", blockstateMissingModels);
            appendSection(lines, "Blockstates referencing invalid models", invalidBlockModelRefs);
            appendSection(lines, "Missing block models", missingBlockModels);
            appendSection(lines, "Missing block textures", missingBlockTextures);
            appendSection(lines, "Failed to read JSON files", failedToReadFiles);
            Files.write(output, lines);
            LOGGER.info("HBM asset checker wrote {}", output.toAbsolutePath());
        }

        private void appendSection(List<String> lines, String title, List<String> entries) {
            lines.add(title + " (" + entries.size() + ")");
            if (entries.isEmpty()) {
                lines.add("  OK");
            } else {
                lines.addAll(entries.stream().sorted().map(s -> "  - " + s).collect(Collectors.toList()));
            }
            lines.add("");
        }
    }
}
