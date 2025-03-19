CREATE TABLE t_role
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
