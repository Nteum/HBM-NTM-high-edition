#!/usr/bin/env bash
set -euo pipefail

usage() {
    cat <<'EOF'
Usage:
  tools/gradle_deep_clean.sh [options]

Options:
  --dry-run                Show what would be removed, but do not delete.
  --with-global-caches     Also remove heavy global Gradle/Forge caches in ~/.gradle.
  -h, --help               Show this help.

Notes:
  - Safe by default: only removes project-local rebuildable outputs.
  - Keeps source code, gradle wrapper files, build scripts, and hand-authored assets.
  - In project gradle/, only non-wrapper extras are removed (if any).
EOF
}

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DRY_RUN=false
WITH_GLOBAL_CACHES=false

while [[ $# -gt 0 ]]; do
    case "$1" in
        --dry-run)
            DRY_RUN=true
            ;;
        --with-global-caches)
            WITH_GLOBAL_CACHES=true
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
            echo "Unknown option: $1" >&2
            usage
            exit 1
            ;;
    esac
    shift
done

if [[ ! -f "$PROJECT_ROOT/gradlew" ]] || [[ ! -f "$PROJECT_ROOT/build.gradle" ]]; then
    echo "Error: project root check failed: $PROJECT_ROOT" >&2
    echo "Run this script from inside the repository." >&2
    exit 1
fi

remove_path() {
    local path="$1"
    if [[ -e "$path" ]]; then
        echo "Remove: $path"
        if [[ "$DRY_RUN" == false ]]; then
            rm -rf "$path"
        fi
    fi
}

clean_project_gradle_extras() {
    local gradle_dir="$PROJECT_ROOT/gradle"
    local entry name

    if [[ ! -d "$gradle_dir" ]]; then
        return
    fi

    echo "Cleaning project gradle/ extras (wrapper is preserved)..."
    shopt -s nullglob dotglob
    for entry in "$gradle_dir"/*; do
        name="$(basename "$entry")"
        if [[ "$name" == "wrapper" ]]; then
            continue
        fi
        remove_path "$entry"
    done
    shopt -u nullglob dotglob
}

echo "Project root: $PROJECT_ROOT"

if [[ -x "$PROJECT_ROOT/gradlew" ]]; then
    echo "Stopping Gradle daemons..."
    if [[ "$DRY_RUN" == false ]]; then
        (
            cd "$PROJECT_ROOT"
            ./gradlew --stop >/dev/null 2>&1 || true
        )
    fi
fi

PROJECT_PATHS=(
    "$PROJECT_ROOT/.gradle"
    "$PROJECT_ROOT/build"
    "$PROJECT_ROOT/out"
    "$PROJECT_ROOT/logs"
    "$PROJECT_ROOT/run"
    "$PROJECT_ROOT/run-data"
    "$PROJECT_ROOT/src/generated/resources/.cache"
    "$PROJECT_ROOT/bin/main"
    "$PROJECT_ROOT/bin/test"
    "$PROJECT_ROOT/bin/generated-sources"
    "$PROJECT_ROOT/bin/generated-test-sources"
)

echo "Cleaning project-local caches and outputs..."
for path in "${PROJECT_PATHS[@]}"; do
    remove_path "$path"
done

clean_project_gradle_extras

if [[ "$WITH_GLOBAL_CACHES" == true ]]; then
    GLOBAL_PATHS=(
        "$HOME/.gradle/caches/8.14.3"
        "$HOME/.gradle/caches/forge_gradle"
        "$HOME/.gradle/daemon/8.14.3"
        "$HOME/.gradle/wrapper/dists/gradle-8.14.3-bin"
        "$HOME/.gradle/wrapper/dists/gradle-8.14.3-all"
    )

    echo "Cleaning optional global Gradle/Forge caches..."
    for path in "${GLOBAL_PATHS[@]}"; do
        remove_path "$path"
    done
fi

if [[ "$DRY_RUN" == true ]]; then
    echo "Dry-run complete."
else
    echo "Deep clean complete."
fi
