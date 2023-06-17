package me.mantou.breadmachine.kook.command.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.kook.command.CSender;
import me.mantou.breadmachine.kook.command.annotation.CmdParam;
import snw.jkook.message.Message;
import snw.jkook.message.TextChannelMessage;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
public class CmdExecutor {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private final List<ParamType> paramClasses = new LinkedList<>();
    private final MethodHandle executorMethod;
    private final Object commandInstance;

    @SneakyThrows
    public CmdExecutor(Object commandInstance, Method method) {
        for (Parameter parameter : method.getParameters()) {
            CmdParam cmdParamAnno = parameter.getAnnotation(CmdParam.class);
            String value = null;
            if (cmdParamAnno != null) value = cmdParamAnno.value();
            paramClasses.add(new ParamType(parameter.getType(), value));
        }

        this.commandInstance = commandInstance;
        executorMethod = LOOKUP.findVirtual(method.getDeclaringClass(), method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()));
    }

    @SneakyThrows
    public void execute(CSender sender, Map<String, Object> params) {
        List<Object> objects = new LinkedList<>();
        objects.add(commandInstance);

        List<Object> extraParams = new LinkedList<>();

        extraParams.add(sender);
        extraParams.add(sender.getJKookSender());
        Message message = sender.getMessage();
        if (message != null){
            if (message instanceof TextChannelMessage channelMessage) {
                extraParams.add(channelMessage.getChannel());
                extraParams.add(channelMessage.getChannel().getGuild());
            }
        }

        paramClasses.forEach(paramType -> {
            Object value = null;

            if (!params.containsKey(paramType.getKey())){

                for (Object extraParam : extraParams) {
                    if (paramType.isSame(extraParam)){
                        value = extraParam;
                        break;
                    }
                }

                if (value == null) log.warn("{}参数 {} 不存在", executorMethod.type().toString(), paramType);
            }else {
                value = params.get(paramType.key);
            }

            objects.add(value);
        });

        executorMethod.invokeWithArguments(objects);
    }

    @Data
    @AllArgsConstructor
    private static class ParamType{
        private final Class<?> type;
        private final String key;
        //TODO 解析器

        public boolean isSame(Object o){
            return type.isAssignableFrom(o.getClass());
        }
    }
}
