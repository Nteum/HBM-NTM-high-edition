#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SCRIPT_NAME="$(basename "${BASH_SOURCE[0]}")"

LEVEL=""
DRY_RUN=false
ONLY_TARGETS_RAW=""
GRADLE_STOPPED=false

usage() {
    cat <<USAGE
用法:
  tools/${SCRIPT_NAME} [low|medium|high] [--dry-run] [--only 目标1,目标2]
  tools/${SCRIPT_NAME} --list-targets

清理强度:
  low     仅清理轻量垃圾: 日志、崩溃报告、profile、临时文件、Python 缓存、.DS_Store
  medium  在 low 基础上，额外清理构建产物、生成缓存、run-data 输出、项目内 Gradle 缓存
  high    在 medium 基础上，额外清理全局 Gradle 缓存 (~/.gradle/caches, daemon, native, wrapper/dists)

选项:
  --dry-run             只预览将要清理的内容，不真正删除
  --only 名称列表       仅执行指定目标，多个目标用逗号分隔
  --list-targets        列出所有可单独执行的清理目标
  -h, --help            显示帮助

说明:
  - 每一类清理都会要求两次确认，必须连续输入 y 才会执行。
  - 默认不会删除: .git, src, run/saves, run/screenshots, run/backups, run/config, run/mods, run/resourcepacks。
  - low 不再删除 build/ 或任何 Gradle 缓存。
USAGE
}

list_targets() {
    cat <<'TARGETS'
可用清理目标:
  build_outputs         构建产物(build/out)
  bin_outputs           Bin 编译产物
  generated_reports     生成缓存与报告
  runtime_logs          运行日志与崩溃报告
  runtime_temp          运行临时文件
  pycache               Python __pycache__
  ds_store              Finder .DS_Store
  project_gradle        项目内 Gradle 缓存
  rundata_outputs       run-data 可再生输出
  global_gradle_caches  全局 Gradle caches
  global_gradle_runtime 全局 Gradle daemon/native
  global_gradle_wrapper 全局 Gradle wrapper dists
TARGETS
}

if [[ ! -f "$PROJECT_ROOT/gradlew" ]] || [[ ! -f "$PROJECT_ROOT/build.gradle" ]]; then
    echo "错误: 未识别到项目根目录: $PROJECT_ROOT" >&2
    exit 1
fi

while [[ $# -gt 0 ]]; do
    case "$1" in
        low|medium|high|LOW|MEDIUM|HIGH|1|2|3)
            LEVEL="$1"
            ;;
        --dry-run)
            DRY_RUN=true
            ;;
        --only)
            shift
            if [[ $# -eq 0 ]]; then
                echo "错误: --only 需要参数" >&2
                exit 1
            fi
            ONLY_TARGETS_RAW="$1"
            ;;
        --list-targets)
            list_targets
            exit 0
            ;;
        -h|--help|help)
            usage
            exit 0
            ;;
        *)
            echo "未知参数: $1" >&2
            usage
            exit 1
            ;;
    esac
    shift
done

if [[ -z "$LEVEL" ]]; then
    echo "请选择清理强度:"
    echo "  1) low"
    echo "  2) medium"
    echo "  3) high"
    read -r -p "输入 1/2/3 或 low/medium/high: " LEVEL
fi

case "$LEVEL" in
    1|low|LOW)
        LEVEL="low"
        ;;
    2|medium|MEDIUM)
        LEVEL="medium"
        ;;
    3|high|HIGH)
        LEVEL="high"
        ;;
    *)
        echo "未知清理强度: $LEVEL" >&2
        usage
        exit 1
        ;;
esac

normalize_only_targets() {
    if [[ -z "$ONLY_TARGETS_RAW" ]]; then
        echo ""
        return
    fi
    echo ",${ONLY_TARGETS_RAW// /},"
}

level_to_index() {
    case "$1" in
        low) echo 1 ;;
        medium) echo 2 ;;
        high) echo 3 ;;
        *) echo 999 ;;
    esac
}

