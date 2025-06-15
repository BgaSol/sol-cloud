-- 文件表
CREATE TABLE t_file
(
    id          VARCHAR(255) NOT NULL PRIMARY KEY,
    type        VARCHAR(255),
    sort        INTEGER,
    create_time TIMESTAMP(6),
    update_time TIMESTAMP(6),
    description VARCHAR(255),
    deleted     INTEGER,

    name        VARCHAR(255), -- 文件名称
    url         VARCHAR(255), -- 文件地址
    size        BIGINT,       -- 文件大小
    hash        VARCHAR(255), -- 文件HASH
    status      VARCHAR(255), -- 文件状态
    suffix      VARCHAR(255), -- 文件后缀
    source      VARCHAR(255), -- 文件来源
    bucket      VARCHAR(255)  -- 文件所在桶
);

-- 为文件表添加索引
CREATE INDEX idx_file_hash ON t_file(hash);
CREATE INDEX idx_file_name ON t_file(name);

-- 图片表
CREATE TABLE t_image
(
    id          VARCHAR(255) NOT NULL PRIMARY KEY,
    type        VARCHAR(255),
    sort        INTEGER,
    create_time TIMESTAMP(6),
    update_time TIMESTAMP(6),
    description VARCHAR(255),
    deleted     INTEGER,

    name        VARCHAR(255), -- 图片名称
    width       INTEGER,      -- 图片宽度
    height      INTEGER,      -- 图片高度
    file_id     VARCHAR(255)  -- 图片文件id
);

-- 为图片表添加索引
CREATE INDEX idx_image_file_id ON t_image(file_id);
CREATE INDEX idx_image_name ON t_image(name);
