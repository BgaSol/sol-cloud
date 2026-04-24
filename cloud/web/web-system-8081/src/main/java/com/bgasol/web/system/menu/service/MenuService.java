package com.bgasol.web.system.menu.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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

import static com.bgasol.common.constant.value.SystemConfigValues.ADMIN_ROLE_ID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
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

    @Transactional(readOnly = true)
    public List<MenuEntity> findByMenuGroup(String group) {
        LambdaQueryWrapper<MenuEntity> nested = Wrappers.<MenuEntity>lambdaQuery()
                .eq(MenuEntity::getMenuGroup, group)
                .nested(w -> w
                        .isNull(MenuEntity::getParentId)
                        .or()
                        .eq(MenuEntity::getParentId, ""));
        List<MenuEntity> menuEntityList = this.findAll(nested, false);
        // 查询左侧菜单的树
        if (StpUtil.getRoleList().contains(ADMIN_ROLE_ID)) {
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
     */
    @Transactional
    public void init(MenuEntity menuEntity) {
        this.initChildren(menuEntity);
    }

    /**
     * 递归初始化子菜单
     *
     * @param menuEntity 菜单实体
     */
    @Transactional()
    public void initChildren(MenuEntity menuEntity) {
        if (ObjectUtils.isEmpty(findById(menuEntity.getId(), false))) {
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

        if (StpUtil.getRoleList().contains(ADMIN_ROLE_ID)) {
            return menuEntityList;
        }
        Set<String> menuIds = getUserMenuIds();

        return menuEntityList.stream()
                .filter(menuEntity -> menuIds.contains(menuEntity.getId()))
                .toList();
    }

    private Set<String> getUserMenuIds() {
        UserEntity user = userService.findById(StpUtil.getLoginIdAsString(), true);
        Set<String> menuIds = new HashSet<>();
        user.getRoles().forEach(role -> role.getMenus().forEach(menu -> menuIds.add(menu.getId())));
        return menuIds;
    }
}
