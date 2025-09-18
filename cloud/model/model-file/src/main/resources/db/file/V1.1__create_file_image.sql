-- 文件表
CREATE TABLE file_t_file
(
    id          VARCHAR(255) NOT NULL PRIMARY KEY,
    type        VARCHAR(255),
    sort        INTEGER,
    create_time TIMESTAMP(6),
    update_time TIMESTAMP(6),
    description TEXT,
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

-- 分页主序
CREATE INDEX idx_file_create_time_desc_deleted ON file_t_file (create_time DESC, deleted);


-- 图片表
CREATE TABLE file_t_image
(
    id          VARCHAR(255) NOT NULL PRIMARY KEY,
    type        VARCHAR(255),
    sort        INTEGER,
    create_time TIMESTAMP(6),
    update_time TIMESTAMP(6),
    description TEXT,
    deleted     INTEGER,

    name        VARCHAR(255), -- 图片名称
    width       INTEGER,      -- 图片宽度
    height      INTEGER,      -- 图高度
    file_id     VARCHAR(255)  -- 图片文件id
);

-- 分页主序
CREATE INDEX idx_image_create_time_desc_deleted ON file_t_image (create_time DESC, deleted);

-- 视频表
CREATE TABLE file_t_video
(
    id          VARCHAR(255) NOT NULL PRIMARY KEY,
    type        VARCHAR(255),
    sort        INTEGER,
    create_time TIMESTAMP(6),
    update_time TIMESTAMP(6),
    description TEXT,
    deleted     INTEGER,

    name        VARCHAR(255), -- 视频名称
    width       INTEGER,      -- 视频宽度
    height      INTEGER,      -- 视频高度
    duration    INTEGER,      -- 视频时长（秒）
    format      VARCHAR(255), -- 视频格式
    bitrate     INTEGER,      -- 视频码率
    fps         INTEGER,      -- 视频帧率
    codec       VARCHAR(255), -- 视频编码格式
    file_id     VARCHAR(255)  -- 视频文件id
);

-- 分页主序
CREATE INDEX idx_video_create_time_desc_deleted ON file_t_video (create_time DESC, deleted);
