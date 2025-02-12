-- 判断表是否存在
create table t_file
(
    id          VARCHAR(255) NOT NULL PRIMARY KEY,
    type        VARCHAR(255),
    sort        INTEGER,
    create_time TIMESTAMP(6),
    update_time TIMESTAMP(6),
    description VARCHAR(255),
    deleted     INTEGER,

    name        varchar(255), -- 文件名称
    url         varchar(255), -- 文件地址
    size        bigint,       -- 文件大小
    hash        varchar(255), -- 文件HASH
    status      varchar(255), -- 文件状态
    suffix      varchar(255), -- 文件后缀
    source      varchar(255), -- 文件来源
    bucket      varchar(255)  -- 文件所在桶
);

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
