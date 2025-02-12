package com.bgasol.web.system.menu.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.model.system.user.entity.UserEntity;
import com.bgasol.web.system.menu.mapper.MenuMapper;
import com.bgasol.web.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@Transactional
public class MenuService extends BaseService<MenuEntity, BasePageDto<MenuEntity>> {
    private final MenuMapper menuMapper;

    @Lazy
    private final UserService userService;

    @Override
    public MenuMapper commonBaseMapper() {
        return menuMapper;
    }

    /**
     * 查询后台管理系统的左侧主菜单
     *
     * @return 菜单实体树集合
     */
    @Transactional(readOnly = true)
    public List<MenuEntity> findAdminMenuGroup() {
        // 查询左侧菜单的树
        LambdaQueryWrapper<MenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuEntity::getMenuGroup, SystemConfigValues.ADMIN_MENU_GROUP_ID);
        queryWrapper.isNull(MenuEntity::getParentId);
        List<MenuEntity> menuEntityList = menuMapper.selectList(queryWrapper);
        for (MenuEntity menuEntity : menuEntityList) {
            menuEntity.setChildren(this.findTreeAll(menuEntity.getId(), null));
        }

        // 获取当前用户可访问的菜单
        return getUserMenuEntities(menuEntityList);
    }

    /**
     * 递归查询子菜单id是否存在如果不存在则删除
     *
     * @param menus   完整菜单
     * @param menuIds 用户可访问菜单id列表
     */
    @Transactional(readOnly = true)
    public void findChildMenu(List<MenuEntity> menus, Set<String> menuIds) {
        for (int i = 0; i < menus.size(); i++) {
            MenuEntity menu = menus.get(i);
            if (menuIds.contains(menu.getId())) {
                // 递归查询子菜单
                List<MenuEntity> children = menu.getChildren();
                if (ObjectUtils.isNotEmpty(children)) {
                    this.findChildMenu(children, menuIds);
                }
            } else {
                menus.remove(i--);
            }
        }
    }


    /**
     * 递归初始化菜单及其子菜单
     *
     * @param menuEntity 菜单实体
     * @return 初始化后的菜单实体
     */
    public MenuEntity init(MenuEntity menuEntity) {
        if (menuMapper.selectById(menuEntity.getId()) == null) {
            this.save(menuEntity);
        } else {
            this.update(menuEntity);
        }
        this.initChildren(menuEntity);
        return this.findById(menuEntity.getId());
    }

    /**
     * 递归初始化子菜单
     *
     * @param menuEntity 菜单实体
     */
    private void initChildren(MenuEntity menuEntity) {
        List<MenuEntity> children = menuEntity.getChildren();
        if (ObjectUtils.isNotEmpty(children)) {
            for (MenuEntity child : children) {
                if (menuMapper.selectById(child.getId()) == null) {
                    this.save(child);
                } else {
                    this.update(child);
                }
                this.initChildren(child); // 递归调用
            }
        }
    }

    @Transactional(readOnly = true)
    public List<MenuEntity> findAllMenuRoutes() {
        LambdaQueryWrapper<MenuEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.isNotNull(MenuEntity::getRouteName);

        boolean login = StpUtil.isLogin();
        if (!login) {
            return new ArrayList<>();
        }
        String userId = StpUtil.getLoginIdAsString();
        List<MenuEntity> menuEntityList = menuMapper.selectList(lambdaQueryWrapper);
        if (userId.equals(SystemConfigValues.ADMIN_USER_ID)) {
            return menuEntityList;
        }

        return getUserMenuEntities(menuEntityList);
    }

    // 获取当前用户可访问的菜单
    @Transactional(readOnly = true)
    public List<MenuEntity> getUserMenuEntities(List<MenuEntity> menuEntityList) {
        UserEntity user = userService.getUserInfo();
        Set<String> menuIds = new HashSet<>();
        user.getRoles().forEach(role -> role.getMenus().forEach(menu -> menuIds.add(menu.getId())));
        if (!user.getId().equals(SystemConfigValues.ADMIN_USER_ID)) {
            this.findChildMenu(menuEntityList, menuIds);
        }
        return menuEntityList;
    }
}
