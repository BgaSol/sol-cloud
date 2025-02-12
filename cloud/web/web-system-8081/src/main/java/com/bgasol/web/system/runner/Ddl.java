package com.bgasol.web.system.runner;

import com.baomidou.mybatisplus.extension.ddl.IDdl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class Ddl implements IDdl {

    private final DataSource dataSource;

    @Override
    public void runScript(Consumer<DataSource> consumer) {
        consumer.accept(dataSource);
    }

    @Override
    public List<String> getSqlFiles() {
        return List.of("db/system.sql");
    }
}
