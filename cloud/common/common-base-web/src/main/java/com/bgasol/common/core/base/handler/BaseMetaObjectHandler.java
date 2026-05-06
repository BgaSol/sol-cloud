package com.bgasol.common.core.base.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component()
public class BaseMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时填充
     *
     * @param metaObject metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();
        if (ObjectUtils.isEmpty(getFieldValByName("createTime", metaObject))) {
            this.setFieldValByName("createTime", now, metaObject);
        }
        if (ObjectUtils.isEmpty(getFieldValByName("updateTime", metaObject))) {
            this.setFieldValByName("updateTime", now, metaObject);
        }
    }

    /**
     * 更新时填充
     *
     * @param metaObject metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Date now = new Date();
        this.setFieldValByName("updateTime", now, metaObject);
    }
}
