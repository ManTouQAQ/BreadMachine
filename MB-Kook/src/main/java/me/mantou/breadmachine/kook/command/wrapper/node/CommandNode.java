package me.mantou.breadmachine.kook.command.wrapper.node;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.kook.command.CSender;
import me.mantou.breadmachine.kook.command.CommandSenderWrapper;
import me.mantou.breadmachine.kook.command.annotation.CmdOpt;
import me.mantou.breadmachine.kook.command.annotation.ParamOpt;
import me.mantou.breadmachine.kook.command.wrapper.CmdExecutor;
import org.jetbrains.annotations.Nullable;
import snw.jkook.command.CommandExecutor;
import snw.jkook.command.JKookCommand;
import snw.jkook.message.Message;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Setter
public class CommandNode {
    @Getter
    private String rootValue;
    private final Set<String> aliases = new LinkedHashSet<>();
    private final List<CommandNode> subCommandNodes = new LinkedList<>();
    private final List<ParamNode> paramNodes = new LinkedList<>();
    private CmdExecutor executor;
    private String description = null;
    private String helpMessage = null;
    private CmdOpt.CommandType[] allowTypes = {CmdOpt.CommandType.ALL};
    private boolean paramsMutable = true; //flag
    private CommandWrapper rootWrapper;

    protected CommandNode(){}
    protected void setRootWrapper(CommandWrapper rootWrapper){
        this.rootWrapper = rootWrapper;
    }

    public CommandNode(CommandWrapper rootWrapper, String rootValue) {
        this.rootWrapper = rootWrapper;
        this.rootValue = rootValue;
    }

    public CommandNode addAlias(String alias){
        aliases.add(alias);
        return this;
    }

    public boolean isAllowType(snw.jkook.command.CommandSender sender, Message msg){
        return Arrays.stream(allowTypes).anyMatch(t -> t.canExecute(sender, msg));
    }

//    public boolean execute(CommandSender commandSender, Map<String, Object> params){
//        if (executor == null) return false;
//
//        if (!isAllowType(commandSender.getJKookSender(), commandSender.getMessage())) return false;
//
//        executor.execute(commandSender, params);
//        return true;
//    }

    protected void wrapperMethod(CommandWrapper rootWrapper, List<String> values, Method method, Object commandInstance){
        values = new ArrayList<>(values);
        if (!values.isEmpty()) {
            ParamOpt[] type = method.getAnnotationsByType(ParamOpt.class);
            Map<String, ParamOpt> paramOptMap = Arrays.stream(type).collect(Collectors.toMap(ParamOpt::key, t -> t));
            for (int i = 0; i < values.size(); i++) {
                String value = values.get(i);

                if (value.startsWith("<") && value.endsWith(">")) {
                    if (!paramsMutable){
                        log.warn("不能修改已经确定参数的Node中的参数");
                        continue;
                    }

                    String key = value.substring(1, value.length() - 1);
                    ParamOpt opt = paramOptMap.get(key);

                    RequiredParamNode node = new RequiredParamNode(key);
                    if (opt != null) node.setParamType(opt.type());

                    addParamNode(node);
                }else if (value.startsWith("[") && value.endsWith("]")){
                    if (!paramsMutable){
                        log.warn("不能修改已经确定参数的Node中的参数");
                        continue;
                    }

                    String key = value.substring(1, value.length() - 1);
                    ParamOpt opt = paramOptMap.get(key);
                    OptionalParamNode node = new OptionalParamNode(key);
                    if (opt != null){
                        node.setParamType(opt.type());
                        node.setDefParamBuilder(opt.defaultValue());
                    }

                    addParamNode(node);
                }else {
                    List<String> list = Arrays.stream(value.split("[|]")).toList();

                    if (paramNodes.size() != 0){
                        paramsMutable = false;
                    }

                    removeWithOffset(values, i + 1);

                    for (CommandNode subNodes : subCommandNodes) {
                        if (!subNodes.isThis(value)) continue;
                        subNodes.wrapperMethod(rootWrapper, values, method, commandInstance);
                        return;
                    }
                    CommandNode node = new CommandNode(rootWrapper, list.get(0));

                    addNode(node);
                    for (int j = 1; j < list.size(); j++) {
                        node.addAlias(list.get(j));
                    }
                    node.wrapperMethod(rootWrapper, values, method, commandInstance);
                    return;
                }
            }
        }

        CmdOpt opt = method.getAnnotation(CmdOpt.class);
        if (opt != null){
            this.description = opt.descMsg();
            this.helpMessage = opt.helpMsg();
            this.allowTypes = opt.allowTypes();
        }

        this.executor = new CmdExecutor(commandInstance, method);
    }

