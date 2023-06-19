package me.mantou.breadmachine.core.command.wrapper.pb;

public class NullDefParamBuilder implements DefParamBuilder {

    @Override
    public Object buildDefault() {
        return Void.TYPE;
    }
}
