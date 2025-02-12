package com.bgasol.web.system.runner;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.model.system.menu.api.MenuApi;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.model.system.menu.entity.MenuType;
import com.bgasol.model.system.user.entity.UserEntity;
import com.bgasol.web.system.department.mapper.DepartmentMapper;
import com.bgasol.web.system.user.mapper.UserMapper;
import com.bgasol.web.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SystemInitData implements ApplicationRunner {

    private final UserMapper userMapper;
    private final UserService userService;

    private final DepartmentMapper departmentMapper;

    @Value("${system.title}")
    private String systemTitle;

    private final MenuApi menuApi;

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

        MenuEntity systemServiceMenu = new MenuEntity();
        systemServiceMenu.setId("system-service");

        systemServiceMenu.setName("系统服务");
        systemServiceMenu.setMenuType(MenuType.MENU);
        systemServiceMenu.setIcon("IconParkServer");

        systemServiceMenu.setMenuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID);

        List<MenuEntity> systemServiceMenuChildren = new ArrayList<>();
        {
            MenuEntity roleMenu = new MenuEntity();
            roleMenu.setId("role");
            roleMenu.setParentId(systemServiceMenu.getId());
            roleMenu.setName("角色管理");
            roleMenu.setMenuType(MenuType.PAGE);
            roleMenu.setIcon("IconParkEveryUser");
            roleMenu.setPath("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + SystemConfigValues.SERVICE_NAME + "/role");
            roleMenu.setRouteName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + SystemConfigValues.SERVICE_NAME + "_role");
            roleMenu.setMenuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID);
            systemServiceMenuChildren.add(roleMenu);

            MenuEntity permissionMenu = new MenuEntity();
            permissionMenu.setId("permission");
            permissionMenu.setParentId(systemServiceMenu.getId());
            permissionMenu.setName("权限管理");
            permissionMenu.setMenuType(MenuType.PAGE);
            permissionMenu.setIcon("IconParkPermissions");
            permissionMenu.setPath("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + SystemConfigValues.SERVICE_NAME + "/permission");
            permissionMenu.setRouteName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + SystemConfigValues.SERVICE_NAME + "_permission");
            permissionMenu.setMenuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID);
            systemServiceMenuChildren.add(permissionMenu);

            MenuEntity userMenu = new MenuEntity();
            userMenu.setId("user");
            userMenu.setParentId(systemServiceMenu.getId());
            userMenu.setName("用户管理");
            userMenu.setMenuType(MenuType.PAGE);
            userMenu.setIcon("IconParkPeople");
            userMenu.setPath("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + SystemConfigValues.SERVICE_NAME + "/user");
            userMenu.setRouteName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + SystemConfigValues.SERVICE_NAME + "_user");
            userMenu.setMenuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID);
            systemServiceMenuChildren.add(userMenu);

            MenuEntity departmentMenu = new MenuEntity();
            departmentMenu.setId("department");
            departmentMenu.setParentId(systemServiceMenu.getId());
            departmentMenu.setName("部门管理");
            departmentMenu.setMenuType(MenuType.PAGE);
            departmentMenu.setIcon("IconParkCategoryManagement");
            departmentMenu.setPath("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + SystemConfigValues.SERVICE_NAME + "/department");
            departmentMenu.setRouteName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + SystemConfigValues.SERVICE_NAME + "_department");
            departmentMenu.setMenuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID);
            systemServiceMenuChildren.add(departmentMenu);

            MenuEntity menuMenu = new MenuEntity();
            menuMenu.setId("menu");
            menuMenu.setParentId(systemServiceMenu.getId());
            menuMenu.setName("菜单管理");
            menuMenu.setMenuType(MenuType.PAGE);
            menuMenu.setIcon("IconParkListView");
            menuMenu.setPath("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + SystemConfigValues.SERVICE_NAME + "/menu");
            menuMenu.setRouteName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + SystemConfigValues.SERVICE_NAME + "_menu");
            menuMenu.setMenuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID);
            systemServiceMenuChildren.add(menuMenu);
        }
        systemServiceMenu.setChildren(systemServiceMenuChildren);

        menuApi.init(systemServiceMenu);
    }

    private void initDepartment() {
        DepartmentEntity department = new DepartmentEntity();
        department.setName(systemTitle);
        department.setDescription("系统必须要有一个部门，用于存放没有部门的用户");
        department.setId(SystemConfigValues.DEFAULT_DEPARTMENT_ID);
        if (this.departmentMapper.selectById(department.getId()) == null) {
            this.departmentMapper.insert(department);
        }
    }

    public void initUsers() {
        UserEntity admin = new UserEntity();
        admin.setId(SystemConfigValues.ADMIN_USER_ID);
        admin.setUsername(SystemConfigValues.ADMIN_USER_ID);
        admin.setPassword(userService.encodePassword(SystemConfigValues.ADMIN_USER_ID));
        admin.setNickname(SystemConfigValues.ADMIN_USER_ID);
        admin.setLocked(false);
        admin.setDescription("超级管理员用户,无需配置权限,拥有系统最高权限");
        admin.setDepartmentId(SystemConfigValues.DEFAULT_DEPARTMENT_ID);
        if (this.userMapper.selectById(admin.getId()) == null) {
            this.userMapper.insert(admin);
        }
    }
}
