package com.bgasol.plugin.mybatisPlus.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.Log;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;

@Slf4j
@RequiredArgsConstructor
public class MybatisSqlLogger implements Log {
    private final String clazz;

    private String color(String msg, AnsiColor color) {
        return AnsiOutput.toString(color, msg, AnsiColor.DEFAULT);
    }

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
        log.error("[{}]:\n{}", clazz, color(s, AnsiColor.RED), e);
    }

    @Override
    public void error(String s) {
        log.error("[{}]:\n{}", clazz, color(s, AnsiColor.RED));
    }

    @Override
    public void debug(String s) {
        log.debug("[{}]:\n{}", clazz, color(s, AnsiColor.BRIGHT_YELLOW));
    }

    @Override
    public void trace(String s) {
        log.trace("[{}]:\n{}", clazz, color(s, AnsiColor.CYAN));
    }

    @Override
    public void warn(String s) {
        log.warn("[{}]:\n{}", clazz, color(s, AnsiColor.MAGENTA));
    }
}