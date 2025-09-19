package com.bgasol.web.file.runner;

import com.bgasol.common.constant.value.FileConfigValues;
import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.model.system.menu.api.MenuApi;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.model.system.menu.entity.MenuType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FileInitData implements ApplicationRunner {

    private final MenuApi menuApi;

    @Override
    public void run(ApplicationArguments args) {
        initMenus();
    }

    public void initMenus() {
        String fileServiceMenuId = "file-service";
        MenuEntity fileServiceMenu = MenuEntity.builder()
                .id(fileServiceMenuId)
                .name("文件服务")
                .menuType(MenuType.MENU)
                .icon("IconParkData")
                .menuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID)
                .children(List.of(
                        MenuEntity.builder()
                                .id("file")
                                .parentId(fileServiceMenuId)
                                .name("文件管理")
                                .menuType(MenuType.PAGE)
                                .icon("IconParkFileCabinet")
                                .path("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + FileConfigValues.SERVICE_NAME + "/file")
                                .routeName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + FileConfigValues.SERVICE_NAME + "_file")
                                .menuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID)
                                .build(),
                        MenuEntity.builder()
                                .id("image")
                                .parentId(fileServiceMenuId)
                                .name("图片管理")
                                .menuType(MenuType.PAGE)
                                .icon("IconParkPic")
                                .path("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + FileConfigValues.SERVICE_NAME + "/image")
                                .routeName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + FileConfigValues.SERVICE_NAME + "_image")
                                .menuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID)
                                .build()
                )).build();
        menuApi.init(fileServiceMenu);
    }
}
