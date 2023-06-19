package me.mantou.breadmachine.core.command.wrapper.node;

import lombok.SneakyThrows;
import me.mantou.breadmachine.core.command.wrapper.pb.DefParamBuilder;
import me.mantou.breadmachine.core.command.wrapper.pb.NullDefParamBuilder;

public class OptionalParamNode extends ParamNode {
    private DefParamBuilder defParamBuilder = new NullDefParamBuilder();

    @SneakyThrows
    public void setDefParamBuilder(Class<? extends DefParamBuilder> paramBuilderClass){
        this.defParamBuilder = paramBuilderClass.getConstructor().newInstance();
    }

    public OptionalParamNode(String key) {
        super(key);
    }

    public Object getDefault() {
        return defParamBuilder.buildDefault();
    }
}
