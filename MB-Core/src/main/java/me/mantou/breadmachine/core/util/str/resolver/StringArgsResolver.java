package me.mantou.breadmachine.core.util.str.resolver;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
public class StringArgsResolver extends PlaceholderResolver {
    private static final String PARAM_PREFIX = "{";
    private static final String PARAM_SUFFIX = "}";
    private final List<Object> indexParams = new LinkedList<>();
    private final Map<String, Object> namedParams = new LinkedHashMap<>();

    {
        setPrefixFilter((str, index) -> index == 0 || str.charAt(index - 1) != '$');
    }

    public StringArgsResolver() {
        super(PARAM_PREFIX, PARAM_SUFFIX);
    }

    public StringArgsResolver(@NotNull Object... params) {
        super(PARAM_PREFIX, PARAM_SUFFIX);
        Arrays.stream(params).forEach(this::addParam);
    }

    public StringArgsResolver addParam(@NotNull Object param) {
        indexParams.add(param);
        return this;
    }

    public StringArgsResolver addParam(String key, @NotNull Object param) {
        namedParams.put(key, param);
        return this;
    }

    public void setParams(Object[] params) {
        indexParams.clear();
        indexParams.addAll(Arrays.asList(params));
    }

    @Override
    public String getPlaceholderResult(String key) {
        if (key.matches("\\d*")) {
            int index = Integer.parseInt(key);
            if (index < indexParams.size()) {
                return paramToString(indexParams.get(index));
            }
        }
        Object o = namedParams.get(key);
        return o == null ? PARAM_PREFIX + key + PARAM_SUFFIX : paramToString(o);
    }

    public String paramToString(Object param) {
        return param.toString();
    }
}
