package com.bgasol.web.file.video.service;

import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.model.file.file.entity.FileEntity;
import com.bgasol.model.file.video.dto.VideoPageDto;
import com.bgasol.model.file.video.entity.VideoEntity;
import com.bgasol.plugin.minio.service.OssService;
import com.bgasol.web.file.file.service.FileService;
import com.bgasol.web.file.video.mapper.VideoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VideoService extends BaseService<VideoEntity, VideoPageDto> {
    private final VideoMapper videoMapper;

    private final FileService fileService;

    private final RedissonClient redissonClient;

    private final OssService ossService;

    @Override
    public RedissonClient commonBaseRedissonClient() {
        return redissonClient;
    }

    @Override
    public VideoMapper commonBaseMapper() {
        return videoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public void findOtherTable(VideoEntity entity) {
        if (ObjectUtils.isNotEmpty(entity.getFileId())) {
            entity.setFile(fileService.findById(entity.getFileId()));
        }
    }

    /**
     * 读取视频文件流
     */
    public InputStream videoStreamFindById(String id) {
        VideoEntity imageEntity = this.findById(id);
        FileEntity file = imageEntity.getFile();
        return ossService.readFileStream(file);
    }
}
