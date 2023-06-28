package me.mantou.breadmachine.webhook;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Stream2StringParser implements PayloadParser<InputStream, String> {
    @Override
    @SneakyThrows
    public String parse(InputStream inputStream) {
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
}
