package com.bgasol.web.system.runner;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.model.system.menu.entity.MenuType;
import com.bgasol.model.system.user.entity.UserEntity;
import com.bgasol.web.system.department.service.DepartmentService;
import com.bgasol.web.system.menu.service.MenuService;
import com.bgasol.web.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SystemInitData implements ApplicationRunner {
    private final UserService userService;
    private final DepartmentService departService;

    @Value("${system.title}")
    private String systemTitle;

    private final MenuService menuService;

    @Override
    public void run(ApplicationArguments args) {
        // 创建菜单
        initMenus();
        // 创建部门
        initDepartment();
        // 创建用户
        initUsers();
    }

    public void initMenus() {
        String systemServiceId = "system-service";
        MenuEntity systemServiceMenu = MenuEntity.builder()
                .id(systemServiceId)
                .name("系统服务")
                .menuType(MenuType.MENU)
                .icon("IconParkServer")
                .menuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID)
                .children(List.of(
                        MenuEntity.builder()
                                .id("role")
                                .parentId(systemServiceId)
                                .name("角色管理")
                                .menuType(MenuType.PAGE)
                                .icon("IconParkEveryUser")
                                .path("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + SystemConfigValues.SERVICE_NAME + "/role")
                                .routeName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + SystemConfigValues.SERVICE_NAME + "_role")
                                .menuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID)
                                .build(),
                        MenuEntity.builder()
                                .id("permission")
                                .parentId(systemServiceId)
                                .name("权限管理")
                                .menuType(MenuType.PAGE)
                                .icon("IconParkPermissions")
                                .path("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + SystemConfigValues.SERVICE_NAME + "/permission")
                                .routeName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + SystemConfigValues.SERVICE_NAME + "_permission")
                                .menuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID)
                                .build(),
                        MenuEntity.builder()
                                .id("user")
                                .parentId(systemServiceId)
                                .name("用户管理")
                                .menuType(MenuType.PAGE)
                                .icon("IconParkPeople")
                                .path("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + SystemConfigValues.SERVICE_NAME + "/user")
                                .routeName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + SystemConfigValues.SERVICE_NAME + "_user")
                                .menuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID)
                                .build(),
                        MenuEntity.builder()
                                .id("department")
                                .parentId(systemServiceId)
                                .name("部门管理")
                                .menuType(MenuType.PAGE)
                                .icon("IconParkCategoryManagement")
                                .path("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + SystemConfigValues.SERVICE_NAME + "/department")
                                .routeName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + SystemConfigValues.SERVICE_NAME + "_department")
                                .menuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID)
                                .build(),
                        MenuEntity.builder()
                                .id("menu")
                                .parentId(systemServiceId)
                                .name("菜单管理")
                                .menuType(MenuType.PAGE)
                                .icon("IconParkListView")
                                .path("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + SystemConfigValues.SERVICE_NAME + "/menu")
                                .routeName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + SystemConfigValues.SERVICE_NAME + "_menu")
                                .menuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID)
                                .build()
                ))
                .build();
        menuService.init(systemServiceMenu);
    }

    private void initDepartment() {
        DepartmentEntity department = DepartmentEntity.builder()
                .name(systemTitle)
                .description("系统必须要有一个部门，用于存放没有部门的用户")
                .id(SystemConfigValues.DEFAULT_DEPARTMENT_ID)
                .build();

        if (ObjectUtils.isEmpty(this.departService.findDirectById(department.getId()))) {
            this.departService.save(department);
        }
    }

    public void initUsers() {
        if (ObjectUtils.isNotEmpty(this.userService.findDirectById(SystemConfigValues.ADMIN_USER_ID))) {
            return;
        }
        this.userService.save(UserEntity.builder()
                .id(SystemConfigValues.ADMIN_USER_ID)
                .username(SystemConfigValues.ADMIN_USER_ID)
                .password(userService.encodePassword(SystemConfigValues.ADMIN_USER_ID))
                .nickname(SystemConfigValues.ADMIN_USER_ID)
                .locked(false)
                .description("超级管理员用户,无需配置权限,拥有系统最高权限")
                .departmentId(SystemConfigValues.DEFAULT_DEPARTMENT_ID)
                .build());
    }
}