    public CommandNode addNode(CommandNode node){
        subCommandNodes.add(node);
        return this;
    }

    public CommandNode addParamNode(ParamNode node){
        paramNodes.add(node);
        return this;
    }

    public boolean isThis(String str){
        return rootValue.equalsIgnoreCase(str) || aliases.stream().anyMatch(s -> s.equalsIgnoreCase(str));
    }

//    public CommandNode getNode(List<Object> command, Map<String, Object> paramContainer){
//        command = new ArrayList<>(command);
//
//        String value = String.valueOf(command.get(0));
//        //如果是它自己
//        if (isThis(value)){
//            //拿到子节点
//            command.remove(0);
//
//            //如果command空了且参数也为空 则返回自己
//            if (command.isEmpty() && paramNodes.isEmpty()) return this;
//
//            //如果command不为空
//            if (!command.isEmpty()){
//                //是否可以被子节点处理
//                //去拿子节点的Node
//                for (CommandNode node : subCommandNodes) {
//                    CommandNode subNode = node.getNode(command, paramContainer);
//                    if (subNode != null) return subNode;
//                    break;
//                }
//            }
//
//            //如果不能被子节点处理,就处理参数
//            //如果需要的参数为空
//            if (paramNodes.isEmpty()) return null;
//
//            //如果必要参数都不足就返回null
//            int reqParamSize = paramNodes.stream()
//                    .filter(n -> n instanceof RequiredParamNode).toList().size();
//            if (command.size() < reqParamSize) return null;
//            Map<String, Object> paramCache = new LinkedHashMap<>();
//            int offset = 0;
//            //如果带有的参数数量等于必要的参数 如果必要参数为0就去尝试解析可选参数
//            if (command.size() == reqParamSize && reqParamSize != 0){
//                //解析必要参数
//                int index = 0;
//                for (ParamNode paramNode : paramNodes) {
//                    paramCache.put(paramNode.getKey(), paramNode.getDefault());
//                    if (paramNode instanceof RequiredParamNode) {
//                        paramCache.put(paramNode.getKey(), command.get(index++));
//                        offset++;
//                    }
//                }
//            }else if (command.size() < paramNodes.size()){ //如果大于必要参数并小于所有参数
//                //解析必要参数并使用null补全空的可选参数
//                int addedReqParamCount = 0;
//                for (int i = 0; i < paramNodes.size(); i++) {
//                    //TODO 自定义默认值
//                    Object paramValue = null;
//
//                    //如果必选参数全部添加了
//                    if (addedReqParamCount >= reqParamSize) {
//                        //先尝试后面的参数是否可以被子指令处理
//                        CommandNode subNode = getNode(command, paramContainer, paramCache, offset);
//                        if (subNode != null) return subNode;
//                    }
//                    //不行的话就接着解析
//                    if (command.size() >= i + 1) paramValue = command.get(i);
//
//                    ParamNode paramNode = paramNodes.get(i);
//                    if (paramNode instanceof RequiredParamNode) addedReqParamCount++;
//                    paramCache.put(paramNode.getKey(), paramValue);
//                    offset++;
//                }
//            }else { //如果参数足够填充所有参数
//                int addedReqParamCount = 0;
//
//                Map<String, Object> paramCache2 = new HashMap<>();
//                //先尝试解析必要参数 并尝试使用子Node处理
//                for (int i = 0; i < paramNodes.size(); i++) {
//                    ParamNode paramNode = paramNodes.get(i);
//
//                    paramCache2.put(paramNode.getKey(), paramNode.getDefault());
//                    if (!(paramNode instanceof RequiredParamNode)) continue;
//
//                    addedReqParamCount++;
//                    paramCache2.put(paramNode.getKey(), command.get(i));
//
//                    if (addedReqParamCount == reqParamSize){
//                        paramCache2.putAll(paramCache);
//                        CommandNode subNode = getNode(command, paramContainer, paramCache2, addedReqParamCount);
//                        if (subNode != null) return subNode;
//                        //没有break是因为要添加null值给可选的
//                    }
//                }
//
//                //如果不能被处理 就按循序处理
//                addedReqParamCount = 0;
//                for (int i = 0; i < paramNodes.size(); i++) {
//                    Object paramValue = command.get(i);
//                    //如果必选参数全部添加了
//                    if (addedReqParamCount >= reqParamSize) {
//                        //先尝试后面的参数是否可以被子指令处理
//                        CommandNode subNode = getNode(command, paramContainer, paramCache, offset);
//                        if (subNode != null) return subNode;
//                    }
//                    ParamNode paramNode = paramNodes.get(i);
//                    if (paramNode instanceof RequiredParamNode) addedReqParamCount++;
//                    paramCache.put(paramNode.getKey(), paramValue);
//                    offset++;
//                }
//            }
//
//            //添加参数
//            paramContainer.putAll(paramCache);
//            //经过上面的if 现在参数解析已完成
//            //删除被解析的参数
//            removeWithOffset(command, offset);
//
//            //如果command解析完了，直接返回此节点
//            if (command.isEmpty()) return this;
//
//            //如果没有子节点了，就返回此节点
//            if (subCommandNodes.isEmpty()) return this;
//
//            //是否可以被子节点处理
//            //去拿子节点的Node
//            for (CommandNode node : subCommandNodes) {
//                CommandNode subNode = node.getNode(command, paramContainer);
//                if (subNode != null) {
//                    paramContainer.putAll(paramCache);
//                    return subNode;
//                }
//            }
//        }
//        //不是此node
//        return null;
//    }
//
//    /**
//     * 内部使用的递归方法
//     */
//    @Nullable
//    private CommandNode getNode(List<Object> command, Map<String, Object> paramContainer, Map<String, Object> paramCache, int offset) {
//        List<Object> list = new ArrayList<>(command);
//        removeWithOffset(list, offset);
//
//        if (list.isEmpty()) return null;
//
//        for (CommandNode node : subCommandNodes) {
//            CommandNode subNode = node.getNode(list, paramContainer);
//            if (subNode != null) {
//                paramContainer.putAll(paramCache);
//                return subNode;
//            }
//        }
//        return null;
//    }

