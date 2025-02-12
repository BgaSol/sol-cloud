package com.bgasol.plugin.mybatisPlus.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.Log;
import org.springframework.boot.ansi.AnsiColor;

@Slf4j
@RequiredArgsConstructor
public class MybatisSqlLogger implements Log {
    private final String clazz;

    private static final String DEFAULT = "\u001B[" + AnsiColor.DEFAULT + "m";
    private static final String BRIGHT_YELLOW = "\u001B[" + AnsiColor.BRIGHT_YELLOW + "m";

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable e) {
        log.error("[{}]:\t\n{}{}{}", clazz, BRIGHT_YELLOW, s, DEFAULT, e);
    }

    @Override
    public void error(String s) {
        log.error("[{}]:\t\n{}{}{}", clazz, BRIGHT_YELLOW, s, DEFAULT);

    }

    @Override
    public void debug(String s) {
        log.debug("[{}]:\t\n{}{}{}", clazz, BRIGHT_YELLOW, s, DEFAULT);
    }

    @Override
    public void trace(String s) {
        log.trace("[{}]:\t\n{}{}{}", clazz, BRIGHT_YELLOW, s, DEFAULT);
    }

    @Override
    public void warn(String s) {
        log.warn("[{}]:\t\n{}{}{}", clazz, BRIGHT_YELLOW, s, DEFAULT);
    }
}
