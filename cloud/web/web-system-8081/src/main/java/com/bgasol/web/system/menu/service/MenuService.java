package com.bgasol.web.system.menu.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.service.BaseTreeService;
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
public class MenuService extends BaseTreeService<MenuEntity, BasePageDto<MenuEntity>> {
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

    @Transactional(readOnly = true)
    public List<MenuEntity> findByMenuGroup(String group) {
        // 查询左侧菜单的树
        List<MenuEntity> menuEntityList = this.findAll()
                .stream()
                .filter(menuEntity -> menuEntity.getMenuGroup().equals(group))
                .toList();
        String userId = StpUtil.getLoginIdAsString();
        if (userId.equals(SystemConfigValues.ADMIN_USER_ID)) {
            return menuEntityList;
        }
        Set<String> menuIds = getUserMenuIds();
        return filterMenus(menuEntityList, menuIds);
    }

    private List<MenuEntity> filterMenus(List<MenuEntity> menuEntityList, Set<String> menuIds) {
        if (ObjectUtils.isNotEmpty(menuEntityList)) {
            return menuEntityList.stream()
                    .filter(menuEntity -> menuIds.contains(menuEntity.getId()))
                    .peek(menuEntity -> menuEntity.setChildren(filterMenus(menuEntity.getChildren(), menuIds)))
                    .toList();
        }
        return List.of();
    }

    /**
     * 递归初始化菜单及其子菜单
     *
     * @param menuEntity 菜单实体
     * @return 初始化后的菜单实体
     */
    public MenuEntity init(MenuEntity menuEntity) {
        this.initChildren(menuEntity);
        return this.findById(menuEntity.getId());
    }

    /**
     * 递归初始化子菜单
     *
     * @param menuEntity 菜单实体
     */
    private void initChildren(MenuEntity menuEntity) {
        if (ObjectUtils.isEmpty(findDirectById(menuEntity.getId()))) {
            this.insert(menuEntity);
        } else {
            this.apply(menuEntity);
        }
        List<MenuEntity> children = menuEntity.getChildren();
        if (ObjectUtils.isEmpty(children)) {
            return;
        }
        for (MenuEntity child : children) {
            this.initChildren(child); // 递归调用
        }
    }

    @Transactional(readOnly = true)
    public List<MenuEntity> findAllMenuRoutes() {
        boolean login = StpUtil.isLogin();
        if (!login) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<MenuEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.isNotNull(MenuEntity::getRouteName);
        List<MenuEntity> menuEntityList = menuMapper.selectList(lambdaQueryWrapper);

        String userId = StpUtil.getLoginIdAsString();
        if (userId.equals(SystemConfigValues.ADMIN_USER_ID)) {
            return menuEntityList;
        }
        Set<String> menuIds = getUserMenuIds();

        return menuEntityList.stream()
                .filter(menuEntity -> menuIds.contains(menuEntity.getId()))
                .toList();
    }

    private Set<String> getUserMenuIds() {
        UserEntity user = userService.getUserInfo();
        Set<String> menuIds = new HashSet<>();
        user.getRoles().forEach(role -> role.getMenus().forEach(menu -> menuIds.add(menu.getId())));
        return menuIds;
    }
}