ONLY_TARGETS_NORMALIZED="$(normalize_only_targets)"
selected_level_index="$(level_to_index "$LEVEL")"

print_header() {
    echo
    echo "== $1 =="
}

should_run_target() {
    local target_id="$1"
    local target_level="$2"
    local target_level_index
    target_level_index="$(level_to_index "$target_level")"

    if [[ -n "$ONLY_TARGETS_RAW" ]]; then
        [[ "$ONLY_TARGETS_NORMALIZED" == *",$target_id,"* ]]
        return
    fi

    [[ "$target_level_index" -le "$selected_level_index" ]]
}

confirm_twice() {
    local label="$1"
    local action_text="清理"
    local answer

    if [[ "$DRY_RUN" == true ]]; then
        action_text="预览清理"
    fi

    read -r -p "确认${action_text} ${label}? (y/n): " answer
    if [[ ! "$answer" =~ ^[Yy]$ ]]; then
        echo "已跳过: ${label}"
        return 1
    fi

    read -r -p "再次确认${action_text} ${label}? (y/n): " answer
    if [[ ! "$answer" =~ ^[Yy]$ ]]; then
        echo "二次确认未通过，已跳过: ${label}"
        return 1
    fi

    return 0
}

collect_existing_paths() {
    local path
    for path in "$@"; do
        [[ -e "$path" ]] && printf '%s\n' "$path"
    done
}

collect_find_files() {
    local search_root="$1"
    local name_pattern="$2"
    [[ -d "$search_root" ]] || return 0
    find "$search_root" -type f -name "$name_pattern" -print 2>/dev/null
}

collect_find_dirs() {
    local search_root="$1"
    local dir_name="$2"
    [[ -d "$search_root" ]] || return 0
    find "$search_root" -type d -name "$dir_name" -print 2>/dev/null
}

fill_array_from_command() {
    local __var_name="$1"
    shift
    local __line

    eval "$__var_name=()"
    while IFS= read -r __line; do
        if [[ -n "$__line" ]]; then
            eval "$__var_name+=(\"\$__line\")"
        fi
    done < <("$@")
}

show_existing_paths() {
    local path
    for path in "$@"; do
        if [[ -e "$path" ]]; then
            du -sh "$path" 2>/dev/null | sed 's#^#  - #' || echo "  - $path"
        fi
    done
}

