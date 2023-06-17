package me.mantou.breadmachine.kook.command.wrapper.pb;

public class NullDefParamBuilder implements DefParamBuilder {

    @Override
    public Object buildDefault() {
        return Void.TYPE;
    }
}
