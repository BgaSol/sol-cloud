CREATE TABLE t_department
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