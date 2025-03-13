package com.bgasol.plugin.mybatisPlus.config;

import com.baomidou.mybatisplus.autoconfigure.DdlApplicationRunner;
import com.baomidou.mybatisplus.extension.ddl.IDdl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@ConditionalOnClass(IDdl.class)
@Configuration()
public class DdlAutoConfiguration {

    @Bean
    @Order(-1)
    @ConditionalOnBean({IDdl.class})
    public DdlApplicationRunner ddlApplicationRunner(List<IDdl> ddlList) {
        return new DdlApplicationRunner(ddlList);
    }
}
