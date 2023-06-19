package me.mantou.breadmachine.core.command.wrapper.node;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public abstract class ParamNode {
    @Setter
    private Class<?> paramType = String.class;
    private final String key;
}
