create table t_image
(
    id          VARCHAR(255) NOT NULL PRIMARY KEY,
    type        VARCHAR(255),
    sort        INTEGER,
    create_time TIMESTAMP(6),
    update_time TIMESTAMP(6),
    description VARCHAR(255),
    deleted     INTEGER,

    name        varchar(255), -- 图片名称
    width       integer,      -- 图片宽度
    height      integer,      -- 图片高度
    file_id     varchar(255)  -- 图片文件id
);
