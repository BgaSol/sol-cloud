package com.bgasol.common.constant.value;

import com.bgasol.common.constant.config.ClientServer;
import org.springframework.stereotype.Component;

@ClientServer
@Component
public class SystemConfigValues {

    public final static String SERVICE_NAME = "system";

    /// 超级管理员用户id
    public final static String ADMIN_USER_ID = "admin";

    /// 默认部门id
    public final static String DEFAULT_DEPARTMENT_ID = "default";

    /// 后台系统 左侧 主菜单组id  admin-master
    public final static String ADMIN_MENU_GROUP_ID = "admin-master";

    /// 后台页面名字
    public final static String ADMIN_PAGE_NAME = "admin";
}
