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
import org.redisson.api.RedissonClient;
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

    private final RedissonClient redissonClient;

    @Override
    public RedissonClient commonBaseRedissonClient() {
        return redissonClient;
    }

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
        return this.findByMenuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID);
    }

    public List<MenuEntity> findByMenuGroup(String group) {
        // 查询左侧菜单的树
        LambdaQueryWrapper<MenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuEntity::getMenuGroup, group);
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
        if (ObjectUtils.isEmpty(cacheSearch(menuEntity.getId()))) {
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
                if (ObjectUtils.isEmpty(cacheSearch(child.getId()))) {
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

    @Override
    public Integer delete(String id) {
        MenuEntity menuEntity = this.findById(id);
        if (ObjectUtils.isEmpty(menuEntity)) {
            return 1;
        }
        HashSet<String> menuIds = new HashSet<>();
        menuIds.add(id);
        List<MenuEntity> menus = findTreeAll(id, null);
        collectDeleteIds(menus, menuIds);
        menuIds.forEach(menuId -> {
            this.menuMapper.deleteFromTable("system_c_role_menu", "menu_id", menuId);
            super.delete(menuId);
        });
        return 1;
    }

    private void collectDeleteIds(List<MenuEntity> menus, Set<String> ids) {
        List<MenuEntity> menuEntityList = menus.stream()
                .filter(e -> !e.getChildren().isEmpty())
                .flatMap(e -> e.getChildren().stream())
                .toList();

        // 将当前层级子部门的ID添加到删除集合中
        menuEntityList.forEach(e -> ids.add(e.getId()));
        menus.forEach(e -> ids.add(e.getId()));

        if (!menuEntityList.isEmpty()) {
            // 递归处理下一层级的子部门
            this.collectDeleteIds(menuEntityList, ids);
        }
    }
}
