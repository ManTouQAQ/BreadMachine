package me.mantou.breadmachine.webhook;

public interface PayloadParser<T, R> {
    R parse(T payload);
}
