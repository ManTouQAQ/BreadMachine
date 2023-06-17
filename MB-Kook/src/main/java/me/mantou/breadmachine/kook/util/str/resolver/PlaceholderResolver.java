package me.mantou.breadmachine.kook.util.str.resolver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;

@RequiredArgsConstructor
@Getter
@Setter
public abstract class PlaceholderResolver {
    private final String prefix;
    private final String suffix;
    private BiPredicate<String, Integer> prefixFilter = (m, i) -> true;
    private BiPredicate<String, Integer> suffixFilter = (m, i) -> true;

    public abstract String getPlaceholderResult(String key);

    private int findPlaceholderStartIndex(CharSequence value, int startIndex){
        String str = String.valueOf(value);
        int i = str.indexOf(prefix, startIndex);

        if (i == -1) return -1;
        if (!prefixFilter.test(str, i)) return findPlaceholderStartIndex(str, i + prefix.length());
        return i;
    }

    private int findPlaceholderEndIndex(CharSequence value, int startIndex) {
        //跳过第一层
        int index = startIndex + prefix.length();
        int withinNestedPlaceholder = 0;

        String str = String.valueOf(value);

        //每个字符依次遍历
        while (index < value.length()) {

            //匹配到suffix了 且是正确的suffix
            if (substringMatch(value, index, suffix) && suffixFilter.test(str, index)) {
                //如果不是最外层 层级-1
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index += suffix.length();
                } else {
                    //get
                    return index;
                }
            } //匹配正确的prefix 如果是则层级+1
            else if (substringMatch(value, index, prefix) && prefixFilter.test(str, index)) {
                withinNestedPlaceholder++;
                index += prefix.length();
            } else {
                index++;
            }
        }
        return -1;
    }

    private boolean substringMatch(CharSequence str, int start, String match) {
        return str.subSequence(start, start + match.length()).equals(match);
    }

    public String getResult(String rawMessage){
        return resolvePlaceholder(rawMessage, new HashSet<>());
    }

    private String resolvePlaceholder(String value, Set<String> cache) {

        int prefixIndex = findPlaceholderStartIndex(value, 0);
        if (prefixIndex == -1) {
            return value;
        }

        StringBuilder builder = new StringBuilder(value);
        while (prefixIndex != -1) {

            int endIndex = findPlaceholderEndIndex(builder, prefixIndex);
            if (endIndex != -1) {

                String key = builder.substring(prefixIndex + prefix.length(), endIndex);
                //解析嵌套的
                key = resolvePlaceholder(key, cache);
                String result = getPlaceholderResult(key);
                //解析结果中的占位符
                if (result != null) {
                    //如果cache存在就跳过
                    if (cache.add(result)) {
                        result = resolvePlaceholder(result, cache);
                    }
                } else { //防止重复解析
                    result = key;
                }
                builder.replace(prefixIndex, endIndex + suffix.length(), result);

                prefixIndex = findPlaceholderStartIndex(builder, prefixIndex + result.length());
            } else {
                prefixIndex = -1;
            }
        }
        return builder.toString();
    }
}
