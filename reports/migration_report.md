# HBM-Modernized Migration Audit (Dec 29, 2025)

## Coverage Summary

| Type  | Old Count | Ported (matched) | Missing | Coverage |
|-------|-----------|------------------|---------|----------|
| Items | 540       | 253              | 287     | 46.9%    |
| Blocks| 391       | 5                | 386     | 1.3%     |
| Fluids| 3         | 0                | 3       | 0%       |

The per-ID status (including `mapped_to` hints for renamed IDs) lives in `reports/migration_report.json`. A compact numeric view is in `reports/migration_summary.json`. Newly-added IDs with no 1:1 legacy match are listed in `reports/migration_new_only.json`.

## Notable Missing Areas

- **Items:** The heaviest gaps come from canned food lines (`canned_*`, 27 IDs), legacy batteries (`battery_*`, 24), armor plates (`plate_*`, 13) and weapon/armor variants (`starmetal_*`, `hazmat_*`, etc.). Legacy airstrike designators and grenade variants are also absent.
- **Blocks:** Decorative/environmental content has not been ported (115 `concrete*`, 28 `brick_*`, 22 `depth_*`, 20 `meteor_*`, 8 `crate_*`). Most barrel variants (6) and Sellafield/nuclear-themed floor blocks are likewise missing.
- **Fluids:** Only the oil family existed in HBM-Modernized (`crude_oil`, `_flowing`, `_source`) and none of those IDs exist yet. The new fork instead registers `oil`, `diesel`, `coolant`, etc., as shown in `reports/migration_new_only.json`.

## Example Mapping

- `actinium_ingot` → `ingot_actinium`
- `plate_steel` → `plate_steel` (direct match)
- `machine_assembler` → `machine_assembler` (one of the few legacy blocks already present)

## Caveats

- Canonical matching only resolves deterministic reorderings (e.g., `uranium_ingot` → `ingot_uranium`). If multiple new IDs share the same token set the entry remains flagged as missing.
- Old block/recipe assets inside `tools/HBM-Modernized` include far more decorative content than the current fork exposes; this audit highlights that delta but does not attempt to auto-port resources.
