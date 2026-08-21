package com.hbm.api.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 富文本解析器。
 *
 * 背景：原版 hbm 的说明文本有大量"一堆字中间某个词高亮"的情况，需要大量字符串拼接
 * （例如 I18nUtil.resolveKey(...) + EnumColor.RED + ... + EnumColor.WHITE）。
 * 本类允许在 lang 文件中直接用轻量标签标注样式，渲染时自动转换为带样式的 Component：
 *
 * 支持的标签（可嵌套，先开先关）：
 * - &lt;color&gt; 任意命名颜色（chat formatting 名，如 red/gold/aqua，或 #RRGGBB）
 *   例如 &lt;red&gt;警告&lt;/red&gt;
 * - &lt;b&gt;粗体&lt;/b&gt;、&lt;i&gt;斜体&lt;/i&gt;、&lt;u&gt;下划线&lt;/u&gt;、&lt;s&gt;删除线&lt;/s&gt;
 * - &lt;obf&gt;混淆&lt;/obf&gt;
 *
 * 示例 lang 条目：
 *   "hbm.machine.x.desc": "这台机器会&lt;red&gt;消耗大量电力&lt;/red&gt;，请注意&lt;b&gt;散热&lt;/b&gt;。"
 *
 * 用法：
 *   RichText.parse("这是一段&lt;gold&gt;金色&lt;/gold&gt;文字")   → MutableComponent
 *   RichText.parseLang(key, args...)                        → 从 lang 取值并解析
 */
public class RichText {
    private static final Pattern TAG = Pattern.compile("<(/)?([a-zA-Z0-9_#]+)>");

    private RichText(){}

    /** 解析含标签的字符串为 MutableComponent。未闭合标签自动关闭。 */
    public static MutableComponent parse(String input){
        MutableComponent result = Component.literal("");
        Deque<Style> styleStack = new ArrayDeque<>();
        Style currentStyle = Style.EMPTY;
        StringBuilder text = new StringBuilder();
        Matcher matcher = TAG.matcher(input);
        int lastEnd = 0;

        while (matcher.find()){
            // 累积标签前的普通文本
            if (matcher.start() > lastEnd){
                text.append(input, lastEnd, matcher.start());
            }
            boolean closing = matcher.group(1) != null;
            String tag = matcher.group(2).toLowerCase(Locale.ROOT);

            if (!closing){
                // 开标签：先把已积累的文本输出到当前样式
                flush(result, text, currentStyle);
                Style next = applyTag(currentStyle, tag);
                styleStack.push(currentStyle);
                currentStyle = next;
            }else {
                // 闭标签：输出文本后恢复上一层样式
                flush(result, text, currentStyle);
                if (!styleStack.isEmpty()){
                    currentStyle = styleStack.pop();
                }else {
                    currentStyle = Style.EMPTY;
                }
            }
            lastEnd = matcher.end();
        }
        // 收尾文本
        if (lastEnd < input.length()){
            text.append(input, lastEnd, input.length());
        }
        flush(result, text, currentStyle);
        return result;
    }

    /** 从 lang 键取字符串并解析为富文本 */
    public static MutableComponent parseLang(String key, Object... args){
        String raw = net.minecraft.client.resources.language.I18n.get(key, args);
        return parse(raw);
    }

    /** 将积累的文本按当前样式追加到组件 */
    private static void flush(MutableComponent result, StringBuilder text, Style style){
        if (text.length() == 0) return;
        result.append(Component.literal(text.toString()).withStyle(style));
        text.setLength(0);
    }

    /** 将标签应用到样式上，返回新样式 */
    private static Style applyTag(Style style, String tag){
        switch (tag){
            case "b": return style.withBold(true);
            case "i": return style.withItalic(true);
            case "u": return style.withUnderlined(true);
            case "s": return style.withStrikethrough(true);
            case "obf": return style.withObfuscated(true);
            default:
                // 命名颜色或 #RRGGBB
                if (tag.startsWith("#")){
                    try {
                        return style.withColor(TextColor.parseColor(tag));
                    }catch (Exception ignored){
                        return style;
                    }
                }
                try {
                    ChatFormatting formatting = ChatFormatting.getByName(tag);
                    if (formatting != null && formatting.isColor()){
                        return style.withColor(TextColor.fromLegacyFormat(formatting));
                    }
                }catch (Exception ignored){
                }
                return style;
        }
    }
}
