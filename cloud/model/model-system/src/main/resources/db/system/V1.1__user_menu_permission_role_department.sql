-- 权限表
CREATE TABLE system_t_permission
(
    id            VARCHAR(255) NOT NULL PRIMARY KEY,
    type          VARCHAR(255),
    sort          INTEGER,
    create_time   TIMESTAMP(6),
    update_time   TIMESTAMP(6),
    description   VARCHAR(255),
    deleted       INTEGER,

    parent_id     VARCHAR(255),
    name          VARCHAR(255), -- 权限名
    code          VARCHAR(255), -- 权限编码
    path          VARCHAR(255), -- 权限路径
    micro_service VARCHAR(255)  -- 微服务名
);
-- 分页主序
CREATE INDEX idx_permission_create_time_desc_deleted ON system_t_permission (create_time DESC, deleted);

-- 菜单表
CREATE TABLE system_t_menu
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
-- 分页主序
CREATE INDEX idx_menu_create_time_desc_deleted ON system_t_menu (create_time DESC, deleted);

-- 角色表
CREATE TABLE system_t_role
(
    id          VARCHAR(255) NOT NULL PRIMARY KEY,
    type        VARCHAR(255),
    sort        INTEGER,
    create_time TIMESTAMP(6),
    update_time TIMESTAMP(6),
    description VARCHAR(255),
    deleted     INTEGER,

    parent_id   VARCHAR(255),
    name        VARCHAR(255), -- 角色名
    code        VARCHAR(255), -- 角色编码
    status      INTEGER       -- 角色状态
);
-- 分页主序
CREATE INDEX idx_role_create_time_desc_deleted ON system_t_role (create_time DESC, deleted);

-- 部门表
CREATE TABLE system_t_department
(
    id          VARCHAR(255) NOT NULL PRIMARY KEY,
    type        VARCHAR(255),
    sort        INTEGER,
    create_time TIMESTAMP(6),
    update_time TIMESTAMP(6),
    description VARCHAR(255),
    deleted     INTEGER,

    parent_id   VARCHAR(255),
    name        VARCHAR(255), -- 部门名
    code        VARCHAR(255), -- 部门编码
    domain      VARCHAR(255), -- 部门域名
    address     VARCHAR(255), -- 部门地址
    phone       VARCHAR(255), -- 部门电话
    html        VARCHAR(255), -- 部门备注HTML
    icon_id     VARCHAR(255)  -- 部门图标id 关联图片id
);
-- 分页主序
CREATE INDEX idx_department_create_time_desc_deleted ON system_t_department (create_time DESC, deleted);

-- 用户表
CREATE TABLE system_t_user
(
    id            VARCHAR(255) NOT NULL PRIMARY KEY,
    type          VARCHAR(255),
    sort          INTEGER,
    create_time   TIMESTAMP(6),
    update_time   TIMESTAMP(6),
    description   VARCHAR(255),
    deleted       INTEGER,

    username      VARCHAR(255), -- 用户名
    password      VARCHAR(255), -- 密码
    nickname      VARCHAR(255), -- 昵称
    email         VARCHAR(255), -- 邮箱
    phone         VARCHAR(255), -- 手机号
    status        VARCHAR(255), -- 状态
    avatar_id     VARCHAR(255), -- 头像id
    locked        BOOLEAN,      -- 账户锁定
    department_id VARCHAR(255)  -- 角色
);
-- 分页主序
CREATE INDEX idx_user_create_time_desc_deleted ON system_t_user (create_time DESC, deleted);

-- 角色菜单关联表
CREATE TABLE system_c_role_menu
(
    role_id VARCHAR(255) NOT NULL,
    menu_id VARCHAR(255) NOT NULL
);
CREATE INDEX idx_role_menu_role_id ON system_c_role_menu(role_id);
CREATE INDEX idx_role_menu_menu_id ON system_c_role_menu(menu_id);

-- 角色权限关联表
CREATE TABLE system_c_role_permission
(
    role_id       VARCHAR(255) NOT NULL,
    permission_id VARCHAR(255) NOT NULL
);
CREATE INDEX idx_role_permission_role_id ON system_c_role_permission(role_id);
CREATE INDEX idx_role_permission_permission_id ON system_c_role_permission(permission_id);

-- 用户角色关联表
CREATE TABLE system_c_user_role
(
    user_id VARCHAR(255) NOT NULL,
    role_id VARCHAR(255) NOT NULL
);
CREATE INDEX idx_user_role_user_id ON system_c_user_role(user_id);
CREATE INDEX idx_user_role_role_id ON system_c_user_role(role_id);