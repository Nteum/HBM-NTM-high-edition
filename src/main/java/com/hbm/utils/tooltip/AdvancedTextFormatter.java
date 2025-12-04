package com.hbm.utils.tooltip;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvancedTextFormatter {
    /**
     * 解析富文本标记，支持颜色、样式等
     * 语法示例: "这是一段{red}红色{/}文字，中间有{yellow}黄色{/}词汇"
     */
    public static List<Component> parseFormattedText(String text) {
        List<Component> components = new ArrayList<>();
        StringBuilder currentText = new StringBuilder();
        Style currentStyle = Style.EMPTY;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '{' && i + 1 < text.length()) {
                // 处理标记
                int endIndex = text.indexOf('}', i);
                if (endIndex != -1) {
                    String tag = text.substring(i + 1, endIndex);

                    // 添加当前文本
                    if (!currentText.isEmpty()) {
                        components.add(Component.literal(currentText.toString()).withStyle(currentStyle));
                        currentText.setLength(0);
                    }

                    // 处理标记
                    if (tag.startsWith("/")) {
                        currentStyle = Style.EMPTY; // 重置样式
                    } else {
                        currentStyle = parseStyleTag(tag);
                    }

                    i = endIndex; // 跳过标记
                    continue;
                }
            }

            currentText.append(c);
        }

        // 添加最后一段文本
        if (!currentText.isEmpty()) {
            components.add(Component.literal(currentText.toString()).withStyle(currentStyle));
        }

        return components;
    }

    private static Style parseStyleTag(String tag) {
        Style style = Style.EMPTY;

        // 尝试解析十六进制颜色
        // 忽略错误
        return switch (tag.toLowerCase()) {
            case "red" -> style.withColor(TextColor.fromRgb(0xFF5555));
            case "yellow" -> style.withColor(TextColor.fromRgb(0xFFFF55));
            case "green" -> style.withColor(TextColor.fromRgb(0x55FF55));
            case "blue" -> style.withColor(TextColor.fromRgb(0x5555FF));
            case "gold" -> style.withColor(TextColor.fromRgb(0xFFAA00));
            case "gray" -> style.withColor(TextColor.fromRgb(0xAAAAAA));
            case "dark_red" -> style.withColor(TextColor.fromRgb(0xAA0000));
            case "bold" -> style.withBold(true);
            case "italic" -> style.withItalic(true);
            case "underline" -> style.withUnderlined(true);
            default -> {
                if (tag.startsWith("#") && tag.length() == 7) {
                    try {
                        int color = Integer.parseInt(tag.substring(1), 16);
                        yield style.withColor(TextColor.fromRgb(color));
                    } catch (NumberFormatException e) {
                        // 忽略错误
                    }
                }
                yield style;
            }
        };
    }

    /**
     * 更强大的标记解析器，支持嵌套样式
     */
    public static Component parseAdvancedMarkup(String markup) {
        return parseMarkupRecursive(markup, 0, markup.length(), Style.EMPTY);
    }

    private static Component parseMarkupRecursive(String markup, int start, int end, Style parentStyle) {
        MutableComponent result = Component.literal("");
        StringBuilder currentText = new StringBuilder();

        for (int i = start; i < end; i++) {
            char c = markup.charAt(i);

            if (c == '{' && i + 1 < end) {
                int tagEnd = markup.indexOf('}', i);
                if (tagEnd != -1) {
                    String tag = markup.substring(i + 1, tagEnd);

                    // 添加当前文本
                    if (!currentText.isEmpty()) {
                        result.append(Component.literal(currentText.toString()).withStyle(parentStyle));
                        currentText.setLength(0);
                    }

                    if (tag.startsWith("/")) {
                        // 结束标签
                        break;
                    } else {
                        // 开始标签 - 递归解析
                        int contentStart = tagEnd + 1;
                        int contentEnd = findMatchingEndTag(markup, tag, contentStart);

                        if (contentEnd != -1) {
                            Style newStyle = applyStyleTag(parentStyle, tag);
                            Component nested = parseMarkupRecursive(markup, contentStart, contentEnd, newStyle);
                            result.append(nested);
                            i = contentEnd + tag.length() + 2; // </tag>
                        }
                    }
                    continue;
                }
            }

            currentText.append(c);
        }

        // 添加剩余文本
        if (!currentText.isEmpty()) {
            result.append(Component.literal(currentText.toString()).withStyle(parentStyle));
        }

        return result;
    }

    private static int findMatchingEndTag(String markup, String tag, int start) {
        String endTag = "{/" + tag + "}";
        return markup.indexOf(endTag, start);
    }

    private static Style applyStyleTag(Style currentStyle, String tag) {
        // 类似于 parseStyleTag 但应用于现有样式
        return parseStyleTag(tag).applyTo(currentStyle);
    }

    /**
     * 解析支持本地化的富文本标记
     * 支持混合使用翻译键和直接文本
     */
    public static Component parseLocalizedMarkup(String markup) {
        return parseMarkupRecursive(markup, 0, markup.length(), Style.EMPTY, new ArrayList<>());
    }

    private static Component parseMarkupRecursive(String markup, int start, int end, Style parentStyle, List<Object> args) {
        MutableComponent result = Component.empty();
        StringBuilder currentText = new StringBuilder();
        Style currentStyle = parentStyle;

        for (int i = start; i < end; i++) {
            char c = markup.charAt(i);

            if (c == '<' && i + 1 < end) {
                int tagEnd = markup.indexOf('>', i);
                if (tagEnd != -1) {
                    String tag = markup.substring(i + 1, tagEnd);

                    // 添加当前文本
                    if (!currentText.isEmpty()) {
                        result.append(createTextComponent(currentText.toString(), currentStyle, args));
                        currentText.setLength(0);
                    }

                    if (tag.startsWith("/")) {
                        // 结束标签
                        break;
                    } else if (tag.startsWith("tr:")) {
                        // 翻译键标签: <tr:key.name>
                        String translationKey = tag.substring(3);
                        result.append(Component.translatable(translationKey).withStyle(currentStyle));
                    } else if (tag.startsWith("arg:")) {
                        // 参数标签: <arg:0>, <arg:1>
                        int argIndex = Integer.parseInt(tag.substring(4));
                        if (argIndex < args.size()) {
                            Object arg = args.get(argIndex);
                            if (arg instanceof Component) {
                                result.append(((Component) arg).copy().withStyle(currentStyle));
                            } else {
                                result.append(Component.literal(arg.toString()).withStyle(currentStyle));
                            }
                        }
                    } else {
                        // 样式标签
                        currentStyle = applyStyleTag(currentStyle, tag);
                    }

                    i = tagEnd;
                    continue;
                }
            }

            currentText.append(c);
        }

        // 添加剩余文本
        if (!currentText.isEmpty()) {
            result.append(createTextComponent(currentText.toString(), currentStyle, args));
        }

        return result;
    }

    private static Component createTextComponent(String text, Style style, List<Object> args) {
        // 检查是否为翻译键（简单启发式：包含点号且没有空格）
        if (text.contains(".") && !text.contains(" ")) {
            return Component.translatable(text).withStyle(style);
        } else {
            return Component.literal(text).withStyle(style);
        }
    }

    /**
     * 更高级的解析方法，支持参数和嵌套
     */
    public static Component parseAdvancedMarkup(String markup, Object... args) {
        List<Object> argList = Arrays.asList(args);
        return parseMarkupRecursive(markup, 0, markup.length(), Style.EMPTY, argList);
    }
}
