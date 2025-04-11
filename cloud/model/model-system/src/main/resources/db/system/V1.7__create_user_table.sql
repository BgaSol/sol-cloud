CREATE TABLE t_user
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