    public JKookCommand build(){
        JKookCommand jKookCommand = new JKookCommand(rootValue);
        jKookCommand.setExecutor(new CommandExecutor() {
            @Override
            public void onCommand(snw.jkook.command.CommandSender sender, Object[] arguments, @Nullable Message message) {

                if (!rootWrapper.isGlobalAllowType(sender, message)){
                    return;
                }

                if (!isAllowType(sender, message)) {
                    return;
                }

                if (executor == null) return;
                CSender senderWrapped = new CommandSenderWrapper(sender, message);

                Map<String, Object> paramValues = new LinkedHashMap<>();
                for (int i = 0; i < paramNodes.size(); i++) {
                    Object argument = arguments[i];
                    if (argument == Void.TYPE) argument = null;
                    paramValues.put(paramNodes.get(i).getKey(), argument);
                }

                executor.execute(senderWrapped, paramValues);
            }
        });

        for (ParamNode node : paramNodes) {
            if (node instanceof RequiredParamNode n){
                jKookCommand.addArgument(n.getParamType());
            }else if (node instanceof OptionalParamNode n){
                jKookCommand.addOptionalArgument((Class) n.getParamType(), n.getDefault());
            }
        }

        jKookCommand.setDescription(this.description);
        jKookCommand.setHelpContent(this.helpMessage);
        aliases.forEach(jKookCommand::addAlias);

        for (CommandNode subCommandNode : subCommandNodes) {
            jKookCommand.addSubcommand(subCommandNode.build());
        }
        return jKookCommand;
    }

    private static void removeWithOffset(Iterable<?> iterable, int offset){
        Iterator<?> iterator = iterable.iterator();
        while (iterator.hasNext() && offset > 0) {
            iterator.next();
            iterator.remove();
            offset--;
        }
    }
}