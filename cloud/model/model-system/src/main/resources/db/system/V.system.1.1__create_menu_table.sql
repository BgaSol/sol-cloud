CREATE TABLE t_menu
(
    id               VARCHAR(255) NOT NULL PRIMARY KEY,
    type             VARCHAR(255),
    sort             INTEGER,
    create_time      TIMESTAMP(6),
    update_time      TIMESTAMP(6),
    description      VARCHAR(255),
    deleted          INTEGER,

    parent_id        VARCHAR(255),

    name             VARCHAR(255), -- 菜单名
    status           INTEGER,      -- 菜单状态
    menu_type        VARCHAR(255), -- 菜单类型
    route_path       VARCHAR(255), -- 菜单路由地址
    icon             VARCHAR(255), -- 菜单图标
    route_name       VARCHAR(255), -- 菜单路由名
    button_code      VARCHAR(255), -- 按钮代码
    is_external      BOOLEAN,      -- 是否是外链
    external_url     VARCHAR(255), -- 外链地址
    is_external_open BOOLEAN,      -- 是否外链新窗口打开
    is_disabled      BOOLEAN,      -- 是否停用（置灰）
    is_hidden        BOOLEAN,      -- 是否隐藏（不显示）
    menu_group       VARCHAR(255)  -- 菜单组
);

