# Legacy Consumable & Control Tab Migration

## Source of Truth
- Extracted automatically from `Hbm-s-Nuclear-Tech-GIT/src/main/java` by scanning every `setCreativeTab(MainRegistry.consumableTab | controlTab)` assignment.
- Manifest: `tools/legacy_tab_manifest.json` — 429 records (146 consumable, 283 control) with `{name, tab, class, file, line}`.
- Aggregated counts: `tools/legacy_tab_summary.json`.

## High-Level Counts
| Tab            | Entries |
|----------------|---------|
| consumableTab  | 146     |
| controlTab     | 283     |

| Core Class (excerpt) | Count | Notes / Target Package |
|----------------------|-------|------------------------|
| `Item` (plain)       | 75    | Mostly simple collectibles; map to existing `parts/consumable/control` builders. |
| `ItemCustomLore`     | 15    | Requires tooltip text + rarity; keep under `consumable`. |
| `ItemLemon`          | 17    | Shared food behavior; port class to `com.hbm.item.consumable`. |
| `ItemPill`           | 10    | Medication effects; needs status effect hooks. |
| `ItemStarterKit`     | 8     | Preloaded loot; adapt to new loot injection format. |
| `ItemSyringe`        | 7     | 3D render + potion payload. |
| `BlockCrate` family  | 6     | Includes ammo/can/jungle variants; bring block/entity + loot tables. |
| `ArmorModel`         | 4     | Cosmetic capes; convert to Forge armor renderers. |
| `ItemRepairKit`      | 2     | Gun repair values; depends on weapon API. |
| `ItemAmmoBag`        | 2     | Inventory helper; needs capability port. |

Full class histogram lives in `tools/legacy_tab_summary.json`.

## Proposed Migration Order
1. **Shared foundations**: port reusable classes (`ItemPill`, `ItemLemon`, `ItemCustomLore`, `ItemStarterKit`, kit/tool base classes) into `com.hbm.item.consumable`/`tool`. Hook up creative tabs + language entries.
2. **Food & drink batch**: apples, soups, cereal, canned goods, marshmallow, pancake, glyphid meat, etc. Ensure saturation/effects preserved.
3. **Chemicals, meds, syringes**: Rad-X, SIOX, syringes, med bags, stealth boy. Requires status effect framework + capability ties.
4. **Utility kits & bags**: ammo/casing bags, plastic/lead containers, containment boxes, toolbox/legacy toolbox.
5. **Scanners & detectors**: dosimeter, geiger, reactor sensor, oil detector, survey scanner, mirror tool, RBMK tool, etc. Each may need GUI/tooltips.
6. **Keys, locks, linkage tools**: key kit, padlocks, linking tools, coupling/drone/radar tools, teleporter linker.
7. **Crates & crafting stations**: weapon table, armor table, key forge, crates (plus loot, block entities, models).
8. **Control tab combat/util items**: multi-tools, bottle opener, wands, remote controls, spawn callers, bomb caller, rod of discord.
9. **Special collectibles**: coins, polaroids, glitch book, bobmazon, guide book.
10. **Arc Furnace (multi-block) & Exposure Chamber**: migrate blocks, block entities, GUI, renderers, recipe handlers, JEI integration. Ensure OBJ models (`ResourceManager.arc_furnace`, `arc_welder`, `exposure_chamber`) convert to modern `BlockEntityRenderer`.

Each batch completion => tick entries off manifest (scriptable). Final validation: rerun manifest scan against new code (should yield zero refs to legacy tabs) + `./gradlew --no-daemon build`.
