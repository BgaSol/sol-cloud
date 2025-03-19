CREATE TABLE t_permission
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