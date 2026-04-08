package com.bgasol.web.system.runner;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.model.system.menu.entity.MenuType;
import com.bgasol.model.system.permission.dto.PermissionCreateDto;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.model.system.role.dto.RoleCreateDto;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.model.system.user.entity.UserEntity;
import com.bgasol.web.system.department.service.DepartmentService;
import com.bgasol.web.system.menu.service.MenuService;
import com.bgasol.web.system.permission.service.PermissionService;
import com.bgasol.web.system.role.service.RoleService;
import com.bgasol.web.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.bgasol.common.constant.value.SystemConfigValues.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemInitData implements ApplicationRunner {
    private final UserService userService;
    private final DepartmentService departmentService;
    private final RoleService roleService;
    private final PermissionService permissionService;

    @Value("${system.title}")
    private String systemTitle;

    private final MenuService menuService;

    @Override
    public void run(ApplicationArguments args) {
        // 创建菜单
        initMenus();
        // 创建部门
        initDepartment();
        // 创建权限
        initPermission();
        // 创建角色
        initRole();
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
                                .build(),
                        MenuEntity.builder()
                                .id("message-envelope")
                                .parentId(systemServiceId)
                                .name("系统消息通知")
                                .menuType(MenuType.PAGE)
                                .icon("IconParkRemind")
                                .path("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + SystemConfigValues.SERVICE_NAME + "/message-envelope")
                                .routeName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + SystemConfigValues.SERVICE_NAME + "_message_envelope")
                                .menuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID)
                                .build(),
                        MenuEntity.builder()
                                .id("requestLog")
                                .parentId(systemServiceId)
                                .name("请求日志管理")
                                .menuType(MenuType.PAGE)
                                .icon("IconParkLog")
                                .path("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + SystemConfigValues.SERVICE_NAME + "/requestLog")
                                .routeName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + SystemConfigValues.SERVICE_NAME + "_request_log")
                                .menuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID)
                                .build()
                ))
                .build();
        menuService.init(systemServiceMenu);
    }

    private void initDepartment() {
        DepartmentEntity department = DepartmentEntity.builder()
                .id(DEFAULT_DEPARTMENT_ID)
                .name(systemTitle)
                .description("系统必须要有一个部门，用于存放没有部门的用户")
                .build();

        if (ObjectUtils.isEmpty(this.departmentService.findById(department.getId(), false))) {
            this.departmentService.insert(department);
            log.info("save department {}", department.getName());
        } else {
            this.departmentService.apply(department);
            log.info("apply department {}", department.getName());
        }

    }

    public void initPermission() {
        PermissionEntity permission = PermissionCreateDto.builder()
                .id(ADMIN_PERMISSION_ID)
                .code(ADMIN_PERMISSION_ID)
                .name("系统最高权限")
                .description("系统全接口访问权限")
                .build().toEntity();
        if (ObjectUtils.isEmpty(this.permissionService.findById(permission.getId(), false))) {
            this.permissionService.insert(permission);
            log.info("save permission {}", permission.getName());
        } else {
            this.permissionService.apply(permission);
            log.info("apply permission {}", permission.getName());
        }
    }

    private void initRole() {
        RoleEntity role = RoleCreateDto.builder()
                .code(ADMIN_ROLE_ID)
                .name("超级管理员")
                .description("系统默认超级管理员角色")
                .permissionIds(List.of(ADMIN_PERMISSION_ID))
                .build().toEntity();
        if (ObjectUtils.isEmpty(this.roleService.findById(role.getId(), false))) {
            this.roleService.insert(role);
            log.info("save role {}", role.getName());
        } else {
            this.roleService.apply(role);
            log.info("apply role {}", role.getName());
        }
    }

    public void initUsers() {
        UserEntity user = UserEntity.builder()
                .id(SystemConfigValues.ADMIN_USER_ID)
                .username(SystemConfigValues.ADMIN_USER_ID)
                .roles(List.of(roleService.findById(ADMIN_ROLE_ID, false)))
                .password(userService.encodePassword(SystemConfigValues.ADMIN_USER_ID))
                .nickname(SystemConfigValues.ADMIN_USER_ID)
                .locked(false)
                .description("超级管理员用户,无需配置权限,拥有系统最高权限")
                .departmentId(DEFAULT_DEPARTMENT_ID)
                .build();
        if (ObjectUtils.isEmpty(this.userService.findById(user.getId(), false))) {
            this.userService.insert(user);
            log.info("save user {}", user.getId());
        } else {
            user.setPassword(null);
            this.userService.apply(user);
            log.info("apply user {}", user.getId());
        }
    }
}
