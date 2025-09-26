package com.bgasol.common.core.base.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * mybatis-plus 的元数据拦截器
 */
@Component()
public class BaseMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时填充
     *
     * @param metaObject metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if (ObjectUtils.isEmpty(getFieldValByName("createTime", metaObject))) {
            this.setFieldValByName("createTime", new Date(), metaObject);
        }
    }

    /**
     * 更新时填充
     *
     * @param metaObject metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}
