package me.mantou.breadmachine.kook.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Function;

@Data
@AllArgsConstructor
public class ResultData<T> {
    public ResultDataType type;
    public String msg;
    public T data;

    public <V> V check(Function<ResultData<T>, V> function){
        return function.apply(this);
    }

    public static <V> ResultData<V> ok(String msg, V data){
        return new ResultData<>(ResultDataType.OK, msg, data);
    }

    public static <V> ResultData<V> warn(String msg, V data){
        return new ResultData<>(ResultDataType.WARN, msg, data);
    }

    public static <V> ResultData<V> err(String msg, V data){
        return new ResultData<>(ResultDataType.ERR, msg, data);
    }

    public static ResultData<Void> ok(String msg){
        return new ResultData<>(ResultDataType.OK, msg, null);
    }

    public static ResultData<Void> warn(String msg){
        return new ResultData<>(ResultDataType.WARN, msg, null);
    }

    public static ResultData<Void> err(String msg){
        return new ResultData<>(ResultDataType.ERR, msg, null);
    }

    public enum ResultDataType{
        OK,
        WARN,
        ERR,
        ;
    }
}