print_target_summary() {
    local label="$1"
    shift
    local existing=("$@")

    if [[ ${#existing[@]} -eq 0 ]]; then
        echo "  - ${label}: 无内容"
        return
    fi

    local total
    total="$(du -sch "${existing[@]}" 2>/dev/null | awk 'END{print $1}')"
    echo "  - ${label}: ${#existing[@]} 项, 合计 ${total}"
}

clean_entries() {
    local target_id="$1"
    local target_level="$2"
    local label="$3"
    shift 3
    local existing=("$@")

    if ! should_run_target "$target_id" "$target_level"; then
        return 0
    fi

    if [[ ${#existing[@]} -eq 0 ]]; then
        echo "[无内容] ${label}"
        return 0
    fi

    print_header "$label"
    show_existing_paths "${existing[@]}"

    if ! confirm_twice "$label"; then
        return 0
    fi

    if [[ "$DRY_RUN" == true ]]; then
        echo "Dry-run: 未实际删除 ${label}"
        return 0
    fi

    rm -rf "${existing[@]}"
    echo "已清理: ${label}"
}

stop_gradle_once() {
    if [[ "$GRADLE_STOPPED" == true ]]; then
        return 0
    fi

    echo
    echo "停止 Gradle Daemon..."
    (
        cd "$PROJECT_ROOT"
        ./gradlew --stop >/dev/null 2>&1 || true
    )
    GRADLE_STOPPED=true
}

BUILD_OUTPUTS=(
    "$PROJECT_ROOT/build"
    "$PROJECT_ROOT/out"
)
BIN_OUTPUTS=(
    "$PROJECT_ROOT/bin/main"
    "$PROJECT_ROOT/bin/test"
    "$PROJECT_ROOT/bin/generated-sources"
    "$PROJECT_ROOT/bin/generated-test-sources"
)
GENERATED_REPORTS=(
    "$PROJECT_ROOT/src/generated/resources/.cache"
    "$PROJECT_ROOT/reports"
    "$PROJECT_ROOT/logs"
)
RUNTIME_LOGS=(
    "$PROJECT_ROOT/run/logs"
    "$PROJECT_ROOT/run/crash-reports"
    "$PROJECT_ROOT/run/profiles"
    "$PROJECT_ROOT/run-data/logs"
)
RUNTIME_TEMP=(
    "$PROJECT_ROOT/run/tmp_secure"
    "$PROJECT_ROOT/run/usercache.json"
    "$PROJECT_ROOT/run/usernamecache.json"
)
PROJECT_GRADLE=(
    "$PROJECT_ROOT/.gradle"
    "$PROJECT_ROOT/.gradle-home"
)
RUNDATA_OUTPUTS=(
    "$PROJECT_ROOT/run-data/build"
    "$PROJECT_ROOT/run-data/out"
)
GLOBAL_GRADLE_CACHES=(
    "$HOME/.gradle/caches"
)
GLOBAL_GRADLE_RUNTIME=(
    "$HOME/.gradle/daemon"
    "$HOME/.gradle/native"
)
GLOBAL_GRADLE_WRAPPER=(
    "$HOME/.gradle/wrapper/dists"
)

declare -a BUILD_OUTPUTS_EXISTING=()
declare -a BIN_OUTPUTS_EXISTING=()
declare -a GENERATED_REPORTS_EXISTING=()
declare -a RUNTIME_LOGS_EXISTING=()
declare -a RUNTIME_TEMP_EXISTING=()
declare -a PYCACHE_EXISTING=()
declare -a DS_STORE_EXISTING=()
declare -a PROJECT_GRADLE_EXISTING=()
declare -a RUNDATA_OUTPUTS_EXISTING=()
declare -a GLOBAL_GRADLE_CACHES_EXISTING=()
declare -a GLOBAL_GRADLE_RUNTIME_EXISTING=()
declare -a GLOBAL_GRADLE_WRAPPER_EXISTING=()

fill_array_from_command BUILD_OUTPUTS_EXISTING collect_existing_paths "${BUILD_OUTPUTS[@]}"
fill_array_from_command BIN_OUTPUTS_EXISTING collect_existing_paths "${BIN_OUTPUTS[@]}"
fill_array_from_command GENERATED_REPORTS_EXISTING collect_existing_paths "${GENERATED_REPORTS[@]}"
fill_array_from_command RUNTIME_LOGS_EXISTING collect_existing_paths "${RUNTIME_LOGS[@]}"
fill_array_from_command RUNTIME_TEMP_EXISTING collect_existing_paths "${RUNTIME_TEMP[@]}"
fill_array_from_command PYCACHE_EXISTING collect_find_dirs "$PROJECT_ROOT" "__pycache__"
fill_array_from_command DS_STORE_EXISTING collect_find_files "$PROJECT_ROOT" ".DS_Store"
fill_array_from_command PROJECT_GRADLE_EXISTING collect_existing_paths "${PROJECT_GRADLE[@]}"
fill_array_from_command RUNDATA_OUTPUTS_EXISTING collect_existing_paths "${RUNDATA_OUTPUTS[@]}"
fill_array_from_command GLOBAL_GRADLE_CACHES_EXISTING collect_existing_paths "${GLOBAL_GRADLE_CACHES[@]}"
fill_array_from_command GLOBAL_GRADLE_RUNTIME_EXISTING collect_existing_paths "${GLOBAL_GRADLE_RUNTIME[@]}"
fill_array_from_command GLOBAL_GRADLE_WRAPPER_EXISTING collect_existing_paths "${GLOBAL_GRADLE_WRAPPER[@]}"

print_header "项目垃圾清理"
echo "项目目录: $PROJECT_ROOT"
echo "清理强度: $LEVEL"
if [[ "$DRY_RUN" == true ]]; then
    echo "模式: dry-run"
else
    echo "模式: 真实删除"
fi
echo "默认保留: run/saves, run/screenshots, run/backups, run/config, run/mods, run/resourcepacks, src, .git"
if [[ -n "$ONLY_TARGETS_RAW" ]]; then
    echo "仅执行目标: $ONLY_TARGETS_RAW"
fi

print_header "清理总览"
set +u
print_target_summary "构建产物(build/out)" "${BUILD_OUTPUTS_EXISTING[@]}"
print_target_summary "Bin 编译产物" "${BIN_OUTPUTS_EXISTING[@]}"
print_target_summary "生成缓存与报告" "${GENERATED_REPORTS_EXISTING[@]}"
print_target_summary "运行日志与崩溃报告" "${RUNTIME_LOGS_EXISTING[@]}"
print_target_summary "运行临时文件" "${RUNTIME_TEMP_EXISTING[@]}"
print_target_summary "Python __pycache__" "${PYCACHE_EXISTING[@]}"
print_target_summary "Finder .DS_Store" "${DS_STORE_EXISTING[@]}"
if [[ "$selected_level_index" -ge 2 ]] || [[ -n "$ONLY_TARGETS_RAW" ]]; then
    print_target_summary "项目内 Gradle 缓存" "${PROJECT_GRADLE_EXISTING[@]}"
    print_target_summary "run-data 可再生输出" "${RUNDATA_OUTPUTS_EXISTING[@]}"
fi
if [[ "$selected_level_index" -ge 3 ]] || [[ -n "$ONLY_TARGETS_RAW" ]]; then
    print_target_summary "全局 Gradle caches" "${GLOBAL_GRADLE_CACHES_EXISTING[@]}"
    print_target_summary "全局 Gradle daemon/native" "${GLOBAL_GRADLE_RUNTIME_EXISTING[@]}"
    print_target_summary "全局 Gradle wrapper dists" "${GLOBAL_GRADLE_WRAPPER_EXISTING[@]}"
fi
set -u

set +u
clean_entries "build_outputs" "medium" "构建产物(build/out)" "${BUILD_OUTPUTS_EXISTING[@]}"
clean_entries "bin_outputs" "medium" "Bin 编译产物" "${BIN_OUTPUTS_EXISTING[@]}"
clean_entries "generated_reports" "medium" "生成缓存与报告" "${GENERATED_REPORTS_EXISTING[@]}"
clean_entries "runtime_logs" "low" "运行日志与崩溃报告" "${RUNTIME_LOGS_EXISTING[@]}"
clean_entries "runtime_temp" "low" "运行临时文件" "${RUNTIME_TEMP_EXISTING[@]}"
clean_entries "pycache" "low" "Python __pycache__" "${PYCACHE_EXISTING[@]}"
clean_entries "ds_store" "low" "Finder .DS_Store" "${DS_STORE_EXISTING[@]}"

if should_run_target "project_gradle" "medium" || should_run_target "global_gradle_caches" "high" || should_run_target "global_gradle_runtime" "high" || should_run_target "global_gradle_wrapper" "high"; then
    stop_gradle_once
fi

clean_entries "project_gradle" "medium" "项目内 Gradle 缓存" "${PROJECT_GRADLE_EXISTING[@]}"
clean_entries "rundata_outputs" "medium" "运行数据工作目录(run-data)中的可再生输出" "${RUNDATA_OUTPUTS_EXISTING[@]}"
clean_entries "global_gradle_caches" "high" "全局 Gradle caches" "${GLOBAL_GRADLE_CACHES_EXISTING[@]}"
clean_entries "global_gradle_runtime" "high" "全局 Gradle daemon/native" "${GLOBAL_GRADLE_RUNTIME_EXISTING[@]}"
clean_entries "global_gradle_wrapper" "high" "全局 Gradle wrapper dists" "${GLOBAL_GRADLE_WRAPPER_EXISTING[@]}"
set -u

echo
echo "清理流程结束。"
