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

import java.util.ArrayList;
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
        MenuEntity fileServiceMenu = new MenuEntity();
        fileServiceMenu.setId("file-service");

        fileServiceMenu.setName("文件服务");
        fileServiceMenu.setMenuType(MenuType.MENU);
        fileServiceMenu.setIcon("IconParkData");

        fileServiceMenu.setMenuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID);
        List<MenuEntity> fileServiceMenuChildren = new ArrayList<>();
        {
            MenuEntity fileMenu = new MenuEntity();
            fileMenu.setId("file");
            fileMenu.setParentId(fileServiceMenu.getId());
            fileMenu.setName("文件管理");
            fileMenu.setMenuType(MenuType.PAGE);
            fileMenu.setIcon("IconParkFileCabinet");
            fileMenu.setPath("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + FileConfigValues.SERVICE_NAME + "/file");
            fileMenu.setRouteName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + FileConfigValues.SERVICE_NAME + "_file");

            fileMenu.setMenuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID);
            fileServiceMenuChildren.add(fileMenu);
        }
        {
            MenuEntity imageMenu = new MenuEntity();
            imageMenu.setId("image");
            imageMenu.setParentId(fileServiceMenu.getId());
            imageMenu.setName("图片管理");
            imageMenu.setMenuType(MenuType.PAGE);
            imageMenu.setIcon("IconParkPic");
            imageMenu.setPath("/" + SystemConfigValues.ADMIN_PAGE_NAME + "/" + FileConfigValues.SERVICE_NAME + "/image");
            imageMenu.setRouteName(SystemConfigValues.ADMIN_PAGE_NAME + "_" + FileConfigValues.SERVICE_NAME + "_image");

            imageMenu.setMenuGroup(SystemConfigValues.ADMIN_MENU_GROUP_ID);
            fileServiceMenuChildren.add(imageMenu);
        }
        fileServiceMenu.setChildren(fileServiceMenuChildren);

        menuApi.init(fileServiceMenu);
    }
}
